package controller;

import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JTabbedPane;
import model.StatusesType;

public class StatusTabManager {
	private JTabbedPane tabs;    
    private ArrayList<StatusTab> tabArray;
    	
	public StatusTabManager() {    
	    tabArray = new ArrayList<StatusTab>();
	}
	
	public void setTabbedPane(JTabbedPane pane) {
		tabs = pane;
	}
	
	public StatusTab addTab(StatusesType typeOfTab, String name) {
		StatusTab tab = new StatusTab(tabs, typeOfTab, name);
		tabArray.add(tab);
		return tab;
	}
	
	public StatusTab getTab(int index) {
		try{//-1 pois a primeira tab é a do menu e nao esta no arraylist
			return tabArray.get(index-1);			
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public StatusTab getTab(StatusesType type) {
		for(int i = 0; i<tabArray.size(); i++) {
			if(((StatusTab)tabArray.get(i)).getType() == type)
				return tabArray.get(i);
		}
		return null;
	}
	
	public void removeTab(int index) {
		tabs.remove(index);
	}
	
	public void setEnabledAt(int index, boolean  enabled) {
		tabs.setEnabledAt(index, enabled);
	}
	
}
