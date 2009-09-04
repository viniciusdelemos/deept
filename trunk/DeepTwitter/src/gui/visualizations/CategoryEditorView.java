package gui.visualizations;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import model.Category;
import model.Tag;
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
import prefuse.controls.ControlAdapter;
import prefuse.controls.FocusControl;
import prefuse.controls.PanControl;
import prefuse.controls.WheelZoomControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
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
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;
import prefuse.visual.sort.TreeDepthItemSorter;
import controller.CategoryManager;

public class CategoryEditorView extends Display {

	private final static String tree = "tree";
	private final static String treeNodes = "tree.nodes";
	private final String treeEdges = "tree.edges";

	private LabelRenderer m_nodeRenderer;
	private EdgeRenderer m_edgeRenderer;

	
	private int m_orientation = Constants.ORIENT_LEFT_RIGHT;

	private Tree t;
	private static CategoryManager cManager = CategoryManager.getInstance();

	private final int t_Categories = 0;
	private final int t_Category = 1;
	private final int t_Word = 2;
	
	private static String c_label = "label";
	private static String c_type = "type";

	// Se as categorias foram editadas para dar mensagem para o usuario
	private boolean edited = false;

	private NodeLinkTreeLayout treeLayout;


	public CategoryEditorView() {
		super(new Visualization());

		t = new Tree();

		t.addColumn(c_label, String.class);
		t.addColumn(c_type, int.class);
		// 0 Category, 1 Categories, 2 Words of category

		Node root = t.addRoot();
		root.set(c_label, "Categories");
		root.set(c_type, t_Categories);

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

		//AutoPanAction autoPan = new AutoPanAction();

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
		ActionList animate = new ActionList(200);
		//animate.setPacingFunction(new SlowInSlowOutPacer());
		//animate.add(autoPan);
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
		//orient.add(autoPan);
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
		getTextEditor().addKeyListener(popup);

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

		TupleSet search = new PrefixSearchTupleSet();
		m_vis.addFocusGroup(Visualization.SEARCH_ITEMS, search);
		search.addTupleSetListener(new TupleSetListener() {
			public void tupleSetChanged(TupleSet t, Tuple[] add, Tuple[] rem) {
				m_vis.cancel("animatePaint");
				m_vis.run("fullPaint");
				m_vis.run("animatePaint");
			}
		});

	}
	
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

	public NodeLinkTreeLayout getTreeLayout() {
		return treeLayout;
	}

