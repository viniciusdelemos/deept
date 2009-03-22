package model;

import gui.GUIViewUpdates;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import controller.ControllerDeepTwitter;
import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataColorAction;
import prefuse.action.assignment.DataSizeAction;
import prefuse.action.layout.Layout;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.activity.Activity;
import prefuse.controls.ControlAdapter;
import prefuse.controls.DragControl;
import prefuse.controls.NeighborHighlightControl;
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
import prefuse.data.io.DataIOException;
import prefuse.data.io.GraphMLReader;
import prefuse.data.tuple.DefaultTupleSet;
import prefuse.data.tuple.TupleSet;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.render.PolygonRenderer;
import prefuse.render.Renderer;
import prefuse.util.ColorLib;
import prefuse.util.GraphicsLib;
import prefuse.util.force.ForceSimulator;
import prefuse.visual.AggregateItem;
import prefuse.visual.AggregateTable;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualGraph;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;
import profusians.controls.CenterOnClickControl;
import profusians.zonemanager.ZoneManager;
import profusians.zonemanager.util.display.ZoneBorderDrawing;
import profusians.zonemanager.zone.shape.RectangularZoneShape;
import profusians.zonemanager.zone.shape.ZoneShape;
import twitter4j.User;

@SuppressWarnings("serial")
public class GraphicManager extends Display {
	
	public final static String GRAPH = "graph";
	public final static String NODES = "graph.nodes";
	public final static String GROUPS = "groups";
	public final static  String EDGES = "graph.edges";
	private final static String SELECTION_BOX = "selectionbox";	
	private final static String SELECTED_NODES = "selected";
    private Graph g;
    private int numUsers, groupId;
    private ForceDirectedLayout fdl;
    private Map<Integer, Node> nodesMap;    
    private boolean isTwitterUser, isHighQuality;	
    private String loggedUserId;
	private SocialNetwork socialNetwork; 
    private ZoneManager zoneManager;
    private VisualItem selectionBox;
	private int defaultEdgeColor, highlightEdgeColor, highlightArrowColor,
	highlightTextColor, defaultTextColor, mainUserStrokeColor, mainUserFillColor,
	selectedUserStrokeColor, selectedUserFillColor;
	private PanControl panControl;
	private TupleSet selectedNodes;
	private AggregateTable groupTable;
	
