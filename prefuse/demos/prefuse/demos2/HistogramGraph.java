package prefuse.demos2;

import java.awt.Insets;
import java.awt.geom.Rectangle2D;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.layout.AxisLabelLayout;
import prefuse.action.layout.AxisLayout;
import prefuse.data.Table;
import prefuse.data.query.NumberRangeModel;
import prefuse.render.AxisRenderer;
import prefuse.render.PolygonRenderer;
import prefuse.render.Renderer;
import prefuse.render.RendererFactory;
import prefuse.util.ColorLib;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.VisiblePredicate;
import prefuse.demos2.BarRenderer;
import prefuse.demos2.HistogramTable;

/**
 * A simple histogram visualization that allows different columns
 * in a data table to be histogramized and displayed.
 * The starting point was ScatterPlot.java by Jeffrey Heer, but
 * Kaitlin Duck Sherwood has modified it quite extensively.  
 * 
 * Kaitlin Duck Sherwood's modifications are granted as is for any
 * commercial or non-commercial use, with or without attribution.
 * The only conditions are that you can't pretend that you wrote them,
 * and that you leave these notices about her authorship in the source.
 * 
 * Known bug: See the comments for HistogramFrame; there might
 * be a bug in HistogramGraph::updateAxes(), but I sure can't figure
 * out what it is.  I suspect that it is a prefuse bug.
 * 
 * Note: I wanted to use Prefuse's StackedAreaChart, but couldn't
 * get it to work.  If you figure out how to make it work, please
 * email me.  -- KDS
 * 
 * @author <a href="http://jheer.org">jeffrey heer</a>
 * @author <a href="http://webfoot.com/ducky.home.html">Kaitlin Duck Sherwood</a>
 */
public class HistogramGraph extends Display {

	protected static final String group = "data";
	public static final int DEFAULT_BIN_COUNT = 15;

	// KDS -- I tend to make things protected instead of private so 
	// that people can subclass them.  I'm not sure that's the right 
	// thing to do.
	protected Rectangle2D m_dataB = new Rectangle2D.Double();
	protected Rectangle2D m_xlabB = new Rectangle2D.Double();
	protected Rectangle2D m_ylabB = new Rectangle2D.Double();

	protected BarRenderer m_shapeR = new BarRenderer(10);

	protected HistogramTable m_histoTable;

	private AxisLayout m_xAxis, m_yAxis;
	private AxisLabelLayout m_xLabels, m_yLabels;

	/**
	 * @param histoTable a histogrammized version of dataTable
	 * @param dataTable a prefuse Table which holds the raw (unhistogrammized) data.
	 * @param startingField the name of the field (column) of the data table 
	 * whose histogram is to be shown in the histogram graph.
	 * 
	 * Note that the data table isn't used much here, and could maybe be moved into
	 * Histo
	 */
	public HistogramGraph(HistogramTable histoTable, String startingField) {
		super(new Visualization());

		m_histoTable = histoTable;
		startingField = getStartingField(startingField);

		// --------------------------------------------------------------------
		// STEP 1: setup the visualized data

		m_vis.addTable(group, m_histoTable);

		initializeRenderer();

		// --------------------------------------------------------------------
		// STEP 2: create actions to process the visual data
		initializeAxes(startingField);

		ColorAction color = new ColorAction(group, 
				VisualItem.STROKECOLOR, ColorLib.rgb(100,100,255));
		m_vis.putAction("color", color);

		ActionList draw = new ActionList();
		draw.add(m_xAxis);
		draw.add(m_yAxis);
		draw.add(m_xLabels);  
		draw.add(m_yLabels); 

		draw.add(color);
		draw.add(new RepaintAction());
		m_vis.putAction("draw", draw);

		// --------------------------------------------------------------------
		// STEP 3: set up a display and ui components to show the visualization

		initializeWindowCharacteristics();

		// --------------------------------------------------------------------        
		// STEP 4: launching the visualization

		m_vis.run("draw");

	}

	/**
	 * This sets up various things about the window, including the size.
	 * TODO include the size in the constructor
	 */
	private void initializeWindowCharacteristics() {
		setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		setSize(1200,800);

		setHighQuality(true);

		initializeLayoutBoundsForDisplay(); 
		m_shapeR.setBounds(m_dataB);
	}

	/**
	 * @param fieldName the name of the field (column) to display
	 */
	private void initializeAxes(String fieldName) {
		m_xAxis = new AxisLayout(group, fieldName, 
				Constants.X_AXIS, VisiblePredicate.TRUE);
		m_xAxis.setLayoutBounds(m_dataB);
		m_vis.putAction("x", m_xAxis);

		String countField = HistogramTable.getCountField(fieldName);
		m_yAxis = new AxisLayout(group, countField, 
				Constants.Y_AXIS, VisiblePredicate.TRUE);

		m_yAxis.setLayoutBounds(m_dataB);
		m_vis.putAction("y", m_yAxis);

		m_xLabels = new AxisLabelLayout("xlabels", m_xAxis, m_xlabB);
		m_vis.putAction("xlabels", m_xLabels);

		m_yLabels = new AxisLabelLayout("ylabels", m_yAxis, m_ylabB);
		m_vis.putAction("ylabels", m_yLabels);
		updateAxes(fieldName);
	}

	
	private void initializeRenderer() {
		m_vis.setRendererFactory(new RendererFactory() {
			Renderer yAxisRenderer = new AxisRenderer(Constants.RIGHT, Constants.TOP);
			Renderer xAxisRenderer = new AxisRenderer(Constants.CENTER, Constants.FAR_BOTTOM);
			Renderer barRenderer = new PolygonRenderer(Constants.POLY_TYPE_LINE);

			public Renderer getRenderer(VisualItem item) {
				if(item.isInGroup("ylabels"))
					return yAxisRenderer;
				if(item.isInGroup("xlabels"))
					return xAxisRenderer;
				if(item.isInGroup("barchart"))
					return barRenderer;
				return m_shapeR;

			}
		});
	}

