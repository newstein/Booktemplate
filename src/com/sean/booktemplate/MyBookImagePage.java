package com.sean.booktemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import com.sean.booktemplate.R;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MyBookImagePage extends Activity implements OnClickListener{

	// ���� ������ ��ȣ ����.
	public static int myCurrentPage = 0;
	public static String myPageExtra = "Current Page";

	private MyBookPrefs myPreferences = null; // å����Ÿ
	private MediaPlayer myMediaPlayer = null; // ��׶��� �÷��̾�.
	private DisplayMetrics myMetrics = null; // ���÷��� ����

	private boolean muteState = false; // �Ҹ�����
	private boolean autoPage = false; // �ڵ����
	private int totalPageNum = 0; // �� ������ ����

	// ������ ����
	private int totalEffectNum = 0; // ���� ������  �� ȿ���� ����
	private HashMap<Integer, Integer> mySoundPoolMap = null;
	private SoundPool mySoundPool = null;

	// �׽�Ʈ ������ ���̰� �ϱ�.
	private boolean testIcon = false;

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;
 
    int REL_SWIPE_MIN_DISTANCE = 0;
    int REL_SWIPE_MAX_OFF_PATH = 0;
    int REL_SWIPE_THRESHOLD_VELOCITY = 0;     
 
    private float initialX = 0;  
    private float initialY = 0;  
    private float deltaX = 0;  
    private float deltaY = 0;  
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mybook_imagepage);

		myPreferences = MyBookMain.myBookPreferences;
		myMediaPlayer = MyBookMain.myBookBGPlayer;
		myMetrics = getResources().getDisplayMetrics();

		muteState = false;
		autoPage = true;
		totalPageNum = myPreferences.getPageNum();

		myCurrentPage = 0; // ��Ʈ�� �������� ����.

        // Gesture detection
        gestureDetector = new GestureDetector(new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {
                    return true;
                }
                return false;
            }
        };      
		
		
		// home
		final ImageView homeImage = (ImageView)findViewById(R.id.imagePageHomeImage);
		if (testIcon) {
			homeImage.setImageResource(R.drawable.home);
			homeImage.setAlpha(0x88);
		}
		homeImage.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				finish();
			}
		});

		// gallery open
		final ImageView openImage = (ImageView)findViewById(R.id.imagePageOpenImage);
		if (testIcon) {
			openImage.setImageResource(R.drawable.open);
			openImage.setAlpha(0x88);
		}
		openImage.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				//Toast.makeText(MyBookPage.this, "��ü������ ����� �̵�", Toast.LENGTH_SHORT).show();
	    		Intent galleryPage = new Intent(MyBookImagePage.this, MyBookGallery.class);
	    		galleryPage.putExtra(myPageExtra, String.valueOf(myCurrentPage));
	    		startActivity(galleryPage);
			}
		});

		// mute
		final ImageView muteImage = (ImageView)findViewById(R.id.imagePageMuteImage);
		if (testIcon) {
			muteImage.setImageResource(R.drawable.volume);
			muteImage.setAlpha(0x88);
		}
		muteImage.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				if (muteState == false) {
					muteState = true;
					myMediaPlayer.setVolume(0, 0);
					if (testIcon) {
						muteImage.setImageResource(R.drawable.mute);
					}
				}
				else {
					muteState = false;
					myMediaPlayer.setVolume(1, 1);
					myMediaPlayer.start();
					if (testIcon) {
						muteImage.setImageResource(R.drawable.volume);
					}
				}
			}
		});

		// left
		final ImageView leftImage = (ImageView)findViewById(R.id.imagePageLeftImage);
		if (testIcon) {
			leftImage.setImageResource(R.drawable.left);
			leftImage.setAlpha(0x88);
		}
		leftImage.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				if (myCurrentPage > 0) {
					myCurrentPage--;
					drawMyCurrentImagePage();
				}
				else {
					finish();
				}
			}
		});

		
		// manual
		final ImageView manualImage = (ImageView)findViewById(R.id.imagePageManualImage);
		if (testIcon) {
			manualImage.setImageResource(R.drawable.play);
			manualImage.setAlpha(0x88);
		}
		manualImage.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				if (autoPage == true) {
					autoPage = false;
					if (testIcon) {
						manualImage.setImageResource(R.drawable.pause);
					}
				}
				else {
					autoPage = true;
					if (testIcon) {
						manualImage.setImageResource(R.drawable.play);
					}
				}
			}
		});

		// right
		final ImageView rightImage = (ImageView)findViewById(R.id.imagePageRightImage);
		if (testIcon) {
			rightImage.setImageResource(R.drawable.right);
			rightImage.setAlpha(0x88);
		}
		rightImage.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				if (myCurrentPage < totalPageNum) {
					myCurrentPage++;
					drawMyCurrentImagePage();
				}
				else {
					Toast.makeText(MyBookImagePage.this, "���̻� �������� ����ϴ�.", Toast.LENGTH_SHORT).show();
				}
			}
		});
		

		
		
	}
    class MyGestureDetector extends SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
 
            DisplayMetrics dm = getResources().getDisplayMetrics();
            REL_SWIPE_MIN_DISTANCE = (int)(SWIPE_MIN_DISTANCE * dm.densityDpi / 160.0f);
            REL_SWIPE_MAX_OFF_PATH = (int)(SWIPE_MAX_OFF_PATH * dm.densityDpi / 160.0f);
            REL_SWIPE_THRESHOLD_VELOCITY = (int)(SWIPE_THRESHOLD_VELOCITY * dm.densityDpi / 160.0f);    
            
            
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                // right to left swipe
                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//                    Toast.makeText(MyBookImagePage.this, "Left Swipe", Toast.LENGTH_SHORT).show();
                    if (myCurrentPage < totalPageNum) {
                        myCurrentPage++;
                        drawMyCurrentImagePage();
                    }
                    else {
                        Toast.makeText(MyBookImagePage.this, "This Page is Last !!", Toast.LENGTH_SHORT).show();
                    }                   
                    
                }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//                    Toast.makeText(MyBookImagePage.this, "Right Swipe", Toast.LENGTH_SHORT).show();
                    if (myCurrentPage > 0) {
                        myCurrentPage--;
                        drawMyCurrentImagePage();
                    }
                    else {
                        finish();
                    }
                }
            } catch (Exception e) {
                // nothing
            }
            return false;
        }
    }
    
	@Override
	protected void onResume() {
		super.onResume();
		drawMyCurrentImagePage();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if(mySoundPoolMap != null) {
			// ��� ȿ���� ����.
			for(int i=1; i<=mySoundPoolMap.size(); i++) {
				mySoundPool.stop(mySoundPoolMap.get(i));
			}
		}
		myMediaPlayer.stop(); // ��׶��� ���� ����.
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
	    
        if (gestureDetector.onTouchEvent(event)) {

            return true;
        } else {
 
          switch (event.getAction()) {
          case MotionEvent.ACTION_DOWN:
              float pos_x = event.getX();
              float pos_y = event.getY();
              for (int soundID=1; soundID<=totalEffectNum; soundID++) {
                  Rect rect = myPreferences.getPageEffectPosition(myCurrentPage, soundID);
                  if (pos_x >= rect.left && pos_x <= rect.right && pos_y >= rect.top && pos_y <= rect.bottom) {
                      mySoundPool.play(soundID, 1f, 1f, 1, 0, 1f);
                      return true;
                  }
              }
                initialX = event.getRawX();  
                initialY = event.getRawY();  
              break;
        }
    
            return false;
        }
        
//		switch (event.getAction()) {
//		case MotionEvent.ACTION_DOWN:
//	        float pos_x = event.getX();
//	        float pos_y = event.getY();
//	        for (int soundID=1; soundID<=totalEffectNum; soundID++) {
//				Rect rect = myPreferences.getPageEffectPosition(myCurrentPage, soundID);
//				if (pos_x >= rect.left && pos_x <= rect.right && pos_y >= rect.top && pos_y <= rect.bottom) {
//					mySoundPool.play(soundID, 1f, 1f, 1, 0, 1f);
//					return true;
//				}
//			}
//            initialX = event.getRawX();  
//            initialY = event.getRawY();  
//			break;
//		}
//		return super.onTouchEvent(event);
	}

	private void drawMyCurrentImagePage() {
		// ��׶��� �̹���
		String imagePath = myPreferences.getPageBgImage(myCurrentPage);
		drawMyBGImage(imagePath);

		// ��׶��� ����
		String soundPath = myPreferences.getPageBgSound(myCurrentPage);
		playMyBGSound(soundPath);

		// �ؽ�Ʈ
		String textPath = myPreferences.getPageBgTextPath(myCurrentPage);
		if (textPath != null) {
			Rect rect = myPreferences.getPageBgTextPosition(myCurrentPage);
			drawMyBGText(textPath, rect);
		}
		else {
			TextView textView = (TextView)findViewById(R.id.imagePageBGText);
			textView.setText(null);
		}

		// ȿ���� ���� �� �ε�
		loadMyEffect();

		// page ����ǥ��
		if(myCurrentPage > 0) {
			TextView pageNumText = (TextView)findViewById(R.id.imagePageNumText);
			String pageNum = String.format("%d/%d page", myCurrentPage, totalPageNum);
			pageNumText.setText(pageNum);
		}
	}

	private void drawMyBGImage(String imagePath) {
		if (imagePath == null) {
			return;
		}
		try {
			InputStream imageIS = getAssets().open(imagePath);

			// ��Ʈ���� �̹����信 �׸���
			Bitmap myBitmap = BitmapFactory.decodeStream(imageIS);
			ImageView myImageView = (ImageView)findViewById(R.id.imagePageBGImage);
			myImageView.setImageBitmap(myBitmap);

			imageIS.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void playMyBGSound(String soundPath) {
		myMediaPlayer.reset();
		if (soundPath == null) {
			return;
		}
		try {
			AssetFileDescriptor soundAFD = getAssets().openFd(soundPath);

			myMediaPlayer.setDataSource(soundAFD.getFileDescriptor(), soundAFD.getStartOffset(), soundAFD.getLength());
			myMediaPlayer.prepare();
			myMediaPlayer.start();

			myMediaPlayer.setOnCompletionListener(new OnCompletionListener(){
				public void onCompletion(MediaPlayer arg0) {
					if(autoPage == true) {
						if (myCurrentPage < totalPageNum) {
							myCurrentPage++;
							drawMyCurrentImagePage();
						}
						else {
							Toast.makeText(MyBookImagePage.this, "���̻� �������� ����ϴ�.", Toast.LENGTH_SHORT).show();
						}
					}
				}
			});

			soundAFD.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void drawMyBGText(String textPath, Rect rect) {
		if (textPath == null) {
			return;
		}
		try {
			InputStream textIS = getAssets().open(textPath);
			InputStreamReader textISR = new InputStreamReader(textIS);
			BufferedReader textBR = new BufferedReader(textISR);
			StringBuffer textSB = new StringBuffer("");

			String strLine = null;
			while ((strLine = textBR.readLine()) != null) {
				textSB.append(strLine+"\n");
			}

			TextView textView = (TextView)findViewById(R.id.imagePageBGText);
			textView.setPadding(rect.left, rect.top, myMetrics.widthPixels - rect.right, myMetrics.heightPixels - rect.bottom);
			textView.setTextSize(10);
			textView.setTextColor(Color.RED);
			textView.setText(textSB);

			textISR.close();
			textBR.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void loadMyEffect() {
		totalEffectNum = myPreferences.getPageEffectNum(myCurrentPage);
		if (mySoundPoolMap == null) {
			mySoundPoolMap = new HashMap<Integer, Integer>();
		}
		else {
			// ��� ȿ���� ����.
			for(int i=1; i<=mySoundPoolMap.size(); i++) {
				mySoundPool.stop(mySoundPoolMap.get(i));
			}
			mySoundPoolMap.clear();
		}

		if (mySoundPool == null) {
			mySoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		}
		else {
			mySoundPool.release();
			mySoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		}

		for (int effect=1; effect<=totalEffectNum; effect++) {
			String effectPath = myPreferences.getPageEffectPath(myCurrentPage, effect);
			if (effectPath != null) {
				try {
					AssetFileDescriptor soundAFD = getAssets().openFd(effectPath);
					mySoundPoolMap.put(effect, mySoundPool.load(soundAFD, 1));
					soundAFD.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        
    }
	
}
