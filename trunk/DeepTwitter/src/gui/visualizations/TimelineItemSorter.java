package gui.visualizations;

import prefuse.visual.VisualItem;
import prefuse.visual.sort.ItemSorter;

public final class TimelineItemSorter extends ItemSorter {
	
		public TimelineItemSorter() {
			super();
		}
		
		/* (non-Javadoc)
		 * @see prefuse.visual.sort.ItemSorter#score(prefuse.visual.VisualItem)
		 */
		public int score(VisualItem item) {
			int score = super.score(item);
			// axis has score 268435456 by default
						
			// put DATA_POINT on top of LINE
			if (item.isInGroup(TimelinePanel.Group.STATUS.toString()))
				score += 300000000;
			
			return score;
		}
		
	}

