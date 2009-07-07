package model;

public class Settings {
	
	private int intervalUpdates; 
	private int intervalMentions; 
	private int intervalFavorites; 
	private int intervalDirectMessages;
	private int intervalSearch;
	private int intervalPublicTimeline; 
	private int intervalMostPopularUsers;
	
	private int edgeColor; 
	private int textColor;
	private int mainUserColor; 
	private int searchResultColor; 
	private int friendsColor; 
	private int followersColor; 
	private int friendsAndFollowersColor; 
	private int selectedItemColor; 
	private int nodeStrokeColor;
	
	private int edgeType;
	
	//forces
	private float gravConstant;
	private float minDistance;
	private float theta;
	private float springCoeff;
	private float defaultLength;
	private float drag;
	
	private int updatesToGet;
	
	//TODO devem ser adicionados limites na hora de ler as configuracoes padroes,
	//pois se tiver lixo no xml, vai ler e pode dar problema.
	
	public Settings(float gravConstant, float minDistance, float theta,
			float springCoeff, float defaultLength, float drag) {
		super();
		this.gravConstant = gravConstant;
		this.minDistance = minDistance;
		this.theta = theta;
		this.springCoeff = springCoeff;
		this.defaultLength = defaultLength;
		this.drag = drag;
	}

	public Settings(int intervalUpdates, int intervalMentions,
			int intervalFavorites, int intervalDirectMessages,
			int intervalSearch, int intervalPublicTimeline,
			int intervalMostPopularUsers, int edgeColor, int textColor,
			int mainUserColor, int searchResultColor, int friendsColor,
			int followersColor, int friendsAndFollowersColor,
			int selectedItemColor, int nodeStrokeColor, int edgeType,
			int updatesToGet) {
		super();
		this.intervalUpdates = intervalUpdates;
		this.intervalMentions = intervalMentions;
		this.intervalFavorites = intervalFavorites;
		this.intervalDirectMessages = intervalDirectMessages;
		this.intervalSearch = intervalSearch;
		this.intervalPublicTimeline = intervalPublicTimeline;
		this.intervalMostPopularUsers = intervalMostPopularUsers;
		this.edgeColor = edgeColor;
		this.textColor = textColor;
		this.mainUserColor = mainUserColor;
		this.searchResultColor = searchResultColor;
		this.friendsColor = friendsColor;
		this.followersColor = followersColor;
		this.friendsAndFollowersColor = friendsAndFollowersColor;
		this.selectedItemColor = selectedItemColor;
		this.nodeStrokeColor = nodeStrokeColor;
		this.edgeType = edgeType;
		this.updatesToGet = updatesToGet;
	}





	public int getIntervalUpdates() {
		return intervalUpdates;
	}

	public void setIntervalUpdates(int intervalUpdates) {
		this.intervalUpdates = intervalUpdates;
	}

	public int getIntervalMentions() {
		return intervalMentions;
	}

	public void setIntervalMentions(int intervalMentions) {
		this.intervalMentions = intervalMentions;
	}

	public int getIntervalFavorites() {
		return intervalFavorites;
	}

	public void setIntervalFavorites(int intervalFavorites) {
		this.intervalFavorites = intervalFavorites;
	}

	public int getIntervalDirectMessages() {
		return intervalDirectMessages;
	}

	public void setIntervalDirectMessages(int intervalDirectMessages) {
		this.intervalDirectMessages = intervalDirectMessages;
	}

	public int getIntervalSearch() {
		return intervalSearch;
	}

	public void setIntervalSearch(int intervalSearch) {
		this.intervalSearch = intervalSearch;
	}

	public int getIntervalPublicTimeline() {
		return intervalPublicTimeline;
	}

	public void setIntervalPublicTimeline(int intervalPublicTimeline) {
		this.intervalPublicTimeline = intervalPublicTimeline;
	}

	public int getIntervalMostPopularUsers() {
		return intervalMostPopularUsers;
	}

	public void setIntervalMostPopularUsers(int intervalMostPopularUsers) {
		this.intervalMostPopularUsers = intervalMostPopularUsers;
	}

	public int getEdgeColor() {
		return edgeColor;
	}

	public void setEdgeColor(int edgeColor) {
		this.edgeColor = edgeColor;
	}

	public int getTextColor() {
		return textColor;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public int getMainUserColor() {
		return mainUserColor;
	}

	public void setMainUserColor(int mainUserColor) {
		this.mainUserColor = mainUserColor;
	}

	public int getSearchResultColor() {
		return searchResultColor;
	}

	public void setSearchResultColor(int searchResultColor) {
		this.searchResultColor = searchResultColor;
	}

	public int getFriendsColor() {
		return friendsColor;
	}

	public void setFriendsColor(int friendsColor) {
		this.friendsColor = friendsColor;
	}

	public int getFollowersColor() {
		return followersColor;
	}

	public void setFollowersColor(int followersColor) {
		this.followersColor = followersColor;
	}

	public int getFriendsAndFollowersColor() {
		return friendsAndFollowersColor;
	}

	public void setFriendsAndFollowersColor(int friendsAndFollowersColor) {
		this.friendsAndFollowersColor = friendsAndFollowersColor;
	}

	public int getSelectedItemColor() {
		return selectedItemColor;
	}

	public void setSelectedItemColor(int selectedItemColor) {
		this.selectedItemColor = selectedItemColor;
	}

	public int getNodeStrokeColor() {
		return nodeStrokeColor;
	}

	public void setNodeStrokeColor(int nodeStrokeColor) {
		this.nodeStrokeColor = nodeStrokeColor;
	}

	public int getEdgeType() {
		return edgeType;
	}

	public void setEdgeType(int edgeType) {
		this.edgeType = edgeType;
	}

	public float getGravConstant() {
		return gravConstant;
	}

	public void setGravConstant(float gravConstant) {
		this.gravConstant = gravConstant;
	}

	public float getMinDistance() {
		return minDistance;
	}

	public void setMinDistance(float minDistance) {
		this.minDistance = minDistance;
	}

	public float getTheta() {
		return theta;
	}

	public void setTheta(float theta) {
		this.theta = theta;
	}

	public float getSpringCoeff() {
		return springCoeff;
	}

	public void setSpringCoeff(float springCoeff) {
		this.springCoeff = springCoeff;
	}

	public float getDefaultLength() {
		return defaultLength;
	}

	public void setDefaultLength(float defaultLength) {
		this.defaultLength = defaultLength;
	}

	public float getDrag() {
		return drag;
	}

	public void setDrag(float drag) {
		this.drag = drag;
	}

	public int getUpdatesToGet() {
		return updatesToGet;
	}

	public void setUpdatesToGet(int updatesToGet) {
		this.updatesToGet = updatesToGet;
	}

}
