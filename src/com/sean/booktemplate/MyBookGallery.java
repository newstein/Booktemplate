package com.sean.booktemplate;

import java.io.IOException;
import java.io.InputStream;


import com.sean.booktemplate.R;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Shader.TileMode;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView.ScaleType;

public class MyBookGallery extends Activity {

	private MyBookPrefs myPreferences = null; // å����Ÿ    private static final int SWIPE_MIN_DISTANCE = 120;
	
    private Context mContext;	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mybook_gallery);

		myPreferences = MyBookMain.myBookPreferences;

//		Gallery myGallery = (Gallery) findViewById(R.id.gallery);
//		myGallery.setAdapter(new ImageAdapter(this));
//		myGallery.setBackgroundColor(Color.LTGRAY);
//		myGallery.setGravity(Gravity.CENTER_VERTICAL);

//		String data = getIntent().getStringExtra(MyBookImagePage.myPageExtra);
//		myGallery.setSelection(Integer.parseInt(data)-1, true);
//
//		myGallery.setOnItemClickListener(new OnItemClickListener() {
//			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
//				MyBookImagePage.myCurrentPage = position+1;
//				finish();
//			}
//		});
		
		//Coverflow 
        CoverFlow coverFlow;
        coverFlow =  (CoverFlow) findViewById(R.id.coverflow);
        ImageAdapter coverImageAdapter =  new ImageAdapter(this);
        
        coverImageAdapter.createReflectedImages();
        
        coverFlow.setAdapter(coverImageAdapter);		
        coverFlow.setOnItemClickListener(new CoverAdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(CoverAdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
//                Toast.makeText(getBaseContext(), 
//                        "You have selected picture " + (arg2+1) + " of Antartica", 
//                        Toast.LENGTH_SHORT).show();
              MyBookImagePage.myCurrentPage = arg2+1;
              finish();
                
            }
            
        });
        
        
        coverFlow.setSpacing(-15);
        coverFlow.setSelection(3, true);
        mContext=this;
	}


    public class ImageAdapter extends BaseAdapter {
        int myGalleryItemBackground;
        private Context mContext;



        private ImageView[] mImages;
        
        public ImageAdapter(Context c) {
            mContext = c;
            mImages = new ImageView[myPreferences.getPageNum()];

        }
        public boolean createReflectedImages() {
                //The gap we want between the reflection and the original image
                final int reflectionGap = 4;
                
                
                int index = 0;
//                for (int imageId : mImageIds) {
                for (int imageId=0 ;imageId< myPreferences.getPageNum();imageId++) {
//                    Bitmap originalImage = BitmapFactory.decodeResource(getResources(), 
//                            imageId);
                    Bitmap originalImage =null;
                    String imagePath = myPreferences.getPageBgImage(imageId+1);
                    if (imagePath != null) {
//                        Bitmap myBitmap = null;
                        try {
                            InputStream imageIS = getAssets().open(imagePath);
                            originalImage = BitmapFactory.decodeStream(imageIS);
                            imageIS.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        
                    }                    
                    
                    int width = originalImage.getWidth();
                    int height = originalImage.getHeight();
                    
           
                    //This will not scale but will flip on the Y axis
                    Matrix matrix = new Matrix();
                    matrix.preScale(1, -1);
                    
                    //Create a Bitmap with the flip matrix applied to it.
                    //We only want the bottom half of the image
                    Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, height/2, width, height/2, matrix, false);
                    
                        
                    //Create a new bitmap with same width but taller to fit reflection
                    Bitmap bitmapWithReflection = Bitmap.createBitmap(width 
                      , (height + height/2), Config.ARGB_8888);
                  
                   //Create a new Canvas with the bitmap that's big enough for
                   //the image plus gap plus reflection
                   Canvas canvas = new Canvas(bitmapWithReflection);
                   //Draw in the original image
                   canvas.drawBitmap(originalImage, 0, 0, null);
                   //Draw in the gap
                   Paint deafaultPaint = new Paint();
                   canvas.drawRect(0, height, width, height + reflectionGap, deafaultPaint);
                   //Draw in the reflection
                   canvas.drawBitmap(reflectionImage,0, height + reflectionGap, null);
                   
                   //Create a shader that is a linear gradient that covers the reflection
                   Paint paint = new Paint(); 
                   LinearGradient shader = new LinearGradient(0, originalImage.getHeight(), 0, 
                     bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff, 0x00ffffff, 
                     TileMode.CLAMP); 
                   //Set the paint to use this shader (linear gradient)
                   paint.setShader(shader); 
                   //Set the Transfer mode to be porter duff and destination in
                   paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN)); 
                   //Draw a rectangle using the paint with our linear gradient
                   canvas.drawRect(0, height, width, 
                     bitmapWithReflection.getHeight() + reflectionGap, paint); 
                   
                   ImageView imageView = new ImageView(mContext);
                   imageView.setImageBitmap(bitmapWithReflection);
//                   imageView.setLayoutParams(new CoverFlow.LayoutParams(120, 180));
                   imageView.setLayoutParams(new CoverFlow.LayoutParams(400, 500));
                   imageView.setScaleType(ScaleType.MATRIX);
                   mImages[index++] = imageView;
                   
                }
                return true;
        }

        public int getCount() {
            return myPreferences.getPageNum();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            //Use this code if you want to load from resources
//            ImageView i = new ImageView(mContext);
//            i.setImageResource(mImageIds[position]);
//            i.setLayoutParams(new CoverFlow.LayoutParams(130, 130));
//            i.setScaleType(ImageView.ScaleType.MATRIX);           
//            return i;
            
            return mImages[position];
        }
         /** Returns the size (0.0f to 1.0f) of the views 
         * depending on the 'offset' to the center. */ 
         public float getScale(boolean focused, int offset) { 
           /* Formula: 1 / (2 ^ offset) */ 
             return Math.max(0, 1.0f / (float)Math.pow(2, Math.abs(offset))); 
         } 

    }	
	

}
