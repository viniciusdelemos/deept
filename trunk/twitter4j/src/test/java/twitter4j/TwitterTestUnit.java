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

import junit.framework.TestCase;

import java.io.FileInputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
public class TwitterTestUnit extends TestCase {
    protected Twitter twitterAPI1 = null;
    protected Twitter twitterAPI2 = null;
    protected Twitter unauthenticated = null;
    protected Properties p = new Properties();

    public TwitterTestUnit(String name) {
        super(name);
    }
    protected String id1,id2,id3,pass1,pass2,pass3;
    protected void setUp() throws Exception {
        super.setUp();
        p.load(new FileInputStream("test.properties"));
        id1 = p.getProperty("id1");
        id2 = p.getProperty("id2");
        id3 = p.getProperty("id3");
        pass1 = p.getProperty("pass1");
        pass2 = p.getProperty("pass2");
        pass3 = p.getProperty("pass3");
        twitterAPI1 = new Twitter(id1, pass1);
        twitterAPI1.setRetryCount(3);
        twitterAPI1.setRetryIntervalSecs(10);
        twitterAPI2 = new Twitter(id2, pass2);
        twitterAPI2.setRetryCount(3);
        twitterAPI2.setRetryIntervalSecs(10);
        unauthenticated = new Twitter();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetPublicTimeline() throws Exception {
        List<Status> statuses;
        statuses = twitterAPI1.getPublicTimeline();
        assertTrue("size", 5 < statuses.size());
        statuses = twitterAPI1.getPublicTimeline(12345);
        assertTrue("size", 5 < statuses.size());

    }

    public void testGetFriendsTimeline()throws Exception {
        Date startDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        String dateStr = (cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.DATE);


        String id1status = dateStr+":id1";
        String id2status = dateStr+":id2";
        Status status = twitterAPI1.update(id1status);
        assertEquals(id1status, status.getText());
        Thread.sleep(3000);
        Status status2 = twitterAPI2.update(id2status);
        assertEquals(id2status, status2.getText());

        List<Status> actualReturn;

        actualReturn = twitterAPI1.getFriendsTimeline();
        assertTrue(actualReturn.size() > 0);
        actualReturn = twitterAPI1.getFriendsTimeline(new Date(0));
        assertTrue(actualReturn.size() > 0);
        actualReturn = twitterAPI1.getFriendsTimeline(1l);
        assertTrue(actualReturn.size() > 0);
        //this is necessary because the twitter server's clock tends to delay
        cal.add(Calendar.MINUTE,-20);
        Date twentyMinutesBefore = cal.getTime();
        actualReturn = twitterAPI1.getFriendsTimeline(twentyMinutesBefore);
        assertTrue(actualReturn.size() > 0);
        actualReturn = twitterAPI1.getFriendsTimeline(1000l);
        assertTrue(actualReturn.size() > 0);

        actualReturn = twitterAPI2.getFriendsTimeline(id1);
        assertTrue(actualReturn.size() > 0);


        actualReturn = twitterAPI1.getFriendsTimeline(id2, new Date(0));
        assertTrue(actualReturn.size() > 0);
        actualReturn = twitterAPI1.getFriendsTimeline(id2, 1l);
        assertTrue(actualReturn.size() > 0);
        actualReturn = twitterAPI1.getFriendsTimeline(id2, new Date());
//        assertTrue(actualReturn.size() == 0);
        actualReturn = twitterAPI1.getFriendsTimeline(id2, status2.getId());
//        assertTrue(actualReturn.size() == 0);
        actualReturn = twitterAPI1.getFriendsTimelineByPage(1);
        assertTrue(actualReturn.size() > 0);
        actualReturn = twitterAPI1.getFriendsTimeline(1);
        assertTrue(actualReturn.size() > 0);


    }
    public void testGetUserDetail() throws Exception{
        UserWithStatus uws = twitterAPI1.getUserDetail(id1);
        assertEquals(id1, uws.getName());
        assertEquals(id1,uws.getScreenName());
        assertNotNull(uws.getLocation());
        assertNotNull(uws.getDescription());
        assertNotNull(uws.getProfileImageURL());
        assertNotNull(uws.getURL());
        assertFalse(uws.isProtected());

        assertTrue(0 <= uws.getFavouritesCount());
        assertTrue(0 <= uws.getFollowersCount());
        assertTrue(0 <= uws.getFriendsCount());
        assertNotNull(uws.getCreatedAt());
        assertNotNull(uws.getTimeZone());
        assertNotNull(uws.getProfileBackgroundImageUrl());
        assertNotNull(uws.getProfileBackgroundTile());
        assertFalse(uws.isFollowing());
        assertFalse(uws.isNotifications());

        assertTrue(0 <= uws.getStatusesCount());
        assertNotNull(uws.getProfileBackgroundColor());
        assertNotNull(uws.getProfileTextColor());
        assertNotNull(uws.getProfileLinkColor());
        assertNotNull(uws.getProfileSidebarBorderColor());
        assertNotNull(uws.getProfileSidebarFillColor());
        assertNotNull(uws.getProfileTextColor());

        assertTrue(1 < uws.getFollowersCount());
        assertNotNull(uws.getStatusCreatedAt());
        assertNotNull(uws.getStatusText());
        assertNotNull(uws.getStatusSource());
        assertFalse(uws.isStatusFavorited());
        assertEquals(-1,uws.getStatusInReplyToStatusId());
        assertEquals(-1,uws.getStatusInReplyToUserId());
        assertFalse(uws.isStatusFavorited());
        assertNull(uws.getStatusInReplyToScreenName());



        uws = twitterAPI1.getAuthenticatedUser();
        assertEquals(id1, uws.getName());
        assertTrue(0 <= uws.getFavouritesCount());
        assertTrue(0 <= uws.getFollowersCount());
        assertTrue(0 <= uws.getFriendsCount());
        assertTrue(0 <= uws.getStatusesCount());
        assertNotNull(uws.getProfileBackgroundColor());
        assertNotNull(uws.getProfileTextColor());
        assertNotNull(uws.getProfileLinkColor());
        assertNotNull(uws.getProfileSidebarBorderColor());
        assertNotNull(uws.getProfileSidebarFillColor());
        assertNotNull(uws.getProfileTextColor());

        uws = twitterAPI1.getUserDetail("6459452");
        assertEquals(6459452,uws.getId());
        assertEquals("fast_ts",uws.getName());
        assertEquals("fast_ts",uws.getScreenName());
        assertEquals("",uws.getLocation());
        assertEquals("",uws.getDescription());
        assertTrue(uws.getProfileImageURL().toString().contains("/25787342/_____-1_normal.gif"));
        assertNull(uws.getURL());
        assertEquals(true,uws.isProtected());
        assertEquals(2,uws.getFollowersCount());
        assertNull(uws.getStatusCreatedAt());
        assertNull(uws.getStatusText());
        assertNull(uws.getStatusSource());
        assertFalse(uws.isStatusFavorited());
        assertEquals(-1,uws.getStatusInReplyToStatusId());
        assertEquals(-1,uws.getStatusInReplyToUserId());
        assertFalse(uws.isStatusFavorited());
        assertNull(uws.getStatusInReplyToScreenName());

        //test case for TFJ-91 null pointer exception getting user detail on users with no statuses
        //http://yusuke.homeip.net/jira/browse/TFJ-91
        twitterAPI1.getUserDetail("twit4jnoupdate");
        twitterAPI1.getUserDetail("tigertest");
    }

    public void testGetAuthenticatedUser() throws Exception {
        assertEquals(id1, twitterAPI1.getAuthenticatedUser().getScreenName());
        assertEquals(id1, new Twitter(id3, pass3).getAuthenticatedUser().getName());
        assertEquals(id1,new Twitter(id3, pass3).verifyCredentials().getName());
    }


    public void testGetUserTimeline_Show() throws Exception{
        List<Status> statuses;
        statuses = twitterAPI1.getUserTimeline();
        assertTrue("size", 0 < statuses.size());
        statuses = twitterAPI2.getUserTimeline(id1);
        assertTrue("size", 0 < statuses.size());
        statuses = twitterAPI1.getUserTimeline(999383469l);
        assertTrue("size", 0 < statuses.size());
        statuses = twitterAPI2.getUserTimeline(id1, 10);
        assertTrue("size", 0 < statuses.size());
        statuses = twitterAPI1.getUserTimeline(15, new Date(0));
        assertTrue("size", 0 < statuses.size());
        statuses = twitterAPI1.getUserTimeline(15, 999383469l);
        assertTrue("size", 0 < statuses.size());
        statuses = twitterAPI1.getUserTimeline(id1, new Date(0));
        assertTrue("size", 0 < statuses.size());
        statuses = twitterAPI1.getUserTimeline(id1, 999383469l);
        assertTrue("size", 0 < statuses.size());

        statuses = twitterAPI1.getUserTimeline(id1, 20, new Date(0));
        assertTrue("size", 0 < statuses.size());
        statuses = twitterAPI1.getUserTimeline(id1, 20, 999383469l);
        assertTrue("size", 0 < statuses.size());
    }
    public void testShow() throws Exception{
        Status status = twitterAPI2.show(1000l);
        assertEquals(52,status.getUser().getId());
        Status status2 = unauthenticated.show(1000l);
        assertEquals(52,status2.getUser().getId());

        status2 = unauthenticated.show(999383469l);
        assertEquals("01010100 01110010 01101001 01110101 01101101 01110000 01101000       <3",status2.getText());



    }
    public void testUpdate() throws Exception{
        String date = new java.util.Date().toString()+"test";
        Status status = twitterAPI1.update(date);
        assertEquals(date, status.getText());
        Status status2 = twitterAPI2.update("@" + id1 + " " + date, status.getId());
        assertEquals("@" + id1 + " " + date, status2.getText());
        assertEquals(status.getId(), status2.getInReplyToStatusId());
        assertEquals(twitterAPI1.getAuthenticatedUser().getId(), status2.getInReplyToUserId());
    }
    public void testDestoryStatus() throws Exception{
        String date = new java.util.Date().toString()+"test";
        Status status = twitterAPI1.update(date);
        assertEquals("",date, status.getText());
        twitterAPI1.destroyStatus(status.getId());
    }
    public void testGetFriends() throws Exception{
        List<User> actualReturn = twitterAPI1.getFriends("al3x");
        boolean found = false;
        for(User user: actualReturn){
            found = found || user.getName().equals("Yusuke Yamamoto");
        }
        assertFalse(found);
        assertTrue(90 < twitterAPI2.getFriends("akr",2).size());
    }

    public void testSocialGraphMethods() throws Exception {
        IDs ids;
        ids = twitterAPI1.getFriendsIDs();
        assertIDExsits(ids, 4933401);
        ids = twitterAPI1.getFriendsIDs(4933401);
        assertIDExsits(ids, 6358482);
        ids = twitterAPI1.getFriendsIDs("yusukey");
        assertIDExsits(ids, 6358482);

        ids = twitterAPI1.getFollowersIDs();
        assertIDExsits(ids, 4933401);
        ids = twitterAPI1.getFollowersIDs(4933401);
        assertIDExsits(ids, 6358482);
        ids = twitterAPI1.getFollowersIDs("yusukey");
        assertIDExsits(ids, 6358482);
    }

    private void assertIDExsits(IDs ids, int idToFind){
        boolean found = false;
        for(int id : ids.getIDs()){
            if(id == idToFind){
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    public void testAccountMethods() throws Exception{
        twitterAPI1.verifyCredentials();
        try {
            new Twitter("doesnotexist--", "foobar").verifyCredentials();
            fail("should throw TwitterException");
        } catch (TwitterException te) {

        }
        String location = "location:"+Math.random();
        User user = twitterAPI1.updateLocation(location);
        assertEquals(location,user.getLocation());

        twitterAPI1.updateDeliverlyDevice(Twitter.SMS);
        try {
            twitterAPI1.create(id2);
            twitterAPI1.follow(id2);
        } catch (twitter4j.TwitterException te) {
            te.printStackTrace();
        }
        try {
            twitterAPI2.create(id1);
            twitterAPI2.follow(id1);
        } catch (twitter4j.TwitterException te) {
            te.printStackTrace();
        }
        assertTrue(twitterAPI1.exists(id1,id2));

        ExtendedUser eu;
        eu = twitterAPI1.updateProfileColors("f00", "f0f", "0ff", "0f0", "f0f");
        assertEquals("f00", eu.getProfileBackgroundColor());
        assertEquals("f0f", eu.getProfileTextColor());
        assertEquals("0ff", eu.getProfileLinkColor());
        assertEquals("0f0", eu.getProfileSidebarFillColor());
        assertEquals("f0f", eu.getProfileSidebarBorderColor());
        eu = twitterAPI1.updateProfileColors("f0f", "f00", "f0f", "0ff", "0f0");
        assertEquals("f0f", eu.getProfileBackgroundColor());
        assertEquals("f00", eu.getProfileTextColor());
        assertEquals("f0f", eu.getProfileLinkColor());
        assertEquals("0ff", eu.getProfileSidebarFillColor());
        assertEquals("0f0", eu.getProfileSidebarBorderColor());
        eu = twitterAPI1.updateProfileColors("87bc44", "9ae4e8", "000000", "0000ff", "e0ff92");
        assertEquals("87bc44", eu.getProfileBackgroundColor());
        assertEquals("9ae4e8", eu.getProfileTextColor());
        assertEquals("000000", eu.getProfileLinkColor());
        assertEquals("0000ff", eu.getProfileSidebarFillColor());
        assertEquals("e0ff92", eu.getProfileSidebarBorderColor());
        eu = twitterAPI1.updateProfileColors("f0f", null, "f0f", null, "0f0");
        assertEquals("f0f", eu.getProfileBackgroundColor());
        assertEquals("9ae4e8", eu.getProfileTextColor());
        assertEquals("f0f", eu.getProfileLinkColor());
        assertEquals("0000ff", eu.getProfileSidebarFillColor());
        assertEquals("0f0", eu.getProfileSidebarBorderColor());
        eu = twitterAPI1.updateProfileColors(null, "f00", null, "0ff", null);
        assertEquals("f0f", eu.getProfileBackgroundColor());
        assertEquals("f00", eu.getProfileTextColor());
        assertEquals("f0f", eu.getProfileLinkColor());
        assertEquals("0ff", eu.getProfileSidebarFillColor());
        assertEquals("0f0", eu.getProfileSidebarBorderColor());
        eu = twitterAPI1.updateProfileColors("9ae4e8", "000000", "0000ff", "e0ff92", "87bc44");
        assertEquals("9ae4e8", eu.getProfileBackgroundColor());
        assertEquals("000000", eu.getProfileTextColor());
        assertEquals("0000ff", eu.getProfileLinkColor());
        assertEquals("e0ff92", eu.getProfileSidebarFillColor());
        assertEquals("87bc44", eu.getProfileSidebarBorderColor());
    }
    public void testFavoriteMethods() throws Exception{
        Status status = twitterAPI1.update("test");
        twitterAPI2.createFavorite(status.getId());
        assertTrue(twitterAPI2.favorites().size() >0);
        twitterAPI2.destroyFavorite(status.getId());
    }
    public void testFollowers() throws Exception{
        List<User> actualReturn = twitterAPI1.getFollowers();
        assertTrue(actualReturn.size() > 0);
        actualReturn = twitterAPI1.getFollowers(1);
        assertTrue(actualReturn.size() > 0);
        actualReturn = twitterAPI1.getFollowers(2);
        assertEquals(0,actualReturn.size());

        actualReturn = twitterAPI2.getFollowers();
        assertTrue(actualReturn.size() > 0);
        actualReturn = twitterAPI2.getFollowers("yusukey");
        assertTrue(actualReturn.size() > 90);
        actualReturn = twitterAPI2.getFollowers("yusukey",2);
        assertTrue(actualReturn.size() > 10);
    }
    public void testFeatured() throws Exception{
        List<User> actualReturn = twitterAPI1.getFeatured();
        assertTrue(actualReturn.size() > 9);
    }

    public void testGetDirectMessages() throws Exception {
        try {
            twitterAPI1.create(id2);
            twitterAPI1.follow(id2);
        } catch (twitter4j.TwitterException te) {
            te.printStackTrace();
        }
        try {
            twitterAPI2.create(id1);
            twitterAPI2.follow(id1);
        } catch (twitter4j.TwitterException te) {
            te.printStackTrace();
        }
        Thread.sleep(3000);

        String expectedReturn = new Date() + ":directmessage test";
//        twitterAPI2.sendDirectMessage(id1,expectedReturn);
        twitterAPI1.sendDirectMessage(id2, expectedReturn);
//        twitterAPI2.sendDirectMessage("yusukey",expectedReturn);
        List<DirectMessage> actualReturn = twitterAPI2.getDirectMessages();
        assertTrue(actualReturn.get(0).getText().contains("directmessage test"));
        actualReturn = twitterAPI2.getDirectMessages(actualReturn.get(1).getId());
        assertEquals(1, actualReturn.size());

//        String expectedReturn = new Date()+":directmessage test";
        DirectMessage message = twitterAPI1.sendDirectMessage(id2, expectedReturn);
        assertEquals("", expectedReturn, message.getText());
        Thread.sleep(5000);
        actualReturn = twitterAPI2.getDirectMessages(new Date(System.currentTimeMillis() - (1000 * 60 * 100)));
        assertEquals("", expectedReturn, actualReturn.get(0).getText());
        assertEquals("", id2, actualReturn.get(0).getRecipient().getName());
        assertEquals("", id1, actualReturn.get(0).getSender().getName());

        //test for TFJ-4
        //http://yusuke.homeip.net/jira/browse/TFJ-4
        actualReturn = twitterAPI1.getDirectMessages(new Date());
//        assertEquals(0,actualReturn.size());

        actualReturn = twitterAPI1.getDirectMessages();
        int size = actualReturn.size();
        twitterAPI2.sendDirectMessage(id1, String.valueOf(System.currentTimeMillis()));
        twitterAPI2.sendDirectMessage(id1, String.valueOf(System.currentTimeMillis()));
        twitterAPI2.sendDirectMessage(id1, String.valueOf(System.currentTimeMillis()));
        twitterAPI2.sendDirectMessage(id1, String.valueOf(System.currentTimeMillis()));
        twitterAPI2.sendDirectMessage(id1, String.valueOf(System.currentTimeMillis()));

        message = twitterAPI1.deleteDirectMessage(actualReturn.get(0).getId());
        assertEquals(message.getId(), actualReturn.get(0).getId());
        assertTrue(5 <= twitterAPI1.getDirectMessages().size());

        actualReturn = twitterAPI1.getSentDirectMessages();
        assertTrue(5 < actualReturn.size());
        assertEquals(id1, actualReturn.get(0).getSender().getName());
        assertEquals(id2, actualReturn.get(0).getRecipient().getName());

        actualReturn = twitterAPI1.getSentDirectMessages(10, 10);
        assertTrue(5 < actualReturn.size());

        actualReturn = twitterAPI1.getDirectMessagesByPage(1);
        assertTrue(5 <= twitterAPI1.getDirectMessages().size());

    }
    public void testCreateDestroyFriend() throws Exception{
        User user;
        try {
            user = twitterAPI2.destroyFriendship(id1);
        } catch (TwitterException te) {
            //ensure destory id1 before the actual test
        }

        try {
            user = twitterAPI2.destroyFriendship(id1);
        } catch (TwitterException te) {
            assertEquals(403, te.getStatusCode());
        }
        user = twitterAPI2.createFriendship(id1, true);
        assertEquals(id1, user.getName());
        // the Twitter API is not returning appropriate notifications value
        // http://code.google.com/p/twitter-api/issues/detail?id=474
//        ExtendedUser detail = twitterAPI2.getUserDetail(id1);
//        assertTrue(detail.isNotificationEnabled());
        try {
            user = twitterAPI2.createFriendship(id2);
            fail("shouldn't be able to befrinend yourself");
        } catch (TwitterException te) {
            assertEquals(403, te.getStatusCode());
        }
        try {
            user = twitterAPI2.createFriendship("doesnotexist--");
            fail("non-existing user");
        } catch (TwitterException te) {
            //now befriending with non-existing user returns 404
            //http://groups.google.com/group/twitter-development-talk/browse_thread/thread/bd2a912b181bc39f
            assertEquals(404, te.getStatusCode());
        }

    }
    public void testGetReplies() throws Exception{
        twitterAPI2.update("@"+id1+" reply to id1");
        List<Status> statuses = twitterAPI1.getReplies();
        assertTrue(statuses.size() > 0);
        System.out.println(statuses.get(0).getText());
        assertTrue(-1 != statuses.get(0).getText().indexOf(" reply to id1"));

        statuses = twitterAPI1.getRepliesByPage(1);
        assertTrue(statuses.size() > 0);
        statuses = twitterAPI1.getReplies(1);
        assertTrue(statuses.size() > 0);
        statuses = twitterAPI1.getReplies(1l,1);
        assertTrue(statuses.size() > 0);
        statuses = twitterAPI1.getReplies(1l);
        assertTrue(statuses.size() > 0);
        assertTrue(-1 != statuses.get(0).getText().indexOf(" reply to id1"));
    }

    public void testNotification() throws Exception {
        try {
            twitterAPI2.create(id1);
        } catch (TwitterException te) {

        }
        try {
            twitterAPI2.follow(id1);
        } catch (TwitterException te) {

        }
        twitterAPI2.leave(id1);
        try {
            twitterAPI2.leave(id1);
            // Twitter API doesn't return error as of 4/18/2009
//            fail("should fail");
        } catch (TwitterException te) {

        }
        try {
            twitterAPI2.create(id1);
        } catch (TwitterException te) {

        }
        try {
            twitterAPI2.follow(id1);
        } catch (TwitterException te) {

        }
        try {
            twitterAPI2.follow(id1);
            fail("should fail");
        } catch (TwitterException te) {

        }
    }
    public void testBlock() throws Exception {
        twitterAPI2.block(id1);
        twitterAPI2.unblock(id1);
    }


    public void testRateLimitStatus() throws Exception{
        RateLimitStatus status = twitterAPI1.rateLimitStatus();
        assertTrue(10 < status.getHourlyLimit());
        assertTrue(10 < status.getRemainingHits());
    }
    public void testTest() throws Exception {
        assertTrue(twitterAPI2.test());
    }
    public void testDowntimeSchedule() throws Exception {
        System.out.println(twitterAPI2.getDowntimeSchedule());
    }
    public void testSearch() throws Exception {
        String queryStr = "thisisarondomstringforatestcase from:twit4j";
        Query query = new Query(queryStr);
        QueryResult queryResult = unauthenticated.search(query);
        assertEquals(0, queryResult.getSinceId());
        assertTrue(1265204883 < queryResult.getMaxId());
        System.out.println(queryResult.getRefreshUrl());
        assertTrue(queryResult.getRefreshUrl().contains(queryStr));
        assertEquals(15, queryResult.getResultsPerPage());
        assertEquals(-1, queryResult.getTotal());
        //warning is not included in the response anymore - 4/24/2009
//        assertTrue(result.getWarning().contains("adjusted"));
        assertTrue(3 > queryResult.getCompletedIn());
        assertEquals(1, queryResult.getPage());
        assertEquals(queryStr, queryResult.getQuery());

        List<Tweet> tweets = queryResult.getTweets();
        assertEquals(1, tweets.size());
        assertNotNull(tweets.get(0).getText());
        assertNull(tweets.get(0).getToUser());
        assertEquals(-1, tweets.get(0).getToUserId());
        assertNotNull(tweets.get(0).getCreatedAt());
        assertEquals("twit4j", tweets.get(0).getFromUser());
        assertEquals(1620730, tweets.get(0).getFromUserId());
        assertEquals(1525853472, tweets.get(0).getId());
        assertNotNull(tweets.get(0).getIsoLanguageCode());
        assertTrue(tweets.get(0).getProfileImageUrl().contains(".jpg") ||tweets.get(0).getProfileImageUrl().contains(".png") );
        assertTrue(tweets.get(0).getSource().contains("twitter"));


        query = new Query("from:twit4j doesnothit");
        queryResult = unauthenticated.search(query);
        assertEquals(0, queryResult.getSinceId());
        assertEquals(-1, queryResult.getMaxId());
        assertNull(queryResult.getRefreshUrl());
        assertEquals(15, queryResult.getResultsPerPage());
        assertEquals(-1, queryResult.getTotal());
        assertNull(queryResult.getWarning());
        assertTrue(1 > queryResult.getCompletedIn());
        assertEquals(1, queryResult.getPage());
        assertEquals("from:twit4j doesnothit", queryResult.getQuery());
    }

    public void testProperties() throws Exception{
        Twitter twitter;
        String test = "t4j";
        String override = "system property";


        System.clearProperty("twitter4j.user");
        twitter = new Twitter();
        assertNull(twitter.getUserId());

        twitter.setUserId(test);
        assertEquals(test, twitter.getUserId());
        System.setProperty("twitter4j.user", override);
        twitter = new Twitter();
        assertEquals(override, twitter.getUserId());
        twitter.setUserId(test);
        assertEquals(override, twitter.getUserId());
        System.clearProperty("twitter4j.user");

        System.clearProperty("twitter4j.password");
        twitter = new Twitter();
        assertNull(twitter.getPassword());

        twitter.setPassword(test);
        assertEquals(test, twitter.getPassword());
        System.setProperty("twitter4j.password", override);
        twitter = new Twitter();
        assertEquals(override, twitter.getPassword());
        twitter.setPassword(test);
        assertEquals(override, twitter.getPassword());
        System.clearProperty("twitter4j.password");


        System.clearProperty("twitter4j.source");
        twitter = new Twitter();
        assertEquals("Twitter4J", twitter.getSource());

        twitter.setSource(test);
        assertEquals(test, twitter.getSource());
        System.setProperty("twitter4j.source", override);
        twitter = new Twitter();
        assertEquals(override, twitter.getSource());
        twitter.setSource(test);
        assertEquals(override, twitter.getSource());
        System.clearProperty("twitter4j.source");


        System.clearProperty("twitter4j.clientVersion");
        twitter = new Twitter();
        assertEquals(Twitter.VERSION, twitter.getClientVersion());

        twitter.setClientVersion(test);
        assertEquals(test, twitter.getClientVersion());
        System.setProperty("twitter4j.clientVersion", override);
        twitter = new Twitter();
        assertEquals(override, twitter.getClientVersion());
        twitter.setClientVersion(test);
        assertEquals(override, twitter.getClientVersion());
        System.clearProperty("twitter4j.clientVersion");


        System.clearProperty("twitter4j.clientURL");
        twitter = new Twitter();
        assertEquals("http://yusuke.homeip.net/twitter4j/en/twitter4j-" + twitter.VERSION + ".xml", twitter.getClientURL());

        twitter.setClientURL(test);
        assertEquals(test, twitter.getClientURL());
        System.setProperty("twitter4j.clientURL", override);
        twitter = new Twitter();
        assertEquals(override, twitter.getClientURL());
        twitter.setClientURL(test);
        assertEquals(override, twitter.getClientURL());
        System.clearProperty("twitter4j.clientURL");



        System.clearProperty("twitter4j.http.userAgent");
        twitter = new Twitter();
        assertEquals("twitter4j http://yusuke.homeip.net/twitter4j/ /" + twitter.VERSION, twitter.http.getRequestHeader("User-Agent"));

        twitter.setUserAgent(test);
        assertEquals(test, twitter.getUserAgent());
        System.setProperty("twitter4j.http.userAgent", override);
        twitter = new Twitter();
        assertEquals(override, twitter.getUserAgent());
        twitter.setUserAgent(test);
        assertEquals(override, twitter.getUserAgent());
        System.clearProperty("twitter4j.http.userAgent");

        System.clearProperty("twitter4j.http.proxyHost");
        twitter = new Twitter();
        assertEquals(null, twitter.http.getProxyHost());

        twitter.setHttpProxy(test,10);
        assertEquals(test, twitter.http.getProxyHost());
        System.setProperty("twitter4j.http.proxyHost", override);
        twitter = new Twitter();
        assertEquals(override, twitter.http.getProxyHost());
        twitter.setHttpProxy(test,10);
        assertEquals(override, twitter.http.getProxyHost());
        System.clearProperty("twitter4j.http.proxyHost");

        System.clearProperty("twitter4j.http.proxyPort");
        twitter = new Twitter();
        assertEquals(0, twitter.http.getProxyPort());

        twitter.setHttpProxy(test,10);
        assertEquals(10, twitter.http.getProxyPort());
        System.setProperty("twitter4j.http.proxyPort", "100");
        twitter = new Twitter();
        assertEquals(100, twitter.http.getProxyPort());
        twitter.setHttpProxy(test,10);
        assertEquals(100, twitter.http.getProxyPort());
        System.clearProperty("twitter4j.http.proxyPort");


        System.clearProperty("twitter4j.http.proxyUser");
        twitter = new Twitter();
        assertEquals(null, twitter.http.getProxyAuthUser());

        twitter.setHttpProxyAuth(test,test);
        assertEquals(test, twitter.http.getProxyAuthUser());
        System.setProperty("twitter4j.http.proxyUser", override);
        twitter = new Twitter();
        assertEquals(override, twitter.http.getProxyAuthUser());
        twitter.setHttpProxyAuth(test,test);
        assertEquals(override, twitter.http.getProxyAuthUser());
        System.clearProperty("twitter4j.http.proxyUser");


        System.clearProperty("twitter4j.http.proxyPassword");
        twitter = new Twitter();
        assertEquals(null, twitter.http.getProxyAuthPassword());

        twitter.setHttpProxyAuth(test,test);
        assertEquals(test, twitter.http.getProxyAuthPassword());
        System.setProperty("twitter4j.http.proxyPassword",  override);
        twitter = new Twitter();
        assertEquals(override, twitter.http.getProxyAuthPassword());
        twitter.setHttpProxyAuth(test,test);
        assertEquals(override, twitter.http.getProxyAuthPassword());
        System.clearProperty("twitter4j.http.proxyPassword");


        System.clearProperty("twitter4j.http.connectionTimeout");
        twitter = new Twitter();
        assertEquals(10000, twitter.http.getConnectionTimeout());

        twitter.setHttpConnectionTimeout(10);
        assertEquals(10, twitter.http.getConnectionTimeout());
        System.setProperty("twitter4j.http.connectionTimeout", "100");
        twitter = new Twitter();
        assertEquals(100, twitter.http.getConnectionTimeout());
        twitter.setHttpConnectionTimeout(10);
        assertEquals(100, twitter.http.getConnectionTimeout());
        System.clearProperty("twitter4j.http.connectionTimeout");


        System.clearProperty("twitter4j.http.readTimeout");
        twitter = new Twitter();
        assertEquals(30000, twitter.http.getReadTimeout());

        twitter.setHttpReadTimeout(10);
        assertEquals(10, twitter.http.getReadTimeout());
        System.setProperty("twitter4j.http.readTimeout",  "100");
        twitter = new Twitter();
        assertEquals(100, twitter.http.getReadTimeout());
        twitter.setHttpConnectionTimeout(10);
        assertEquals(100, twitter.http.getReadTimeout());
        System.clearProperty("twitter4j.http.readTimeout");
    }
}
