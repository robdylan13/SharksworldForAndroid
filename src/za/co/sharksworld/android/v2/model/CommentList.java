package za.co.sharksworld.android.v2.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import za.co.sharksworld.android.v2.SharksworldApplication;
import za.co.sharksworld.android.v2.util.Constants;
import za.co.sharksworld.android.v2.util.GeneralConnectivityException;
import za.co.sharksworld.android.v2.util.RESTUtil;
import za.co.sharksworld.android.v2.util.WebUtil;

public class CommentList {

	public CommentList() {
		mComments = new ArrayList<Comment>();
	}

	public static CommentList getCommentsFromServer(long pPostId,
			SharksworldApplication pSharksworldApplication) throws GeneralConnectivityException {

		CommentList commentList = new CommentList();

		JSONArray commentsJSON = null;

		try {
			commentsJSON = RESTUtil.getArrayFromREST(Constants.COMMENT_LIST_URL
					+ pPostId);
			List<Comment> comments = commentList.getComments();
			for (int i = 0; i < commentsJSON.length(); i++) {
				JSONObject commentJSON = commentsJSON.getJSONObject(i);
				Comment comment = new Comment(commentJSON);
				comments.add(comment);
				try {
					if (!pSharksworldApplication.imageCached(comment
							.getCommentAuthorAvatarURL()))
						pSharksworldApplication.cacheImage(comment
								.getCommentAuthorAvatarURL(), WebUtil
								.getBitmapFromUrl(comment
										.getCommentAuthorAvatarURL()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} catch (JSONException e) {
			// handle exception
			return null;
		}
		return commentList;

	}

	public List<Comment> getComments() {
		return mComments;
	}

	private List<Comment> mComments = null;
}
