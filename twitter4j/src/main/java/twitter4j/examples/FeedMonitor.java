package twitter4j.examples;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.http.HttpClient;
import twitter4j.http.Response;
import java.util.ArrayList;

/**
 * <p>
 * FeedMonitor is a simple feed monitoring application.<br>
 * FeedMonitor monitors specified feeds and reports newly posted entries to the specified Twitter account every 10 minutes.<br>
 * It is possible to specify multiple configuration files.<br>
 * Numeric parameter will be recognized as monitoring interverval in minutes.<br>
 * Usage: java twitter4j.examples.FeedMonitor [config_file_path ..] [interval(min)]<br>
 * <br>
 * If no configuration file path is specified, FeedMonitor will look for default configuration file name - &quot;feedmonitor.properties&quot;.<br>
 * The configuration file format is Java standard properties file format with following properties:<br>
 * feedurl : the feed URL you want to monitor<br>
 * id : Twitter id<br>
 * password : Twitter password<br>
 * <br>
 * <hr>
 * e.g. a sample properties for monitoring CSS latest news every 10 minutes
 * <pre style="border: solid 1px black;background-color:#AAF">
 * feedurl=http://rss.cnn.com/rss/cnn_latest.rss
 * id=YOUR_TWITTER_ID
 * password=YOUR_TWITTER_PASSWORD</pre>
 * </p>
 */
public class FeedMonitor {
    static Logger log = LoggerFactory.getLogger(FeedMonitor.class);
    /**
     * Main entry point for this application.<br>
     * If config_file_path is not specified, feedmonitor.properties will be used.<br>
     * Usage: java twitter4j.examples.FeedMonitor [config_file_path ..] [interval(min)]
     */
    public static void main(String[] args) {
        int interval = 10;
        List<FeedMonitor> list = new ArrayList<FeedMonitor> ();
        for (String arg : args) {
            try {
                interval = Integer.parseInt(arg);
            } catch (NumberFormatException nfe) {
                list.add(new FeedMonitor(arg));
            }
        }
        if (list.size() == 0) {
            list.add(new FeedMonitor("feedmonitor.properties"));
        } while (true) {
            for (FeedMonitor monitor : list) {
                monitor.check();
            }
            try {
                log.info("Sleeping " + interval + " minutes.");
                Thread.sleep(interval * 60 * 1000);
            } catch (InterruptedException ex) {
            }
        }
    }

    public FeedMonitor(String fileName) {
        this.fileName = fileName;
        log.info("Loading properties from " + fileName);
        try {
            prop.load(new FileInputStream(fileName));
        } catch (IOException ex) {
            log.error("Configuration file not found:" + ex.getMessage());
            System.exit( -1);
        }
        this.twitter = new Twitter(prop.getProperty("id"),
                                   prop.getProperty("password"));
        this.feedurl = prop.getProperty("feedurl");
        this.lastUpdate = new Date(Long.valueOf(prop.getProperty("lastUpdate",
            "0")));
    }

    private Properties prop = new Properties();
    private String feedurl;
    private Twitter twitter;
    private HttpClient http = new HttpClient();
    private Date lastUpdate;
    private String fileName;

    private void check() {
        log.info("Checking feed from {}", feedurl);
        Date latestEntry = lastUpdate;
        log.info("Last update is {}", lastUpdate);
        try {
            Response res = http.get(feedurl);
            List<SyndEntry> entries = new SyndFeedInput().build(res.asDocument()).
                getEntries();

            Collections.sort(entries, new Comparator<SyndEntry> () {
                public int compare(SyndEntry o1, SyndEntry o2) {
                    return o1.getPublishedDate().compareTo(o2.getPublishedDate());
                }
            });
            for (SyndEntry entry : entries) {
                if (lastUpdate.before(entry.getPublishedDate())) {
                    latestEntry = latestEntry.before(entry.getPublishedDate()) ?
                        entry.getPublishedDate() : latestEntry;
                    String title = entry.getTitle().split("\n")[0];
                    String link = entry.getLink();
                    log.info("New entry \"{}\" published at {}", title,
                             entry.getPublishedDate());
                    String status = title + " " + link;
                    if (status.length() > 160) {
                        if (link.length() > 160) {
                            int cutLength = status.length() - 159;
                            status = title.substring(0,
                                title.length() - cutLength) + " " + link;
                        } else {
                            if (title.length() > 160) {
                                status = title.substring(0, 160);
                            } else {
                                status = title;
                            }
                        }
                    }
                    log.info("Updating Twitter.");
//                    System.out.println(status);
                    twitter.update(status);
                    log.info("Done.");
                }
            }
            if (!lastUpdate.equals(latestEntry)) {
                log.info("Updating last update.");
                prop.setProperty("lastUpdate",
                                 String.valueOf(latestEntry.getTime()));
                try {
                    prop.store(new FileOutputStream(fileName), "FeedMonitor");
                } catch (IOException ex1) {
                    log.error("Failed to save configuration file:" +
                              ex1.getMessage());
                }
            } else {
                log.info("No new entry found.");
            }

        } catch (TwitterException te) {
            log.info("Failed to fetch the feed:" + te.getMessage());
        } catch (FeedException fe) {
            log.info("Failed to parse the feed:" + fe.getMessage());
        }
//        Date latestEntry = monitor.monitor(lastUpdate);

    }
}