    public GraphicManager(boolean isTwitterUser)
    {    	
    	super(new Visualization());    	
    	    	
    	this.isTwitterUser = isTwitterUser;    	
    	nodesMap = new HashMap<Integer, Node>();
    	socialNetwork = new SocialNetwork();
    	//modo qualidade - setar para falso para melhor desempenho
    	isHighQuality = true;
    	numUsers = 0;
    	groupId = 1;
    	
    	g = new Graph(true);
    	g.addColumn("id", int.class);//Integer.class);
    	g.addColumn("idTwitter",int.class); //Integer.class);
    	g.addColumn("name", String.class);
    	g.addColumn("image", String.class);//URL.class);
    	g.addColumn("isOpen", boolean.class); 
    	g.addColumn("weight", float.class);
    	   	
    	// add visual data groups
    	VisualGraph vg = m_vis.addGraph(GRAPH, g);
    	m_vis.setInteractive(EDGES, null, false);

    	LabelRenderer nodeRenderer = new LabelRenderer("name", "image");
    	nodeRenderer.setVerticalAlignment(Constants.BOTTOM);       
    	nodeRenderer.setHorizontalPadding(0);
    	nodeRenderer.setVerticalPadding(0);        	
    	nodeRenderer.setMaxImageDimensions(100,100);
    	nodeRenderer.setRoundedCorner(8,8);

    	EdgeRenderer edgeRenderer = new EdgeRenderer(prefuse.Constants.EDGE_TYPE_LINE,prefuse.Constants.EDGE_ARROW_FORWARD);
    	edgeRenderer.setArrowHeadSize(20,20);   
    	edgeRenderer.setDefaultLineWidth(2);   	
    	//edgeRenderer.setHorizontalAlignment1(prefuse.Constants.CENTER);
    	//edgeRenderer.setHorizontalAlignment2(prefuse.Constants.CENTER);
    	
    	// draw aggregates as polygons with curved edges
        Renderer polyR = new PolygonRenderer(Constants.POLY_TYPE_CURVE);
        ((PolygonRenderer)polyR).setCurveSlack(0.15f);
    	
    	DefaultRendererFactory drf = new DefaultRendererFactory();
    	drf.setDefaultRenderer(nodeRenderer);
    	drf.setDefaultEdgeRenderer(edgeRenderer);
    	drf.add("ingroup('groups')", polyR);
 
    	m_vis.setRendererFactory(drf);
    	
    	//criacao de grupos
    	groupTable = m_vis.addAggregates(GROUPS);
    	groupTable.addColumn(VisualItem.POLYGON, float[].class);
    	groupTable.addColumn("id", int.class);    	
    	
    	//definindo cores padrao
    	defaultEdgeColor = ColorLib.color(ChartColor.gray);
    	highlightEdgeColor = ColorLib.color(ChartColor.red);
    	highlightArrowColor = ColorLib.color(ChartColor.blue);
    	
    	defaultTextColor = ColorLib.color(ChartColor.black);
    	highlightTextColor = ColorLib.color(ChartColor.blue);

    	mainUserStrokeColor = ColorLib.color(ChartColor.black);
    	mainUserFillColor = ColorLib.color(ChartColor.orange);
    	
    	selectedUserStrokeColor = ColorLib.color(ChartColor.black);
    	selectedUserFillColor = ColorLib.color(ChartColor.LIGHT_GREEN);
    	
//    	ColorAction nodeText = new ColorAction(NODES, VisualItem.TEXTCOLOR);
//    	nodeText.setDefaultColor(defaultTextColor);
//    	nodeText.add(VisualItem.HIGHLIGHT, highlightTextColor);    	
    	NodesColorAction nodeText = new NodesColorAction(NODES,VisualItem.TEXTCOLOR);
    	nodeText.setDefaultColor(defaultTextColor);

//    	ColorAction nodeStroke = new ColorAction(NODES, VisualItem.STROKECOLOR);
//    	nodeStroke.setDefaultColor(ColorLib.gray(100));
//    	nodeStroke.add(VisualItem.HIGHLIGHT, highlightEdgeColor); 
    	NodesColorAction nodeStroke = new NodesColorAction(NODES, VisualItem.STROKECOLOR);    	
    	
//    	ColorAction edgeStroke = new ColorAction(EDGES, VisualItem.STROKECOLOR);
//    	edgeStroke.setDefaultColor(defaultEdgeColor); 
//    	edgeStroke.add(VisualItem.HIGHLIGHT, highlightEdgeColor);
    	EdgesColorAction edgeStroke = new EdgesColorAction(EDGES,VisualItem.STROKECOLOR);

//    	ColorAction edgeArrow = new ColorAction(EDGES,VisualItem.FILLCOLOR);
//    	edgeArrow.setDefaultColor(defaultEdgeColor);
//    	edgeArrow.add(VisualItem.HIGHLIGHT, highlightArrowColor);
    	EdgesColorAction edgeArrow = new EdgesColorAction(EDGES,VisualItem.FILLCOLOR);
    	
    	ColorAction groupStroke = new ColorAction(GROUPS, VisualItem.STROKECOLOR);
        groupStroke.setDefaultColor(ColorLib.gray(200));
        groupStroke.add("_hover", ColorLib.color(ChartColor.red));
    	
    	int[] palette = new int[] {
                ColorLib.rgba(255,200,200,150),
                ColorLib.rgba(200,255,200,150),
                ColorLib.rgba(200,200,255,150)
            };
            ColorAction groups = new DataColorAction(GROUPS, "id",
                    Constants.NOMINAL, VisualItem.FILLCOLOR, palette);

    	//color actions
    	ActionList draw = new ActionList();        
    	draw.add(nodeText);
    	//draw.add(nodeStroke);
    	draw.add(edgeStroke);   
    	draw.add(edgeArrow);
    	draw.add(groupStroke);
    	draw.add(groups);

    	fdl = new WeightedForceDirectedLayout(GRAPH,false);
    	ForceSimulator fsim = fdl.getForceSimulator();
    	fsim.getForces()[2].setParameter(1,180.0f);    
    	fsim.getForces()[2].setParameter(0,9.99E-6f); 
    	
    	//zoneManager = new ZoneManager(m_vis,fsim);
    	    	
    	//acoes a serem executadas
    	ActionList layout = new ActionList(ActionList.INFINITY); 
    	layout.add(fdl);
    	layout.add(draw);    	
    	layout.add(new RepaintAction());
    	layout.add(new AggregateLayout(GROUPS));
    	m_vis.putAction("layout", layout);    	

    	setSize(800,600);
    	pan(super.getWidth()/2,super.getHeight()/2);    	
    	
    	setHighQuality(isHighQuality);
    	
    	//criando um novo focus group para guardar os nodos selecionados
        selectedNodes = new DefaultTupleSet(); 
        m_vis.addFocusGroup(SELECTED_NODES, selectedNodes);
        selectedNodes.addTupleSetListener(new TupleSetListener() {
        	public void tupleSetChanged(TupleSet ts, Tuple[] add, Tuple[] rem) {        		
        		//m_vis.cancel("layout");
        		for (int i = 0; i < add.length; i++) {
        			VisualItem item = (VisualItem) add[i];
        			item.setStroke(new BasicStroke(1.5f));
        			item.setStrokeColor(selectedUserStrokeColor);        				
        			item.setFillColor(selectedUserFillColor);        			
        		}
        		for (int i = 0; i < rem.length; i++) {
        			VisualItem item = (VisualItem) rem[i];
        			item.setStrokeColor(Color.TRANSLUCENT);
        			item.setFillColor(Color.TRANSLUCENT);
        		}
        		//m_vis.run("layout");
        	}
        });
        
        //criacao de um VisualItem, o retangulo que aparecer� ao selecionar nodos
        Table selectionBoxTable = new Table();
        selectionBoxTable.addColumn(VisualItem.POLYGON, float[].class);      
        selectionBoxTable.addRow();
        m_vis.add(SELECTION_BOX, selectionBoxTable);  
        
        selectionBox = (VisualItem) m_vis.getVisualGroup(SELECTION_BOX).tuples().next();
        selectionBox.set(VisualItem.POLYGON, new float[8]);
        selectionBox.setStroke(new BasicStroke(1.2f));
        selectionBox.setStrokeColor(ColorLib.color(ChartColor.DARK_CYAN));

        //criando um renderer para a selection box
        Renderer selectionBoxRenderer = new PolygonRenderer(Constants.POLY_TYPE_LINE);
        drf.add(new InGroupPredicate(SELECTION_BOX), selectionBoxRenderer);
    	
        panControl = new PanControl();
        panControl.setEnabled(true);
        
    	//addControlListener(new DragControl());
    	addControlListener(new AggregateDragControl());
    	addControlListener(new ZoomControl());        	      	
    	addControlListener(panControl);    	
    	//addControlListener(new NeighborHighlightControl()); 
    	addControlListener(new ZoomToFitControl());
    	addControlListener(new WheelZoomControl());
    	addControlListener(new ListenerAdapter(this));
    	//addPaintListener(new ZoneBorderDrawing(zoneManager));
    	//addControlListener(new CenterOnClickControl(1000));

    	//executar a��es associadas ao layout    	
    	m_vis.run("layout");    	
    }        
	
