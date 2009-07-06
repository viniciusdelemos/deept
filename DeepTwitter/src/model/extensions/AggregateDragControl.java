package model.extensions;

import gui.visualizations.NetworkView;

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
import prefuse.visual.AggregateItem;
import prefuse.visual.AggregateTable;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;

/**
 * Interactive drag control that is "aggregate-aware"
 */
public class AggregateDragControl extends ControlAdapter {

	private VisualItem activeItem;
	private NetworkView networkView;
	protected Point2D down = new Point2D.Double();
	protected Point2D temp = new Point2D.Double();
	protected boolean dragged;

	/**
	 * Creates a new drag control that issues repaint requests as an item
	 * is dragged.
	 */
	public AggregateDragControl(NetworkView networkView) {
		this.networkView = networkView;
	}

	/**
	 * @see prefuse.controls.Control#itemEntered(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
	 */
	public void itemEntered(VisualItem item, MouseEvent e) {		
		Display d = (Display)e.getSource();
		if(!(item instanceof EdgeItem))
			d.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		else
			return;
		activeItem = item;
		//if ( !(item instanceof AggregateItem) )
		setFixed(item, true);
		//if(item instanceof EdgeItem) System.out.println(item);
		if(item instanceof NodeItem) {
			NodeItem selectedItem = (NodeItem)item;
			selectedItem.setStroke(new BasicStroke(1.5f));
			selectedItem.setStrokeColor(networkView.getNodeStrokeColor());
			selectedItem.setFillColor(networkView.getSelectedItemColor());

			Iterator<NodeItem> i = selectedItem.neighbors();
			while(i.hasNext()) {
				NodeItem neighbor = i.next();				
				
				int hasEdge = networkView.getEdge(selectedItem.getInt("id"), neighbor.getInt("id"));				
				boolean iAmSource = hasEdge != -1 &&
					networkView.getEdge(hasEdge).getFloat("weight") != -1;
				hasEdge = networkView.getEdge(neighbor.getInt("id"), selectedItem.getInt("id"));
				boolean iAmTarget = hasEdge != -1 &&
					networkView.getEdge(hasEdge).getFloat("weight") != -1;
				
				if(iAmSource && iAmTarget) {
					neighbor.setFillColor(networkView.getFriendsAndFollowersColor());
				}
				else if(iAmSource) {
					neighbor.setFillColor(networkView.getFriendsColor());
				}
				else if(iAmTarget){
					neighbor.setFillColor(networkView.getFollowersColor());
				}
				else
					continue;
				
				neighbor.setStroke(new BasicStroke(1.5f));
				neighbor.setStrokeColor(networkView.getNodeStrokeColor());
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
			networkView.getColorsBack(selectedItem);

			Iterator<NodeItem> i = selectedItem.neighbors();
			while(i.hasNext()) {
				NodeItem neighbor = i.next();        		
				networkView.getColorsBack(neighbor);
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
		AggregateTable at = networkView.getGroups();
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
								networkView.getGroupManager().addToGroup(ni,group);
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