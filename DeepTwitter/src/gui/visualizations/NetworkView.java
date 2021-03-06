package gui.visualizations;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import model.ChartColor;
import model.SocialNetwork;
import model.StatusTab;
import model.StatusesType;
import model.URLLinkAction;
import model.extensions.AggregateDragControl;
import model.extensions.AggregateLayout;
import model.extensions.WeightedForceDirectedLayout;
import model.threads.AddFollowersThread;
import model.threads.AddFriendsThread;
import model.threads.BlockUserThread;
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
import prefuse.data.query.SearchQueryBinding;
import prefuse.data.search.PrefixSearchTupleSet;
import prefuse.data.search.SearchTupleSet;
import prefuse.data.tuple.DefaultTupleSet;
import prefuse.data.tuple.TupleSet;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.render.PolygonRenderer;
import prefuse.render.Renderer;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.util.force.DragForce;
import prefuse.util.force.ForceSimulator;
import prefuse.util.force.NBodyForce;
import prefuse.util.force.SpringForce;
import prefuse.util.ui.JSearchPanel;
import prefuse.visual.AggregateItem;
import prefuse.visual.AggregateTable;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualGraph;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;
import profusians.controls.CenterOnClickControl;
import profusians.controls.GenericToolTipControl;
import twitter4j.TwitterException;
import twitter4j.User;
import controller.ControllerDeepTwitter;
import controller.GroupManager;


@SuppressWarnings("serial")
public class NetworkView extends Display { 
	public final static String GRAPH = "graph";
	public final static String NODES = "graph.nodes";
	public final static String GROUPS = "groups";
	public final static String EDGES = "graph.edges";		
	public final static String SELECTED_NODES = "selected";
	
	private int edgeColor, textColor, mainUserColor, searchResultColor, friendsColor, followersColor,
	friendsAndFollowersColor, selectedItemColor, edgeType, nodeStrokeColor;	
	private final static String SELECTION_BOX = "selectionbox";
    private Graph g;
    private int numUsers, groupId;
    private ForceSimulator forceSimulator;
    
    //(ATUALIZA��O)
    //de Integer para Long
    private Map<Long, Node> nodesMap;    
    private boolean isHighQuality, isTwitterUser;
    private SocialNetwork socialNetwork; 
    private VisualItem selectionBox;	
	private PanControl panControl;
	private CenterOnClickControl centerUserControl;
	private GenericToolTipControl toolTipControl;
	private EdgeRenderer edgeRenderer;
	private TupleSet selectedNodes;
	private ControllerDeepTwitter controller;
	private float weight_value;
	private GroupManager groupManager;
	private LabelRenderer nodeRenderer;
	private SearchTupleSet searchResultTuples;
	
