package controller;

import java.awt.Color;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.StringTokenizer;

import model.ChartColor;

import prefuse.visual.VisualItem;

import com.sun.xml.internal.bind.v2.runtime.RuntimeUtil.ToStringAdapter;

import twitter4j.Status;
import twitter4j.Tweet;
import twitter4j.TwitterResponse;

public class CategoryManager {	
	private Map<String,Category> categoriesMap;	
	private TagParser tagParser;
	private int relatedResponsesCount;
	private Paint[] colorArray;
	private int colorIndex;
	
	private CategoryManager(){
		//construtor private previne chamadas nao autorizadas ao construtor.	
		categoriesMap = new HashMap<String,Category>();	
		relatedResponsesCount = 0;
		colorArray = ChartColor.createDefaultPaintArray();
		colorIndex = new Random().nextInt(colorArray.length);
		loadTestCategories();
	}
	
	private static class SingletonHolder { 
		private final static CategoryManager INSTANCE = new CategoryManager();
	}

	public static CategoryManager getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	public Category getCategory(String name) {
		return categoriesMap.get(name);
	}
	
	public Category addCategory(String name) {
		if(categoriesMap.containsKey(name)) return categoriesMap.get(name);
		Category c = new Category(name);
		c.setPaintColor(colorArray[colorIndex]);
		categoriesMap.put(name, c);
		colorIndex++;
		if(colorIndex>=colorArray.length)
			colorIndex = 0;
		return c;
	}
	
	public Category addCategory(String name, List<String> words) {
		Category c = addCategory(name);
		//sobreescreve todas as palavras da categoria
		c.setWords(words);
		return c;
	}
	
	public boolean addWord(String category, String word) {
		Category c = categoriesMap.get(category);
		if(c == null) return false;
		return c.addWord(word);		
	}
	
	public void addWords(String category, List<String> words) {
		for(String s : words) {
			addWord(category,s);
		}
	}
	
	public boolean removeWord(String category, String word) {
		Category c = categoriesMap.get(category);
		if(c == null) return false;
		return c.removeWord(word);
	}
	
	public Category removeCategory(String name) {
		return categoriesMap.remove(name);
	}
	
	public boolean setCategoryName(String oldName, String newName) {
		Category c = categoriesMap.get(oldName);
		if(c!=null) {
			c.setName(newName);
			categoriesMap.put(newName, c);
			categoriesMap.remove(oldName);
			return true;
		}
		return false;
	}
	
	public boolean setCategoryName(Category c, String newName) {
		return setCategoryName(c.getName(),newName);
	}
	
	public List<Category> getCategories() {
		List<Category> list = new ArrayList<Category>();
		Iterator<String> i = categoriesMap.keySet().iterator();
		while(i.hasNext()) {
			list.add(categoriesMap.get(i.next()));
		}
		return list;
	}
		
	private void loadTestCategories(){	
		Category c = addCategory("Divers„o");
		c.addWord("festa");
		c.addWord("festa");
		c.addWord("amigos");
		c.addWord("amigos");
		c.addWord("rir");
		
		c = addCategory("Geek");
		c.addWord("windows");
		c.addWord("linUx");
		c.addWord("mAc");
		
		c = addCategory("VaiSerRemovida");
		c.addWord("lerolero");
		
		c = addCategory("Sentimentos");
		c.addWord("amor");
		c.addWord("ÛDio");
		c.addWord("alegria");
		c.addWord("trIsteza");
		
		addWord("Divers„o", "sair");
		ArrayList<String> testList = new ArrayList<String>();
		testList.add("amo");
		testList.add("alegria"); //ja tem
		testList.add("odeio");
		addWords("Sentimentos", testList);
		
		c = addCategory("Teste");
		c.addWord("I am");
		c.addWord("@");
		c.addWord("is");
		c.addWord("festa");
		c.addWord("RT");
		
		c = removeCategory("VaiSerRemovida");	
		
		//System.out.println(c);			
		//System.out.println(this.toString());		
	}
	
	public void categorizeResponse(TwitterResponse response, VisualItem item) {
		if(response instanceof Status) {
			Status status = (Status)response;
			String text = status.getText();			
			long responseId = status.getId();			
			categorize(responseId,text,item);			
		}
		else if(response instanceof Tweet) {
			Tweet t = (Tweet)response;
			String text = t.getText();			
			long responseId = t.getId();			
			categorize(responseId,text,item);	
		}
		else
			throw new IllegalArgumentException("Tipo de objeto inv·lido para este mÈtodo. Aceitos: Status, Tweet");
	}
	
	public void categorize(long responseId, String text, VisualItem item) {
		for(Category c : getCategories()) {
			for(CategoryWord word : c.getWords())
				if(!word.hasRelatedResponse(responseId)) {
					tagParser = new TagParser(text,word.getName());
					if(tagParser.hasTag()) {
						word.addRelatedResponse(responseId);	
						formatItem(item,c);
						System.out.println("response "+responseId+" categorizada em "+c.getName()+" pela palavra "+word.getName()+" e cor"+c.getColor());
						relatedResponsesCount++;
					}
				}
				else {
					formatItem(item,c);
				}	
		}
		if(relatedResponsesCount>=1000) {
			clearRelatedResponses();
			System.out.println("* cleared related responses");
		}
	}
	
	public void formatItem(VisualItem item, Category c) {
		item.setFillColor(((Color)colorArray[colorIndex]).getRGB());
	}
	
	public String getFormatedText(String startTag, String endTag) {
		if(tagParser!=null)
			return tagParser.getFormatedText(startTag, endTag);
		return null;
	}
	
	public void clearRelatedResponses() {
		for(Category c : getCategories()) 
			for(CategoryWord word : c.getWords())
				word.clearRelatedResponses();
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(Category x : getCategories()) {
			sb.append(x.getName()+"\n");
			for(CategoryWord s : x.getWords()) {
				sb.append("  "+s.getName()+"\n");
			}
		}
		return sb.toString();
	}

}
