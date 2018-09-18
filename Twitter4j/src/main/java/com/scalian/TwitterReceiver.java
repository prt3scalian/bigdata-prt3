package com.scalian;
import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.Authorization;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.conf.ConfigurationBuilder;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.receiver.Receiver;

public class TwitterReceiver extends Receiver<Status>{
	
	private Authorization authorization;
	private List<String> filters;
	private StorageLevel storageLevel;
	
	private volatile Boolean stopped = false;
	private volatile TwitterStream twitterStream;

	public TwitterReceiver(StorageLevel _storageLevel) {
		super(_storageLevel);
		this.storageLevel = _storageLevel;
	}
	
	public TwitterReceiver(ConfigurationBuilder _twitterConfBuilder, List<String> _filters, StorageLevel _storageLevel) {
		super(_storageLevel);
		
		this.authorization = new OAuthAuthorization(_twitterConfBuilder.build());
		this.filters = _filters;
		this.storageLevel = _storageLevel;
	}

	@Override
	public void onStart() {
		try {
            
            TwitterStream newTwitterStream = new TwitterStreamFactory().getInstance(authorization);
            // create stream
            newTwitterStream.addListener(new StatusListener() {
            	
            	@Override
				public void onStatus(Status status) {
            		System.out.println("\n\n TwitterReceiver:onStatus:status.getText(): .................\n"+status.getText());
					store(status);
				}
            	
            	@Override
				public void onDeletionNotice(StatusDeletionNotice arg0) {	
				}

				@Override
				public void onScrubGeo(long arg0, long arg1) {					
				}

				@Override
				public void onStallWarning(StallWarning arg0) {
				}
				@Override
				public void onTrackLimitationNotice(int arg0) {	
				}

				@Override
				public void onException(Exception e) {
					if (!stopped) {
						restart("Error receiveing tweets", e);
					}
				}
    	
            });
            
            FilterQuery query = new FilterQuery();
            if (filters.size() > 0 ) {
//            	query.track(mkStringFromList(filters));
            	query.track(filters.toArray(new String[0]));
            	query.language(new String[]{"en"});
            	newTwitterStream.filter(query);
            } else {
            	newTwitterStream.sample();
            }
            
            setTwitterStream(newTwitterStream);
            System.out.println("Twitter receiver started");
            this.stopped = false;
			
		} catch (Exception e) {
			
		}
		
	}

//	private String mkStringFromList(List<String> filters2) {
//		// TODO Auto-generated method stub
//		return filters2.stream().map(i -> i).collect(Collectors.joining(", "));
//	}

	@Override
	public void onStop() {
		this.stopped = true;
		setTwitterStream(null);
		System.out.println("Twitter receiver stopped");
		
	}

	private void setTwitterStream(TwitterStream newTwitterStream) {
		try {
			String idNewTwitterStream = (newTwitterStream != null) ? Long.toString(newTwitterStream.getId()) : "null";
			System.out.println("TwitterReceiver:setTwitterStream " + idNewTwitterStream);
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (twitterStream != null) {
		      twitterStream.shutdown();
		    }
		    twitterStream = newTwitterStream;	
	}

}
