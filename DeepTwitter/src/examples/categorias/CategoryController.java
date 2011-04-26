package examples.categorias;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import prefuse.visual.AggregateItem;
import prefuse.visual.NodeItem;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Tweet;
import twitter4j.TwitterException;
import controller.ControllerDeepTwitter;

public class CategoryController {
	
	private AggregateItem users;
	private List<CategoryVinicius> categories;
	private ControllerDeepTwitter controller;
	private CategoryView categoryView;
	
	
	//private GUIMainWindow mainWindow;


	
	public CategoryController(AggregateItem selectedUsers){
		
		//this.mainWindow = mainWindow;
		
		this.users = selectedUsers;
		this.categories = new ArrayList<CategoryVinicius>();
		
		this.controller = ControllerDeepTwitter.getInstance();
		
		this.openCategories();
		
		this.categoryView = new CategoryView(this);
		this.categoryView.setJDialog();
		
		Make makeThread = new Make();
		makeThread.start();
		
	}
	
	class Make extends Thread {
		
		Map<Long, Tweet> tweets = new HashMap<Long, Tweet>();

		//List<Tweet> tweets = new ArrayList<Tweet>();

		public void run() {

			List<String> querys = null;

			try {
				querys = mountSearchs();
			} catch (DeepTwitterException e) {
				// TODO mandar mensagem para usuario dizendo
				// que nao tem ou usuario ou words em categorias
				return;
			}
			
			//Coloca todas querys para serem feitas em thread, assim mais rapido
			// e tratamento fica mais facil
			
			List<DisparaSearchThread> listThreads = new ArrayList<DisparaSearchThread>();
			
			for(String query : querys){
				
				DisparaSearchThread disparaSearchThread = 
					new DisparaSearchThread(query);
				disparaSearchThread.start();
				
				listThreads.add(disparaSearchThread);
			}
			
			boolean threadsRodando = true;
			
			//Verifica se todas threads terminaram de executar
			while(threadsRodando){
				
				boolean threadsTerminaram = true;
				
				for(DisparaSearchThread dst : listThreads){
					if(dst.getState() != Thread.State.TERMINATED){
						threadsTerminaram = false;
						break;
					}
				}
				
				if(threadsTerminaram == true)
					threadsRodando = false;
				
			}
			
			

			List<CategoriesGroup> categoriesGroupGlobal = null;
			
			List<Tweet> listTweets = new ArrayList<Tweet>();
			
			Iterator<Tweet> iterator = tweets.values().iterator();
			
			while(iterator.hasNext()){
				Tweet t = iterator.next();
				listTweets.add(t);
			}

			categoriesGroupGlobal = analizeSearchWithGroups(listTweets);

			for (CategoriesGroup wg : categoriesGroupGlobal) {

				for (Tweet t : wg.getTweets()) {

//					categoryView.addNode(wg.getColor(), t, controller
//							.getUserNameByScreenName(t.getFromUser()));
					System.out.println("Descomentar esta linha");
				}
			}
			
			print(categoriesGroupGlobal);

		}
		
		public synchronized void addTweets(List<Tweet> tw){
			
			for(Tweet t : tw)
				tweets.put(t.getId(), t);
		
		}

		class DisparaSearchThread extends Thread{
			
			private String query;

			public DisparaSearchThread(String query) {
				this.query = query;
			}

			public void run() {
				
				boolean controle = true;
				//enquanto nao tiver feito a consulta, pois pode dar problema
				
				while(controle){
					
					controle = false;
					
					QueryResult queryResult = null;
					
					try{
						queryResult = controller.getTwitter().search(new Query(query));
					} catch (TwitterException e){
						controle = true;
					}
					
					if(queryResult == null)
						controle = true;
					
					if(controle == false)
						addTweets(queryResult.getTweets());
					
				}

			}

		}
	}
	

