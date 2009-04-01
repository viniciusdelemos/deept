package model;

import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.util.force.ForceSimulator;
import prefuse.visual.EdgeItem; 

public class WeightedForceDirectedLayout extends ForceDirectedLayout { 
 
	public WeightedForceDirectedLayout(String graph, ForceSimulator fsim, boolean enforceBounds) { 
		super(graph,fsim,enforceBounds); 
	} 

	@Override 
	protected float getSpringLength(EdgeItem e) { 
		if (e.getFloat("weight")>0) 
			return e.getFloat("weight"); 
		else 
			return super.getSpringLength(e); 
	} 
} 
