package examples;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.filter.GraphDistanceFilter;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.activity.Activity;
import prefuse.controls.ControlAdapter;
import prefuse.controls.DragControl;
import prefuse.controls.FocusControl;
import prefuse.controls.PanControl;
import prefuse.controls.WheelZoomControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Table;
import prefuse.data.Tuple;
import prefuse.data.event.TupleSetListener;
import prefuse.data.io.GraphMLReader;
import prefuse.data.tuple.TupleSet;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.GraphLib;
import prefuse.util.GraphicsLib;
import prefuse.util.collections.Queue;
import prefuse.util.display.DisplayLib;
import prefuse.util.display.ItemBoundsListener;
import prefuse.util.force.ForceSimulator;
import prefuse.util.io.IOLib;
import prefuse.util.ui.JForcePanel;
import prefuse.util.ui.JValueSlider;
import prefuse.util.ui.UILib;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualGraph;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;

/**
 * A further variation of the the GraphView example from the prefuse download.
 * This demo implements an extended  NeighbourHighLightControl for directed graph,
 * highlighting nodes according to if they can be reached through traversing along source edges,
 * target edges or both with different colors. Both here means that the highlighted node can be
 * reached from the focued node through traversing along sourcenodes alone and traversing along 
 * targetnodes alone.
 * This demo needs the file socialnetLoops.xml to work. Please  notice that "Jeff" is not the root
 * of this graph, but He has entered the scene. This was done not to question Jeff as the source of ... but to 
 * see if the highlighting functions also for the nodes which can be reached via source edges and target edges. 
 * 
 * @author <a href="http://jheer.org">jeffrey heer</a>
 * @author <a href="http://goosebumps4all.net/34all">martin dudek</a>
 */

public class GraphViewDirectedExtendedNeighborHiglightControlV2 extends JPanel {

    private static final String graph = "graph";
    private static final String nodes = "graph.nodes";
    private static final String edges = "graph.edges";

    // MAD - the name of the different neighborgroups
    private static final String[] neighborGroups = { "sourceNode", "targetNode", "bothNode" };

    private Visualization m_vis;

