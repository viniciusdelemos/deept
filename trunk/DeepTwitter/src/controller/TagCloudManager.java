package controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JLabel;

import model.Category;
import model.JLabelTag;
import model.Tag;

import twitter4j.Status;
import twitter4j.Trend;
import twitter4j.Trends;
import twitter4j.TwitterException;
import twitter4j.TwitterResponse;

public class TagCloudManager {
	
	private ControllerDeepTwitter controller = ControllerDeepTwitter.getInstance();
	private CategoryManager categoryManager = CategoryManager.getInstance();
	
	
	private List<TwitterResponse> statusList;
	
	public TagCloudManager(List<TwitterResponse> statusesList){
		
		this.statusList = statusesList;
	}
	
	
	public Hashtable<String, JLabelTag> makeTagCloud(){
		
		Hashtable<String, Integer> tagsCategories = new Hashtable<String, Integer>();
		Hashtable<String, Integer> tagsTrends = new Hashtable<String, Integer>();
		
		//Put all tags of categories in hashmap
		List<Category> categories = categoryManager.getCategories();
		
		for(Category c : categories){
			
			Tag[] tags = c.getTags();
			
			for(Tag t : tags){
				
				if(tagsCategories.containsKey(t.getName()) == false){
					
					tagsCategories.put(t.getName().toLowerCase().trim(), 0);
					
				}
			}
		}
		
		/** Agora pegando weekly trends*/
		
		List<Trends> trends = null;
		
		try {
			trends = controller.getTwitter().getWeeklyTrends();
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		
		
		for(Trends tr : trends){
			
			Trend[] trend = tr.getTrends();
			
			for(Trend t : trend){
				if(tagsTrends.containsKey(t.getName().toLowerCase().trim()) == false){
					tagsTrends.put(t.getName().toLowerCase().trim(), 0);
				}
			}
			
			
		}
		
		/** Não pega mais current trends*/
		///////////
		
//		//Put current trends in hashmap
//		Trends trends = null;
//		try{
//			trends = controller.getTwitter().getCurrentTrends();
//		}catch(TwitterException e){
//			e.printStackTrace();
//		}
//		
//		Trend[] trend = trends.getTrends();
//		
//		for(Trend t : trend){
//			if(tagsTrends.containsKey(t.getName()) == false){
//				tagsTrends.put(t.getName().toLowerCase().trim(), 0);
//			}
//		}
		
		//Percorrendo todas atualizacoes procurando por alguma tag...
		for(TwitterResponse tr : statusList){
			
			if (tr instanceof Status){
				Status status = (Status)tr;
				
				String[] text = status.getText().split(" ");
				
				for(String word : text){
					if(tagsCategories.containsKey(word.toLowerCase().trim())){
						int score = tagsCategories.get(word.toLowerCase().trim());
						score++;
						tagsCategories.put(word.toLowerCase().trim(), score);
					}
					
					if(tagsTrends.containsKey(word.toLowerCase().trim())){
						int score = tagsTrends.get(word.toLowerCase().trim());
						score++;
						tagsTrends.put(word.toLowerCase().trim(), score);
					}
				}
			}
			else{
				
				System.out.println("VERRRRRRRRRRRRRRRR ISTTTOOOOOOOOOOOOOOOOOOOOO");
			}
		}
		
		List<Map.Entry> listCategories = new ArrayList<Map.Entry>();
		List<Map.Entry> listTrends = new ArrayList<Map.Entry>();
		
		
		//Remove tags com zero ocorrencias
		System.out.println("\nTags Categories");
		List<Map.Entry> listCategoriesAux = new ArrayList<Map.Entry>(tagsCategories.entrySet());
		for(Map.Entry<String, Integer> ls : listCategoriesAux){
			if(ls.getValue() > 0){
				listCategories.add(ls);
				System.out.println("\t"+ls.getKey()+ "\t" + ls.getValue());
			}
		}
		
		System.out.println("\nTags Trends");
		List<Map.Entry> listTrendsAux = new ArrayList<Map.Entry>(tagsTrends.entrySet());
		for(Map.Entry<String, Integer> ls : listTrendsAux){
			if(ls.getValue() > 0){
				listTrends.add(ls);
				System.out.println("\t"+ls.getKey()+ "\t" + ls.getValue());
			}
		}
				
		
		
		//TODO Se tiver 3 ou menos tags, nao precisa fazer o resto!!!
		
		//Construindo tag cloud, valores, tamanhos
		
		
        Collections.sort(listCategories, new Comparator<Map.Entry>() {
            public int compare(Map.Entry e1, Map.Entry e2) {
                Integer i1 = (Integer) e1.getValue();
                Integer i2 = (Integer) e2.getValue();
                return i2.compareTo(i1);
            }
        });
        
        
        Collections.sort(listTrends, new Comparator<Map.Entry>() {
            public int compare(Map.Entry e1, Map.Entry e2) {
                Integer i1 = (Integer) e1.getValue();
                Integer i2 = (Integer) e2.getValue();
                return i2.compareTo(i1);
            }
        });
        
        //Se tivermos todas tags com ocorrencia 1
        
        int first = -1;
        int last = -1;
        
        if(listCategories.size() > 0){
        	first = (Integer) listCategories.get(listCategories.size()-1).getValue();
        	last = (Integer) listCategories.get(0).getValue();
        }
        
        if(listTrends.size() > 0){
        	
        	if((Integer)listTrends.get(0).getValue() < first)
        		first = (Integer)listTrends.get(0).getValue();
        		
        	else if(first == -1)
        		first = (Integer)listTrends.get(0).getValue();
        	
        	if((Integer)listTrends.get(listTrends.size()-1).getValue() > last)
        		last = (Integer)listTrends.get(listTrends.size()-1).getValue();
        		
        	else if(last == -1)
        		last = (Integer)listTrends.get(listTrends.size()-1).getValue();
        	
        }
        
        System.out.println("FIRST: "+first+ " LAST: " +last );
        
        
        // --> Tags <--
        Hashtable<String, JLabelTag> tags = new Hashtable<String, JLabelTag>();

        //adiciona tags de categorias, e calcula tamanho de cada tag, colocando no atributo normalizado! 
        for(Map.Entry<String, Integer> ls : listCategories){
        	
        	if(tags.containsKey(ls.getKey().toLowerCase().trim()) == false){
        		JLabelTag jLabelTag = new JLabelTag(ls.getKey().toLowerCase().trim());
        		//jlaLabelTag.setlink(link) //TODO set link
        		jLabelTag.setScore(ls.getValue());
        		jLabelTag.setType(JLabelTag.typeCategories);
        		
        		jLabelTag.setNormScore(interpolacaoLinear(first, last, 0, 1, ls.getValue()));
        		
        		tags.put(ls.getKey(), jLabelTag);

        	}
        	else{
        		//TODO nunca vai conter, eu acho!
        	}
        }
        
        
        //adiciona tags de trends, e calcula tamanho de cada tag, colocando no atributo normalizado!
        for(Map.Entry<String, Integer> ls : listTrends){
        	
        	if(tags.containsKey(ls.getKey().toLowerCase().trim()) == false){
        		JLabelTag jLabelTag = new JLabelTag(ls.getKey().toLowerCase().trim());
        		//jlaLabelTag.setlink(link) //TODO set link
        		jLabelTag.setScore(ls.getValue());
        		jLabelTag.setType(JLabelTag.typeTrends);
        		
        		jLabelTag.setNormScore(interpolacaoLinear(first, last, 0, 1, ls.getValue()));
        		
        		tags.put(ls.getKey(), jLabelTag);
        	}
        	else{
        		
        		JLabelTag jlLabelTag = tags.get(ls.getKey().toLowerCase().trim());
        		
        		jlLabelTag.setType(JLabelTag.typeBoth);
        		tags.put(ls.getKey(), jlLabelTag);
        		
        		//nao atualizou ocorrencias
        	}
        }
        
        return tags;

	}
	
	private void menos3tags(){
		
	}
	
	private double interpolacaoLinear(double a, double b, double fa, double fb, double x){
	
		return ((b-x)*fa/(b-a)) + ((x-a)*fb/(b-a)); 
		
	}

}
