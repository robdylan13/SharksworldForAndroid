package za.co.sharksworld.android.v2;

import org.json.JSONException;
import org.json.JSONObject;

import za.co.sharksworld.android.v2.util.Constants;
import za.co.sharksworld.android.v2.util.GeneralConnectivityException;
import za.co.sharksworld.android.v2.util.RESTUtil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;

import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		Intent i = getIntent();
		if (i != null) {
			String uName = i.getStringExtra(Constants.USER_NAME);
			if (uName != null && !uName.isEmpty()) {
				//System.out.println("LOGINACTIVITY ON CREATE USER ID: " + uName);
				EditText usernameEditText = (EditText) findViewById(R.id.editTextUsername);
				usernameEditText.setText(uName);
				findViewById(R.id.editTextPassword).requestFocus();
			}
		}
	}

	public void onCancelButton(View view) {
		setResult(Constants.LOGIN_CANCELLED);
		finish();
	}

	public void onLoginButton(View view) {
		new LoginTask().execute();
	}

	private class LoginTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			// Right. Let's get all the values
			EditText usernameEditText = (EditText) findViewById(R.id.editTextUsername);
			EditText passwordEditText = (EditText) findViewById(R.id.editTextPassword);
			CheckBox rememberCheckBox = (CheckBox) findViewById(R.id.checkRemember);
			String unVal = usernameEditText.getText().toString();
			String pwVal = passwordEditText.getText().toString();
			boolean remember = rememberCheckBox.isChecked();
			if (unVal != null && unVal.length() > 0 && pwVal != null
					&& pwVal.length() > 0) {
				// We can try to authenticate
				JSONObject input = new JSONObject();
				try {
					input.put(Constants.USER_NAME, unVal);
					input.put(Constants.PASSWORD, pwVal);
					SharedPreferences settings = getSharedPreferences(
							Constants.SECURITY_PREFS, 0);
					String appKey = settings.getString(
							Constants.APPLICATION_KEY, null);
					String appToken = settings.getString(
							Constants.APPLICATION_TOKEN, null);
					input.put(Constants.CLIENT_NAME, appKey);
					input.put(Constants.CLIENT_ID, appToken);
					JSONObject output = RESTUtil.postObjectToREST(
							Constants.LOGIN_URL, input);
					if (output != null
							&& output.getInt(Constants.ERROR_KEY) == 0) {
						// We've got a successful login
						SharksworldApplication app = (SharksworldApplication) getApplication();
						app.setContextValue(Constants.SESSION_AUTHENTICATED,
								Constants.AUTHENTICATED);
						app.setContextValue(Constants.USER_ID,
								"" + output.getInt(Constants.USER_ID));
						app.setContextValue(Constants.USER_NAME,
								output.getString(Constants.USER_NAME));
						app.setContextValue(Constants.AUTH_TOKEN,
								output.getString(Constants.AUTH_TOKEN));
						app.setContextValue(Constants.EXPIRY,
								output.getString(Constants.EXPIRY));
						// at this point, our session is authenticated.
						// Now to update settings, depending on whether remember
						// me was checked
						Editor e = settings.edit();
						//System.out.println(remember);
						e.putInt(Constants.USER_ID,
								remember ? output.getInt(Constants.USER_ID) : 0);
						e.putString(
								Constants.USER_NAME,
								remember ? output
										.getString(Constants.USER_NAME) : null);
						e.putString(
								Constants.AUTH_TOKEN,
								remember ? output
										.getString(Constants.AUTH_TOKEN) : null);
						e.putString(Constants.EXPIRY,
								remember ? output.getString(Constants.EXPIRY)
										: null);
						e.commit();
						return true;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				} catch (GeneralConnectivityException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					return false;
				}
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				setResult(Constants.LOGIN_SUCCESS);
				finish();
			} else {
				Toast toast = Toast.makeText(LoginActivity.this, getResources()
						.getString(R.string.login_failed), Toast.LENGTH_LONG);
				toast.setGravity(Gravity.TOP, 0, 400);
				toast.show();
			}
		}
	}

}
