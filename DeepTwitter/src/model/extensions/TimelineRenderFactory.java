package model.extensions;

import gui.visualizations.TimelineView;
import prefuse.Constants;
import prefuse.render.AxisRenderer;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.render.Renderer;
import prefuse.render.ShapeRenderer;
import prefuse.visual.expression.InGroupPredicate;

public final class TimelineRenderFactory extends DefaultRendererFactory {
	Renderer labelRenderer, shapeRenderer;
	
	public TimelineRenderFactory(boolean isGroup) {
		super();		
		labelRenderer = new LabelRenderer(null,StatusesDataTable.ColNames.IMAGE_URL.toString());		
		((LabelRenderer)labelRenderer).setMaxImageDimensions(35,35);
		shapeRenderer = new ShapeRenderer(13);
		
		Renderer axisX = new RotateLabelAxisRenderer(Constants.CENTER, Constants.FAR_BOTTOM);
		Renderer axisY = new AxisRenderer(Constants.FAR_LEFT, Constants.CENTER);
	    
		if(isGroup)
			this.setDefaultRenderer(labelRenderer);
		else
			this.setDefaultRenderer(shapeRenderer);
		
        this.add(new InGroupPredicate(TimelineView.Group.X_AXIS.toString()), axisX);
        this.add(new InGroupPredicate(TimelineView.Group.Y_AXIS.toString()), axisY);
	}
	
	public void switchRenderer() {
		if(this.getDefaultRenderer() instanceof LabelRenderer)
			this.setDefaultRenderer(shapeRenderer);
		else
			this.setDefaultRenderer(labelRenderer);
	}

}