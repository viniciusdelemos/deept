/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package prefuse.demos2.decorators;

import java.util.Iterator;
import prefuse.action.layout.Layout;
import prefuse.visual.DecoratorItem;
import prefuse.visual.VisualItem;

/**
 *
 * @author Juanjo Vega
 */
abstract public class DecoratorLayout extends Layout {

    public DecoratorLayout(String group) {
        super(group);
    }

    public void run(double frac) {
        Iterator iter = m_vis.items(m_group);
        while (iter.hasNext()) {
            DecoratorItem decorator = (DecoratorItem) iter.next();
            VisualItem node = decorator.getDecoratedItem();

            placeDecorator(node, decorator);
        }
    }

    abstract protected void placeDecorator(VisualItem node, DecoratorItem decorator);
}