	/*
	class MakeThread extends Thread {

		public void run() {

			List<String> querys = mountSearchs();
			
			List<WordsGroup> wordsGroupGlobal = null;

			for (String query : querys) {

				List<Tweet> tweets = null;

				try {
					tweets = search(query);
				} catch (TwitterException e) {
					e.printStackTrace();
					// TODO analizar erro, e dependendo mostrar para usuario
				}
				
				List<WordsGroup> wordsGroup = null;

				wordsGroup = analizeSearchWithGroups(tweets);

				for (WordsGroup wg : wordsGroupGlobal) {

					for (Tweet t : wg.getTweets()) {

						categoryView.addNode(wg.getColor(), t, controller
								.getUserNameByScreenName(t.getFromUser()));
						System.out.println(t.getFromUser() + " "
								+ t.getProfileImageUrl());

					}
				}

			}
			
			print(wordsGroupGlobal);
		}

	}
	*/
	



	private List<CategoryVinicius> categoriesOfStatus(String status){
		
		List<CategoryVinicius> categoriesOfStatus = new ArrayList<CategoryVinicius>();
		
		for(CategoryVinicius c : categories){
			
			for(String s : c.getWords()){
				if(status.toLowerCase().contains(s.toLowerCase())
						&& categoriesOfStatus.contains(c) == false){
					categoriesOfStatus.add(c);
				}
			}
			
		}
		
		return categoriesOfStatus;
		
	}
	
	//esse eh o cara
	private List<CategoriesGroup> analizeSearchWithGroups(List<Tweet> tweets){
		
		//Lista de categoria e palavra de todas categorias definidas pelo usuario
		//List<CategoryWord> listCategoriesWordGlobal = listCategoryWord(); 
		
		List<CategoriesGroup> categoriesGroup = new ArrayList<CategoriesGroup>();
		
		//Inicializa color dos grupos de wordsGroup
		int color = 1;
		
		for(Tweet t : tweets){
			
			List<CategoryVinicius> listCategoryStatus = null;
			
			listCategoryStatus =
				categoriesOfStatus(t.getText());
			
			CategoriesGroup wg = null;
			
			try{
				wg = hasWordsGroup(categoriesGroup, listCategoryStatus);
			} catch(DeepTwitterException e){
				System.out.println("PROBLEMAOOOOOOOOOOOO");
				//TODO tratar isto aqui
				e.printStackTrace();
			}
			
			//criar wordGroup para isto
			if(wg == null){
				
				CategoriesGroup newWordsGroup = new CategoriesGroup(color, listCategoryStatus);
				newWordsGroup.addTweet(t);
				color++;
				categoriesGroup.add(newWordsGroup);
			}
			//adicionar no wordGroup
			else{
				wg.addTweet(t);
			}
	
		}
		
		return categoriesGroup;
	}
	
	/**
	 * Se tem algum WordsGroup com a lista de CategoryWord
	 * passada por parâmetro
	 * 
	 * @param wordsGroup os grupos das categorias e palavra
	 * @param listCategoryWord lista das categorias e palavra que deseja-se ver se existe o grupo
	 * @return Retorna o WordsGroup igual que contenha TODAS CategoryWord passada por parametro, se nao tiver retorna null
	 * @throws DeepTwitterException 
	 */
	private CategoriesGroup hasWordsGroup(List<CategoriesGroup> wordsGroup, List<CategoryVinicius> listCategory) throws DeepTwitterException{
		
		if(wordsGroup.size() == 0)
			return null;
		
		if(listCategory.size() == 0)
			throw new DeepTwitterException("Lista de CategoryWord passada vazia");
		
		for(CategoriesGroup woGroup : wordsGroup){
			
			if(woGroup.equalListCategoryWord(listCategory))
				return woGroup;
			
		}
		
		return null;
		
	}
	


	
	/**
	 * Faz pesquisa no TwitterSearch pela query passada por parâmetro. 
	 * 
	 * @return List<Tweet> Lista de Tweets encontrados pela pesquisa.
	 * 
	 * @throws TwitterException
	 */
	private List<Tweet> search(String query) throws TwitterException{
		
		QueryResult queryResult = null;
		
		queryResult = controller.getTwitter().search(new Query(query));
		
		return queryResult.getTweets();
		
	}
	
