package za.co.sharksworld.android.v2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import za.co.sharksworld.android.v2.R;
import za.co.sharksworld.android.v2.R.string;
import za.co.sharksworld.android.v2.util.Constants;
import za.co.sharksworld.android.v2.util.WebUtil;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

public class BaseActivity extends FragmentActivity {

	protected void showNetworkUnavailableDialog() {
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle(getResources().getString(
				string.no_network_dialog_title));
		alertDialog.setMessage(getResources().getString(
				string.no_network_dialog_text));
		alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, getResources()
				.getString(string.button_ok_label),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						setResult(Constants.KILL_APP);
						finish();

					}
				});

		alertDialog.setIcon(R.drawable.ic_launcher);
		alertDialog.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.common, menu);
		MenuItem loginMenu = menu.findItem(R.id.menuLogin);
		MenuItem logoutMenu = menu.findItem(R.id.menuLogout);
		if (sessionAuthenticated()) {
			logoutMenu.setVisible(true);
		} else {
			loginMenu.setVisible(true);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menuLogin:
			showLoginDialog();
			return true;
		case R.id.menuLogout:
			doLogout();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//System.out.println("onActivityResult ::" + requestCode + "/"
		//		+ resultCode);
		switch (resultCode) {
		case Constants.KILL_APP:
			setResult(requestCode);
			this.finish();
			break;
		case Constants.LOGIN_SUCCESS:
			invalidateOptionsMenu();
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void showLoginDialog() {
		SharksworldApplication appRef = (SharksworldApplication) getApplication();
		Intent intent = new Intent(this, LoginActivity.class);
		String tmpUName = appRef.getContextValue(Constants.USER_NAME);
		if (tmpUName == null) {
			SharedPreferences settings = getSharedPreferences(
					Constants.SECURITY_PREFS, 0);
			tmpUName = settings.getString(Constants.USER_NAME, null);
		}
		if (tmpUName != null)
			intent.putExtra(Constants.USER_NAME,
					tmpUName);
		startActivityForResult(intent, Constants.LOGIN_REQUEST);
	}

	protected void authenticateFromPrefs() {

		SharksworldApplication appRef = (SharksworldApplication) getApplication();
		appRef.setContextValue(Constants.SESSION_AUTHENTICATED,
				Constants.NOT_AUTHENTICATED);
		SharedPreferences settings = getSharedPreferences(
				Constants.SECURITY_PREFS, 0);
		int userID = settings.getInt(Constants.USER_ID, 0);
		String username = settings.getString(Constants.USER_NAME, null);
		String authtoken = settings.getString(Constants.AUTH_TOKEN, null);
		if (authtoken != null && username != null) {

			String expiryDateString = settings
					.getString(Constants.EXPIRY, null);
			if (expiryDateString != null) {
				try {
					Date expiryDate = new SimpleDateFormat(
							Constants.MYSQL_DATE_FORMAT)
							.parse(expiryDateString);
					Calendar cal = Calendar.getInstance();
					cal.setTime(new Date());
					cal.add(Calendar.DATE, 10);
					Date tenDaysFromNow = cal.getTime();
					if (expiryDate.after(tenDaysFromNow)) {
						// taking the 10-day grace period into account, our
						// token is valid. User is logged in
						appRef.setContextValue(Constants.SESSION_AUTHENTICATED,
								Constants.AUTHENTICATED);
						appRef.setContextValue(Constants.USER_ID, "" + userID);
						appRef.setContextValue(Constants.USER_NAME, username);
						appRef.setContextValue(Constants.AUTH_TOKEN, authtoken);
						appRef.setContextValue(Constants.EXPIRY,
								expiryDateString);
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}

			}
		}

	}

	public void doLogout() {
		// We need to do three things
		// 1) Kill the session. We have a method for that
		expireSession();
		// 2) Remove everything from the preferences, in case the user clicked
		// remember me
		SharedPreferences settings = getSharedPreferences(
				Constants.SECURITY_PREFS, 0);
		Editor e = settings.edit();
		e.remove(Constants.USER_ID);
		e.remove(Constants.AUTH_TOKEN);
		e.remove(Constants.EXPIRY);
		e.commit();
		// 3) redraw the menu
		invalidateOptionsMenu();
		// aaaaand we're done.
	}

	public boolean sessionAuthenticated() {
		String authenticated = ((SharksworldApplication) getApplication())
				.getContextValue(Constants.SESSION_AUTHENTICATED);
		if ((authenticated != null)
				&& authenticated.equals(Constants.AUTHENTICATED)) {
			return !authTokenExpired();
		}
		return false;
	}

	public boolean authTokenExpired() {
		String expiryDateString = ((SharksworldApplication) getApplication())
				.getContextValue(Constants.EXPIRY);
		if (expiryDateString != null) {
			try {
				Date expiryDate = new SimpleDateFormat(
						Constants.MYSQL_DATE_FORMAT).parse(expiryDateString);
				Calendar cal = Calendar.getInstance();
				cal.setTime(new Date());
				cal.add(Calendar.DATE, 10);
				Date tenDaysFromNow = cal.getTime();
				if (expiryDate.after(tenDaysFromNow)) {
					return false;
				} else {
					expireSession();
					return true;
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}

		}
		return true;
	}

	public void expireSession() {
		SharksworldApplication appRef = (SharksworldApplication) getApplication();
		appRef.setContextValue(Constants.SESSION_AUTHENTICATED,
				Constants.NOT_AUTHENTICATED);
		appRef.setContextValue(Constants.USER_ID, null);
		appRef.setContextValue(Constants.AUTH_TOKEN, null);
		appRef.setContextValue(Constants.EXPIRY, null);
	}

	protected int getAppVersion() {
		PackageInfo packageInfo;
		try {
			packageInfo = getPackageManager().getPackageInfo(getPackageName(),
					0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return 0;
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!WebUtil.internetAvailable(this)) {
			showNetworkUnavailableDialog();
		} else {
			authenticateFromPrefs();
		}

	}
}
