package za.co.sharksworld.android.v2.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;



import org.json.JSONException;
import org.json.JSONObject;



public class Comment {

	private static final String COMMENT_ID_TAG = "id";
	private static final String COMMENT_NUMBER_TAG = "no";
	private static final String COMMENT_DATE_TAG = "dt";
	private static final String COMMENT_AUTHOR_TAG = "au";
	private static final String COMMENT_CONTENT_TAG = "ct";
	private static final String COMMENT_AUTHOR_AVATAR_URL_TAG = "av";
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final String REPLY_LINK_START = "@<a href=";
	private static final String REPLY_LINK_END = ") :";
	private static final String ALTERNATE_REPLY_LINK_END = "):";

	private long mCommentId;
	private int mCommentNumber;
	private Date mCommentDate;
	private String mCommentAuthor;
	private String mCommentContent;
	private String mCommentAuthorAvatarURL;
	


	public Comment(JSONObject pComment) {
		try {
			mCommentId = pComment.getLong(COMMENT_ID_TAG);
			mCommentNumber = pComment.getInt(COMMENT_NUMBER_TAG);
			mCommentDate = new SimpleDateFormat(DATE_FORMAT).parse(pComment
					.getString(COMMENT_DATE_TAG));
			mCommentAuthor = pComment.getString(COMMENT_AUTHOR_TAG);
			// we may want a utility function call here to strip content of anything we don't want.
			mCommentContent = reformatReplyLinks(pComment.getString(COMMENT_CONTENT_TAG));
			//mCommentContent = replaceEmoticonsWithImages(mCommentContent);
			mCommentAuthorAvatarURL = pComment
					.getString(COMMENT_AUTHOR_AVATAR_URL_TAG);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	private static String reformatReplyLinks(String pCommentContent) {
		/** This method removes the HTML HREF tags that we get in reply comments and replaces them
		 *  with something we can include in a textview in a readable way. 
		 */
		
		if (pCommentContent.contains(REPLY_LINK_START)) {
			// We are going to deal with the first occurrence and the recursively call the method
			//System.out.println("pCommentContent : " + pCommentContent);
			int startPos = pCommentContent.indexOf(REPLY_LINK_START);
			//System.out.println("startPos : " + startPos);
			int endPos =  pCommentContent.indexOf(REPLY_LINK_END, startPos) + REPLY_LINK_END.length();
			// Hack in case someone got rid of the space
			if (endPos == 2) {
				endPos =  pCommentContent.indexOf(ALTERNATE_REPLY_LINK_END, startPos) + ALTERNATE_REPLY_LINK_END.length();
			}
			//System.out.println("endPos : " + endPos);
			String replyLink = pCommentContent.substring(startPos, endPos);
			//System.out.println("REPLYLINK : " + replyLink);
			int startPos2 = replyLink.indexOf(">");
			int endPos2 = replyLink.indexOf("</a");
			replyLink = "@" + replyLink.substring(startPos2+1, endPos2) + " "+replyLink.substring(endPos2+5, replyLink.length());
			//System.out.println("REPLYLINK : " + replyLink);
			return reformatReplyLinks(pCommentContent.substring(0,startPos) + replyLink + pCommentContent.substring(endPos)) ;
			
			//return replyLink + pCommentContent.substring(endPos);
		} else {
			// there's no need to do anything else
			return pCommentContent;
		}
		
	}

	public long getCommentId() {
		return mCommentId;
	}

	public void setCommentId(long pCommentId) {
		this.mCommentId = pCommentId;
	}

	public Date getCommentDate() {
		return mCommentDate;
	}

	public void setCommentDate(Date pCommentDate) {
		this.mCommentDate = pCommentDate;
	}

	public String getCommentAuthor() {
		return mCommentAuthor;
	}

	public void setCommentAuthor(String pCommentAuthor) {
		this.mCommentAuthor = pCommentAuthor;
	}

	public int getCommentNumber() {
		return mCommentNumber;
	}

	public void setCommentNumber(int pCommentNumber) {
		this.mCommentNumber = pCommentNumber;
	}

	public String getCommentContent() {
		return mCommentContent;
	}

	public void setCommentContent(String pCommentContent) {
		this.mCommentContent = pCommentContent;
	}

	public String getCommentAuthorAvatarURL() {
		return mCommentAuthorAvatarURL;
	}

	public void setCommentAuthorAvatarURL(String pCommentAuthorAvatarURL) {
		this.mCommentAuthorAvatarURL = pCommentAuthorAvatarURL;
	}

}
