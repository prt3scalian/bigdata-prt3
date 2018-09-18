package com.scalian;

import java.util.List;

import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.StreamingContext;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.dstream.ReceiverInputDStream;

import scala.reflect.ClassTag;
import twitter4j.Status;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterUtils {
	
	private static ClassTag<Status> evidence$1;
	
	public static ReceiverInputDStream<Status> createStream(StreamingContext ssc,
															ConfigurationBuilder twitterConfBuilder,
															List<String> filters,
															StorageLevel storageLevel) {
		return new TwitterInputDStream(ssc, twitterConfBuilder, filters, storageLevel);
	}
	
	public static JavaReceiverInputDStream<Status> createJavaStream(JavaStreamingContext jssc,
			ConfigurationBuilder twitterConfBuilder,
			List<String> filters
			) {
		StorageLevel storageLevel = StorageLevel.MEMORY_AND_DISK_2();
		
		return new JavaReceiverInputDStream<Status>(createStream(jssc.ssc(), twitterConfBuilder, filters, storageLevel), evidence$1);
	}

}
