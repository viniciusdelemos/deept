package model;

import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.visual.EdgeItem; 

public class WeightedForceDirectedLayout extends ForceDirectedLayout { 
 
	public WeightedForceDirectedLayout(String graph, boolean enforceBounds) { 
		super(graph,enforceBounds); 
	} 

	@Override 
	protected float getSpringLength(EdgeItem e) { 
		if (e.getFloat("weight")>0) 
			return e.getFloat("weight"); 
		else 
			return super.getSpringLength(e); 
	} 
} 
