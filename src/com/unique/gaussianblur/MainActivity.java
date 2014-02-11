package com.unique.gaussianblur;

import java.io.ByteArrayOutputStream;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

	private ImageView myImageView;
	private SeekBar seekBar;
	private Button button;// 原来的添加文字button，现在已经没有用了
	private Button saveButton;
	private Button wallpaperButton;
	private Button shareButton;
	private EditText editText;

	private Uri selectedImage;// 从sd卡中获取的图片的uri
	private static int RESULT_LOAD_IMAGE = 0;// onActivityResult中requestCode的值
	Bitmap bitmap0;
	Bitmap newBitmap;
	Bitmap icon;
	WallpaperManager wallpaperManager;
	float startX;//touch的起始x
	float startY;// touch的起始y
	String str1;

	private StackBlurManager stackBlurManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		myImageView = (ImageView) findViewById(R.id.imageView);
		myImageView.setOnTouchListener(touch);// 使图片可触摸
		seekBar = (SeekBar) findViewById(R.id.seekBar);
		button = (Button) findViewById(R.id.button1);
		editText = (EditText) findViewById(R.id.editText1);
		saveButton = (Button) findViewById(R.id.button_save);
		wallpaperButton = (Button) findViewById(R.id.button_wallpaper);
		shareButton=(Button)findViewById(R.id.button_share);

		bitmap0 = FirstActivity.bitmap;
		newBitmap = FirstActivity.bitmap;
		myImageView.setImageBitmap(bitmap0);
		stackBlurManager = new StackBlurManager(bitmap0);

		wallpaperManager = WallpaperManager.getInstance(this);

		//监听 拖动条
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
				stackBlurManager.process(progress * 25);
				myImageView.setImageBitmap(stackBlurManager
						.returnBlurredImage());
				newBitmap = stackBlurManager.returnBlurredImage();
			}
		});

		//监听 添加文字button 现在已经没有用了
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				myImageView.setImageBitmap(newBitmap);
				
			}
		});

		//监听 保存button
		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				saveMyBitmap(icon, icon + "");
			}
		});

		//监听 设为壁纸button
		wallpaperButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					wallpaperManager.setBitmap(icon);
					Toast.makeText(getApplicationContext(), "壁纸设置成功",
							Toast.LENGTH_LONG).show();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		//监听 分享button
		shareButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent shareIntent = new Intent(Intent.ACTION_SEND);  
		        shareIntent.setType("image/*");
		        
		        //先储存到sd卡中
		        File f = new File("/sdcard/" + icon + ".png");
				FileOutputStream fOut = null;
				try {
					f.createNewFile();
					fOut = new FileOutputStream(f);
					icon.compress(Bitmap.CompressFormat.PNG, 100, fOut);
					fOut.flush();
					fOut.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
		        //再分享uri，如果直接分享bitmap会超过intent的上限
				Uri uri = Uri.fromFile(getFileStreamPath(icon + ".png"));  
		        shareIntent.putExtra(Intent.EXTRA_STREAM, uri); 
		        startActivity(Intent.createChooser(shareIntent, getTitle()));  
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
				myImageView = (ImageView) findViewById(R.id.imageView);
				/* 将Bitmap设定到ImageView */
				myImageView.setImageBitmap(bitmap0);
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
		canvas.drawBitmap(photo, src, dst, photoPaint);// 将photo
														// 缩放或则扩大到dst使用的填充区photoPaint

		Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DEV_KERN_TEXT_FLAG);// 设置画笔
		textPaint.setShadowLayer(10, 10, 10, Color.GRAY);// 设置阴影
		textPaint.setTextSize(80.0f);// 字体大小
		textPaint.setTypeface(Typeface.DEFAULT_BOLD);// 采用默认的宽度
		textPaint.setColor(Color.BLACK);// 采用的颜色

		canvas.drawText(str, startX, startY, textPaint);// 绘制上去字，开始未知x,y采用那只笔绘制
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		imageView.setImageBitmap(icon);

	}

	private View.OnTouchListener touch = new OnTouchListener() {
		
		Canvas canvas;
		Paint paint;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			paint = new Paint();
			paint.setStrokeWidth(5);
			paint.setColor(Color.RED);

			switch (event.getAction()) {
			// 用户按下动作
			case MotionEvent.ACTION_DOWN:
				Bitmap photo;
				str1 = "";
				str1 = editText.getText().toString();
				drawNewBitmap(myImageView, str1);
				// 第一次绘图初始化内存图片，指定背景为白色
				if (icon == null)
					photo = newBitmap;
				photo = icon;

				int width = photo.getWidth();
				int hight = photo.getHeight();
				System.out.println("宽" + width + "高" + hight);

				icon = Bitmap.createBitmap(width, hight,
						Bitmap.Config.ARGB_8888); // 建立一个空的BItMap
				canvas = new Canvas(icon);// 初始化画布绘制的图像到icon上
				Paint photoPaint = new Paint(); // 建立画笔
				photoPaint.setDither(true); // 获取跟清晰的图像采样
				photoPaint.setFilterBitmap(true);// 过滤一些
				Rect src = new Rect(0, 0, photo.getWidth(), photo.getHeight());// 创建一个指定的新矩形的坐标
				Rect dst = new Rect(0, 0, width, hight);// 创建一个指定的新矩形的坐标
				canvas.drawBitmap(photo, src, dst, photoPaint);

				// 记录开始触摸的点的坐标
				startX = event.getX();
				startY = event.getY();
				break;
			// 用户手指在屏幕上移动的动作
			case MotionEvent.ACTION_MOVE:
				// 记录移动位置的点的坐标
				float stopX = event.getX();
				float stopY = event.getY();

				// 根据两点坐标，绘制连线
				canvas.drawLine(startX, startY, stopX, stopY, paint);

				// 更新开始点的位置
				startX = event.getX();
				startY = event.getY();

				// 把图片展示到ImageView中
				myImageView.setImageBitmap(icon);
				break;
			case MotionEvent.ACTION_UP:

				break;
			default:
				break;
			}
			return true;
		}
	};

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