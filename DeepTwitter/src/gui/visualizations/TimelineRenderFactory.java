package gui.visualizations;

import prefuse.Constants;
import prefuse.render.AxisRenderer;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.render.Renderer;
import prefuse.render.ShapeRenderer;
import prefuse.visual.expression.InGroupPredicate;

public final class TimelineRenderFactory extends DefaultRendererFactory {

	public TimelineRenderFactory() {
		super();
		
		//Renderer renderer = new LabelRenderer(null,StatusesDataTable.ColNames.IMAGE_URL.toString());
		Renderer renderer = new ShapeRenderer(13);
		Renderer axisX = new RotateLabelAxisRenderer(Constants.CENTER, Constants.FAR_BOTTOM);
		Renderer axisY = new AxisRenderer(Constants.FAR_LEFT, Constants.CENTER);
	    
	    this.setDefaultRenderer(renderer);
        this.add(new InGroupPredicate(TimelinePanel.Group.STATUS.toString()), renderer);
        this.add(new InGroupPredicate(TimelinePanel.Group.X_AXIS.toString()), axisX);
        this.add(new InGroupPredicate(TimelinePanel.Group.Y_AXIS.toString()), axisY);
	}
	
	//inserir método para setar o renderer

}