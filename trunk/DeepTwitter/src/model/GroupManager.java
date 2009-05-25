package model;

import java.util.Iterator;

import controller.ControllerDeepTwitter;

import prefuse.data.Edge;
import prefuse.data.Node;
import prefuse.data.tuple.TupleSet;
import prefuse.visual.AggregateItem;
import prefuse.visual.AggregateTable;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;

public class GroupManager {
	private int id;
	private AggregateTable groupTable;
	private GraphicManager gManager;
	private ControllerDeepTwitter controller = ControllerDeepTwitter.getInstance();
	private TupleSet selectedNodes;
	
	public GroupManager(GraphicManager gManager) {
		this.gManager = gManager;		
		groupTable = gManager.getVisualization().addAggregates(GraphicManager.GROUPS);
    	groupTable.addColumn(VisualItem.POLYGON, float[].class);
    	groupTable.addColumn("id", int.class);
	}
	
	public void addGroup()
	{
		selectedNodes = gManager.getTupleSet(GraphicManager.SELECTED_NODES);
		if(selectedNodes.getTupleCount()<2) {
    		controller.showMessageDialog("Você deve selecionar dois ou mais usuários para criar um grupo.",MessageType.INFORMATION);
    		return;
    	}
    	AggregateItem group = (AggregateItem)groupTable.addItem();    	
    	group.setInt("id", id);    
    	   	
    	Iterator<NodeItem> selected = selectedNodes.tuples();    	
    	while(selected.hasNext())
    		addToGroup(selected.next(), group);    		
    	
    	gManager.clearSelection();
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
					edge.set("weight", gManager.getWeightValue());    				
				else
					edge.set("weight", 0f);
			}
			if(target == n) {
				if(!group.containsItem(source))
					edge.set("weight", gManager.getWeightValue());
				else
					edge.set("weight", 0f);
			}			
		}
		
		Iterator<NodeItem> nodes = group.items();
		while(nodes.hasNext()) {
			NodeItem next = nodes.next();
			Edge e = gManager.addEdge((Node)next.getSourceTuple(), (Node)n.getSourceTuple());			
			//setando -1 para indicar que é aresta falsa (invisível)
			e.setFloat("weight", -1f);
		}
    }
    
    public void removeFromGroup(NodeItem n) {
    	int groupId = n.getInt("groupId");
    	if(groupId<0) return;
    	
    	AggregateItem ai = (AggregateItem) groupTable.getItem(groupId);
    	ai.removeItem(n);
    	n.setInt("groupId", -1);
    	Iterator<EdgeItem> i = n.edges();
    	
    	//TODO: trocar esta gambiarra por algo menos custoso?
    	boolean aindaTem = true;
    	while(aindaTem)
    	while(i.hasNext()) {
    		try{
    			EdgeItem edge = i.next();
    			if(edge.getFloat("weight")==gManager.getWeightValue()) 
    				edge.setFloat("weight", 0f);
    			else if(edge.getFloat("weight")==-1.0f) 
    				gManager.removeEdge((Edge)edge.getSourceTuple());
    		}
    		catch(IllegalArgumentException e) {
    			aindaTem = true;
    			i = n.edges();
    			System.out.println("TODO: loop de gambiarra (tentar evitar)");
    		}
    		aindaTem = false;
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
}
