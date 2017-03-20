package com.melissayu.cp.mynytimes.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by melissa on 3/15/17.
 */

public class Article implements Parcelable{

    String webUrl;
    String headline;
    String thumbnail;
    String snippet;
    String newsDesk;

    protected Article(Parcel in) {
        webUrl = in.readString();
        headline = in.readString();
        thumbnail = in.readString();
        snippet = in.readString();
        newsDesk = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(webUrl);
        dest.writeString(headline);
        dest.writeString(thumbnail);
        dest.writeString(snippet);
        dest.writeString(newsDesk);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };

    public String getWebUrl() {
        return webUrl;
    }

    public String getHeadline() {
        return headline;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getSnippet() {
        return snippet;
    }

    public String getNewsDesk() {
        return newsDesk;
    }


    public Article(JSONObject jsonObject) {
        try {
            this.webUrl = jsonObject.getString("web_url");
            this.headline = jsonObject.getJSONObject("headline").getString("main");
            this.snippet = jsonObject.getString("snippet");
            this.newsDesk = jsonObject.getString("news_desk");
            JSONArray multimedia = jsonObject.getJSONArray("multimedia");
            if (multimedia.length() > 0) {
                JSONObject multimediaObject = multimedia.getJSONObject(0);
                this.thumbnail = "http://www.nytimes.com/"+multimediaObject.getString("url");
            } else {
                this.thumbnail = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Article> fromJSONArray(JSONArray array) {
        ArrayList<Article> results = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            try {
                results.add(new Article(array.getJSONObject(i)));
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        return results;
    }

}
