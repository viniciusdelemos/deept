package gui.visualizations;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.Action;
import prefuse.action.ActionList;
import prefuse.action.ItemAction;
import prefuse.action.RepaintAction;
import prefuse.action.animate.ColorAnimator;
import prefuse.action.animate.LocationAnimator;
import prefuse.action.animate.QualityControlAnimator;
import prefuse.action.animate.VisibilityAnimator;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.FontAction;
import prefuse.action.filter.FisheyeTreeFilter;
import prefuse.action.layout.CollapsedSubtreeLayout;
import prefuse.action.layout.graph.NodeLinkTreeLayout;
import prefuse.activity.SlowInSlowOutPacer;
import prefuse.controls.Control;
import prefuse.controls.ControlAdapter;
import prefuse.controls.FocusControl;
import prefuse.controls.PanControl;
import prefuse.controls.WheelZoomControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Tree;
import prefuse.data.Tuple;
import prefuse.data.event.TupleSetListener;
import prefuse.data.search.PrefixSearchTupleSet;
import prefuse.data.tuple.TupleSet;
import prefuse.render.AbstractShapeRenderer;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.util.PrefuseLib;
import prefuse.util.ui.JFastLabel;
import prefuse.util.ui.JSearchPanel;
import prefuse.visual.AggregateItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;
import prefuse.visual.sort.TreeDepthItemSorter;
import controller.Category;
import controller.CategoryManager;
import controller.CategoryWord;


public class CategoryEdit extends Display{

	private final static String tree = "tree";
	private final static String treeNodes = "tree.nodes";
	private final String treeEdges = "tree.edges";

	private LabelRenderer m_nodeRenderer;
	private EdgeRenderer m_edgeRenderer;

	private static String c_label = "label";
	private int m_orientation = Constants.ORIENT_LEFT_RIGHT;

	private static Tree t;
	private static CategoryManager cManager = CategoryManager.getInstance();

	private final int m_Categories = 0;
	private final int m_Category = 1;
	private final int m_Word = 2;

	// Se as categorias foram editadas para dar mensagem para o usuario
	private boolean edited = false;

	private static NodeLinkTreeLayout treeLayout;

	// TODO
	// visualizacao muda de lugar no primeiro clique
	// ao apertar enter, nada acontece

	public String c_label() {
		return c_label;
	}

	public boolean isEdited() {
		return edited;
	}

	public void setEdited(boolean b) {
		edited = b;
	}

	public Tree getTree() {
		return t;
	}

