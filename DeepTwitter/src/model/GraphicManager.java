package model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import model.threads.AddFollowersThread;
import model.threads.AddFriendsThread;
import model.threads.FollowUserThread;
import model.threads.StatusesTableThread;
import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataColorAction;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.controls.ControlAdapter;
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
import prefuse.data.tuple.DefaultTupleSet;
import prefuse.data.tuple.TupleSet;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.render.PolygonRenderer;
import prefuse.render.Renderer;
import prefuse.util.ColorLib;
import prefuse.util.force.DragForce;
import prefuse.util.force.ForceSimulator;
import prefuse.util.force.NBodyForce;
import prefuse.util.force.SpringForce;
import prefuse.visual.AggregateItem;
import prefuse.visual.AggregateTable;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualGraph;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;
import profusians.controls.GenericToolTipControl;
import twitter4j.ExtendedUser;
import twitter4j.TwitterException;
import twitter4j.User;
import controller.ControllerDeepTwitter;
import controller.StatusTab;

@SuppressWarnings("serial")
public class GraphicManager extends Display { 
	public final static String GRAPH = "graph";
	public final static String NODES = "graph.nodes";
	public final static String GROUPS = "groups";
	public final static  String EDGES = "graph.edges";		
	public final static String SELECTED_NODES = "selected";
	
	private int edgeColor, textColor, mainUserColor, friendsColor, followersColor,
	friendsAndFollowersColor, selectedItemColor, edgeType, nodeStrokeColor;	
	private final static String SELECTION_BOX = "selectionbox";
    private Graph g;
    private int numUsers, groupId;
    private ForceSimulator forceSimulator;
    private Map<Integer, Node> nodesMap;    
    private boolean isHighQuality, isTwitterUser;
    private SocialNetwork socialNetwork; 
    private VisualItem selectionBox;	
	private PanControl panControl;
	private GenericToolTipControl toolTipControl;
	private EdgeRenderer edgeRenderer;
	private TupleSet selectedNodes;
	private AggregateTable groupTable;
	private ControllerDeepTwitter controller;
	
