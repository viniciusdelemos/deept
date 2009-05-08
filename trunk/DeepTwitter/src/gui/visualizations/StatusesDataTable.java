package gui.visualizations;
import model.twitter4j.StatusDeepT;
import prefuse.data.Table;

public final class StatusesDataTable extends Table {
	
	public enum ColNames {
		STATUSDEEPT, SCREEN_NAME, IMAGE_URL, HOUR, DAY 
	}
	
	public StatusesDataTable() {
		super();
		this.addColumn(ColNames.STATUSDEEPT.toString(), StatusDeepT.class);
		this.addColumn(ColNames.SCREEN_NAME.toString(), String.class);
		this.addColumn(ColNames.IMAGE_URL.toString(), String.class);
		this.addColumn(ColNames.HOUR.toString(), CustomDateHours.class);
		this.addColumn(ColNames.DAY.toString(), CustomDateDay.class);
	}
}