	public CategoryEdit() {
		super(new Visualization());

		t = new Tree();

		t.addColumn(c_label, String.class);
		t.addColumn("type", int.class);
		// 0 Category, 1 Categories, 2 Words of category

		Node root = t.addRoot();
		root.set(c_label, "Categorias");
		root.set("type", m_Categories);

		// abre categorias do CategoryManager
		this.openCategories();

		m_vis.add(tree, t);

		m_nodeRenderer = new LabelRenderer(c_label);
		m_nodeRenderer.setRenderType(AbstractShapeRenderer.RENDER_TYPE_FILL);
		m_nodeRenderer.setHorizontalAlignment(Constants.LEFT);
		m_nodeRenderer.setRoundedCorner(8, 8);
		m_edgeRenderer = new EdgeRenderer(Constants.EDGE_TYPE_CURVE);

		DefaultRendererFactory rf = new DefaultRendererFactory(m_nodeRenderer);
		rf.add(new InGroupPredicate(treeEdges), m_edgeRenderer);
		m_vis.setRendererFactory(rf);

		// colors
		ItemAction nodeColor = new NodeColorAction(treeNodes);
		ItemAction textColor = new ColorAction(treeNodes, VisualItem.TEXTCOLOR,
				ColorLib.rgb(10, 10, 10));
		m_vis.putAction("textColor", textColor);

		ItemAction edgeColor = new ColorAction(treeEdges,
				VisualItem.STROKECOLOR, ColorLib.rgb(200, 200, 200));

		// quick repaint
		ActionList repaint = new ActionList();
		repaint.add(nodeColor);
		repaint.add(new RepaintAction());
		m_vis.putAction("repaint", repaint);

		// full paint
		ActionList fullPaint = new ActionList();
		fullPaint.add(nodeColor);
		m_vis.putAction("fullPaint", fullPaint);

		// animate paint change
		ActionList animatePaint = new ActionList(400);
		animatePaint.add(new ColorAnimator(treeNodes));
		animatePaint.add(new RepaintAction());
		m_vis.putAction("animatePaint", animatePaint);

		// create the tree layout action
		treeLayout = new NodeLinkTreeLayout(tree, m_orientation, 50, 1, 8);
		treeLayout.setLayoutAnchor(new Point2D.Double(150, 300));
		m_vis.putAction("treeLayout", treeLayout);

		CollapsedSubtreeLayout subLayout = new CollapsedSubtreeLayout(tree,
				m_orientation);
		m_vis.putAction("subLayout", subLayout);

		AutoPanAction autoPan = new AutoPanAction();

		// create the filtering and layout
		ActionList filter = new ActionList();
		filter.add(new FisheyeTreeFilter(tree, 2));
		filter.add(new FontAction(treeNodes, FontLib.getFont("Tahoma", 16)));
		filter.add(treeLayout);
		filter.add(subLayout);
		filter.add(textColor);
		filter.add(nodeColor);
		filter.add(edgeColor);
		m_vis.putAction("filter", filter);

		// animated transition
		ActionList animate = new ActionList(1000);
		animate.setPacingFunction(new SlowInSlowOutPacer());
		animate.add(autoPan);
		animate.add(new QualityControlAnimator());
		animate.add(new VisibilityAnimator(tree));
		animate.add(new LocationAnimator(treeNodes));
		animate.add(new ColorAnimator(treeNodes));
		animate.add(new RepaintAction());
		m_vis.putAction("animate", animate);
		m_vis.alwaysRunAfter("filter", "animate");

		// create animator for orientation changes
		ActionList orient = new ActionList(2000);
		orient.setPacingFunction(new SlowInSlowOutPacer());
		orient.add(autoPan);
		orient.add(new QualityControlAnimator());
		orient.add(new LocationAnimator(treeNodes));
		orient.add(new RepaintAction());
		m_vis.putAction("orient", orient);

		// ------------------------------------------------
		PopupMenuController popup = new PopupMenuController(m_vis);

		// initialize the display
		setSize(800, 600);
		setItemSorter(new TreeDepthItemSorter());
		addControlListener(new ZoomToFitControl());
		addControlListener(new ZoomControl());
		addControlListener(new WheelZoomControl());
		addControlListener(new PanControl());
		addControlListener(new FocusControl(1, "filter"));
		addControlListener(popup);

		registerKeyboardAction(new OrientAction(Constants.ORIENT_LEFT_RIGHT),
				"left-to-right", KeyStroke.getKeyStroke("ctrl 1"), WHEN_FOCUSED);
		registerKeyboardAction(new OrientAction(Constants.ORIENT_TOP_BOTTOM),
				"top-to-bottom", KeyStroke.getKeyStroke("ctrl 2"), WHEN_FOCUSED);
		registerKeyboardAction(new OrientAction(Constants.ORIENT_RIGHT_LEFT),
				"right-to-left", KeyStroke.getKeyStroke("ctrl 3"), WHEN_FOCUSED);
		registerKeyboardAction(new OrientAction(Constants.ORIENT_BOTTOM_TOP),
				"bottom-to-top", KeyStroke.getKeyStroke("ctrl 4"), WHEN_FOCUSED);

		// ------------------------------------------------

		// filter graph and perform layout
		setOrientation(m_orientation);
		m_vis.run("filter");

		search = new PrefixSearchTupleSet();
		m_vis.addFocusGroup(Visualization.SEARCH_ITEMS, search);
		search.addTupleSetListener(new TupleSetListener() {
			public void tupleSetChanged(TupleSet t, Tuple[] add, Tuple[] rem) {
				m_vis.cancel("animatePaint");
				m_vis.run("fullPaint");
				m_vis.run("animatePaint");
			}
		});

	}

	private TupleSet search;

	private void openCategories() {

		List<Category> categories = cManager.getCategories();

		for (Category c : categories) {

			Node category = t.addNode();

			category.set(c_label, c.getName());
			category.set("type", m_Category);

			t.addChildEdge(t.getRoot(), category);

			for (CategoryWord w : c.getWords()) {

				Node word = t.addChild(category);
				word.set("label", w.getName());
				word.set("type", m_Word);

			}
		}
	}

	// ------------------------------------------------------------------------

