package controller;

import gui.visualizations.NetworkView;

import java.util.ArrayList;
import java.util.Iterator;

import model.MessageType;

import controller.ControllerDeepTwitter;

import prefuse.data.Edge;
import prefuse.data.Node;
import prefuse.data.tuple.TupleSet;
import prefuse.data.util.TableIterator;
import prefuse.visual.AggregateItem;
import prefuse.visual.AggregateTable;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;

public class GroupManager {
	private int id;
	private AggregateTable groupTable;
	private NetworkView networkView;
	private ControllerDeepTwitter controller = ControllerDeepTwitter.getInstance();
	private TupleSet selectedNodes;
	
	public GroupManager(NetworkView networkView) {
		this.networkView = networkView;		
		groupTable = networkView.getVisualization().addAggregates(NetworkView.GROUPS);
    	groupTable.addColumn(VisualItem.POLYGON, float[].class);
    	groupTable.addColumn("id", int.class);
	}
	
	public void addGroup()
	{
		selectedNodes = networkView.getTupleSet(NetworkView.SELECTED_NODES);
		if(selectedNodes.getTupleCount()<2) {
    		controller.showMessageDialog("Voc� deve selecionar dois ou mais usu�rios para criar um grupo.",MessageType.INFORMATION);
    		return;
    	}
    	AggregateItem group = (AggregateItem)groupTable.addItem();    	
    	group.setInt("id", id);    
    	   	
    	Iterator<NodeItem> selected = selectedNodes.tuples();    	
    	while(selected.hasNext())
    		addToGroup(selected.next(), group);    		
    	
    	networkView.clearSelection();
    	id++;
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
					edge.set("weight", networkView.getWeightValue());    				
				else
					edge.set("weight", 0f);
			}
			if(target == n) {
				if(!group.containsItem(source))
					edge.set("weight", networkView.getWeightValue());
				else
					edge.set("weight", 0f);
			}			
		}
		
		Iterator<NodeItem> nodes = group.items();
		while(nodes.hasNext()) {
			NodeItem next = nodes.next();
			Edge e = networkView.addEdge((Node)next.getSourceTuple(), (Node)n.getSourceTuple());			
			//setando -1 para indicar que � aresta falsa (invis�vel)
			e.setFloat("weight", -1f);
		}
    }
    
    public void removeFromGroup(NodeItem n) {
    	synchronized(networkView.getVisualization()) {
    		int groupId = n.getInt("groupId");
    		if(groupId<0) return;

    		Iterator<AggregateItem> aggregates = groupTable.getAggregates(n);
    		while(aggregates.hasNext()) {
    			AggregateItem ai = aggregates.next();    	
    			n.setInt("groupId", -1);
    			Iterator<EdgeItem> i = n.edges();

    			ArrayList<EdgeItem> edgesToRemove = new ArrayList<EdgeItem>();

    			while(i.hasNext()) {
    				try{
    					EdgeItem edge = i.next();
    					if(edge.getFloat("weight")==networkView.getWeightValue()) 
    						edge.setFloat("weight", 0f);
    					else if(edge.getFloat("weight")==-1.0f) 
    						edgesToRemove.add(edge);
    				}
    				catch(IllegalArgumentException e) {    				
    					e.printStackTrace();
    				}
    			}
    			for(EdgeItem e : edgesToRemove) {
    				try {
    					networkView.removeEdge((Edge)e.getSourceTuple());
    				}
    				catch(Exception e1) {}
    			}
    			ai.removeItem(n);
    		}
    	}
    }
    
    public void removeGroup(AggregateItem ai) {
    	synchronized(networkView.getVisualization()) {
    		Iterator<NodeItem> i = ai.items();
    		ArrayList<NodeItem> nodesToRemove = new ArrayList<NodeItem>();
    		while(i.hasNext()) {
    			NodeItem n = i.next();
    			n.setInt("groupId", -1);
    			nodesToRemove.add(n);
    			Iterator<EdgeItem> i2 = n.edges();
    			ArrayList<EdgeItem> edgesToRemove = new ArrayList<EdgeItem>();
    			while(i2.hasNext()) {
    				try{
    					EdgeItem edge = i2.next();
    					if(edge.getFloat("weight")==networkView.getWeightValue()) 
    						edge.setFloat("weight", 0f);
    					else if(edge.getFloat("weight")==-1.0f) 
    						edgesToRemove.add(edge);
    				}
    				catch(IllegalArgumentException e) {    				
    					e.printStackTrace();
    				}
    			}
    			for(EdgeItem e : edgesToRemove) {
    				try {
    					networkView.removeEdge((Edge)e.getSourceTuple());
    				}
    				catch(Exception e1) {}
    			}			
    		}    
    		for(NodeItem n : nodesToRemove) {
    			ai.removeItem(n);
    		}
    		groupTable.removeTuple(ai);//ai.getInt("id"));    
    	}
    }
    
    public AggregateTable getGroups() {
    	return groupTable;
    }
}
