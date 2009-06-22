package model.extensions;
import model.extensions.CustomDateDay;
import model.extensions.CustomDateHours;
import prefuse.data.Table;

public final class StatusesDataTable extends Table {
	
	public enum ColNames {
		//TODO categoria...
		ID, TWEET, SCREEN_NAME, IMAGE_URL, CATEGORIES, HOUR, DAY, FULL_DATE 
	}
	
	public StatusesDataTable() {
		super();
		this.addColumn(ColNames.ID.toString(), String.class);
		this.addColumn(ColNames.SCREEN_NAME.toString(), String.class);
		this.addColumn(ColNames.TWEET.toString(), String.class);
		this.addColumn(ColNames.IMAGE_URL.toString(), String.class);
		this.addColumn(ColNames.CATEGORIES.toString(), String.class);
		this.addColumn(ColNames.HOUR.toString(), CustomDateHours.class);
		this.addColumn(ColNames.DAY.toString(), CustomDateDay.class);
		this.addColumn(ColNames.FULL_DATE.toString(), String.class);
	}
}