    public NetworkView()
    {    	
    	super(new Visualization());   	
    	
    	controller = ControllerDeepTwitter.getInstance();
    	isTwitterUser = controller.isTwitterUser();    	
    	
    	nodesMap = new HashMap<Long, Node>();
    	
    	socialNetwork = new SocialNetwork();
    	
    	try {
			//socialNetwork.addBlockedUsers(controller.getTwitter().getBlockingUsersIDs().getIDs());
    		socialNetwork.addBlockedUsers(controller.getTwitter().getBlocksIDs().getIDs());
		} catch (Exception e) {}
		//mudei de TwitterException para Exception pq nesse local ocorriam erros na op��o "Encontrar outro usu�rio"
    	
		isHighQuality = true;
    	numUsers = 0;
    	groupId = 0;
    	weight_value = 370f;
    	
    	g = new Graph(true);
    	
    	g.addColumn("id", int.class);
    	g.addColumn("idTwitter",int.class);
    	g.addColumn("screenName", String.class);
    	g.addColumn("name", String.class);
    	g.addColumn("image", String.class);    	
    	g.addColumn("location", String.class);
    	g.addColumn("description", String.class);
    	g.addColumn("protected", boolean.class);
    	g.addColumn("friendsCount", int.class);
    	g.addColumn("followersCount", int.class);
    	g.addColumn("statusesCount", int.class);
    	g.addColumn("favoritesCount", int.class);    	
    	g.addColumn("latestStatus", String.class);
    	g.addColumn("isOpen", boolean.class); 
    	g.addColumn("isShowingFriends", boolean.class);
    	g.addColumn("isShowingFollowers", boolean.class);
    	g.addColumn("groupId", int.class);
    	g.addColumn("weight", float.class);
    	g.addColumn("friendsNextCursor", long.class);
    	g.addColumn("followersNextCursor", long.class);
    	g.addColumn("addedFriends", int.class);
    	g.addColumn("addedFollowers", int.class);
    	   	
    	// add visual data groups
    	VisualGraph vg = m_vis.addGraph(GRAPH, g);
    	m_vis.setInteractive(EDGES, null, false);

    	nodeRenderer = new LabelRenderer("name", "image");
    	nodeRenderer.setVerticalAlignment(Constants.CENTER);       
    	nodeRenderer.setHorizontalPadding(1);
    	nodeRenderer.setVerticalPadding(0);        	
    	nodeRenderer.setMaxImageDimensions(48,48);
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
    	
    	groupManager = new GroupManager(this);

    	m_vis.setRendererFactory(drf);
    	   	
    	//definindo cores padrao
    	edgeColor = ChartColor.GRAY.getRGB();    	
    	textColor = ChartColor.BLACK.getRGB();
    	//highlightTextColor = ColorLib.color(ChartColor.blue);
    	searchResultColor = ColorLib.color(ChartColor.LIGHT_BLUE.brighter());
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
    	edgeStroke.add("weight==-1", ChartColor.TRANSLUCENT);

    	ColorAction edgeArrow = new ColorAction(EDGES,VisualItem.FILLCOLOR);
    	edgeArrow.setDefaultColor(edgeColor);
    	edgeArrow.add("weight==-1", ChartColor.TRANSLUCENT);
    	
    	ColorAction groupStroke = new ColorAction(GROUPS, VisualItem.STROKECOLOR);
        groupStroke.setDefaultColor(ColorLib.gray(200));
        groupStroke.add("_hover", ColorLib.color(ChartColor.red));
        
    	int[] palette = new int[] {
                ColorLib.rgba(255,200,200,150),
                ColorLib.rgba(200,255,200,150),
                ColorLib.rgba(200,200,255,150)
            };
            ColorAction groups = new DataColorAction(GROUPS, "id",
                    Constants.ORDINAL, VisualItem.FILLCOLOR, palette);

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

    	setSize(700,600);
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
        centerUserControl = new CenterOnClickControl(1000);
        centerUserControl.setEnabled(false);
        
		addControlListener(new AggregateDragControl(this));
    	addControlListener(new ZoomControl());        	      	
    	addControlListener(panControl);    	
    	//addControlListener(new NeighborHighlightControl()); 
    	addControlListener(new ZoomToFitControl());
    	addControlListener(new WheelZoomControl());
    	addControlListener(new ListenerAdapter(this));
    	addControlListener(centerUserControl);
    	
    	String descriptions[] = { "Username:", "Last Tweet: ", "Description:", "Location:", "Friends:", "Followers:", "Tweets:" };
    	String data[] = { "screenName", "latestStatus", "description", "location", "friendsCount", "followersCount", "statusesCount" };

    	toolTipControl = new GenericToolTipControl(descriptions,data,200);    	
    	addControlListener(toolTipControl);
    	
    	searchResultTuples = new PrefixSearchTupleSet();
    	m_vis.addFocusGroup(Visualization.SEARCH_ITEMS, searchResultTuples);
    	
    	searchResultTuples.addTupleSetListener(new TupleSetListener(){
			@Override
			public void tupleSetChanged(TupleSet tset, Tuple[] added,
					Tuple[] removed) {
				for(Tuple r : removed) {
					NodeItem n = (NodeItem)m_vis.getVisualItem(NODES, r);
					getColorsBack(n);
				}
				for(Tuple a : added) {
					VisualItem n = m_vis.getVisualItem(NODES, a);
					n.setFillColor(searchResultColor);
					n.setStroke(new BasicStroke(2f));
					n.setStrokeColor(ChartColor.BLACK.getRGB());
				}
			}    		
    	});
    	
    	JToolBar mainToolBar = controller.getMainToolBar();
    	SearchQueryBinding nameQuery = new SearchQueryBinding((Table) m_vis
    			.getGroup(NODES), "name", (SearchTupleSet) m_vis
    			.getGroup(Visualization.SEARCH_ITEMS));    	
    	JSearchPanel searchPanel = nameQuery.createSearchPanel();
    	//searchPanel.setShowResultCount(true);
    	searchPanel.setMaximumSize(new Dimension(200, 20));
    	searchPanel.setFont(FontLib.getFont("Tahoma", Font.BOLD, 12));
    	searchPanel.setLabelText("Search by:");
    	searchPanel.setShowBorder(false);
    	searchPanel.setBackground(mainToolBar.getBackground());
    	mainToolBar.add(searchPanel,mainToolBar.getComponentCount()-4);
    	mainToolBar.add(Box.createHorizontalGlue(),mainToolBar.getComponentCount()-4);
    	
    	//executar a��es associadas ao layout    	
    	m_vis.run("layout");    
    }
    
