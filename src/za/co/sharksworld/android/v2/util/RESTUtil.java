package za.co.sharksworld.android.v2.util;

import java.io.BufferedInputStream;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

import java.net.URL;
import java.util.Scanner;

import org.json.JSONArray;

import org.json.JSONObject;

public class RESTUtil {

	private static final int CONNECTION_TIMEOUT = 5000;
	private static final int DATARETRIEVAL_TIMEOUT = 5000;

	public static JSONArray getArrayFromREST(String pServiceUrl)
			throws GeneralConnectivityException {
		JSONArray jsonArray = null;
		try {
			String resp = requestRESTService(pServiceUrl);
			if (resp == null)
				throw new GeneralConnectivityException();
			jsonArray = new JSONArray(resp);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new GeneralConnectivityException();
		}
		return jsonArray;
	}

	public static JSONObject getObjectFromREST(String pServiceUrl)
			throws GeneralConnectivityException {
		JSONObject jsonobject = null;
		try {
			String resp = requestRESTService(pServiceUrl);
			if (resp == null)
				throw new GeneralConnectivityException();
			jsonobject = new JSONObject(resp);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new GeneralConnectivityException();
		}
		return jsonobject;
	}

	public static JSONObject postObjectToREST(String pServiceUrl,
			JSONObject pJSONInput) throws GeneralConnectivityException {
		JSONObject jsonobject = null;
		try {
			String resp = postRESTService(pServiceUrl, pJSONInput);
			if (resp == null)
				throw new GeneralConnectivityException();
			jsonobject = new JSONObject(resp);
		} catch (Exception e) {
			e.printStackTrace();
			throw new GeneralConnectivityException();
		}
		return jsonobject;
	}

	protected static String requestRESTService(String pServiceUrl)
			throws GeneralConnectivityException {

		HttpURLConnection urlConnection = null;
		try {
			// create connection
			URL urlToRequest = new URL(pServiceUrl);
			urlConnection = (HttpURLConnection) urlToRequest.openConnection();
			urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
			urlConnection.setReadTimeout(DATARETRIEVAL_TIMEOUT);

			// handle issues
			int statusCode = urlConnection.getResponseCode();
			if (statusCode != HttpURLConnection.HTTP_OK) {
				throw new GeneralConnectivityException();
			}

			InputStream in = new BufferedInputStream(
					urlConnection.getInputStream());
			return getResponseText(in);

		} catch (Exception e) {
			e.printStackTrace();
			throw new GeneralConnectivityException();
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
		}
	}

	protected static String postRESTService(String pServiceUrl,
			JSONObject pJSONInput) throws GeneralConnectivityException {

		HttpURLConnection urlConnection = null;
		try {
			URL urlToRequest = new URL(pServiceUrl);

			urlConnection = (HttpURLConnection) urlToRequest.openConnection();
			urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
			urlConnection.setReadTimeout(DATARETRIEVAL_TIMEOUT);
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			urlConnection
					.setRequestProperty("Content-Type", "application/json");
			urlConnection.setRequestProperty("Accept", "application/json");
			urlConnection.setRequestMethod("POST");
			OutputStreamWriter wr = new OutputStreamWriter(
					urlConnection.getOutputStream());
			wr.write(pJSONInput.toString());
			wr.flush();
			int statusCode = urlConnection.getResponseCode();

			if (statusCode != HttpURLConnection.HTTP_OK) {
				throw new GeneralConnectivityException();
			}
			InputStream in = new BufferedInputStream(
					urlConnection.getInputStream());
			return getResponseText(in);

		} catch (Exception e) {

			e.printStackTrace();
			throw new GeneralConnectivityException();

		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
		}

	}

	private static String getResponseText(InputStream inStream) {
		// very nice trick from
		// http://weblogs.java.net/blog/pat/archive/2004/10/stupid_scanner_1.html
		Scanner scanner = new Scanner(inStream);
		String results = scanner.useDelimiter("\\A").next();
		scanner.close();
		return results;
	}

}