    public GraphicManager()
    {    	
    	super(new Visualization());    	
    	
    	controller = ControllerDeepTwitter.getInstance();
    	isTwitterUser = controller.isTwitterUser();    	
    	
    	nodesMap = new HashMap<Integer, Node>();
    	socialNetwork = new SocialNetwork();
    	//modo qualidade - setar para falso para melhor desempenho
    	isHighQuality = true;
    	numUsers = 0;
    	groupId = 0;
    	
    	g = new Graph(true);
    	g.addColumn("id", int.class);//Integer.class);
    	g.addColumn("idTwitter",int.class); //Integer.class);
    	g.addColumn("name", String.class);
    	g.addColumn("image", String.class);//URL.class);
    	g.addColumn("isOpen", boolean.class); 
    	g.addColumn("isShowingFriends", boolean.class);
    	g.addColumn("isShowingFollowers", boolean.class);
    	g.addColumn("groupId", int.class);
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


    	edgeType = Constants.EDGE_TYPE_LINE;
    	edgeRenderer = new EdgeRenderer(edgeType,prefuse.Constants.EDGE_ARROW_FORWARD);
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
    	edgeColor = ChartColor.GRAY.getRGB();    	
    	textColor = ChartColor.BLACK.getRGB();
    	//highlightTextColor = ColorLib.color(ChartColor.blue);
    	mainUserColor = ChartColor.ORANGE.getRGB();    	
    	selectedItemColor = ChartColor.LIGHT_YELLOW.getRGB();
    	followersColor = ChartColor.LIGHT_RED.getRGB();
    	friendsColor = ChartColor.LIGHT_CYAN.getRGB();
    	friendsAndFollowersColor = ChartColor.LIGHT_GREEN.getRGB();
    	nodeStrokeColor = ChartColor.BLACK.getRGB();
    	
    	ColorAction nodeText = new ColorAction(NODES, VisualItem.TEXTCOLOR);
    	nodeText.setDefaultColor(textColor);
    	//nodeText.add(VisualItem.HIGHLIGHT, highlightTextColor);    	

    	ColorAction edgeStroke = new ColorAction(EDGES, VisualItem.STROKECOLOR);
    	edgeStroke.setDefaultColor(edgeColor); 

    	ColorAction edgeArrow = new ColorAction(EDGES,VisualItem.FILLCOLOR);
    	edgeArrow.setDefaultColor(edgeColor);
    	
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
    	
    	//ForceSimulator fsim = fdl.getForceSimulator();
    	//fsim.getForces()[2].setParameter(1,180.0f);    
    	//fsim.getForces()[2].setParameter(0,9.99E-6f);     	
    	//zoneManager = new ZoneManager(m_vis,fsim);
    	    	
    	ForceDirectedLayout fdl = new WeightedForceDirectedLayout(GRAPH,getForceSimulator(),false);
    	    	
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
        			item.setStroke(new BasicStroke(2f));
        			item.setStrokeColor(nodeStrokeColor);        				
        			item.setFillColor(selectedItemColor);
        		}
        		for (int i = 0; i < rem.length; i++) {
        			VisualItem item = (VisualItem) rem[i];
        			item.setStrokeColor(Color.TRANSLUCENT);
        			item.setFillColor(Color.TRANSLUCENT);
        		}
        		//m_vis.run("layout");
        	}
        });
        
        //criacao de um VisualItem, o retangulo que aparecerá ao selecionar nodos
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
    	addControlListener(new AggregateDragControl(this));
    	addControlListener(new ZoomControl());        	      	
    	addControlListener(panControl);    	
    	//addControlListener(new NeighborHighlightControl()); 
    	addControlListener(new ZoomToFitControl());
    	addControlListener(new WheelZoomControl());
    	addControlListener(new ListenerAdapter(this));
    	//addPaintListener(new ZoneBorderDrawing(zoneManager));
    	//addControlListener(new CenterOnClickControl(1000));
    	
    	String descriptions[] = { "Nome:", "Twitter ID:" };
    	String data[] = { "name", "idTwitter" };

    	toolTipControl = new GenericToolTipControl(descriptions,
    		data, 100);

    	addControlListener(toolTipControl);
    	toolTipControl.setEnabled(false);

    	//executar ações associadas ao layout    	
    	m_vis.run("layout");    	
    }        
	
    public Node addNode(User u) {    	  	
    	synchronized (m_vis) {
			socialNetwork.addUser(u);
			Node newNode = g.addNode();
			newNode.set("id", numUsers);
			newNode.set("idTwitter", u.getId());
			newNode.set("name", u.getName());
			newNode.set("image", u.getProfileImageURL().toString());//sem tostring
			newNode.set("isOpen", false);
			newNode.set("isShowingFriends", false);
			newNode.set("isShowingFollowers", false);
			newNode.set("groupId", -1);
			nodesMap.put(u.getId(), newNode);			
			if (numUsers == 0) {
				VisualItem mainUser = getVisualization().getVisualItem(NODES,
						newNode);
				mainUser.setStroke(new BasicStroke(2));
				mainUser.setStrokeColor(nodeStrokeColor);
				mainUser.setFillColor(mainUserColor);
			}
			numUsers++;
			return newNode;
		}    	
    }
    
    public void searchAndAddUserToNetwork(User u) {    	
    	//verifica se está na SocialNetwork
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
    
    public Edge addEdge(Node source, Node target) {
    	synchronized(m_vis) {
    		return g.addEdge(source, target);	
    	}
	}
    
    public void removeEdge(Node source, Node target) {
		Edge e = g.getEdge(source, target);
		g.removeEdge(e);
	}
    
    public void setEdgeType(boolean isCurved) {
    	if(isCurved)
    		edgeType = Constants.EDGE_TYPE_CURVE;
    	else
    		edgeType = Constants.EDGE_TYPE_LINE;
    	edgeRenderer.setEdgeType(edgeType);
    }
        
    public void addGroup()
	{
		if(selectedNodes.getTupleCount()<2) {
    		controller.showMessageDialog("Você deve selecionar dois ou mais usuários para criar um grupo.",MessageType.INFORMATION);
    		return;
    	}
    	AggregateItem group = (AggregateItem)groupTable.addItem();    	
    	group.setInt("id", groupId);    
    	   	
    	Iterator<NodeItem> selected = selectedNodes.tuples();    	
    	while(selected.hasNext())
    		addToGroup(selected.next(), group);    		
    	
    	clearSelection();
    	groupId++;
	}
    
    public void addToGroup(NodeItem n, AggregateItem group) {
    	int groupId = group.getInt("id");
    	group.addItem(n);
		n.setInt("groupId", groupId);
		//alterar o peso de sua aresta para o grupo se distanciar dos demais nodos
		Iterator<EdgeItem> edges = n.edges();
		while(edges.hasNext()) {
			EdgeItem edge = edges.next();
			NodeItem source = edge.getSourceItem();
			NodeItem target = edge.getTargetItem();
			if(source == n) {
				if(!group.containsItem(target))
					edge.set("weight", 340f);    				
				else
					edge.set("weight", 0f);
			}
			if(target == n) {
				if(!group.containsItem(source))
					edge.set("weight", 340f);
				else
					edge.set("weight", 0f);
			}			
		}
		
		Iterator<NodeItem> nodes = group.items();
		while(nodes.hasNext()) {
			NodeItem next = nodes.next();
			Edge e = g.addEdge((Node)next.getSourceTuple(), (Node)n.getSourceTuple());			
			//setando -1 para indicar que eh aresta invisível
			e.setFloat("weight", -1f); 		
			VisualItem edge = getVisualization().getVisualItem(EDGES,e);			
			edge.setStrokeColor(ChartColor.RED.getRGB());
			edge.setFillColor(Color.blue.getRGB());
			edge.setStroke(new BasicStroke(0f));
		}
    }
    
    public void removeFromGroup(NodeItem n) {
    	int groupId = n.getInt("groupId");
    	if(groupId<0) return;
    	
    	AggregateItem ai = (AggregateItem) groupTable.getItem(groupId);
    	ai.removeItem(n);
    	n.setInt("groupId", -1);
    	Iterator<EdgeItem> i = n.edges();
    	while(i.hasNext()) {
    		EdgeItem edge = i.next();
    		edge.setFloat("weight", 0f);
    	}
    }
    
    public void removeGroup(AggregateItem ai) {
    	Iterator<NodeItem> i = ai.items();
    	while(i.hasNext()) {
    		NodeItem n = i.next();
    		removeFromGroup(n);
    	}
    	groupTable.removeRow(ai.getInt("id"));    
    }
    
    public AggregateTable getGroups() {
    	return groupTable;
    }
    
	public Node getNodeById(int id) {
		return g.getNode(id);
	}
	
	public Node getNodeByTwitterId(int id) {
		return nodesMap.get(id);			
	}
	
	public String getUserName(int id) {
		Node n = nodesMap.get(id);
		return n.getString("name");
	}
	
//	public VisualItem getLoggedUser() {
//		int id = Integer.parseInt(ControllerDeepTwitter.getLoggedUserId());
//		Node n = getNodeByTwitterId(id);
//		return getVisualization().getVisualItem(NODES, n);
//	}
		
	public User getUser(int idTwitter) {
		return socialNetwork.getUser(idTwitter);
	}
	
	public int getEdge(int source, int target) {
		return g.getEdge(source, target);
	}
	
	public SocialNetwork getSocialNetwork() {
    	return socialNetwork;
    }

	public ForceSimulator getForceSimulator() {		
		if(forceSimulator != null) return forceSimulator;
		
		forceSimulator = new ForceSimulator();

    	float gravConstant = -1f;
    	float minDistance = -1f;
    	float theta = 0.9f;
    	
    	float drag = 0.007f;
    	float springCoeff = 9.99E-6f;
    	float defaultLength = 180f;    	
    	
    	forceSimulator.addForce(new NBodyForce(gravConstant, minDistance, theta));
    	forceSimulator.addForce(new DragForce(drag));
    	forceSimulator.addForce(new SpringForce(springCoeff, defaultLength));
		
		return forceSimulator;
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
	
	public boolean isTwitterUser() {
		return isTwitterUser;
	}
		
	public void setPanControlOn(boolean b) {
		panControl.setEnabled(b);		
	}
	
	public void setToolTipControlOn(boolean b) {
		toolTipControl.setEnabled(b);		
	}
	
	public void clearSelection()
	{
		m_vis.getFocusGroup(SELECTED_NODES).clear();
	}
	
	public TupleSet getTupleSet(String name) {
		return m_vis.getFocusGroup(name);
	}
	
	public void setChildrenVisible(NodeItem source, boolean visible)
	{
		try{
			Iterator<EdgeItem> outEdges = source.outEdges();
			Iterator<EdgeItem> inEdges = source.inEdges();
			while(outEdges.hasNext()) {
				EdgeItem nextEdge = outEdges.next();
				NodeItem nextNode = nextEdge.getTargetItem();			
				if(nextNode.getDegree()>1) continue;
				nextEdge.setVisible(visible);
				nextNode.setVisible(visible);
			}
			while(inEdges.hasNext()) {
				EdgeItem nextEdge = outEdges.next();
				NodeItem nextNode = nextEdge.getSourceItem();
				if(nextNode.getDegree()>1) continue;
				nextEdge.setVisible(visible);
				nextNode.setVisible(visible);
			}
		}
		catch(Exception e) {
			System.out.println("$$$ Exception! $$$");
			e.printStackTrace();
		}
	}
	
	private	class ListenerAdapter extends ControlAdapter{
		GraphicManager gManager;
		VisualItem clickedItem;
		JPopupMenu nodeMenu;
		//variáveis utilizadas para a manipulação da caixa de seleção
		Point2D mousePositionBegin, mousePositionEnd;
	    Rectangle2D rect;
	    boolean controlWasPressed;
	    
		public ListenerAdapter(GraphicManager gManager)
		{
			this.gManager = gManager;			
			mousePositionBegin = new Point2D.Float();
			mousePositionEnd = new Point2D.Float();
		    rect = new Rectangle2D.Float();							
		}
		
		public void createPopupMenu(VisualItem item)
		{
			final String clickedUserName = getUser(item.getInt("idTwitter")).getScreenName();
			nodeMenu = new JPopupMenu();
			
			JMenuItem friends = new JMenuItem("Ver Amigos");
			JMenuItem updates = new JMenuItem("Ver Tweets");
			JMenuItem favorites = new JMenuItem("Ver Favoritos");			
    		JMenuItem follow = new JMenuItem("Seguir");//("Follow",'f') para adicionar atalho
    		JMenuItem leave = new JMenuItem("Deixar");
    		JMenuItem sendReply = new JMenuItem("@"+clickedUserName);
    		JMenuItem sendMessage = new JMenuItem("Enviar mensagem");
    		JMenuItem openURL = new JMenuItem("Abrir URL");
    		JMenuItem block = new JMenuItem("Bloquear"); 
    		JMenuItem followers = new JMenuItem("Ver Seguidores");
    		JMenuItem removeFromGroup = new JMenuItem("Remover do Grupo");
    		 
    		Integer loggedUserId = Integer.parseInt(controller.getLoggedUserId());
    		Node mainUserNode = getNodeByTwitterId(loggedUserId);			
			Node clickedNode = (Node)item.getSourceTuple();
			
			nodeMenu.add(updates);
    		nodeMenu.add(friends); //é necessário?
    		nodeMenu.add(followers);
    		nodeMenu.add(favorites);
    		
    		if(!clickedNode.get("idTwitter").equals(loggedUserId)) { 
    			nodeMenu.addSeparator();
    			nodeMenu.add(sendReply);
    			nodeMenu.add(sendMessage);
    			//verifica se estou seguindo o usuário destino
    			int edge = g.getEdge(mainUserNode.getInt("id"), clickedNode.getInt("id"));
    			if(edge == -1) 
    				nodeMenu.add(follow);    			
    			else 
    				nodeMenu.add(leave);
    			
    			//verifica se estou sendo seguido
    			//edge = g.getEdge(clickedNode.getInt("id"),mainUserNode.getInt("id"), );
    			
    			//TODO inserir algo para mostrar que bloqueou e remover amizade entre os 2!    			
    			nodeMenu.add(block);
    		}
    		if(clickedNode.getInt("groupId")>=0)
    			nodeMenu.add(removeFromGroup);
    		nodeMenu.addSeparator();
    		nodeMenu.add(openURL);
			
    		if(!controller.isTwitterUser()) {
				followers.setEnabled(false);
				follow.setEnabled(false);
				leave.setEnabled(false);
				block.setEnabled(false);
			}   		
			
    		//follow.setMnemonic(KeyEvent.VK_F);
    		friends.addActionListener(new ActionListener() {				
    			@Override
				public void actionPerformed(ActionEvent e) {    				
					new AddFriendsThread(gManager, (NodeItem)clickedItem);					
				}});
    		updates.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					StatusTab tab = controller.getStatusTabManager().getTab(StatusesType.UPDATES);
					if(isTwitterUser && clickedItem.getString("idTwitter").equals(controller.getLoggedUserId()))
						tab.setPanelContent(new StatusesTableThread(StatusesType.UPDATES));
					else
						tab.setPanelContent(new StatusesTableThread(StatusesType.UPDATES,clickedItem.getString("idTwitter")));
					controller.selectTab(1);
				}    			
    		});
    		followers.addActionListener(new ActionListener(){
    			@Override
    			public void actionPerformed(ActionEvent arg0) {
    				new AddFollowersThread(gManager, (NodeItem)clickedItem);
    			}
    		});
    		favorites.addActionListener(new ActionListener(){
    			@Override
    			public void actionPerformed(ActionEvent arg0) {
    				StatusTab tab = controller.getStatusTabManager().getTab(StatusesType.FAVORITES);
					if(isTwitterUser && clickedItem.getString("idTwitter").equals(controller.getLoggedUserId()))
						tab.setPanelContent(new StatusesTableThread(StatusesType.FAVORITES));
					else
						tab.setPanelContent(new StatusesTableThread(StatusesType.FAVORITES,clickedItem.getString("idTwitter")));
					controller.selectTab(3);
    			}
    		});
    		follow.addActionListener(new ActionListener(){				
    			@Override
				public void actionPerformed(ActionEvent e) {    				
					new FollowUserThread(gManager, clickedItem, true);					
				}});
    		leave.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					new FollowUserThread(gManager, clickedItem, false);					
				}});
    		sendReply.addActionListener(new ActionListener(){
    			@Override
				public void actionPerformed(ActionEvent e) {
    				controller.openGUINewUpdateWindow(clickedUserName,StatusesType.REPLIES);					
    		}});
    		sendMessage.addActionListener(new ActionListener(){
    			@Override
				public void actionPerformed(ActionEvent e) {
    				controller.openGUINewUpdateWindow(clickedUserName,StatusesType.DIRECT_MESSAGES);
    		}});
    		block.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.println("BLOCK USER");					
				}});
    		openURL.addActionListener(new ActionListener(){
    			@Override
    			public void actionPerformed(ActionEvent e) {
    				new URLLinkAction("http://www.twitter.com/"+clickedUserName);					
    			}});
    		removeFromGroup.addActionListener(new ActionListener(){
    			@Override
    			public void actionPerformed(ActionEvent e) {
    				removeFromGroup((NodeItem)clickedItem);				
    			}});
    		//create popupMenu for 'background'
    		//JPopupMenu backgroundMenu = new JPopupMenu(); 
    		// ....
    		//addNode.addActionListener(this);
		}	
		
		public void itemClicked(VisualItem item, MouseEvent e) {
			clickedItem = item;
			//System.out.println(item);
			if (!(item instanceof NodeItem)) return;
			
			//zoneManager.addItemToZone((NodeItem)item, 0);				
			
			if(SwingUtilities.isRightMouseButton(e))
			{				
				createPopupMenu(item);
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
					if(item.getBoolean("isShowingFriends") == false) 
						new AddFriendsThread(gManager, (NodeItem)item);
					else
						setChildrenVisible((NodeItem)item, true);	
					item.setBoolean("isOpen", true);
				}
				else
				{							
					setChildrenVisible((NodeItem)item,false);
					item.setBoolean("isOpen", false);		
				}
			}
	    }
			
		//métodos utilizados para desenhar a caixa de seleção
		public void mousePressed(MouseEvent e)
		{		
			if(!SwingUtilities.isLeftMouseButton(e)) return;
			
			if(e.isControlDown()) { 
				controlWasPressed = true;
				setPanControlOn(false);
			}

			float[] box = (float[]) selectionBox.get(VisualItem.POLYGON);
			box[0] = box[1] = box[2] = box[3] = 
				box[4] = box[5] = box[6] = box[7] = 0;

			//setando coordenada x e y em relação ao frame
			mousePositionBegin.setLocation(e.getX(), e.getY());
			//setando coordenada absoluta em relação ao Display
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
			//definindo os limites do retangulo com o qual a intersecção será verificada
			rect.setRect(x1,y1,x2-x1, y2-y1);			
			
			//limpa o grupo a cada movimento do mouse, para depois verificar a intersecção novamente
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
						
			gManager.getVisualization().repaint();
		}	
	}
	//métodos de get/set para as cores
	public int getEdgeColor() {
		return edgeColor;
	}

	public void setEdgeColor(int edgeColor) {
		this.edgeColor = edgeColor;
	}

	public int getTextColor() {
		return textColor;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public int getMainUserColor() {
		return mainUserColor;
	}

	public void setMainUserColor(int mainUserColor) {
		this.mainUserColor = mainUserColor;
	}

	public int getFriendsColor() {
		return friendsColor;
	}

	public void setFriendsColor(int friendsColor) {
		this.friendsColor = friendsColor;
	}

	public int getFollowersColor() {
		return followersColor;
	}

	public void setFollowersColor(int followersColor) {
		this.followersColor = followersColor;
	}

	public int getFriendsAndFollowersColor() {
		return friendsAndFollowersColor;
	}

	public void setFriendsAndFollowersColor(int friendsAndFollowersColor) {
		this.friendsAndFollowersColor = friendsAndFollowersColor;
	}

	public int getSelectedItemColor() {
		return selectedItemColor;
	}

	public void setSelectedItemColor(int selectedItemColor) {
		this.selectedItemColor = selectedItemColor;
	}

	public int getNodeStrokeColor() {
		return nodeStrokeColor;
	}

	public void setNodeStrokeColor(int nodeStrokeColor) {
		this.nodeStrokeColor = nodeStrokeColor;
	}
}
