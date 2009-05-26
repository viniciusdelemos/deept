package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import twitter4j.Status;
import twitter4j.Tweet;
import twitter4j.TwitterResponse;

public class CategoryManager {	
	private Map<String,Category> categories;
	
	private CategoryManager(){
		//construtor private previne chamadas nao autorizadas ao construtor.	
		System.out.println("inside CategoryManager constructor");
		categories = new HashMap<String,Category>();		
		loadTestCategories();
	}
	
	private static class SingletonHolder { 
		private final static CategoryManager INSTANCE = new CategoryManager();
	}

	public static CategoryManager getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	public Category getCategory(String name) {
		return categories.get(name);
	}
	
	public Category addCategory(String name) {
		if(categories.containsKey(name)) return categories.get(name);
		Category c = new Category(name);
		categories.put(name, c);
		return c;
	}
	
	public Category addCategory(String name, List<String> words) {
		Category c = addCategory(name);
		//sobreescreve todas as palavras da categoria
		c.setWords(words);
		return c;
	}
	
	public boolean addWord(String category, String word) {
		Category c = categories.get(category);
		if(c == null) return false;
		return c.addWord(word);		
	}
	
	public void addWords(String category, List<String> words) {
		for(String s : words) {
			addWord(category,s);
		}
	}
	
	public boolean removeWord(String category, String word) {
		Category c = categories.get(category);
		if(c == null) return false;
		return c.removeWord(word);
	}
	
	public Category removeCategory(String name) {
		return categories.remove(name);
	}
	
	public List<Category> getCategories() {
		List<Category> list = new ArrayList<Category>();
		Iterator<String> i = categories.keySet().iterator();
		while(i.hasNext()) {
			list.add(categories.get(i.next()));
		}
		return list;
	}
		
	private void loadTestCategories(){	
		Category c = addCategory("Diversão");
		c.addWord("festa");
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
		c.addWord("óDio");
		c.addWord("alegria");
		c.addWord("trIsteza");
		
		addWord("Diversão", "sair");
		ArrayList<String> testList = new ArrayList<String>();
		testList.add("amo");
		testList.add("alegria"); //ja tem
		testList.add("odeio");
		addWords("Sentimentos", testList);
		
		c = addCategory("Teste");
		c.addWord("I am");
		c.addWord("@");
		c.addWord("is");
		
		c = removeCategory("VaiSerRemovida");
		//System.out.println(c);	
		
		//System.out.println(this.toString());		
	}
	
	public void setCategory(TwitterResponse response) {
		if(response instanceof Status) {
			Status status = (Status)response;
			StringTokenizer tokens = new StringTokenizer(status.getText());
			System.out.println("Status: "+status.getText());

			while(tokens. hasMoreTokens()) {
				System.out.println(tokens.nextToken());
			}
//			for(Category c : getCategories()) {
//				for(String s : c.getWords()) {
//					try{
//						String aux = tokens.nextToken(s);
//						System.out.println("  AUX: "+aux);
//						System.out.println("Category: "+c.getName()+" pela palavra "+s);
//					}
//					catch(NoSuchElementException e) {System.out.println("Nao tem a palavra "+s);}
//				}
//			}
			
//			if(s.getCategory() == null) {
//			//TODO categoriza
//		}
//		else if(getCategory(s.getCategory()).getVersion() != s.getCategoryVersion()) {
//			//categoriza novamente
//		}
		}
		else if(response instanceof Tweet) {
			Tweet t = (Tweet)response;
			//TODO
		}
		else
			throw new IllegalArgumentException("Tipo de objeto inválido para este método. Aceitos: Status, Tweet");
	}
	
	private void categorize(TwitterResponse response) {
		for(Category c : getCategories()) {
			
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(Category x : getCategories()) {
			sb.append(x.getName()+"\n");
			for(String s : x.getWords()) {
				sb.append("  "+s+"\n");
			}
		}
		return sb.toString();
	}

}
