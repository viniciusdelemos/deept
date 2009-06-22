package prefuse.demos2.decorators;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataColorAction;
import prefuse.action.filter.GraphDistanceFilter;
import prefuse.action.layout.Layout;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.activity.Activity;
import prefuse.controls.ControlAdapter;
import prefuse.controls.FocusControl;
import prefuse.controls.NeighborHighlightControl;
import prefuse.controls.PanControl;
import prefuse.controls.WheelZoomControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.Graph;
import prefuse.data.Schema;
import prefuse.data.Table;
import prefuse.data.Tuple;
import prefuse.data.event.TupleSetListener;
import prefuse.data.expression.Predicate;
import prefuse.data.expression.parser.ExpressionParser;
import prefuse.data.io.GraphMLReader;
import prefuse.data.tuple.TupleSet;
import prefuse.demos.TreeMap.LabelLayout;
import prefuse.render.AbstractShapeRenderer;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.render.PolygonRenderer;
import prefuse.render.Renderer;
import prefuse.render.ShapeRenderer;
import prefuse.util.ColorLib;
import prefuse.util.GraphLib;
import prefuse.util.GraphicsLib;
import prefuse.util.PrefuseLib;
import prefuse.util.display.DisplayLib;
import prefuse.util.display.ItemBoundsListener;
import prefuse.util.force.ForceSimulator;
import prefuse.util.io.IOLib;
import prefuse.util.ui.JForcePanel;
import prefuse.util.ui.JValueSlider;
import prefuse.util.ui.UILib;
import prefuse.visual.AggregateItem;
import prefuse.visual.AggregateTable;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualGraph;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;

/**
 * @author <a href="http://jheer.org">jeffrey heer</a>
 * @author Juanjo Vega
 */
public class GraphViewDistanceFilterAggregatesAndDecoratorsDemo extends JPanel {

    private static final int COLOR_AGGR_BORDER = ColorLib.rgb(176, 196, 222);
    private static final int DECORATOR_COLOR = ColorLib.hex("#FA5858");//hex("#87CEEB");
    private static final int DECORATOR_BORDER_COLOR = ColorLib.hex("#FF0000");//hex("#579EAB");
    private static final String GRAPH = "graph";
    private static final String NODES = GRAPH + ".nodes";
    private static final String EDGES = GRAPH + ".edges";
    private static final String AGGR = "aggregates";
    private static final String NODE_DECORATORS = "nodeDeco";
    private static final String AGGR_DECORATORS = "aggrDeco";
    private static final String DECORATORS_GROUP = "decoratorsGroup";
    private static final String TREENODE_LABEL = "label";
    private static final String ACTION_DRAW = "draw";
    private static final String ACTION_LAYOUT = "layout";
    private Visualization m_vis;
    private GraphDistanceFilter filter;
    private int hops = 30;
    private final JValueSlider slider = new JValueSlider("Distance", 1, hops, hops / 4);
    private static final Schema LABEL_SCHEMA = PrefuseLib.getVisualItemSchema();


    {
        LABEL_SCHEMA.setDefault(VisualItem.INTERACTIVE, false);
        LABEL_SCHEMA.setDefault(VisualItem.FILLCOLOR, DECORATOR_COLOR);
        LABEL_SCHEMA.setDefault(VisualItem.STROKECOLOR, DECORATOR_BORDER_COLOR);
        LABEL_SCHEMA.setDefault(VisualItem.SHAPE, Constants.SHAPE_CROSS);
    }

    public GraphViewDistanceFilterAggregatesAndDecoratorsDemo(Graph g, String label) {
        super(new BorderLayout());

        // create a new, empty visualization for our data
        m_vis = new Visualization();

        // --------------------------------------------------------------------
        // set up the renderers
        LabelRenderer tr = new LabelRenderer();
        tr.setRoundedCorner(8, 8);
        m_vis.setRendererFactory(new DefaultRendererFactory(tr));

        // adds graph to visualization and sets renderer label field
        setGraph(g, label);
    }

