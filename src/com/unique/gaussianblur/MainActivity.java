package com.unique.gaussianblur;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

	private ImageView imageView0;
	private SeekBar seekBar;
	private ToggleButton toggleButton;
	private Button button;
	private Button saveButton;
	private Button wallpaperButton;
	private EditText editText;

	private Uri selectedImage;
	private static int RESULT_LOAD_IMAGE = 0;
	Bitmap bitmap0;
	Bitmap newBitmap;
	Bitmap icon;
	WallpaperManager wallpaperManager;

	private StackBlurManager stackBlurManager;

	private String IMAGE_TO_ANALYZE = "android_platform_256.png";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		imageView0 = (ImageView) findViewById(R.id.imageView);
		seekBar = (SeekBar) findViewById(R.id.seekBar);
		button = (Button) findViewById(R.id.button1);
		editText = (EditText) findViewById(R.id.editText1);
		saveButton = (Button) findViewById(R.id.button_save);
		wallpaperButton = (Button) findViewById(R.id.button_wallpaper);

		bitmap0 = FirstActivity.bitmap;
		newBitmap = FirstActivity.bitmap;
		imageView0.setImageBitmap(bitmap0);
		stackBlurManager = new StackBlurManager(bitmap0);
		
		wallpaperManager = WallpaperManager
				.getInstance(this);

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
				// 改变模糊上限
				stackBlurManager.process(progress * 20);
				imageView0.setImageBitmap(stackBlurManager.returnBlurredImage());
				newBitmap = stackBlurManager.returnBlurredImage();
			}
		});

		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				imageView0.setImageBitmap(newBitmap);

				String str1 = "";
				str1 = editText.getText().toString();
				drawNewBitmap(imageView0, str1);
			}
		});

		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				saveMyBitmap(icon, icon + "");
			}
		});

		wallpaperButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Resources res = getResources();
				try {
					wallpaperManager.setBitmap(icon);
					Toast.makeText(getApplicationContext(),
							"壁纸设置成功", Toast.LENGTH_LONG)
							.show();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

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
				imageView0 = (ImageView) findViewById(R.id.imageView);
				/* 将Bitmap设定到ImageView */
				imageView0.setImageBitmap(bitmap0);
			} catch (FileNotFoundException e) {
				Log.e("Exception", e.getMessage(), e);
			}
		}

	}

	// 图片上加文字
	private void drawNewBitmap(ImageView imageView, String str) {
		Bitmap photo = newBitmap;

		int width = photo.getWidth();
		int hight = photo.getHeight();
		System.out.println("宽" + width + "高" + hight);

		icon = Bitmap.createBitmap(width, hight, Bitmap.Config.ARGB_8888); // 建立一个空的BItMap
		Canvas canvas = new Canvas(icon);// 初始化画布绘制的图像到icon上

		Paint photoPaint = new Paint(); // 建立画笔
		photoPaint.setDither(true); // 获取跟清晰的图像采样
		photoPaint.setFilterBitmap(true);// 过滤一些

		Rect src = new Rect(0, 0, photo.getWidth(), photo.getHeight());// 创建一个指定的新矩形的坐标
		Rect dst = new Rect(0, 0, width, hight);// 创建一个指定的新矩形的坐标
		canvas.drawBitmap(photo, src, dst, photoPaint);// 将photo 缩放或则扩大到
		// dst使用的填充区photoPaint
		Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DEV_KERN_TEXT_FLAG);// 设置画笔
		textPaint.setShadowLayer(10, 10, 10, Color.GRAY);
		textPaint.setTextSize(80.0f);// 字体大小
		textPaint.setTypeface(Typeface.DEFAULT_BOLD);// 采用默认的宽度
		textPaint.setColor(Color.BLACK);// 采用的颜色
		canvas.drawText(str, 150, 150, textPaint);// 绘制上去字，开始未知x,y采用那只笔绘制
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		imageView.setImageBitmap(icon);

	}

	// 读取asset中的图片（未使用）
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

	// 保存到SD卡
	public void saveMyBitmap(Bitmap bitmap, String bitName) {
		File f = new File("/sdcard/" + bitName + ".png");

		FileOutputStream fOut = null;
		try {
			f.createNewFile();
			fOut = new FileOutputStream(f);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
			fOut.flush();
			fOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Toast.makeText(getApplicationContext(),
				"成功保存到" + "/sdcard/" + bitName + ".png", Toast.LENGTH_LONG)
				.show();

	}

}