    public GraphViewDirectedExtendedNeighborHiglightControlV2(Graph g, String label) {

	// create a new, empty visualization for our data
	m_vis = new Visualization();

	// --------------------------------------------------------------------
	// set up the renderers

	LabelRenderer tr = new LabelRenderer();
	tr.setRoundedCorner(8, 8);
	m_vis.setRendererFactory(new DefaultRendererFactory(tr));

	// --------------------------------------------------------------------
	// register the data with a visualization

	// adds graph to visualization and sets renderer label field
	setGraph(g, label);

	// fix selected focus nodes
	TupleSet focusGroup = m_vis.getGroup(Visualization.FOCUS_ITEMS); 
	focusGroup.addTupleSetListener(new TupleSetListener() {
	    public void tupleSetChanged(TupleSet ts, Tuple[] add, Tuple[] rem)
	    {
		for ( int i=0; i<rem.length; ++i )
		    ((VisualItem)rem[i]).setFixed(false);
		for ( int i=0; i<add.length; ++i ) {
		    ((VisualItem)add[i]).setFixed(false);
		    ((VisualItem)add[i]).setFixed(true);
		}
		if ( ts.getTupleCount() == 0 ) {
		    ts.addTuple(rem[0]);
		    ((VisualItem)rem[0]).setFixed(false);
		}
		m_vis.run("draw");
	    }
	});



	// --------------------------------------------------------------------
	// create actions to process the visual data

	int hops = 30;
	final GraphDistanceFilter filter = new GraphDistanceFilter(graph, hops);

	ColorAction fill = new ColorAction(nodes, 
		VisualItem.FILLCOLOR, ColorLib.rgb(200,200,255));
	fill.add(VisualItem.FIXED, ColorLib.rgb(255,100,100));
	fill.add(VisualItem.HIGHLIGHT, ColorLib.rgb(255,200,125));


//	MAD - here we define the colors in which the neighbour nodes should be filled
//		bothGroup first	
	
	fill.add(new InGroupPredicate(neighborGroups[0]), ColorLib.rgb(0, 0,
		250));
	fill.add(new InGroupPredicate(neighborGroups[1]), ColorLib.rgb(0, 250,
		0));
	fill.add(new InGroupPredicate(neighborGroups[2]), ColorLib.rgb(250, 0,
		0));
	


	ActionList draw = new ActionList();
	draw.add(filter);
	draw.add(fill);
	draw.add(new ColorAction(nodes, VisualItem.STROKECOLOR, 0));
	draw.add(new ColorAction(nodes, VisualItem.TEXTCOLOR, ColorLib.rgb(0,0,0)));
	draw.add(new ColorAction(edges, VisualItem.FILLCOLOR, ColorLib.gray(200)));
	draw.add(new ColorAction(edges, VisualItem.STROKECOLOR, ColorLib.gray(200)));

	ActionList animate = new ActionList(Activity.INFINITY);
	animate.add(new ForceDirectedLayout(graph));
	animate.add(fill);
	animate.add(new RepaintAction());

	// finally, we register our ActionList with the Visualization.
	// we can later execute our Actions by invoking a method on our
	// Visualization, using the name we've chosen below.
	m_vis.putAction("draw", draw);
	m_vis.putAction("layout", animate);

	m_vis.runAfter("draw", "layout");


	// --------------------------------------------------------------------
	// set up a display to show the visualization

	Display display = new Display(m_vis);
	display.setSize(700,700);
	display.pan(350, 350);
	display.setForeground(Color.GRAY);
	display.setBackground(Color.WHITE);

	// main display controls
	
	//MAD - the new control
	final ExtendedNeighborHighlightControlForDirectedGraphs enhc = new ExtendedNeighborHighlightControlForDirectedGraphs(m_vis,neighborGroups,graph);
	
	display.addControlListener(new FocusControl(1));
	display.addControlListener(new DragControl());
	display.addControlListener(new PanControl());
	display.addControlListener(new ZoomControl());
	display.addControlListener(new WheelZoomControl());
	display.addControlListener(new ZoomToFitControl());
	display.addControlListener(enhc);

	// overview display
//	Display overview = new Display(vis);
//	overview.setSize(290,290);
//	overview.addItemBoundsListener(new FitOverviewListener());

	display.setForeground(Color.GRAY);
	display.setBackground(Color.WHITE);

	// --------------------------------------------------------------------        
	// launch the visualization

	// create a panel for editing force values
	ForceSimulator fsim = ((ForceDirectedLayout)animate.get(0)).getForceSimulator();
	JForcePanel fpanel = new JForcePanel(fsim);

//	JPanel opanel = new JPanel();
//	opanel.setBorder(BorderFactory.createTitledBorder("Overview"));
//	opanel.setBackground(Color.WHITE);
//	opanel.add(overview);

	final JValueSlider slider = new JValueSlider("Distance", 0, hops, hops);
	slider.addChangeListener(new ChangeListener() {
	    public void stateChanged(ChangeEvent e) {
		filter.setDistance(slider.getValue().intValue());
		m_vis.run("draw");
	    }
	});
	slider.setBackground(Color.WHITE);
	slider.setPreferredSize(new Dimension(300,30));
	slider.setMaximumSize(new Dimension(300,30));

	//MAD
	final JValueSlider hslider = new JValueSlider("Highlight distance", 0, 7, 7);
	hslider.addChangeListener(new ChangeListener() {
	    public void stateChanged(ChangeEvent e) {
		enhc.setDistance(hslider.getValue().intValue());
		m_vis.run("draw");
	    }
	});
	enhc.setDistance(7);
	
	hslider.setBackground(Color.WHITE);
	hslider.setPreferredSize(new Dimension(300,30));
	hslider.setMaximumSize(new Dimension(300,30));

	
	
	Box cf = new Box(BoxLayout.Y_AXIS);
	cf.add(slider);
	cf.setBorder(BorderFactory.createTitledBorder("Connectivity Filter"));
	fpanel.add(cf);
	
	Box df = new Box(BoxLayout.Y_AXIS);
	df.add(hslider);
	df.setBorder(BorderFactory.createTitledBorder("Lightning"));
	fpanel.add(df);

	//fpanel.add(opanel);

	fpanel.add(Box.createVerticalGlue());

	// create a new JSplitPane to present the interface
	JSplitPane split = new JSplitPane();
	split.setLeftComponent(display);
	split.setRightComponent(fpanel);
	split.setOneTouchExpandable(true);
	split.setContinuousLayout(false);
	split.setDividerLocation(700);

	// now we run our action list
	m_vis.run("draw");

	add(split);
    }

