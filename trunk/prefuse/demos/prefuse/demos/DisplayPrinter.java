package prefuse.demos;

/*
 * @(#)DisplayPrinter.java   30.01.2009
 * 
 * Copyright (c) 2009 RBG, Technical University of Darmstadt
 * Copyright (c) 2009 Marcus Staender
 *
 */

import prefuse.Display;
import prefuse.Visualization;

import prefuse.util.GraphicsLib;

//~--- JDK imports ------------------------------------------------------------

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

//~--- Classes ----------------------------------------------------------------

/**
 * This class prints a prefuse.Display to a printer. The scalefactor will be 1 or less if the image does not fit 
 * on the page. It automatically uses as much space as possible which includes possible rotation if the image is in 
 * landscape style. Further it centers the image on the page.
 *
 *
 * @version    1.0.0, 30.01.2009
 * @author     Marcus St&auml;nder (<a href="mailto:webmaster@msdevelopment.org">webmaster@msdevelopment.org</a>)
 */
public class DisplayPrinter extends AbstractAction {
	
	private static final long serialVersionUID = 2866833786471348149L;
	
	private Display m_display = null;
	
    //~--- Constructors -------------------------------------------------------

    /**
     * Constructs the printer
     *
     */
    public DisplayPrinter() {
    }
    
    /**
     * Constructs the printer
     *
     * @param component The {@link JComponent} to register this action under the keystroke <i>ctrl P</i>
     */
    public DisplayPrinter(JComponent component) {
    	registerKeyboardAction(component);
    }

    /**
     * Constructs the printer
     *
     * @param display The {@link Display} to print
     */
    public DisplayPrinter(Display display) {
    	m_display = display;
    }

    /**
     * Constructs the printer
     *
     * @param display The {@link Display} to print
     * @param component The {@link JComponent} to register this action under the keystroke <i>ctrl P</i>
     */
    public DisplayPrinter(Display display, JComponent component) {
    	m_display = display;
    	registerKeyboardAction(component);
    }
    
    //~--- Methods ------------------------------------------------------------
    
    /**
     * This method registers the keystroke <i>ctrl P</i> to the {@link JComponent} for executing this action
     * 
     * @param component The {@link JComponent} to register this action under the keystroke <i>ctrl P</i>
     */
    public void registerKeyboardAction(JComponent component) {
    	component.getInputMap().put(KeyStroke.getKeyStroke("ctrl P"), "print");
    	component.getActionMap().put("print", this);
    }
    
    /**
     * This method lets the user pass a prefuse <code>Display</code> for later printing
     *
     * @paran display The {@link Display} to print
     * 
     * @returns display The old {@link Display} that might have been set or <code>null</code> if none available
     */
    public Display setDisplay(Display display) {
    	Display old_display = m_display;
    	m_display = display;
    	return old_display;
    }
    
    /**
     * This method lets the user print the assigned prefuse <code>Display</code>. If you use this method MAKE SURE you
     * already registered a permanent display!
     */
    public void print() {
    	print(m_display);
    }

