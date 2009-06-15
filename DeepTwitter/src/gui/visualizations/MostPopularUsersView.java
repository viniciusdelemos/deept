package gui.visualizations;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;

import model.ChartColor;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.DataSizeAction;
import prefuse.action.filter.VisibilityFilter;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.controls.ControlAdapter;
import prefuse.controls.DragControl;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.expression.parser.ExpressionParser;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.PrefuseLib;
import prefuse.util.force.DragForce;
import prefuse.util.force.ForceSimulator;
import prefuse.util.force.NBodyForce;
import prefuse.util.force.SpringForce;
import prefuse.visual.VisualGraph;
import prefuse.visual.VisualItem;
import profusians.controls.GenericToolTipControl;
import controller.ControllerDeepTwitter;

public class MostPopularUsersView extends Display{
	public final static String GRAPH = "graph";
	public final static String NODES = "graph.nodes";
	private DataSizeAction sizeAction;
	private Graph g;
	private LabelRenderer nodeRenderer;
	private GenericToolTipControl toolTipControl;
	private Node[] nodesArray;
	private List<Node> orderedByFollowersList, orderedByFriendsList,
	orderedByTweetsList, orderedByFavoritesList;
	private ControllerDeepTwitter controller = ControllerDeepTwitter.getInstance();
	private ShowingBy type;
	private int maxUsers;
	
	public enum ShowingBy{
    	followersCount,
    	friendsCount,
    	favoritesCount,
    	statusesCount,
    	nothing
    }
	
	public MostPopularUsersView(Node[] nodesArray, JEditorPane editor) {
		super(new Visualization());
		this.nodesArray = nodesArray;
		orderedByFollowersList = new ArrayList<Node>(nodesArray.length);
		orderedByFriendsList = new ArrayList<Node>(nodesArray.length);
		orderedByTweetsList = new ArrayList<Node>(nodesArray.length);
		orderedByFavoritesList = new ArrayList<Node>(nodesArray.length);
		type = ShowingBy.nothing;
		maxUsers = nodesArray.length;
		
		g = new Graph();
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
    	g.addColumn("isShown", boolean.class);

		// add visual data groups
		VisualGraph vg = m_vis.addGraph(GRAPH, g);
		
		nodeRenderer = new LabelRenderer(null, "image");
    	nodeRenderer.setVerticalAlignment(Constants.CENTER);       
    	nodeRenderer.setHorizontalPadding(2);
    	nodeRenderer.setVerticalPadding(2);        	
    	nodeRenderer.setMaxImageDimensions(48,48);
    	nodeRenderer.setRoundedCorner(8,8);
		m_vis.setRendererFactory(new DefaultRendererFactory(nodeRenderer));
		
		sizeAction = new DataSizeAction(NODES,"followersCount");//,Constants.QUANTILE_SCALE);
		sizeAction.setMaximumSize(10.0);
		sizeAction.setMinimumSize(0.5);
		sizeAction.setEnabled(false);
		
		ActionList update = new ActionList();		
        update.add(sizeAction);
        update.add(new RepaintAction());
        update.add(new VisibilityFilter(NODES,ExpressionParser.predicate("isShown==TRUE")));
        m_vis.putAction("update", update);
		
		ActionList layout = new ActionList(ActionList.INFINITY); 
		layout.add(update);
		ForceDirectedLayout fdl = new ForceDirectedLayout(GRAPH,getForceSimulator(),true);
		layout.add(fdl);    	
    	m_vis.putAction("layout", layout);    
    	
    	for(Node n : nodesArray) {
    		addOrRefreshNode(n);
		}
    	
    	String descriptions[] = { "Nome:", "Último Status: ", "Descrição:", "Localidade:", "Amigos:", "Seguidores:", "Tweets:", "Favoritos:" };
    	String data[] = { "name", "latestStatus", "description", "location", "friendsCount", "followersCount", "statusesCount", "favoritesCount" };

    	toolTipControl = new GenericToolTipControl(descriptions,data,200);    	
    	addControlListener(toolTipControl);
    	addControlListener(new ListenerAdapter(this));   	    	
    	addControlListener(new DragControl(false,true));
    	
    	new UpdateStatusEditor(editor).start();
    	
    	setHighQuality(true);
    	setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
    	m_vis.run("layout");   	
	}
	
	public void setSizeActionDataField(ShowingBy type) {
		sizeAction.setEnabled(true);
		sizeAction.setDataField(type.toString());
		this.type = type;
		setMaxUsers(maxUsers);
	}
	
