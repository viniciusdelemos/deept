package model.extensions;

import java.util.Date;

public class CustomDateHours extends Date{
	public CustomDateHours(long time) {
		super(time);
	}
	@Override
	public String toString() {
		String h = String.valueOf(super.getHours());
		String m = String.valueOf(super.getMinutes());
		StringBuffer sbH = new StringBuffer(h);
		StringBuffer sbM = new StringBuffer(m);
		
		if(h.length()==1) {
			sbH.insert(0,"0");
		}
		if(m.length()==1) {
			sbM.insert(0,"0");
		}
		
		return sbH.toString()+":"+sbM.toString();
	}
}
