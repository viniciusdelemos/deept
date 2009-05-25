package gui.visualizations;
import prefuse.data.Table;

public final class StatusesDataTable extends Table {
	
	public enum ColNames {
		//TODO categoria...
		STATUS, SCREEN_NAME, IMAGE_URL, HOUR, DAY, FULL_DATE 
	}
	
	public StatusesDataTable() {
		super();		
		this.addColumn(ColNames.SCREEN_NAME.toString(), String.class);
		this.addColumn(ColNames.STATUS.toString(), String.class);
		this.addColumn(ColNames.IMAGE_URL.toString(), String.class);
		this.addColumn(ColNames.HOUR.toString(), CustomDateHours.class);
		this.addColumn(ColNames.DAY.toString(), CustomDateDay.class);
		this.addColumn(ColNames.FULL_DATE.toString(), String.class);
	}
}

