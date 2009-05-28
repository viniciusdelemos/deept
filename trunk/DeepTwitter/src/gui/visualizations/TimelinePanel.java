package gui.visualizations;

import gui.visualizations.StatusesDataTable.ColNames;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
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
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;

import model.ChartColor;
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
import twitter4j.Tweet;
import twitter4j.TwitterResponse;
import controller.CategoryManager;

public class TimelinePanel extends JPanel {
       
    private String panelTitle = "Timeline";
    private String statusesCountText;
    private int visibleStatuses;
    private JFastLabel labelTotalStatuses = new JFastLabel(visibleStatuses+" updates");
    private JFastLabel labelDetails;
    private boolean categoriesOn, isStatusOrTweet;
    
    private Visualization m_vis;
    private Display display;
    private Rectangle2D m_dataB = new Rectangle2D.Double();
    private Rectangle2D m_xlabB = new Rectangle2D.Double();
    private Rectangle2D m_ylabB = new Rectangle2D.Double();
    
    public enum Group {
		STATUSES, X_AXIS, Y_AXIS
	}
    
    public TimelinePanel(final List<TwitterResponse> statusesList, boolean isGroup) {
        super(new BorderLayout());
        final Visualization vis = new Visualization();
        m_vis = vis;
        
        Table sdt = getStatusesDataTable(statusesList);		
		VisualTable vt = vis.addTable(Group.STATUSES.toString(),sdt);	        		       
        
		final TimelineRenderFactory timelineRenderFactory = new TimelineRenderFactory(isGroup);		
        vis.setRendererFactory(timelineRenderFactory);
        if(isGroup)
        	((LabelRenderer)timelineRenderFactory.getDefaultRenderer()).getImageFactory().preloadImages(m_vis.items(Group.STATUSES.toString()),StatusesDataTable.ColNames.IMAGE_URL.toString());
        
        SearchQueryBinding screenNameSearchQuery = new SearchQueryBinding(vt, ColNames.SCREEN_NAME.toString());
        SearchQueryBinding textSearchQuery = new SearchQueryBinding(vt, ColNames.STATUS.toString());
        
        AxisLayout xAxis = new AxisLayout(Group.STATUSES.toString(), 
				StatusesDataTable.ColNames.DAY.toString(), Constants.X_AXIS);        
        RangeQueryBinding  dayQuery = new RangeQueryBinding(vt,StatusesDataTable.ColNames.DAY.toString());
        xAxis.setRangeModel(dayQuery.getModel());      
        AxisLabelLayout xlabels = new AxisLabelLayout(Group.X_AXIS.toString(), xAxis, m_xlabB, 15);
        //xlabels.setSpacing(5);
        vis.putAction("xlabels", xlabels);
                
        AxisLayout yAxis = new AxisLayout(Group.STATUSES.toString(), 
				StatusesDataTable.ColNames.HOUR.toString(),	Constants.Y_AXIS);
        RangeQueryBinding  hourQuery = new RangeQueryBinding(vt,StatusesDataTable.ColNames.HOUR.toString());
        yAxis.setRangeModel(hourQuery.getModel());      
        AxisLabelLayout ylabels = new AxisLabelLayout(Group.Y_AXIS.toString(), yAxis, m_ylabB);
        
        xAxis.setLayoutBounds(m_dataB);
        yAxis.setLayoutBounds(m_dataB);
        
        AndPredicate filter = new AndPredicate(screenNameSearchQuery.getPredicate());
        filter.add(textSearchQuery.getPredicate());
        filter.add(hourQuery.getPredicate());
        filter.add(dayQuery.getPredicate());
        
//        int[] palette = new int[] {
//            ColorLib.rgb(150,150,255), ColorLib.rgb(255,150,150),
//            ColorLib.rgb(0,255,0)
//            //CRIAR ARRAY DE CORES REFERENTES AS CATEGORIAS
//        };
        DataColorAction shapeColor = new DataColorAction(Group.STATUSES.toString(), 
				StatusesDataTable.ColNames.HOUR.toString(),
				Constants.NOMINAL, VisualItem.STROKECOLOR,
				new int[] {ChartColor.DARK_BLUE.getRGB()});
                
        DataShapeAction shape = new DataShapeAction(Group.STATUSES.toString(),
				StatusesDataTable.ColNames.HOUR.toString(),
				new int[] {Constants.SHAPE_ELLIPSE});
        
        Counter cntr = new Counter(Group.STATUSES.toString());
        
        ActionList draw = new ActionList();
        draw.add(cntr);
        draw.add(shapeColor);
        draw.add(shape);
        draw.add(xAxis);
        draw.add(yAxis);
        draw.add(ylabels);
        draw.add(new ColorAction(Group.STATUSES.toString(), VisualItem.FILLCOLOR, 0));
        draw.add(new RepaintAction());
        vis.putAction("draw", draw);

        ActionList update = new ActionList();
        update.add(new VisibilityFilter(Group.STATUSES.toString(), filter));
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
        filter.addExpressionListener(lstnr);
                
        display = new Display(vis);
        display.setItemSorter(new ItemSorter() {
            public int score(VisualItem item) {
                int score = super.score(item);
                if (item.isInGroup(Group.STATUSES.toString()))
                    score += 300000000;//item.getLong(StatusesDataTable.ColNames.DATE_MILIS.toString());
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
        labelDetails.setPreferredSize(new Dimension(75,20));
        labelDetails.setVerticalAlignment(SwingConstants.BOTTOM);
        
        labelTotalStatuses.setPreferredSize(new Dimension(500,20));
        labelTotalStatuses.setHorizontalAlignment(SwingConstants.RIGHT);
        labelTotalStatuses.setVerticalAlignment(SwingConstants.BOTTOM);
                
        String descriptions[] = { "Usuário:", "Status:", "Categorias:","Data:" };
        String data[] = { StatusesDataTable.ColNames.SCREEN_NAME.toString(), StatusesDataTable.ColNames.STATUS.toString(),
        		StatusesDataTable.ColNames.CATEGORIES.toString(),
        		StatusesDataTable.ColNames.FULL_DATE.toString()};

    	GenericToolTipControl toolTipControl = new GenericToolTipControl(descriptions,data,200);

    	display.addControlListener(toolTipControl);
        
        //ToolTipControl ttc = new ToolTipControl("label");
        Control hoverc = new ControlAdapter() {
            public void itemEntered(VisualItem item, MouseEvent evt) {
                if ( item.isInGroup(Group.STATUSES.toString()) ) {
                	labelTotalStatuses.setText(item.getString(StatusesDataTable.ColNames.SCREEN_NAME.toString()));
                	if(!categoriesOn)
                		item.setFillColor(item.getStrokeColor());
                	item.setStrokeColor(ColorLib.rgb(0,0,0));
                	item.setStroke(new BasicStroke(1.5f));
                	display.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));	
                	item.getVisualization().repaint();
                }
            }
            public void itemExited(VisualItem item, MouseEvent evt) {
                if ( item.isInGroup(Group.STATUSES.toString()) ) {
                  labelTotalStatuses.setText(statusesCountText);
                  if(!categoriesOn)
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
        textSearcher.setLabelText("Text: ");
        textSearcher.setBorder(BorderFactory.createEmptyBorder(5,5,5,0));
        textSearcher.setPreferredSize(new Dimension(170,30));
        
        final JRadioButton buttonShapes = new JRadioButton("Formas");        
        final JRadioButton buttonPhotos = new JRadioButton("Fotos");
        
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
					((LabelRenderer)timelineRenderFactory.getDefaultRenderer()).getImageFactory().preloadImages(m_vis.items(Group.STATUSES.toString()),StatusesDataTable.ColNames.IMAGE_URL.toString());
					displayLayout();
				}
			}}); 
        
        JButton buttonCategoryManager = new JButton("Gerenciar Categorias");        
        buttonCategoryManager.addActionListener(new ActionListener(){
        	@Override
        	public void actionPerformed(ActionEvent arg0) {				
        		//TODO abrir janela de config de categorias
        	}});
        
        JButton buttonCategorize = new JButton("Categorizar Atualizações");        
        buttonCategorize.addActionListener(new ActionListener(){
        	@Override
        	public void actionPerformed(ActionEvent arg0) {			
        		categoriesOn = true;
        		CategoryManager cManager = CategoryManager.getInstance();
        		System.out.println("Setando Categorias");
        		
        		TwitterResponse tr;
        		long responseId = -1;
        		for(int i=0; i<statusesList.size(); i++) {        			
        			tr = statusesList.get(i);
        			//substituir estes ifs com instance of atraves da classe ResponseDeepT :)
        			if(tr instanceof Status) responseId = ((Status)tr).getId();
        			else if(tr instanceof Tweet) responseId = ((Tweet)tr).getId();
        			
        			String expr = "ID='"+responseId+"'";
        			Iterator<VisualItem> it = m_vis.items(Group.STATUSES.toString(),expr);
        			VisualItem item = null;
    				while(it.hasNext()) { //deve retornar apenas 1!
    					item = it.next();
    				}        			
        			cManager.categorizeResponse(tr,item);
        		}
        		System.out.println("Terminei de setar Categorias!");
        		
				//TODO: exibir combobox com categorias + cor
				displayLayout();
        	}});
        
        Box radioBox = new Box(BoxLayout.X_AXIS);
        radioBox.add(Box.createHorizontalStrut(5));
        radioBox.add(screenNameSearcher);
        radioBox.add(textSearcher);
        radioBox.add(Box.createHorizontalStrut(15));
        radioBox.add(buttonShapes);
        radioBox.add(buttonPhotos);
        radioBox.add(Box.createHorizontalStrut(15));
        if(isStatusOrTweet) {
        	radioBox.add(buttonCategoryManager);
        	radioBox.add(buttonCategorize);
        }
        radioBox.add(Box.createHorizontalGlue()); 
        
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
        add(radioBox, BorderLayout.SOUTH);
        UILib.setColor(this, ColorLib.getColor(255,255,255), Color.DARK_GRAY);
        verticalSlider.setForeground(ChartColor.VERY_LIGHT_BLUE);
        horizontalSlider.setForeground(ChartColor.VERY_LIGHT_BLUE);
        UILib.setFont(radioBox, FontLib.getFont("Tahoma", 12));
        labelDetails.setFont(FontLib.getFont("Tahoma", 18));
        labelTotalStatuses.setFont(FontLib.getFont("Tahoma", 16));
    }
    
