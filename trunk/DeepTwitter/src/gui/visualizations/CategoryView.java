package gui.visualizations;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import controller.CategoryController;

import model.ChartColor;
import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.filter.GraphDistanceFilter;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.controls.ControlAdapter;
import prefuse.controls.DragControl;
import prefuse.controls.FocusControl;
import prefuse.controls.NeighborHighlightControl;
import prefuse.controls.PanControl;
import prefuse.controls.WheelZoomControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Table;
import prefuse.data.Tuple;
import prefuse.data.event.TupleSetListener;
import prefuse.data.tuple.DefaultTupleSet;
import prefuse.data.tuple.TupleSet;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.render.PolygonRenderer;
import prefuse.render.Renderer;
import prefuse.util.ColorLib;
import prefuse.util.force.ForceSimulator;
import prefuse.util.ui.JForcePanel;
import prefuse.util.ui.JValueSlider;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;
import twitter4j.Tweet;

public class CategoryView extends Display {

	private static final String graphNodesAndEdges = "graph";
	private static final String graphNodes = "graph.nodes";
	private static final String graphEdges = "graph.edges";
	private static final String RUBBER_BAND = "rubberband";
	private static final String NODES = graphNodes;
	private static final String SELECTED = "sel";
	private LabelRenderer m_nodeRenderer;
	private EdgeRenderer m_edgeRenderer;
	
	//private Map<String, String> usersPictures;
	
	CategoryController categoryController;

	ActionList layout;

	JSplitPane split;

	Display display;

	private Graph g;

