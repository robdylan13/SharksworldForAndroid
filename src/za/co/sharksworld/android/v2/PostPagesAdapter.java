package za.co.sharksworld.android.v2;

import za.co.sharksworld.android.v2.util.Constants;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

public class PostPagesAdapter  extends FragmentPagerAdapter  {

	
	public PostPagesAdapter(FragmentManager pFragmentManager) {
        super(pFragmentManager);
    }
	
	
	@Override
	public Fragment getItem(int pIndex) {
		 switch (pIndex) {
	        case Constants.POST_FRAGMENT_INDEX:
	            return new PostFragment();
	        case Constants.COMMENTS_FRAGMENT_INDEX:
	            return new CommentsFragment();

	        }
	 
	        return null;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 2;
	}

}