    public Node addNode(User u) {
    	socialNetwork.addUser(u);
    	Node newNode = g.addNode();
    	newNode.set("id", numUsers);
    	newNode.set("idTwitter", u.getId());
		newNode.set("name", u.getName());
		newNode.set("image", u.getProfileImageURL().toString());//sem tostring
		newNode.set("isOpen", false);
		nodesMap.put(u.getId(), newNode);
		
		if(numUsers==0)
		{
			VisualItem mainUser = getVisualization().getVisualItem(NODES, newNode);
			mainUser.setStroke(new BasicStroke(2));
			mainUser.setStrokeColor(mainUserStrokeColor);
			mainUser.setFillColor(mainUserFillColor);
		}		
		//System.out.println(u.getId()+"\t"+u.getProfileImageURL());
		numUsers++;
    	return newNode;    	
    }
    
    public void searchAndAddUserToNetwork(User u) {
    	User exists = getUser(u.getId());
    	VisualItem selectedNode;
    	
    	if(exists==null) {
    		Node n = addNode(u);
    		selectedNode = getVisualization().getVisualItem(NODES, n);    		
    	}			
		else
			selectedNode = getVisualization().getVisualItem(NODES, getNodeByTwitterId(u.getId()));
		
    	selectedNode.setStroke(new BasicStroke(1.2f));
    	selectedNode.setStrokeColor(ColorLib.color(ChartColor.blue));
    	selectedNode.setFillColor(ColorLib.color(ChartColor.DARK_CYAN));
		centerItem(selectedNode);
    }
    
