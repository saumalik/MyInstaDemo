package org.example.instaexample;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.example.instaexample.InsDialog.OAuthDialogListener;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class InsApp {

	private InsSession mSession;
	private InsDialog mDialog;
	private OAuthAuthenticationListener mListener;
	private ProgressDialog mProgress;
	private String mAuthUrl;
	private String mTokenUrl;
	public static String mAccessToken;
	private Context mCtx;

	private String mClientId;
	private String mClientSecret;
	private String imageUrlStringStand, imageUrlStringLow;
	public static ArrayList<ImageUsed> arrPersons = new ArrayList<ImageUsed>();
	public JSONArray jsonArray = null;

	public JSONArray getJsonArray() {
		return jsonArray;
	}

	public void setJsonArray(JSONArray jsonArray) {
		this.jsonArray = jsonArray;
	}

	private static int WHAT_FINALIZE = 0;
	private static int WHAT_ERROR = 1;
	private static int WHAT_FETCH_INFO = 2;

	/**
	 * Callback url, as set in 'Manage OAuth Consumers' page
	 */

	public static String mCallbackUrl = "";
	private static final String AUTH_URL = "https://api.instagram.com/oauth/authorize/";
	private static final String TOKEN_URL = "https://api.instagram.com/oauth/access_token";
	public static final String API_URL = "https://api.instagram.com/v1";

	private static final String TAG = "MyAPI";

//	public InsApp(Context ctx) {
//		super(ctx);
//	}

	public InsApp(Context context, String clientId, String clientSecret, String callbackUrl) {

		mClientId = clientId;
		mClientSecret = clientSecret;
		mCtx = context;
		mSession = new InsSession(context);
		mAccessToken = mSession.getAccessToken();
		mCallbackUrl = callbackUrl;
		mTokenUrl = TOKEN_URL + "?client_id=" + clientId + "&client_secret=" + clientSecret + "&redirect_uri=" + mCallbackUrl + "&grant_type=authorization_code";
		mAuthUrl = AUTH_URL + "?client_id=" + clientId + "&redirect_uri=" + mCallbackUrl + "&response_type=code&display=touch&scope=likes+comments+relationships";

		OAuthDialogListener listener = new OAuthDialogListener() {
			@Override
			public void onComplete(String code) {
				getAccessToken(code);
			}

			@Override
			public void onError(String error) {
				mListener.onFail("Authorization failed");
			}
		};

		mDialog = new InsDialog(context, mAuthUrl, listener);
		mProgress = new ProgressDialog(context);
		mProgress.setCancelable(false);
	}

	private void getAccessToken(final String code) {
		mProgress.setMessage("Getting access token ...");
		mProgress.show();

		new Thread() {
			@Override
			public void run() {
				Log.i(TAG, "Getting access token");
				int what = WHAT_FETCH_INFO;
				try {
					URL url = new URL(TOKEN_URL);
					// URL url = new URL(mTokenUrl + "&code=" + code);
					Log.i(TAG, "Opening Token URL " + url.toString());
					HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
					urlConnection.setRequestMethod("POST");
					urlConnection.setDoInput(true);
					urlConnection.setDoOutput(true);
					// urlConnection.connect();
					OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
					writer.write("client_id=" + mClientId + "&client_secret=" + mClientSecret + "&grant_type=authorization_code" + "&redirect_uri=" + mCallbackUrl + "&code=" + code);
					writer.flush();
					String response = streamToString(urlConnection.getInputStream());
					Log.i(TAG, "response " + response);
					JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();

					mAccessToken = jsonObj.getString("access_token");
					Log.i(TAG, "Got access token: " + mAccessToken);

					String id = jsonObj.getJSONObject("user").getString("id");
					String user = jsonObj.getJSONObject("user").getString("username");
					String name = jsonObj.getJSONObject("user").getString("full_name");

					mSession.storeAccessToken(mAccessToken, id, user, name);

				} catch (Exception ex) {
					what = WHAT_ERROR;
					ex.printStackTrace();
				}

				mHandler.sendMessage(mHandler.obtainMessage(what, 1, 0));
			}
		}.start();
	}

	private void fetchUserNameAndPic() {
		mProgress.setMessage("Finalizing ...");

		new Thread() {
			@Override
			public void run() {
				Log.i(TAG, "Fetching user info");
				int what = WHAT_FINALIZE;
				try {
					URL url = new URL(API_URL + "/tags/" + "selfie/media/recent/?access_token=" + mAccessToken);

					Log.d(TAG, "Opening URL " + url.toString());
					
					InputStream inputStream = url.openConnection().getInputStream();
					
					String response = streamToString(inputStream);
					System.out.println(response);
					JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
					JSONArray jsonArray = jsonObj.getJSONArray("data");
					
					for(int i = 0; i <= jsonArray.length(); i++ ) {
						JSONObject mainImageJsonObjectStand = jsonArray.getJSONObject(i).getJSONObject("images").getJSONObject("standard_resolution");
						imageUrlStringStand = mainImageJsonObjectStand.getString("url");
						JSONObject mainImageJsonObjectLow = jsonArray.getJSONObject(i).getJSONObject("images").getJSONObject("low_resolution");
						imageUrlStringLow = mainImageJsonObjectLow.getString("url");
						
						url = new URL(imageUrlStringStand);

						// Creating an http connection to communicate with url
						HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

						// Connecting to url
						urlConnection.connect();
						
						InputStream iStream = urlConnection.getInputStream();
						
						File cacheDirectory = mCtx.getCacheDir();

						// Temporary file to store the downloaded image
						File tmpFile = new File(cacheDirectory.getPath() + "/myName" + i + ".png");

						// The FileOutputStream to the temporary file
						FileOutputStream fOutStream = new FileOutputStream(tmpFile);

						// Creating a bitmap from the downloaded inputstream
						Bitmap b = BitmapFactory.decodeStream(iStream);

						// Writing the bitmap to the temporary file as png file
						b.compress(Bitmap.CompressFormat.JPEG, 100, fOutStream);

						// Flush the FileOutputStream
						fOutStream.flush();

						// Close the FileOutputStream
						fOutStream.close();
						
				    
				    ImageUsed img = new ImageUsed();
					img.setImgStand(b);
//					img.imgLow = imageUrlStringLow;
					arrPersons.add(img);
					}
					
//					String name = jsonObj.getJSONObject("data").getString("full_name");
//					String bio = jsonObj.getJSONObject("data").getString("bio");
//					Log.i(TAG, "Got name: " + name + ", bio [" + bio + "]");
				} catch (Exception ex) {
					what = WHAT_ERROR;
					ex.printStackTrace();
				}

				mHandler.sendMessage(mHandler.obtainMessage(what, 2, 0));
			}
		}.start();	

	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == WHAT_ERROR) {
				mProgress.dismiss();
				if (msg.arg1 == 1) {
					mListener.onFail("Failed to get access token");
				} else if (msg.arg1 == 2) {
					mListener.onFail("Failed to get user information");
				}
			} else if (msg.what == WHAT_FETCH_INFO) {
				fetchUserNameAndPic();
			} else {
				mProgress.dismiss();
				mListener.onSuccess();
			}
		}
	};

	public boolean hasAccessToken() {
		return (mAccessToken == null) ? false : true;
	}

	public void setListener(OAuthAuthenticationListener listener) {
		mListener = listener;
	}

	public String getUserName() {
		return mSession.getUsername();
	}

	public String getId() {
		return mSession.getId();
	}

	public String getName() {
		return mSession.getName();
	}

	public void authorize() {
		// Intent webAuthIntent = new Intent(Intent.ACTION_VIEW);
		// webAuthIntent.setData(Uri.parse(AUTH_URL));
		// mCtx.startActivity(webAuthIntent);
		mDialog.show();
	}

	private String streamToString(InputStream is) throws IOException {
		String str = "";

		if (is != null) {
			StringBuilder sb = new StringBuilder();
			String line;

			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));

				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}

				reader.close();
			} finally {
				is.close();
			}

			str = sb.toString();
		}

		return str;
	}

	public void resetAccessToken() {
		if (mAccessToken != null) {
			mSession.resetAccessToken();
			mAccessToken = null;
		}
	}

	public interface OAuthAuthenticationListener {
		public abstract void onSuccess();

		public abstract void onFail(String error);
	}
}