	public CategoryView(CategoryController categoryController) {
		
		super(new Visualization());
		
		this.categoryController = categoryController;
		
		//this.usersPictures = usersPictures;

		
		g = new Graph(true);
		g.addColumn(VisualItem.LABEL, VisualItem.LABEL.getClass());
		g.addColumn("color", int.class);
		g.addColumn("status", String.class);
		g.addColumn("screenNameUser", String.class);
		g.addColumn("userName", String.class);
		g.addColumn("imageUrl", String.class);
		
		//initDataGroups();

		// -- set up visualization --
		m_vis.add(graphNodesAndEdges, g);

		// -- set up renderers --
		m_nodeRenderer = new LabelRenderer(VisualItem.LABEL);
		m_nodeRenderer.setHorizontalAlignment(Constants.CENTER);
		m_nodeRenderer.setRoundedCorner(8, 8);
		m_edgeRenderer = new EdgeRenderer();

		DefaultRendererFactory rf = new DefaultRendererFactory(m_nodeRenderer);
		rf.add(new InGroupPredicate(graphEdges), m_edgeRenderer);
		m_vis.setRendererFactory(rf);

		// -- set up processing actions --

		// colors
		ColorAction nodeTextColor = new ColorAction(graphNodes,
				VisualItem.TEXTCOLOR);
		ColorAction nodeFillColor = new ColorAction(graphNodes,
				VisualItem.FILLCOLOR, ColorLib.rgb(234, 234, 234));
		nodeFillColor.add("_hover", ColorLib.rgb(220, 200, 200));
		nodeFillColor.add(VisualItem.HIGHLIGHT, ColorLib.rgb(220, 220, 0));
		ColorAction nodeStrokeColor = new ColorAction(graphNodes,
				VisualItem.STROKECOLOR);
		
		defineColors(nodeFillColor);


		ColorAction edgeLineColor = new ColorAction(graphEdges,
				VisualItem.STROKECOLOR, ColorLib.rgb(200, 200, 200));
		edgeLineColor.add("_hover", ColorLib.rgb(220, 100, 100));
		ColorAction edgeArrowColor = new ColorAction(graphEdges,
				VisualItem.FILLCOLOR, ColorLib.rgb(100, 100, 100));
		edgeArrowColor.add("_hover", ColorLib.rgb(220, 100, 100));

		// recolor
		ActionList recolor = new ActionList();
		recolor.add(nodeTextColor);
		recolor.add(nodeFillColor);
		recolor.add(nodeStrokeColor);
		recolor.add(edgeLineColor);
		recolor.add(edgeArrowColor);
		m_vis.putAction("recolor", recolor);

		int maxhops = 4, hops = 4;

		ForceDirectedLayout fdl = new ForceDirectedLayout(graphNodesAndEdges,
				true);
		final GraphDistanceFilter filter = new GraphDistanceFilter(
				graphNodesAndEdges, hops);

		ForceSimulator fsim = fdl.getForceSimulator();
		fsim.getForces()[0].setParameter(0, -1.2f);

		ActionList layout = new ActionList(ActionList.INFINITY);
		layout.add(fdl);
		layout.add(recolor);
		layout.add(new RepaintAction());
		m_vis.putAction("layout", layout);

		m_vis.putAction("repaint", new RepaintAction());

		// Create the focus group
		TupleSet selectedItems = new DefaultTupleSet();
		m_vis.addFocusGroup(SELECTED, selectedItems);

		// listen for changes
		TupleSet focusGroup = m_vis.getGroup(SELECTED);
		focusGroup.addTupleSetListener(new TupleSetListener() {
			public void tupleSetChanged(TupleSet ts, Tuple[] add, Tuple[] rem) {
				// do whatever you do with newly selected/deselected items
				// m_vis.cancel("layout");
				for (int i = 0; i < add.length; i++) {
					VisualItem item = (VisualItem) add[i];
					item.setHighlighted(true);
				}
				for (int i = 0; i < rem.length; i++) {
					VisualItem item = (VisualItem) rem[i];
					item.setHighlighted(false);
				}

				// m_vis.run("layout");
			}
		});

		// Create the rubber band object for rendering on screen
		Table rubberBandTable = new Table();
		rubberBandTable.addColumn(VisualItem.POLYGON, float[].class);
		rubberBandTable.addRow();
		m_vis.add(RUBBER_BAND, rubberBandTable);
		VisualItem rubberBand = (VisualItem) m_vis.getVisualGroup(RUBBER_BAND)
				.tuples().next();
		rubberBand.set(VisualItem.POLYGON, new float[8]);
		rubberBand.setStrokeColor(ColorLib.color(ColorLib.getColor(255, 0, 0)));

		// render the rubber band with the default polygon renderer
		Renderer rubberBandRenderer = new PolygonRenderer(
				Constants.POLY_TYPE_LINE);
		rf.add(new InGroupPredicate(RUBBER_BAND), rubberBandRenderer);

		// Link the rubber band control to the rubber band display object
		// addControlListener(new RubberBandSelect(rubberBand));

		// initialize the display
		// PopupMenuController popup = new PopupMenuController(m_vis);

		display = new Display(m_vis);
		display.setSize(500, 500);
		display.setForeground(Color.GRAY);
		display.setBackground(Color.WHITE);

		// main display controls
		display.addControlListener(new FocusControl(1));
		display.addControlListener(new DragControl());
		display.addControlListener(new PanControl());
		display.addControlListener(new ZoomControl());
		display.addControlListener(new WheelZoomControl());
		display.addControlListener(new ZoomToFitControl());
		display.addControlListener(new NeighborHighlightControl());
		display.addControlListener(new Agregate());
		// display.addControlListener(new HoverTooltip());

		display.setForeground(Color.GRAY);
		display.setBackground(Color.WHITE);

		// --------------------------------------------------------------------
		// STEP 5: launching the visualization

		// create a panel for editing force values
		final JForcePanel fpanel = new JForcePanel(fsim);

		final JValueSlider slider = new JValueSlider("Distance", 0, maxhops,
				hops);
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				filter.setDistance(slider.getValue().intValue());
				m_vis.run("draw");
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

		// create a new JSplitPane to present the interface
		split = new JSplitPane();
		split.setLeftComponent(display);
		split.setRightComponent(fpanel);
		split.setOneTouchExpandable(true);
		split.setContinuousLayout(false);
		split.setDividerLocation(530);
		split.setDividerLocation(800);

		// pan(400, 300);
		// zoom(new Point2D.Double(400,300), 1.75);
		// addControlListener(new DragControl());
		// addControlListener(new
		// ZoomToFitControl(Control.MIDDLE_MOUSE_BUTTON));
		// addControlListener(new ZoomControl());
		// addControlListener(new AggregateDragControl());

		// addControlListener(new PanControl());
		// addControlListener(popup);
		// makes us able to stop TextEditor by special KeyEvents (e.g. Enter)
		// getTextEditor().addKeyListener(popup);

		// filter graph and perform layout
		m_vis.run("layout");

	}
	
