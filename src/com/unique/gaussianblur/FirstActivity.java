package com.unique.gaussianblur;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class FirstActivity extends Activity{
	
	private Button select_picture_button;
	static Bitmap bitmap;
	private Uri selectedImage;
	ContentResolver cr;
	
	private static int RESULT_LOAD_IMAGE = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.first_activity);
		
		select_picture_button=(Button)findViewById(R.id.button1);
		
		cr = this.getContentResolver();
		select_picture_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
	
				startActivityForResult(intent, RESULT_LOAD_IMAGE);
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
				&& null != data) {
			selectedImage=data.getData();
			
			try {
				bitmap = BitmapFactory.decodeStream(cr
						.openInputStream(selectedImage));
			} catch (FileNotFoundException e) {
				Log.e("Exception", e.getMessage(), e);
			}
			
			//将bitmap变小，使它可以用intent传输
			/*ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			byte[] bitmapByte = baos.toByteArray();
			*/
			Intent intent=new Intent();
			//intent.putExtra("bitmap", bitmapByte);
			//Log.e("aa", bitmapByte+"");
			intent.setClass(FirstActivity.this, MainActivity.class);
			startActivity(intent);
			finish();
		}

	}

}
