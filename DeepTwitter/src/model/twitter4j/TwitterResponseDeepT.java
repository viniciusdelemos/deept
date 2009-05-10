package model.twitter4j;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.StatusesType;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import twitter4j.Tweet;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterResponse;
import twitter4j.User;
import twitter4j.org.json.JSONArray;
import twitter4j.org.json.JSONException;
import twitter4j.org.json.JSONObject;

public class TwitterResponseDeepT extends TwitterResponse {

	// User
	UserDeepT userDeepT;

	// Status
	StatusDeepT statusDeepT;

	// Direct message
	DirectMessageDeepT directMessageDeepT;

	// Se pesquisa por search
	Tweet tweet;

	StatusesType statusesType = null;

	public TwitterResponseDeepT(Element elem, Twitter twitter,
			StatusesType statusesType) throws TwitterException {
		
		// DIRECT MESSAGE
		if (statusesType == StatusesType.DIRECT_MESSAGES
				|| statusesType == StatusesType.DIRECT_MESSAGES_RECEIVED
				|| statusesType == StatusesType.DIRECT_MESSAGES_SENT) {

			directMessageDeepT = new DirectMessageDeepT(elem, twitter);

		}

		// Retornam Status com User
		else if (statusesType == StatusesType.FAVORITES
				|| statusesType == StatusesType.REPLIES
				|| statusesType == StatusesType.UPDATES
				|| statusesType == StatusesType.PUBLIC_TIMELINE) {

			statusDeepT = new StatusDeepT(elem, twitter);
			userDeepT = new UserDeepT((Element) elem
					.getElementsByTagName("user").item(0), twitter);

		}

		// Retornam User com status
		else if (statusesType == StatusesType.USER) {

			userDeepT = new UserDeepT(elem, twitter);
			statusDeepT = new StatusDeepT((Element) elem.getElementsByTagName(
					"status").item(0), twitter);

		} else
			throw new TwitterException(
					"Doesn't possible this data, "
							+ "I'm in contructor of TwitterResponseDeepT "
							+ "(Element elem, Twitter twitter, StatusesType statusesType)");

		this.statusesType = statusesType;

	}

	public TwitterResponseDeepT(Tweet tweet, Twitter twitter,
			StatusesType statusesType) throws TwitterException {

		if (statusesType == StatusesType.SEARCH) {

			this.tweet = tweet;
			this.statusesType = statusesType;

		} else
			throw new TwitterException(
					"Doesn't possible this data, "
							+ "I'm in contructor of TwitterResponseDeepT "
							+ "(JSONObject json, Twitter twitter, StatusesType statusesType)");

	}
	
	public UserDeepT getUserDeepT() {
		return userDeepT;
	}

	public StatusDeepT getStatusDeepT() {
		return statusDeepT;
	}

	public DirectMessageDeepT getDirectMessageDeepT() {
		return directMessageDeepT;
	}

	public Tweet getTweet() {
		return tweet;
	}

	public StatusesType getStatusesType() {
		return statusesType;
	}

	static final String[] POSSIBLE_ROOT_NAMES = new String[] { "users",
			"statuses", "direct-messages" };

	public static List<TwitterResponseDeepT> constructTwitterResponseDeepT(
			List<Tweet> tweets, Twitter twitter, StatusesType statusesType)
			throws TwitterException {

		if (tweets.size() > 0) {

			List<TwitterResponseDeepT> tweetsList = new ArrayList<TwitterResponseDeepT>();

			for (Tweet t : tweets) {

				tweetsList.add(new TwitterResponseDeepT(t, twitter,
						statusesType));

			}

			return tweetsList;
		} else
			return new ArrayList<TwitterResponseDeepT>(0);

	}

	public static List<TwitterResponseDeepT> constructTwitterResponseDeepT(
			Document doc, Twitter twitter, StatusesType statusesType)
			throws TwitterException {
		if (TwitterResponse.isRootNodeNilClasses(doc)) {
			return new ArrayList<TwitterResponseDeepT>(0);
		} else {
			try {
				ensureRootNodeNameIs(POSSIBLE_ROOT_NAMES, doc);

				String tag = null;

				if (statusesType == StatusesType.PUBLIC_TIMELINE
						|| statusesType == StatusesType.UPDATES
						|| statusesType == StatusesType.REPLIES
						|| statusesType == StatusesType.FAVORITES) {

					tag = "status";

				} else if (statusesType == StatusesType.DIRECT_MESSAGES
						|| statusesType == StatusesType.DIRECT_MESSAGES_RECEIVED
						|| statusesType == StatusesType.DIRECT_MESSAGES_SENT) {

					tag = "direct_message";

				}
				// Lista de user com status
				else if (statusesType == StatusesType.USER) {
					tag = "user";
				} else {
					throw new TwitterException(
							"Doesn't possible this data, I'm in constructTwitterResponseDeepT of TwitterResponseDeepT");
				}

				NodeList list = doc.getDocumentElement().getElementsByTagName(
						tag);
				int size = list.getLength();
				List<TwitterResponseDeepT> messages = new ArrayList<TwitterResponseDeepT>(
						size);
				for (int i = 0; i < size; i++) {
					Element status = (Element) list.item(i);
					messages.add(new TwitterResponseDeepT(status, twitter,
							statusesType));
				}
				return messages;
			} catch (TwitterException te) {
				if (isRootNodeNilClasses(doc)) {
					return new ArrayList<TwitterResponseDeepT>(0);
				} else {
					throw te;
				}
			}
		}
	}

	@Override
	public String toString() {

		if (statusesType == StatusesType.PUBLIC_TIMELINE
				|| statusesType == StatusesType.UPDATES
				|| statusesType == StatusesType.REPLIES
				|| statusesType == StatusesType.FAVORITES) {

			return statusDeepT + "\n\t" + userDeepT;

		} else if (statusesType == StatusesType.DIRECT_MESSAGES
				|| statusesType == StatusesType.DIRECT_MESSAGES_RECEIVED
				|| statusesType == StatusesType.DIRECT_MESSAGES_SENT) {

			return directMessageDeepT.toString();

		} else if (statusesType == StatusesType.SEARCH) {
			// TODO pensar nisto aki

			return tweet.toString();
		}
		// Lista de user com status
		else {

			return userDeepT + "\n\t" + statusDeepT;

		}

	}

	protected static void ensureRootNodeNameIs(String[] rootNames, Document doc)
			throws TwitterException {
		Element elem = doc.getDocumentElement();
		String actualRootName = elem.getNodeName();
		for (String rootName : rootNames) {
			if (rootName.equals(actualRootName)) {
				return;
			}
		}

		String expected = "";
		for (int i = 0; i < rootNames.length; i++) {
			if (i != 0) {
				expected += " or ";
			}
			expected += rootNames[i];
		}
		throw new TwitterException("Unexpected root node name:"
				+ elem.getNodeName() + ". " + "Expected:" + expected
				+ ". Check Twitter service availability.\n" + toString(elem));
	}

}