	public void addNode(int color, Tweet tweet, String userName){
		
//		g.addColumn(VisualItem.LABEL, VisualItem.LABEL.getClass());
//		g.addColumn("color", int.class);
//		g.addColumn("status", String.class);
//		g.addColumn("screenNameUser", String.class);
//		g.addColumn("userName", String.class);
		
		if(color > 7){
			Node node = g.addNode();
			node.set(VisualItem.LABEL, "   ");
			node.set("status", tweet.getText());
			node.set("screenNameUser", tweet.getFromUser());
			node.set("userName", userName);
			node.set("imageUrl", tweet.getProfileImageUrl());
		}
		else{

			Node node = g.addNode();
			node.set(VisualItem.LABEL, "   ");
			node.set("color", color);
			node.set("status", tweet.getText());
			node.set("screenNameUser", tweet.getFromUser());
			node.set("userName", userName);
			node.set("imageUrl", tweet.getProfileImageUrl());
		}
		
	}
	
	private void defineColors(ColorAction nodeFillColor){
		
		nodeFillColor.add("color==1", new Color(235, 70, 94).getRGB());
		nodeFillColor.add("color==2", new Color(40, 72, 114).getRGB());
		nodeFillColor.add("color==3", new Color(205, 105, 5).getRGB());
		nodeFillColor.add("color==4", new Color(205, 199, 5).getRGB());
		nodeFillColor.add("color==5", new Color(125, 167, 21).getRGB());
		
		//nodeFillColor.add("color==1", ChartColor.RED.getRGB());
		//nodeFillColor.add("color==2", ChartColor.BLUE.getRGB());
		//nodeFillColor.add("color==3", ChartColor.GREEN.getRGB());
		//nodeFillColor.add("color==4", ChartColor.GRAY.getRGB());
		//nodeFillColor.add("color==5", ChartColor.MAGENTA.getRGB());
		nodeFillColor.add("color==6", ChartColor.VERY_DARK_BLUE.getRGB());
		nodeFillColor.add("color==7", ChartColor.DARK_BLUE.getRGB());
		
		
		
		
	}
	/*
	private void initDataGroups() {

		// create sample graph


		int k = 15;

		for (int i = 0; i < k; i++) {
			Node n10 = g.addNode();
			n10.set(VisualItem.LABEL, "");
			n10.setString("nome", "Node " + i);

			if (i % 3 == 0)
				n10.setInt("weight", 1);
			else if (i % 3 == 1)
				n10.setInt("weight", 2);
			else
				n10.setInt("weight", 0);
		}
	}
	*/

	public void setJDialog() {
		JPanel main = new JPanel(new BorderLayout());
		main.add(split);
		// main.add(ed, BorderLayout.CENTER);
		// main.add(south, BorderLayout.SOUTH);

		JDialog jDialog = new JDialog();
		jDialog.setTitle("Categorias DeepTwitter");
		jDialog.setContentPane(main);
		jDialog.setBounds(0, 0, 800, 600);
		jDialog.setLocationRelativeTo(null);
		jDialog.setVisible(true);

		//usaAlgoritmoRuim();

		// mudaCor();

	}

	private void mudaCor() {

		System.out.println("Chamou");

		Iterator<Node> nodes = m_vis.items(NODES);

		int cont = 0;

		while (nodes.hasNext()) {

			Node t = nodes.next();

			VisualItem visualItem = getVisualization().getVisualItem(NODES, t);

			cont++;

			if (cont == 5)
				break;

		}

	}

	private void usaAlgoritmoRuim() {

		MoveObjectsBad moveObjects = new MoveObjectsBad(this);
		moveObjects.start();

	}

	private void usaAlgoritmoBom() {
		MoveObjectsGood moveObjectsGood = new MoveObjectsGood();
		moveObjectsGood.start();
	}
	
	class Agregate extends ControlAdapter{
		
