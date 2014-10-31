package za.co.sharksworld.android.v2.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


import za.co.sharksworld.android.v2.R;


public class Constants {
	public static final String POST_LIST_URL = "http://www.sharksworld.co.za/wp-json-get-posts.php";
	public static final String POST_URL = "http://www.sharksworld.co.za/wp-json-get-post.php?id=";
	public static final String COMMENT_LIST_URL = "http://www.sharksworld.co.za/wp-json-get-comments.php?id=";
	public static final String REGISTER_DEVICE_URL = "http://www.sharksworld.co.za/wp-json-register-device.php";
	public static final String LOGIN_URL = "http://www.sharksworld.co.za/wp-json-login.php";
	public static final String POST_COMMENT_URL = "http://www.sharksworld.co.za/wp-json-comment-post.php";
	public static final String GCM_REGISTRATION_URL = "http://www.sharksworld.co.za/wp-json-register-push-client.php";
	public static final String DEVICE_AUTHN_URL = "http://www.sharksworld.co.za/wp-json-authenticate-device.php";
	public static final String GCM_SENDER_ID = "201345304671";
	public static final String POST_ID = "POST_ID";
	public static final String IS_REPLY = "IS_REPLY";
	public static final String REPLY_COMMENT_ID = "REPLY_COMMENT_ID";
	public static final String REPLY_COMMENT_NUMBER = "REPLY_COMMENT_NUMBER";
	public static final String REPLY_COMMENT_AUTHOR = "REPLY_COMMENT_AUTHOR";
	public static final String POST_FRAGMENT = "POST_FRAGMENT";
	public static final String COMMENTS_FRAGMENT = "COMMENT_FRAGMENT";
	public static final String POST_TITLE = "POST_TITLE";
	public static final String POST_DATE = "POST_DATE";
	public static final String POST_AUTHOR = "POST_AUTHOR";
	public static final String COMMENT_COUNT = "COMMENT_COUNT";
	public static final String HAS_RECENT_COMMENTS = "HAS_RECENT_COMMENTS";
	public static final String POST_CONTENT = "POST_CONTENT";
	public static final String AVATAR_URL = "AVATAR_URL";
	public static final String SECURITY_PREFS = "SECURITY";
	public static final String GENERAL_PREFS = "GENERAL";
	public static final int POST_FRAGMENT_INDEX = 0;
	public static final int COMMENTS_FRAGMENT_INDEX = 1;
	public static final String IMG_START = "<img src=\"";
	public static final String IMG_END = "\" />";
	public static final String APPLICATION_KEY = "applicationKey";
	public static final String APPLICATION_TOKEN = "applicationToken";
	public static final Map<String, Integer> EMOTICONS;
	public static final String APPLICATION_NAME = "appname";
	public static final String USER_ID = "userID";
	public static final String USER_NAME = "username";
	public static final String PASSWORD = "password";
	public static final String ERROR_KEY = "error";
	public static final String AUTH_TOKEN = "auth-token";
	public static final String TOKEN = "token";
	public static final String EXPIRY = "expiry";
	public static final String POST_NOTIFICATION = "post_notification";
	public static final String COMMENT_NOTIFICATION = "comment_notification";
	public static final String CLIENT_NAME = "clientname";
	public static final String CLIENT_ID = "clientid";
	public static final String COMMENT = "comment";
	public static final String COMMENT_POST_ID = "postid";
	public static final String REGISTRATION_ID = "regid";
	public static final String SESSION_AUTHENTICATED = "SESSION_AUTHENTICATED";
	public static final String NOT_AUTHENTICATED = "NO";
	public static final String AUTHENTICATED = "YES";
	public static final String MYSQL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String REPLY_LINK_START = "@<a href=\"#comment-";
	public static final String REPLY_LINK_MID1 = "\">";
	public static final String REPLY_LINK_MID2 = "</a> (Comment ";	
	public static final String REPLY_LINK_END = ") : ";
	public static final int LOGIN_REQUEST = 100;
	public static final int VIEW_POST_REQUEST = 110;
	public static final int LOGIN_CANCELLED = 98;
	public static final int COMMENT_CANCELLED = 97;
	public static final int LOGIN_SUCCESS = 50;
	public static final int COMMENT_SUCCESS = 51;
	public static final int KILL_APP = 99;
	public static final int NEW_COMMENT_REQUEST = 120;
	public static final int NEW_COMMENT_REPLY_REQUEST = 125;
	public static final int GCM_ERROR_DIALOG_REQUEST = 190;
	public static final String REPLY_HINT_SHOWN = "REPLY_HINT_SHOWN";
	public static final String RECENT_COMMENT_HINT_SHOWN = "RECENT_COMMENT_HINT_SHOWN";
	public static final String PROPERTY_APP_VERSION = "APP_VERSION";
	public static final String PROPERTY_REG_ID = "GCM_REG_ID";
	public static final String FROM_NOTIFICATION = "FROM_NOTIFICATION";
	

	
	
	
	

	
	
	
	static {
		Map<String, Integer> aMap = new HashMap<String, Integer>();
		aMap.put(":)", R.drawable.icon_smile);
		aMap.put(":-)", R.drawable.icon_smile);
		aMap.put(":smile:", R.drawable.icon_smile);	
		aMap.put(";-)", R.drawable.icon_wink);
		aMap.put(";)", R.drawable.icon_wink);
		aMap.put(":wink:", R.drawable.icon_wink);
		aMap.put(":|", R.drawable.icon_neutral);
		aMap.put(":x", R.drawable.icon_mad);
		aMap.put(":twisted:", R.drawable.icon_twisted);
		aMap.put(":shock:", R.drawable.icon_eek);
		aMap.put(":sad:", R.drawable.icon_sad);
		aMap.put(":roll:", R.drawable.icon_rolleyes);
		aMap.put(":razz:", R.drawable.icon_razz);
		aMap.put(":oops:", R.drawable.icon_redface);
		aMap.put(":o", R.drawable.icon_surprised);
		aMap.put(":mrgreen:", R.drawable.icon_mrgreen);
		aMap.put(":lol:", R.drawable.icon_lol);
		aMap.put(":idea:", R.drawable.icon_idea);
		aMap.put(":grin:", R.drawable.icon_biggrin);
		aMap.put(":evil:", R.drawable.icon_evil);
		aMap.put(":cry:", R.drawable.icon_cry);
		aMap.put(":cool:", R.drawable.icon_cool);
		aMap.put(":arrow: ", R.drawable.icon_arrow);
		aMap.put(":???:", R.drawable.icon_confused);
		aMap.put(":?:", R.drawable.icon_question);
		aMap.put(":!:", R.drawable.icon_exclaim);
		EMOTICONS = Collections.unmodifiableMap(aMap);
	}

}
