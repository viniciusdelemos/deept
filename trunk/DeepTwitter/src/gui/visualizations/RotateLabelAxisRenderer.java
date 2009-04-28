package gui.visualizations;

import java.awt.geom.AffineTransform;

import prefuse.render.AxisRenderer;
import prefuse.visual.VisualItem;

/**
 * AxisRenderer, but with rotate label.
 * @author easy
 *
 */
public class RotateLabelAxisRenderer extends AxisRenderer {

	private static double radius60n = Math.toRadians(-60);
	private static double sin60 = Math.sin(Math.toRadians(60));
	private static double cos60 = Math.cos(Math.toRadians(60));

	/** Create a new RotateLabelAxisRenderer. */
	public RotateLabelAxisRenderer() {
		super(); 
	}

	/** Create a new RotateLabelAxisRenderer. */
	public RotateLabelAxisRenderer(int xalign, int yalign) {
		super(xalign, yalign);
	}

	// The easy way is buggy: the transformed font is not drawn correctly.
	/* (non-Javadoc)
	 * @see prefuse.render.AbstractShapeRenderer#getTransform(prefuse.visual.VisualItem)
	 */
	protected AffineTransform getTransform(VisualItem item) {
		AffineTransform tr = AffineTransform.getTranslateInstance(0, 0);

		double centerX =  this.getRawShape(item).getBounds2D().getCenterX();
		double centerY =  this.getRawShape(item).getBounds2D().getCenterY();
		double w = this.getRawShape(item).getBounds2D().getWidth() / 2;
		double yOffset = w * sin60;
		double xOffset = w * cos60;

		tr.translate(centerX - xOffset, centerY + yOffset); //3
		tr.rotate(radius60n); // 2
		tr.translate(-centerX, -centerY); // 1			
		
		return tr;
	}
}