	/**
	 * Define a query para a ser pesquisada no TwitterSearch.
	 * 
	 * Para isto, consulta as palavras das categorias,
	 * e usuários do grupo.
	 * 
	 * @return String com query a ser pesquisada no TwitterSearch.
	 * @throws DeepTwitterException 
	 */
	private List<String> mountSearchs() throws DeepTwitterException{
		
		List<String> listWords = new ArrayList<String>();
		List<String> listUsers = new ArrayList<String>();
		
		List<String> querys = new ArrayList<String>();
		
		
		//Words of categories
		for(CategoryVinicius c : categories){
			
			for(String  s : c.getWords()){
				if(listWords.contains(s) == false){
					listWords.add(s);
				}
			}
			
		}
		
		//TODO testar se adicionar outro usuario no grupo, ou remover, vai continuar funcionando
		//Name of users at group
		AggregateItem ai = users;
		Iterator<NodeItem> iterator = ai.items();

		while(iterator.hasNext()){
			NodeItem nodeItem = iterator.next();
			String userName = nodeItem.get("screenName").toString();
			if(listUsers.contains(userName) == false)
				listUsers.add(userName);
		}
		
		if(listUsers.size() == 0)
			throw new DeepTwitterException("Nenhuma palavra nas categorias", DeepTwitterException.NENHUM_USUARIO);
		else if(listWords.size() == 0)
			throw new DeepTwitterException("Nenhuma palavra nas categorias", DeepTwitterException.NENHUMA_MENSAGEM);
		
		for(String user : listUsers){
			for(String word : listWords){
				
				String query = word + " from:"+user.trim();
				if(querys.contains(query) == false)
					querys.add(query);
				
			}
		}
		
		return querys;
		
	}
	
	
	

	
	private void openCategories(){
		
		
		CategoryVinicius category1 = new CategoryVinicius("Temporary");
		category1.addWord("medication");
		category1.addWord("notebook");
		//category1.addWord("entendeu");
		
		CategoryVinicius category2 = new CategoryVinicius("FelipeVeiga");
		category2.addWord("Confusion");
		category2.addWord("lost");
		category2.addWord("TwitterFon");
		
		CategoryVinicius category3 = new CategoryVinicius("ericasFish");
		//category3.addWord("without");
		category3.addWord("tweet");
		
		category3.addWord("human");
		category3.addWord("Mummy");
		
		CategoryVinicius category4 = new CategoryVinicius("outra categoria");
		//TODO tratar espacoes em branco nos requests
		category4.addWord("metro");
		category4.addWord("iPhone");
		category4.addWord("reply");
		category4.addWord("human");
		
		CategoryVinicius category5 = new CategoryVinicius("rotta");
		category5.addWord("setzinhos");
		category5.addWord("luxo");
		
		
		categories.add(category1);
		categories.add(category2);
		categories.add(category3);
		categories.add(category4);
		categories.add(category5);
		
	}
	
	private void print(List<CategoriesGroup> wordsGroup){
		
		String print = "";
		
		for(CategoriesGroup wg : wordsGroup){
			
			print += "\n\n CategoriesGroup color:" + wg.getColor();
			
			
			
			for(CategoryVinicius cw : wg.getListCategory()){
				
				print += "\n\t Category:" + cw.getCategory();
				
			}
			
			for(Tweet t : wg.getTweets()){
				
				print += "\n\t Tweet: " + t.getText() + " User:" +t.getFromUser();
				
			}
			
			
		}
		
		System.out.println(print);
		
	}
	
	
//	public CategoryController(){
//	
//	//this.mainWindow = mainWindow;
//	
//	//this.users = selectedUsers;
//	this.categories = new ArrayList<Category>();
//	
//	this.controller = ControllerDeepTwitter.getInstance();
//	
//	this.openCategories();
//	
//	this.categoryView = new CategoryView();
//	this.categoryView.setJDialog();
//	
//}

}
