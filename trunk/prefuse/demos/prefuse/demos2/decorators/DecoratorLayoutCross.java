/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package prefuse.demos2.decorators;

import java.awt.geom.Rectangle2D;
import prefuse.visual.DecoratorItem;
import prefuse.visual.VisualItem;

/**
 *
 * @author Juanjo Vega
 */
public class DecoratorLayoutCross extends DecoratorLayout {

    public DecoratorLayoutCross(String group) {
        super(group);
    }

    protected void placeDecorator(VisualItem node, DecoratorItem decorator) {
        if (node.isVisible() && !node.isExpanded()) {
            double x = 0.0, y = 0.0;

            Rectangle2D nodeBounds = node.getBounds();
            double nodeW = nodeBounds.getWidth();
            double nodeH = nodeBounds.getHeight();

            //Rectangle2D decoratorBounds = decorator.getBounds();
            //double decoratorW = decoratorBounds.getWidth();
            //double decoratorH = decoratorBounds.getHeight();

            x = node.getX() + nodeW / 2;// - decoratorW / 2;
            y = node.getY() + nodeH / 2;// - decoratorH / 2;

            // Sets decorator position.
            setX(decorator, null, x);
            setY(decorator, null, y);

            decorator.setVisible(true);
        } else {
            decorator.setVisible(false);
        }
    }
}