    class TestLoadUserImage extends Thread {
		private User u;
		private Node newNode;
		
		public TestLoadUserImage(User u, Node newNode) {
			this.u = u;
			this.newNode = newNode;
			start();
		}
		
		public void run() {			
			if(controller.getUserImageMap().containsKey(u.getId())) 
				newNode.set("image",(ImageIcon)controller.getUserImageMap().get(u.getId()));				
			else {			
				ImageIcon userPicture = new ImageIcon(u.getProfileImageURL());											
				newNode.set("image",userPicture);				
				controller.getUserImageMap().put(u.getId(), userPicture);
			}			
		}
	}
	
    public Node addNode(User u) {    	  	
    	synchronized (m_vis) {
			socialNetwork.addUser(u);
			Node newNode = g.addNode();
			newNode.set("id", numUsers);
			newNode.set("idTwitter", u.getId());
			newNode.set("screenName", u.getScreenName());
			newNode.set("name", u.getName());
			
			//new TestLoadUserImage(u,newNode);
			newNode.set("image", u.getProfileImageURL().toString());
			
			//(ATUALIZA��O)
			//de u.getStatusText() para u.getStatus().getText()
			try{
				newNode.set("latestStatus",u.getStatus().getText());
			}
			catch(Exception e){}
			
			newNode.set("location", u.getLocation());
			newNode.set("description", u.getDescription());
			newNode.set("protected", u.isProtected());
			newNode.set("friendsCount", u.getFriendsCount());
			newNode.set("followersCount", u.getFollowersCount());
			newNode.set("statusesCount", u.getStatusesCount());
			newNode.set("favoritesCount", u.getFavouritesCount());			
			
			newNode.set("isOpen", false);
			newNode.set("isShowingFriends", false);
			newNode.set("isShowingFollowers", false);
			newNode.set("groupId", -1);
			
			newNode.set("friendsNextCursor", -1);
			newNode.set("followersNextCursor", -1);
			newNode.set("addedFriends", 0);
			newNode.set("addedFollowers", 0);
			
			nodesMap.put(u.getId(), newNode);			
			if (numUsers == 0) {
				//nodeRenderer.getImageFactory().preloadImages(m_vis.items(NODES),"image");
				VisualItem mainUser = getVisualization().getVisualItem(NODES,newNode);
				mainUser.setStroke(new BasicStroke(2));
				mainUser.setStrokeColor(nodeStrokeColor);
				mainUser.setFillColor(mainUserColor);
			}
			searchResultTuples.index(m_vis.getVisualItem(GRAPH, newNode), "name");
			searchResultTuples.index(m_vis.getVisualItem(GRAPH, newNode), "screenName");
			searchResultTuples.index(m_vis.getVisualItem(GRAPH, newNode), "location");
			searchResultTuples.index(m_vis.getVisualItem(GRAPH, newNode), "description");
			numUsers++;			
			return newNode;
		}
    }
    
