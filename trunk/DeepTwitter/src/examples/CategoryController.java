package examples;

import gui.GUIMainWindow;

import java.util.ArrayList;
import java.util.List;

import controller.ControllerDeepTwitter;

import prefuse.visual.NodeItem;

public class CategoryController {
	
	private List<NodeItem> users;
	private List<Category> categories;
	private ControllerDeepTwitter controller;
	private CategoryView categoryView;
	
	private GUIMainWindow mainWindow;
	
	public CategoryController(List<NodeItem> selectedUsers, GUIMainWindow mainWindow){
		
		this.mainWindow = mainWindow;
		
		this.users = selectedUsers;
		this.categories = new ArrayList<Category>();
		
		this.controller = ControllerDeepTwitter.getInstance();
		
		this.openCategories();
		
		this.categoryView = new CategoryView();
		this.categoryView.setJDialog();
		
	}
	
	public CategoryController(){
		
		//this.mainWindow = mainWindow;
		
		//this.users = selectedUsers;
		this.categories = new ArrayList<Category>();
		
		this.controller = ControllerDeepTwitter.getInstance();
		
		this.openCategories();
		
		this.categoryView = new CategoryView();
		this.categoryView.setJDialog();
		
	}
	
	private void openCategories(){
		
		
		Category category1 = new Category("Diversão");
		category1.addWord("festa");
		category1.addWord("amigos");
		category1.addWord("rir");
		
		Category category2 = new Category("Geek");
		category2.addWord("windows");
		category2.addWord("linux");
		category2.addWord("mac");
		
		Category category3 = new Category("Sentimentos");
		category3.addWord("amor");
		category3.addWord("ódio");
		category3.addWord("alegria");
		category3.addWord("tristeza");
		
		categories.add(category1);
		categories.add(category2);
		categories.add(category3);
		
	}

}
