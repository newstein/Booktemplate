package com.sean.booktemplate;

import java.io.IOException;
import java.io.InputStream;

import com.sean.booktemplate.R;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MyBookMain extends Activity {

	// å����Ÿ
	public static MyBookPrefs myBookPreferences = null;

	// ��׶��� �÷��̾�.
	public static MediaPlayer myBookBGPlayer = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mybook_main);

		// å����Ÿ Ŭ���� ��.
		if (myBookPreferences == null) {
			myBookPreferences = new MyBookPrefs(MyBookMain.this, getAssets());
		}

		// ��׶��� �÷��̾� ��.
		if (myBookBGPlayer == null) {
			myBookBGPlayer = new MediaPlayer();
		}

		// ��׶��� �̹���
		String imagePath = myBookPreferences.getTitleBgImage();
		if (imagePath != null) {
			try {
				InputStream imageIS = getAssets().open(imagePath);

				// ��Ʈ���� �̹����信 �׸���
				Bitmap myBitmap = BitmapFactory.decodeStream(imageIS);
				ImageView titleImage = (ImageView)findViewById(R.id.mainImageView);
				titleImage.setImageBitmap(myBitmap);

				imageIS.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// ��׶��� ����
		myBookBGPlayer.reset();
		String soundPath = myBookPreferences.getTitleBgSound();
		if (soundPath != null) {
			try {
				AssetFileDescriptor soundAFD = getAssets().openFd(soundPath);

				myBookBGPlayer.setDataSource(soundAFD.getFileDescriptor(), soundAFD.getStartOffset(), soundAFD.getLength());
				myBookBGPlayer.prepare();
				myBookBGPlayer.start();

				soundAFD.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// E-book ��ư
		Button ebookButton = (Button)findViewById(R.id.ebookBtn);
		ebookButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				//Toast.makeText(MyGasBookMain.this, "E-book ��ư�� Ŭ���Ǿ���", Toast.LENGTH_SHORT).show();
				Intent pageIntent = new Intent(MyBookMain.this, MyBookImagePage.class);
				startActivity(pageIntent);
			}
		});

		// Text ��ư
		Button textButton = (Button)findViewById(R.id.textBtn);
		textButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				//Toast.makeText(MyBookMain.this, "Text ��ư�� Ŭ���Ǿ���", Toast.LENGTH_SHORT).show();
				Intent textIntent = new Intent(MyBookMain.this, MyBookTextPage.class);
				startActivity(textIntent);
			}
		});

		// Bookstore ��ư
		Button bookstoreButton = (Button)findViewById(R.id.bookstoreBtn);
		bookstoreButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(MyBookMain.this, "Bookstore ��ư�� Ŭ���Ǿ���", Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
		myBookBGPlayer.stop(); // ��׶��� ���� ����.
	}

}