    public void centerItem(VisualItem item) {
    	double scale = getScale();
		double displayX = getDisplayX();
		double displayY = getDisplayY();
		double nodeX = item.getX() * scale;
		double nodeY = item.getY() * scale;
		double screenWidth = getWidth();
		double screenHeight = getHeight();
		double moveX = (nodeX * -1) + ((screenWidth / 2) + displayX);
		double moveY = (nodeY * -1) + ((screenHeight / 2) + displayY);			
		animatePan(moveX, moveY, 1000);
    }
    
    public void addEdge(Node source, Node target) {
		g.addEdge(source, target);	
	}
    
    public void removeEdge(Node source, Node target) {
		Edge e = g.getEdge(source, target);
		g.removeEdge(e);
	}
    
    public void createGroup()
	{
		//ZoneShape zone = new RectangularZoneShape(x,y,w,h);
		//int i = zoneManager.createAndAddZone(zone);
    	if(selectedNodes.getTupleCount()<=0) {
    		ControllerDeepTwitter.showMessageDialog(null, "Nenhum usu�rio selecionado");
    		return;
    	}
    	AggregateItem group = (AggregateItem)groupTable.addItem();    	
    	group.setInt("id", groupId);    	
    	
    	groupId++;
    	
    	NodeItem item;
    	Iterator selected = selectedNodes.tuples();
    	while(selected.hasNext()) {    		
    		item = (NodeItem)selected.next();    		
    		group.addItem(item); 
    		item.setFixed(true);
    		
    		//alterar o peso de sua aresta para o grupo se distanciar dos demais nodos
    		Iterator edges = item.edges();
    		while(edges.hasNext()) {
    			EdgeItem edge = (EdgeItem)edges.next();
    			NodeItem source = edge.getSourceItem();
    			NodeItem target = edge.getTargetItem();
    			if(source == item) {
    				if(!group.containsItem(target)) {
        				edge.set("weight", 340);
        			}
    			}
    			if(target == item) {
    				if(!group.containsItem(source)) {
    					edge.set("weight", 340);
    				}
    			}    			
    		}
    	}
    	clearSelection();
	}
    
	public Node getNodeById(int id) {
		return g.getNode(id);
	}
	
	public Node getNodeByTwitterId(int id) {
		return nodesMap.get(id);			
	}
		
	public User getUser(int idTwitter) {
		return socialNetwork.getUser(idTwitter);
	}
	
	public SocialNetwork getSocialNetwork() {
    	return socialNetwork;
    }