    public void setGraph(Graph g, String label) {
        // create a new, empty visualization for our data
        m_vis = new Visualization();

        // --------------------------------------------------------------------
        // set up the renderers
        LabelRenderer tr = new LabelRenderer();
        tr.setRoundedCorner(8, 8);
        m_vis.setRendererFactory(new DefaultRendererFactory(tr));

        // update graph
        VisualGraph vg = m_vis.addGraph(GRAPH, g);
        m_vis.setValue(EDGES, null, VisualItem.INTERACTIVE, Boolean.FALSE);
        VisualItem f = (VisualItem) vg.getNode(0);
        m_vis.getGroup(Visualization.FOCUS_ITEMS).setTuple(f);
        f.setFixed(false);

        // --------------------------------------------------------------------
        // register the data with a visualization

        // add visual data groups
        m_vis.setInteractive(EDGES, null, false);
        m_vis.setValue(NODES, null, VisualItem.SHAPE,
                new Integer(Constants.SHAPE_ELLIPSE));

        AggregateTable at = m_vis.addAggregates(AGGR);
        at.addColumn(VisualItem.POLYGON, float[].class);
        at.addColumn("id", int.class);

        // add nodes to aggregates
        // create an aggregate for each node and its children when they don't have any child.
        Iterator nodes = vg.nodes();
        for (int i = 0; i < vg.getNodeCount(); i++) {
            NodeItem node = (NodeItem) nodes.next();
            if (node.getChildCount() > 0) {
                AggregateItem aitem = (AggregateItem) at.addItem();
                aitem.setInt("id", i);
                aitem.addItem(node);
                for (int j = 0; j < node.getChildCount(); j++) {
                    NodeItem child = (NodeItem) node.getChild(j);
                    if (node.getChild(j).getChildCount() == 0) {
                        aitem.addItem(child);
                    }
                }
            }
        }

        // draw the nodes as basic shapes
        LabelRenderer m_nodeRenderer = new LabelRenderer(TREENODE_LABEL);
        m_nodeRenderer.setRenderType(AbstractShapeRenderer.RENDER_TYPE_FILL);
        m_nodeRenderer.setHorizontalAlignment(Constants.CENTER | Constants.LEFT);
        m_nodeRenderer.setRoundedCorner(8, 8);

        // draw aggregates as polygons with curved edges
        Renderer polyR = new PolygonRenderer(Constants.POLY_TYPE_CURVE);
        ((PolygonRenderer) polyR).setCurveSlack(0.15f);

        DefaultRendererFactory drf = new DefaultRendererFactory();
        drf.setDefaultRenderer(m_nodeRenderer);
        ((LabelRenderer) drf.getDefaultRenderer()).setTextField(label);

        ShapeRenderer decoratorsRenderer = new ShapeRenderer();
        drf.add(new InGroupPredicate(DECORATORS_GROUP), decoratorsRenderer);

        drf.add("ingroup('aggregates')", polyR);
        drf.add(new InGroupPredicate(NODE_DECORATORS), new LabelRenderer(TREENODE_LABEL));
        m_vis.setRendererFactory(drf);

        // Add decorators render.
        Predicate hasChildren = (Predicate) ExpressionParser.parse("childcount()>0");
        m_vis.addDecorators(DECORATORS_GROUP, NODES, hasChildren, LABEL_SCHEMA);

        // fix selected focus nodes
        TupleSet focusGroup = m_vis.getGroup(Visualization.FOCUS_ITEMS);
        focusGroup.addTupleSetListener(new TupleSetListener() {

            public void tupleSetChanged(TupleSet ts, Tuple[] add, Tuple[] rem) {
                if (add.length > 0 && add[0] instanceof NodeItem) {

                    for (int i = 0; i < rem.length; ++i) {
                        ((VisualItem) rem[i]).setFixed(false);
                    }

                    for (int i = 0; i < add.length; ++i) {
                        ((VisualItem) add[i]).setFixed(false);
                        ((VisualItem) add[i]).setFixed(true);
                    }

                    if (ts.getTupleCount() == 0) {
                        ts.addTuple(rem[0]);
                        ((VisualItem) rem[0]).setFixed(false);
                    }
                    m_vis.run(ACTION_DRAW);
                }
            }
        });

        // --------------------------------------------------------------------
        // create actions to process the visual data
        // set up the visual operators
        // first set up all the color actions
        ColorAction aStroke = new ColorAction(AGGR, VisualItem.STROKECOLOR);
        aStroke.setDefaultColor(COLOR_AGGR_BORDER);
        aStroke.add("_hover", ColorLib.rgb(255, 100, 100));

        int[] palette = new int[]{
            ColorLib.rgba(255, 200, 200, 150),
            ColorLib.rgba(200, 255, 200, 150),
            ColorLib.rgba(200, 200, 255, 150)
        };
        ColorAction aFill = new DataColorAction(AGGR, "id",
                Constants.NOMINAL, VisualItem.FILLCOLOR, palette);

        // bundle the color actions
        ActionList colors = new ActionList();
        colors.add(aStroke);
        colors.add(aFill);

        filter = new GraphDistanceFilter(GRAPH, slider.getValue().intValue());
        ColorAction fill = new ColorAction(NODES, VisualItem.FILLCOLOR, COLOR_AGGR_BORDER);
        fill.add(VisualItem.FIXED, ColorLib.rgb(255, 100, 100));
        fill.add(VisualItem.HIGHLIGHT, ColorLib.rgb(255, 200, 125));

        ActionList draw = new ActionList();
        draw.add(filter);
        draw.add(fill);
////        draw.add(colors);
        draw.add(new ColorAction(NODES, VisualItem.STROKECOLOR, ColorLib.gray(100)));
        draw.add(new ColorAction(NODES, VisualItem.TEXTCOLOR, ColorLib.rgb(0, 0, 0)));
        draw.add(new ColorAction(EDGES, VisualItem.FILLCOLOR, ColorLib.gray(200)));
        draw.add(new ColorAction(EDGES, VisualItem.STROKECOLOR, ColorLib.gray(200)));
        m_vis.putAction(ACTION_DRAW, draw);

        ActionList animate = new ActionList(Activity.INFINITY);
        animate.add(new ForceDirectedLayout(GRAPH));
        animate.add(new DecoratorLayoutCross(DECORATORS_GROUP));
        animate.add(colors);
        animate.add(new AggregateLayout(AGGR));
        animate.add(new LabelLayout(NODE_DECORATORS));
        animate.add(new LabelLayout(AGGR_DECORATORS));
        animate.add(new RepaintAction());
        m_vis.putAction(ACTION_LAYOUT, animate);

        // finally, we register our ActionList with the Visualization.
        // we can later execute our Actions by invoking a method on our
        // Visualization, using the name we've chosen below.
        m_vis.runAfter(ACTION_DRAW, ACTION_LAYOUT);

        // set up the display
        Display display = new Display(m_vis);
        display.pan(250, 250);
        display.setHighQuality(true);
        // main display controls
        display.addControlListener(new AggregateDragControl());
        display.addControlListener(new FocusControl(1));
        display.addControlListener(new PanControl());
        display.addControlListener(new ZoomControl());
        display.addControlListener(new WheelZoomControl());
        display.addControlListener(new ZoomToFitControl());
        display.addControlListener(new NeighborHighlightControl());

        // Creates GUI.
        display.setForeground(Color.GRAY);
        display.setBackground(Color.WHITE);

        // --------------------------------------------------------------------
        // launch the visualization

        // create a panel for editing force values
        ForceSimulator fsim = ((ForceDirectedLayout) animate.get(0)).getForceSimulator();
        JForcePanel fpanel = new JForcePanel(fsim);

        slider.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                filter.setDistance(slider.getValue().intValue());
                hops = filter.getDistance();
                m_vis.run(ACTION_DRAW);
            }
        });

        slider.setBackground(Color.WHITE);
        slider.setPreferredSize(new Dimension(300, 30));
        slider.setMaximumSize(new Dimension(300, 30));

        Box cf = new Box(BoxLayout.Y_AXIS);
        cf.add(slider);
        cf.setBorder(BorderFactory.createTitledBorder("Connectivity Filter"));
        fpanel.add(cf);
        fpanel.add(Box.createVerticalGlue());
        fpanel.add(new Overview(display));

        // create a new JSplitPane to present the interface
        JSplitPane split = new JSplitPane();

        // To add the
        split.setLeftComponent(display);
        split.setRightComponent(fpanel);
        split.setOneTouchExpandable(true);
        split.setContinuousLayout(false);
        split.setDividerLocation(550);

        removeAll();
        add(split, BorderLayout.CENTER);
        updateUI();

        // set things running
        m_vis.run(ACTION_DRAW);
    }

