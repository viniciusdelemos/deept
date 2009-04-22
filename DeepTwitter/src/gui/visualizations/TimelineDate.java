package gui.visualizations;

import java.util.Date;

public class TimelineDate extends Date{
	public TimelineDate(long date) {
		super(date);
	}
	@Override
	public String toString() {
		String h = String.valueOf(super.getHours());
		String m = String.valueOf(super.getMinutes());
		if(h.length()==1) {
			h = "0"+h;
		}
		if(m.length()==1) {
			m = "0"+m;
		}
		return h+":"+m;
	}
	
	
}
