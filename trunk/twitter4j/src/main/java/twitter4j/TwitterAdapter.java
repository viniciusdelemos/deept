package twitter4j;

import java.util.List;

/**
 * A handy adapter of TwitterListener.
 * @see twitter4j.AsyncTwitter
 * @see twitter4j.TwitterListener
 */
public class TwitterAdapter implements TwitterListener {
    public TwitterAdapter() {
    }
    public void gotPublicTimeline(List<Status> statuses){
    }
    public void gotFriendsTimeline(List<Status> statuses){
    }
    public void gotUserTimeline(List<Status> statuses){
    }
    public void gotShow(Status statuses){
    }
    public void updated(Status statuses){
    }
    public void gotReplies(List<Status> statuses){
    }
    public void destroyedStatus(Status destroyedStatus){
    }
    public void gotFriends(List<UserWithStatus> users){
    }
    public void gotFollowers(List<UserWithStatus> users){
    }
    public void gotFeatured(List<User> users){
    }
    public void gotUserDetail(UserWithStatus userWithStatus){
    }
    public void gotDirectMessages(List<DirectMessage> messages){
    }
    public void gotSentDirectMessages(List<DirectMessage> messages){
    }
    public void sentDirectMessage(DirectMessage message){
    }
    public void deletedDirectMessage(DirectMessage message){
    }
    public void created(User user){
    }
    public void destroyed(User user){
    }
    public void gotExists(boolean exists) {
    }
    public void updatedLocation(User user){
    }
    public void gotRateLimitStatus(RateLimitStatus status){
    }
    public void updatedDeliverlyDevice(User user){
    }
    public void gotFavorites(List<Status> statuses){
    }
    public void createdFavorite(Status status){
    }
    public void destroyedFavorite(Status status){
    }
    public void followed(User user){
    }
    public void left(User user){
    }
    public void blocked(User user){
    }
    public void unblocked(User user){
    }
    public void tested(boolean test){
    }
    public void gotDowntimeSchedule(String schedule){
    }
    public void searched(QueryResult result){
    }
    /**
     * @param ex TwitterException
     * @param method int
     */
    public void onException(TwitterException ex,int method) {
    }
}