	public ForceDirectedLayout getForceDirectedLayout() {
    	return fdl;
    }
	
	public Graph getGraph() {		
		return g;
	}
	
	public void setGraph(Graph newGraph) {
		//TODO
		//deletar todos os nodos do grafo atual
		//copiar todos os nodos de newGraph e inseri-los no grafo atual
	}
		
	public int getNumUsers() {
		return nodesMap.size();
	}
		
	public void setPanControlOn(boolean b) {
		panControl.setEnabled(b);
	}
	
	public void clearSelection()
	{
		m_vis.getFocusGroup(SELECTED_NODES).clear();
	}
	
	public void setChildrenVisible(NodeItem source, boolean visible)
	{
		Iterator neighbors = source.outNeighbors();
		Iterator edges = source.outEdges();
		while(neighbors.hasNext() || edges.hasNext())
		{
			NodeItem nextNode = (NodeItem)neighbors.next();			
			//if(nextNode.isVisible() == visible) break;
			nextNode.setVisible(visible);
			EdgeItem nextEdge = (EdgeItem) edges.next();			
			nextEdge.setVisible(visible);
			
			setChildrenVisible(nextNode,visible);
		}
	}
	
	private	class ListenerAdapter extends ControlAdapter{
		GraphicManager gManager;
		VisualItem clickedItem;
		JPopupMenu nodeMenu;
		//TupleSet selectedNodes;
		//vari�veis utilizadas para a manipula��o da caixa de sele��o
		Point2D mousePositionBegin, mousePositionEnd;
	    Rectangle2D rect;
	    //int x1,y1,x2,y2;
	    boolean controlWasPressed;
	    
		public ListenerAdapter(GraphicManager gManager)
		{
			this.gManager = gManager;			
			//selectedNodes = m_vis.getFocusGroup(SELECTED_NODES);
			mousePositionBegin = new Point2D.Float();
			mousePositionEnd = new Point2D.Float();
		    rect = new Rectangle2D.Float();
			createPopupMenus();				
		}
		
