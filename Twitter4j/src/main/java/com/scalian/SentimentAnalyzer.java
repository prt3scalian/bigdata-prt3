package com.scalian;

import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

public class SentimentAnalyzer {

  private String toSentiment(int sentiment) {
    switch (sentiment) {
      case 0:
      case 1:
        return "NEGATIF";
      case 2:
        return "NEUTRE";
      case 3:
      case 4:
        return "POSITIF";
      default:
        return "NONE";
    }
    
  }

  public TweetWithSentiment findSentiment(String line) {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    int mainSentiment = 0;
    if (line != null && line.length() > 0) {
      int longest = 0;
      Annotation annotation = pipeline.process(line);
      for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
        Tree tree = sentence.get(SentimentCoreAnnotations.AnnotatedTree.class);
        int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
        String partText = sentence.toString();
        if (partText.length() > longest) {
          mainSentiment = sentiment;
          longest = partText.length();
        }

      }
    }
    
    TweetWithSentiment tweetWithSentiment = new TweetWithSentiment(line, toSentiment(mainSentiment));
    return tweetWithSentiment;

  }
}
