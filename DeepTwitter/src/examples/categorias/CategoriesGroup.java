package examples.categorias;

import java.util.ArrayList;
import java.util.List;


import twitter4j.Status;;

public class CategoriesGroup {
	
	private List<CategoryVinicius> listCategory;
	private int color;
	
	private List<Status> tweets;
	
		
	public CategoriesGroup(int color, List<CategoryVinicius> listCategory){
		this.listCategory = listCategory;
		this.color = color;
		this.tweets = new ArrayList<Status>();
	}


	public List<CategoryVinicius> getListCategory() {
		return listCategory;
	}


	public int getColor() {
		return color;
	}
	
	public void addTweet(Status t){
		this.tweets.add(t);
	}
	
	public List<Status> getTweets(){
		return tweets;
	}
	
	public boolean equalListCategoryWord(List<CategoryVinicius> paramList){
		
		//Coloca para falso o contabilizado
		for(CategoryVinicius c : listCategory){
			c.accounted = false;
		}
		
		//Coloca para falso o contabilizado
		for(CategoryVinicius cw : paramList){
			cw.accounted = false;
		}
		
		//Marca as contabilizacoes se achar igual
		for(CategoryVinicius c : listCategory){
			
			for(CategoryVinicius param : paramList){
				
				if(param.getCategory().equals(c.getCategory())){
					//cw.accounted = true;
					param.accounted = true;
				}
			}
		}
		
		//Verifica se marcou todos do listCategorWord
		for(CategoryVinicius c : listCategory){
			if(c.accounted == false)
				return false;
		}
		
		for(CategoryVinicius cw : paramList){
			if(cw.accounted == false)
				return false;
		}
		
		return true;
		
	}
	
	
	
	
	
	

}