	public void setOrientation(int orientation) {
		NodeLinkTreeLayout rtl = (NodeLinkTreeLayout) m_vis
				.getAction("treeLayout");
		CollapsedSubtreeLayout stl = (CollapsedSubtreeLayout) m_vis
				.getAction("subLayout");
		switch (orientation) {
		case Constants.ORIENT_LEFT_RIGHT:
			m_nodeRenderer.setHorizontalAlignment(Constants.LEFT);
			m_edgeRenderer.setHorizontalAlignment1(Constants.RIGHT);
			m_edgeRenderer.setHorizontalAlignment2(Constants.LEFT);
			m_edgeRenderer.setVerticalAlignment1(Constants.CENTER);
			m_edgeRenderer.setVerticalAlignment2(Constants.CENTER);
			break;
		case Constants.ORIENT_RIGHT_LEFT:
			m_nodeRenderer.setHorizontalAlignment(Constants.RIGHT);
			m_edgeRenderer.setHorizontalAlignment1(Constants.LEFT);
			m_edgeRenderer.setHorizontalAlignment2(Constants.RIGHT);
			m_edgeRenderer.setVerticalAlignment1(Constants.CENTER);
			m_edgeRenderer.setVerticalAlignment2(Constants.CENTER);
			break;
		case Constants.ORIENT_TOP_BOTTOM:
			m_nodeRenderer.setHorizontalAlignment(Constants.CENTER);
			m_edgeRenderer.setHorizontalAlignment1(Constants.CENTER);
			m_edgeRenderer.setHorizontalAlignment2(Constants.CENTER);
			m_edgeRenderer.setVerticalAlignment1(Constants.BOTTOM);
			m_edgeRenderer.setVerticalAlignment2(Constants.TOP);
			break;
		case Constants.ORIENT_BOTTOM_TOP:
			m_nodeRenderer.setHorizontalAlignment(Constants.CENTER);
			m_edgeRenderer.setHorizontalAlignment1(Constants.CENTER);
			m_edgeRenderer.setHorizontalAlignment2(Constants.CENTER);
			m_edgeRenderer.setVerticalAlignment1(Constants.TOP);
			m_edgeRenderer.setVerticalAlignment2(Constants.BOTTOM);
			break;
		default:
			throw new IllegalArgumentException(
					"Unrecognized orientation value: " + orientation);
		}
		m_orientation = orientation;
		rtl.setOrientation(orientation);
		stl.setOrientation(orientation);
	}

	public int getOrientation() {
		return m_orientation;
	}

	// ------------------------------------------------------------------------

	// ------------------------------------------------------------------------

	public class OrientAction extends AbstractAction {
		private int orientation;

		public OrientAction(int orientation) {
			this.orientation = orientation;
		}

		public void actionPerformed(ActionEvent evt) {
			setOrientation(orientation);
			getVisualization().cancel("orient");
			getVisualization().run("treeLayout");
			getVisualization().run("orient");
		}
	}

	public class AutoPanAction extends Action {
		private Point2D m_start = new Point2D.Double();
		private Point2D m_end = new Point2D.Double();
		private Point2D m_cur = new Point2D.Double();
		private int m_bias = 150;

		public void run(double frac) {
			TupleSet ts = m_vis.getFocusGroup(Visualization.FOCUS_ITEMS);
			if (ts.getTupleCount() == 0)
				return;

			if (frac == 0.0) {
				int xbias = 0, ybias = 0;
				switch (m_orientation) {
				case Constants.ORIENT_LEFT_RIGHT:
					xbias = m_bias;
					break;
				case Constants.ORIENT_RIGHT_LEFT:
					xbias = -m_bias;
					break;
				case Constants.ORIENT_TOP_BOTTOM:
					ybias = m_bias;
					break;
				case Constants.ORIENT_BOTTOM_TOP:
					ybias = -m_bias;
					break;
				}

				VisualItem vi = (VisualItem) ts.tuples().next();
				m_cur.setLocation(getWidth() / 2, getHeight() / 2);
				getAbsoluteCoordinate(m_cur, m_start);
				m_end.setLocation(vi.getX() + xbias, vi.getY() + ybias);
			} else {
				m_cur.setLocation(m_start.getX() + frac
						* (m_end.getX() - m_start.getX()), m_start.getY()
						+ frac * (m_end.getY() - m_start.getY()));
				panToAbs(m_cur);
			}
		}
	}

	public static class NodeColorAction extends ColorAction {

		public NodeColorAction(String group) {
			super(group, VisualItem.FILLCOLOR);
		}

		public int getColor(VisualItem item) {
			if (m_vis.isInGroup(item, Visualization.SEARCH_ITEMS))
				return ColorLib.rgb(255, 190, 190);
			else if (m_vis.isInGroup(item, Visualization.FOCUS_ITEMS))
				return ColorLib.rgb(198, 229, 229);
			else if (item.getDOI() > -1)
				return ColorLib.rgb(164, 193, 193);
			else
				return ColorLib.rgba(255, 255, 255, 0);
		}

	} // end of inner class TreeMapColorAction

