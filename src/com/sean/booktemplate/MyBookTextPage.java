package com.sean.booktemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.sean.booktemplate.R;
import com.sean.booktemplate.MyBookImagePage.MyGestureDetector;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MyBookTextPage extends Activity {

	private MyBookPrefs myPreferences = null; // å����Ÿ
	private MediaPlayer myMediaPlayer = null; // ��׶��� �÷��̾�.

	private boolean muteState = false; // �Ҹ�����
	private boolean autoPage = false; // �ڵ����
	private int totalPageNum = 0; // �� ������ ����

	private int currentPage = 0; // ���� ������

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
		setContentView(R.layout.mybook_textpage);

		myPreferences = MyBookMain.myBookPreferences;
		myMediaPlayer = MyBookMain.myBookBGPlayer;

		muteState = false;
		autoPage = true;
		totalPageNum = myPreferences.getPageNum();

		currentPage = 1; // 1 �������� ����.

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
		final ImageView homeImage = (ImageView)findViewById(R.id.textPageHomeImage);
		if (testIcon) {
			homeImage.setImageResource(R.drawable.home);
			homeImage.setAlpha(0x88);
		}
		homeImage.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				finish();
			}
		});

		// mute
		final ImageView muteImage = (ImageView)findViewById(R.id.textPageMuteImage);
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
		final ImageView leftImage = (ImageView)findViewById(R.id.textPageLeftImage);
		if (testIcon) {
			leftImage.setImageResource(R.drawable.left);
			leftImage.setAlpha(0x88);
		}
		leftImage.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				if (currentPage > 1) {
					currentPage--;
					drawMyCurrentTextPage();
				}
				else {
					finish();
				}
			}
		});

		// manual
		final ImageView manualImage = (ImageView)findViewById(R.id.textPageManualImage);
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
		final ImageView rightImage = (ImageView)findViewById(R.id.textPageRightImage);
		if (testIcon) {
			rightImage.setImageResource(R.drawable.right);
			rightImage.setAlpha(0x88);
		}
		rightImage.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				if (currentPage < totalPageNum) {
					currentPage++;
					drawMyCurrentTextPage();
				}
				else {
					Toast.makeText(MyBookTextPage.this, "���̻� �������� ����ϴ�.", Toast.LENGTH_SHORT).show();
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
                    if (currentPage < totalPageNum) {
                        currentPage++;
                        drawMyCurrentTextPage();
                    }
                    else {
                        Toast.makeText(MyBookTextPage.this, "This Page is Last !!", Toast.LENGTH_SHORT).show();
                    }                   
                    
                }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//                    Toast.makeText(MyBookImagePage.this, "Right Swipe", Toast.LENGTH_SHORT).show();
                    if (currentPage > 0) {
                        currentPage--;
                        drawMyCurrentTextPage();
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
		drawMyCurrentTextPage();
	}

	@Override
	protected void onPause() {
		super.onPause();
		myMediaPlayer.stop(); // ��׶��� ���� ����.
	}

	private void drawMyCurrentTextPage() {
		// ��׶��� �̹���
		String imagePath = myPreferences.getPageBgImage(currentPage);
		drawMyBGImage(imagePath);

		// ��׶��� ����
		String soundPath = myPreferences.getPageBgSound(currentPage);
		playMyBGSound(soundPath);

		// �ؽ�Ʈ
		String textPath = myPreferences.getPageBgTextPath(currentPage);
		if (textPath != null) {
			drawMyBGText(textPath);
		}
		else {
			TextView textView = (TextView)findViewById(R.id.textPageBGText);
			textView.setText(null);
		}

		// page ����ǥ��
		if(currentPage > 0) {
			TextView pageNumText = (TextView)findViewById(R.id.textPageNumText);
			String pageNum = String.format("%d/%d page", currentPage, totalPageNum);
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
			ImageView myImageView = (ImageView)findViewById(R.id.textPageBGImage);
			myImageView.setColorFilter(Color.GRAY, PorterDuff.Mode.LIGHTEN);
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
						if (currentPage < totalPageNum) {
							currentPage++;
							drawMyCurrentTextPage();
						}
						else {
							Toast.makeText(MyBookTextPage.this, "���̻� �������� ����ϴ�.", Toast.LENGTH_SHORT).show();
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
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        
        if (gestureDetector.onTouchEvent(event)) {

            return true;
        } else {
 
            return false;
        }
        
    }

	private void drawMyBGText(String textPath) {
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

			TextView textView = (TextView)findViewById(R.id.textPageBGText);
			textView.setGravity(Gravity.CENTER_VERTICAL |Gravity.LEFT);
			textView.setPadding(30, 0, 0, 0);
			textView.setTextSize(20);
			textView.setTextColor(Color.BLACK);
			textView.setText(textSB);

			textISR.close();
			textBR.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