		public void createPopupMenus()
		{
			nodeMenu = new JPopupMenu(); 
    		
			JMenuItem friends = new JMenuItem("View Friends");
			JMenuItem updates = new JMenuItem("View Updates");
    		JMenuItem follow = new JMenuItem("Follow");//("Follow",'f') para adicionar atalho
    		JMenuItem leave = new JMenuItem("Leave");
    		JMenuItem block = new JMenuItem("Block"); 
    		JMenuItem followers = new JMenuItem("View Followers");
    		    		
    		friends.setActionCommand("user_friends");
    		updates.setActionCommand("user_updates");
    		follow.setActionCommand("user_follow");
    		leave.setActionCommand("user_leave");
    		block.setActionCommand("user_block");  
    		followers.setActionCommand("user_followers");
    		    		
    		nodeMenu.add(friends); //� necess�rio?
    		nodeMenu.add(updates);
    		nodeMenu.add(followers);
    		nodeMenu.addSeparator();
    		nodeMenu.add(follow);
    		nodeMenu.add(leave);
    		nodeMenu.add(block);
    		    		
    		//follow.setMnemonic(KeyEvent.VK_F);
    		friends.addActionListener(new ActionListener() {				
    			@Override
				public void actionPerformed(ActionEvent e) {    				
					new AddFriendsThread(gManager, (NodeItem)clickedItem).start();					
				}});
    		updates.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					new GUIViewUpdates(clickedItem,isTwitterUser);
				}    			
    		});
    		followers.addActionListener(new ActionListener(){
    			@Override
    			public void actionPerformed(ActionEvent arg0) {
    				new AddFollowersThread(gManager, (NodeItem)clickedItem).start();
    			}
    		});
    		follow.addActionListener(new ActionListener(){				
    			@Override
				public void actionPerformed(ActionEvent e) {    				
					new FollowUserThread(gManager, clickedItem, true).start();					
				}});
    		leave.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					new FollowUserThread(gManager, clickedItem, false).start();					
				}});
    		block.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.println("BLOCK USER");					
				}});
    		
    		//create popupMenu for 'background'
    		//JPopupMenu backgroundMenu = new JPopupMenu(); 
    		// ....
    		//addNode.addActionListener(this);
		}	
		
		public void itemClicked(VisualItem item, MouseEvent e) {
			clickedItem = item;System.out.println(item);
			if (!(item instanceof NodeItem)) return;
			
			//zoneManager.addItemToZone((NodeItem)item, 0);	
			
			//System.out.println(zoneManager.getZone(0).getNumberOfItems());
			
			if(SwingUtilities.isRightMouseButton(e))
			{				
				nodeMenu.show(e.getComponent(), e.getX(), e.getY());
				clickedItem.setFixed(true);
			}
			else if(SwingUtilities.isLeftMouseButton(e))
			{
				if(e.isControlDown())
				{					
					if(selectedNodes.containsTuple(clickedItem))
						selectedNodes.removeTuple(clickedItem);
					else
						selectedNodes.addTuple(clickedItem);					
					return;
				}			
				if(item.getBoolean("isOpen") == false)
				{
					item.setBoolean("isOpen", true);
					if(((Node)item).getOutDegree()>0)					
						setChildrenVisible((NodeItem)item, true);				
					else
						new AddFriendsThread(gManager, (NodeItem)item).start();   			            
				}
				else
				{
					item.setBoolean("isOpen", false);				
					setChildrenVisible((NodeItem)item,false);				
				}
			}
	    }
			
		//m�todos utilizados para desenhar a caixa de sele��o
		public void mousePressed(MouseEvent e)
		{	
			if(!SwingUtilities.isLeftMouseButton(e)) return;
			
			if(e.isControlDown()) { 
				controlWasPressed = true;
				setPanControlOn(false);
			}
//			else { if(rect.getHeight()==0 && rect.getWidth()==0) {
//				selectedNodes.clear();
//			}

			float[] box = (float[]) selectionBox.get(VisualItem.POLYGON);
			box[0] = box[1] = box[2] = box[3] = 
				box[4] = box[5] = box[6] = box[7] = 0;

			//gManager.setHighQuality(false);
			//setando coordenada x e y em rela��o ao frame
			mousePositionBegin.setLocation(e.getX(), e.getY());
			//setando coordenada absoluta em rela��o ao Display
			mousePositionBegin = gManager.getAbsoluteCoordinate(mousePositionBegin,null);
			
			selectionBox.setVisible(true);
		}
		
		public void mouseDragged(MouseEvent e)
		{
			if(!SwingUtilities.isLeftMouseButton(e)) return;
			if(!controlWasPressed) return;
			
			//atualizando coordenadas do mouse 
			mousePositionEnd.setLocation(e.getX(), e.getY());
			mousePositionEnd = gManager.getAbsoluteCoordinate(mousePositionEnd, null);
			
			int x1 = (int) mousePositionBegin.getX();
			int y1 = (int) mousePositionBegin.getY();
			int x2 = (int) mousePositionEnd.getX();
			int y2 = (int) mousePositionEnd.getY();

			//vetor responsavel por apenas desenhar a caixa na tela.
			float[] box = (float[]) selectionBox.get(VisualItem.POLYGON);
			box[0] = x1;
			box[1] = y1;
			box[2] = x2;
			box[3] = y1;
			box[4] = x2;
			box[5] = y2;
			box[6] = x1;
			box[7] = y2;

			if (x2 < x1){
				int temp = x2;
				x2 = x1;
				x1 = temp;
			}
			if (y2 < y1) {
				int temp = y2;
				y2 = y1;
				y1 = temp;
			}
			//definindo os limites do retangulo com o qual a intersec��o ser� verificada
			rect.setRect(x1,y1,x2-x1, y2-y1);			
			
			//limpa o grupo a cada movimento do mouse, para depois verificar a intersec��o novamente
			if (!e.isControlDown() && !panControl.isEnabled()) {
				selectedNodes.clear();
			}					
			
			//fazendo com que apenas os nodos sejam selecionados
			Iterator it = m_vis.getGroup(NODES).tuples();

			while (it.hasNext()) {
				VisualItem item = (VisualItem) it.next();           
				//verificando se os items interseccionam o retangulo
				if (item.isVisible() && item.getBounds().intersects(rect)){
					selectedNodes.addTuple(item);
				}				
			}	
			
			selectionBox.setValidated(false);
			gManager.repaint();
		}
		
		public void mouseReleased(MouseEvent e)
		{			
			if (!SwingUtilities.isLeftMouseButton(e)) return;
			if(!controlWasPressed) return;
			
			setPanControlOn(true);		
			controlWasPressed = false;
			selectionBox.setVisible(false);
			//createUsersGroup(x1,y1,x2-x1,y2-y1);
			//System.out.println(zoneManager.getNumberOfZones());
						
			gManager.getVisualization().repaint();
		}	
	}
	
	private class EdgesColorAction extends ColorAction {
		private String field;		
		
		public EdgesColorAction(String group, String field) { 
			super(group, field); 		 
			// set the action to all (not only to the visible) edges 
			//setFilterPredicate((Predicate)ExpressionParser.parse("TRUE")); 
			this.field = field;			
		} 
		 
		public int getColor(VisualItem item)
		{ 
			Color c = new Color(defaultEdgeColor);		 
			EdgeItem edge = (EdgeItem)item;
			
			if(field == VisualItem.STROKECOLOR)
			{
				if (edge.getSourceItem().isHover()) c = new Color(highlightEdgeColor);
			}
			if(field == VisualItem.FILLCOLOR)
			{
				if(edge.getSourceItem().isHover()) c = new Color(highlightArrowColor);		
				if(edge.getTargetItem().isHover()) c = new Color(highlightArrowColor);
			}					
			return c.getRGB();
		}
	}
	
	private class NodesColorAction extends ColorAction {
		private String field;		
		
		public NodesColorAction(String group, String field) { 
			super(group, field);		 
			this.field = field;			
		} 
		 
		public int getColor(VisualItem item)
		{ 
			Color c = new Color(defaultTextColor);
			try
			{			 
				if(field == VisualItem.TEXTCOLOR)
				{
					NodeItem node = (NodeItem)item;
					if (node.isHover()) c = new Color(highlightTextColor);
					else
					{				
						Iterator i = node.inEdges();
						while(i.hasNext())
						{
							EdgeItem edge = (EdgeItem)i.next();
							NodeItem nodeItem = edge.getSourceItem();
							if(nodeItem.isHover())
							{
								//colorindo os filhos
								c = new Color(highlightTextColor);
								break;
							}
						}
						i = node.outEdges();
						while(i.hasNext())
						{
							EdgeItem edge = (EdgeItem)i.next();
							NodeItem nodeItem = edge.getTargetItem();
							if(nodeItem.isHover())
							{
								//colorindo os pais com uma cor diferente
								c = new Color(highlightEdgeColor);
								break;
							}
						}
					}
				}
				

				//			if(node.getParent()!=null) //FAZER O IN E OUT EDGES
				//			{
				//				if(((NodeItem)(node.getParent())).isHover())
				//				{
				//					if(field == VisualItem.TEXTCOLOR) c = new Color(highlightTextColor);
				//				}
				//			}
				//			if(node.getTargetItem().isHover())
				//			{
				//				if(field == VisualItem.FILLCOLOR) c = new Color(highlightArrowColor);
				//			}
				//else ei.setVisible(false);	

			}
			catch(Exception e)
			{
				System.out.println("***** EXCEPTIONNNN");
				e.printStackTrace();
			}
			return c.getRGB();
		}
	}
}