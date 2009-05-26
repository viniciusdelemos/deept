package controller;

import java.util.ArrayList;
import java.util.List;

public class Category {

	private String name;
	private List<String> words;
	private int version;

	public Category(String name) {
		this.name = name;
		this.words = new ArrayList<String>();
		this.version = 0;
	}

	public boolean addWord(String word) {
		word = word.toLowerCase();
		if (words.contains(word))
			return false;
		else {
			words.add(word);
			version++;
			return true;
		}		
	}
	
	public boolean removeWord(String word) {
		word = word.toLowerCase();
		if (!words.contains(word))
			return false;
		else {
			words.remove(word);
			version++;
			return true;
		}
	}
	
	public String getName() {
		return name;
	}

	public List<String> getWords() {
		return words;
	}

	public void setWords(List<String> words) {
		clearWords();
		for(String s : words) {
			addWord(s.toLowerCase());
		}		
	}
	
	public void clearWords() {
		words.clear();
		version++;
	}
	
	public int getVersion() {
		return version;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(name+" = {");
		for(String s : words) {
			sb.append(s+",");
		}
		sb.append("}");
		return sb.toString();
	}
}
