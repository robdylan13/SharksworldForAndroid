package za.co.sharksworld.android.v2;


import java.text.SimpleDateFormat;
import java.util.Map;

import za.co.sharksworld.android.v2.model.Comment;
import za.co.sharksworld.android.v2.model.CommentList;
import za.co.sharksworld.android.v2.util.Constants;

import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class CommentsListAdapter implements ListAdapter {

	
	private static final String DATE_FORMAT_STRING = "EEE d MMM yy h:mm a";

	public CommentsListAdapter(CommentList pCommentList, SharksworldApplication pSharksworldApplication) {
		mCommentList = pCommentList;
		mApplicationRef = pSharksworldApplication;
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
		int tempCount = 0;
		if (mCommentList != null) {
			tempCount = mCommentList.getComments().size();
			if (isCondensed() && tempCount > MAX_COMMENTS)
				return MAX_COMMENTS;
			else
				return tempCount;
		
		}
		else return 0;
	}

	@Override
	public Object getItem(int position) {
		
		if (mCommentList != null && mCommentList.getComments()!= null ) {
			if (!isCondensed() || mCommentList.getComments().size() <= MAX_COMMENTS  )
				return mCommentList.getComments().get(position);
			else {
				int start = mCommentList.getComments().size() - MAX_COMMENTS;
				return mCommentList.getComments().get(start+position);
			}
		}
		else return null;
	}

	@Override
	public long getItemId(int position) {
		/*if (mCommentList != null && mCommentList.getComments()!= null ) {
			Comment c = mCommentList.getComments().get(position);
			if (c == null) return 0;
			else return c.getCommentNumber();
		} else 
		return 0; */
		if (getItem(position) != null) {
			return ((Comment) getItem(position)).getCommentNumber();
		} else
			return 0;
	}

	public boolean hasMoreComments() {
		if (mCommentList != null) {
			int tempCount = mCommentList.getComments().size();
			if ( tempCount > MAX_COMMENTS)
				return true;
			else
				return false;
		
		}
		return false;
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
	        view = vi.inflate(R.layout.comment_list_item, null);
	    }
	    Comment comment = (Comment) getItem(position);
	    if (comment != null) {

	       TextView contentTextViewFull = (TextView) view.findViewById(R.id.textViewCommentContentFull);
	    
	       Spanned spannedComment = getSmiledText(comment.getCommentContent());
	      contentTextViewFull.setText(spannedComment);
	
	        TextView contentHeaderTextView = (TextView) view.findViewById(R.id.textViewCommentHeader);
	        String commentDateString = new SimpleDateFormat(DATE_FORMAT_STRING).format(comment.getCommentDate());
	        contentHeaderTextView.setText("Comment "+ comment.getCommentNumber() + " by " + comment.getCommentAuthor() + " on " + commentDateString );
	        Bitmap bitmap = mApplicationRef.getImageFromCache(comment.getCommentAuthorAvatarURL());
	        ImageView authorAvatarImageView = (ImageView) view.findViewById(R.id.imageViewCommentAuthorAvatar);
	        authorAvatarImageView.setImageBitmap(bitmap);
	        authorAvatarImageView.setOnClickListener(new AuthorAvatarReplyClickLister(comment.getCommentId(),comment.getCommentAuthor(),comment.getCommentNumber()));
	    }
	    return view;
	}

	
	private class AuthorAvatarReplyClickLister implements OnClickListener {

		private long mCommentID;
		private String mAuthor;
		private int mCommentNum;

		public AuthorAvatarReplyClickLister(long pCommentID, String pAuthor, int pCommentNum) {
			mCommentID = pCommentID;
			mCommentNum = pCommentNum;
			mAuthor = pAuthor;
		}
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			BaseActivity a = (BaseActivity)v.getContext();
			if (a.sessionAuthenticated()) {
			PostIDProvider prov = (PostIDProvider) a;
			Intent intent = new Intent(v.getContext(), NewCommentActivity.class);
			intent.putExtra(Constants.POST_ID, prov.getPostId());
			intent.putExtra(Constants.IS_REPLY, true);
			intent.putExtra(Constants.REPLY_COMMENT_AUTHOR, mAuthor);
			intent.putExtra(Constants.REPLY_COMMENT_ID, mCommentID);
			intent.putExtra(Constants.REPLY_COMMENT_NUMBER, mCommentNum);
			a.startActivityForResult(intent, Constants.NEW_COMMENT_REPLY_REQUEST);
			} else {
				Toast toast = Toast.makeText(a,
						a.getResources().getString(R.string.please_log_in),
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.TOP, 0, 400);
				toast.show();
			}
		}
		
	}

	public Spannable getSmiledText(String text) {
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        if (Constants.EMOTICONS.size() > 0) {
            int index;
            for (index = 0; index < builder.length(); index++) {
                if (Character.toString(builder.charAt(index)).equals(":") || Character.toString(builder.charAt(index)).equals(";")) {
                    for (Map.Entry<String, Integer> entry : Constants.EMOTICONS.entrySet()) {
                        int length = entry.getKey().length();
                        if (index + length > builder.length())
                            continue;
                        if (builder.subSequence(index, index + length).toString().equals(entry.getKey())) {
                            builder.setSpan(new ImageSpan(mApplicationRef, entry.getValue()), index, index + length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            index += length - 1;
                            break;
                        }
                    }
                }
            }
        }
        return builder;
    }
	
	@Override
	public int getItemViewType(int position) {
		return R.layout.comment_list_item;
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public boolean isEmpty() {
		return (getCount() == 0);
	}

	@Override
	public boolean areAllItemsEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnabled(int position) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean isCondensed() {
		return mCondensed;
	}


	public void setCondensed(boolean pCondensed) {
		this.mCondensed = pCondensed;
	}

	private CommentList mCommentList = null;
	private SharksworldApplication mApplicationRef = null;
	private boolean mCondensed = true;
	private boolean mDirty = false;
	private static final int MAX_COMMENTS=10;

	public void setDirty(boolean b) {
		// TODO Auto-generated method stub
		mDirty = b;
	}
	
	public boolean isDirty() {
		return mDirty;
	}
	
	
}