// ------------------------------------------------------------------------
// Main and demo methods
    public static void main(String[] args) {
        UILib.setPlatformLookAndFeel();

        // create graphview
        String datafile = null;
        String label = "label";
        if (args.length > 1) {
            datafile = args[0];
            label = args[1];
        }

        JFrame frame = demo(datafile, label);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
    }

    public static JFrame demo() {
        return demo((String) null, "label");
    }

    public static JFrame demo(
            String datafile, String label) {
        Graph g = null;
        if (datafile == null) {
            g = GraphLib.getGrid(15, 15);
            label = "label";
        } else {
            try {
                g = new GraphMLReader().readGraph(datafile);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }

        }
        return demo(g, label);
    }

    public static JFrame demo(
            Graph g, String label) {
        final GraphViewDistanceFilterAggregatesAndDecoratorsDemo view = new GraphViewDistanceFilterAggregatesAndDecoratorsDemo(g, label);

        // set up menu
        JMenu dataMenu = new JMenu("Data");
        dataMenu.add(new OpenGraphAction(view));
        dataMenu.add(new GraphMenuAction("Grid", "ctrl 1", view) {

            protected Graph getGraph() {
                return GraphLib.getGrid(15, 15);
            }
        });
        dataMenu.add(new GraphMenuAction("Clique", "ctrl 2", view) {

            protected Graph getGraph() {
                return GraphLib.getClique(10);
            }
        });
        dataMenu.add(new GraphMenuAction("Honeycomb", "ctrl 3", view) {

            protected Graph getGraph() {
                return GraphLib.getHoneycomb(5);
            }
        });
        dataMenu.add(new GraphMenuAction("Balanced Tree", "ctrl 4", view) {

            protected Graph getGraph() {
                return GraphLib.getBalancedTree(3, 5);
            }
        });
        dataMenu.add(new GraphMenuAction("Diamond Tree", "ctrl 5", view) {

            protected Graph getGraph() {
                return GraphLib.getDiamondTree(3, 3, 3);
            }
        });
        JMenuBar menubar = new JMenuBar();
        menubar.add(dataMenu);

        // launch window
        JFrame frame = new JFrame("p r e f u s e  |  GraphView with Distance Filter, Aggregates and Decorators");
        frame.setJMenuBar(menubar);
        frame.setContentPane(view);
        frame.pack();
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowActivated(WindowEvent e) {
                view.m_vis.run("layout");
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                view.m_vis.cancel("layout");
            }
        });

        return frame;
    }
