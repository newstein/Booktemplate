package com.sean.booktemplate;

import java.io.IOException;
import java.io.InputStream;

import com.sean.booktemplate.R;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MyBookGallery extends Activity {

	private MyBookPrefs myPreferences = null; // å����Ÿ    private static final int SWIPE_MIN_DISTANCE = 120;
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mybook_gallery);

		myPreferences = MyBookMain.myBookPreferences;

		Gallery myGallery = (Gallery) findViewById(R.id.gallery);
		myGallery.setAdapter(new ImageAdapter(this));
		myGallery.setBackgroundColor(Color.LTGRAY);
		myGallery.setGravity(Gravity.CENTER_VERTICAL);

		String data = getIntent().getStringExtra(MyBookImagePage.myPageExtra);
		myGallery.setSelection(Integer.parseInt(data)-1, true);

		myGallery.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				MyBookImagePage.myCurrentPage = position+1;
				finish();
			}
		});
	}

	public class ImageAdapter extends BaseAdapter {
		private Context myContext;
		int myGalleryItemBackground;

		public ImageAdapter(Context c) {
			myContext = c;
			// See res/values/attrs.xml for the <declare-styleable> that defines
			TypedArray a = obtainStyledAttributes(R.styleable.Gallery);
			myGalleryItemBackground = a.getResourceId(
					R.styleable.Gallery_android_galleryItemBackground, 0);
			a.recycle();
		}

		public int getCount() {
			return myPreferences.getPageNum();
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView = new ImageView(myContext);
			String imagePath = myPreferences.getPageBgImage(position+1);
			if (imagePath != null) {
				Bitmap myBitmap = null;
				try {
					InputStream imageIS = getAssets().open(imagePath);
					myBitmap = BitmapFactory.decodeStream(imageIS);
					imageIS.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				imageView.setImageBitmap(myBitmap);
			}
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			imageView.setLayoutParams(new Gallery.LayoutParams(400, 240));

			// The preferred Gallery item background
			imageView.setBackgroundResource(myGalleryItemBackground);
			return imageView;
		}
	}

}