		public void itemEntered(VisualItem item, MouseEvent e){
			
            Display d = (Display)e.getSource();
            createTTT(item, d);

		}
	
		
	private void createTTT(VisualItem item, Display d) {
			
		if (!item.isInteractive())
				return;

			//int idTwitter = (Integer) item.get("idTwitter");
//			UserDeepT userDeepT = socialNetwork.getUser(idTwitter);
//
//			if (userDeepT == null)
//				return;

			StringBuffer sbuf = new StringBuffer("");
			int tipWidth = 250;

			sbuf.delete(0, sbuf.length());
			sbuf.append((new StringBuilder("<html><div style=\"width:"))
					.append(tipWidth).append("\"<table>").toString());

			/** Repetir **/
			sbuf.append("<tr valign='top'><td align='left'><b>");

			// tipo da informaçao
			sbuf.append((new StringBuilder(String.valueOf("Nome:"))).append(
					"</b></td><td align='left'>").toString());

			// Informaçao
			sbuf.append(item.get("userName"));

			sbuf.append("</td></tr>");

			/** Repetir **/

			/** Repetir **/
			sbuf.append("<tr valign='top'><td align='left'><b>");

			// tipo da informaçao
			sbuf.append((new StringBuilder(String.valueOf("Status:"))).append(
					"</b></td><td align='left'>").toString());

			// Informaçao
			sbuf.append(item.get("status"));
			
			/** repetir **/
			
			sbuf.append("</div></table>");
			
			/** Foto **/
			
			sbuf.append("<div style='text-align: center;'><img style='width: 69px; height: 69px;' alt='' src='");

			// tipo da informaçao
			
			// Informaçao
			sbuf.append(item.get("imageUrl"));
			
			sbuf.append("'");
			
			

			
			/** Foto **/

			sbuf.append("</td></tr>");
			
			sbuf.append("</div></html>");

			
			// sbuf.append("</div></table><hr color=\"#999999\"><div align=\"right\"><font color=\"#999999\">return or right-mouse button to close&nbsp;</font></div></html>");
			// if (showIt)
			
			d.setToolTipText(sbuf.toString());

		}

		public void itemExited(VisualItem item, MouseEvent e) {
			Display d = (Display) e.getSource();
			d.setToolTipText(null);
		}
	}

	class PosicaoDesejada {

		// public PosicaoDesejada(Node node, int x, int y, int dx, int dy) {
		// this.node = node;
		// this.x = x;
		// this.y = y;
		// dX = dx;
		// dY = dy;
		// //posicoes = new ArrayList<Posicoes>();
		// }

		public PosicaoDesejada(Node node, int dx, int dy) {
			this.node = node;
			dX = dx;
			dY = dy;
			// posicoes = new ArrayList<Posicoes>();
		}

		Node node;

		// List<Posicoes> posicoes;

		int dX; // final
		int dY; // final

		// int x; //inicial
		// int y; //inicial

		public String toString() {
			return "PD{" + "node:" + node + ", dX:" + dX + ", dY:" + dY + "}";
		}

	}

	/*
	 * class Posicoes {
	 * 
	 * int x; int y; }
	 */

	class MoveObjectsGood extends Thread {

		List<PosicaoDesejada> posicoesObj;

		public MoveObjectsGood() {

			posicoesObj = new ArrayList<PosicaoDesejada>();
		}

		private double fy(double a, double b, double fa, double fb, double x) {

			double ret = (((b - x) / (b - a)) * fa)
					+ (((x - a) / (b - a)) / fb);

			return ret;

		}

		private void primeiraPosicao() {

			Iterator<Node> nodes = m_vis.items(NODES);

			while (nodes.hasNext()) {

				Node t = nodes.next();

				VisualItem visualItem = getVisualization().getVisualItem(NODES,
						t);

				int dX = (int) (Math.random() * display.getBounds().width);
				int dY = (int) (Math.random() * display.getBounds().height);

				double xx = (50 + Math.random()
						* (display.getBounds().width - 100));
				double yy = (50 + Math.random()
						* (display.getBounds().height - 100));

				PosicaoDesejada posicaoDesejada = new PosicaoDesejada(t, dX, dY);

				posicoesObj.add(posicaoDesejada);

				double x = visualItem.getX();
				double y = visualItem.getY();

				visualItem.setStartX(x);
				visualItem.setX(xx);
				visualItem.setEndX(xx);

				visualItem.setStartY(y);
				visualItem.setY(yy);
				visualItem.setEndY(yy);

			}

		}

