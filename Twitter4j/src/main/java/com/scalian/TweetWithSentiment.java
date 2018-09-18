package com.scalian;

import java.io.Serializable;

public class TweetWithSentiment implements Serializable{

    private String line;
    private String sentiment;

    public TweetWithSentiment() {
    }

    public TweetWithSentiment(String line, String sentiment) {
        super();
        this.line = line;
        this.sentiment = sentiment;
    }

    public String getLine() {
        return line;
    }

    public String getSentiment() {
        return sentiment;
    }

    @Override
    public String toString() {
        return "TweetWithSentiment [texte=" + line + ", sentiment=" + sentiment + "]";
    }

}