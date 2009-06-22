/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package prefuse.demos2.decorators;

import java.awt.Color;
import javax.swing.BorderFactory;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.demos.GraphView.FitOverviewListener;
import prefuse.util.display.DisplayLib;

/**
 *
 * @author Juanjo Vega
 */
public class Overview extends Display {

    public Overview(Display display) {
        super(display.getVisualization());

        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder("Overview"));

        DisplayLib.fitViewToBounds(this, getVisualization().getBounds(Visualization.ALL_ITEMS), 0);
        addItemBoundsListener(new FitOverviewListener());

        OverviewControl zoomToFitRectangleControl = new OverviewControl(display, this);
        addControlListener(zoomToFitRectangleControl);
        addPaintListener(zoomToFitRectangleControl);
    }
}