    public void removeNode(VisualItem item) {
    	synchronized(m_vis) {
    		//(ATUALIZA��O)
    		//mudan�a de item.getInt para item.getLong
    		socialNetwork.removeUser(item.getLong("idTwitter"));
    		g.removeNode((Node)item.getSourceTuple());
    	}
    }
    
    public LabelRenderer getRenderer() {
    	return nodeRenderer;
    }
    
    public void searchAndAddUserToNetwork(User u) {    	
    	//verifica se est� na SocialNetwork
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
    	removeEdge(e);
	}
    
    public void removeEdge(Edge e) {
    	synchronized(m_vis) {
    		g.removeEdge(e);    
    	}
    }
    
    public void removeEdge(int e) {
    	synchronized(m_vis) {
    		g.removeEdge(e);    
    	}
    }
    
    public void setEdgeType(boolean isCurved) {
    	if(isCurved)
    		edgeType = Constants.EDGE_TYPE_CURVE;
    	else
    		edgeType = Constants.EDGE_TYPE_LINE;
    	edgeRenderer.setEdgeType(edgeType);
    }
    
    public void setEdgeType(int value) {
    	if(value>=0 && value<=3)
    		edgeType = value;
    	edgeRenderer.setEdgeType(edgeType);
    }
    
    public int getEdgeType() {
    	return edgeType;
    }
        
    public GroupManager getGroupManager() {
    	return groupManager;
    }
    
    public AggregateTable getGroups() {
    	return groupManager.getGroups();
    }
    
	public Node getNodeById(int id) {
		return g.getNode(id);
	}
	
	//(ATUALIZA��O)
	//mudan�a de int para long
	public Node getNodeByTwitterId(long id) {
		return nodesMap.get(id);			
	}
	
	public Node[] getNodes() {		
		Node[] nodeArray = new Node[g.getNodeCount()];
		for(int i=0; i<g.getNodeCount(); i++)
			nodeArray[i] = g.getNode(i);
		return nodeArray;
	}
	
	public VisualItem getNodeItem(Node node) {
		return m_vis.getVisualItem(NODES, node);
	}
	
	//(ATUALIZA��O)
	//mudan�a de int para long
	public String getUserName(long id) {
		Node n = nodesMap.get(id);
		return n.getString("name");
	}
	
	//(ATUALIZA��O)
	//mudan�a de int para long
	public User getUser(long idTwitter) {
		return socialNetwork.getUser(idTwitter);
	}
	
	public int getEdge(int source, int target) {
		return g.getEdge(source, target);
	}
	