		private void novaPosicao(PosicaoDesejada p) {

			VisualItem visualItem = getVisualization().getVisualItem(NODES,
					p.node);

			// 1 quadrante
			// x < width / 2
			// y > height /2

			// 2 quadrante
			// x > width / 2
			// y > height /2

			// 3 quadrante
			// x > width /2
			// y < height /2

			// 4 quadrante
			// x < width /2
			// y < height /2

			// primeiro quadrante
			if (visualItem.getX() < (display.getWidth() / 2)
					&& visualItem.getY() > (display.getHeight() / 2)) {

				p.dX = (int) (Math.random() * (display.getWidth() / 2)); // <
				p.dY = (int) ((display.getHeight() / 2) + (Math.random() * (display
						.getHeight() / 2))); // >

				System.out.println("-> Primeiro quadrante: " + p.dX + " "
						+ p.dY);
			}

			// segundo quadrante
			else if (visualItem.getX() > (display.getWidth() / 2)
					&& visualItem.getY() > (display.getHeight() / 2)) {

				p.dX = (int) ((display.getWidth() / 2) + (Math.random() * (display
						.getWidth() / 2))); // >
				p.dY = (int) ((display.getHeight() / 2) + (Math.random() * (display
						.getHeight() / 2))); // >

				System.out
						.println("-> Segundo quadrante: " + p.dX + " " + p.dY);
			}

			// terceiro quadrante
			else if (visualItem.getX() > (display.getWidth() / 2)
					&& visualItem.getY() < (display.getHeight() / 2)) {

				p.dX = (int) ((display.getWidth() / 2) + (Math.random() * (display
						.getWidth() / 2))); // >
				p.dY = (int) (Math.random() * (display.getHeight() / 2)); // <

				System.out.println("-> Terceiro quadrante: " + p.dX + " "
						+ p.dY);
			}

			// quarto quadrante
			else if (visualItem.getX() < (display.getWidth() / 2)
					&& visualItem.getY() < (display.getHeight() / 2)) {

				p.dX = (int) (Math.random() * (display.getWidth() / 2)); // <
				p.dY = (int) (Math.random() * (display.getHeight() / 2)); // <

				System.out.println("-> Quarto quadrante: " + p.dX + " " + p.dY);

			} else {

				p.dX = (int) (Math.random() * display.getWidth());
				p.dY = (int) (Math.random() * display.getHeight());

				System.out
						.println("--> NENHUM quadrante: " + p.dX + " " + p.dY);

			}
		}

		public void run() {

			primeiraPosicao();

			while (true) {

				double y;
				int mov = 1;
				double x;

				for (PosicaoDesejada p : posicoesObj) {

					VisualItem visualItem = getVisualization().getVisualItem(
							NODES, p.node);

					if (visualItem.isHover())
						continue;

					System.out.println("Display x:" + display.getWidth()
							+ " y:" + display.getHeight() + " Objeto x:"
							+ visualItem.getX() + " y:" + visualItem.getY());

					if (visualItem.getX() < 0
							|| visualItem.getX() > display.getWidth()
							|| visualItem.getY() < 0
							|| visualItem.getY() > display.getHeight()) {

						novaPosicao(p);

					}

					x = (visualItem.getX() + mov);

					y = fy(visualItem.getX(), p.dX, visualItem.getY(), p.dY, x);

					visualItem.setStartX(visualItem.getX());
					visualItem.setX(x);
					visualItem.setEndX(x);

					visualItem.setStartY(visualItem.getY());
					visualItem.setY(y);
					visualItem.setEndY(y);

				}

				try {
					Thread.sleep(150);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}

		}
	}

	class MoveObjectsBad extends Thread {

		int contorno = 20;

		CategoryView categoryView;

		public MoveObjectsBad(CategoryView categoryView) {
			this.categoryView = categoryView;
		}

