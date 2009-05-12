package prefuse.demos2;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JToolBar;

import prefuse.data.Table;
import prefuse.data.io.DataIOException;
import prefuse.data.io.DelimitedTextTableReader;


import prefuse.demos2.HistogramTable;

/**
 * A simple histogram visualization that allows different columns
 * in a data table to be histogramized and displayed on the fly.
 * extended by Kaitlin Duck Sherwood to show histograms.
 * 
 * Kaitlin Duck Sherwood's modifications are granted as is for any
 * commercial or non-commercial use, with or without attribution.
 * The only conditions are that you can't pretend that you wrote them,
 * and that you leave these notices about her authorship in the source.
 * 
 * Possible limitations: I'm not completely sure if the histogram 
 * graph gets cleaned up properly.  I *think* that when I do the 
 * getContentPane().remove() of the histoGraph and toolbar
 * in getToolbar, that that frees them for garbage
 * collection.  However, I have not verified that. 
 * 
 * Known bug: If you use the default data (fisher.iris.txt),
 * and display the "Species Name" (a String column) and then display
 * PetalWidth (a double column), then the first two axis labels use 
 * names from Species Name.  I've looked at this carefully and suspect
 * it is a Prefuse bug.  I could be wrong.
 * 
 * @author <a href="http://jheer.org">jeffrey heer</a>
 * @author <a href="http://webfoot.com/ducky.home.html">Kaitlin Duck Sherwood</a>
 */
public class HistogramFrame extends JFrame {
	Table m_dataTable;
	HistogramTable m_histoTable;
	JToolBar m_toolbar;

	/**
	 * @param dataFileName - the name of a file in CSV format that holds the 
	 * data to be histogrammized.
	 * @param defaultFieldName - the name of the field (column) of the data table 
	 * whose histogram is to be shown in the histogram graph.
	 * @param aBinCount - the number of bins that the histogram should sort data values into
	 */
	public HistogramFrame(String dataFileName, String defaultFieldName, int aBinCount) {
		super("histogram demo");

		m_dataTable = getDataTable(dataFileName);

		int binCount = (aBinCount > 0) ? aBinCount : 50;
		m_histoTable = new HistogramTable(m_dataTable, binCount);
		// histoTable.printWholeTable();  // debug

		HistogramGraph m_histoGraph = new HistogramGraph(m_histoTable, defaultFieldName);
		m_toolbar = getToolbar(m_histoGraph, defaultFieldName, binCount);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().add(m_toolbar, BorderLayout.NORTH);
		getContentPane().add(m_histoGraph, BorderLayout.CENTER);
		pack();
		setVisible(true);

	}

	/**
	 * @param dataFileName - the name of a file in CSV format that holds the 
	 * data to be histogrammized
	 * @return table - a prefuse Table with non-histogrammed data values
	 */
	private Table getDataTable(String dataFileName) {
		Table table = null;
		try {
			table = new DelimitedTextTableReader().readTable(dataFileName);
		} catch ( ArrayIndexOutOfBoundsException e ) {
			e.printStackTrace();
			System.exit(-2);
		} catch ( DataIOException e) {
			System.err.println("Uh-oh, there was some problem with the file "+dataFileName+":");
			System.err.println("	"+e.getLocalizedMessage());
			System.exit(-78);
		}

		return table;
	}

	/**
	 * @param histoGraph - a JComponent showing a graph of the histogram data
	 * @param fieldName - the name of the field (column) of the data table whose 
	 * histogram is to be shown in the histogram graph.
	 * @param binCount - the number of bins that the histogram should sort data values into
	 * @return toolbar - a JToolbar that controls the field to display and the number of bins
	 */
	private JToolBar getToolbar(final HistogramGraph histoGraph, 
			final String fieldName, final int binCount)
	{
		int spacing = 10;

		String[] fieldNames = HistogramTable.getFieldNames(m_dataTable);

		// create toolbar that allows displaying different histograms
		JToolBar toolbar = new JToolBar();
		toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.X_AXIS));
		toolbar.add(Box.createHorizontalStrut(spacing));

		final JComboBox fieldsBox = new JComboBox(fieldNames);
		if(fieldName == null) 
		{
			fieldsBox.setSelectedItem(fieldNames[0]);
		} else {
			fieldsBox.setSelectedItem(fieldName);
		}

		fieldsBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String dataField = (String)fieldsBox.getSelectedItem();
				histoGraph.updateAxes(dataField); 
			}
		});

		toolbar.add(fieldsBox);
		toolbar.add(Box.createHorizontalStrut(2*spacing));

		// I don't have a good reason for giving these particular values.
		// Really this should be an editable combobox, but it wasn't
		// important enough to me to do that.  If you are interested in
		// doing so, please send me the code and I'll fold it in. -- KDS
		toolbar.add(new JLabel("# of bins: "));
		String binCountString = ((Integer)binCount).toString();
		String[] binCountsList = new String[6];
		binCountsList[0] = "3";
		binCountsList[1] = "5";
		binCountsList[2] = "15";
		binCountsList[3] = "40";
		binCountsList[4] = "70";
		binCountsList[5] = "100";
		final JComboBox bcb = new JComboBox(binCountsList);
		if(binCountString == null) 
		{
			bcb.setSelectedItem(binCountsList[2]);
		} else {
			bcb.setSelectedItem(binCountString);
		}
		bcb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String binString = (String)bcb.getSelectedItem();
				int binCount = Integer.parseInt(binString);
				HistogramTable ht = new HistogramTable(m_dataTable, binCount);

				String field = (String)fieldsBox.getSelectedItem();					
				HistogramGraph newHistogramGraph = new HistogramGraph(ht, field);
				getContentPane().remove(histoGraph);
				getContentPane().remove(m_toolbar);
				m_toolbar = getToolbar(newHistogramGraph, field, binCount);
				getContentPane().add(m_toolbar, BorderLayout.NORTH);
				getContentPane().add(newHistogramGraph, BorderLayout.CENTER);

				pack();
				setVisible(true);
			}
		});

		toolbar.add(bcb);
		toolbar.add(Box.createHorizontalStrut(2*spacing));

		return toolbar;
	}



	/**
	 * @param argv - first arg is the file name for the CSV data file, 
	 * second arg is the bin count,
	 * third arg is the name of the field (column) to display first 
	 */
	public static void main(String[] argv) {
		String dataFileName = "/fisher.iris.txt";
		String defaultFieldName = null;
		int binCount = -1;

		// if the file doesn't exist, it will fail later
		if (argv.length > 0) 
		{
			dataFileName = argv[0];
		} 

		if (argv.length > 1) 
		{
			try {
				binCount = Integer.parseInt(argv[1]);
			} catch (NumberFormatException e) {
				System.err.println("Uh-oh.  I don't know how to make an integer out of the argument \""+argv[1]+"\"");
				printUsage();
				System.exit(-5);
			}
		} else {
			binCount = HistogramGraph.getDefaultBinCount();
		}

		// need to check if the field is valid later, after
		// the data table has been created
		if (argv.length > 2) 
		{
			defaultFieldName = argv[2];
		} 

		new HistogramFrame( dataFileName,  defaultFieldName,  binCount);
	}



	public static void printUsage() {
		System.err.println("Usage: HistogramDemo [dataFileName] [binCount]");
	}

}
