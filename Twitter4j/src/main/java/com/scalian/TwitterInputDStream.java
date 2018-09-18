package com.scalian;

import java.util.List;

import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.StreamingContext;
import org.apache.spark.streaming.dstream.ReceiverInputDStream;
import org.apache.spark.streaming.receiver.Receiver;

import scala.reflect.ClassTag;
import twitter4j.Status;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterInputDStream extends ReceiverInputDStream<Status>{

	private static ClassTag<Status> classTagStatus = scala.reflect.ClassTag$.MODULE$.apply(Status.class);
	private ConfigurationBuilder cb;
	private List<String> filters;
	private StorageLevel storageLevel;

//	public TwitterInputDStream(StreamingContext _ssc) {
//		super(_ssc, evidence$1);
//		// TODO Auto-generated constructor stub
//	}
	
	public TwitterInputDStream(StreamingContext _ssc, ConfigurationBuilder _twitterConfBuilder, List<String> _filters, StorageLevel _storageLevel) {
		super(_ssc, classTagStatus);
		this.cb = _twitterConfBuilder;
		this.filters = _filters;
		this.storageLevel = _storageLevel;
		
	}

	@Override
	public Receiver<Status> getReceiver() {
		System.out.println("TwitterInputDStream:getReceiver() ...");
		return new TwitterReceiver(this.cb, this.filters, this.storageLevel);
	}

}
