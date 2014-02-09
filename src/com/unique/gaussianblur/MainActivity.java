package com.unique.gaussianblur;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

	private ImageView imageView;
	private SeekBar seekBar;
	private ToggleButton toggleButton;
	private Button button;
	
	private Uri selectedImage;
	private static int RESULT_LOAD_IMAGE = 0;
	Bitmap bitmap0;

	private StackBlurManager stackBlurManager;

	private String IMAGE_TO_ANALYZE = "android_platform_256.png";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		imageView=(ImageView)findViewById(R.id.imageView);
		seekBar=(SeekBar)findViewById(R.id.seekBar);
		//toggleButton=(ToggleButton)findViewById(R.id.toggleButton);
		//button=(Button)findViewById(R.id.button1);
		
		/*Intent intent = getIntent();
		byte[] bis = intent.getByteArrayExtra("bitmap");
		bitmap0 = BitmapFactory.decodeByteArray(bis, 0, bis.length);*/
		
		bitmap0=FirstActivity.bitmap;
		
		imageView.setImageBitmap(bitmap0);
		
		stackBlurManager = new StackBlurManager(bitmap0);
		
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				stackBlurManager.process(progress*5);
				imageView.setImageBitmap(stackBlurManager.returnBlurredImage() );
			}
		});
		
		/*button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

				startActivityForResult(intent, RESULT_LOAD_IMAGE);
			}
		});*/
		
		/*toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		        if (isChecked) {
		        	IMAGE_TO_ANALYZE = "image_transparency.png";
		        	stackBlurManager = new StackBlurManager(getBitmapFromAsset(getApplicationContext(), IMAGE_TO_ANALYZE));
		        	imageView.setImageDrawable(getResources().getDrawable(R.drawable.image_transparency));
		        } else {
		        	IMAGE_TO_ANALYZE = "android_platform_256.png";
		        	stackBlurManager = new StackBlurManager(getBitmapFromAsset(getApplicationContext(), IMAGE_TO_ANALYZE));
		        	imageView.setImageDrawable(getResources().getDrawable(R.drawable.android_platform_256));
		        }
		    }
		});*/
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
				&& null != data) {
			selectedImage = data.getData();
			Log.e("uri", selectedImage.toString());
			Log.e("path", selectedImage.getPath());
			ContentResolver cr = this.getContentResolver();
			try {
				bitmap0 = BitmapFactory.decodeStream(cr
						.openInputStream(selectedImage));
				imageView = (ImageView) findViewById(R.id.imageView);
				/* 将Bitmap设定到ImageView */
				imageView.setImageBitmap(bitmap0);
			} catch (FileNotFoundException e) {
				Log.e("Exception", e.getMessage(), e);
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private Bitmap getBitmapFromAsset(Context context, String strName) {
		Log.e("a", "first");
		AssetManager assetManager = context.getAssets();
		InputStream istr;
		Bitmap bitmap = null;
		try {
			istr = assetManager.open(strName);
			bitmap = BitmapFactory.decodeStream(istr);
		} catch (IOException e) {
			return null;
		}
		return bitmap;
	}

}