package za.co.sharksworld.android.v2.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import za.co.sharksworld.android.v2.util.Constants;

import android.os.Bundle;

public class PostHeader {

	private static final String POST_ID_TAG = "id";
	private static final String POST_TITLE_TAG = "ti";
	private static final String POST_DATE_TAG = "dt";
	private static final String POST_AUTHOR_TAG = "au";
	private static final String COMMENT_COUNT_TAG = "cm";
	private static final String RECENT_COMMENT_TAG = "rc";
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private long mPostId;
	private String mPostTitle;
	private Date mPostDate;
	private String mPostAuthor;
	private int mCommentCount;
	private boolean mHasRecentComments;

	public PostHeader(JSONObject pPost) {
		try {
			mPostId = pPost.getLong(POST_ID_TAG);
			mPostTitle = pPost.getString(POST_TITLE_TAG);

			mPostDate = new SimpleDateFormat(DATE_FORMAT).parse(pPost
					.getString(POST_DATE_TAG));

			mPostAuthor = pPost.getString(POST_AUTHOR_TAG);
			mCommentCount = pPost.getInt(COMMENT_COUNT_TAG);
			mHasRecentComments = (pPost.has(RECENT_COMMENT_TAG) );
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public PostHeader(Bundle pBundle) {
		mPostId = pBundle.getLong(Constants.POST_ID);
		mPostTitle = pBundle.getString(Constants.POST_TITLE);
		try {
			mPostDate = new SimpleDateFormat(DATE_FORMAT).parse(pBundle	.getString(Constants.POST_DATE));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mPostAuthor = pBundle.getString(Constants.POST_AUTHOR);
		mCommentCount = pBundle.getInt(Constants.COMMENT_COUNT);
		mHasRecentComments = pBundle.getBoolean(Constants.HAS_RECENT_COMMENTS, false);
	}

	
	public String toString() {
		
		return "PostHeader:{" + mPostId + "," + mPostTitle + "," + new SimpleDateFormat(DATE_FORMAT).format(mPostDate) + "," + mPostAuthor + "," + mCommentCount + "}"; 
	}

	public long getPostId() {
		return mPostId;
	}

	public void setPostId(long pPostId) {
		this.mPostId = pPostId;
	}

	public String getPostTitle() {
		return mPostTitle;
	}

	public void setPostTitle(String pPostTitle) {
		this.mPostTitle = pPostTitle;
	}

	public Date getPostDate() {
		return mPostDate;
	}

	public void setPostDate(Date pPostDate) {
		this.mPostDate = pPostDate;
	}

	public String getPostAuthor() {
		return mPostAuthor;
	}

	public void setPostAuthor(String pPostAuthor) {
		this.mPostAuthor = pPostAuthor;
	}

	public int getCommentCount() {
		return mCommentCount;
	}

	public void setCommentCount(int pCommentCount) {
		this.mCommentCount = pCommentCount;
	}
	
	public void saveToBundle (Bundle pBundle) {
		pBundle.putLong(Constants.POST_ID, mPostId);
		pBundle.putString(Constants.POST_TITLE, mPostTitle);
		String dateString = new SimpleDateFormat(DATE_FORMAT).format(mPostDate);
		pBundle.putString(Constants.POST_DATE, dateString);
		pBundle.putString(Constants.POST_AUTHOR, mPostAuthor);
		pBundle.putInt(Constants.COMMENT_COUNT, mCommentCount);
		pBundle.putBoolean(Constants.HAS_RECENT_COMMENTS, mHasRecentComments);
	}

	public boolean hasRecentComments() {
		return mHasRecentComments;
	}

	public void setHasRecentComments(boolean pHasRecentComments) {
		this.mHasRecentComments = pHasRecentComments;
	}


}
