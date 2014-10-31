package za.co.sharksworld.android.v2.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import za.co.sharksworld.android.v2.util.Constants;
import za.co.sharksworld.android.v2.util.GeneralConnectivityException;
import za.co.sharksworld.android.v2.util.RESTUtil;


public class PostHeaderList {

	public PostHeaderList() {
		mPostHeaders = new ArrayList<PostHeader> ();
	}
	
	public static PostHeaderList getPostsFromServer() throws GeneralConnectivityException {
		
		PostHeaderList postHeaderList = new PostHeaderList();
		//todo - populate postList from Server via REST call
		JSONArray posts = null;
		
			try {
		        posts = RESTUtil.getArrayFromREST(Constants.POST_LIST_URL);
		        List<PostHeader> postHeaders = postHeaderList.getPostHeaders() ;
		        for (int i = 0; i < posts.length(); i++) {
		            JSONObject post = posts.getJSONObject(i);
		            postHeaders.add(new PostHeader(post));
		        }
		         
		    } catch (JSONException e) {
		        // handle exception
		    	return null;
		    }
		return postHeaderList;
		
	}
	
	public List<PostHeader> getPostHeaders() {
		return mPostHeaders;
	}


	private List<PostHeader> mPostHeaders = null;
	
}

