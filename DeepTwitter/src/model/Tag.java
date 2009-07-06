package model;

import java.util.HashSet;
import java.util.Set;

public class Tag {
	private String myName;
	private Set<Long> relatedResponses;
	
	public Tag(String tag) {
		myName = tag;
		relatedResponses = new HashSet<Long>();
	}
	
	public String getName() {
		return myName;
	}
	
	public boolean addRelatedResponse(long responseId) {
		return relatedResponses.add(responseId);
	}
	
	public boolean removeRelatedResponse(long responseId) {
		return relatedResponses.remove(responseId);
	}
	
	public boolean hasRelatedResponse(long responseId) {
		return relatedResponses.contains(responseId);
	}
	
	public Long[] getRelatedResponses() {		
		return relatedResponses.toArray(new Long[0]);
	}
	
	public void clearRelatedResponses() {
		relatedResponses.clear();
	}
}
