package gui.visualizations;

import java.util.List;

import javax.swing.BorderFactory;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.DataSizeAction;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.controls.ControlAdapter;
import prefuse.controls.DragControl;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.force.DragForce;
import prefuse.util.force.ForceSimulator;
import prefuse.util.force.NBodyForce;
import prefuse.util.force.SpringForce;
import prefuse.visual.VisualGraph;
import prefuse.visual.VisualItem;
import profusians.controls.GenericToolTipControl;
import twitter4j.User;

public class ActiveUsers2 extends Display{
	public final static String GRAPH = "graph";
	public final static String NODES = "graph.nodes";
	private DataSizeAction sizeAction;
	private Graph g;
	private LabelRenderer nodeRenderer;
	private GenericToolTipControl toolTipControl;
	
	public ActiveUsers2(User[] usersList) {
		super(new Visualization());

		g = new Graph();
		g.addColumn("screenName", String.class);
		g.addColumn("name", String.class);
    	g.addColumn("image", String.class);
    	g.addColumn("location", String.class);
    	g.addColumn("description", String.class);
    	g.addColumn("protected", Boolean.class);
    	g.addColumn("friendsCount", int.class);
    	g.addColumn("followersCount", int.class);
    	g.addColumn("statusesCount", int.class);
    	g.addColumn("favoritesCount", int.class);    	
    	g.addColumn("latestStatus", String.class);

		// add visual data groups
		VisualGraph vg = m_vis.addGraph(GRAPH, g);
		
		nodeRenderer = new LabelRenderer(null, "image");
    	nodeRenderer.setVerticalAlignment(Constants.CENTER);       
    	nodeRenderer.setHorizontalPadding(0);
    	nodeRenderer.setVerticalPadding(0);        	
    	nodeRenderer.setMaxImageDimensions(48,48);
    	nodeRenderer.setRoundedCorner(8,8);
		m_vis.setRendererFactory(new DefaultRendererFactory(nodeRenderer));
		
		sizeAction = new DataSizeAction(NODES,"followersCount");//,usersList.length);
		sizeAction.setMaximumSize(10.0);
		sizeAction.setMinimumSize(0.5);
		sizeAction.setEnabled(false);
		
		ActionList update = new ActionList();		
        update.add(sizeAction);
//        update.add(new ColorAction("data", VisualItem.STROKECOLOR) {
//            public int getColor(VisualItem item) {
//                return ColorLib.rgb((item.isHover() ? 255 : 0), 0, 0);
//            }
//        });
        update.add(new RepaintAction());
        m_vis.putAction("update", update);
		
		ActionList layout = new ActionList(ActionList.INFINITY); 
		layout.add(update);
		ForceDirectedLayout fdl = new ForceDirectedLayout(GRAPH,getForceSimulator(),true);
		layout.add(fdl);    	
    	m_vis.putAction("layout", layout);    
    	
    	for(User u : usersList) {
			addNode(u);
		}
    	
    	String descriptions[] = { "Nome:", "Último Status: ", "Descrição:", "Localidade:", "Amigos:", "Seguidores:", "Statuses:" };
    	String data[] = { "name", "latestStatus", "description", "location", "friendsCount", "followersCount", "statusesCount" };

    	toolTipControl = new GenericToolTipControl(descriptions,data,200);    	
    	addControlListener(toolTipControl);
    	addControlListener(new ListenerAdapter(this));   	    	
    	addControlListener(new DragControl(false,true));
    	//nodeRenderer.getImageFactory().preloadImages(m_vis.items(),"image");
    	
    	setHighQuality(true);
    	m_vis.run("layout");   	
	}
	
	public void setSizeActionDataField(String field) {
		sizeAction.setEnabled(true);
		sizeAction.setDataField(field);
	}
	
	public ForceSimulator getForceSimulator() {		
		
		ForceSimulator	forceSimulator = new ForceSimulator();

    	float gravConstant = -0.4f;
    	float minDistance = -0.01f;
    	float theta = NBodyForce.DEFAULT_THETA;
    	
    	//float drag = 0.007f;
    	float springCoeff = 1e-5f;
    	float defaultLength = 0f;    	
    	
    	forceSimulator.addForce(new NBodyForce(gravConstant, minDistance, theta));
    	forceSimulator.addForce(new DragForce());
    	forceSimulator.addForce(new SpringForce(springCoeff, defaultLength));
		
		return forceSimulator;
		
//        fsim.addForce(new NBodyForce(-0.4f, 25f, NBodyForce.DEFAULT_THETA));
//        fsim.addForce(new SpringForce(1e-5f,0f));
//        fsim.addForce(new DragForce());
    }
	
	public void addNode(User u) {
		synchronized (m_vis) {
			Node newNode = g.addNode();
			newNode.set("screenName", u.getScreenName());
			newNode.set("name", u.getName());
			newNode.set("image", u.getProfileImageURL().toString());
			newNode.set("protected",u.isProtected());
			newNode.set("latestStatus",u.getStatusText());			
			newNode.set("location", u.getLocation());
			newNode.set("description", u.getDescription());
			newNode.set("friendsCount", u.getFriendsCount());
			newNode.set("followersCount", u.getFollowersCount());
			newNode.set("statusesCount", u.getStatusesCount());
			newNode.set("favoritesCount", u.getFavouritesCount());
			
			VisualItem vi = m_vis.getVisualItem(NODES, newNode);
			int distMin = 30;

			double x = distMin
					+ (Math.random() * (this.size().width - (distMin * 2)));
			double y = distMin
					+ (Math.random() * (this.size().height - (distMin * 2)));

			vi.setStartX(0);
			vi.setStartY(0);
			vi.setX(x);
			vi.setY(y);
			vi.setEndX(x);
			vi.setEndY(y);
		}
	}
	
	private	class ListenerAdapter extends ControlAdapter{
		public ListenerAdapter(ActiveUsers2 display) {
			//inserir item clicked - mostrar nodo na rede
		}
	}
}