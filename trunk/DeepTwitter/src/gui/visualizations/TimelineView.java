package gui.visualizations;

import gui.GUICategoryEditor;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxEditor;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.event.EventListenerList;

import model.Category;
import model.ChartColor;
import model.extensions.CustomDateDay;
import model.extensions.CustomDateHours;
import model.extensions.StatusesDataTable;
import model.extensions.TimelineRenderFactory;
import model.extensions.StatusesDataTable.ColNames;
import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.GroupAction;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataColorAction;
import prefuse.action.assignment.DataShapeAction;
import prefuse.action.filter.VisibilityFilter;
import prefuse.action.layout.AxisLabelLayout;
import prefuse.action.layout.AxisLayout;
import prefuse.controls.Control;
import prefuse.controls.ControlAdapter;
import prefuse.data.Table;
import prefuse.data.expression.AndPredicate;
import prefuse.data.expression.Predicate;
import prefuse.data.expression.parser.ExpressionParser;
import prefuse.data.query.RangeQueryBinding;
import prefuse.data.query.SearchQueryBinding;
import prefuse.render.LabelRenderer;
import prefuse.render.ShapeRenderer;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.util.UpdateListener;
import prefuse.util.ui.JFastLabel;
import prefuse.util.ui.JRangeSlider;
import prefuse.util.ui.JSearchPanel;
import prefuse.util.ui.UILib;
import prefuse.visual.VisualItem;
import prefuse.visual.VisualTable;
import prefuse.visual.sort.ItemSorter;
import profusians.controls.GenericToolTipControl;
import twitter4j.DirectMessage;
import twitter4j.Status;
import twitter4j.Status;
import twitter4j.TwitterResponse;
import controller.CategoryManager;

public class TimelineView extends JPanel {

	private String panelTitle = "Tweets from ";
	private String statusesCountText;
	private int visibleStatuses;
	private JFastLabel labelTotalStatuses = new JFastLabel("Showing "+visibleStatuses+" tweets");
	private JFastLabel labelDetails;
	private boolean categoriesOn, isStatusOrTweet;
	private AndPredicate statusesFilter;
	private RangeQueryBinding  hourQuery, dayQuery;
	private SearchQueryBinding textSearchQuery, screenNameSearchQuery;

	private Visualization m_vis;
	private Display display;
	private Rectangle2D m_dataB = new Rectangle2D.Double();
	private Rectangle2D m_xlabB = new Rectangle2D.Double();
	private Rectangle2D m_ylabB = new Rectangle2D.Double();

	public enum Group {
		TWEETS, X_AXIS, Y_AXIS
	}