    public void setGraph(Graph g, String label) {
	// update labeling
	DefaultRendererFactory drf = (DefaultRendererFactory)
	m_vis.getRendererFactory();
	((LabelRenderer)drf.getDefaultRenderer()).setTextField(label);

	// update graph
	m_vis.removeGroup(graph);
	VisualGraph vg = m_vis.addGraph(graph, g);
	m_vis.setValue(edges, null, VisualItem.INTERACTIVE, Boolean.FALSE);
	VisualItem f = (VisualItem)vg.getNode(0);
	m_vis.getGroup(Visualization.FOCUS_ITEMS).setTuple(f);
	f.setFixed(false);
    }

    // ------------------------------------------------------------------------
    // Main and demo methods

    public static void main(String[] args) {
	UILib.setPlatformLookAndFeel();

	// create graphview
	//String datafile = null;
	//String label = "label";

	String datafile = "/examples/socialnetLoops.xml";
	String label = "name";

	if ( args.length > 1 ) {
	    datafile = args[0];
	    label = args[1];
	}

	JFrame frame = demo(datafile, label);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static JFrame demo() {
	return demo((String)null, "label");
    }

    public static JFrame demo(String datafile, String label) {
	Graph g = null;
	if ( datafile == null ) {
	    g = GraphLib.getGrid(15,15);
	    label = "label";
	} else {
	    try {
		g = new GraphMLReader().readGraph(datafile);
	    } catch ( Exception e ) {
		e.printStackTrace();
		System.exit(1);
	    }
	}
	return demo(g, label);
    }

    public static JFrame demo(Graph g, String label) {
	final GraphViewDirectedExtendedNeighborHiglightControlV2 view = new GraphViewDirectedExtendedNeighborHiglightControlV2(g, label);

	// set up menu
	JMenu dataMenu = new JMenu("Data");
	dataMenu.add(new OpenGraphAction(view));
	dataMenu.add(new GraphMenuAction("Grid","ctrl 1",view) {
	    protected Graph getGraph() {
		return GraphLib.getGrid(15,15);
	    }
	});
	dataMenu.add(new GraphMenuAction("Clique","ctrl 2",view) {
	    protected Graph getGraph() {
		return GraphLib.getClique(10);
	    }
	});
	dataMenu.add(new GraphMenuAction("Honeycomb","ctrl 3",view) {
	    protected Graph getGraph() {
		return GraphLib.getHoneycomb(5);
	    }
	});
	dataMenu.add(new GraphMenuAction("Balanced Tree","ctrl 4",view) {
	    protected Graph getGraph() {
		return GraphLib.getBalancedTree(3,5);
	    }
	});
	dataMenu.add(new GraphMenuAction("Diamond Tree","ctrl 5",view) {
	    protected Graph getGraph() {
		return GraphLib.getDiamondTree(3,3,3);
	    }
	});
	JMenuBar menubar = new JMenuBar();
	menubar.add(dataMenu);

	// launch window
	JFrame frame = new JFrame("p r e f u s e  |  g r a p h v i e w");
	frame.setJMenuBar(menubar);
	frame.setContentPane(view);
	frame.pack();
	frame.setVisible(true);

	frame.addWindowListener(new WindowAdapter() {
	    public void windowActivated(WindowEvent e) {
		view.m_vis.run("layout");
	    }
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
	private GraphViewDirectedExtendedNeighborHiglightControlV2 m_view;
	public GraphMenuAction(String name, String accel, GraphViewDirectedExtendedNeighborHiglightControlV2 view) {
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
	private GraphViewDirectedExtendedNeighborHiglightControlV2 m_view;

	public OpenGraphAction(GraphViewDirectedExtendedNeighborHiglightControlV2 view) {
	    m_view = view;
	    this.putValue(AbstractAction.NAME, "Open File...");
	    this.putValue(AbstractAction.ACCELERATOR_KEY,
		    KeyStroke.getKeyStroke("ctrl O"));
	}
	public void actionPerformed(ActionEvent e) {
	    Graph g = IOLib.getGraphFile(m_view);
	    if ( g == null ) return;
	    String label = getLabel(m_view, g);
	    if ( label != null ) {
		m_view.setGraph(g, label);
	    }
	}
	public static String getLabel(Component c, Graph g) {
	    // get the column names
	    Table t = g.getNodeTable();
	    int  cc = t.getColumnCount();
	    String[] names = new String[cc];
	    for ( int i=0; i<cc; ++i )
		names[i] = t.getColumnName(i);

	    // where to store the result
	    final String[] label = new String[1];

	    // -- build the dialog -----
	    // we need to get the enclosing frame first
	    while ( c != null && !(c instanceof JFrame) ) {
		c = c.getParent();
	    }
	    final JDialog dialog = new JDialog(
		    (JFrame)c, "Choose Label Field", true);

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
			    if ( sel >= 0 ) {
				ok.setEnabled(true);
				label[0] = (String)list.getModel().getElementAt(sel);
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
	    panel.setBorder(BorderFactory.createEmptyBorder(5,2,2,2));

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
	    GraphicsLib.expand(m_temp, 25/d.getScale());

	    double dd = m_d/d.getScale();
	    double xd = Math.abs(m_temp.getMinX()-m_bounds.getMinX());
	    double yd = Math.abs(m_temp.getMinY()-m_bounds.getMinY());
	    double wd = Math.abs(m_temp.getWidth()-m_bounds.getWidth());
	    double hd = Math.abs(m_temp.getHeight()-m_bounds.getHeight());
	    if ( xd>dd || yd>dd || wd>dd || hd>dd ) {
		m_bounds.setFrame(m_temp);
		DisplayLib.fitViewToBounds(d, m_bounds, 0);
	    }
	}
    }


    /**
     * MAD - The class, which implements the new extended highlight control
     * It basically defines three focus groups for source nodes, target nodes
     * and nodes which are both. These "distinct" focus groups are filled with the respecitve
     * nodes at "itemEntered" and cleared at "itemExit".
     * This filling is done through traversing along source and target edges respectively.
     * 
     * Beta version, not yet finaized
     *  
     * @author goose
     *
     */


    public class ExtendedNeighborHighlightControlForDirectedGraphs extends ControlAdapter {

	private Visualization visu;

	String sourceGroupName, targetGroupName, bothGroupName;

	TupleSet sourceTupleSet, targetTupleSet, bothTupleSet;

	String ngroup; 
        String egroup;
        
        int distance = Integer.MAX_VALUE;
	
	public ExtendedNeighborHighlightControlForDirectedGraphs(Visualization vis, String[] groupNames,String dataName) {
	    visu = vis;

	    
	    sourceGroupName = groupNames[0];
	    targetGroupName = groupNames[1];
	    bothGroupName   = groupNames[2];

	    try {
		visu.addFocusGroup(sourceGroupName);
		visu.addFocusGroup(targetGroupName);
		visu.addFocusGroup(bothGroupName);
	    } catch (Exception e) {
		System.out.println("Problems over problems while adding foucs groups to visualization " + e.getMessage());
	    }

	    sourceTupleSet = visu.getFocusGroup(sourceGroupName);
	    targetTupleSet = visu.getFocusGroup(targetGroupName);
	    bothTupleSet = visu.getFocusGroup(bothGroupName);
	    
	    ngroup = dataName + ".nodes";
	    egroup = dataName + ".edges";
	}
	
	public void setDistance(int distance) {
	    this.distance = distance;
	    System.out.println("Set distance to " + this.distance);
	}

	public void itemEntered(VisualItem item, MouseEvent e) {
	    if (item instanceof NodeItem) 
		setNeighbourHighlight((NodeItem) item);
	}

	public void itemExited(VisualItem item, MouseEvent e) {
	    if (item instanceof NodeItem) {
		sourceTupleSet.clear();
		targetTupleSet.clear();
		bothTupleSet.clear();
	    }
	}

	protected void setNeighbourHighlight(NodeItem centerNode) {

	    HashSet source = sourceNodeItems(centerNode, true, distance);
	    HashSet target = targetNodeItems(centerNode, true, distance);
	 
	    HashSet both = (HashSet) source.clone();
	    
	    both.retainAll(target);
	    
	    source.removeAll(both);
	    target.removeAll(both);
	    
	    Iterator iterSource = source.iterator();
	    while (iterSource.hasNext()) {
		sourceTupleSet.addTuple((NodeItem)iterSource.next());
	    }
	    
	    Iterator iterTarget = target.iterator();
	    while (iterTarget.hasNext()) {
		targetTupleSet.addTuple((NodeItem)iterTarget.next());
	    }
	    
	    Iterator iterBoth = both.iterator();
	    while (iterBoth.hasNext()) {
		bothTupleSet.addTuple((NodeItem)iterBoth.next());
	    }
	    
	     
	}


	private HashSet sourceNodeItems(NodeItem startNodeItem, boolean onlyVisible, int depth) {
	    return nodeItems(startNodeItem, true, onlyVisible, depth);
	}


	private HashSet targetNodeItems(NodeItem startNodeItem, boolean onlyVisible, int depth) {
	    return nodeItems(startNodeItem, false, onlyVisible, depth);
	}

	private HashSet nodeItems(NodeItem startNodeItem, boolean source, boolean onlyVisible, int depth) {

	    HashSet result = new HashSet();

	    if (depth < 0) {
		depth = Integer.MAX_VALUE;
	    }
	    
	    Iterator iter;

	    Queue q = new Queue(); // prefuse queue util

	    q.add(startNodeItem.getSourceTuple(), 0);

	    Visualization vis = startNodeItem.getVisualization();

	    while (!q.isEmpty()) {

		Node aNode = (Node) q.removeFirst();
		NodeItem aNodeItem = (NodeItem) vis.getVisualItem(ngroup, aNode);

		if (q.getDepth(aNode) > depth) { //we gone to deep
		    continue;
		}
		
	
		if (result.contains(aNodeItem)) { //we have dealed with this node already 
		    continue;
		}
		
		
		if (onlyVisible && !aNodeItem.isVisible()) { //we are visiting a non-visible node and we want only to deal with visible nodes 
		    continue;
		}

		result.add(aNodeItem);
		
		
		if (source) {
		    iter = aNode.inEdges();
		} else {
		    iter = aNode.outEdges();
		}

		while (iter.hasNext()) {
		    Edge anEdge = (Edge) iter.next();

		    if (onlyVisible) {
			EdgeItem anEdgeItem = (EdgeItem) vis.getVisualItem(egroup, anEdge);    
			if(!anEdgeItem.isVisible()) {
			    continue;
			}
		    }
		    Node nextNode = anEdge.getAdjacentNode(aNode);

		    NodeItem nextNodeItem = (NodeItem) vis.getVisualItem(ngroup, nextNode);

		    if (onlyVisible && !nextNodeItem.isVisible()) {
			continue;   
		    }
		    if (q.getDepth(aNode) + 1 <= depth) {
			q.add(nextNode, q.getDepth(aNode) + 1);
		    }
		}
	    }
	    return result;
	}	
    }


} // end of class GraphView