// ------------------------------------------------------------------------

    /**
     * Swing menu action that loads a graph into the graph viewer.
     */
    public abstract static class GraphMenuAction extends AbstractAction {

        private GraphViewDistanceFilterAggregatesAndDecoratorsDemo m_view;

        public GraphMenuAction(String name, String accel, GraphViewDistanceFilterAggregatesAndDecoratorsDemo view) {
            m_view = view;
            this.putValue(AbstractAction.NAME, name);
            this.putValue(AbstractAction.ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke(accel));
        }

        public void actionPerformed(ActionEvent e) {
            m_view.setGraph(getGraph(), "label");
        }

        protected abstract Graph getGraph();
    }

    public static class OpenGraphAction extends AbstractAction {

        private GraphViewDistanceFilterAggregatesAndDecoratorsDemo m_view;

        public OpenGraphAction(GraphViewDistanceFilterAggregatesAndDecoratorsDemo view) {
            m_view = view;
            this.putValue(AbstractAction.NAME, "Open File...");
            this.putValue(AbstractAction.ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke("ctrl O"));
        }

        public void actionPerformed(ActionEvent e) {
            Graph g = IOLib.getGraphFile(m_view);
            if (g == null) {
                return;
            }
            String label = getLabel(m_view, g);
            if (label != null) {
                m_view.setGraph(g, label);
            }
        }

        public static String getLabel(Component c, Graph g) {
            // get the column names
            Table t = g.getNodeTable();
            int cc = t.getColumnCount();
            String[] names = new String[cc];
            for (int i = 0; i < cc; ++i) {
                names[i] = t.getColumnName(i);            // where to store the result
            }
            final String[] label = new String[1];

            // -- build the dialog -----
            // we need to get the enclosing frame first
            while (c != null && !(c instanceof JFrame)) {
                c = c.getParent();
            }
            final JDialog dialog = new JDialog(
                    (JFrame) c, "Choose Label Field", true);

            // create the ok/cancel buttons
            final JButton ok = new JButton("OK");
            ok.setEnabled(false);
            ok.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    dialog.setVisible(false);
                }
            });
            JButton cancel = new JButton("Cancel");
            cancel.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    label[0] = null;
                    dialog.setVisible(false);
                }
            });

            // build the selection list
            final JList list = new JList(names);
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            list.getSelectionModel().addListSelectionListener(
                    new ListSelectionListener() {

                        public void valueChanged(ListSelectionEvent e) {
                            int sel = list.getSelectedIndex();
                            if (sel >= 0) {
                                ok.setEnabled(true);
                                label[0] = (String) list.getModel().getElementAt(sel);
                            } else {
                                ok.setEnabled(false);
                                label[0] = null;
                            }
                        }
                    });
            JScrollPane scrollList = new JScrollPane(list);

            JLabel title = new JLabel("Choose a field to use for node labels:");

            // layout the buttons
            Box bbox = new Box(BoxLayout.X_AXIS);
            bbox.add(Box.createHorizontalStrut(5));
            bbox.add(Box.createHorizontalGlue());
            bbox.add(ok);
            bbox.add(Box.createHorizontalStrut(5));
            bbox.add(cancel);
            bbox.add(Box.createHorizontalStrut(5));

            // put everything into a panel
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(title, BorderLayout.NORTH);
            panel.add(scrollList, BorderLayout.CENTER);
            panel.add(bbox, BorderLayout.SOUTH);
            panel.setBorder(BorderFactory.createEmptyBorder(5, 2, 2, 2));

            // show the dialog
            dialog.setContentPane(panel);
            dialog.pack();
            dialog.setLocationRelativeTo(c);
            dialog.setVisible(true);
            dialog.dispose();

            // return the label field selection
            return label[0];
        }
    }

    public static class FitOverviewListener implements ItemBoundsListener {

        private Rectangle2D m_bounds = new Rectangle2D.Double();
        private Rectangle2D m_temp = new Rectangle2D.Double();
        private double m_d = 15;

        public void itemBoundsChanged(Display d) {
            d.getItemBounds(m_temp);
            GraphicsLib.expand(m_temp, 25 / d.getScale());

            double dd = m_d / d.getScale();
            double xd = Math.abs(m_temp.getMinX() - m_bounds.getMinX());
            double yd = Math.abs(m_temp.getMinY() - m_bounds.getMinY());
            double wd = Math.abs(m_temp.getWidth() - m_bounds.getWidth());
            double hd = Math.abs(m_temp.getHeight() - m_bounds.getHeight());
            if (xd > dd || yd > dd || wd > dd || hd > dd) {
                m_bounds.setFrame(m_temp);
                DisplayLib.fitViewToBounds(d, m_bounds, 0);
            }
        }
    }
} // end of class GraphView

