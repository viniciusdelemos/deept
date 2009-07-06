package model;

import java.awt.Paint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.ws.Response;

public class Category {

	private String name;
	private Map<String,Tag> tagsMap;
	private Paint color;

	public Category(String name) {
		this.name = name;
		this.tagsMap = new  HashMap<String,Tag>();
	}

	public boolean addTag(String tag) {
		if (tagsMap.containsKey(tag))
			return false;
		else {
			tagsMap.put(tag,new Tag(tag.toLowerCase()));
			return true;
		}		
	}
	
	public void addTags(List<String> words) {
		for(String s : words) {
			addTag(s);
		}
	}
	
	public boolean removeTag(String word) {
		if (!tagsMap.containsKey(word))
			return false;
		else {
			tagsMap.remove(word);
			return true;
		}
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String n) {
		name = n;
	}

	public Tag[] getTags() {
		return tagsMap.values().toArray(new Tag[0]);
	}

	public void setTags(List<String> words) {
		clearTags();
		for(String s : words) {
			addTag(s);
		}		
	}
	
	public void setPaintColor(Paint p) {
		color = p;
	}
	
	public Paint getColor() {
		return color;
	}
	 
	public void clearTags() {
		tagsMap.clear();
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(name+" = {");
		Iterator<String> i = tagsMap.keySet().iterator();
		while(i.hasNext()) {
			sb.append(i.next()+",");
		}
		sb.append("}");
		return sb.toString();
	}	
}