	public ForceSimulator getForceSimulator() {		
		
		ForceSimulator forceSimulator = new ForceSimulator();

    	float gravConstant = -0.4f;
    	float minDistance = -0.01f;
    	float theta = NBodyForce.DEFAULT_THETA;
    	
    	//float drag = 0.007f;
    	float springCoeff = 1e-5f;
    	float defaultLength = 0f;    	
    	
    	forceSimulator.addForce(new NBodyForce(-5f, NBodyForce.DEFAULT_MIN_DISTANCE, theta));
    	forceSimulator.addForce(new DragForce(DragForce.DEFAULT_DRAG_COEFF));
    	forceSimulator.addForce(new SpringForce(SpringForce.DEFAULT_SPRING_COEFF, SpringForce.DEFAULT_SPRING_LENGTH));
		
		return forceSimulator;
				
		
//        fsim.addForce(new NBodyForce(-0.4f, 25f, NBodyForce.DEFAULT_THETA));
//        fsim.addForce(new SpringForce(1e-5f,0f));
//        fsim.addForce(new DragForce());
    }
	
	public void addOrRefreshNode(Node n) {
		synchronized (m_vis) {
			Node newNode = g.addNode();			
			newNode.set("screenName", n.getString("screenName"));
			newNode.set("name", n.getString("name"));
			newNode.set("image", n.getString("image"));
			newNode.set("protected",n.getBoolean("protected"));
			newNode.set("latestStatus",n.getString("latestStatus"));			
			newNode.set("location", n.getString("location"));
			newNode.set("description", n.getString("description"));
			newNode.set("friendsCount", n.getInt("friendsCount"));
			newNode.set("followersCount", n.getInt("followersCount"));
			newNode.set("statusesCount", n.getInt("statusesCount"));
			newNode.set("favoritesCount", n.getInt("favoritesCount"));
			newNode.set("isShown", true);
			
			orderLists(newNode);
			
			VisualItem vi = controller.getNodeItem(newNode);			
			double x = Math.random() * this.getSize().width;
			double y = Math.random() * this.getSize().height;
			PrefuseLib.setX(vi, null, x);
			PrefuseLib.setY(vi, null, y);
		}
	}
	
	public void setMaxUsers(int max) {
		maxUsers = max;
		List<Node> currentTypeList;
		if(type==ShowingBy.friendsCount)
			currentTypeList = orderedByFriendsList;
		else if(type==ShowingBy.followersCount)
			currentTypeList = orderedByFollowersList;
		else if(type==ShowingBy.statusesCount)
			currentTypeList = orderedByTweetsList;
		else
			currentTypeList = orderedByFavoritesList;
		
		for(int i=0; i<currentTypeList.size(); i++) {
			Node n = currentTypeList.get(i);
			if(i>=max)
				n.setBoolean("isShown",false);
			else
				n.setBoolean("isShown",true);
		}
	}
	
	public void orderLists(Node n) {		
		orderList(orderedByTweetsList, "statusesCount", n);
		orderList(orderedByFollowersList, "followersCount", n);
		orderList(orderedByFriendsList, "friendsCount", n);
		orderList(orderedByFavoritesList, "favoritesCount", n);
	}
	
	public void orderList(List<Node> list, String field, Node n) {
		if(list.size()==0)
			list.add(n);
		else
			for(int i=0; i<list.size(); i++) {
				if(n.getInt(field)>list.get(i).getInt(field)) {				
					list.add(i, n);
					return;
				}				
			}
		list.add(n);
	}
	
	private	class ListenerAdapter extends ControlAdapter{
		public ListenerAdapter(MostPopularUsersView display) {
			//TODO inserir item clicked - mostrar nodo na rede
		}	
	}
	
	private class UpdateStatusEditor extends Thread {
		private JEditorPane editor;
		
		public UpdateStatusEditor(JEditorPane editor) {
			this.editor = editor;
		}
		
		public void run() {
			try {
				while(true) {
					Iterator<VisualItem> iterator = m_vis.visibleItems(NODES);
					while(iterator.hasNext()) {
						VisualItem n = iterator.next();
						n.setStroke(new BasicStroke(4f));
						n.setStrokeColor(ChartColor.LIGHT_BLUE.getRGB());
						editor.setText("<html><font size=\"4\" face=\"Arial\"><b><font color=\"Navy\">"+n.getString("screenName")+"</font></b>: "+n.getString("latestStatus")+"</font></html>");
						//editor.setText(n.getString("latestStatus"));
						this.sleep(7000);
						n.setStrokeColor(Color.TRANSLUCENT);
					}
				}
			}
			catch(InterruptedException e) {
				
			}
		}
	}
}