	private void openCategories() {

		List<Category> categories = cManager.getCategories();

		for (Category c : categories) {

			Node category = t.addNode();

			category.set(c_label, c.getName());
			category.set(c_type, t_Category);

			t.addChildEdge(t.getRoot(), category);

			for (Tag w : c.getTags()) {

				Node word = t.addChild(category);
				word.set("label", w.getName());
				word.set(c_type, t_Word);

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

	private class PopupMenuController extends ControlAdapter implements
			ActionListener {

		private Visualization vis;
		private Display d;
		private VisualItem clickedItem;

		private JPopupMenu nodePopupMenu;
		private boolean editing;

		private final String l_editLabel = "editLabel";
		private final String l_delete = "delete";
		private final String l_addCategory = "addCategory";
		private final String l_addWord = "addWord";

		private JMenuItem delete;
		private JMenuItem edit;
		private JMenuItem addCategory;
		private JMenuItem addWord;

		private String defaultTextNewCategory = "New Category";
		private String defaultTextNewWord = "New Tag";

		public PopupMenuController(Visualization vis) {
			this.d = vis.getDisplay(0);
			this.vis = vis;

			nodePopupMenu = new JPopupMenu();

			delete = new JMenuItem("Delete");
			edit = new JMenuItem("Edit");
			addCategory = new JMenuItem("Add category");
			addWord = new JMenuItem("Add tag");

			delete.setActionCommand(l_delete);
			edit.setActionCommand(l_editLabel);
			addCategory.setActionCommand(l_addCategory);
			addWord.setActionCommand(l_addWord);

			edit.addActionListener(this);
			addCategory.addActionListener(this);
			addWord.addActionListener(this);
			delete.addActionListener(this);

		}

		// ---------------------------------------------
		// --- methods for event processing
		// ---------------------------------------------

		@Override
		public void itemClicked(VisualItem item, MouseEvent e) {
			if (SwingUtilities.isRightMouseButton(e)) {
				clickedItem = item;
				if (item instanceof NodeItem) {

					Visualization vis = item.getVisualization();
					TupleSet ts = vis.getFocusGroup(Visualization.FOCUS_ITEMS);
					ts.setTuple(item);
					vis.run("filter");

					nodePopupMenu.removeAll();

					int type = item.getInt(c_type);

					if (type == t_Categories) {
						nodePopupMenu.add(addCategory);
					} else if (type == t_Category) {
						nodePopupMenu.add(edit);						
						nodePopupMenu.add(addWord);
						nodePopupMenu.add(delete);
					} else if (type == t_Word) {
						nodePopupMenu.add(edit);
						nodePopupMenu.add(delete);
					}
					nodePopupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			} else if (SwingUtilities.isLeftMouseButton(e)) {
				if (editing)
					stopEditing();
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (editing) {
				stopEditing();
				vis.run("filter");
			}
			if (SwingUtilities.isRightMouseButton(e)) {
				clickedItem = null;
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			// called, when keyReleased events on displays textEditor are fired
			if (e.getKeyCode() == KeyEvent.VK_ENTER && editing) {
				stopEditing();
			}
		}

		@Override
		public void itemKeyReleased(VisualItem item, KeyEvent e) {
			keyReleased(e);
		}

		/**
		 * called on popupMenu Action
		 */
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals(l_editLabel)) {
				startEditing(clickedItem);
			} else if (e.getActionCommand().equals(l_addCategory)) {
				addCategory();
			} else if (e.getActionCommand().equals(l_addWord)) {
				addWord();
			} else if (e.getActionCommand().equals(l_delete)) {
				delete();
			}
		}

		// ---------------------------------------------
		// --- helper methods
		// ---------------------------------------------
		private void delete() {

			if (clickedItem != null) {
				Node n = (Node) m_vis.getSourceTuple(clickedItem);

				if (n.getInt(c_type) == t_Category) {
					// Primeiro remove palavras do nodo
					// Iterator i = t.children(n);
					// while (i.hasNext()) {
					// Node word = (Node) i.next();
					// t.removeNode(word);
					// }

					// Depois remove categoria
					t.removeNode(n);
				} else if (n.getInt(c_type) == t_Word) {
					t.removeNode(n);
				}
			}

			m_vis.run("filter");
			editedTrue("delete");

		}

		// TODO
		// se usuario nao salvar, e tentar fechar, avisar ele
		// se usuario salvar, colocar mensagem de que foi salvo

		private void addWord() {

			if (clickedItem != null) {
				Node n = (Node) m_vis.getSourceTuple(clickedItem);

				Iterator i = t.children(n);
				while (i.hasNext()) {
					Node word = (Node) i.next();
					if (word.getString(c_label).toLowerCase().equals(
							defaultTextNewWord.toLowerCase())) {
						JOptionPane.showMessageDialog(null,
								"Primeiro edite a palavra com label "
										+ defaultTextNewWord + " da Categoria "
										+ n.getString(c_label), "Atenção",
								JOptionPane.WARNING_MESSAGE);
						// TODO colocar como modal do frame
						return;
					}
				}

				Node newWord = t.addNode();
				newWord.set(c_label, defaultTextNewWord);
				newWord.set(c_type, t_Word);

				t.addChildEdge(n, newWord);

				// Adicionando no grupo para search
				// TupleSet ts = vis.getFocusGroup(Visualization.SEARCH_ITEMS);
				// ts.addTuple(newWord);

			}
			m_vis.run("filter");
			editedTrue("addWord");
		}

		private void addCategory() {

			Iterator i = t.children(t.getRoot());
			while (i.hasNext()) {
				Node ch = (Node) i.next();

				if (ch.getString(c_label).toLowerCase().equals(
						defaultTextNewCategory.toLowerCase())) {
					JOptionPane.showMessageDialog(null,
							"Primeiro edite a categoria com label "
									+ defaultTextNewCategory, "Atenção",
							JOptionPane.WARNING_MESSAGE);
					// TODO colocar como modal do frame
					return;
				}
			}

			Node n = t.addNode();
			n.set(c_label, defaultTextNewCategory);
			n.set(c_type, t_Category);

			t.addChildEdge(t.getRoot(), n);

			// Adicionando no grupo para search
			// TupleSet ts = vis.getFocusGroup(Visualization.SEARCH_ITEMS);
			// VisualItem visualItem = getVisualization().getVisualItem(tree,
			// n);
			// ts.addTuple(visualItem);

			m_vis.run("filter");
			editedTrue("addCategory");

		}

		private void startEditing(VisualItem item) {
			m_vis.run("filter");
			editing = true;
			d.editText(item, c_label);
			editedTrue("startEditing: " + item);
		}

		private void stopEditing() {
			System.out.println(clickedItem.getString(c_label) + "\t" + d.getTextEditor().getText());
			editing = false;
			d.stopEditing();
			m_vis.run("filter");
			editedTrue("stopEditing");
		}
		
		private void isEquals(){
			if(clickedItem.getInt(c_type) == t_Category){
				
			}
			else if(clickedItem.getInt(c_type) == t_Word){
				
			}
		}

		private void editedTrue(String from) {
			edited = true;
			// System.out.println("Edited from: "+from);
		}

	} // end of inner class PopupMenuController

}
