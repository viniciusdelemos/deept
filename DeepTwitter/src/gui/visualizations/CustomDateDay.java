package gui.visualizations;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomDateDay extends Date{
	
	public CustomDateDay(long time) {
		super(time);
	}
	@Override
	public String toString() {
		String day = String.valueOf(super.getDate());
		String month = String.valueOf(super.getMonth()+1);
		StringBuffer sbD = new StringBuffer(day);
		StringBuffer sbM = new StringBuffer(month);
		
		SimpleDateFormat formatter = new SimpleDateFormat("EEE");		
		String dayOfTheWeek = formatter.format(this)+" ";
		
		if(day.length()==1) {
			sbD.insert(0,"0");
		}
		if(month.length()==1) {
			sbM.insert(0,"0");
		}
		
		sbD.insert(0,dayOfTheWeek);		
		sbD.append("/");
		sbD.append(sbM.toString());
		
		return sbD.toString();
	}	
}
