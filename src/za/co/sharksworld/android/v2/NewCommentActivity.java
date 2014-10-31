package za.co.sharksworld.android.v2;

import org.json.JSONException;
import org.json.JSONObject;

import za.co.sharksworld.android.v2.util.Constants;
import za.co.sharksworld.android.v2.util.GeneralConnectivityException;
import za.co.sharksworld.android.v2.util.RESTUtil;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NewCommentActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_comment);
		Intent intent = getIntent();
		mPostId = intent.getLongExtra(Constants.POST_ID, 0);
		isReply = intent.getBooleanExtra(Constants.IS_REPLY, false);
		if (isReply) {
			mReplyCommentAuthor = intent
					.getStringExtra(Constants.REPLY_COMMENT_AUTHOR);
			mReplyCommentId = intent
					.getLongExtra(Constants.REPLY_COMMENT_ID, 0);
			mReplyCommentNo = intent.getIntExtra(
					Constants.REPLY_COMMENT_NUMBER, 0);
		}
		TextView replyToTextView = (TextView) findViewById(R.id.newCommentReplyToTextView);
		if (isReply) {
			String replyText = getResources()
					.getString(R.string.reply_to_start)
					+ " " +mReplyCommentAuthor + " " 
					+ getResources().getString(R.string.reply_to_mid)+ " "
					+ mReplyCommentNo
					+ getResources().getString(R.string.reply_to_end);
			replyToTextView.setText(replyText);
			replyToTextView.setVisibility(View.VISIBLE);
		} else {
			replyToTextView.setVisibility(View.GONE);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_comment, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onCancelButton(View view) {
		setResult(Constants.COMMENT_CANCELLED);
		finish();
	}

	public void onPostCommentButton(View view) {
		Button pcb = (Button)findViewById(R.id.buttonDoComment);
		Button ccb = (Button)findViewById(R.id.buttonCancelComment);
		pcb.setEnabled(false);
		ccb.setEnabled(false);
		new PostCommentTask().execute();
	}

	private class PostCommentTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			// Right. Let's get all the values
			EditText commentContentEditText = (EditText) findViewById(R.id.editTextCommentContent);
			String commentVal = commentContentEditText.getText().toString();
			
			if (commentVal != null && commentVal.length() > 0
					&& sessionAuthenticated()) {
				// We can try to post the comment
				if (isReply) {
					//we need to massage this comment text a little
					String replyLink = Constants.REPLY_LINK_START + mReplyCommentId +  Constants.REPLY_LINK_MID1 + mReplyCommentAuthor +  Constants.REPLY_LINK_MID2 + mReplyCommentNo +  Constants.REPLY_LINK_END;
					commentVal = replyLink + commentVal;
					
				}
				JSONObject input = new JSONObject();
				try {
					SharksworldApplication app = (SharksworldApplication) getApplication();
					input.put(Constants.USER_NAME,
							app.getContextValue(Constants.USER_NAME));
					input.put(Constants.TOKEN,
							app.getContextValue(Constants.AUTH_TOKEN));
					SharedPreferences settings = getSharedPreferences(
							Constants.SECURITY_PREFS, 0);
					String appKey = settings.getString(
							Constants.APPLICATION_KEY, null);
					String appToken = settings.getString(
							Constants.APPLICATION_TOKEN, null);
					input.put(Constants.CLIENT_NAME, appKey);
					input.put(Constants.CLIENT_ID, appToken);
					input.put(Constants.COMMENT, commentVal);
					input.put(Constants.COMMENT_POST_ID, mPostId);
					JSONObject output = RESTUtil.postObjectToREST(
							Constants.POST_COMMENT_URL, input);
					if (output != null
							&& output.getInt(Constants.ERROR_KEY) == 0) {
						// We've got a successful comment post

						return true;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (GeneralConnectivityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				setResult(Constants.COMMENT_SUCCESS);
				finish();
			} else {
				Toast toast = Toast.makeText(NewCommentActivity.this,
						getResources().getString(R.string.comment_failed),
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.TOP, 0, 400);
				toast.show();
				Button pcb = (Button)findViewById(R.id.buttonDoComment);
				Button ccb = (Button)findViewById(R.id.buttonCancelComment);
				pcb.setEnabled(true);
				ccb.setEnabled(true);
			}
		}
	}

	private long mPostId = 0;
	private boolean isReply;
	private long mReplyCommentId;
	private int mReplyCommentNo;
	private String mReplyCommentAuthor;

}
