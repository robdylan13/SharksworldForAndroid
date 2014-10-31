package za.co.sharksworld.android.v2;

import java.io.IOException;
import java.text.SimpleDateFormat;

import za.co.sharksworld.android.v2.R;
import za.co.sharksworld.android.v2.model.Post;
import za.co.sharksworld.android.v2.util.GeneralConnectivityException;
import za.co.sharksworld.android.v2.util.WebUtil;
import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

public class PostFragment extends Fragment {

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
		//System.out.println("POSTFRAG ONCREATE");
		View rootView = inflater.inflate(R.layout.fragment_post, container,
				false);
		setPostId(retrievePostIdFromActivity(getActivity()));
		if (savedInstanceState != null) {
			//System.out.println("savedInstanceState is not null");
			Post post = new Post(savedInstanceState);
			if (post.getPostId() == mPostId) // It's safe to recreate the post
												// from the bundle
				mPost = post;
		}
		new PostRetrievalTask().execute();
		return rootView;
	}

	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mPost.saveToBundle(outState);
	}
	
	@Override
	public void onStop() {
	    super.onStop();

	    if(dialog != null)
	    	dialog.dismiss();
	}
	
	@Override
	public void onPause() {
	    super.onPause();

	    if(dialog != null)
	    	dialog.dismiss();
	} 
	

	private class PostRetrievalTask extends AsyncTask<Void, Void, Boolean> {
		
		
		 @Override
		    protected void onPreExecute() {
			 dialog = new ProgressDialog(getActivity());
		        dialog.setMessage("Please wait");
		        dialog.show();
		    } 
		 
		@Override
		protected Boolean doInBackground(Void... params) {
			// Get the post from the REST service
			if (mPostId != 0 && mPost == null) {
				//System.out.println("PostRetrievalTask calling service");
				try {
					mPost = Post.getPostFromServer(mPostId);
				} catch (GeneralConnectivityException e1) {
					e1.printStackTrace();
					return false;
				}

				try {
					Bitmap avatarBitmap = WebUtil.getBitmapFromUrl(mPost
							.getAvatarURL());
					SharksworldApplication app = (SharksworldApplication) getActivity()
							.getApplication();
					app.cacheImage(mPost.getAvatarURL(), avatarBitmap);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
			return true;
		}

		protected void onPostExecute(Boolean result) {
			// Right
			if (result && mPost != null && getActivity() != null) {
				TextView postTitleView = ((TextView) getActivity()
						.findViewById(R.id.textViewPostTitle));
				postTitleView.setText(mPost.getPostTitle());
				ImageView postAuthorImageView = ((ImageView) getActivity()
						.findViewById(R.id.imageViewAuthorAvatar));
				SharksworldApplication app = (SharksworldApplication) getActivity()
						.getApplication();
				postAuthorImageView.setImageBitmap(app.getImageFromCache(mPost
						.getAvatarURL()));
				TextView postSubTitleView = ((TextView) getActivity()
						.findViewById(R.id.textViewPostSubTitle));
				postSubTitleView.setText(mPost.getPostAuthor()
						+ " "
						+ (new SimpleDateFormat(DATE_FORMAT_STRING))
								.format(mPost.getPostDate()));
				WebView postContentView = ((WebView) getActivity()
						.findViewById(R.id.webViewPostContent));
				postContentView.loadData(mPost.getPostContent(),
						"text/html; charset=utf-8", null);
				getActivity().setTitle(mPost.getPostTitle());
				if (dialog != null && dialog.isShowing()) {
		            dialog.dismiss();
		        } 
			} else {

				// We've had a network issue
				((BaseActivity) getActivity()).showNetworkUnavailableDialog();

			}
		}

	}
	private ProgressDialog dialog = null;
	private long mPostId = 0;
	private Post mPost = null;
	private static final String DATE_FORMAT_STRING = "EEE d MMM yy h:mm a";

}
