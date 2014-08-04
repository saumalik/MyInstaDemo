package org.example.instaexample;

import org.example.instaexample.InsApp.OAuthAuthenticationListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private InsApp mApp;
	private Button btnConnect;
	private TextView tvSummary;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mApp = new InsApp(this, AppData.CLIENT_ID,
				AppData.CLIENT_SECRET, AppData.CALLBACK_URL);
		mApp.setListener(listener);

		tvSummary = (TextView) findViewById(R.id.tvSummary);

		btnConnect = (Button) findViewById(R.id.btnConnect);
		btnConnect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (mApp.hasAccessToken()) {
					final AlertDialog.Builder builder = new AlertDialog.Builder(
							MainActivity.this);
					builder.setMessage("Disconnect from App?")
							.setCancelable(false)
							.setPositiveButton("Yes",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											mApp.resetAccessToken();
											btnConnect.setText("Connect");
											tvSummary.setText("Not connected");
										}
									})
							.setNegativeButton("No",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											dialog.cancel();
										}
									});
					final AlertDialog alert = builder.create();
					alert.show();
				} else {
					mApp.authorize();
				}
			}
			
		});

		if (mApp.hasAccessToken()) {
			tvSummary.setText("Connected as " + mApp.getUserName());
			btnConnect.setText("Disconnect");
		}

	}
	

	OAuthAuthenticationListener listener = new OAuthAuthenticationListener() {

		@Override
		public void onSuccess() {
			tvSummary.setText("Connected as " + mApp.getUserName());
			btnConnect.setText("Disconnect");
		}

		@Override
		public void onFail(String error) {
			Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
		}
	};
	
	public void onShow(View view) {
		System.out.println("HI");
		Intent i = new Intent(MainActivity.this, ImageShowActivity.class);
		startActivity(i);
	}
}