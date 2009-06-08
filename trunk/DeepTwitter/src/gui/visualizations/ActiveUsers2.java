package gui.visualizations;

import gui.visualizations.MostActiveUsers.Sizes;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.DataSizeAction;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.controls.ControlAdapter;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Schema;
import prefuse.data.tuple.TupleSet;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.PrefuseLib;
import prefuse.util.force.DragForce;
import prefuse.util.force.ForceItem;
import prefuse.util.force.ForceSimulator;
import prefuse.util.force.NBodyForce;
import prefuse.util.force.SpringForce;
import prefuse.visual.VisualGraph;
import prefuse.visual.VisualItem;
import twitter4j.User;

public class ActiveUsers2 extends Display{
	private DataSizeAction sizeAction;
	private Graph g;
	
	public ActiveUsers2(List<User> usersList) {
		super(new Visualization());

		g = new Graph();
		g.addColumn("idTwitter",int.class);
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
		nodeRenderer.setVerticalAlignment(Constants.BOTTOM);
		nodeRenderer.setHorizontalPadding(0);
		nodeRenderer.setVerticalPadding(0);
		nodeRenderer.setMaxImageDimensions(100, 100);
		m_vis.setRendererFactory(new DefaultRendererFactory(nodeRenderer));
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

			Node n = g.addNode();

			n.set("image", u.getProfileImageURL().toString());
			n.set("user", u);
			n.set("userName", u.getScreenName());

			VisualItem vi = getVisualization().getVisualItem(GRAPH, n);

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

			System.out.println(u.getScreenName());

			nodes.add(n);
			m_vis.notifyAll();
		}
	}
	
}
