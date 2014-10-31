package za.co.sharksworld.android.v2;

import za.co.sharksworld.android.v2.util.Constants;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmMessageHandler extends IntentService {

	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;

	public GcmMessageHandler() {
		super("GcmMessageHandler");
	}


	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		// The getMessageType() intent parameter must be the intent you received
		// in your BroadcastReceiver.
		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) { // has effect of unparcelling Bundle
			/*
			 * Filter messages based on message type. Since it is likely that
			 * GCM will be extended in the future with new message types, just
			 * ignore any message types you're not interested in, or that you
			 * don't recognize.
			 */
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType)) {
				// sendNotification("Send error: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
				// sendNotification("Deleted messages on server: " +
				// extras.toString());
				// If it's a regular GCM message, do some work.
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {
				for (String key : extras.keySet()) {
					//System.out.println("onHandleIntent:: " + key + ":"
					//		+ extras.get(key));
				}
				try {
					long postid = Long.parseLong(extras.getString("post_id"));
					String post_title = extras.getString("post_title");
					String post_author = extras.getString("post_author");
					// Post notification of received message.
					sendNotification(postid, post_title, post_author);
				} catch (Exception e) {
					//System.out.println("onHandleIntent:: Exception generation notification");
				}

			}
		}
		// Release the wake lock provided by the WakefulBroadcastReceiver.
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	// Put the message into a notification and post it.
	// This is just one simple example of what you might choose to do with
	// a GCM message.
	private void sendNotification(long postID, String title, String author) {
		mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Intent resultIntent = new Intent(this, PostActivity.class);
		resultIntent.putExtra(Constants.POST_ID, postID);
		resultIntent.putExtra(Constants.FROM_NOTIFICATION, true);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addParentStack(PostActivity.class);
		stackBuilder.addNextIntent(resultIntent);

		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent((int)postID,
				PendingIntent.FLAG_CANCEL_CURRENT);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("Sharksword: New Post")
				.setStyle(new NotificationCompat.BigTextStyle().bigText(title))
				.setContentText(title + " (by " + author + ")");

		mBuilder.setContentIntent(resultPendingIntent);
		mNotificationManager.notify((int) postID , mBuilder.build());
	}
}