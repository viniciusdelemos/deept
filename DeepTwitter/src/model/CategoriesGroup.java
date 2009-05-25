package model;

import java.util.ArrayList;
import java.util.List;


import twitter4j.Tweet;

public class CategoriesGroup {
	
	private List<Category> listCategory;
	private int color;
	
	private List<Tweet> tweets;
	
		
	public CategoriesGroup(int color, List<Category> listCategory){
		this.listCategory = listCategory;
		this.color = color;
		this.tweets = new ArrayList<Tweet>();
	}


	public List<Category> getListCategory() {
		return listCategory;
	}


	public int getColor() {
		return color;
	}
	
	public void addTweet(Tweet t){
		this.tweets.add(t);
	}
	
	public List<Tweet> getTweets(){
		return tweets;
	}
	
	public boolean equalListCategoryWord(List<Category> paramList){
		
		//Coloca para falso o contabilizado
		for(Category c : listCategory){
			c.accounted = false;
		}
		
		//Coloca para falso o contabilizado
		for(Category cw : paramList){
			cw.accounted = false;
		}
		
		//Marca as contabilizacoes se achar igual
		for(Category c : listCategory){
			
			for(Category param : paramList){
				
				if(param.getCategory().equals(c.getCategory())){
					//cw.accounted = true;
					param.accounted = true;
				}
			}
		}
		
		//Verifica se marcou todos do listCategorWord
		for(Category c : listCategory){
			if(c.accounted == false)
				return false;
		}
		
		for(Category cw : paramList){
			if(cw.accounted == false)
				return false;
		}
		
		return true;
		
	}
	
	
	
	
	
	

}
