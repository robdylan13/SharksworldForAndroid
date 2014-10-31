package za.co.sharksworld.android.v2;

import za.co.sharksworld.android.v2.model.CommentList;

import za.co.sharksworld.android.v2.util.GeneralConnectivityException;
import za.co.sharksworld.android.v2.util.WebUtil;
import android.app.Activity;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class CommentsFragment extends Fragment {

	public long getPostId() {
		return mPostId;
	}

	public void setPostId(long pPostId) {
		this.mPostId = pPostId;
	}

	protected long retrievePostIdFromActivity(Activity activity) {
		try {
			PostIDProvider postIDProvider = (PostIDProvider) activity;
			return postIDProvider.getPostId();
		} catch (ClassCastException cce) {
			//System.out
			//		.println("Cannot cast activity instance to PostIDProvider");
		}
		return 0;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//System.out.println("COMMENTSFRAG ONCREATE");
		View rootView = inflater.inflate(R.layout.fragment_comments, container,
				false);
		setPostId(retrievePostIdFromActivity(getActivity()));
		//System.out.println("COMMENTSFRAGMENT POSTID IS " + mPostId );
		View v = rootView.findViewById(R.id.commentListViewSwipe);
		if (v != null) {
			SwipeRefreshLayout swiperNoSwiping = (SwipeRefreshLayout) v;
			swiperNoSwiping.setOnRefreshListener(new CommentsRefreshListener());
		}
		new CommentRetrievalTask().execute();
		Button expandCommentsButton = (Button) rootView
				.findViewById(R.id.buttonExpandComments);
		expandCommentsButton.setOnClickListener(new ExpandCommentsListener());
		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((CommentsFragmentHost) activity).setCommentsFragment(this);
	}

	private class ExpandCommentsListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			if (mCommentsListAdapter != null) {
				mCommentsListAdapter.setCondensed(false);
				new CommentRetrievalTask().execute();
			}
		}

	}

	/*
	 * @Override public void onStop() { super.onStop();
	 * 
	 * if(dialog != null) dialog.dismiss(); }
	 * 
	 * @Override public void onPause() { super.onPause();
	 * 
	 * if(dialog != null) dialog.dismiss(); }
	 */

	private class CommentsRefreshListener implements OnRefreshListener {

		@Override
		public void onRefresh() {
			if (!WebUtil.internetAvailable((BaseActivity) getActivity())) {
				((BaseActivity) getActivity()).showNetworkUnavailableDialog();
			} else {
				//System.out.println("Refreshing.....");
				// SwipeRefreshLayout swiperNoSwiping = ((SwipeRefreshLayout)
				// getActivity()
				// .findViewById(R.id.commentListViewSwipe));
				// swiperNoSwiping.setRefreshing(true);
				if (mCommentsListAdapter != null)
					mCommentsListAdapter.setDirty(true);
				new CommentRetrievalTask().execute();
			}
		}

	}

	public void refresh() {
		if (mCommentsListAdapter != null)
			mCommentsListAdapter.setDirty(true);
		new CommentRetrievalTask().execute();
	}

	private class CommentRetrievalTask extends AsyncTask<Void, Void, Boolean> {

		/*
		 * @Override protected void onPreExecute() { dialog = new
		 * ProgressDialog(getActivity()); dialog.setMessage("Please wait");
		 * dialog.show(); }
		 */

		@Override
		protected void onPreExecute() {

			if (getActivity() != null) {
				SwipeRefreshLayout swiperNoSwiping = ((SwipeRefreshLayout) getActivity()
						.findViewById(R.id.postHeaderListViewSwipe));
				if (swiperNoSwiping != null)
					swiperNoSwiping.setRefreshing(true);
			}
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			// Get the post from the REST service
			CommentList commentList = null;

			if (mCommentsListAdapter == null || mCommentsListAdapter.isDirty()) {
				//System.out.println("CommentRetrievalTask calling service");
				try {
					if (getActivity() != null)
						commentList = CommentList.getCommentsFromServer(
								mPostId, (SharksworldApplication) getActivity()
										.getApplication());
					else
						return false;
				} catch (GeneralConnectivityException e) {
					e.printStackTrace();
					return false;
				}
				if (getActivity() != null) {
					mCommentsListAdapter = new CommentsListAdapter(commentList,
							(SharksworldApplication) getActivity()
									.getApplication());
					return true;
				} else
					return false;
			}
			return true;

		}

		protected void onPostExecute(Boolean result) {
			if (getActivity() != null) {
				SwipeRefreshLayout swiperNoSwiping = ((SwipeRefreshLayout) getActivity()
						.findViewById(R.id.commentListViewSwipe));
				if (swiperNoSwiping != null)
					swiperNoSwiping.setRefreshing(false);
				if (result) {
					ListView commentsListView = ((ListView) getActivity()
							.findViewById(R.id.listViewComments));
					commentsListView.setAdapter(mCommentsListAdapter);
					Button expandCommentsButton = (Button) getActivity()
							.findViewById(R.id.buttonExpandComments);
					if (mCommentsListAdapter.isCondensed()
							&& mCommentsListAdapter.hasMoreComments()) {
						expandCommentsButton.setVisibility(View.VISIBLE);
					} else {
						expandCommentsButton.setVisibility(View.GONE);
					}
					if (mCommentsListAdapter.getCount() == 0) {
						TextView noComments = (TextView) getActivity()
								.findViewById(R.id.textViewNoComments);
						noComments.setVisibility(View.VISIBLE);
						noComments.setText(getActivity().getResources()
								.getString(R.string.no_comments));

					} else {
						TextView noComments = (TextView) getActivity()
								.findViewById(R.id.textViewNoComments);
						noComments.setVisibility(View.GONE);
					}

					mCommentsListAdapter.setDirty(false);
					/*
					 * if (dialog.isShowing()) { dialog.dismiss(); }
					 */
				} else {
					((BaseActivity) getActivity())
							.showNetworkUnavailableDialog();
				}
			}
		}

	}

	// private ProgressDialog dialog =null;

	private long mPostId = 0;
	private CommentsListAdapter mCommentsListAdapter = null;

}