    public StatusesDataTable getStatusesDataTable(List<TwitterResponse> statusesList) {
		StatusesDataTable tbl = new StatusesDataTable();
		tbl.addRows(statusesList.size());
		int index = 0;
		
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
				else if (response instanceof Tweet) {					
					Tweet s = (Tweet) response;
					id = s.getId();
					text = s.getText();
					screenName = s.getFromUser();
					profileImageURL = s.getProfileImageUrl().toString();
					date = s.getCreatedAt();
					isStatusOrTweet = true;
				}
				else
					throw new IllegalArgumentException("Objeto inválido dentro da lista");
				
				tbl.set(index, StatusesDataTable.ColNames.ID.toString(), String.valueOf(id));
				tbl.set(index, StatusesDataTable.ColNames.STATUS.toString(), text);
				tbl.set(index, StatusesDataTable.ColNames.SCREEN_NAME.toString(), screenName);
				tbl.set(index, StatusesDataTable.ColNames.IMAGE_URL.toString(), profileImageURL);
				
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
				
				index++;		
			}
			catch(Exception e) {
				System.out.println("*****Exception******\n");
				e.printStackTrace();
			}			
		}
		return tbl;
	}
	
//	private ObjectRangeModel yRangeModel(VisualTable vt) {
//		Object[] array = DataLib.ordinalArray(vt.tuples(), StatusesDataTable.ColNames.HOUR.toString());
//		ArrayList<TimelineDate> axisLabels = new ArrayList<TimelineDate>();
//		
////		TimelineDate first = (TimelineDate)array[0];
////		TimelineDate last = (TimelineDate)array[array.length-1];
////		
////		for(int i = first.getHours(); i<=last.getHours(); i++) {
////			TimelineDate newDate = (TimelineDate)first.clone();
////			newDate.setHours(i);
////			newDate.setMinutes(0);
////			newDate.setSeconds(0);	
////			axisLabels.add(newDate);
////		}
//		for(int i=0; i<array.length; i++) {
//			TimelineDate statusDate = (TimelineDate)array[i];
//			axisLabels.add(statusDate);
//		}
//		
//		return  new ObjectRangeModel(axisLabels.toArray());
//	}
	
//	private ObjectRangeModel xRangeModel(VisualTable vt) {
//		List<Object> labels = new ArrayList<Object>();
//
//		Iterator<Tuple> it = vt.tuples();
//		String latestDay = "";
//		while (it.hasNext()) {
//			Tuple item = it.next();
//			String newDay = item.getString(StatusesDataTable.ColNames.DAY.toString());
//			//System.out.println(newDay);
//			if(!newDay.equals(latestDay)) {
//				labels.add(newDay);
//				latestDay = newDay;
//			}			
//		}				
//
//		// padding range with extra "blank" at head and tail
//		Object[] a = new Object[labels.size()];//+2
//		//a[0] = "";  
//		//a[a.length-1] = "";
//		for (int i = labels.size()-1; i >=0; i--) {//i = 1
//			a[i] = labels.get(i);
//		}
//		
//		// return range model
//		return new ObjectRangeModel(a);  
//	}
    
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
    			statusesCountText = visibleStatuses + " update";
    		else
    			statusesCountText = visibleStatuses + " updates";
    		labelTotalStatuses.setText(statusesCountText);
    	}
    }
}