	public Edge getEdge(int id) {
		return g.getEdge(id);
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
	
	public void setCenterUserControlOn(boolean b) {
		centerUserControl.setEnabled(b);		
	}
	
	public void setToolTipControlOn(boolean b) {
		toolTipControl.setEnabled(b);		
	}
	
	public void clearSelection()
	{
		m_vis.getFocusGroup(SELECTED_NODES).clear();
	}
	
	public TupleSet getTupleSet(String name) {
		return m_vis.getGroup(name);
	}
	
	public void setChildrenVisible(NodeItem source, boolean visible)
	{
		synchronized(m_vis) {
			try{
				Iterator<EdgeItem> outEdges = source.outEdges();			
				while(outEdges.hasNext()) {
					EdgeItem nextEdge = outEdges.next();
					NodeItem nextNode = nextEdge.getTargetItem();			
					if(nextNode.getDegree()>1) continue;				
					nextNode.setVisible(visible);
					nextEdge.setVisible(visible);
				}
				Iterator<EdgeItem> inEdges = source.inEdges();
				while(inEdges.hasNext()) {
					EdgeItem nextEdge = inEdges.next();
					NodeItem nextNode = nextEdge.getSourceItem();					
					if(nextNode.getDegree()>1) {						
						if(nextNode.getDegree()==2) {
							int edgeId = getEdge(source.getInt("id"), nextNode.getInt("id"));
							if(edgeId == -1) 
								continue;
							else 
								m_vis.getVisualItem(EDGES, getEdge(edgeId)).setVisible(visible);
						}						
						else
							continue;				
					}
					nextNode.setVisible(visible);
					nextEdge.setVisible(visible);
				}
			}
			catch(Exception e) {
				System.out.println("$$$ Exception! $$$");
				e.printStackTrace();
			}
		}
	}
	
	public void getColorsBack(NodeItem node) {
		if(selectedNodes.containsTuple(node)) {
			node.setStroke(new BasicStroke(2f));
			node.setFillColor(getSelectedItemColor());
			node.setStrokeColor(getNodeStrokeColor());
		}
		else if(searchResultTuples.containsTuple(node)) {
			node.setFillColor(searchResultColor);
			node.setStroke(new BasicStroke(2f));
			node.setStrokeColor(ChartColor.BLACK.getRGB());
		}
		else if(node.getInt("id")==0) {
			node.setStroke(new BasicStroke(2));
			node.setStrokeColor(getNodeStrokeColor());
			node.setFillColor(getMainUserColor());
		}
		else {
			node.setFillColor(ChartColor.TRANSLUCENT);
			node.setStrokeColor(ChartColor.TRANSLUCENT);
		}
	}
	
	private	class ListenerAdapter extends ControlAdapter{
		NetworkView networkView;
		VisualItem clickedItem;
		JPopupMenu popupMenu;
		//vari�veis utilizadas para a manipula��o da caixa de sele��o
		Point2D mousePositionBegin, mousePositionEnd;
	    Rectangle2D rect;
	    boolean controlWasPressed;
	    
		public ListenerAdapter(NetworkView networkView)
		{
			this.networkView = networkView;			
			mousePositionBegin = new Point2D.Float();
			mousePositionEnd = new Point2D.Float();
		    rect = new Rectangle2D.Float();							
		}
		
		public void createNodeMenu(NodeItem item)
		{
			if(!(item instanceof NodeItem)) {
				return;
			}
			
			//(ATUALIZA��O)
			//mudan�a de item.getInt para item.getLong
			final String clickedUserName = getUser(item.getLong("idTwitter")).getScreenName();
			popupMenu = new JPopupMenu();
			
			JMenuItem friends = new JMenuItem("View Friends");
			JMenuItem updates = new JMenuItem("View Tweets");
			//JMenuItem timeline = new JMenuItem("Ver Timeline");
			JMenuItem favorites = new JMenuItem("View Favorites");			
    		JMenuItem follow = new JMenuItem("Follow");//("Follow",'f') para adicionar atalho
    		JMenuItem leave = new JMenuItem("Unfollow");
    		JMenuItem sendReply = new JMenuItem("@"+clickedUserName);
    		JMenuItem sendMessage = new JMenuItem("Send Direct Message");
    		JMenuItem openURL = new JMenuItem("Open URL");
    		JMenuItem blockUnblock = new JMenuItem(); 
    		JMenuItem followers = new JMenuItem("View Followes");
    		JMenuItem removeFromGroup = new JMenuItem("Remove of Group");
    		 
    		Integer loggedUserId = Integer.parseInt(controller.getLoggedUserId());
    		Node mainUserNode = getNodeByTwitterId(loggedUserId);
			
			popupMenu.add(updates);
			//popupMenu.add(timeline);
    		popupMenu.add(friends); //� necess�rio?
    		popupMenu.add(followers);
    		popupMenu.add(favorites);
    		   		
    		if(!item.get("idTwitter").equals(loggedUserId)) { 
    			
    			popupMenu.addSeparator();
    			popupMenu.add(sendReply);
    			popupMenu.add(sendMessage);
    			//verifica se estou seguindo o usu�rio destino
    			int edge = g.getEdge(mainUserNode.getInt("id"), item.getInt("id"));
    			if(edge == -1) 
    				popupMenu.add(follow);    			
    			else 
    				popupMenu.add(leave);
    			
    			//verifica se estou sendo seguido
    			//edge = g.getEdge(clickedNode.getInt("id"),mainUserNode.getInt("id"), );
    			
    			//(ATUALIZA��O)
    			//mudan�a de clickedItem.getInt para .getLong
    			if(!socialNetwork.isUserBlocked(clickedItem.getLong("idTwitter")))
    				blockUnblock.setText("Block");
    			else
    				blockUnblock.setText("Unblock");
    				
    			popupMenu.add(blockUnblock);    			
    		}
    		if(item.getInt("groupId")>=0)
    			popupMenu.add(removeFromGroup);
    		popupMenu.addSeparator();
    		popupMenu.add(openURL);
			
    		if(!controller.isTwitterUser()) {
    			//(ATUALIZA��O)
    			//deixei comentado
				//followers.setEnabled(false);
				follow.setEnabled(false);
				leave.setEnabled(false);
				blockUnblock.setEnabled(false);
				sendReply.setEnabled(false);
				sendMessage.setEnabled(false);
			}   		
			
    		//follow.setMnemonic(KeyEvent.VK_F);
    		friends.addActionListener(new ActionListener() {				
    			@Override
				public void actionPerformed(ActionEvent e) {    				
					new AddFriendsThread(networkView, (NodeItem)clickedItem);
				}});
    		updates.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					
					StatusTab tab = controller.getStatusTabManager().getTab(StatusesType.UPDATES);
					
					if(isTwitterUser && clickedItem.getString("idTwitter").equals(controller.getLoggedUserId()))
						tab.setPanelContent(new StatusesTableThread(StatusesType.UPDATES));
					else
						tab.setPanelContent(new StatusesTableThread(StatusesType.UPDATES,clickedItem.getString("idTwitter")));
					controller.selectTab(0);
				}    			
    		});
//    		timeline.addActionListener(new ActionListener(){
//				@Override
//				public void actionPerformed(ActionEvent e) {
//					// TODO Auto-generated method stub					
//				}});
    		followers.addActionListener(new ActionListener(){
    			@Override
    			public void actionPerformed(ActionEvent arg0) {
    				new AddFollowersThread(networkView, (NodeItem)clickedItem);
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
					controller.selectTab(2);
    			}
    		});
    		follow.addActionListener(new ActionListener(){				
    			@Override
				public void actionPerformed(ActionEvent e) {    				
					new FollowUserThread(networkView, clickedItem, true);					
				}});
    		leave.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					new FollowUserThread(networkView, clickedItem, false);					
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
    		blockUnblock.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					new BlockUserThread(NetworkView.this,clickedItem);
				}});
    		openURL.addActionListener(new ActionListener(){
    			@Override
    			public void actionPerformed(ActionEvent e) {
    				new URLLinkAction("http://www.twitter.com/"+clickedUserName);					
    			}});
    		removeFromGroup.addActionListener(new ActionListener(){
    			@Override
    			public void actionPerformed(ActionEvent e) {
    				groupManager.removeFromGroup((NodeItem)clickedItem);				
    			}});
    		
    		//create popupMenu for 'background'
    		//JPopupMenu backgroundMenu = new JPopupMenu(); 
    		// ....
    		//addNode.addActionListener(this);
		}
		
		public void createGroupMenu(final AggregateItem item) {
			popupMenu = new JPopupMenu();
			JMenuItem updates = new JMenuItem("View Tweets");
			//JMenuItem timeline = new JMenuItem("Ver Timeline");
			JMenuItem removeGroup = new JMenuItem("Remove Group");
			JMenuItem mostPopularUsers = new JMenuItem("Most Popular Users");
			
			popupMenu.add(updates);
			//popupMenu.add(timeline);
			popupMenu.add(removeGroup);
			popupMenu.add(mostPopularUsers);
			
			updates.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					controller.selectTab(0);
					String groupName = "Grupo "+(item.getLong("id")+1);
					int aggSize = item.getAggregateSize();
					String[] ids = new String[aggSize];
					Iterator<NodeItem> nodes = item.items();
					int cont = 0;
					while(nodes.hasNext()) {
						NodeItem next = nodes.next();
						
						ids[cont] = String.valueOf(next.getLong("idTwitter"));
						cont++;
					}					
					StatusTab tab = controller.getStatusTabManager().getTab(StatusesType.UPDATES);					
					tab.setPanelContent(new StatusesTableThread(ids,groupName));					
				}});
