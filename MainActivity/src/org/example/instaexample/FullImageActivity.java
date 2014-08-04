package org.example.instaexample;

import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class FullImageActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.largeimage);

		ImageView myimage = (ImageView) findViewById(R.id.viewLargeImage);
//		Bundle b = getIntent();
		 Intent intent = getIntent();

		 int imageId = (Integer) intent.getExtras().get("LIMAGE");
//		int imageId = (Integer) b.getSerializable("LIMAGE");
		System.out.println("********" + imageId + "**********");
//		InputStream is = this.getResources().openRawResource(imageId);
//		Bitmap originalBitmap = BitmapFactory.decodeStream(is);
//		Matrix imageMatrix = new Matrix();
//		imageMatrix.postRotate(90);
//		Bitmap scaledBitmap = Bitmap.createBitmap(originalBitmap, myimage.getWidth(), myimage.getHeight(), originalBitmap.getWidth(), originalBitmap.getHeight(), imageMatrix, false);
		ImageUsed bean = new ImageUsed();
//		ImageUsed bean = (ImageUsed) InsApp.arrPersons.get(imageId);
//		myimage.setImageBitmap(scaledBitmap);
		myimage.setImageBitmap(bean.getImgStand());
		myimage.setScaleType(ScaleType.FIT_XY);

	}
}