	public static class PopupMenuController extends ControlAdapter implements
			ActionListener {

		private Graph g;
		private Display d;
		private Visualization vis;
		private VisualItem clickedItem;

		private JPopupMenu nodePopupMenu;
		private JPopupMenu popupMenu;

		private Point2D mousePosition = new Point2D.Double();
		private VisualItem nodeVisualDummy;
		public Node nodeSourceDummy;
		public Edge edgeDummy;
		private boolean creatingEdge = false;
		private boolean editing;

		public PopupMenuController(Visualization vis) {
			this.vis = vis;
			this.g = (Graph) vis.getSourceData(tree);
			this.d = vis.getDisplay(0);

			//createDummy();

			// create popupMenu for nodes
			nodePopupMenu = new JPopupMenu();

			JMenuItem delete = new JMenuItem("delete", 'd');
			JMenuItem editText = new JMenuItem("edit Name", 'a');
			JMenuItem addEdge = new JMenuItem("add Edge", 'e');
			JMenuItem addNode = new JMenuItem("add Node", 'n');

			delete.setActionCommand("node_delete");
			editText.setActionCommand("node_editText");
			addEdge.setActionCommand("node_addEdge");
			addNode.setActionCommand("node_addNode");

			//nodePopupMenu.addSeparator();
			//nodePopupMenu.add(delete);
			//nodePopupMenu.addSeparator();
			nodePopupMenu.add(editText);
			//nodePopupMenu.addSeparator();
			//nodePopupMenu.add(addEdge);
			//nodePopupMenu.add(addNode);

			delete.setMnemonic(KeyEvent.VK_D);
			editText.setMnemonic(KeyEvent.VK_A);
			addEdge.setMnemonic(KeyEvent.VK_E);
			addNode.setMnemonic(KeyEvent.VK_N);

			delete.addActionListener(this);
			editText.addActionListener(this);
			addEdge.addActionListener(this);
			addNode.addActionListener(this);

			/*
			// create popupMenu for 'background'
			popupMenu = new JPopupMenu();
			addNode = new JMenuItem("add Node", 'n');
			addNode.setActionCommand("addNode");
			popupMenu.addSeparator();
			popupMenu.add(addNode);
			addNode.setMnemonic(KeyEvent.VK_N);
			addNode.addActionListener(this);
			*/

		}

		// ---------------------------------------------
		// --- methods for event processing
		// ---------------------------------------------

		@Override
		public void itemClicked(VisualItem item, MouseEvent e) {
			if (SwingUtilities.isRightMouseButton(e)) {
				clickedItem = item;
				// on rightclick, stop the edge creation
//				if (creatingEdge) {
//					stopEdgeCreation();
//					return;
//				}

				if (item instanceof NodeItem) {
					nodePopupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			} else if (SwingUtilities.isLeftMouseButton(e)) {
//				if (creatingEdge) {
//					g.addEdge(edgeDummy.getSourceNode(), (Node) item
//							.getSourceTuple());
//				}
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (editing) {
				stopEditing();
			}
			if (SwingUtilities.isRightMouseButton(e)) {
				clickedItem = null;
//				if (creatingEdge)
//					stopEdgeCreation();
//				popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			// called, when keyReleased events on displays textEditor are fired
			if (e.getKeyCode() == KeyEvent.VK_ENTER && editing) {
				stopEditing();
			}
			System.out.println("FOI");
		}

		@Override
		public void itemKeyReleased(VisualItem item, KeyEvent e) {
			keyReleased(e);
		}

		/**
		 * called on popupMenu Action
		 */
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().startsWith("node")) {
				if (e.getActionCommand().endsWith("delete")) {
					//g.removeNode((Node) clickedItem.getSourceTuple());
				} else if (e.getActionCommand().endsWith("editText")) {
					startEditing(clickedItem);
				} else if (e.getActionCommand().endsWith("addEdge")) {
					//creatingEdge = true;
					//createTemporaryEdgeFromSourceToDummy(clickedItem);
				} else if (e.getActionCommand().endsWith("addNode")) {
					//addNewNode(clickedItem);
				}
			} else {
				if (e.getActionCommand().equals("addNode")) {
//					int node = (int) (Math.random() * (g.getNodeCount() - 1));
//					Node source = g.getNode(node); // random source
//					addNewNode(source);
				} else {

				}
			}
		}

		// ---------------------------------------------
		// --- helper methods
		// ---------------------------------------------

		private void startEditing(VisualItem item) {
			editing = true;
			d.editText(item, c_label);
		}

		private void stopEditing() {
			editing = false;
			d.stopEditing();
		}

//		private void addNewNode(VisualItem source) {
//			addNewNode((Node) source.getSourceTuple());
//		}
//
//		private void addNewNode(Node source) {
//			Node n = g.addNode(); // create a new node
//			n.set(VisualItem.LABEL, "Node " + n.getRow()); // assign a new name
//			g.addEdge(source, n); // add an edge from source to the new node
//		}

		// ---------------------------------------------
		// --- methods for edgeCreation
		// ---------------------------------------------

//		private void stopEdgeCreation() {
//			creatingEdge = false;
//			removeEdgeDummy();
//		}
//
//		/**
//		 * Removes all dummies, the node and the two edges. Additionally sets
//		 * the variables who stored a reference to these dummies to null.
//		 */
//		public void removeAllDummies() {
//			if (nodeSourceDummy != null)
//				g.removeNode(nodeSourceDummy);
//			edgeDummy = null;
//			nodeSourceDummy = null;
//			nodeVisualDummy = null;
//		}
//
//		/**
//		 * Removes all edge dummies, if the references stored to these dummies
//		 * are not null. Additionally sets the references to these dummies to
//		 * null.
//		 */
//		private void removeEdgeDummy() {
//			if (edgeDummy != null) {
//				g.removeEdge(edgeDummy);
//				edgeDummy = null;
//			}
//		}
//
//		public VisualItem createDummy() {
//			// create the dummy node for the creatingEdge mode
//			nodeSourceDummy = g.addNode();
//			nodeSourceDummy.set(VisualItem.LABEL, "");
//
//			nodeVisualDummy = vis.getVisualItem(treeNodes, nodeSourceDummy);
//			nodeVisualDummy.setSize(0.0);
//			nodeVisualDummy.setVisible(false);
//
//			/*
//			 * initially set the dummy's location. upon mouseMove events, we
//			 * will do that there. otherwise, the dummy would appear on top left
//			 * position until the mouse moves
//			 */
//			double x = d.getBounds().getCenterX();
//			double y = d.getBounds().getCenterY();
//			mousePosition.setLocation(x, y);
//			nodeVisualDummy.setX(mousePosition.getX());
//			nodeVisualDummy.setY(mousePosition.getY());
//			return nodeVisualDummy;
//		}
//
//		public void removeNodeDummy() {
//			g.removeNode(nodeSourceDummy);
//			nodeSourceDummy = null;
//			nodeVisualDummy = null;
//		}
//
//		public void createTemporaryEdgeFromSourceToDummy(Node source) {
//			if (edgeDummy == null) {
//				edgeDummy = g.addEdge(source, nodeSourceDummy);
//			}
//		}
//
//		public void createTemporaryEdgeFromDummyToTarget(Node target) {
//			if (edgeDummy == null) {
//				edgeDummy = g.addEdge((Node) nodeVisualDummy.getSourceTuple(),
//						target);
//			}
//		}
//
//		/**
//		 * @param source
//		 *            the item to use as source for the dummy edge
//		 */
//		public void createTemporaryEdgeFromSourceToDummy(VisualItem source) {
//			createTemporaryEdgeFromSourceToDummy((Node) source.getSourceTuple());
//		}
//
//		/**
//		 * @param target
//		 *            the item to use as target for the dummy edge
//		 */
//		public void createTemporaryEdgeFromDummyToTarget(VisualItem target) {
//			createTemporaryEdgeFromDummyToTarget((Node) target.getSourceTuple());
//		}
//
//		@Override
//		public void mouseMoved(MouseEvent e) {
//			// necessary, if you have no dummy and this ControlAdapter is
//			// running
//			if (nodeVisualDummy == null)
//				return;
//			// update the coordinates of the dummy-node to the mouselocation so
//			// the tempEdge is drawn to the mouselocation too
//			d.getAbsoluteCoordinate(e.getPoint(), mousePosition);
//			nodeVisualDummy.setX(mousePosition.getX());
//			nodeVisualDummy.setY(mousePosition.getY());
//		}

		/**
		 * only necessary if edge-creation is used together with aggregates and
		 * the edge should move on when mousepointer moves within an aggregate
		 */
		@Override
		public void itemMoved(VisualItem item, MouseEvent e) {
			if (item instanceof AggregateItem)
				mouseMoved(e);
		}

	} // end of inner class PopupMenuController

}
