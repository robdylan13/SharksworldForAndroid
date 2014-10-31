package za.co.sharksworld.android.v2;


import java.util.HashMap;
import java.util.Map;
import android.app.Application;
import android.graphics.Bitmap;


public class SharksworldApplication extends Application {

	

	@Override
	public void onCreate() {
		super.onCreate();
		// Register device if not already done
		//new DeviceRegistrationTask().execute();
		// Check if stored username/token exist and add them
		//authenticateFromPrefs();
	}

	/*protected void authenticateFromPrefs() {
		setContextValue(Constants.SESSION_AUTHENTICATED,
				Constants.NOT_AUTHENTICATED);
		SharedPreferences settings = getSharedPreferences(Constants.SECURITY_PREFS, 0);
		int userID = settings.getInt(Constants.USER_ID, 0);
		String username = settings.getString(Constants.USER_NAME, null);
		String authtoken = settings.getString(Constants.AUTH_TOKEN, null);
		if (authtoken != null && username != null) {

			String expiryDateString = settings
					.getString(Constants.EXPIRY, null);
			if (expiryDateString != null) {
				try {
					Date expiryDate = new SimpleDateFormat(Constants.MYSQL_DATE_FORMAT).parse(expiryDateString);
					Calendar cal = Calendar.getInstance();
					cal.setTime(new Date());
					cal.add(Calendar.DATE, 10);
					Date tenDaysFromNow =cal.getTime();
					if (expiryDate.after(tenDaysFromNow)) {
						// taking the 10-day grace period into account, our token is valid. User is logged in
						setContextValue(Constants.SESSION_AUTHENTICATED,
								Constants.AUTHENTICATED);
						setContextValue(Constants.USER_ID, ""+userID);
						setContextValue(Constants.USER_NAME, username);
						setContextValue(Constants.AUTH_TOKEN, authtoken);
						setContextValue(Constants.EXPIRY, expiryDateString);
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		} 
	} */

	public String getContextValue(String pValKey) {
		if (mContextMap == null || mContextMap.get(pValKey) == null)
			return null;
		else
			return mContextMap.get(pValKey);
	}

	public void setContextValue(String pValKey, String pValue) {
		if (mContextMap == null)
			mContextMap = new HashMap<String, String>();
		mContextMap.put(pValKey, pValue);
	}

	public void cacheImage(String pImageURL, Bitmap pImageBitmap) {
		if (mImageCache == null)
			mImageCache = new HashMap<String, Bitmap>();
		mImageCache.put(pImageURL, pImageBitmap);
	}

	public Bitmap getImageFromCache(String pImageURL) {
		if (mImageCache == null || mImageCache.get(pImageURL) == null)
			return null;
		else
			return mImageCache.get(pImageURL);
	}

	public boolean imageCached(String pImageURL) {
		return !(mImageCache == null || mImageCache.get(pImageURL) == null);
	}

	

	private Map<String, String> mContextMap = null;
	private Map<String, Bitmap> mImageCache = null;
}
