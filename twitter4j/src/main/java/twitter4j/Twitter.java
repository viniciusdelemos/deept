/*
Copyright (c) 2007-2009, Yusuke Yamamoto
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of the Yusuke Yamamoto nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY Yusuke Yamamoto ``AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL Yusuke Yamamoto BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package twitter4j;

import twitter4j.http.AccessToken;
import twitter4j.http.HttpClient;
import twitter4j.http.PostParameter;
import twitter4j.http.RequestToken;
import twitter4j.http.Response;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * A java reporesentation of the <a href="http://apiwiki.twitter.com/">Twitter API</a>
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
public class Twitter implements java.io.Serializable {
    protected HttpClient http = null;
    private String baseURL = "http://twitter.com/";
    private String searchBaseURL = "http://search.twitter.com/";
    private String source;

    private boolean usePostForcibly = false;
    private static final int MAX_COUNT = 200;
    private static final long serialVersionUID = -7550633067620779906L;
    /*package*/ static final String VERSION = "2.0.3";

    public Twitter() {
        http = new HttpClient();
        setUserAgent("twitter4j http://yusuke.homeip.net/twitter4j/ /" + VERSION);
        setSource("Twitter4J");
        setClientVersion(VERSION);
        setClientURL("http://yusuke.homeip.net/twitter4j/en/twitter4j-" + VERSION + ".xml");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));

        http.setRequestTokenURL("http://twitter.com/oauth/request_token");
        http.setAuthorizationURL("http://twitter.com/oauth/authorize");
        http.setAccessTokenURL("http://twitter.com/oauth/access_token");
        // ensure userid and password are initialized with system properties
        setUserId(null);
        setPassword(null);
    }

    public Twitter(String baseURL) {
        this();
        this.baseURL = baseURL;
    }

    public Twitter(String id, String password) {
        this();
        setUserId(id);
        setPassword(password);
    }

    public Twitter(String id, String password, String baseURL) {
        this();
        setUserId(id);
        setPassword(password);
        this.baseURL = baseURL;
    }

    /**
     * Sets the User-Agent header. System property -Dtwitter4j.http.userAgent overrides this attribute.
     * @param userAgent UserAgent
     * @since Twitter4J 1.1.8
     */
    public void setUserAgent(String userAgent){
        http.setUserAgent(System.getProperty("twitter4j.http.userAgent", userAgent));
    }

    /**
     *
     * @return UserAgent
     * @since Twitter4J 1.1.8
     */
    public String getUserAgent(){
        return http.getUserAgent();
    }

    /**
     * Sets the X-Twitter-Client-Version header. System property -Dtwitter4j.clientVersion overrides this attribute.
     * @param version client version
     * @since Twitter4J 1.1.8
     */
    public void setClientVersion(String version){
        setRequestHeader("X-Twitter-Client-Version", System.getProperty("twitter4j.clientVersion", version));
    }

    /**
     *
     * @return client version
     * @since Twitter4J 1.1.8
     */
    public String getClientVersion(){
        return http.getRequestHeader("X-Twitter-Client-Version");
    }

    /**
     * Sets the X-Twitter-Client-URL header. System property -Dtwitter4j.clientURL overrides this attribute.
     * @param clientURL client URL
     * @since Twitter4J 1.1.8
     */
    public void setClientURL(String clientURL){
        setRequestHeader("X-Twitter-Client-URL",System.getProperty("twitter4j.clientURL", clientURL));
    }

    /**
     *
     * @return client URL
     * @since Twitter4J 1.1.8
     */
    public String getClientURL(){
        return http.getRequestHeader("X-Twitter-Client-URL");
    }

    /**
     * Sets the base URL
     *
     * @param baseURL String the base URL
     */
    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    /**
     * Returns the base URL
     *
     * @return the base URL
     */
    public String getBaseURL() {
        return this.baseURL;
    }

    /**
     * Sets the search base URL
     *
     * @param searchBaseURL the search base URL
     * @since Twitter4J 1.1.7
     */
    public void setSearchBaseURL(String searchBaseURL) {
        this.searchBaseURL = searchBaseURL;
    }

    /**
     * Returns the search base url
     * @return search base url
     * @since Twitter4J 1.1.7
     */
    public String getSearchBaseURL(){
        return this.searchBaseURL;
    }

    /**
     * Sets the userid
     *
     * @param userId new userid
     */
    public synchronized void setUserId(String userId) {
        http.setUserId(System.getProperty("twitter4j.user", userId));
    }

    /**
     * Returns authenticating userid
     *
     * @return userid
     */
    public String getUserId() {
        return http.getUserId();
    }

    /**
     * Sets the password
     *
     * @param password new password
     */
    public synchronized void setPassword(String password) {
        http.setPassword(System.getProperty("twitter4j.password", password));
    }

    /**
     * Returns authenticating password
     *
     * @return password
     */
    public String getPassword() {
        return http.getPassword();
    }

    /**
     *
     * @param consumerKey OAuth consumer key
     * @param consumerSecret OAuth consumer secret
     * @since Twitter 2.0.0
     */
    public synchronized void setOAuthConsumer(String consumerKey, String consumerSecret){
        this.http.setOAuthConsumer(consumerKey, consumerSecret);
    }

    /**
     * Retrieves a request token
     * @return generated request token.
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter 2.0.0
     * @see <a href="http://apiwiki.twitter.com/OAuth-FAQ">Twitter API Wiki - OAuth FAQ</a>
     * @see <a href="http://oauth.net/core/1.0/#auth_step1">OAuth Core 1.0 - 6.1.  Obtaining an Unauthorized Request Token</a>
     */
    public RequestToken getOAuthRequestToken() throws TwitterException {
        return http.getOAuthRequestToken();
    }

    /**
     * Retrieves an access token assosiated with the supplied request token.
     * @param requestToken the request token
     * @return access token associsted with the supplied request token.
     * @throws TwitterException when Twitter service or network is unavailable, or the user has not authorized
     * @see <a href="http://apiwiki.twitter.com/OAuth-FAQ#Howlongdoesanaccesstokenlast">Twitter API Wiki - How long does an access token last?</a>
     * @see <a href="http://oauth.net/core/1.0/#auth_step2">OAuth Core 1.0 - 6.2.  Obtaining User Authorization</a>
     * @since Twitter 2.0.0
     */
    public synchronized AccessToken getOAuthAccessToken(RequestToken requestToken) throws TwitterException {
        return http.getOAuthAccessToken(requestToken);
    }

    /**
     * Retrieves an access token assosiated with the supplied request token.
     * @param token request token
     * @param tokenSecret request token secret
     * @return access token associsted with the supplied request token.
     * @throws TwitterException when Twitter service or network is unavailable, or the user has not authorized
     * @see <a href="http://apiwiki.twitter.com/OAuth-FAQ#Howlongdoesanaccesstokenlast">Twitter API Wiki - How long does an access token last?</a>
     * @see <a href="http://oauth.net/core/1.0/#auth_step2">OAuth Core 1.0 - 6.2.  Obtaining User Authorization</a>
     * @since Twitter 2.0.1
     */
    public synchronized AccessToken getOAuthAccessToken(String token, String tokenSecret) throws TwitterException {
        return http.getOAuthAccessToken(token, tokenSecret);
    }

    /**
     * Sets the access token
     * @param accessToken accessToken
     * @since Twitter 2.0.0
     */
    public void setOAuthAccessToken(AccessToken accessToken){
        this.http.setOAuthAccessToken(accessToken);
    }

    /**
     * Sets the access token
     * @param token token
     * @param tokenSecret token secret
     * @since Twitter 2.0.0
     */
    public void setOAuthAccessToken(String token, String tokenSecret) {
        setOAuthAccessToken(new AccessToken(token, tokenSecret));
    }


    /**
     * Enables use of HTTP proxy
     *
     * @param proxyHost proxy host, can be overridden system property -Dtwitter4j.http.proxyHost , -Dhttp.proxyHost
     * @param proxyPort proxy port, can be overridden system property -Dtwitter4j.http.proxyPort , -Dhttp.proxyPort
     * @since Twitter4J 1.1.6
     */
    public void setHttpProxy(String proxyHost, int proxyPort) {
        http.setProxyHost(proxyHost);
        http.setProxyPort(proxyPort);
    }

    /**
     * Adds authentication on HTTP proxy
     *
     * @param proxyUser proxy user, can be overridden system property -Dtwitter4j.http.proxyUser
     * @param proxyPass proxy password, can be overridden system property -Dtwitter4j.http.proxyPassword
     * @since Twitter4J 1.1.6
     */
    public void setHttpProxyAuth(String proxyUser, String proxyPass) {
        http.setProxyAuthUser(proxyUser);
        http.setProxyAuthPassword(proxyPass);
    }

    /**
     * Sets a specified timeout value, in milliseconds, to be used when opening a communications link to the Twitter API.
     * System property -Dtwitter4j.http.connectionTimeout overrides this attribute.
     *
     * @param connectionTimeout an int that specifies the connect timeout value in milliseconds
     * @since Twitter4J 1.1.6
     */
    public void setHttpConnectionTimeout(int connectionTimeout) {
        http.setConnectionTimeout(connectionTimeout);
    }

    /**
     * Sets the read timeout to a specified timeout, in milliseconds.
     *
     * @param readTimeout an int that specifies the timeout value to be used in milliseconds
     * @since Twitter4J 1.1.6
     */
    public void setHttpReadTimeout(int readTimeout) {
        http.setReadTimeout(readTimeout);
    }

    /**
     * Sets X-Twitter-Client http header and the source parameter that will be passed by updating methods. System property -Dtwitter4j.source overrides this attribute.
     * System property -Dtwitter4j.source overrides this attribute.
     *
     * @param source the new source
     * @see <a href='http://apiwiki.twitter.com/FAQ#HowdoIget“fromMyApp”appendedtoupdatessentfrommyAPIapplication'>How do I get "from [MyApp]" appended to updates sent from my API application?</a>
     * @see <a href="http://twitter.com/help/request_source">Twitter - Request a link to your application</a>
     */
    public void setSource(String source) {
        this.source = System.getProperty("twitter4j.source", source);
        setRequestHeader("X-Twitter-Client", this.source);
    }

    /**
     * Returns the source
     *
     * @return source
     */
    public String getSource() {
        return this.source;
    }

    /**
     * Sets the request header name/value combination
     * see Twitter Fan Wiki for detail.
     * http://twitter.pbwiki.com/API-Docs#RequestHeaders
     *
     * @param name  the name of the request header
     * @param value the value of the request header
     */
    public void setRequestHeader(String name, String value) {
        http.setRequestHeader(name, value);
    }

    /**
     * Set true to force using POST method communicating to the server
     *
     * @param forceUsePost if true POST method will be used forcibly
     * @deprecated some methods don't accept POST method anymore
     */
    public void forceUsePost(boolean forceUsePost) {
        this.usePostForcibly = forceUsePost;
    }

    /**
     * @return true if POST is used forcibly
     */
    public boolean isUsePostForced() {
        return this.usePostForcibly;
    }

    /**
     * Issues an HTTP GET request. POST method will be used instead in case forceUsePost is set true.
     *
     * @param url          the request url
     * @param authenticate if true, the request will be sent with BASIC authentication header
     * @return the response
     * @throws TwitterException when Twitter service or network is unavailable
     */

    private Response get(String url, boolean authenticate) throws TwitterException {
        return get(url, null, authenticate);
    }

    /**
     * Issues an HTTP GET request. POST method will be used instead in case forceUsePost is set true.
     *
     * @param url          the request url
     * @param authenticate if true, the request will be sent with BASIC authentication header
     * @param name1        the name of the first parameter
     * @param value1       the value of the first parameter
     * @return the response
     * @throws TwitterException when Twitter service or network is unavailable
     */

    protected Response get(String url, String name1, String value1, boolean authenticate) throws TwitterException {
        return get(url, new PostParameter[]{new PostParameter(name1, value1)}, authenticate);
    }

    /**
     * Issues an HTTP GET request. POST method will be used instead in case forceUsePost is set true.
     *
     * @param url          the request url
     * @param name1        the name of the first parameter
     * @param value1       the value of the first parameter
     * @param name2        the name of the second parameter
     * @param value2       the value of the second parameter
     * @param authenticate if true, the request will be sent with BASIC authentication header
     * @return the response
     * @throws TwitterException when Twitter service or network is unavailable
     */

    protected Response get(String url, String name1, String value1, String name2, String value2, boolean authenticate) throws TwitterException {
        return get(url, new PostParameter[]{new PostParameter(name1, value1), new PostParameter(name2, value2)}, authenticate);
    }

    /**
     * Issues an HTTP GET request. POST method will be used instead in case forceUsePost is set true.
     *
     * @param url          the request url
     * @param params       the request parameters
     * @param authenticate if true, the request will be sent with BASIC authentication header
     * @return the response
     * @throws TwitterException when Twitter service or network is unavailable
     */
    protected Response get(String url, PostParameter[] params, boolean authenticate) throws TwitterException {
        if (usePostForcibly) {
            if (null == params) {
                return http.post(url, new PostParameter[0], authenticate);
            } else {
                return http.post(url, params, authenticate);
            }
        } else {
            if (null != params && params.length > 0) {
                url += "?" + HttpClient.encodeParameters(params);
            }
            return http.get(url, authenticate);
        }
    }

    /**
     * Issues an HTTP GET request. POST method will be used instead in case forceUsePost is set true.
     *
     * @param url          the request url
     * @param params       the request parameters
     * @param paging controls pagination
     * @param authenticate if true, the request will be sent with BASIC authentication header
     * @return the response
     * @throws TwitterException when Twitter service or network is unavailable
     */
    protected Response get(String url, PostParameter[] params, Paging paging, boolean authenticate) throws TwitterException {
        if (null != paging) {
            List<PostParameter> pagingParams = new ArrayList<PostParameter>(4);
            if (-1 != paging.getCount()) {
                pagingParams.add(new PostParameter("count", String.valueOf(paging.getCount())));
            }
            if (-1 != paging.getMaxId()) {
                pagingParams.add(new PostParameter("max_id", String.valueOf(paging.getMaxId())));
            }
            if (-1 != paging.getSinceId()) {
                pagingParams.add(new PostParameter("since_id", String.valueOf(paging.getSinceId())));
            }
            if (-1 != paging.getPage()) {
                pagingParams.add(new PostParameter("page", String.valueOf(paging.getPage())));
            }
            if (-1 != paging.getSinceId()) {
                if (-1 != url.indexOf("search")) {
                    // search api takes "rpp"
                    // http://apiwiki.twitter.com/Twitter-Search-API-Method%3A-search
                    pagingParams.add(new PostParameter("rpp", String.valueOf(paging.getCount())));
                } else {
                    pagingParams.add(new PostParameter("count", String.valueOf(paging.getCount())));
                }
            }
            PostParameter[] newparams = null;
            PostParameter[] arrayPagingParams = pagingParams.toArray(new PostParameter[pagingParams.size()]);
            if (null != params) {
                newparams = new PostParameter[params.length + pagingParams.size()];
                System.arraycopy(params, 0, newparams, 0, params.length);
                System.arraycopy(arrayPagingParams, 0, newparams, params.length, pagingParams.size());
            } else {
                if (0 != arrayPagingParams.length) {
                    String encodedParams = HttpClient.encodeParameters(arrayPagingParams);
                    if (-1 != url.indexOf("?")) {
                        url += "&" + encodedParams;
                    } else {
                        url += "?" + encodedParams;
                    }
                }
            }
            return get(url, newparams, authenticate);
        } else {
            return get(url, params, authenticate);
        }
    }

    /**
     * Returns tweets that match a specified query.
     * <br>This method calls http://search.twitter.com/search
     * @param query - the search condition
     * @return the result
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 1.1.7
     * @see <a href="http://apiwiki.twitter.com/Twitter-Search-API-Method%3A-search">Twitter API Wiki / Twitter Search API Method: search</a>
     */
    public QueryResult search(Query query) throws TwitterException {
        return new QueryResult(get(searchBaseURL + "search.json", query.asPostParameters(), false), this);
    }
    
    /**
     * Returns the top ten topics that are currently trending on Twitter.  The response includes the time of the request, the name of each trend, and the url to the <a href="http://search.twitter.com/">Twitter Search</a> results page for that topic.
     * <br>This method calls http://search.twitter.com/trends
     * @return the result
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.2
     * @see <a href="http://apiwiki.twitter.com/Twitter-Search-API-Method%3A-trends">Twitter Search API Method: trends</a>
     */
    public Trends getTrends() throws TwitterException {
        return Trends.constructTrends(get(searchBaseURL + "trends.json", false));
    }

    /**
     * Returns the current top 10 trending topics on Twitter.  The response includes the time of the request, the name of each trending topic, and query used on <a href="http://search.twitter.com/">Twitter Search</a> results page for that topic.
     * <br>This method calls http://search.twitter.com/trends/current
     * @return the result
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.2
     * @see <a href="http://apiwiki.twitter.com/Twitter-Search-API-Method%3A-trends">Twitter Search API Method: trends</a>
     */
    public Trends getCurrentTrends() throws TwitterException {
        return Trends.constructTrendsList(get(searchBaseURL + "trends/current.json"
                , false)).get(0);
    }

    /**
     * Returns the current top 10 trending topics on Twitter.  The response includes the time of the request, the name of each trending topic, and query used on <a href="http://search.twitter.com/">Twitter Search</a> results page for that topic.
     * <br>This method calls http://search.twitter.com/trends/current
     * @param excludeHashTags Setting this to true will remove all hashtags from the trends list.
     * @return the result
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.2
     * @see <a href="http://apiwiki.twitter.com/Twitter-Search-API-Method%3A-trends">Twitter Search API Method: trends</a>
     */
    public Trends getCurrentTrends(boolean excludeHashTags) throws TwitterException {
        return Trends.constructTrendsList(get(searchBaseURL + "trends/current.json"
                + (excludeHashTags ? "?exclude=hashtags" : ""), false)).get(0);
    }


    /**
     * Returns the top 20 trending topics for each hour in a given day.
     * <br>This method calls http://search.twitter.com/trends/daily
     * @return the result
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.2
     * @see <a href="http://apiwiki.twitter.com/Twitter-Search-API-Method%3A-trends-daily">Twitter Search API Method: trends daily</a>
     */
    public List<Trends> getDailyTrends() throws TwitterException {
        return Trends.constructTrendsList(get(searchBaseURL + "trends/daily.json", false));
    }

    /**
     * Returns the top 20 trending topics for each hour in a given day.
     * <br>This method calls http://search.twitter.com/trends/daily
     * @param date Permits specifying a start date for the report.
     * @param excludeHashTags Setting this to true will remove all hashtags from the trends list.
     * @return the result
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.2
     * @see <a href="http://apiwiki.twitter.com/Twitter-Search-API-Method%3A-trends-daily">Twitter Search API Method: trends daily</a>
     */
    public List<Trends> getDailyTrends(Date date, boolean excludeHashTags) throws TwitterException {
        return Trends.constructTrendsList(get(searchBaseURL
                + "trends/daily.json?date=" + toDateStr(date)
                + (excludeHashTags ? "&exclude=hashtags" : ""), false));
    }

    private String toDateStr(Date date){
        if(null == date){
            date = new Date();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    /**
     * Returns the top 30 trending topics for each day in a given week.
     * <br>This method calls http://search.twitter.com/trends/weekly
     * @return the result
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.2
     * @see <a href="http://apiwiki.twitter.com/Twitter-Search-API-Method%3A-trends-weekly">Twitter Search API Method: trends weekly</a>
     */
    public List<Trends> getWeeklyTrends() throws TwitterException {
        return Trends.constructTrendsList(get(searchBaseURL
                + "trends/weekly.json", false));
    }

    /**
     * Returns the top 30 trending topics for each day in a given week.
     * <br>This method calls http://search.twitter.com/trends/weekly
     * @param date Permits specifying a start date for the report.
     * @param excludeHashTags Setting this to true will remove all hashtags from the trends list.
     * @return the result
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.2
     * @see <a href="http://apiwiki.twitter.com/Twitter-Search-API-Method%3A-trends-weekly">Twitter Search API Method: trends weekly</a>
     */
    public List<Trends> getWeeklyTrends(Date date, boolean excludeHashTags) throws TwitterException {
        return Trends.constructTrendsList(get(searchBaseURL
                + "trends/weekly.json?date=" + toDateStr(date)
                + (excludeHashTags ? "&exclude=hashtags" : ""), false));
    }

    /* Status Methods */

    /**
     * Returns the 20 most recent statuses from non-protected users who have set a custom user icon.
     * <br>This method calls http://twitter.com/statuses/public_timeline
     *
     * @return list of statuses of the Public Timeline
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses-public_timeline">Twitter API Wiki / Twitter REST API Method: statuses public_timeline</a>
     */
    public List<Status> getPublicTimeline() throws
            TwitterException {
        return Status.constructStatuses(get(baseURL +
                "statuses/public_timeline.xml", false), this);
    }

    /**
     * Returns only public statuses with an ID greater than (that is, more recent than) the specified ID.
     * <br>This method calls http://twitter.com/statuses/public_timeline
     *
     * @param sinceID returns only public statuses with an ID greater than (that is, more recent than) the specified ID
     * @return the 20 most recent statuses
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses-public_timeline">Twitter API Wiki / Twitter REST API Method: statuses public_timeline</a>
     */
    public List<Status> getPublicTimeline(long sinceID) throws
            TwitterException {
        return Status.constructStatuses(get(baseURL +
                "statuses/public_timeline.xml", null, new Paging((long) sinceID)
                , false), this);
    }

    /**
     * Returns the 20 most recent statuses posted in the last 24 hours from the authenticating1 user and that user's friends.
     * It's also possible to request another user's friends_timeline via the id parameter below.
     * <br>This method calls http://twitter.com/statuses/friends_timeline
     *
     * @return list of the Friends Timeline
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses-friends_timeline">Twitter API Wiki / Twitter REST API Method: statuses friends_timeline</a>
     */
    public List<Status> getFriendsTimeline() throws
            TwitterException {
        return Status.constructStatuses(get(baseURL + "statuses/friends_timeline.xml", true), this);
    }

    /**
     * Returns the 20 most recent statuses posted in the last 24 hours from the authenticating user.
     * <br>This method calls http://twitter.com/statuses/friends_timeline
     *
     * @param page the number of page
     * @return list of the Friends Timeline
     * @throws TwitterException when Twitter service or network is unavailable
     * @deprecated Use getFriendsTimeline(Paging paging) instead
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses-friends_timeline">Twitter API Wiki / Twitter REST API Method: statuses friends_timeline</a>
     */
    public List<Status> getFriendsTimelineByPage(int page) throws
            TwitterException {
        return getFriendsTimeline(new Paging(page));
    }

    /**
     * Returns the 20 most recent statuses posted in the last 24 hours from the authenticating user.
     * <br>This method calls http://twitter.com/statuses/friends_timeline
     *
     * @param page the number of page
     * @return list of the Friends Timeline
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 1.1.8
     * @deprecated Use getFriendsTimeline(Paging paging) instead
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses-friends_timeline">Twitter API Wiki / Twitter REST API Method: statuses friends_timeline</a>
     */
    public List<Status> getFriendsTimeline(int page) throws
            TwitterException {
        return getFriendsTimeline(new Paging(page));
    }

    /**
     * Returns the 20 most recent statuses posted in the last 24 hours from the authenticating user.
     * <br>This method calls http://twitter.com/statuses/friends_timeline
     *
     * @param sinceId Returns only statuses with an ID greater than (that is, more recent than) the specified ID
     * @param page    the number of page
     * @return list of the Friends Timeline
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 1.1.8
     * @deprecated Use getFriendsTimeline(Paging paging) instead
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses-friends_timeline">Twitter API Wiki / Twitter REST API Method: statuses friends_timeline</a>
     */
    public List<Status> getFriendsTimeline(long sinceId, int page) throws
            TwitterException {
        return getFriendsTimeline(new Paging(page).sinceId(sinceId));
    }

    /**
     * Returns the 20 most recent statuses posted in the last 24 hours from the specified userid.
     * <br>This method calls http://twitter.com/statuses/friends_timeline
     *
     * @param id specifies the ID or screen name of the user for whom to return the friends_timeline
     * @return list of the Friends Timeline
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses-friends_timeline">Twitter API Wiki / Twitter REST API Method: statuses friends_timeline</a>
     */
    public List<Status> getFriendsTimeline(String id) throws
            TwitterException {
        return Status.constructStatuses(get(baseURL + "statuses/friends_timeline/" + id + ".xml", true), this);
    }

    /**
     * Returns the 20 most recent statuses posted in the last 24 hours from the specified userid.
     * <br>This method calls http://twitter.com/statuses/friends_timeline
     *
     * @param id   specifies the ID or screen name of the user for whom to return the friends_timeline
     * @param page the number of page
     * @return list of the Friends Timeline
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses-friends_timeline">Twitter API Wiki / Twitter REST API Method: statuses friends_timeline</a>
     * @deprecated Use getFriendsTimeline(String id, Paging paging) instead
     */
    public List<Status> getFriendsTimelineByPage(String id, int page) throws
            TwitterException {
        return getFriendsTimeline(id, new Paging(page));
    }

    /**
     * Returns the 20 most recent statuses posted in the last 24 hours from the specified userid.
     * <br>This method calls http://twitter.com/statuses/friends_timeline
     *
     * @param id   specifies the ID or screen name of the user for whom to return the friends_timeline
     * @param page the number of page
     * @return list of the Friends Timeline
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 1.1.8
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses-friends_timeline">Twitter API Wiki / Twitter REST API Method: statuses friends_timeline</a>
     * @deprecated Use getFriendsTimeline(String id, Paging paging) instead
     */
    public List<Status> getFriendsTimeline(String id, int page) throws
            TwitterException {
        return getFriendsTimeline(id, new Paging(page));
    }

    /**
     * Returns the 20 most recent statuses posted in the last 24 hours from the specified userid.
     * <br>This method calls http://twitter.com/statuses/friends_timeline
     *
     * @param sinceId Returns only statuses with an ID greater than (that is, more recent than) the specified ID
     * @param id   specifies the ID or screen name of the user for whom to return the friends_timeline
     * @param page the number of page
     * @return list of the Friends Timeline
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 1.1.8
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses-friends_timeline">Twitter API Wiki / Twitter REST API Method: statuses friends_timeline</a>
     * @deprecated Use getFriendsTimeline(String id, Paging paging) instead
     */
    public List<Status> getFriendsTimeline(long sinceId, String id, int page) throws
            TwitterException {
        return getFriendsTimeline(id, new Paging(page).sinceId(sinceId));
    }

    /**
     * Returns the 20 most recent statuses posted in the last 24 hours from the specified userid.
     * <br>This method calls http://twitter.com/statuses/friends_timeline
     *
     * @param paging controls pagination
     * @return list of the Friends Timeline
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.1
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses-friends_timeline">Twitter API Wiki / Twitter REST API Method: statuses friends_timeline</a>
     */
    public List<Status> getFriendsTimeline(Paging paging) throws
            TwitterException {
        return Status.constructStatuses(get(baseURL + "statuses/friends_timeline.xml",null, paging, true), this);
    }

    /**
     * Returns the 20 most recent statuses posted in the last 24 hours from the specified userid.
     * <br>This method calls http://twitter.com/statuses/friends_timeline
     *
     * @param id   specifies the ID or screen name of the user for whom to return the friends_timeline
     * @param paging controls pagination
     * @return list of the Friends Timeline
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.1
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses-friends_timeline">Twitter API Wiki / Twitter REST API Method: statuses friends_timeline</a>
     */
    public List<Status> getFriendsTimeline(String id, Paging paging) throws
            TwitterException {
        return Status.constructStatuses(get(baseURL + "statuses/friends_timeline/" + id + ".xml",null, paging, true), this);
    }


    /**
     * Returns the 20 most recent statuses posted in the last 24 hours from the authenticating user.
     * <br>This method calls http://twitter.com/statuses/friends_timeline
     *
     * @param since narrows the returned results to just those statuses created after the specified HTTP-formatted date
     * @return list of the Friends Timeline
     * @throws TwitterException when Twitter service or network is unavailable
     * @deprecated Use getFriendsTimeline(Paging paging) instead
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses-friends_timeline">Twitter API Wiki / Twitter REST API Method: statuses friends_timeline</a>
     */
    public List<Status> getFriendsTimeline(Date since) throws
            TwitterException {
        return Status.constructStatuses(get(baseURL + "statuses/friends_timeline.xml",
                "since", format.format(since), true), this);
    }

    /**
     * Returns the 20 most recent statuses posted in the last 24 hours from the authenticating user.
     * <br>This method calls http://twitter.com/statuses/friends_timeline
     *
     * @param sinceId Returns only statuses with an ID greater than (that is, more recent than) the specified ID
     * @return list of the Friends Timeline
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 1.1.8
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses-friends_timeline">Twitter API Wiki / Twitter REST API Method: statuses friends_timeline</a>
     * @deprecated Use getFriendsTimeline(Paging paging) instead
     */
    public List<Status> getFriendsTimeline(long sinceId) throws
            TwitterException {
        return Status.constructStatuses(get(baseURL + "statuses/friends_timeline.xml",
                "since_id", String.valueOf(sinceId), true), this);
    }

    /**
     * Returns the most recent statuses posted in the last 24 hours from the specified userid.
     * <br>This method calls http://twitter.com/statuses/friends_timeline
     *
     * @param id    specifies the ID or screen name of the user for whom to return the friends_timeline
     * @param since narrows the returned results to just those statuses created after the specified HTTP-formatted date
     * @return list of the Friends Timeline
     * @throws TwitterException when Twitter service or network is unavailable
     * @deprecated Use getFriendsTimeline(String id, Paging paging) instead
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses-friends_timeline">Twitter API Wiki / Twitter REST API Method: statuses friends_timeline</a>
     */
    public List<Status> getFriendsTimeline(String id,
                                                        Date since) throws TwitterException {
        return Status.constructStatuses(get(baseURL + "statuses/friends_timeline/" + id + ".xml",
                "since", format.format(since), true), this);
    }

    /**
     * Returns the most recent statuses posted in the last 24 hours from the specified userid.
     * <br>This method calls http://twitter.com/statuses/friends_timeline
     *
     * @param id    specifies the ID or screen name of the user for whom to return the friends_timeline
     * @param sinceId Returns only statuses with an ID greater than (that is, more recent than) the specified ID
     * @return list of the Friends Timeline
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 1.1.8
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses-friends_timeline">Twitter API Wiki / Twitter REST API Method: statuses friends_timeline</a>
     * @deprecated Use getFriendsTimeline(String id, Paging paging) instead
     */
    public List<Status> getFriendsTimeline(String id, long sinceId) throws TwitterException {
        return getFriendsTimeline(id,new Paging(sinceId));
    }

    /**
     * Returns the most recent statuses posted in the last 24 hours from the specified userid.
     * <br>This method calls http://twitter.com/statuses/user_timeline
     *
     * @param id    specifies the ID or screen name of the user for whom to return the user_timeline
     * @param count specifies the number of statuses to retrieve.  May not be greater than 200 for performance purposes
     * @param since narrows the returned results to just those statuses created after the specified HTTP-formatted date
     * @return list of the user Timeline
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses-friends_timeline">Twitter API Wiki / Twitter REST API Method: statuses friends_timeline</a>
     * @deprecated using long sinceId is suggested.
     */
    public List<Status> getUserTimeline(String id, int count
            , Date since) throws TwitterException {
        if (MAX_COUNT < count) {
            throw new IllegalArgumentException("count may not be greater than " + MAX_COUNT + " for performance purposes.");
        }
        return Status.constructStatuses(get(baseURL + "statuses/user_timeline/" + id + ".xml",
                "since", format.format(since), "count", String.valueOf(count), true), this);
    }

    /**
     * Returns the most recent statuses posted in the last 24 hours from the specified userid.
     * <br>This method calls http://twitter.com/statuses/user_timeline
     *
     * @param id    specifies the ID or screen name of the user for whom to return the user_timeline
     * @param count specifies the number of statuses to retrieve.  May not be greater than 200 for performance purposes
     * @param sinceId Returns only statuses with an ID greater than (that is, more recent than) the specified ID
     * @return list of the user Timeline
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 1.1.8
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses-friends_timeline">Twitter API Wiki / Twitter REST API Method: statuses friends_timeline</a>
     */
    public List<Status> getUserTimeline(String id, int count,
                                                     long sinceId) throws TwitterException {
        return getUserTimeline(id, new Paging(sinceId).count(count));
    }

    /**
     * Returns the most recent statuses posted in the last 24 hours from the specified userid.
     * <br>This method calls http://twitter.com/statuses/user_timeline
     *
     * @param id    specifies the ID or screen name of the user for whom to return the user_timeline
     * @param paging controls pagenation
     * @return list of the user Timeline
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.1
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses-friends_timeline">Twitter API Wiki / Twitter REST API Method: statuses friends_timeline</a>
     */
    public List<Status> getUserTimeline(String id, Paging paging)
            throws TwitterException {
        return Status.constructStatuses(get(baseURL + "statuses/user_timeline/" + id + ".xml",
                null, paging, true), this);
    }

    /**
     * Returns the most recent statuses posted in the last 24 hours from the specified userid.
     * <br>This method calls http://twitter.com/statuses/user_timeline
     *
     * @param id    specifies the ID or screen name of the user for whom to return the user_timeline
     * @param since narrows the returned results to just those statuses created after the specified HTTP-formatted date
     * @return the 20 most recent statuses posted in the last 24 hours from the user
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses-friends_timeline">Twitter API Wiki / Twitter REST API Method: statuses friends_timeline</a>
     * @deprecated Use getUserTimeline(String id, Paging paging) instead
     */
    public List<Status> getUserTimeline(String id, Date since) throws TwitterException {
        return Status.constructStatuses(get(baseURL + "statuses/user_timeline/" + id + ".xml",
                "since", format.format(since), true), this);
    }

    /**
     * Returns the most recent statuses posted in the last 24 hours from the specified userid.
     * <br>This method calls http://twitter.com/statuses/user_timeline
     *
     * @param id    specifies the ID or screen name of the user for whom to return the user_timeline
     * @param count specifies the number of statuses to retrieve.  May not be greater than 200 for performance purposes
     * @return the 20 most recent statuses posted in the last 24 hours from the user
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses-friends_timeline">Twitter API Wiki / Twitter REST API Method: statuses friends_timeline</a>
     * @deprecated Use getUserTimeline(String id, Paging paging) instead
     */
    public List<Status> getUserTimeline(String id, int count) throws
            TwitterException {
        if (MAX_COUNT < count) {
            throw new IllegalArgumentException("count may not be greater than " + MAX_COUNT + " for performance purposes.");
        }
        return Status.constructStatuses(get(baseURL + "statuses/user_timeline/" + id + ".xml",
                "count", String.valueOf(count), true), this);
    }

    /**
     * Returns the most recent statuses posted in the last 24 hours from the authenticating user.
     * <br>This method calls http://twitter.com/statuses/user_timeline
     *
     * @param count specifies the number of statuses to retrieve.  May not be greater than 200 for performance purposes
     * @param since narrows the returned results to just those statuses created after the specified HTTP-formatted date
     * @return the 20 most recent statuses posted in the last 24 hours from the user
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses-friends_timeline">Twitter API Wiki / Twitter REST API Method: statuses friends_timeline</a>
     * @deprecated using long sinceId is suggested.
     */
    public List<Status> getUserTimeline(int count, Date since) throws TwitterException {
        if (MAX_COUNT < count) {
            throw new IllegalArgumentException("count may not be greater than " + MAX_COUNT + " for performance purposes.");
        }
        return Status.constructStatuses(get(baseURL + "statuses/user_timeline.xml",
                "since", format.format(since), "count", String.valueOf(count), true), this);
    }

    /**
     * Returns the most recent statuses posted in the last 24 hours from the authenticating user.
     * <br>This method calls http://twitter.com/statuses/user_timeline
     *
     * @param count specifies the number of statuses to retrieve.  May not be greater than 200 for performance purposes
     * @param sinceId returns only statuses with an ID greater than (that is, more recent than) the specified ID.
     * @return the 20 most recent statuses posted in the last 24 hours from the user
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses-friends_timeline">Twitter API Wiki / Twitter REST API Method: statuses friends_timeline</a>
     * @since Twitter4J 2.0.0
     * @deprecated Use getUserTimeline(String id, Paging paging) instead
     */
    public List<Status> getUserTimeline(int count, long sinceId) throws TwitterException {
        return getUserTimeline(new Paging(sinceId).count(count));
    }

    /**
     * Returns the most recent statuses posted in the last 24 hours from the specified userid.
     * <br>This method calls http://twitter.com/statuses/user_timeline
     *
     * @param id specifies the ID or screen name of the user for whom to return the user_timeline
     * @return the 20 most recent statuses posted in the last 24 hours from the user
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses-friends_timeline">Twitter API Wiki / Twitter REST API Method: statuses friends_timeline</a>
     */
    public List<Status> getUserTimeline(String id) throws TwitterException {
        return Status.constructStatuses(get(baseURL + "statuses/user_timeline/" + id + ".xml", true), this);
    }

    /**
     * Returns the most recent statuses posted in the last 24 hours from the specified userid.
     * <br>This method calls http://twitter.com/statuses/user_timeline
     *
     * @param id specifies the ID or screen name of the user for whom to return the user_timeline
     * @param sinceId returns only statuses with an ID greater than (that is, more recent than) the specified ID.
     * @return the 20 most recent statuses posted in the last 24 hours from the user
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses-friends_timeline">Twitter API Wiki / Twitter REST API Method: statuses friends_timeline</a>
     * @since Twitter4J 2.0.0
     * @deprecated Use getUserTimeline(String id, Paging paging) instead
     */
    public List<Status> getUserTimeline(String id, long sinceId) throws TwitterException {
        return getUserTimeline(id, new Paging(sinceId));
    }

    /**
     * Returns the most recent statuses posted in the last 24 hours from the authenticating user.
     * <br>This method calls http://twitter.com/statuses/user_timeline
     *
     * @return the 20 most recent statuses posted in the last 24 hours from the user
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses-friends_timeline">Twitter API Wiki / Twitter REST API Method: statuses friends_timeline</a>
     */
    public List<Status> getUserTimeline() throws
            TwitterException {
        return Status.constructStatuses(get(baseURL + "statuses/user_timeline.xml"
                , true), this);
    }

    /**
     * Returns the most recent statuses posted in the last 24 hours from the authenticating user.
     * <br>This method calls http://twitter.com/statuses/user_timeline
     *
     * @param paging controls pagination
     * @return the 20 most recent statuses posted in the last 24 hours from the user
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses-friends_timeline">Twitter API Wiki / Twitter REST API Method: statuses friends_timeline</a>
     * @since Twitter4J 2.0.1
     */
    public List<Status> getUserTimeline(Paging paging) throws
            TwitterException {
        return Status.constructStatuses(get(baseURL + "statuses/user_timeline.xml"
                , null, paging, true), this);
    }

    /**
     * Returns the most recent statuses posted in the last 24 hours from the authenticating user.
     * <br>This method calls http://twitter.com/statuses/user_timeline
     *
     * @param sinceId returns only statuses with an ID greater than (that is, more recent than) the specified ID.
     * @return the 20 most recent statuses posted in the last 24 hours from the user
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses-friends_timeline">Twitter API Wiki / Twitter REST API Method: statuses friends_timeline</a>
     * @since Twitter4J 2.0.0
     * @deprecated Use getUserTimeline(Paging paging) instead
     */
    public List<Status> getUserTimeline(long sinceId) throws
            TwitterException {
        return getUserTimeline(new Paging(sinceId));
    }

    /**
     * Returns the 20 most recent replies (status updates prefixed with @username) to the authenticating user.  Replies are only available to the authenticating user; you can not request a list of replies to another user whether public or protected.
     * <br>This method calls http://twitter.com/statuses/mentions
     *
     * @return the 20 most recent replies
     * @throws TwitterException when Twitter service or network is unavailable
     * @deprecated Use getMentions() instead
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses-mentions">Twitter API Wiki / Twitter REST API Method: statuses mentions</a>
     */
    public List<Status> getReplies() throws TwitterException {
        return Status.constructStatuses(get(baseURL + "statuses/replies.xml", true), this);
    }

    /**
     * Returns the 20 most recent replies (status updates prefixed with @username) to the authenticating user.  Replies are only available to the authenticating user; you can not request a list of replies to another user whether public or protected.
     * <br>This method calls http://twitter.com/statuses/mentions
     *
     * @param sinceId Returns only statuses with an ID greater than (that is, more recent than) the specified ID
     * @return the 20 most recent replies
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 1.1.8
     * @deprecated Use getMentions(Paging paging) instead
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses-mentions">Twitter API Wiki / Twitter REST API Method: statuses mentions</a>
     */
    public List<Status> getReplies(long sinceId) throws TwitterException {
        return Status.constructStatuses(get(baseURL + "statuses/replies.xml",
                "since_id", String.valueOf(sinceId), true), this);
    }

    /**
     * Returns the most recent replies (status updates prefixed with @username) to the authenticating user.  Replies are only available to the authenticating user; you can not request a list of replies to another user whether public or protected.
     * <br>This method calls http://twitter.com/statuses/mentions
     *
     * @param page the number of page
     * @return the 20 most recent replies
     * @throws TwitterException when Twitter service or network is unavailable
     * @deprecated Use getMentions(Paging paging) instead
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses-mentions">Twitter API Wiki / Twitter REST API Method: statuses mentions</a>
     */
    public List<Status> getRepliesByPage(int page) throws TwitterException {
        if (page < 1) {
            throw new IllegalArgumentException("page should be positive integer. passed:" + page);
        }
        return Status.constructStatuses(get(baseURL + "statuses/replies.xml",
                "page", String.valueOf(page), true), this);
    }

    /**
     * Returns the most recent replies (status updates prefixed with @username) to the authenticating user.  Replies are only available to the authenticating user; you can not request a list of replies to another user whether public or protected.
     * <br>This method calls http://twitter.com/statuses/mentions
     *
     * @param page the number of page
     * @return the 20 most recent replies
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 1.1.8
     * @deprecated Use getMentions(Paging paging) instead
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses-mentions">Twitter API Wiki / Twitter REST API Method: statuses mentions</a>
     */
    public List<Status> getReplies(int page) throws TwitterException {
        if (page < 1) {
            throw new IllegalArgumentException("page should be positive integer. passed:" + page);
        }
        return Status.constructStatuses(get(baseURL + "statuses/replies.xml",
                "page", String.valueOf(page), true), this);
    }

    /**
     * Returns the most recent replies (status updates prefixed with @username) to the authenticating user.  Replies are only available to the authenticating user; you can not request a list of replies to another user whether public or protected.
     * <br>This method calls http://twitter.com/statuses/mentions
     *
     * @param sinceId Returns only statuses with an ID greater than (that is, more recent than) the specified ID
     * @param page the number of page
     * @return the 20 most recent replies
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 1.1.8
     * @deprecated Use getMentions(Paging paging) instead
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses-mentions">Twitter API Wiki / Twitter REST API Method: statuses mentions</a>
     */
    public List<Status> getReplies(long sinceId, int page) throws TwitterException {
        if (page < 1) {
            throw new IllegalArgumentException("page should be positive integer. passed:" + page);
        }
        return Status.constructStatuses(get(baseURL + "statuses/replies.xml",
                "since_id", String.valueOf(sinceId),
                "page", String.valueOf(page), true), this);
    }

    /**
     * Returns the 20 most recent mentions (status containing @username) for the authenticating user.
     * <br>This method calls http://twitter.com/statuses/mentions
     *
     * @return the 20 most recent replies
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.1
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses-mentions">Twitter API Wiki / Twitter REST API Method: statuses mentions</a>
     */
    public List<Status> getMentions() throws TwitterException {
        return Status.constructStatuses(get(baseURL + "statuses/mentions.xml",
                null, true), this);
    }

    /**
     * Returns the 20 most recent mentions (status containing @username) for the authenticating user.
     * <br>This method calls http://twitter.com/statuses/mentions
     *
     * @param paging controls pagination
     * @return the 20 most recent replies
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.1
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses-mentions">Twitter API Wiki / Twitter REST API Method: statuses mentions</a>
     */
    public List<Status> getMentions(Paging paging) throws TwitterException {
        return Status.constructStatuses(get(baseURL + "statuses/mentions.xml",
                null, paging, true), this);
    }

    /**
     * Returns a single status, specified by the id parameter. The status's author will be returned inline.
     * <br>This method calls http://twitter.com/statuses/show
     *
     * @param id the numerical ID of the status you're trying to retrieve
     * @return a single status
     * @throws TwitterException when Twitter service or network is unavailable
     * @deprecated Use showStatus(long id) instead.
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses%C2%A0show">Twitter API Wiki / Twitter REST API Method: statuses show</a>
     */

    public Status show(int id) throws TwitterException {
        return showStatus((long)id);
    }

    /**
     * Returns a single status, specified by the id parameter. The status's author will be returned inline.
     * <br>This method calls http://twitter.com/statuses/show
     *
     * @param id the numerical ID of the status you're trying to retrieve
     * @return a single status
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 1.1.1
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses%C2%A0show">Twitter API Wiki / Twitter REST API Method: statuses show</a>
     * @deprecated Use showStatus(long id) instead.
     */

    public Status show(long id) throws TwitterException {
        return new Status(get(baseURL + "statuses/show/" + id + ".xml", false), this);
    }

    /**
     * Returns a single status, specified by the id parameter. The status's author will be returned inline.
     * <br>This method calls http://twitter.com/statuses/show
     *
     * @param id the numerical ID of the status you're trying to retrieve
     * @return a single status
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.1
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses%C2%A0show">Twitter API Wiki / Twitter REST API Method: statuses show</a>
     */
    public Status showStatus(long id) throws TwitterException {
        return new Status(get(baseURL + "statuses/show/" + id + ".xml", false), this);
    }

    /**
     * Updates the user's status.
     * The text will be trimed if the length of the text is exceeding 160 characters.
     * <br>This method calls http://twitter.com/statuses/update
     *
     * @param status the text of your status update
     * @return the latest status
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses%C2%A0update">Twitter API Wiki / Twitter REST API Method: statuses update</a>
     * @deprecated Use updateStatus(String status) instead
     */
    public Status update(String status) throws TwitterException {
        return updateStatus(status);
    }

    /**
     * Updates the user's status.
     * The text will be trimed if the length of the text is exceeding 160 characters.
     * <br>This method calls http://twitter.com/statuses/update
     *
     * @param status the text of your status update
     * @return the latest status
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.1
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses%C2%A0update">Twitter API Wiki / Twitter REST API Method: statuses update</a>
     */
    public Status updateStatus(String status) throws TwitterException {
        return new Status(http.post(baseURL + "statuses/update.xml",
                new PostParameter[]{new PostParameter("status", status), new PostParameter("source", source)}, true), this);
    }

    /**
     * Updates the user's status.
     * The text will be trimed if the length of the text is exceeding 160 characters.
     * <br>This method calls http://twitter.com/statuses/update
     *
     * @param status            the text of your status update
     * @param inReplyToStatusId The ID of an existing status that the status to be posted is in reply to.  This implicitly sets the in_reply_to_user_id attribute of the resulting status to the user ID of the message being replied to.  Invalid/missing status IDs will be ignored.
     * @return the latest status
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses%C2%A0update">Twitter API Wiki / Twitter REST API Method: statuses update</a>
     * @deprecated Use updateStatus(String status, long inReplyToStatusId) instead
     */
    public Status update(String status, long inReplyToStatusId) throws TwitterException {
        return updateStatus(status, inReplyToStatusId);
    }

    /**
     * Updates the user's status.
     * The text will be trimed if the length of the text is exceeding 160 characters.
     * <br>This method calls http://twitter.com/statuses/update
     *
     * @param status            the text of your status update
     * @param inReplyToStatusId The ID of an existing status that the status to be posted is in reply to.  This implicitly sets the in_reply_to_user_id attribute of the resulting status to the user ID of the message being replied to.  Invalid/missing status IDs will be ignored.
     * @return the latest status
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.1
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses%C2%A0update">Twitter API Wiki / Twitter REST API Method: statuses update</a>
     */
    public Status updateStatus(String status, long inReplyToStatusId) throws TwitterException {
        return new Status(http.post(baseURL + "statuses/update.xml",
                new PostParameter[]{new PostParameter("status", status), new PostParameter("in_reply_to_status_id", String.valueOf(inReplyToStatusId)), new PostParameter("source", source)}, true), this);
    }

    /**
     * Destroys the status specified by the required ID parameter.  The authenticating user must be the author of the specified status.
     * <br>This method calls http://twitter.com/statuses/destroy
     *
     * @param statusId The ID of the status to destroy.
     * @return the deleted status
     * @throws TwitterException when Twitter service or network is unavailable
     * @since 1.0.5
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses%C2%A0destroy">Twitter API Wiki / Twitter REST API Method: statuses destroy</a>
     */
    public Status destroyStatus(long statusId) throws TwitterException {
        return new Status(http.post(baseURL + "statuses/destroy/" + statusId + ".xml",
                new PostParameter[0], true), this);
    }

    /**
     * Returns extended information of a given user, specified by ID or screen name as per the required id parameter below.  This information includes design settings, so third party developers can theme their widgets according to a given user's preferences.
     * <br>This method calls http://twitter.com/users/show
     *
     * @param id the ID or screen name of the user for whom to request the detail
     * @return ExtendedUser
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-users%C2%A0show">Twitter API Wiki / Twitter REST API Method: users show</a>
     */
    public ExtendedUser getUserDetail(String id) throws TwitterException {
        return new ExtendedUser(get(baseURL + "users/show/" + id + ".xml", true), this);
    }

    /* User Methods */

    /**
     * Returns the specified user's friends, each with current status inline.
     * <br>This method calls http://twitter.com/statuses/friends
     *
     * @return the list of friends
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses%C2%A0friends">Twitter API Wiki / Twitter REST API Method: statuses friends</a>
     */
    public List<User> getFriends() throws TwitterException {
        return User.constructUsers(get(baseURL + "statuses/friends.xml", true), this);
    }

    /**
     * Returns the specified user's friends, each with current status inline.
     * <br>This method calls http://twitter.com/statuses/friends
     *
     * @param paging controls pagination
     * @return the list of friends
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.1
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses%C2%A0friends">Twitter API Wiki / Twitter REST API Method: statuses friends</a>
     */
    public List<User> getFriends(Paging paging) throws TwitterException {
        return User.constructUsers(get(baseURL + "statuses/friends.xml", null,
                paging, true), this);
    }

    /**
     * Returns the specified user's friends, each with current status inline.
     * <br>This method calls http://twitter.com/statuses/friends
     *
     * @param page number of page
     * @return the list of friends
     * @throws TwitterException when Twitter service or network is unavailable
     * @deprecated Use getFriends(Paging paging) instead
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses%C2%A0friends">Twitter API Wiki / Twitter REST API Method: statuses friends</a>
     */
    public List<User> getFriends(int page) throws TwitterException {
        return getFriends(new Paging(page));
    }

    /**
     * Returns the user's friends, each with current status inline.
     * <br>This method calls http://twitter.com/statuses/friends
     *
     * @param id the ID or screen name of the user for whom to request a list of friends
     * @return the list of friends
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses%C2%A0friends">Twitter API Wiki / Twitter REST API Method: statuses friends</a>
     */
    public List<User> getFriends(String id) throws TwitterException {
        return User.constructUsers(get(baseURL + "statuses/friends/" + id + ".xml"
                , true), this);
    }

    /**
     * Returns the user's friends, each with current status inline.
     * <br>This method calls http://twitter.com/statuses/friends
     *
     * @param id the ID or screen name of the user for whom to request a list of friends
     * @param paging controls pagination
     * @return the list of friends
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.1
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses%C2%A0friends">Twitter API Wiki / Twitter REST API Method: statuses friends</a>
     */
    public List<User> getFriends(String id, Paging paging) throws TwitterException {
        return User.constructUsers(get(baseURL + "statuses/friends/" + id + ".xml"
                , null, paging, true), this);
    }

    /**
     * Returns the user's friends, each with current status inline.
     * <br>This method calls http://twitter.com/statuses/friends
     *
     * @param id   the ID or screen name of the user for whom to request a list of friends
     * @param page the number of page
     * @return List
     * @throws TwitterException when Twitter service or network is unavailable
     * @deprecated Use getFriends(String id, Paging paging) instead
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses%C2%A0friends">Twitter API Wiki / Twitter REST API Method: statuses friends</a>
     */
    public List<User> getFriends(String id, int page) throws TwitterException {
        return getFriends(id, new Paging(page));
    }

    /**
     * Returns the authenticating user's followers, each with current status inline. They are ordered by the order in which they joined Twitter (this is going to be changed).
     * <br>This method calls http://twitter.com/statuses/followers
     *
     * @return List
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses%C2%A0followers">Twitter API Wiki / Twitter REST API Method: statuses followers</a>
     */
    public List<User> getFollowers() throws TwitterException {
        return User.constructUsers(get(baseURL + "statuses/followers.xml", true), this);
    }

    /**
     * Returns the authenticating user's followers, each with current status inline. They are ordered by the order in which they joined Twitter (this is going to be changed).
     * <br>This method calls http://twitter.com/statuses/followers
     *
     * @param paging controls pagination
     * @return List
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.1
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses%C2%A0followers">Twitter API Wiki / Twitter REST API Method: statuses followers</a>
     */
    public List<User> getFollowers(Paging paging) throws TwitterException {
        return User.constructUsers(get(baseURL + "statuses/followers.xml", null
                , paging, true), this);
    }

    /**
     * Returns the authenticating user's followers, each with current status inline. They are ordered by the order in which they joined Twitter (this is going to be changed).
     * <br>This method calls http://twitter.com/statuses/followers
     *
     * @param page Retrieves the next 100 followers.
     * @return List
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 1.1.0
     * @deprecated Use getFollowers(Paging paging) instead
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses%C2%A0followers">Twitter API Wiki / Twitter REST API Method: statuses followers</a>
     */
    public List<User> getFollowers(int page) throws TwitterException {
        return getFollowers(new Paging(page));
    }

    /**
     * Returns the authenticating user's followers, each with current status inline. They are ordered by the order in which they joined Twitter (this is going to be changed).
     * <br>This method calls http://twitter.com/statuses/followers
     *
     * @param id The ID or screen name of the user for whom to request a list of followers.
     * @return List
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 1.1.0
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses%C2%A0followers">Twitter API Wiki / Twitter REST API Method: statuses followers</a>
     */
    public List<User> getFollowers(String id) throws TwitterException {
        return User.constructUsers(get(baseURL + "statuses/followers/" + id + ".xml", true), this);
    }

    /**
     * Returns the authenticating user's followers, each with current status inline. They are ordered by the order in which they joined Twitter (this is going to be changed).
     * <br>This method calls http://twitter.com/statuses/followers
     *
     * @param id   The ID or screen name of the user for whom to request a list of followers.
     * @param paging controls pagination
     * @return List
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.1
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses%C2%A0followers">Twitter API Wiki / Twitter REST API Method: statuses followers</a>
     */
    public List<User> getFollowers(String id, Paging paging) throws TwitterException {
        return User.constructUsers(get(baseURL + "statuses/followers/" + id +
                ".xml", null, paging, true), this);
    }

    /**
     * Returns the authenticating user's followers, each with current status inline. They are ordered by the order in which they joined Twitter (this is going to be changed).
     * <br>This method calls http://twitter.com/statuses/followers
     *
     * @param id   The ID or screen name of the user for whom to request a list of followers.
     * @param page Retrieves the next 100 followers.
     * @return List
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 1.1.0
     * @deprecated Use getFollowers(String id, Paging paging) instead
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses%C2%A0followers">Twitter API Wiki / Twitter REST API Method: statuses followers</a>
     */
    public List<User> getFollowers(String id, int page) throws TwitterException {
        return getFollowers(id, new Paging(page));
    }

    /**
     * Returns a list of the users currently featured on the site with their current statuses inline.
     *
     * @return List of User
     * @throws TwitterException when Twitter service or network is unavailable
     */
    public List<User> getFeatured() throws TwitterException {
        return User.constructUsers(get(baseURL + "statuses/featured.xml", true), this);
    }

    /**
     * Returns a list of the direct messages sent to the authenticating user.
     * <br>This method calls http://twitter.com/direct_messages
     *
     * @return List
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-direct_messages">Twitter API Wiki / Twitter REST API Method: direct_messages</a>
     */
    public List<DirectMessage> getDirectMessages() throws TwitterException {
        return DirectMessage.constructDirectMessages(get(baseURL + "direct_messages.xml", true), this);
    }

    /**
     * Returns a list of the direct messages sent to the authenticating user.
     * <br>This method calls http://twitter.com/direct_messages
     *
     * @param paging controls pagination
     * @return List
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-direct_messages">Twitter API Wiki / Twitter REST API Method: direct_messages</a>
     */
    public List<DirectMessage> getDirectMessages(Paging paging) throws TwitterException {
        return DirectMessage.constructDirectMessages(get(baseURL
                + "direct_messages.xml", null, paging, true), this);
    }

    /**
     * Returns a list of the direct messages sent to the authenticating user.
     * <br>This method calls http://twitter.com/direct_messages
     *
     * @param page the number of page
     * @return List
     * @throws TwitterException when Twitter service or network is unavailable
     * @deprecated Use getDirectMessages(Paging paging) instead
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-direct_messages">Twitter API Wiki / Twitter REST API Method: direct_messages</a>
     */
    public List<DirectMessage> getDirectMessagesByPage(int page) throws TwitterException {
        return getDirectMessages(new Paging(page));
    }

    /**
     * Returns a list of the direct messages sent to the authenticating user.
     * <br>This method calls http://twitter.com/direct_messages
     *
     * @param page    the number of page
     * @param sinceId Returns only direct messages with an ID greater than (that is, more recent than) the specified ID.
     * @return List
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.0
     * @deprecated Use getDirectMessages(Paging paging) instead
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-direct_messages">Twitter API Wiki / Twitter REST API Method: direct_messages</a>
     */
    public List<DirectMessage> getDirectMessages(int page
            , int sinceId) throws TwitterException {
        return getDirectMessages(new Paging(page).sinceId(sinceId));
    }

    /**
     * Returns a list of the direct messages sent to the authenticating user.
     * <br>This method calls http://twitter.com/direct_messages
     *
     * @param sinceId Returns only direct messages with an ID greater than (that is, more recent than) the specified ID.
     * @return list of direct messages
     * @throws TwitterException when Twitter service or network is unavailable
     * @deprecated Use getDirectMessages(Paging paging) instead
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-direct_messages">Twitter API Wiki / Twitter REST API Method: direct_messages</a>
     */
    public List<DirectMessage> getDirectMessages(int sinceId) throws TwitterException {
        return getDirectMessages(new Paging((long)sinceId));
    }

    /**
     * Returns a list of the direct messages sent to the authenticating user.
     * <br>This method calls http://twitter.com/direct_messages
     *
     * @param since narrows the resulting list of direct messages to just those sent after the specified HTTP-formatted date
     * @return list of direct messages
     * @throws TwitterException when Twitter service or network is unavailable
     * @deprecated Use getDirectMessages(Paging paging) instead
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-direct_messages">Twitter API Wiki / Twitter REST API Method: direct_messages</a>
     */
    public List<DirectMessage> getDirectMessages(Date since) throws
            TwitterException {
        return DirectMessage.constructDirectMessages(get(baseURL +
                "direct_messages.xml", "since", format.format(since), true), this);
    }

    /**
     * Returns a list of the direct messages sent by the authenticating user.
     * <br>This method calls http://twitter.com/direct_messages/sent
     *
     * @return List
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-direct_messages%C2%A0sent">Twitter API Wiki / Twitter REST API Method: direct_messages sent</a>
     */
    public List<DirectMessage> getSentDirectMessages() throws
            TwitterException {
        return DirectMessage.constructDirectMessages(get(baseURL +
                "direct_messages/sent.xml", new PostParameter[0], true), this);
    }

    /**
     * Returns a list of the direct messages sent by the authenticating user.
     * <br>This method calls http://twitter.com/direct_messages/sent
     *
     * @param paging controls pagination
     * @return List
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.1
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-direct_messages%C2%A0sent">Twitter API Wiki / Twitter REST API Method: direct_messages sent</a>
     */
    public List<DirectMessage> getSentDirectMessages(Paging paging) throws
            TwitterException {
        return DirectMessage.constructDirectMessages(get(baseURL +
                "direct_messages/sent.xml", new PostParameter[0],paging, true), this);
    }

    /**
     * Returns a list of the direct messages sent by the authenticating user.
     * <br>This method calls http://twitter.com/direct_messages/sent
     *
     * @param since narrows the resulting list of direct messages to just those sent after the specified HTTP-formatted date
     * @return List
     * @throws TwitterException when Twitter service or network is unavailable
     * @deprecated using long sinceId is suggested.
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-direct_messages%C2%A0sent">Twitter API Wiki / Twitter REST API Method: direct_messages sent</a>
     */
    public List<DirectMessage> getSentDirectMessages(Date since) throws
            TwitterException {
        return DirectMessage.constructDirectMessages(get(baseURL +
                "direct_messages/sent.xml", "since", format.format(since), true), this);
    }

    /**
     * Returns a list of the direct messages sent by the authenticating user.
     * <br>This method calls http://twitter.com/direct_messages/sent
     *
     * @param sinceId returns only sent direct messages with an ID greater than (that is, more recent than) the specified ID
     * @return List
     * @throws TwitterException when Twitter service or network is unavailable
     * @deprecated Use getSentDirectMessages(Paging paging) instead
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-direct_messages%C2%A0sent">Twitter API Wiki / Twitter REST API Method: direct_messages sent</a>
     */
    public List<DirectMessage> getSentDirectMessages(int sinceId) throws
            TwitterException {
        return getSentDirectMessages(new Paging((long)sinceId));
    }

    /**
     * Returns a list of the direct messages sent by the authenticating user.
     * <br>This method calls http://twitter.com/direct_messages/sent
     *
     * @param sinceId returns only sent direct messages with an ID greater than (that is, more recent than) the specified ID
     * @param page Retrieves the 20 next most recent direct messages.
     * @return List
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.0
     * @deprecated Use getSentDirectMessages(Paging paging) instead
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-direct_messages%C2%A0sent">Twitter API Wiki / Twitter REST API Method: direct_messages sent</a>
     */
    public List<DirectMessage> getSentDirectMessages(int page
            , int sinceId) throws TwitterException {
        return getSentDirectMessages(new Paging(page, (long)sinceId));
    }

    /**
     * Sends a new direct message to the specified user from the authenticating user.  Requires both the user and text parameters below.
     * The text will be trimed if the length of the text is exceeding 140 characters.
     * <br>This method calls http://twitter.com/direct_messages/new
     *
     * @param id   the ID or screen name of the user to whom send the direct message
     * @param text String
     * @return DirectMessage
     * @throws TwitterException when Twitter service or network is unavailable
     @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-direct_messages%C2%A0new">Twitter API Wiki / Twitter REST API Method: direct_messages new</a>
     */
    public DirectMessage sendDirectMessage(String id,
                                                        String text) throws TwitterException {
        return new DirectMessage(http.post(baseURL + "direct_messages/new.xml",
                new PostParameter[]{new PostParameter("user", id),
                        new PostParameter("text", text)}, true), this);
    }


    /**
     * Destroys the direct message specified in the required ID parameter.  The authenticating user must be the recipient of the specified direct message.
     * <br>This method calls http://twitter.com/direct_messages/destroy
     *
     * @param id the ID of the direct message to destroy
     * @return the deleted direct message
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-direct_messages%C2%A0destroy">Twitter API Wiki / Twitter REST API Method: direct_messages destroy</a>
     * @deprecated Use destroyDirectMessage(int id) instead
     */
    public DirectMessage deleteDirectMessage(int id) throws
            TwitterException {
        return destroyDirectMessage(id);
    }

    /**
     * Destroys the direct message specified in the required ID parameter.  The authenticating user must be the recipient of the specified direct message.
     * <br>This method calls http://twitter.com/direct_messages/destroy
     *
     * @param id the ID of the direct message to destroy
     * @return the deleted direct message
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-direct_messages%C2%A0destroy">Twitter API Wiki / Twitter REST API Method: direct_messages destroy</a>
     * @since Twitter4J 2.0.1
     */
    public DirectMessage destroyDirectMessage(long id) throws
            TwitterException {
        return new DirectMessage(http.post(baseURL +
                "direct_messages/destroy/" + id + ".xml", new PostParameter[0], true), this);
    }

    /**
     * Befriends the user specified in the ID parameter as the authenticating user.  Returns the befriended user in the requested format when successful.  Returns a string describing the failure condition when unsuccessful.
     *
     * @param id the ID or screen name of the user to be befriended
     * @return the befriended user
     * @throws TwitterException when Twitter service or network is unavailable
     * @deprecated Use createFriendship(String id) instead
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-friendships%C2%A0create">Twitter API Wiki / Twitter REST API Method: friendships create</a>
     */

    public User create(String id) throws TwitterException {
        return createFriendship(id);
    }

    /**
     * Befriends the user specified in the ID parameter as the authenticating user.  Returns the befriended user in the requested format when successful.  Returns a string describing the failure condition when unsuccessful.
     *
     * @param id the ID or screen name of the user to be befriended
     * @return the befriended user
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.1
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-friendships%C2%A0create">Twitter API Wiki / Twitter REST API Method: friendships create</a>
     */
    public User createFriendship(String id) throws TwitterException {
        return new User(http.post(baseURL + "friendships/create/" + id + ".xml", new PostParameter[0], true), this);
    }

    /**
     * Befriends the user specified in the ID parameter as the authenticating user.  Returns the befriended user in the requested format when successful.  Returns a string describing the failure condition when unsuccessful.
     *
     * @param id the ID or screen name of the user to be befriended
     * @param follow Enable notifications for the target user in addition to becoming friends.
     * @return the befriended user
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.2
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-friendships%C2%A0create">Twitter API Wiki / Twitter REST API Method: friendships create</a>
     */
    public User createFriendship(String id, boolean follow) throws TwitterException {
        return new User(http.post(baseURL + "friendships/create/" + id + ".xml"
                , new PostParameter[]{new PostParameter("follow"
                        , String.valueOf(follow))}, true)
                , this);
    }

    /**
     * Discontinues friendship with the user specified in the ID parameter as the authenticating user.  Returns the un-friended user in the requested format when successful.  Returns a string describing the failure condition when unsuccessful.
     *
     * @param id the ID or screen name of the user for whom to request a list of friends
     * @return User
     * @throws TwitterException when Twitter service or network is unavailable
     * @deprecated Use destroyFriendship(String id) instead
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-friendships%C2%A0destroy">Twitter API Wiki / Twitter REST API Method: friendships destroy</a>
     */
    public User destroy(String id) throws TwitterException {
        return destroyFriendship(id);
    }

    /**
     * Discontinues friendship with the user specified in the ID parameter as the authenticating user.  Returns the un-friended user in the requested format when successful.  Returns a string describing the failure condition when unsuccessful.
     *
     * @param id the ID or screen name of the user for whom to request a list of friends
     * @return User
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.1
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-friendships%C2%A0destroy">Twitter API Wiki / Twitter REST API Method: friendships destroy</a>
     */
    public User destroyFriendship(String id) throws TwitterException {
        return new User(http.post(baseURL + "friendships/destroy/" + id + ".xml", new PostParameter[0], true), this);
    }

    /**
     * Tests if a friendship exists between two users.
     *
     * @param userA The ID or screen_name of the first user to test friendship for.
     * @param userB The ID or screen_name of the second user to test friendship for.
     * @return if a friendship exists between two users.
     * @throws TwitterException when Twitter service or network is unavailable
     * @deprecated Use friendshipExists(String userA, String userB)
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-friendships-exists">Twitter API Wiki / Twitter REST API Method: friendships exists</a>
     */
    public boolean exists(String userA, String userB) throws TwitterException {
        return existsFriendship(userA, userB);
    }
    /**
     * Tests if a friendship exists between two users.
     *
     * @param userA The ID or screen_name of the first user to test friendship for.
     * @param userB The ID or screen_name of the second user to test friendship for.
     * @return if a friendship exists between two users.
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.1
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-friendships-exists">Twitter API Wiki / Twitter REST API Method: friendships exists</a>
     */
    public boolean existsFriendship(String userA, String userB) throws TwitterException {
        return -1 != get(baseURL + "friendships/exists.xml", "user_a", userA, "user_b", userB, true).
                asString().indexOf("true");
    }

    /**
     * Returns an array of numeric IDs for every user the authenticating user is following.
     * @return an array of numeric IDs for every user the authenticating user is following
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.0
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-friends%C2%A0ids">Twitter API Wiki / Twitter REST API Method: friends ids</a>
     */
    public IDs getFriendsIDs() throws TwitterException {
        return new IDs(get(baseURL + "friends/ids.xml", true));
    }

    /**
     * Returns an array of numeric IDs for every user the authenticating user is following.
     * @param paging Specifies the page number of the results beginning at 1. A single page contains 5000 ids. This is recommended for users with large ID lists. If not provided all ids are returned.
     * @return an array of numeric IDs for every user the authenticating user is following
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.1
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-friends%C2%A0ids">Twitter API Wiki / Twitter REST API Method: friends ids</a>
     */
    public IDs getFriendsIDs(Paging paging) throws TwitterException {
        return new IDs(get(baseURL + "friends/ids.xml", null, paging, true));
    }

    /**
     * Returns an array of numeric IDs for every user the specified user is following.
     * @param userId Specfies the ID of the user for whom to return the friends list.
     * @return an array of numeric IDs for every user the specified user is following
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.0
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-friends%C2%A0ids">Twitter API Wiki / Twitter REST API Method: friends ids</a>
     */
    public IDs getFriendsIDs(int userId) throws TwitterException {
        return new IDs(get(baseURL + "friends/ids.xml?user_id=" + userId, true));
    }

    /**
     * Returns an array of numeric IDs for every user the specified user is following.
     * @param userId Specfies the ID of the user for whom to return the friends list.
     * @param paging Specifies the page number of the results beginning at 1. A single page contains 5000 ids. This is recommended for users with large ID lists. If not provided all ids are returned.
     * @return an array of numeric IDs for every user the specified user is following
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.1
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-friends%C2%A0ids">Twitter API Wiki / Twitter REST API Method: friends ids</a>
     */
    public IDs getFriendsIDs(int userId, Paging paging) throws TwitterException {
        return new IDs(get(baseURL + "friends/ids.xml?user_id=" + userId, null
                , paging, true));
    }

    /**
     * Returns an array of numeric IDs for every user the specified user is following.
     * @param screenName Specfies the screen name of the user for whom to return the friends list.
     * @return an array of numeric IDs for every user the specified user is following
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.0
     * @see <a href="http://apiwiki.twitter.com/REST-API-Documentation#friends/ids">Twitter API Wiki / REST API Documentation - Social Graph Methods - friends/ids</a>
     */
    public IDs getFriendsIDs(String screenName) throws TwitterException {
        return new IDs(get(baseURL + "friends/ids.xml?screen_name=" + screenName, true));
    }

    /**
     * Returns an array of numeric IDs for every user the specified user is following.
     * @param screenName Specfies the screen name of the user for whom to return the friends list.
     * @param paging Specifies the page number of the results beginning at 1. A single page contains 5000 ids. This is recommended for users with large ID lists. If not provided all ids are returned.
     * @return an array of numeric IDs for every user the specified user is following
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.1
     * @see <a href="http://apiwiki.twitter.com/REST-API-Documentation#friends/ids">Twitter API Wiki / REST API Documentation - Social Graph Methods - friends/ids</a>
     */
    public IDs getFriendsIDs(String screenName, Paging paging) throws TwitterException {
        return new IDs(get(baseURL + "friends/ids.xml?screen_name=" + screenName
                , null, paging, true));
    }

    /**
     * Returns an array of numeric IDs for every user the specified user is followed by.
     * @return The ID or screen_name of the user to retrieve the friends ID list for.
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.0
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-followers%C2%A0ids">Twitter API Wiki / Twitter REST API Method: followers ids</a>
     */
    public IDs getFollowersIDs() throws TwitterException {
        return new IDs(get(baseURL + "followers/ids.xml", true));
    }

    /**
     * Returns an array of numeric IDs for every user the specified user is followed by.
     * @return The ID or screen_name of the user to retrieve the friends ID list for.
     * @param paging Specifies the page number of the results beginning at 1. A single page contains 5000 ids. This is recommended for users with large ID lists. If not provided all ids are returned.
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.1
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-followers%C2%A0ids">Twitter API Wiki / Twitter REST API Method: followers ids</a>
     */
    public IDs getFollowersIDs(Paging paging) throws TwitterException {
        return new IDs(get(baseURL + "followers/ids.xml", null, paging
                , true));
    }

    /**
     * Returns an array of numeric IDs for every user the specified user is followed by.
     * @param userId Specfies the ID of the user for whom to return the followers list.
     * @return The ID or screen_name of the user to retrieve the friends ID list for.
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.0
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-followers%C2%A0ids">Twitter API Wiki / Twitter REST API Method: followers ids</a>
     */
    public IDs getFollowersIDs(int userId) throws TwitterException {
        return new IDs(get(baseURL + "followers/ids.xml?user_id=" + userId, true));
    }

    /**
     * Returns an array of numeric IDs for every user the specified user is followed by.
     * @param userId Specfies the ID of the user for whom to return the followers list.
     * @param paging Specifies the page number of the results beginning at 1. A single page contains 5000 ids. This is recommended for users with large ID lists. If not provided all ids are returned.
     * @return The ID or screen_name of the user to retrieve the friends ID list for.
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.1
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-followers%C2%A0ids">Twitter API Wiki / Twitter REST API Method: followers ids</a>
     */
    public IDs getFollowersIDs(int userId, Paging paging) throws TwitterException {
        return new IDs(get(baseURL + "followers/ids.xml?user_id=" + userId, null
                , paging, true));
    }

    /**
     * Returns an array of numeric IDs for every user the specified user is followed by.
     * @param screenName Specfies the screen name of the user for whom to return the followers list.
     * @return The ID or screen_name of the user to retrieve the friends ID list for.
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.0
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-followers%C2%A0ids">Twitter API Wiki / Twitter REST API Method: followers ids</a>
     */
    public IDs getFollowersIDs(String screenName) throws TwitterException {
        return new IDs(get(baseURL + "followers/ids.xml?screen_name=" + screenName, true));
    }

    /**
     * Returns an array of numeric IDs for every user the specified user is followed by.
     * @param screenName Specfies the screen name of the user for whom to return the followers list.
     * @param paging Specifies the page number of the results beginning at 1. A single page contains 5000 ids. This is recommended for users with large ID lists. If not provided all ids are returned.
     * @return The ID or screen_name of the user to retrieve the friends ID list for.
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.1
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-followers%C2%A0ids">Twitter API Wiki / Twitter REST API Method: followers ids</a>
     */
    public IDs getFollowersIDs(String screenName, Paging paging) throws TwitterException {
        return new IDs(get(baseURL + "followers/ids.xml?screen_name="
                + screenName, null, paging, true));
    }

    /**
     * Returns an HTTP 200 OK response code and a representation of the requesting user if authentication was successful; returns a 401 status code and an error message if not.  Use this method to test if supplied user credentials are valid.
     *
     * @return extended user
     * @since Twitter4J 2.0.0
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-account%C2%A0verify_credentials">Twitter API Wiki / Twitter REST API Method: account verify_credentials</a>
     */
    public ExtendedUser verifyCredentials() throws TwitterException {
        return new ExtendedUser(get(baseURL + "account/verify_credentials.xml"
                , true), this);
    }

    /**
     * Updates the location
     *
     * @param location the current location of the user
     * @return the updated user
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 1.0.4
     * @see <a href="http://apiwiki.twitter.com/REST%20API%20Documentation#account/updatelocation">Twitter REST API Documentation &gt; Account Methods &gt; account/update_location</a>
     * @deprecated Use updateProfile(String name, String email, String url, String location, String description) instead
     */
    public User updateLocation(String location) throws TwitterException {
        return new User(http.post(baseURL + "account/update_location.xml", new PostParameter[]{new PostParameter("location", location)}, true), this);
    }

    /**
     * Sets values that users are able to set under the "Account" tab of their settings page. Only the parameters specified(non-null) will be updated.
     *
     * @param name Optional. Maximum of 20 characters.
     * @param email Optional. Maximum of 40 characters. Must be a valid email address.
     * @param url Optional. Maximum of 100 characters. Will be prepended with "http://" if not present.
     * @param location Optional. Maximum of 30 characters. The contents are not normalized or geocoded in any way.
     * @param description Optional. Maximum of 160 characters.
     * @return the updated user
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.2
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-account%C2%A0update_profile">Twitter REST API Documentation &gt; Account Methods &gt; account/update_location</a>
     */
    public ExtendedUser updateProfile(String name, String email, String url
            , String location, String description) throws TwitterException {
        List<PostParameter> profile = new ArrayList<PostParameter>(5);
        addParameterToList(profile, "name", name);
        addParameterToList(profile, "email", email);
        addParameterToList(profile, "url", url);
        addParameterToList(profile, "location", location);
        addParameterToList(profile, "description", description);
        return new ExtendedUser(http.post(baseURL + "account/update_profile.xml"
                , profile.toArray(new PostParameter[profile.size()]), true), this);
    }

    /**
     * Returns the remaining number of API requests available to the requesting user before the API limit is reached for the current hour. Calls to rate_limit_status do not count against the rate limit.  If authentication credentials are provided, the rate limit status for the authenticating user is returned.  Otherwise, the rate limit status for the requester's IP address is returned.<br>
     *
     * @return the rate limit status
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 1.1.4
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-account%C2%A0rate_limit_status">Twitter API Wiki / Twitter REST API Method: account rate_limit_status</a>
     */
    public RateLimitStatus rateLimitStatus() throws TwitterException {
        return new RateLimitStatus(http.get(baseURL + "account/rate_limit_status.xml", null != getUserId() && null != getPassword()));
    }

    public final static Device IM = new Device("im");
    public final static Device SMS = new Device("sms");
    public final static Device NONE = new Device("none");

    static class Device {
        final String DEVICE;

        public Device(String device) {
            DEVICE = device;
        }
    }

    /**
     * Sets which device Twitter delivers updates to for the authenticating user.  Sending none as the device parameter will disable IM or SMS updates.
     *
     * @param device new Delivery device. Must be one of: IM, SMS, NONE.
     * @return the updated user
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 1.0.4
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-account%C2%A0update_delivery_device">Twitter API Wiki / Twitter REST API Method: account update_delivery_device</a>
     */
    public User updateDeliverlyDevice(Device device) throws TwitterException {
        return new User(http.post(baseURL + "account/update_delivery_device.xml", new PostParameter[]{new PostParameter("device", device.DEVICE)}, true), this);
    }


    /**
     * Sets one or more hex values that control the color scheme of the authenticating user's profile page on twitter.com.  These values are also returned in the getUserDetail() method.
     * @param profileBackgroundColor optional, can be null
     * @param profileTextColor optional, can be null
     * @param profileLinkColor optional, can be null
     * @param profileSidebarFillColor optional, can be null
     * @param profileSidebarBorderColor optional, can be null
     * @return the updated user
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.0
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-account%C2%A0update_profile_colors">Twitter API Wiki / Twitter REST API Method: account update_profile_colors</a>
     */
    public ExtendedUser updateProfileColors(
            String profileBackgroundColor,
            String profileTextColor,
            String profileLinkColor,
            String profileSidebarFillColor,
            String profileSidebarBorderColor)
            throws TwitterException {
        List<PostParameter> colors = new ArrayList<PostParameter>(5);
        addParameterToList(colors, "profile_background_color"
                , profileBackgroundColor);
        addParameterToList(colors, "profile_text_color"
                , profileTextColor);
        addParameterToList(colors, "profile_link_color"
                , profileLinkColor);
        addParameterToList(colors, "profile_sidebar_fill_color"
                , profileSidebarFillColor);
        addParameterToList(colors, "profile_sidebar_border_color"
                , profileSidebarBorderColor);
        return new ExtendedUser(http.post(baseURL +
                "account/update_profile_colors.xml",
                colors.toArray(new PostParameter[colors.size()]), true), this);
    }

    private void addParameterToList(List<PostParameter> colors,
                                      String paramName, String color) {
        if(null != color){
            colors.add(new PostParameter(paramName,color));
        }
    }

    /**
     * Returns the 20 most recent favorite statuses for the authenticating user or user specified by the ID parameter in the requested format.
     *
     * @return List<Status>
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-favorites">Twitter API Wiki / Twitter REST API Method: favorites</a>
     * @deprecated Use getFavorited() instead
     */
    public List<Status> favorites() throws TwitterException {
        return getFavorites();
    }

    /**
     * Returns the 20 most recent favorite statuses for the authenticating user or user specified by the ID parameter in the requested format.
     *
     * @return List<Status>
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-favorites">Twitter API Wiki / Twitter REST API Method: favorites</a>
     * @since Twitter4J 2.0.1
     */
    public List<Status> getFavorites() throws TwitterException {
        return Status.constructStatuses(get(baseURL + "favorites.xml", new PostParameter[0], true), this);
    }

    /**
     * Returns the 20 most recent favorite statuses for the authenticating user or user specified by the ID parameter in the requested format.
     *
     * @param page the number of page
     * @return List<Status>
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-favorites">Twitter API Wiki / Twitter REST API Method: favorites</a>
     * @deprecated Use getFavorites(int page) instead
     */
    public List<Status> favorites(int page) throws TwitterException {
        return getFavorites(page);
    }

    /**
     * Returns the 20 most recent favorite statuses for the authenticating user or user specified by the ID parameter in the requested format.
     *
     * @param page the number of page
     * @return List<Status>
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-favorites">Twitter API Wiki / Twitter REST API Method: favorites</a>
     * @since Twitter4J 2.0.1
     */
    public List<Status> getFavorites(int page) throws TwitterException {
        return Status.constructStatuses(get(baseURL + "favorites.xml", "page", String.valueOf(page), true), this);
    }

    /**
     * Returns the 20 most recent favorite statuses for the authenticating user or user specified by the ID parameter in the requested format.
     *
     * @param id the ID or screen name of the user for whom to request a list of favorite statuses
     * @return List<Status>
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-favorites">Twitter API Wiki / Twitter REST API Method: favorites</a>
     * @deprecated Use getFavorites(String id) instead
     */
    public List<Status> favorites(String id) throws TwitterException {
        return getFavorites(id);
    }

    /**
     * Returns the 20 most recent favorite statuses for the authenticating user or user specified by the ID parameter in the requested format.
     *
     * @param id the ID or screen name of the user for whom to request a list of favorite statuses
     * @return List<Status>
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-favorites">Twitter API Wiki / Twitter REST API Method: favorites</a>
     * @since Twitter4J 2.0.1
     */
    public List<Status> getFavorites(String id) throws TwitterException {
        return Status.constructStatuses(get(baseURL + "favorites/" + id + ".xml", new PostParameter[0], true), this);
    }

    /**
     * Returns the 20 most recent favorite statuses for the authenticating user or user specified by the ID parameter in the requested format.
     *
     * @param id   the ID or screen name of the user for whom to request a list of favorite statuses
     * @param page the number of page
     * @return List<Status>
     * @throws TwitterException when Twitter service or network is unavailable
     * @deprecated Use getFavorites(String id, int page) instead
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-favorites">Twitter API Wiki / Twitter REST API Method: favorites</a>
     */
    public List<Status> favorites(String id, int page) throws TwitterException {
        return getFavorites(id, page);
    }

    /**
     * Returns the 20 most recent favorite statuses for the authenticating user or user specified by the ID parameter in the requested format.
     *
     * @param id   the ID or screen name of the user for whom to request a list of favorite statuses
     * @param page the number of page
     * @return List<Status>
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.1
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-favorites">Twitter API Wiki / Twitter REST API Method: favorites</a>
     */
    public List<Status> getFavorites(String id, int page) throws TwitterException {
        return Status.constructStatuses(get(baseURL + "favorites/" + id + ".xml", "page", String.valueOf(page), true), this);
    }

    /**
     * Favorites the status specified in the ID parameter as the authenticating user.  Returns the favorite status when successful.
     *
     * @param id the ID of the status to favorite
     * @return Status
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-favorites%C2%A0create">Twitter API Wiki / Twitter REST API Method: favorites create</a>
     */
    public Status createFavorite(long id) throws TwitterException {
        return new Status(http.post(baseURL + "favorites/create/" + id + ".xml", true), this);
    }

    /**
     * Un-favorites the status specified in the ID parameter as the authenticating user.  Returns the un-favorited status in the requested format when successful.
     *
     * @param id the ID of the status to un-favorite
     * @return Status
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-favorites%C2%A0destroy">Twitter API Wiki / Twitter REST API Method: favorites destroy</a>
     */
    public Status destroyFavorite(long id) throws TwitterException {
        return new Status(http.post(baseURL + "favorites/destroy/" + id + ".xml", true), this);
    }

    /**
     * Enables notifications for updates from the specified user to the authenticating user.  Returns the specified user when successful.
     *
     * @param id String
     * @return User
     * @throws TwitterException when Twitter service or network is unavailable
     * @deprecated Use enableNotification(String id) instead
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-notifications%C2%A0follow">Twitter API Wiki / Twitter REST API Method: notifications follow</a>
     */
    public User follow(String id) throws TwitterException {
        return enableNotification(id);
    }

    /**
     * Enables notifications for updates from the specified user to the authenticating user.  Returns the specified user when successful.
     *
     * @param id String
     * @return User
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.1
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-notifications%C2%A0follow">Twitter API Wiki / Twitter REST API Method: notifications follow</a>
     */
    public User enableNotification(String id) throws TwitterException {
        return new User(http.post(baseURL + "notifications/follow/" + id + ".xml", true), this);
    }

    /**
     * Disables notifications for updates from the specified user to the authenticating user.  Returns the specified user when successful.
     *
     * @param id String
     * @return User
     * @throws TwitterException when Twitter service or network is unavailable
     * @deprecated Use disableNotification(String id) instead
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-notifications%C2%A0leave">Twitter API Wiki / Twitter REST API Method: notifications leave</a>
     */
    public User leave(String id) throws TwitterException {
        return disableNotification(id);
    }

    /**
     * Disables notifications for updates from the specified user to the authenticating user.  Returns the specified user when successful.
     *
     * @param id String
     * @return User
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.1
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-notifications%C2%A0leave">Twitter API Wiki / Twitter REST API Method: notifications leave</a>
     */
    public User disableNotification(String id) throws TwitterException {
        return new User(http.post(baseURL + "notifications/leave/" + id + ".xml", true), this);
    }

    /* Block Methods */

    /**
     * Blocks the user specified in the ID parameter as the authenticating user.  Returns the blocked user in the requested format when successful.
     *
     * @param id the ID or screen_name of the user to block
     * @return the blocked user
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 1.0.4
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-blocks%C2%A0create">Twitter API Wiki / Twitter REST API Method: blocks create</a>
     * @deprecated Use createBlock(String id) instead
     */
    public User block(String id) throws TwitterException {
        return new User(http.post(baseURL + "blocks/create/" + id + ".xml", true), this);
    }

    /**
     * Blocks the user specified in the ID parameter as the authenticating user.  Returns the blocked user in the requested format when successful.
     *
     * @param id the ID or screen_name of the user to block
     * @return the blocked user
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.1
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-blocks%C2%A0create">Twitter API Wiki / Twitter REST API Method: blocks create</a>
     */
    public User createBlock(String id) throws TwitterException {
        return new User(http.post(baseURL + "blocks/create/" + id + ".xml", true), this);
    }


    /**
     * Un-blocks the user specified in the ID parameter as the authenticating user.  Returns the un-blocked user in the requested format when successful.
     *
     * @param id the ID or screen_name of the user to block
     * @return the unblocked user
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 1.0.4
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-blocks%C2%A0destroy">Twitter API Wiki / Twitter REST API Method: blocks destroy</a>
     * @deprecated Use destroyBlock(String id) instead
     */
    public User unblock(String id) throws TwitterException {
        return new User(http.post(baseURL + "blocks/destroy/" + id + ".xml", true), this);
    }

    /**
     * Un-blocks the user specified in the ID parameter as the authenticating user.  Returns the un-blocked user in the requested format when successful.
     *
     * @param id the ID or screen_name of the user to block
     * @return the unblocked user
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.1
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-blocks%C2%A0destroy">Twitter API Wiki / Twitter REST API Method: blocks destroy</a>
     */
    public User destroyBlock(String id) throws TwitterException {
        return new User(http.post(baseURL + "blocks/destroy/" + id + ".xml", true), this);
    }

    /* Help Methods */
    /**
     * Returns the string "ok" in the requested format with a 200 OK HTTP status code.
     *
     * @return true if the API is working
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 1.0.4
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-help%C2%A0test">Twitter API Wiki / Twitter REST API Method: help test</a>
     */
    public boolean test() throws TwitterException {
        return -1 != get(baseURL + "help/test.xml", false).
                asString().indexOf("ok");
    }

    /**
     * Returns extended information of the authenticated user.  This information includes design settings, so third party developers can theme their widgets according to a given user's preferences.<br>
     * The call Twitter.getAuthenticatedUser() is equivalent to the call:<br>
     * twitter.getUserDetail(twitter.getUserId());
     *
     * @return UserWithStatus
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 1.1.3
     * @deprecated Use verifyCredentials() instead
     */
    public ExtendedUser getAuthenticatedUser() throws TwitterException {
        return new ExtendedUser(get(baseURL + "account/verify_credentials.xml", true),this);
    }

    /**
     * Returns the same text displayed on http://twitter.com/home when a maintenance window is scheduled, in the requested format.
     *
     * @return the schedule
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 1.0.4
     * @deprecated this method is not supported by the Twitter API anymore
     */
    public String getDowntimeSchedule() throws TwitterException {
        throw new TwitterException(
                "this method is not supported by the Twitter API anymore"
                , new NoSuchMethodException("this method is not supported by the Twitter API anymore"));
    }


    private SimpleDateFormat format = new SimpleDateFormat(
            "EEE, d MMM yyyy HH:mm:ss z", Locale.ENGLISH);

    public void setRetryCount(int retryCount) {
        http.setRetryCount(retryCount);
    }

    public void setRetryIntervalSecs(int retryIntervalSecs) {
        http.setRetryIntervalSecs(retryIntervalSecs);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Twitter twitter = (Twitter) o;

        if (usePostForcibly != twitter.usePostForcibly) return false;
        if (!baseURL.equals(twitter.baseURL)) return false;
        if (!format.equals(twitter.format)) return false;
        if (!http.equals(twitter.http)) return false;
        if (!searchBaseURL.equals(twitter.searchBaseURL)) return false;
        if (!source.equals(twitter.source)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = http.hashCode();
        result = 31 * result + baseURL.hashCode();
        result = 31 * result + searchBaseURL.hashCode();
        result = 31 * result + source.hashCode();
        result = 31 * result + (usePostForcibly ? 1 : 0);
        result = 31 * result + format.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Twitter{" +
                "http=" + http +
                ", baseURL='" + baseURL + '\'' +
                ", searchBaseURL='" + searchBaseURL + '\'' +
                ", source='" + source + '\'' +
                ", usePostForcibly=" + usePostForcibly +
                ", format=" + format +
                '}';
    }
}
