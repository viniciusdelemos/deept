package gui.visualizations;
import prefuse.data.Table;

public final class StatusesDataTable extends Table {
	
	public enum ColNames {
		TWITTER_ID, SCREEN_NAME, IMAGE_URL, STATUS_ID, DATE, DATE_MILIS, DAY//, HOUR 
	}
	
	public StatusesDataTable() {
		super();
		this.addColumn(ColNames.TWITTER_ID.toString(), String.class);
		this.addColumn(ColNames.SCREEN_NAME.toString(), String.class);
		this.addColumn(ColNames.IMAGE_URL.toString(), String.class);
		this.addColumn(ColNames.STATUS_ID.toString(), String.class);		
		this.addColumn(ColNames.DATE.toString(), TimelineDate.class);
		this.addColumn(ColNames.DAY.toString(), String.class);
	}
}

