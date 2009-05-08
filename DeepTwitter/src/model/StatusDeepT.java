package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import twitter4j.DirectMessage;
import twitter4j.ExtendedUser;
import twitter4j.Status;
import twitter4j.Tweet;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterResponse;
import twitter4j.User;

public class StatusDeepT extends Status{

    

    private String inReplyToScreenName;
    
    //para TODOS requests que retornam lista de status (menos PUBLIC TIMELINE)
    private ExtendedUser extendedUser;
    
    //somente para PUBLIC TIMELINE, ou quando consulta os dados através do Tweet
    private User user;
    
  
    /**
     * Usado para pegar informacoes de todos requests que retornem
     * lista de status com um usuário extendido dentro:
     * 
     * statuses dos friends, followers, mentios (replies), 
     * show status, update (nova mensagem), delete mensagem, favoritos 
     */
    public StatusDeepT(Element elem, Twitter twitter) throws TwitterException {
        super();
        ensureRootNodeNameIs("status", elem);
        extendedUser = new ExtendedUser((Element) elem.getElementsByTagName("user").item(0),
                twitter);
        id = getChildLong("id", elem);
        text = getChildText("text", elem);
        source = getChildText("source", elem);
        createdAt = getChildDate("created_at", elem);
        isTruncated = getChildBoolean("truncated", elem);
        inReplyToStatusId = getChildInt("in_reply_to_status_id", elem);
        inReplyToUserId = getChildInt("in_reply_to_user_id", elem);
        isFavorited = getChildBoolean("favorited", elem);
        inReplyToScreenName = getChildText("in_reply_to_screen_name", elem);
    }
    
    
    /**
     * 
     * Deve ser chamado pelo UserDeepT para assim o userDeepT ter statuses
     * neste caso ExtendedUser e User nao irao receber nenhum dado!
     */
	public StatusDeepT(Date createdAt, long id, String text, String source,
			boolean isTruncated, long inReplyToStatusId, int inReplyToUserId,
			boolean isFavorited, String inReplyToScreenName) throws TwitterException{

		this.createdAt = createdAt;
		this.id = id;
		this.text = text;
		this.source = source;
		this.isTruncated = isTruncated;
		this.inReplyToStatusId = inReplyToStatusId;
		this.inReplyToUserId = inReplyToUserId;
		this.isFavorited = isFavorited;
		this.inReplyToScreenName = inReplyToScreenName;
	}
	
//	public <T extends TwitterResponse> StatusDeepT(T status) {
//    	
//    }
	
	/**
	 * Somente para as requisicoes do tipo PUBLIC TIMELINE
	 * uma vez que esta retorna lista de statuses com
	 * usuario simplificado (poucos detalhes)
	 */
	public StatusDeepT(Status status) throws TwitterException{
		
		this.createdAt = status.getCreatedAt();
		this.id = status.getId();
		this.text = status.getText();
		this.source = status.getSource();
		this.isTruncated = status.isTruncated();
		this.inReplyToStatusId = status.getInReplyToStatusId();
		this.inReplyToUserId = status.getInReplyToUserId();
		this.isFavorited = status.isFavorited();
		
		//nao tem esta informacao
		//this.inReplyToScreenName = status.getInReplyToScreenName();
		
		this.user = status.getUser();
	}

	
	//TODO verificar este construtor
	public StatusDeepT(twitter4j.Tweet status) throws TwitterException{
		this.createdAt = status.getCreatedAt();
		this.id = status.getId();
		this.text = status.getText();
		this.source = status.getSource();		
		this.inReplyToUserId = status.getToUserId();
		this.inReplyToScreenName = status.getToUser();
		
		//Antes era UserDeepT, agora user,
		//axo melhor assim, pois UserDeepT tem lista de Status, DirectMessages e outras coisas
		user = new User(status.getFromUserId(), status.getFromUser(),status.getProfileImageUrl());

	}
		
	public Date getCreatedAt() {
		return createdAt;
	}

	public long getId() {
		return id;
	}

	public String getText() {
		return text;
	}

	public String getSource() {
		return source;
	}

	public boolean isTruncated() {
		return isTruncated;
	}

	public long getInReplyToStatusId() {
		return inReplyToStatusId;
	}

	public int getInReplyToUserId() {
		return inReplyToUserId;
	}

	public boolean isFavorited() {
		return isFavorited;
	}

	public String getInReplyToScreenName() {
		return inReplyToScreenName;
	}
	
	public ExtendedUser getUserDeepT() {
		return extendedUser;
	}
	
	
	
	public ExtendedUser getExtendedUser() {
		return extendedUser;
	}


	public User getUser() {
		return user;
	}


	public static List<StatusDeepT> constructStatusesDeepT(Document doc, Twitter twitter)
			throws TwitterException {
		if (isRootNodeNilClasses(doc)) {
			return new ArrayList<StatusDeepT>(0);
		} else {
			try {
				ensureRootNodeNameIs("statuses", doc);
				NodeList list = doc.getDocumentElement().getElementsByTagName(
						"status");
				int size = list.getLength();
				List<StatusDeepT> statuses = new ArrayList<StatusDeepT>(size);
				for (int i = 0; i < size; i++) {
					Element status = (Element) list.item(i);
					statuses.add(new StatusDeepT(status, twitter));
				}
				return statuses;
			} catch (TwitterException te) {
				ensureRootNodeNameIs("nil-classes", doc);
				return new ArrayList<StatusDeepT>(0);
			}
		}
	}
	
	@Override
    public String toString() {
        return "StatusDeepT{" +
                "createdAt=" + createdAt +
                ", id=" + id +
                ", text='" + text + '\'' +
                ", source='" + source + '\'' +
                ", isTruncated=" + isTruncated +
                ", inReplyToStatusId=" + inReplyToStatusId +
                ", inReplyToUserId=" + inReplyToUserId +
                ", isFavorited=" + isFavorited +
                ", inReplyToScreenName=" + inReplyToScreenName +
                ", extendedUser=" + extendedUser +
                ", user=" + user +
                '}';
    }
}
