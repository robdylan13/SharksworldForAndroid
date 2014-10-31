package za.co.sharksworld.android.v2;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import za.co.sharksworld.android.v2.R.string;
import za.co.sharksworld.android.v2.util.Constants;
import za.co.sharksworld.android.v2.util.GeneralConnectivityException;
import za.co.sharksworld.android.v2.util.RESTUtil;
import za.co.sharksworld.android.v2.util.WebUtil;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class SplashScreenActivity extends BaseActivity {

	// Splash screen timer
	private static int SPLASH_TIME_OUT = 2000;
	private static int GCM_REGISTATION_SUCCESS = 0;
	private static int PLAY_SERVICES_UNAVAILABLE = 1;
	private static int CANNOT_GET_REGID = 2;
	private static final int CANNOT_SEND_REGID = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		// Let's spit out what we have:
		SharedPreferences settings = getSharedPreferences(
				Constants.SECURITY_PREFS, 0);
		Map<String, ?> settingsMap = settings.getAll();
		for (String key : settingsMap.keySet()) {
			//System.out.println("Setting::" + key + " " + settingsMap.get(key));
		}
		settings = getSharedPreferences(Constants.GENERAL_PREFS, 0);
		settingsMap = settings.getAll();
		for (String key : settingsMap.keySet()) {
			//System.out.println("Setting::" + key + " " + settingsMap.get(key));
		}
		if (!WebUtil.internetAvailable(this)) {
			showNetworkUnavailableDialog();

		} else {
			new DeviceRegistrationTask().execute();
			// new GCMRegistrationTask().execute();
		}
	}

	private class GCMRegistrationTask extends
			AsyncTask<Void, Void, RegistrationTaskResult> {

		@Override
		protected RegistrationTaskResult doInBackground(Void... params) {
			//System.out.println("3 - GCM REGISTRATION TASK");
			// Let's see if we need to do this at all
			boolean validGCMRegistration = isGCMRegistrationValid();
			if (validGCMRegistration) {
				// We're good - continue
				return new RegistrationTaskResult(GCM_REGISTATION_SUCCESS, 0);
			} else {
				// We need to (re)register
				// first, check if Google Play Services are available

				int gcmAvailable = GooglePlayServicesUtil
						.isGooglePlayServicesAvailable(SplashScreenActivity.this);
				if (gcmAvailable == ConnectionResult.SUCCESS) {
					// We can carry on here
					try {
						String regID = GoogleCloudMessaging.getInstance(
								SplashScreenActivity.this).register(
								Constants.GCM_SENDER_ID);
						System.out.println("Received GCM Registration ID "
								+ regID);
						if (sendGCMRegistationToServer(regID)) {
							// persist REGID TO PREFS
							saveGCMRegistration(regID);
							return new RegistrationTaskResult(
									GCM_REGISTATION_SUCCESS, 0);
						} else {
							return new RegistrationTaskResult(
									CANNOT_SEND_REGID, 0);

						}
					} catch (Exception e) {
						return new RegistrationTaskResult(CANNOT_GET_REGID, 0);
					}

				} else {
					return new RegistrationTaskResult(
							PLAY_SERVICES_UNAVAILABLE, gcmAvailable);

				}
			}

		}

		private boolean sendGCMRegistationToServer(String pRegID) {
			SharedPreferences settings = getSharedPreferences(
					Constants.SECURITY_PREFS, 0);
			String appKey = settings.getString(Constants.APPLICATION_KEY, null);
			String appToken = settings.getString(Constants.APPLICATION_TOKEN,
					null);
			String username = null;
			if (sessionAuthenticated()) {
				username = ((SharksworldApplication) getApplication())
						.getContextValue(Constants.USER_NAME);
			}
			System.out.println("Going to save GCM RegID to backend " + pRegID
					+ username + appKey);
			// CALL REST AND GET RESULT
			JSONObject input = new JSONObject();
			try {
				input.put(Constants.CLIENT_NAME, appKey);
				input.put(Constants.CLIENT_ID, appToken);
				if (username != null)
					input.put(Constants.USER_NAME, username);
				input.put(Constants.REGISTRATION_ID, pRegID);
				input.put(Constants.POST_NOTIFICATION, 1);
				input.put(Constants.COMMENT_NOTIFICATION, 0);
				JSONObject output = null;
				try {
					output = RESTUtil.postObjectToREST(
							Constants.GCM_REGISTRATION_URL, input);
				} catch (GeneralConnectivityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					showNetworkUnavailableDialog();
				}
				System.out.println(output);
				if (output != null && output.getInt(Constants.ERROR_KEY) == 0)
					return true;
				else
					return false;
			} catch (JSONException e) {
				e.printStackTrace();
				return false;
			}
		}

		private boolean isGCMRegistrationValid() {
			// TODO Auto-generated method stub
			SharedPreferences prefs = getSharedPreferences(
					Constants.GENERAL_PREFS, Context.MODE_PRIVATE);
			int savedAppVersion = prefs.getInt(Constants.PROPERTY_APP_VERSION,
					Integer.MIN_VALUE);
			if (savedAppVersion != getAppVersion())
				return false;
			else {
				String registrationId = prefs.getString(
						Constants.PROPERTY_REG_ID, "");
				return (!registrationId.isEmpty());
			}
		}

		private void saveGCMRegistration(String pRegistrationId) {
			SharedPreferences prefs = getSharedPreferences(
					Constants.GENERAL_PREFS, Context.MODE_PRIVATE);
			Editor e = prefs.edit();
			e.putInt(Constants.PROPERTY_APP_VERSION, getAppVersion());
			e.putString(Constants.PROPERTY_REG_ID, pRegistrationId);
			e.commit();

		}

		@Override
		protected void onPostExecute(RegistrationTaskResult result) {

			if (result.MajorCode == PLAY_SERVICES_UNAVAILABLE) {
				if (GooglePlayServicesUtil
						.isUserRecoverableError(result.MinorCode)) {
					Dialog gcmErrorDialog = GooglePlayServicesUtil
							.getErrorDialog(result.MinorCode,
									SplashScreenActivity.this,
									Constants.GCM_ERROR_DIALOG_REQUEST,
									new GCMErrorDialogCancelListener());
					gcmErrorDialog.show();
				} else {
					showNoNotifiationDialog(
							getResources().getString(
									string.no_play_dialog_title),
							getResources()
									.getString(string.no_play_dialog_text));

				}
			} else if (result.MajorCode == GCM_REGISTATION_SUCCESS) {
				continueToMainActivity(true);

			}
		}

	}

	protected void showNoNotifiationDialog(String title, String message) {
		AlertDialog alertDialog = new AlertDialog.Builder(
				SplashScreenActivity.this).create();
		alertDialog.setTitle(title);
		alertDialog.setMessage(message);
		alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, getResources()
				.getString(string.button_ok_label),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						continueToMainActivity(false);

					}
				});
		alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources()
				.getString(string.button_cancel_label),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
		alertDialog.setIcon(R.drawable.ic_launcher);
		alertDialog.show();

	}

	protected void continueToMainActivity(boolean wait) {
		//System.out.println("4 - CONTINUE TO MAIN");
		if (wait) {
			new Handler().postDelayed(new Runnable() {
				public void run() {
					Intent i = new Intent(SplashScreenActivity.this,
							MainActivity.class);
					startActivity(i);
					finish();
				}
			}, SPLASH_TIME_OUT);
		} else {
			Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
			startActivity(i);
			finish();
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
		case Constants.GCM_ERROR_DIALOG_REQUEST:
			switch (resultCode) {
			case 0:
				showNoNotifiationDialog(
						getResources().getString(string.no_play_dialog_title),
						getResources().getString(string.no_play_dialog_text));
				break;
			default:
				continueToMainActivity(false);
				break;
			}
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public class RegistrationTaskResult {

		public RegistrationTaskResult(int pMaj, int pMin) {
			MajorCode = pMaj;
			MinorCode = pMin;
		}

		public int MajorCode = 0;
		public int MinorCode = 0;
	}

	private class GCMErrorDialogCancelListener implements
			DialogInterface.OnCancelListener {

		@Override
		public void onCancel(DialogInterface dialog) {
			// Here we need to abort GCM registration and let the user know that
			// notifications won't work

		}

	}

	private class DeviceRegistrationTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			System.out.println("1 - DEVICE REGISTRATION TASK");
			SharedPreferences settings = getSharedPreferences(
					Constants.SECURITY_PREFS, 0);
			String appKey = settings.getString(Constants.APPLICATION_KEY, null);
			String appToken = settings.getString(Constants.APPLICATION_TOKEN,
					null);
			System.out.println("DeviceRegistrationTask:: doInBackground Key: "
					+ appKey + " Token: " + appToken);
			// I want to try authenticate using these values.
			boolean deviceAuthenticated = false;
			if (appKey != null && appToken != null) {
				JSONObject input = new JSONObject();
				try {
					input.put(Constants.CLIENT_NAME, appKey);
					input.put(Constants.CLIENT_ID, appToken);
					JSONObject output;
					try {
						output = RESTUtil.postObjectToREST(
								Constants.DEVICE_AUTHN_URL, input);
					} catch (GeneralConnectivityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return false;
					}
					if (output != null
							&& output.getInt(Constants.ERROR_KEY) == 0) {
						System.out.println("Authenticated device successfully");
						deviceAuthenticated = true;
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
			}
			if (appKey == null || appToken == null || !deviceAuthenticated) {
				// We need to call the Register Device service to obtain these
				try {
					PackageInfo pInfo = getPackageManager().getPackageInfo(
							getPackageName(), 0);
					// String appName = pInfo.packageName + ":"
					// + pInfo.versionName;
					String appName = pInfo.packageName;
					JSONObject input = new JSONObject();
					input.put(Constants.APPLICATION_NAME, appName);
					JSONObject output = null;
					try {
						output = RESTUtil.postObjectToREST(
								Constants.REGISTER_DEVICE_URL, input);
					} catch (GeneralConnectivityException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						return false;
					}
					if (output != null
							&& output.getInt(Constants.ERROR_KEY) == 0) {
						appKey = output.getString(Constants.APPLICATION_KEY);
						appToken = output
								.getString(Constants.APPLICATION_TOKEN);
						Editor e = settings.edit();
						e.putString(Constants.APPLICATION_KEY, appKey);
						e.putString(Constants.APPLICATION_TOKEN, appToken);
						e.remove(Constants.USER_ID);
						e.remove(Constants.AUTH_TOKEN);
						e.remove(Constants.EXPIRY);
						e.commit();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
			//System.out.println("2 - AUTHENTICATE FROM PREFS");
			authenticateFromPrefs();
			new GCMRegistrationTask().execute();
			} else {
				showNetworkUnavailableDialog();
			}
		}
	}
}
