package za.co.sharksworld.android.v2.util;

import java.io.IOException;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class WebUtil {
	public static Bitmap getBitmapFromUrl(String pUrlString) throws IOException {
		URL url = new URL(pUrlString);
		Bitmap bmp = BitmapFactory.decodeStream(url.openConnection()
				.getInputStream());
		return bmp;
	}

	public static boolean internetAvailable(Context pContext) {
		ConnectivityManager connectivity = (ConnectivityManager) pContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}

		}
		return false;

	}


}
