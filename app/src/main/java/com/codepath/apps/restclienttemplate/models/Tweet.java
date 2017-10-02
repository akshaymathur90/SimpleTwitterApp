
package com.codepath.apps.restclienttemplate.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;
import android.util.Log;

import com.codepath.apps.restclienttemplate.database.MyDatabase;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

@Table(database = MyDatabase.class)
public class Tweet extends BaseModel implements Parcelable {

    public static final String TAG = "Tweet";
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @PrimaryKey
    @Column
    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("id_str")
    @Expose
    private String idStr;
    @Column
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("truncated")
    @Expose
    private Boolean truncated;
    @Column
    @ForeignKey(saveForeignKeyModel = false)
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("is_quote_status")
    @Expose
    private Boolean isQuoteStatus;
    @SerializedName("retweet_count")
    @Expose
    private Integer retweetCount;
    @SerializedName("favorite_count")
    @Expose
    private Integer favoriteCount;
    @SerializedName("favorited")
    @Expose
    private Boolean favorited;
    @SerializedName("retweeted")
    @Expose
    private Boolean retweeted;
    @SerializedName("possibly_sensitive")
    @Expose
    private Boolean possiblySensitive;
    @SerializedName("possibly_sensitive_appealable")
    @Expose
    private Boolean possiblySensitiveAppealable;
    @SerializedName("lang")
    @Expose
    private String lang;

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdStr() {
        return idStr;
    }

    public void setIdStr(String idStr) {
        this.idStr = idStr;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getTruncated() {
        return truncated;
    }

    public void setTruncated(Boolean truncated) {
        this.truncated = truncated;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getIsQuoteStatus() {
        return isQuoteStatus;
    }

    public void setIsQuoteStatus(Boolean isQuoteStatus) {
        this.isQuoteStatus = isQuoteStatus;
    }

    public Integer getRetweetCount() {
        return retweetCount;
    }

    public void setRetweetCount(Integer retweetCount) {
        this.retweetCount = retweetCount;
    }

    public Integer getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(Integer favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public Boolean getFavorited() {
        return favorited;
    }

    public void setFavorited(Boolean favorited) {
        this.favorited = favorited;
    }

    public Boolean getRetweeted() {
        return retweeted;
    }

    public void setRetweeted(Boolean retweeted) {
        this.retweeted = retweeted;
    }

    public Boolean getPossiblySensitive() {
        return possiblySensitive;
    }

    public void setPossiblySensitive(Boolean possiblySensitive) {
        this.possiblySensitive = possiblySensitive;
    }

    public Boolean getPossiblySensitiveAppealable() {
        return possiblySensitiveAppealable;
    }

    public void setPossiblySensitiveAppealable(Boolean possiblySensitiveAppealable) {
        this.possiblySensitiveAppealable = possiblySensitiveAppealable;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getRelativeTimeAgo() {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        String timeString = null;
        String relDate[]=new String[3];
        try {
            long dateMillis = sf.parse(createdAt).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.FORMAT_ABBREV_ALL).toString();
            relDate = relativeDate.split(" ");
            timeString = relDate[0]+relDate[1].charAt(0);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d(TAG,"relative time--> "+relativeDate);
        Log.d(TAG,"modified string time--> "+timeString);
        return timeString;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.createdAt);
        dest.writeValue(this.id);
        dest.writeString(this.idStr);
        dest.writeString(this.text);
        dest.writeValue(this.truncated);
        dest.writeParcelable(this.user, flags);
        dest.writeValue(this.isQuoteStatus);
        dest.writeValue(this.retweetCount);
        dest.writeValue(this.favoriteCount);
        dest.writeValue(this.favorited);
        dest.writeValue(this.retweeted);
        dest.writeValue(this.possiblySensitive);
        dest.writeValue(this.possiblySensitiveAppealable);
        dest.writeString(this.lang);
    }

    public Tweet() {
    }

    protected Tweet(Parcel in) {
        this.createdAt = in.readString();
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.idStr = in.readString();
        this.text = in.readString();
        this.truncated = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.user = in.readParcelable(User.class.getClassLoader());
        this.isQuoteStatus = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.retweetCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.favoriteCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.favorited = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.retweeted = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.possiblySensitive = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.possiblySensitiveAppealable = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.lang = in.readString();
    }

    public static final Parcelable.Creator<Tweet> CREATOR = new Parcelable.Creator<Tweet>() {
        @Override
        public Tweet createFromParcel(Parcel source) {
            return new Tweet(source);
        }

        @Override
        public Tweet[] newArray(int size) {
            return new Tweet[size];
        }
    };
}
