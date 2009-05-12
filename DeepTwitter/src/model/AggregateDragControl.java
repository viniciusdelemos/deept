package model;

import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Iterator;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import prefuse.Display;
import prefuse.controls.ControlAdapter;
import prefuse.data.tuple.TupleSet;
import prefuse.visual.AggregateItem;
import prefuse.visual.AggregateTable;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;

/**
 * Interactive drag control that is "aggregate-aware"
 */
public class AggregateDragControl extends ControlAdapter {

    private VisualItem activeItem;
    private GraphicManager gManager;
    protected Point2D down = new Point2D.Double();
    protected Point2D temp = new Point2D.Double();
    protected boolean dragged;
    
    /**
     * Creates a new drag control that issues repaint requests as an item
     * is dragged.
     */
    public AggregateDragControl(GraphicManager gManager) {
    	this.gManager = gManager;
    }
        
    /**
     * @see prefuse.controls.Control#itemEntered(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
     */
    public void itemEntered(VisualItem item, MouseEvent e) {
        Display d = (Display)e.getSource();
        d.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        activeItem = item;
        //if ( !(item instanceof AggregateItem) )
            setFixed(item, true);
        if(item instanceof NodeItem) {
        	NodeItem selectedItem = (NodeItem)item;
        	selectedItem.setStroke(new BasicStroke(1.5f));
        	selectedItem.setStrokeColor(gManager.getNodeStrokeColor());
        	selectedItem.setFillColor(gManager.getSelectedItemColor());
        	
        	Iterator<NodeItem> i = selectedItem.neighbors();
        	while(i.hasNext()) {
        		NodeItem neighbor = i.next();
        		boolean iAmSource = gManager.getEdge(selectedItem.getInt("id"), neighbor.getInt("id")) != -1;
        		boolean iAmTarget = gManager.getEdge(neighbor.getInt("id"), selectedItem.getInt("id")) != -1;
        		
        		neighbor.setStroke(new BasicStroke(1.5f));
        		neighbor.setStrokeColor(gManager.getNodeStrokeColor());
        		
        		if(iAmSource && iAmTarget) {
        			neighbor.setFillColor(gManager.getFriendsAndFollowersColor());
        		}
        		else if(iAmSource) {
        			neighbor.setFillColor(gManager.getFriendsColor());
        		}
        		else {
        			neighbor.setFillColor(gManager.getFollowersColor());
        		}
        	}
        }
    }
    
    /**
     * @see prefuse.controls.Control#itemExited(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
     */
    public void itemExited(VisualItem item, MouseEvent e) {
        if ( activeItem == item ) {
            activeItem = null;
            setFixed(item, false);
        }
        Display d = (Display)e.getSource();
        d.setCursor(Cursor.getDefaultCursor());
        
        if(item instanceof NodeItem) {
        	NodeItem selectedItem = (NodeItem)item;
        	//TODO se item estiver selecionado, voltar a cor de selecao
//        	TupleSet selectedNodes = gManager.getTupleSet(GraphicManager.SELECTED_NODES);
//        	if(!selectedNodes.containsTuple(selectedItem)) {
        		selectedItem.setFillColor(ChartColor.TRANSLUCENT);
        		selectedItem.setStrokeColor(ChartColor.TRANSLUCENT);
//        	}
        	
        	Iterator<NodeItem> i = selectedItem.neighbors();
        	while(i.hasNext()) {
        		NodeItem neighbor = i.next();        		
        		//TODO se item estiver selecionado, voltar a cor de selecao
//        		if(!selectedNodes.containsTuple(selectedItem)) {
        			neighbor.setStrokeColor(ChartColor.TRANSLUCENT);
        			neighbor.setFillColor(ChartColor.TRANSLUCENT);
//        		}
        	}
        }
    }
    
    /**
     * @see prefuse.controls.Control#itemPressed(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
     */
    public void itemPressed(VisualItem item, MouseEvent e) {
    	if (!SwingUtilities.isLeftMouseButton(e)) return;
        dragged = false;        
        Display d = (Display)e.getComponent();
        d.getAbsoluteCoordinate(e.getPoint(), down);
        if ( item instanceof AggregateItem )
            setFixed(item, true);
    }
    
    /**
     * @see prefuse.controls.Control#itemReleased(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
     */
    public void itemReleased(VisualItem item, MouseEvent e) {
        if (!SwingUtilities.isLeftMouseButton(e)) return;
        AggregateTable at = gManager.getGroups();
        if ( dragged ) {
            activeItem = null;
            if(item instanceof NodeItem)
            	for(int i=0; i<at.getRowCount(); i++) {
            		final AggregateItem group = (AggregateItem)at.getItem(i);
            		if(item.getBounds().intersects(group.getBounds()) && item.getInt("groupId")<0) {
            			JPopupMenu menu = new JPopupMenu();
            			JMenuItem addToGroup = new JMenuItem("Adicionar ao grupo");
            			final NodeItem ni = (NodeItem) item;
            			addToGroup.addActionListener(new ActionListener(){
            				@Override
            				public void actionPerformed(ActionEvent arg0) {
            					gManager.addToGroup(ni,group);
            				}});
            			menu.add(addToGroup);
            			menu.show(e.getComponent(), e.getX(), e.getY());
            			break;
            	}
            }            
            setFixed(item, false);
            dragged = false;
        }            
    }
    
    /**
     * @see prefuse.controls.Control#itemDragged(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
     */
    public void itemDragged(VisualItem item, MouseEvent e) {
    	if (!SwingUtilities.isLeftMouseButton(e)) return;
        dragged = true;
        Display d = (Display)e.getComponent();
        d.getAbsoluteCoordinate(e.getPoint(), temp);
        double dx = temp.getX()-down.getX();
        double dy = temp.getY()-down.getY();
        
        move(item, dx, dy);
        
        down.setLocation(temp);
    }

    protected static void setFixed(VisualItem item, boolean fixed) {
        if ( item instanceof AggregateItem ) {
            Iterator items = ((AggregateItem)item).items();
            while ( items.hasNext() ) {
            	setFixed((VisualItem)items.next(), fixed);
            }
        } else {
            item.setFixed(fixed);
        }
    }
    
    protected static void move(VisualItem item, double dx, double dy) {
        if ( item instanceof AggregateItem ) {
            Iterator items = ((AggregateItem)item).items();
            while ( items.hasNext() ) {
                move((VisualItem)items.next(), dx, dy);
            }
        } else {
            double x = item.getX();
            double y = item.getY();
            item.setStartX(x);  item.setStartY(y);
            item.setX(x+dx);    item.setY(y+dy);
            item.setEndX(x+dx); item.setEndY(y+dy);
        }
    }
    
} // end of class AggregateDragControl