/**
 * Layout algorithm that computes a convex hull surrounding
 * aggregate items and saves it in the "_polygon" field.
 */
class AggregateLayout extends Layout {

    private int m_margin_ = 5; // convex hull pixel margin
    private double[] m_pts;   // buffer for computing convex hulls

    public AggregateLayout(String aggrGroup) {
        super(aggrGroup);
    }

    /**
     * @see edu.berkeley.guir.prefuse.action.Action#run(edu.berkeley.guir.prefuse.ItemRegistry, double)
     */
    public void run(double frac) {

        AggregateTable aggr = (AggregateTable) m_vis.getGroup(m_group);

        // do we have any  to process?
        if (aggr == null || aggr.getTupleCount() == 0) {
            return;
        }

        // update buffers
        int maxsz = 0;
        for (Iterator aggrs = aggr.tuples(); aggrs.hasNext();) {
            maxsz = Math.max(maxsz, 4 * 2 *
                    ((AggregateItem) aggrs.next()).getAggregateSize());
        }
        if (m_pts == null || maxsz > m_pts.length) {
            m_pts = new double[maxsz];
        }

        // compute and assign convex hull for each aggregate
        Iterator aggrs = m_vis.visibleItems(m_group);
        while (aggrs.hasNext()) {
            AggregateItem aitem = (AggregateItem) aggrs.next();

            int idx = 0;
            if (aitem.getAggregateSize() == 0) {
                continue;
            }
            VisualItem item = null;
            Iterator iter = aitem.items();

            while (iter.hasNext()) {
                item = (VisualItem) iter.next();
                if (item.isVisible()) {
                    addPoint(m_pts, idx, item, m_margin_);
                    idx += 2 * 4;
                }
            }

            // if no aggregates are visible, do nothing
            if (idx == 0) {
                aitem.set(VisualItem.POLYGON, null);
                continue;
            }

            // compute convex hull
            double[] nhull = GraphicsLib.convexHull(m_pts, idx);

            // prepare viz attribute array
            float[] fhull = (float[]) aitem.get(VisualItem.POLYGON);
            if (fhull == null || fhull.length < nhull.length) {
                fhull = new float[nhull.length];
            } else if (fhull.length > nhull.length) {
                fhull[nhull.length] = Float.NaN;
            }

            // copy hull values
            for (int j = 0; j < nhull.length; j++) {
                fhull[j] = (float) nhull[j];
            }
            aitem.set(VisualItem.POLYGON, fhull);
            aitem.setValidated(false); // force invalidation
        }
    }