    /**
     * This method lets the user pass a prefuse <code>Display</code> for printing
     *
     * @paran display The {@link Display} to print
     */
    public void print(Display display) {

        // Now print the display
        boolean success = false;

        try {
            System.out.print("INFO Printing display");
        	if (m_display != null) {
        		success = printDisplay(display);
        		System.out.println("\tDONE");
        	} else {
        		System.err.println("WARN No display given to the printer.");
        	}
        } catch (Exception e) {
            success = false;
        }

        // show result dialog on failure
        if (!success) {
            JOptionPane.showMessageDialog(display, "Error printing the display!", "Printer error",
                                          JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean printDisplay(Display display) {

        String m_group = Visualization.ALL_ITEMS;

        try {

            // Now comes the nice part

            // Get the bounding box
            Rectangle2D bounds = display.getVisualization().getBounds(m_group);

            // Some little extra spacing
            GraphicsLib.expand(bounds, 10 + (int) (1 / display.getScale()));

            // Get a buffered image to draw into
            BufferedImage img       = getNewOffscreenBuffer(display, (int) bounds.getWidth(), (int) bounds.getHeight());
            Graphics2D    g         = (Graphics2D) img.getGraphics();

            /*
             * Set up the display, render, then revert to normal settings
             */

            // The zoom point, zooming should not change anything else than the scale
            Point2D       zoomPoint = new Point2D.Double(0, 0);

            // Get and remember the current scaling
            Double        scale     = display.getScale();

            // Change scale to normal (1)
            display.zoom(zoomPoint, 1 / scale);

            boolean isHighQuality = display.isHighQuality();

            display.setHighQuality(true);

            // Remember the current point
            Point2D currentPoint = new Point2D.Double(display.getDisplayX(), display.getDisplayY());

            // Now pan so the most left element is at the left side of the display and
            // the highest element is at the top.
            display.panToAbs(new Point2D.Double(bounds.getMinX() + display.getWidth() / 2,
                    bounds.getMinY() + display.getHeight() / 2));

            // Now lets prefuse to the actual painting
            display.paintDisplay(g, new Dimension((int) bounds.getWidth(), (int) bounds.getHeight()));

            // Undo the panning, zooming and reset the quality mode
            display.panToAbs(new Point2D.Double(currentPoint.getX() + display.getWidth() / 2,
                    currentPoint.getY() + display.getHeight() / 2));
            display.setHighQuality(isHighQuality);
            display.zoom(zoomPoint, scale);    // also takes care of damage report

            // Now print the bufferd data
            
            try {
            	PrinterJob printJob = PrinterJob.getPrinterJob();
    			PrintableCenteringJob pm = new PrintableCenteringJob(img);

		
    			printJob.setPrintable(pm);
    			if (printJob.printDialog()) {
    				try {
    					printJob.print();
    				} catch (PrinterException pe) {
    					System.out.print("\tError printing: " + pe);
    				}
    			} else {
    				System.out.print("\tABORTED");
    			}
    			//return true;
    		} catch (Exception e) {
    			e.printStackTrace();
    			//return false;
    		}
            
            
            
            

            return true;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }

    // From Display
    private BufferedImage getNewOffscreenBuffer(Display display, int width, int height) {
        BufferedImage img = null;

        if (!GraphicsEnvironment.isHeadless()) {
            try {
                img = (BufferedImage) display.createImage(width, height);
            } catch (Exception e) {
                img = null;
            }
        }

        if (img == null) {
            return new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        }

        return img;
    }
    
    /**
     * This class provides a {@link Printable} job that automatically fits the image on the page 
     * and rotates + centers it.
     * 
     * @version    1.0.0, 30.01.2009
     * @author     Marcus St&auml;nder (<a href="mailto:webmaster@msdevelopment.org">webmaster@msdevelopment.org</a>)
     */
	private class PrintableCenteringJob extends JComponent implements Printable {

		private static final long serialVersionUID = -2085393948363282949L;

		BufferedImage m_image = null;

		public PrintableCenteringJob(BufferedImage img) {
			m_image = img;
		}

		public int print(Graphics graphics, PageFormat pf, int pageIndex) {

			if (pageIndex != 0) {
				return NO_SUCH_PAGE;
			}

			AffineTransform at = new AffineTransform();
			at.translate(0,0);

			double imageWidth = m_image.getWidth();
			double imageHeight = m_image.getHeight();

			// Always use Protrait mode but rotate the image if needed
			pf.setOrientation(PageFormat.PORTRAIT);

			// Scaling information for fitting on the page (the printable space!)
			double xScale = 1;
			double yScale = 1;
			double angle = 0; // The angle to rotate in rad, *NOT* degree!
			if (imageHeight >= imageWidth) {
				// Simulate portrait mode
				xScale = pf.getImageableWidth() / imageWidth;
				yScale = pf.getImageableHeight() / imageHeight;
			} else {
				// Simulate landscape mode
				xScale = pf.getImageableWidth() / imageHeight;
				yScale = pf.getImageableHeight() / imageWidth;
				angle = Math.PI/2;
			}

			// Only use the minimum value and use it for both axis to keep aspect ratio
			double aspectScale = Math.min(xScale, yScale);

			// Do not zoom in
			aspectScale = Math.min(aspectScale, 1);

			// Now do the rotating
			at.rotate(angle);

			// Now do the scaling
			at.scale(aspectScale, aspectScale);

			if (angle != 0) {
				// If rotated by 90 degrees basically calculate with the rotated values, swap h and w
				at.translate( (pf.getHeight()/aspectScale - imageWidth )/2, -(pf.getWidth() /aspectScale + imageHeight)/2);
			} else {
				at.translate( (pf.getWidth() /aspectScale - imageWidth) /2,  (pf.getHeight()/aspectScale - imageHeight)/ 2);
			}

			((Graphics2D)graphics).drawRenderedImage(m_image, at);

			return PAGE_EXISTS;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override public void actionPerformed(ActionEvent e) {
		print();
	}
}
