package org.example.instaexample;

import java.io.InputStream;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ImageShowActivity extends Activity implements OnItemClickListener {

	ListView lview3;
	ListViewCustomAdapter adapter;
	private ProgressDialog mProgress;
	InsApp insApp = null;
	InputStream iStream = null;
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mylistview);

		mProgress = new ProgressDialog(this);
		mProgress.setCancelable(false);

//		insApp = new InsApp(this, null, null, null);
//		jArray = insApp.getJsonArray();

		lview3 = (ListView) findViewById(R.id.listView4);
		adapter = new ListViewCustomAdapter(this, InsApp.arrPersons);
		lview3.setAdapter(adapter);

		lview3.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Intent fullScreenIntent = new Intent(v.getContext(), FullImageActivity.class);
//		Bundle b = new Bundle();
//        b.putSerializable("LIMAGE",v.getId()) ;
//        fullScreenIntent.putExtras(b);
        fullScreenIntent.putExtra("LIMAGE", v.getId());
        startActivity(fullScreenIntent); 
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}