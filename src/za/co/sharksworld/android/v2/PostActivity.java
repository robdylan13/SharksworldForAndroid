package za.co.sharksworld.android.v2;

import za.co.sharksworld.android.v2.util.Constants;
import za.co.sharksworld.android.v2.util.WebUtil;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.widget.Toast;

public class PostActivity extends BaseActivity implements PostIDProvider,
		TabListener, CommentsFragmentHost {

	private static final float TABLET_MIN_WIDTH = 900;
	private ActionBar mActionBar;
	private PostPagesAdapter mAdapter;
	private CommentsFragment mCommentsFragment = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (!WebUtil.internetAvailable(this)) {
			showNetworkUnavailableDialog();
		} else {
			//System.out
			//		.println("POSTACTIVITY ON CREATE BEFORE GET INTENT: POSTID "
			//				+ mPostId);
			setContentView(R.layout.activity_post);
			Intent intent = getIntent();
			mPostId = intent.getLongExtra(Constants.POST_ID, 0);

			if (intent.getBooleanExtra(Constants.FROM_NOTIFICATION, false)) {
				// we received this intent form a notification. Kill the thing
				NotificationManager notificationManager = (NotificationManager) this
						.getSystemService(Context.NOTIFICATION_SERVICE);
				notificationManager.cancel((int) mPostId);
			}
			if (mPostId == 0 && savedInstanceState != null) {
				mPostId = savedInstanceState.getLong(Constants.POST_ID);
				//System.out
				//		.println("POSTACTIVITY ON CREATE got POST_ID from BUNDLE: POSTID "
				//				+ mPostId);
			}
			//System.out.println("POSTACTIVITY ON CREATE: POSTID " + mPostId);

			if (mPostId == 0)
				finish(); // we tried everything and we have no Post ID.... must
							// close.

			mViewPager = (ViewPager) findViewById(R.id.postActivityPager);
			boolean tabletLayout = false;
			if (mViewPager == null) { System.out.println("No View Pager  - tablet landscape");
				tabletLayout = true;
			
			}else {
				System.out.println("View Pager");
				tabletLayout = false;
			}
			
			//if (tabletLayout()) {
			if (tabletLayout) {
				System.out.println("TABLET LANDSCAPE");
				PostFragment pf = new PostFragment();
				CommentsFragment cf = new CommentsFragment();
				android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager()
						.beginTransaction();
				if (getSupportFragmentManager().findFragmentByTag(
						"PostFragment") == null)
					ft.add(R.id.PostFragmentFrame, pf, "PostFragment");
				if (getSupportFragmentManager().findFragmentByTag(
						"CommentsFragment") == null)
					ft.add(R.id.CommentFragmentFrame, cf, "CommentsFragment");
				ft.commit();

			} else {
				System.out.println("NORMAL");
				// Initilization
				mViewPager = (ViewPager) findViewById(R.id.postActivityPager);
				mActionBar = getActionBar();

				mAdapter = new PostPagesAdapter(getSupportFragmentManager());

				mViewPager.setAdapter(mAdapter);
				// mActionBar.setHomeButtonEnabled(false);
				mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
				mViewPager
						.setOnPageChangeListener(new PostActivityPageChangeListener());

				// Adding Tabs
				for (String tab_name : mTabs) {
					mActionBar.addTab(mActionBar.newTab().setText(tab_name)
							.setTabListener(this));
				}
			}

		}

	}

	private boolean tabletLayout() {
		Display display = getWindowManager().getDefaultDisplay();
		System.out.println("Rotation: " + display.getRotation());

		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

		//float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
		float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

		System.out.println("DP Width: " + dpWidth);

		return (dpWidth >= TABLET_MIN_WIDTH && (display.getRotation() == Surface.ROTATION_90 || display
				.getRotation() == Surface.ROTATION_270));

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(Constants.POST_ID, mPostId);
		System.out.println("POSTACTIVITY SAVING STATE");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.post, menu);
		MenuItem commentMenu = menu.findItem(R.id.menuComment);
		if (sessionAuthenticated()) {
			commentMenu.setVisible(true);
		} else {
			commentMenu.setVisible(false);
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.menuComment:
			showNewCommentDialog();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//System.out.println("onActivityResult ::" + requestCode + "/"
		//		+ resultCode);
		switch (resultCode) {
		case Constants.COMMENT_SUCCESS:
			//System.out.println("onActivityResult :: COMMENT SUCCESS");
			if (mCommentsFragment != null)
				mCommentsFragment.refresh();
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void showNewCommentDialog() {
		Intent intent = new Intent(this, NewCommentActivity.class);
		intent.putExtra(Constants.POST_ID, mPostId);
		startActivityForResult(intent, Constants.NEW_COMMENT_REQUEST);
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {

		mViewPager.setCurrentItem(tab.getPosition());
		if (tab.getPosition() == Constants.COMMENTS_FRAGMENT_INDEX) {
			boolean shownReplyHint = (((SharksworldApplication) getApplication())
					.getContextValue(Constants.REPLY_HINT_SHOWN) != null);
			if (!shownReplyHint) {
				if (sessionAuthenticated()) {

					Toast toast = Toast.makeText(this, getResources()
							.getString(R.string.reply_hint), Toast.LENGTH_LONG);
					toast.setGravity(Gravity.TOP, 0, 400);
					toast.show();
					((SharksworldApplication) getApplication())
							.setContextValue(Constants.REPLY_HINT_SHOWN,
									Constants.AUTHENTICATED);
				}
			}
		}
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	@Override
	public long getPostId() {
		return mPostId;
	}

	private class PostActivityPageChangeListener implements
			ViewPager.OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageSelected(int pIndex) {
			// TODO Auto-generated method stub
			mActionBar.setSelectedNavigationItem(pIndex);
		}

	}

	protected long mPostId;
	private ViewPager mViewPager;
	private String[] mTabs = { "Post", "Comments" };

	@Override
	public void setCommentsFragment(CommentsFragment pCommentsFragment) {
		mCommentsFragment = pCommentsFragment;
	}

	@Override
	public CommentsFragment getCommentsFragment() {
		return mCommentsFragment;
	}

}