//			timeline.addActionListener(new ActionListener(){
//				@Override
//				public void actionPerformed(ActionEvent e) {
//					// TODO Auto-generated method stub					
//				}});
			removeGroup.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					groupManager.removeGroup(item);
				}});
			mostPopularUsers.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					int groupId = item.getInt("id");
					Node[] groupArray = new Node[item.getAggregateSize()];
					Iterator<NodeItem> nodes = item.items();
					int cont = 0;
					while(nodes.hasNext()) {
						NodeItem next = nodes.next();
						groupArray[cont] = (Node)m_vis.getSourceTuple(next);
						cont++;
					}					
					controller.openGUIMostPopularUsersWindow(groupArray);
				}
			});
		}
		
		public void itemClicked(VisualItem item, MouseEvent e) {
			clickedItem = item;
				
			if(!(clickedItem instanceof NodeItem || clickedItem instanceof AggregateItem)) return;
					
			if(SwingUtilities.isRightMouseButton(e))
			{				
				if(clickedItem instanceof AggregateItem) 
					createGroupMenu((AggregateItem)item);		
				else if (clickedItem instanceof NodeItem)
					createNodeMenu((NodeItem)item);
				popupMenu.show(e.getComponent(), e.getX(), e.getY());
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
					{
						//adiciona amigos do usuario clicado
						new AddFriendsThread(networkView, (NodeItem)item);
						//adiciona followers do usuario clicado
						new AddFollowersThread(networkView, (NodeItem)item);
						//mostra tweets do usuario clicado
						StatusTab tab = controller.getStatusTabManager().getTab(StatusesType.UPDATES);
						if(isTwitterUser && clickedItem.getString("idTwitter").equals(controller.getLoggedUserId()))
							tab.setPanelContent(new StatusesTableThread(StatusesType.UPDATES));
						else
							tab.setPanelContent(new StatusesTableThread(StatusesType.UPDATES,clickedItem.getString("idTwitter")));
						controller.selectTab(0);
					}
					else {
						setChildrenVisible((NodeItem)item, true);
						item.setBoolean("isOpen", true);
					}
				}
				else
				{							
					setChildrenVisible((NodeItem)item,false);
					item.setBoolean("isOpen", false);		
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

			float[] box = (float[]) selectionBox.get(VisualItem.POLYGON);
			box[0] = box[1] = box[2] = box[3] = 
				box[4] = box[5] = box[6] = box[7] = 0;

			//setando coordenada x e y em rela��o ao frame
			mousePositionBegin.setLocation(e.getX(), e.getY());
			//setando coordenada absoluta em rela��o ao Display
			mousePositionBegin = networkView.getAbsoluteCoordinate(mousePositionBegin,null);
			
			selectionBox.setVisible(true);
		}
		
		public void mouseDragged(MouseEvent e)
		{
			if(!SwingUtilities.isLeftMouseButton(e)) return;
			if(!controlWasPressed) return;
			
			//atualizando coordenadas do mouse 
			mousePositionEnd.setLocation(e.getX(), e.getY());
			mousePositionEnd = networkView.getAbsoluteCoordinate(mousePositionEnd, null);
			
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
			networkView.repaint();
		}
		
		public void mouseReleased(MouseEvent e)
		{			
			if (!SwingUtilities.isLeftMouseButton(e)) return;
			if(!controlWasPressed) return;
			
			setPanControlOn(true);		
			controlWasPressed = false;
			selectionBox.setVisible(false);
						
			networkView.getVisualization().repaint();
		}	
	}
	
	//m�todos de get/set para customiza��o de items
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
	
	public int getSearchResultColor() {
		return searchResultColor;
	}

	public void setSearchResultColor(int searchResultColor) {
		this.searchResultColor = searchResultColor;
	}

	public int getNodeStrokeColor() {
		return nodeStrokeColor;
	}

	public void setNodeStrokeColor(int nodeStrokeColor) {
		this.nodeStrokeColor = nodeStrokeColor;
	}
	
	public void setWeightValue(float v) {
		weight_value = v;
	}
	
	public float getWeightValue() {
		return weight_value;
	}
}