	// This is taken from CongressDemo.displayLayout.
	// This puts the axes on the right
	public void initializeLayoutBoundsForDisplay() {
		Insets i = getInsets();
		int w = getWidth();
		int h = getHeight();
		int insetWidth = i.left+i.right;
		int insetHeight = i.top+i.bottom;
		int yAxisWidth = 85;
		int xAxisHeight = 10;

		m_dataB.setRect(i.left, i.top, w-insetWidth-yAxisWidth, h-insetHeight-xAxisHeight); 
		m_xlabB.setRect(i.left, h-xAxisHeight-i.bottom, w-insetWidth-yAxisWidth, xAxisHeight);
		m_ylabB.setRect(i.left, i.top, w-insetWidth, h-insetHeight-xAxisHeight);

		m_vis.run("update");
		// m_vis.run("xlabels"); // This didn't seem to do anything, but was in the Congress demo
	}


	/**
	 * @param dataField the name of the column in histoTable to display
	 */
	public void updateAxes(String dataField) {

		// The extra variable defs are probably unneeded, but
		// date from the time I was trying to debug the
		// xaxis labelling bug.  Left in to make future
		// debugging easier.
		AxisLayout xaxis = getXAxis();
		AxisLayout yaxis = getYAxis();
		AxisLabelLayout xlabels = getXLabels();
		AxisLabelLayout ylabels = getYLabels();
		
		xaxis.setScale(Constants.LINEAR_SCALE);
		xaxis.setDataField(dataField);

		xaxis.setDataType(getAxisType(dataField)); 
		xlabels.setRangeModel(null);  // setting to null seems to force a recalc -> redraw
		/* also used trying to debug the axis bug -- KDS
		if(isNumeric(dataField))
		{
			double min, max;
			min = m_histoTable.getBinMin(dataField);
			max =m_histoTable.getBinMax(dataField);
			NumberRangeModel xrangeModel = new NumberRangeModel(min, max, min, max);
			xlabels.setRangeModel(xrangeModel);

		}
		*/
		
		// yaxis is the bin counts, which are always numeric
		String countField = HistogramTable.getCountField(dataField);
		yaxis.setDataField(countField);
		// I could set the range model to null as above, but with histograms,
		// you really want the bars to go from 0 to max, not from min to max. -- KDS
		NumberRangeModel rangeModel = new NumberRangeModel(0, m_histoTable.getCountMax(dataField), 0, m_histoTable.getCountMax(dataField));
		yaxis.setRangeModel(rangeModel);
		ylabels.setRangeModel(rangeModel);
		
		m_vis.run("draw");
	}


	/**
	 * @param dataField the name of a column in histoTable to display
	 * @return isNumeric boolean which says if the column named by dataField 
	 * is int, float, or double or if it is not.  Note that booleans are
	 * treated as non-numerics under this logic.
	 */
	private boolean isNumeric(String dataField) {
		return m_histoTable.getColumn(dataField).canGetDouble();
	}

	/**
	 * @param dataField the name of a column to display
	 * @return the type of the axis (NUMERICAL for numbers and ORDINAL for strings)
	 * Note that HistogramGraph hasn't been tested with boolean or derived fields.
	 * I believe that HistogramTable treats booleans as strings. -- KDS
	 */
	protected int getAxisType(String dataField) {
		if( isNumeric(dataField) ) {
			return Constants.NUMERICAL;
		} else {     // completely untested with derived columns or strings
			return Constants.ORDINAL;
		}
	}

	/**
	 * @param startingField
	 * @return either the input or the first field in the data table
	 */
	protected String getStartingField(String startingField) {
		if(null == startingField)
		{
			startingField = m_histoTable.getColumnName(0);
		}
		return startingField;
	}
	
	// These getters were purely for help debugging the axis problem.
	// As I haven't chased that down completely, I've left them in. -- KDS
	protected AxisLayout getXAxis() {
		return m_xAxis;
	}
	
	protected AxisLayout getYAxis() {
		return m_yAxis;
	}
	
	protected AxisLabelLayout getXLabels() {
		return m_xLabels;
	}
	
	protected AxisLabelLayout getYLabels() {
		return m_yLabels;
	}
	
	protected HistogramTable getHistoTable() {
		return m_histoTable;
	}
	
	
	public static int getDefaultBinCount() {
		return DEFAULT_BIN_COUNT;
	}
	

} // end of class HistogramGraph
