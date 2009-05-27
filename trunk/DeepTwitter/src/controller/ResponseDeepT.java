package controller;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import model.StatusesType;
import prefuse.data.Node;
import twitter4j.DirectMessage;
import twitter4j.Status;
import twitter4j.Tweet;
import twitter4j.TwitterResponse;
import twitter4j.User;

public class ResponseDeepT {
	private long responseId;
	private String text,screenName;
	private int senderId;
	private Date date;
	private URL profileImageURL;	
	private boolean isStatus, isMessage, isTweet;
	//para status ter mais atributos: replyTo, etc
	//adicionar variavel setando texto para mostrar em negrito atualizacoes categorizadas

	public ResponseDeepT(TwitterResponse response, StatusesType statusesType) {
		if(response instanceof Status) {
			Status s = (Status) response;
			responseId = s.getId();
	//text = processText(s.getText());
			//para poder exibir links
			//TODO s.setText(text);
			screenName = s.getUser().getScreenName();
			profileImageURL = s.getUser().getProfileImageURL();
			senderId = s.getUser().getId();
			date = s.getCreatedAt();			

			//setando no nodo a ultima atualizacao
	//Node userNode = controller.getNode(senderId);
	//if(userNode!=null)
		//synchronized(userNode) {
			//userNode.set("latestStatus", text);
		//}
		}
		else if (response instanceof DirectMessage) {
			DirectMessage dm = (DirectMessage) response;
			responseId = dm.getId();
	//text = processText(dm.getText());
			User u;
			if(statusesType==StatusesType.DIRECT_MESSAGES_SENT)
				u = dm.getRecipient();
			else
				u = dm.getSender();
			screenName = u.getScreenName();
			profileImageURL = u.getProfileImageURL();
			senderId = u.getId();
			date = dm.getCreatedAt();
		}
		else if (response instanceof Tweet) {
			Tweet t = (Tweet) response;
			responseId = t.getId();
	//text = processText(t.getText());
			screenName = t.getFromUser();
			senderId = t.getFromUserId();
			try {
				profileImageURL = new URL(t.getProfileImageUrl());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			date = t.getCreatedAt();
		}
	}
	
	public long getResponseId() {
		return responseId;
	}

	public String getText() {
		return text;
	}

	public String getScreenName() {
		return screenName;
	}

	public int getSenderId() {
		return senderId;
	}

	public Date getDate() {
		return date;
	}

	public URL getProfileImageURL() {
		return profileImageURL;
	}	
}