	public TimelineView(final List<TwitterResponse> statusesList, boolean isGroup, String userName) {
		super(new BorderLayout());
		panelTitle += userName;
		final Visualization vis = new Visualization();
		m_vis = vis;

		Table sdt = getStatusesDataTable(statusesList);		
		VisualTable vt = vis.addTable(Group.TWEETS.toString(),sdt);	        		       

		final TimelineRenderFactory timelineRenderFactory = new TimelineRenderFactory(isGroup);		
		vis.setRendererFactory(timelineRenderFactory);
		if(isGroup)
			((LabelRenderer)timelineRenderFactory.getDefaultRenderer()).getImageFactory().preloadImages(m_vis.items(Group.TWEETS.toString()),StatusesDataTable.ColNames.IMAGE_URL.toString());

		screenNameSearchQuery = new SearchQueryBinding(vt, ColNames.SCREEN_NAME.toString());
		textSearchQuery = new SearchQueryBinding(vt, ColNames.TWEET.toString());

		AxisLayout xAxis = new AxisLayout(Group.TWEETS.toString(), 
				StatusesDataTable.ColNames.DAY.toString(), Constants.X_AXIS);        
		dayQuery = new RangeQueryBinding(vt,StatusesDataTable.ColNames.DAY.toString());
		xAxis.setRangeModel(dayQuery.getModel());      
		AxisLabelLayout xlabels = new AxisLabelLayout(Group.X_AXIS.toString(), xAxis, m_xlabB, 15);
		//xlabels.setSpacing(5);
		vis.putAction("xlabels", xlabels);

		AxisLayout yAxis = new AxisLayout(Group.TWEETS.toString(), 
				StatusesDataTable.ColNames.HOUR.toString(),	Constants.Y_AXIS);
		hourQuery = new RangeQueryBinding(vt,StatusesDataTable.ColNames.HOUR.toString());
		yAxis.setRangeModel(hourQuery.getModel());      
		AxisLabelLayout ylabels = new AxisLabelLayout(Group.Y_AXIS.toString(), yAxis, m_ylabB);

		xAxis.setLayoutBounds(m_dataB);
		yAxis.setLayoutBounds(m_dataB);

		statusesFilter = new AndPredicate(screenNameSearchQuery.getPredicate());
		statusesFilter.add(textSearchQuery.getPredicate());
		statusesFilter.add(hourQuery.getPredicate());
		statusesFilter.add(dayQuery.getPredicate());
		statusesFilter.add(ExpressionParser.predicate("ID!=NULL"));

		DataColorAction shapeColor = new DataColorAction(Group.TWEETS.toString(), 
				StatusesDataTable.ColNames.HOUR.toString(),
				Constants.NOMINAL, VisualItem.STROKECOLOR,
				new int[] {ChartColor.DARK_BLUE.getRGB()});

		DataShapeAction shape = new DataShapeAction(Group.TWEETS.toString(),
				StatusesDataTable.ColNames.HOUR.toString(),
				new int[] {Constants.SHAPE_ELLIPSE});

		Counter cntr = new Counter(Group.TWEETS.toString());

		ActionList draw = new ActionList();
		draw.add(cntr);
		draw.add(shapeColor);
		draw.add(shape);
		draw.add(xAxis);
		draw.add(yAxis);
		draw.add(ylabels);
		draw.add(new ColorAction(Group.TWEETS.toString(), VisualItem.FILLCOLOR, 0));
		draw.add(new RepaintAction());
		vis.putAction("draw", draw);

		ActionList update = new ActionList();
		update.add(new VisibilityFilter(Group.TWEETS.toString(), statusesFilter));
		update.add(cntr);
		update.add(xAxis);
		update.add(yAxis);
		update.add(ylabels);
		update.add(xlabels);
		update.add(new RepaintAction());
		vis.putAction("update", update);

		UpdateListener lstnr = new UpdateListener() {
			public void update(Object src) {
				vis.run("update");
			}
		};
		statusesFilter.addExpressionListener(lstnr);

		display = new Display(vis);
		display.setItemSorter(new ItemSorter() {
			public int score(VisualItem item) {
				int score = super.score(item);
				if (item.isInGroup(Group.TWEETS.toString())) {
					if(item.getString(StatusesDataTable.ColNames.CATEGORIES.toString())!=null)
						score+=300000;
					else
						score += 200000;//item.getLong(StatusesDataTable.ColNames.ID.toString()); 
				}
				return score;
			}
		});
		display.setBorder(BorderFactory.createEmptyBorder(10,50,50,10));
		display.setSize(700,450);
		display.setHighQuality(true);
		display.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				displayLayout();
			}
		});
		displayLayout();

		labelDetails = new JFastLabel(panelTitle);
		labelDetails.setPreferredSize(new Dimension(300,20));
		labelDetails.setVerticalAlignment(SwingConstants.BOTTOM);

		labelTotalStatuses.setPreferredSize(new Dimension(100,20));
		labelTotalStatuses.setHorizontalAlignment(SwingConstants.RIGHT);
		labelTotalStatuses.setVerticalAlignment(SwingConstants.BOTTOM);

		String descriptions[] = { "User:", "Tweet:", "Categories:","Date:" };
		String data[] = { StatusesDataTable.ColNames.SCREEN_NAME.toString(), StatusesDataTable.ColNames.TWEET.toString(),
				StatusesDataTable.ColNames.CATEGORIES.toString(),
				StatusesDataTable.ColNames.FULL_DATE.toString()};

		GenericToolTipControl toolTipControl = new GenericToolTipControl(descriptions,data,200);

		display.addControlListener(toolTipControl);

		//ToolTipControl ttc = new ToolTipControl("label");
		Control hoverc = new ControlAdapter() {
			public void itemEntered(VisualItem item, MouseEvent evt) {
				if ( item.isInGroup(Group.TWEETS.toString()) ) {
					labelTotalStatuses.setText(item.getString(StatusesDataTable.ColNames.FULL_DATE.toString()));
					if(item.getString(StatusesDataTable.ColNames.CATEGORIES.toString())==null)
						item.setFillColor(item.getStrokeColor());
					item.setStrokeColor(ColorLib.rgb(0,0,0));
					item.setStroke(new BasicStroke(1.5f));
					display.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));	
					item.getVisualization().repaint();
				}
			}
			public void itemExited(VisualItem item, MouseEvent evt) {
				if ( item.isInGroup(Group.TWEETS.toString()) ) {
					labelTotalStatuses.setText(statusesCountText);
					if(item.getString(StatusesDataTable.ColNames.CATEGORIES.toString())==null)
						item.setFillColor(item.getEndFillColor());
					item.setStrokeColor(item.getEndStrokeColor());
					item.setStroke(new BasicStroke(1f));
					display.setCursor(Cursor.getDefaultCursor());	
					item.getVisualization().repaint();
				}
			}
		};
		//m_display.addControlListener(ttc);
		display.addControlListener(hoverc);       

		this.addComponentListener(lstnr);

		Box infoBox = new Box(BoxLayout.X_AXIS);
		infoBox.add(Box.createHorizontalStrut(5));
		infoBox.add(labelDetails);
		infoBox.add(Box.createHorizontalGlue());
		infoBox.add(Box.createHorizontalStrut(5));
		infoBox.add(labelTotalStatuses);
		infoBox.add(Box.createHorizontalStrut(5));

		JSearchPanel screenNameSearcher = screenNameSearchQuery.createSearchPanel();
		screenNameSearcher.setLabelText("User: ");
		screenNameSearcher.setBorder(BorderFactory.createEmptyBorder(5,5,5,0));
		screenNameSearcher.setPreferredSize(new Dimension(150,30));
		JSearchPanel textSearcher = textSearchQuery.createSearchPanel();
		textSearcher.setLabelText("Tweet: ");
		textSearcher.setBorder(BorderFactory.createEmptyBorder(5,5,5,0));
		textSearcher.setPreferredSize(new Dimension(170,30));

		final JRadioButton buttonShapes = new JRadioButton("Forms");        
		final JRadioButton buttonPhotos = new JRadioButton("Photos");

		ButtonGroup bg = new ButtonGroup();
		bg.add(buttonShapes);
		bg.add(buttonPhotos);

		buttonPhotos.setSelected(isGroup);
		buttonShapes.setSelected(!isGroup);        

		buttonShapes.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {				
				if(timelineRenderFactory.getDefaultRenderer() instanceof LabelRenderer) {
					timelineRenderFactory.switchRenderer();
					displayLayout();
				}
			}});
		buttonPhotos.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {				
				if(timelineRenderFactory.getDefaultRenderer() instanceof ShapeRenderer) {					
					timelineRenderFactory.switchRenderer();					
					((LabelRenderer)timelineRenderFactory.getDefaultRenderer()).getImageFactory().preloadImages(m_vis.items(Group.TWEETS.toString()),StatusesDataTable.ColNames.IMAGE_URL.toString());
					displayLayout();
				}
			}}); 

		JButton buttonCategoryManager = new JButton();
		buttonCategoryManager.setToolTipText("Management Categories");
		buttonCategoryManager.setIcon(new ImageIcon(getClass().getResource("/archive.png")));        
		buttonCategoryManager.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				GUICategoryEditor.openFrame();
			}});

		final CategoryManager cManager = CategoryManager.getInstance();
		final JComboBox categoriesComboBox = new JComboBox();
		categoriesComboBox.setMaximumRowCount(4);
		categoriesComboBox.addItem("Show All");
		categoriesComboBox.setVisible(false);
		categoriesComboBox.setRenderer(new ColorCellRenderer());
		categoriesComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {				
				if(categoriesComboBox.getSelectedItem() == null)
					return;				
				for(int i=statusesFilter.size()-1; i>=0; i--) {
					Predicate p = statusesFilter.get(i);
					if(p.toString().contains("CATEGORIES")) {						
						statusesFilter.remove(p);
						break;
					}
				}
				String query;
				if(!(categoriesComboBox.getSelectedItem() instanceof Category))
					query = "CATEGORIES != '#*&E^D'"; //string qualquer para retornar todas
				else
					query = "CATEGORIES = '"+((Category)categoriesComboBox.getSelectedItem()).getName()+"'";				
				statusesFilter.add(ExpressionParser.predicate(query));		
			}});

		final JButton buttonCategorize = new JButton();
		buttonCategorize.setToolTipText("Categorize tweets");
		buttonCategorize.setIcon(new ImageIcon(getClass().getResource("/tag.png")));
		buttonCategorize.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {

				//Classe para colocar em thread a acao de categorizar tweets
				//e desabilitar o botao
				class RunButtonCategorize extends Thread{
					public void run(){
						buttonCategorize.setEnabled(false);
						buttonCategorize.setText("Categorizing...");

						categoriesOn = true;                    		
						TwitterResponse tr;
						long responseId = -1;
						for(int i=0; i<statusesList.size(); i++) {        			
							tr = statusesList.get(i);
							//substituir estes ifs com instance of atraves da classe ResponseDeepT :)
							if(tr instanceof Status) responseId = ((Status)tr).getId();
							else if(tr instanceof Status) responseId = ((Status)tr).getId();

							String expr = "ID='"+responseId+"'";
							Iterator<VisualItem> it = m_vis.items(Group.TWEETS.toString(),expr);
							VisualItem item = null;
							while(it.hasNext()) { //deve retornar apenas 1!
								item = it.next();
								item.set(StatusesDataTable.ColNames.CATEGORIES.toString(), null);
								item.setFillColor(ChartColor.TRANSLUCENT);
								item.setStrokeColor(ChartColor.DARK_BLUE.getRGB());
							}        			
							cManager.categorizeResponse(tr,item);
						}

						categoriesComboBox.removeAllItems();
						categoriesComboBox.addItem("Show All");

						for(Category c : cManager.getUsedCategories()) {
							categoriesComboBox.addItem(c);
						}

						categoriesComboBox.setVisible(true);
						buttonCategorize.setEnabled(true);
						buttonCategorize.setText("");
						displayLayout();
					}
				}        		
				new RunButtonCategorize().start();
			}});

		Box componentsBox = new Box(BoxLayout.X_AXIS);
		componentsBox.add(Box.createHorizontalStrut(5));
		componentsBox.add(screenNameSearcher);
		componentsBox.add(textSearcher);
		componentsBox.add(Box.createHorizontalStrut(15));
		componentsBox.add(buttonShapes);
		componentsBox.add(buttonPhotos);
		componentsBox.add(Box.createHorizontalStrut(15));
		if(isStatusOrTweet) {
			componentsBox.add(buttonCategoryManager);
			componentsBox.add(buttonCategorize);
		}                
		componentsBox.add(Box.createHorizontalStrut(15));
		Box categoriesBox = new Box(BoxLayout.X_AXIS);
		categoriesBox.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		categoriesBox.add(categoriesComboBox);
		componentsBox.add(categoriesBox);
		componentsBox.add(Box.createHorizontalGlue()); 

		JRangeSlider verticalSlider = hourQuery.createVerticalRangeSlider();
		verticalSlider.setThumbColor(ChartColor.LIGHT_GRAY);
		verticalSlider.setMinExtent(hourQuery.getModel().getMinimum());

		JRangeSlider horizontalSlider = dayQuery.createHorizontalRangeSlider();
		horizontalSlider.setThumbColor(ChartColor.LIGHT_GRAY);
		horizontalSlider.setMinExtent(dayQuery.getModel().getMinimum());
		horizontalSlider.setPreferredSize(new Dimension(100,20));
		horizontalSlider.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				display.setHighQuality(false);
			}
			public void mouseReleased(MouseEvent e) {
				display.setHighQuality(true);
				display.repaint();
			}
		});

		vis.run("draw");
		vis.run("xlabels");

		JPanel southPanel = new JPanel(new BorderLayout());
		southPanel.add(display,BorderLayout.CENTER);
		southPanel.add(horizontalSlider,BorderLayout.SOUTH);

		Box rightBox = new Box(BoxLayout.Y_AXIS);
		rightBox.add(verticalSlider);
		rightBox.add(Box.createVerticalStrut(17));

		add(infoBox, BorderLayout.NORTH);
		add(southPanel, BorderLayout.CENTER);
		add(rightBox, BorderLayout.EAST);
		add(componentsBox, BorderLayout.SOUTH);
		UILib.setColor(this, ColorLib.getColor(255,255,255), Color.DARK_GRAY);
		verticalSlider.setForeground(ChartColor.VERY_LIGHT_BLUE);
		horizontalSlider.setForeground(ChartColor.VERY_LIGHT_BLUE);
		UILib.setFont(componentsBox, FontLib.getFont("Tahoma", 12));
		labelDetails.setFont(FontLib.getFont("Tahoma", 18));
		labelTotalStatuses.setFont(FontLib.getFont("Tahoma", 16));        
	}

	public StatusesDataTable getStatusesDataTable(List<TwitterResponse> statusesList) {
		StatusesDataTable tbl = new StatusesDataTable();
		tbl.addRows(statusesList.size()+1);
		int index = 0;		
		CustomDateDay dayAux = null;
		CustomDateHours hourAux = null;

		for (TwitterResponse response : statusesList) {
			try{				
				String text, screenName, profileImageURL;				
				Date date;
				long id = -1;
				if(response instanceof Status) {
					Status s = (Status) response;
					id = s.getId();
					text = s.getText();
					screenName = s.getUser().getScreenName();
					profileImageURL = s.getUser().getProfileImageURL().toString();
					date = s.getCreatedAt();		
					isStatusOrTweet = true;
					System.out.println("timezone: "+s.getUser().getTimeZone());
					System.out.println("offset: "+s.getUser().getUtcOffset());
					System.out.println("hora aqui: "+date.toString());
					System.out.println("hora GMT: "+date.toGMTString()+"\n");
				}
				else if (response instanceof DirectMessage) {
					DirectMessage s = (DirectMessage) response;
					id = s.getId();
					text = s.getText();
					screenName = s.getSender().getScreenName();
					profileImageURL = s.getSender().getProfileImageURL().toString();
					date = s.getCreatedAt();
					isStatusOrTweet = false;
				}
				else if (response instanceof Status) {					
					Status s = (Status) response;
					id = s.getId();
					text = s.getText();
					
					//screenName = s.getFromUser();
					screenName = s.getUser().getScreenName();
					
					//profileImageURL = s.getProfileImageUrl().toString();
					profileImageURL = s.getUser().getProfileImageURL();
					
					date = s.getCreatedAt();
					isStatusOrTweet = true;
				}
				else
					throw new IllegalArgumentException("Objeto inválido dentro da lista");

				tbl.set(index, StatusesDataTable.ColNames.ID.toString(), String.valueOf(id));
				tbl.set(index, StatusesDataTable.ColNames.TWEET.toString(), text);
				tbl.set(index, StatusesDataTable.ColNames.SCREEN_NAME.toString(), screenName);
				tbl.set(index, StatusesDataTable.ColNames.IMAGE_URL.toString(), profileImageURL);
				tbl.set(index, StatusesDataTable.ColNames.CATEGORIES.toString(), null);

				SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");		
				String formatedTime = formatter.format(date);
				Date d = formatter.parse(formatedTime);
				CustomDateHours formatedDate = new CustomDateHours(d.getTime());						
				tbl.set(index, StatusesDataTable.ColNames.HOUR.toString(), formatedDate);

				formatter = new SimpleDateFormat("EEE dd/MM/yyyy");
				formatedTime = formatter.format(date);
				d = formatter.parse(formatedTime);							
				CustomDateDay day = new CustomDateDay(d.getTime());			

				tbl.set(index, StatusesDataTable.ColNames.DAY.toString(), day);
				tbl.set(index, StatusesDataTable.ColNames.FULL_DATE.toString(), day+" "+formatedDate);
				if(index==0) {
					dayAux = day;
					dayAux.setDate(dayAux.getDate()+1);
					hourAux = formatedDate;
					hourAux.setHours(hourAux.getHours()+1);
				}
				index++;		
			}
			catch(Exception e) {
				System.out.println("*****Exception******\n");
				e.printStackTrace();
			}				

			//inserindo linha vazia
			tbl.set(index, StatusesDataTable.ColNames.ID.toString(), null);
			tbl.set(index, StatusesDataTable.ColNames.HOUR.toString(), hourAux);
			tbl.set(index, StatusesDataTable.ColNames.DAY.toString(), dayAux);
			tbl.set(index, StatusesDataTable.ColNames.FULL_DATE.toString(), dayAux+" "+hourAux);
		}
		return tbl;
	}

	public void displayLayout() {
		Insets i = display.getInsets();
		int w = display.getWidth();
		int h = display.getHeight();
		int iw = i.left+i.right;
		int ih = i.top+i.bottom;
		int aw = 45;
		int ah = 15;
		m_dataB.setRect(i.left+20, i.top, w-iw-aw, h-ih-ah);
		m_xlabB.setRect(i.left+20, h-ah-i.bottom, w-iw-aw, ah-10);        
		m_ylabB.setRect(i.left, i.top, w-iw, h-ih-ah);

		m_vis.run("update");
		m_vis.run("xlabels");
	}

	private class Counter extends GroupAction {
		public Counter(String group) {
			super(group);
		}
		public void run(double frac) {
			int cont = 0;
			Iterator<VisualItem> visibleItems = m_vis.visibleItems(m_group);
			while(visibleItems.hasNext()) { 
				VisualItem item = visibleItems.next();
				cont++;
			}
			visibleStatuses = cont;
			if(visibleStatuses == 1)
				statusesCountText = "Showing " + visibleStatuses + " tweet";
			else
				statusesCountText = "Showing " + visibleStatuses + " tweets";
			labelTotalStatuses.setText(statusesCountText);
		}
	}
	private class ColorCellRenderer implements ListCellRenderer {
		protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			JLabel renderer = (JLabel) defaultRenderer
			.getListCellRendererComponent(list, value, index,
					isSelected, cellHasFocus);
			if (value instanceof Category) {
				renderer.setBackground((Color)((Category)value).getColor());
				renderer.setText(((Category)value).getName());
			}
			return renderer;
		}
	}
}