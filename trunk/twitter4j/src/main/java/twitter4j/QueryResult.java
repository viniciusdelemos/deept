package twitter4j;

import twitter4j.org.json.JSONObject;
import twitter4j.org.json.JSONArray;
import twitter4j.org.json.JSONException;

import java.util.List;
import java.util.ArrayList;

public class QueryResult extends TwitterResponse {

    private long sinceId;
    private long maxId;
    private String refreshUrl;
    private int resultsPerPage;
    private int total = 0;
    private String warning;
    private double completedIn;
    private int page;
    private String query;
    private List<Tweet> tweets;
    private static final long serialVersionUID = -9059136565234613286L;

    /*package*/ QueryResult(JSONObject json, Twitter twitter) throws TwitterException {
        try {
            sinceId = json.getLong("since_id");
            maxId = json.getLong("max_id");
            try {
                refreshUrl = json.getString("refresh_url");
            } catch (JSONException ignore) {
                // refresh_url could be missing
            }
            resultsPerPage = json.getInt("results_per_page");
            try {
                total = json.getInt("total");
            } catch (JSONException ignore) {
                // total could be missing
            }
            try {
                warning = json.getString("warning");
            } catch (JSONException ignore) {
                // warning could be missing
            }
            completedIn = json.getDouble("completed_in");
            page = json.getInt("page");
            query = json.getString("query");
            JSONArray array = json.getJSONArray("results");
            tweets = new ArrayList<Tweet>(array.length());
            for (int i = 0; i < array.length(); i++) {
                JSONObject tweet = array.getJSONObject(i);
                tweets.add(new Tweet(tweet, twitter));
            }
        } catch (JSONException jsone) {
            throw new TwitterException(jsone.getMessage());
        }
    }

    public long getSinceId() {
        return sinceId;
    }

    public long getMaxId() {
        return maxId;
    }

    public String getRefreshUrl() {
        return refreshUrl;
    }

    public int getResultsPerPage() {
        return resultsPerPage;
    }

    /**
     * returns the number of hits
     * @return number of hits
     */

    public int getTotal() {
        return total;
    }

    public String getWarning() {
        return warning;
    }

    public double getCompletedIn() {
        return completedIn;
    }

    public int getPage() {
        return page;
    }

    public String getQuery() {
        return query;
    }

    public List<Tweet> getTweets() {
        return tweets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QueryResult that = (QueryResult) o;

        if (Double.compare(that.completedIn, completedIn) != 0) return false;
        if (maxId != that.maxId) return false;
        if (page != that.page) return false;
        if (resultsPerPage != that.resultsPerPage) return false;
        if (sinceId != that.sinceId) return false;
        if (total != that.total) return false;
        if (!query.equals(that.query)) return false;
        if (refreshUrl != null ? !refreshUrl.equals(that.refreshUrl) : that.refreshUrl != null)
            return false;
        if (tweets != null ? !tweets.equals(that.tweets) : that.tweets != null)
            return false;
        if (warning != null ? !warning.equals(that.warning) : that.warning != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (sinceId ^ (sinceId >>> 32));
        result = 31 * result + (int) (maxId ^ (maxId >>> 32));
        result = 31 * result + (refreshUrl != null ? refreshUrl.hashCode() : 0);
        result = 31 * result + resultsPerPage;
        result = 31 * result + total;
        result = 31 * result + (warning != null ? warning.hashCode() : 0);
        temp = completedIn != +0.0d ? Double.doubleToLongBits(completedIn) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + page;
        result = 31 * result + query.hashCode();
        result = 31 * result + (tweets != null ? tweets.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "QueryResult{" +
                "sinceId=" + sinceId +
                ", maxId=" + maxId +
                ", refreshUrl='" + refreshUrl + '\'' +
                ", resultsPerPage=" + resultsPerPage +
                ", total=" + total +
                ", warning='" + warning + '\'' +
                ", completedIn=" + completedIn +
                ", page=" + page +
                ", query='" + query + '\'' +
                ", tweets=" + tweets +
                '}';
    }
}
