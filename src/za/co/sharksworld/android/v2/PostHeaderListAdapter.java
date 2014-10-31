package za.co.sharksworld.android.v2;

import java.text.SimpleDateFormat;

import za.co.sharksworld.android.v2.R;
import za.co.sharksworld.android.v2.R.string;
import za.co.sharksworld.android.v2.model.PostHeader;
import za.co.sharksworld.android.v2.model.PostHeaderList;
import android.database.DataSetObserver;
import android.graphics.Color;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ListAdapter;
import android.widget.TextView;

public class PostHeaderListAdapter implements ListAdapter {


	private static final String DATE_FORMAT_STRING = "EEE d MMM yy h:mm a";

	public PostHeaderListAdapter(PostHeaderList pPostHeaderList) {
		mPostHeaderList = pPostHeaderList;
	}
	
	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (mPostHeaderList != null) return mPostHeaderList.getPostHeaders().size();
		else return 0;
	}

	@Override
	public Object getItem(int position) {
		if (mPostHeaderList != null && mPostHeaderList.getPostHeaders()!= null ) return mPostHeaderList.getPostHeaders().get(position);
		else return null;
	}

	@Override
	public long getItemId(int position) {
		if (mPostHeaderList != null && mPostHeaderList.getPostHeaders()!= null ) {
			PostHeader ph = mPostHeaderList.getPostHeaders().get(position);
			if (ph == null) return 0;
			else return ph.getPostId();
		} else 
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
	    if (view == null) {
	        LayoutInflater vi = LayoutInflater.from(parent.getContext());
	        view = vi.inflate(R.layout.post_header_list_item, null);
	    }
	    PostHeader postHeader = (PostHeader) getItem(position);
	    if (postHeader != null) {
	        TextView postTitleView = (TextView) view.findViewById(R.id.postHeaderTitle);
	        TextView postDateView = (TextView) view.findViewById(R.id.postHeaderDate);
	        TextView postCommentsView = (TextView) view.findViewById(R.id.postHeaderComments);
	        if (postTitleView != null) {
	        	postTitleView.setText(postHeader.getPostTitle());
	        }
	        if (postDateView != null) {
	        	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_STRING);
	        	postDateView.setText(simpleDateFormat.format(postHeader.getPostDate()));
	        } 
	        if (postCommentsView != null) {
	        	String commentText = parent.getResources().getString(string.comments);
	        	if (postHeader.getCommentCount() == 1) commentText = parent.getResources().getString(string.comment);
	        	postCommentsView.setText(postHeader.getCommentCount() + " " + commentText);
	        	if (postHeader.hasRecentComments()) {
	        		postCommentsView.setTextColor(Color.GREEN);
	        	} else {
	        		postCommentsView.setTextColor(Color.GRAY);
	        	}
	        }
	    }
	    return view;
	}

	@Override
	public int getItemViewType(int position) {
		return R.layout.post_header_list_item;
	}

	@Override
	public int getViewTypeCount() {
		
		return 1;
	}
	@Override
	public boolean areAllItemsEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled(int position) {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public boolean isEmpty() {
		return (getCount() == 0);
	}
	
	private PostHeaderList mPostHeaderList = null;

	

}
