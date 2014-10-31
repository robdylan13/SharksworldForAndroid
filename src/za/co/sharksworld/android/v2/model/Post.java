package za.co.sharksworld.android.v2.model;



import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;

import za.co.sharksworld.android.v2.util.Constants;
import za.co.sharksworld.android.v2.util.GeneralConnectivityException;
import za.co.sharksworld.android.v2.util.RESTUtil;

public class Post extends PostHeader  {

	private String mPostContent = null;
	private String mAvatarURL = null;
	private static final String POST_CONTENT_TAG = "ct";
	private static final String AVATAR_URL_TAG = "av";

	public Post(JSONObject pPost) {
		super(pPost);
		try {
			mPostContent = pPost.getString(POST_CONTENT_TAG);
			mAvatarURL = pPost.getString(AVATAR_URL_TAG);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Post(Bundle pBundle) {
		super(pBundle);
		mPostContent = pBundle.getString(Constants.POST_CONTENT);
		mAvatarURL = pBundle.getString(Constants.AVATAR_URL);
	}

	public String toString() {
		return "Post:{" + mPostContent + "," + mAvatarURL + " "
				+ super.toString();
	}

	public String getPostContent() {
		return mPostContent;
	}

	public void setPostContent(String pPostContent) {
		this.mPostContent = pPostContent;
	}

	public String getAvatarURL() {
		return mAvatarURL;
	}

	public void setAvatarURL(String pAvatarURL) {
		this.mAvatarURL = pAvatarURL;
	}

	public static Post getPostFromServer(long pPostId) throws GeneralConnectivityException {
		Post post = null;
		String serviceURL = Constants.POST_URL + pPostId;
		JSONObject serviceResult = RESTUtil.getObjectFromREST(serviceURL);
		if (serviceResult == null) return null;
		post = new Post(serviceResult);
		return post;
	}
	
	public void saveToBundle (Bundle pBundle) {
		super.saveToBundle(pBundle);
		pBundle.putString(Constants.POST_CONTENT, mPostContent);
		pBundle.putString(Constants.AVATAR_URL, mAvatarURL);	
	}
}
