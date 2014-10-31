package za.co.sharksworld.android.v2;

import za.co.sharksworld.android.v2.model.PostHeaderList;
import za.co.sharksworld.android.v2.util.Constants;
import za.co.sharksworld.android.v2.util.GeneralConnectivityException;
import za.co.sharksworld.android.v2.util.WebUtil;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends BaseActivity {

	// the MainActivity displays a list of posts

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!WebUtil.internetAvailable(this)) {
			showNetworkUnavailableDialog();
		} else {
			setContentView(R.layout.activity_main);
			new PostHeaderListTask().execute();
			ListView postHeaderListView = ((ListView) findViewById(R.id.postHeaderListView));
			postHeaderListView
					.setOnItemClickListener(new PostHeaderClickListener());
			SwipeRefreshLayout swiperNoSwiping = ((SwipeRefreshLayout) findViewById(R.id.postHeaderListViewSwipe));
			swiperNoSwiping
					.setOnRefreshListener(new PostHeaderRefreshListener());
			boolean shownRecentCommentHint = (((SharksworldApplication) getApplication())
					.getContextValue(Constants.RECENT_COMMENT_HINT_SHOWN) != null);
			if (!shownRecentCommentHint) {

				Toast toast = Toast.makeText(this,
						getResources().getString(R.string.recent_comment_hint),
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.TOP, 0, 400);
				toast.show();
				((SharksworldApplication) getApplication()).setContextValue(
						Constants.RECENT_COMMENT_HINT_SHOWN,
						Constants.AUTHENTICATED);

			}

		}
	}

	private class PostHeaderClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			// Launch the Post Activiy passing the "id" in the intent
			Intent intent = new Intent(parent.getContext(), PostActivity.class);
			intent.putExtra(Constants.POST_ID, id);
			startActivityForResult(intent, Constants.VIEW_POST_REQUEST);

		}

	}

	private class PostHeaderRefreshListener implements OnRefreshListener {

		@Override
		public void onRefresh() {
			if (!WebUtil.internetAvailable(MainActivity.this)) {
				showNetworkUnavailableDialog();
			} else {
				//SwipeRefreshLayout swiperNoSwiping = ((SwipeRefreshLayout) findViewById(R.id.postHeaderListViewSwipe));
				//swiperNoSwiping.setRefreshing(true);
				new PostHeaderListTask().execute();
			}
		}

	}

	private class PostHeaderListTask extends AsyncTask<Void, Void, Boolean> {
		/*
		 * private ProgressDialog dialog = new
		 * ProgressDialog(MainActivity.this);
		 * 
		 * @Override protected void onPreExecute() {
		 * this.dialog.setMessage("Please wait"); this.dialog.show(); }
		 */

		@Override
		protected void onPreExecute() {

			SwipeRefreshLayout swiperNoSwiping = ((SwipeRefreshLayout) findViewById(R.id.postHeaderListViewSwipe));
			if (swiperNoSwiping != null)
				swiperNoSwiping.setRefreshing(true);
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			// Get the posts from the REST service
			PostHeaderList postHeaderList = null;
			try {
				postHeaderList = PostHeaderList.getPostsFromServer();
			} catch (GeneralConnectivityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			postHeaderListAdapter = new PostHeaderListAdapter(postHeaderList);
			return true;
		}

		protected void onPostExecute(Boolean result) {
			SwipeRefreshLayout swiperNoSwiping = ((SwipeRefreshLayout) findViewById(R.id.postHeaderListViewSwipe));
			if (swiperNoSwiping != null)
				swiperNoSwiping.setRefreshing(false);
			if (result) {
				ListView postHeaderListView = ((ListView) findViewById(R.id.postHeaderListView));
				postHeaderListView.setAdapter(postHeaderListAdapter);
				/*
				 * if (dialog.isShowing()) { dialog.dismiss(); }
				 */
			} else {
				showNetworkUnavailableDialog();
			}
		}

	}

	private ListAdapter postHeaderListAdapter = null;

}