    private static void addPoint(double[] pts, int idx,
            VisualItem item, int growth) {
        Rectangle2D b = item.getBounds();
        double minX = (b.getMinX()) - growth, minY = (b.getMinY()) - growth;
        double maxX = (b.getMaxX()) + growth, maxY = (b.getMaxY()) + growth;
        pts[idx] = minX;
        pts[idx + 1] = minY;
        pts[idx + 2] = minX;
        pts[idx + 3] = maxY;
        pts[idx + 4] = maxX;
        pts[idx + 5] = minY;
        pts[idx + 6] = maxX;
        pts[idx + 7] = maxY;
    }
} // end of class AggregateLayout

/**
 * Interactive drag control that is "aggregate-aware"
 */
class AggregateDragControl extends ControlAdapter {

    private VisualItem activeItem;
    protected Point2D down = new Point2D.Double();
    protected Point2D temp = new Point2D.Double();
    protected boolean dragged;

    /**
     * Creates a new drag control that issues repaint requests as an item
     * is dragged.
     */
    public AggregateDragControl() {
    }

    /**
     * @see prefuse.controls.Control#itemEntered(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
     */
    @Override
    public void itemEntered(VisualItem item, MouseEvent e) {
        Display d = (Display) e.getSource();
        d.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        activeItem = item;
        if (!(item instanceof AggregateItem)) {
            setFixed(item, true);
        }
    }

    /**
     * @see prefuse.controls.Control#itemExited(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
     */
    @Override
    public void itemExited(VisualItem item, MouseEvent e) {
        if (activeItem == item) {
            activeItem = null;
            setFixed(item, false);
        }
        Display d = (Display) e.getSource();
        d.setCursor(Cursor.getDefaultCursor());
    }

    /**
     * @see prefuse.controls.Control#itemPressed(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
     */
    @Override
    public void itemPressed(VisualItem item, MouseEvent e) {
        if (!SwingUtilities.isLeftMouseButton(e)) {
            return;
        }
        dragged = false;
        Display d = (Display) e.getComponent();
        d.getAbsoluteCoordinate(e.getPoint(), down);
        if (item instanceof AggregateItem) {
            setFixed(item, true);
        }
    }

    /**
     * @see prefuse.controls.Control#itemReleased(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
     */
    @Override
    public void itemReleased(VisualItem item, MouseEvent e) {
        if (!SwingUtilities.isLeftMouseButton(e)) {
            return;
        }
        if (dragged) {
            activeItem = null;
            setFixed(item, false);
            dragged = false;
        }
    }

    /**
     * @see prefuse.controls.Control#itemDragged(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
     */
    @Override
    public void itemDragged(VisualItem item, MouseEvent e) {
        if (!SwingUtilities.isLeftMouseButton(e)) {
            return;
        }
        dragged = true;
        Display d = (Display) e.getComponent();
        d.getAbsoluteCoordinate(e.getPoint(), temp);
        double dx = temp.getX() - down.getX();
        double dy = temp.getY() - down.getY();

        move(item, dx, dy);

        down.setLocation(temp);
    }

    protected static void setFixed(VisualItem item, boolean fixed) {
        if (item instanceof AggregateItem) {
            Iterator items = ((AggregateItem) item).items();
            while (items.hasNext()) {
                setFixed((VisualItem) items.next(), fixed);
            }
        } else {
            item.setFixed(fixed);
        }
    }

    protected static void move(VisualItem item, double dx, double dy) {
        if (item instanceof AggregateItem) {
            Iterator items = ((AggregateItem) item).items();
            while (items.hasNext()) {
                move((VisualItem) items.next(), dx, dy);
            }
        } else {
            double x = item.getX();
            double y = item.getY();
            item.setStartX(x);
            item.setStartY(y);
            item.setX(x + dx);
            item.setY(y + dy);
            item.setEndX(x + dx);
            item.setEndY(y + dy);
        }
    }
} // end of class AggregateDragControl