		private void novaPosicao(PosicaoDesejada p) {

			VisualItem visualItem = getVisualization().getVisualItem(NODES,
					p.node);

			int choice = (int) (Math.random() * 2);
			// 0 seta y
			// 1 seta x

			if (choice == 0) {
				p.dX = 1;
				p.dY = (int) (Math.random() * 2);
			} else {
				p.dX = (int) (Math.random() * 2);
				p.dY = 1;
			}

			visualItem.setStartX(visualItem.getX());
			visualItem.setStartY(visualItem.getY());

			int width = display.getWidth() - contorno;
			int height = display.getHeight() - contorno;

			// 1 quadrante
			if (visualItem.getX() >= width && visualItem.getY() <= contorno) {

				visualItem.setX(width - 1);
				visualItem.setEndX(width - 1);

				visualItem.setY(contorno);
				visualItem.setEndY(contorno);

			}
			// segundo quadrante
			else if (visualItem.getX() <= contorno
					&& visualItem.getY() <= contorno) {

				visualItem.setX(contorno + 1);
				visualItem.setEndX(contorno + 1);

				visualItem.setY(contorno + 1);
				visualItem.setEndY(contorno + 1);

			}
			// terceiro quadrante
			else if (visualItem.getX() <= contorno
					&& visualItem.getY() >= height) {

				visualItem.setX(contorno + 1);
				visualItem.setEndX(contorno + 1);

				visualItem.setY(height - 1);
				visualItem.setEndY(height - 1);

			}
			// quarto quadrante
			else if (visualItem.getX() >= width && visualItem.getY() >= height) {

				visualItem.setX(width - 1);
				visualItem.setEndX(width - 1);

				visualItem.setY(height - 1);
				visualItem.setEndY(height - 1);

			}

			// cima
			else if (visualItem.getY() <= contorno) {

				visualItem.setY(contorno + 1);
				visualItem.setEndY(contorno + 1);

			}

			// esquerda
			else if (visualItem.getX() < contorno) {

				visualItem.setX(contorno + 1);
				visualItem.setEndX(contorno + 1);

			}

			// baixo
			else if (visualItem.getY() >= height) {

				visualItem.setY(height - 1);
				visualItem.setEndY(height - 1);

			}

			// direita
			else if (visualItem.getX() >= width) {

				visualItem.setX(width - 1);
				visualItem.setEndX(width - 1);
			}

			/*
			 * //primeiro quadrante if(visualItem.getX() < (display.getWidth() /
			 * 2) && visualItem.getY() > (display.getHeight() / 2)){
			 * 
			 * if(choice == 0){ p.dX = 1; p.dY = (int)(Math.random() * 2); }
			 * else{ p.dX = (int)(Math.random() * 2); p.dY = 1; } }
			 * 
			 * //segundo quadrante else if(visualItem.getX() >
			 * (display.getWidth() / 2) && visualItem.getY() >
			 * (display.getHeight() / 2)){
			 * 
			 * if(choice == 0){ p.dX = 1; p.dY = (int)(Math.random() * 2); }
			 * else{ p.dX = (int)(Math.random() * 2); p.dY = 1; }
			 * 
			 * }
			 * 
			 * //terceiro quadrante else if(visualItem.getX() >
			 * (display.getWidth() / 2) && visualItem.getY() <
			 * (display.getHeight() /2)){
			 * 
			 * if(choice == 0){ p.dX = 1; p.dY = (int)(Math.random() * 2); }
			 * else{ p.dX = (int)(Math.random() * 2); p.dY = 1; }
			 * 
			 * 
			 * }
			 * 
			 * //quarto quadrante else if(visualItem.getX() <
			 * (display.getWidth() / 2) && visualItem.getY() <
			 * (display.getHeight() /2)){
			 * 
			 * if(choice == 0){ p.dX = 1; p.dY = (int)(Math.random() * 2); }
			 * else{ p.dX = (int)(Math.random() * 2); p.dY = 1; }
			 * 
			 * } else{
			 * 
			 * p.dX = (int)(Math.random()*2); p.dY = (int)(Math.random()*2);
			 * 
			 * System.out.println("--> NENHUM quadrante: "+ p.dX+" "+p.dY);
			 * 
			 * }
			 */

		}

