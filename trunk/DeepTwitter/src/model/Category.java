package model;

import java.util.ArrayList;
import java.util.List;

import com.sun.org.apache.bcel.internal.generic.ACONST_NULL;

public class Category {
	
	boolean accounted = false;

	private String category;
	private List<String> words;

	public Category(String category) {
		this.category = category;
		this.words = new ArrayList<String>();
	}

	public boolean addWord(String word) {

		if (words.contains(word))
			return false;
		else {
			words.add(word);
			return true;
		}
	}
	
	public String getCategory(){
		return category;
	}

	public List<String> getWords() {
		return words;
	}

	public void setWords(List<String> words) {
		this.words = words;
	}
	
	
	
	
	
	
	/*
		List<NodeItem> itens = new ArrayList<NodeItem>();
					
					AggregateItem ai = item;
					Iterator<NodeItem> iterator = ai.items();

					while(iterator.hasNext()){
						NodeItem nodeItem = iterator.next();
						itens.add(nodeItem);
					}
					
					CategoryController categoryController = 
						new CategoryController(itens, mainWindow);
					}
	 */

}
