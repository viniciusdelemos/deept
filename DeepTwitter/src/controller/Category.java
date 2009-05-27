package controller;

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
	private Map<String,CategoryWord> words;
	private Paint color;

	public Category(String name) {
		this.name = name;
		this.words = new  HashMap<String,CategoryWord>();
	}

	public boolean addWord(String word) {
		if (words.containsKey(word))
			return false;
		else {
			words.put(word,new CategoryWord(word.toLowerCase()));
			return true;
		}		
	}
	
	public void addWords(List<String> words) {
		for(String s : words) {
			addWord(s);
		}
	}
	
	public boolean removeWord(String word) {
		if (!words.containsKey(word))
			return false;
		else {
			words.remove(word);
			return true;
		}
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String n) {
		name = n;
	}

	public CategoryWord[] getWords() {
		return words.values().toArray(new CategoryWord[0]);
	}

	public void setWords(List<String> words) {
		clearWords();
		for(String s : words) {
			addWord(s);
		}		
	}
	
	public void setPaintColor(Paint p) {
		color = p;
	}
	
	public Paint getColor() {
		return color;
	}
	 
	public void clearWords() {
		words.clear();
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(name+" = {");
		Iterator<String> i = words.keySet().iterator();
		while(i.hasNext()) {
			sb.append(i.next()+",");
		}
		sb.append("}");
		return sb.toString();
	}	
}
