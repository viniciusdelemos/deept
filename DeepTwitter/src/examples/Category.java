package examples;

import java.util.ArrayList;
import java.util.List;

public class Category {

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