		public void run() {

			List<PosicaoDesejada> posicoes = new ArrayList<PosicaoDesejada>();

			Iterator<Node> nodes = m_vis.items(NODES);

			while (nodes.hasNext()) {

				Node t = nodes.next();

				VisualItem visualItem = getVisualization().getVisualItem(NODES,
						t);

				int dX = (int) (Math.random() * 2);
				int dY = (int) (Math.random() * 2);

				PosicaoDesejada posicaoDesejada = new PosicaoDesejada(t, dX, dY);

				posicoes.add(posicaoDesejada);

				double x = visualItem.getX();
				double y = visualItem.getY();

				double xx = (50 + Math.random()
						* (display.getBounds().width - 100));
				double yy = (50 + Math.random()
						* (display.getBounds().height - 100));

				visualItem.setStartX(x);
				visualItem.setX(xx);
				visualItem.setEndX(xx);

				visualItem.setStartY(y);
				visualItem.setY(yy);
				visualItem.setEndY(yy);

			}

			boolean condicao = true;
			while (condicao) {

				// condicao = false;

				for (PosicaoDesejada p : posicoes) {

					VisualItem visualItem = getVisualization().getVisualItem(
							NODES, p.node);

					if (visualItem.isHover())
						continue;

					if (visualItem.getBounds().getX() < contorno
							|| visualItem.getBounds().getX() > display
									.getWidth()
									- contorno
							|| visualItem.getBounds().getY() < contorno
							|| visualItem.getBounds().getY() > display
									.getHeight()
									- contorno) {

						System.out.println("---> FOI PARA ZERO");
						novaPosicao(p);

					}

					int verticalhorizontal = (int) (Math.random() * 2);

					if (verticalhorizontal == 0
					// && visualItem.getBounds().getX() >=0 &&
					// visualItem.getBounds().getX() <=
					// display.getBounds().getWidth()
					) {

						System.out.println("Vetical: " + p);

						double x = visualItem.getX();

						// esquerda
						if (p.dX == 0) {
							System.out.print(" ESQUERDA\n");
							visualItem.setStartX(x);
							visualItem.setX(x - 1);
							visualItem.setEndX(x - 1);

						}
						// direira
						else {
							System.out.print(" DIREITA\n");
							visualItem.setStartX(x);
							visualItem.setX(x + 1);
							visualItem.setEndX(x + 1);
						}

					} else if (verticalhorizontal == 1
					// && visualItem.getBounds().getY() >=0 &&
					// visualItem.getBounds().getY() <=
					// display.getBounds().getHeight()
					) {

						System.out.println("Horizontal: " + p);

						double y = visualItem.getY();

						// cima
						if (p.dY == 0) {
							System.out.print(" CIMA\n");
							visualItem.setStartY(y);
							visualItem.setY(y - 1);
							visualItem.setEndY(y - 1);
						}
						// baixo
						else {
							System.out.print(" BAIXO\n");

							visualItem.setStartY(y);
							visualItem.setY(y + 1);
							visualItem.setEndY(y + 1);
						}
					} else {
						System.out.println("NAO SE MEXEU: " + p);
					}

					// Iterator<Node> nodes2 = m_vis.items(NODES);
					//    				
					// while(nodes2.hasNext()){
					//    					
					// Node tt = nodes2.next();
					//    					
					// VisualItem visualItem2 =
					// getVisualization().getVisualItem(NODES, tt);
					// visualItem2.setStroke(new BasicStroke(2));
					//    					
					// visualItem2.setStartFillColor(ChartColor.GREEN.getRGB());
					// visualItem2.setFillColor(ChartColor.GREEN.getRGB());
					// visualItem2.setEndFillColor(ChartColor.GREEN.getRGB());
					//    					
					// visualItem2.setStrokeColor(ChartColor.red.getRGB());
					//    					
					//    					
					// }

					// NodeItem node = (NodeItem) visualItem;
					//    				
					// node.setStroke(new BasicStroke(3)); //borda
					// node.setFillColor(ChartColor.GREEN.getRGB());
					// node.setStrokeColor(ChartColor.red.getRGB());

				}

				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				// System.out.println(display.getSize());

				/*
				 * VisualItem item;
				 * 
				 * Iterator<Node> nodes = m_vis.items(NODES);
				 * 
				 * while(nodes.hasNext()){
				 * 
				 * int dx = (int)(Math.random() *5); int dy =
				 * (int)(Math.random() * 5); System.out.println(dx + " - " +
				 * dy); Node t = nodes.next();
				 * 
				 * item = getVisualization().getVisualItem(NODES, t);
				 * 
				 * double x = item.getX(); double y = item.getY();
				 * 
				 * int x1 = (int)(Math.random() * 2); int y2 =
				 * (int)(Math.random() * 2);
				 * 
				 * if(x1 == 0){ item.setStartX(x1); item.setX(x1+dx);
				 * item.setEndX(x1+dx); } else{ item.setStartX(x1);
				 * item.setX(x1-dx); item.setEndX(x1-dx); } if(y2 == 0){
				 * item.setStartY(y); item.setY(y+dy); item.setEndY(y+dy); }
				 * else{ item.setStartY(y); item.setY(y-dy); item.setEndY(y-dy);
				 * } }
				 */
			}
		}

	}

}
