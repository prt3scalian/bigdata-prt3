package com.scalian;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import org.apache.spark.*;
import org.apache.spark.api.java.function.*;
import org.apache.spark.streaming.*;
import org.apache.spark.streaming.api.java.*;
import scala.Tuple2;


public class Main {

    public static void main(String[] args) {
        try {
//            new NamexTweet().start("machine learning");
            new SparkNamexTweet().start("machine learning");
        } catch (TwitterException ex) {
            System.out.println(ex);
        } catch ( IOException ex) {
            System.out.println(ex);
        }
    }

    public static class SparkNamexTweet {

        private final static String CONSUMER_KEY = "qosodkQEm2XVgrBjFVg65Beqk";
        private final static String CONSUMER_KEY_SECRET = "lgPyPw56fcGR0UWF9YtIJaiVgy5pOTd4pQap9k5gXd3O5rCTC6";
        private final static String TOKEN = "55223316-V0UV4SLmnkBOwrG8FBUtiZ1QxmiNkkRndeaRUjFGE";
        private final static String TOKEN_SECRET = "XBRl0sIvvQ7F4ySJ9IRLtB8D8YGnzOA3Co3wI9jVSYIaj";


        public List<Status> search(String keyword) {
            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setDebugEnabled(true).setOAuthConsumerKey(CONSUMER_KEY)
                    .setOAuthConsumerSecret(CONSUMER_KEY_SECRET)
                    .setOAuthAccessToken(TOKEN)
                    .setOAuthAccessTokenSecret(TOKEN_SECRET);
            TwitterFactory tf = new TwitterFactory(cb.build());
 
            Twitter twitter = tf.getInstance();
            
            Query query = new Query(keyword + " -filter:retweets -filter:links -filter:replies -filter:images");
            //Query query = new Query(keyword);
            query.setCount(20);
            query.setLocale("en");
            query.setLang("en");
            try {
                QueryResult queryResult = twitter.search(query);
                return queryResult.getTweets();
            } catch (TwitterException e) {
                // ignore
                e.printStackTrace();
            }
            return Collections.emptyList();
        }

        public void start(String keyword) throws TwitterException, IOException {
        	
        	// Create a local StreamingContext with two working thread and batch interval of 1 second
        	SparkConf conf = new SparkConf().setMaster(System.getenv("SPARK_MASTER_URL"))
        									.setAppName("StreamTweetWithSentiment");
        	JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.milliseconds(500));
        	ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setDebugEnabled(true).setOAuthConsumerKey(CONSUMER_KEY)
                    .setOAuthConsumerSecret(CONSUMER_KEY_SECRET)
                    .setOAuthAccessToken(TOKEN)
                    .setOAuthAccessTokenSecret(TOKEN_SECRET);
//            String filters = keyword + " -filter:retweets -filter:links -filter:replies -filter:images";
            List<String> filters = new ArrayList<String>();
//			keyword = keyword + " exclude:retweets exclude:links exclude:replies exclude:images";
            filters.add(keyword);
            System.out.println("SparkNamexTweet:start: TwitterUtils.createJavaStream(jssc, cb, filters);");
        	JavaReceiverInputDStream<Status> tweetDStream = TwitterUtils.createJavaStream(jssc, cb, filters);
        	
        	System.out.println("SparkNamexTweet:start: tweetDStream.print();");
//        	tweetDStream.print();
        	
            
        	System.out.println("SparkNamexTweet:start: tweetDStream.map(status -> {\n" + 
        			"            	TweetWithSentiment sentiment = new SentimentAnalyzer().findSentiment(status.getText());\n" + 
        			"            	System.out.println(sentiment);");
        	
            JavaDStream<TweetWithSentiment> sentimentDStream = tweetDStream.map(status -> {
            	TweetWithSentiment sentiment = new SentimentAnalyzer().findSentiment(status.getText());
            	System.out.println(sentiment);
            	return sentiment;
            	});
            
            sentimentDStream.print();
            
            System.out.println("SparkNamexTweet:start: starting Streaming computation by jssc.start()..........");
            jssc.start();

          try {
				Thread.sleep(120000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
          jssc.stop();
            
        }

    }
    
    public static class NamexTweet {

        private final static String CONSUMER_KEY = "qosodkQEm2XVgrBjFVg65Beqk";
        private final static String CONSUMER_KEY_SECRET = "lgPyPw56fcGR0UWF9YtIJaiVgy5pOTd4pQap9k5gXd3O5rCTC6";
        private final static String TOKEN = "55223316-V0UV4SLmnkBOwrG8FBUtiZ1QxmiNkkRndeaRUjFGE";
        private final static String TOKEN_SECRET = "XBRl0sIvvQ7F4ySJ9IRLtB8D8YGnzOA3Co3wI9jVSYIaj";


        public List<Status> search(String keyword) {
            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setDebugEnabled(true).setOAuthConsumerKey(CONSUMER_KEY)
                    .setOAuthConsumerSecret(CONSUMER_KEY_SECRET)
                    .setOAuthAccessToken(TOKEN)
                    .setOAuthAccessTokenSecret(TOKEN_SECRET);
            TwitterFactory tf = new TwitterFactory(cb.build());
            Twitter twitter = tf.getInstance();
            Query query = new Query(keyword + " -filter:retweets -filter:links -filter:replies -filter:images");
            //Query query = new Query(keyword);
            query.setCount(20);
            query.setLocale("en");
            query.setLang("en");
            try {
                QueryResult queryResult = twitter.search(query);
                return queryResult.getTweets();
            } catch (TwitterException e) {
                // ignore
                e.printStackTrace();
            }
            return Collections.emptyList();
        }

        public void start(String keyword) throws TwitterException, IOException {

            List<Status> list = search(keyword);
            int tweetCount = 0;
            for (Status each : list) {
            	System.out.println("TweetWithSentiment n°"+tweetCount);
                System.out.println(new SentimentAnalyzer().findSentiment(each.getText()));
                tweetCount++;
            }
            
            // TODO :Re-implement search(keyword) method as a spark streaming
        }

    }
}
