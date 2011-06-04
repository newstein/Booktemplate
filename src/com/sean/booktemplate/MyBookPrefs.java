package com.sean.booktemplate;

import java.io.IOException;
import java.net.MalformedURLException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Rect;

public class MyBookPrefs {

	// �о���� xml����
	public static final String xmlFileName = "bookdata.xml";

	// xml tag��
	private static final String TAG_BOOKDATA = "bookdata";
	private static final String TAG_TITLE = "title";
	private static final String TAG_INTRO = "intro";
	private static final String TAG_PAGE = "page";
	private static final String TAG_BGIMAGE = "bgimage";
	private static final String TAG_BGSOUND = "bgsound";
	private static final String TAG_BGTEXT = "bgtext";
	private static final String TAG_EFFECT = "effect";
	private static final String TAG_LEFT = "left";
	private static final String TAG_TOP = "top";
	private static final String TAG_RIGHT = "right";
	private static final String TAG_BOTTOM = "bottom";
	private static final String TAG_PATH = "path";

	private static boolean b_bookdata = false;
	private static boolean b_title = false;
	private static boolean b_intro = false;
	private static boolean b_page = false;
	private static boolean b_bgimage = false;
	private static boolean b_bgsound = false;
	private static boolean b_bgtext = false;
	private static boolean b_effect = false;
	private static boolean b_left = false;
	private static boolean b_top = false;
	private static boolean b_right = false;
	private static boolean b_bottom = false;
	private static boolean b_path = false;
	private static int page_num = 0;
	private static int effect_num = 0;

	// Preference ����
	private static SharedPreferences myPrefs;

	private static final String MY_PREFS_NAME = "mybookdata";
	private static final String KEY_PAGE = "%s";
	private static final String KEY_PAGE_DATA = "%s_%d_%s";
	private static final String KEY_PAGE_DATA_SUB = "%s_%d_%s_%d_%s";

	public MyBookPrefs(Context context, AssetManager assetManager) {

		myPrefs = context.getSharedPreferences(MY_PREFS_NAME, 0);
		SharedPreferences.Editor myEditor = myPrefs.edit();

		// XmlPullParser�� �̿��ؼ� xml���� ����Ÿ ��������.
		try {
			XmlPullParserFactory myParserFactory = XmlPullParserFactory.newInstance();
			myParserFactory.setNamespaceAware(true);
			XmlPullParser myParser = myParserFactory.newPullParser();

			myParser.setInput(assetManager.open(xmlFileName), null);
			int eventType = myParser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.END_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					if (myParser.getName().compareTo(TAG_BOOKDATA) == 0) {
						b_bookdata = true;
					}
					else if (myParser.getName().compareTo(TAG_TITLE) == 0) {
						b_title = true;
					}
					else if (myParser.getName().compareTo(TAG_INTRO) == 0) {
						b_intro = true;
					}
					else if (myParser.getName().compareTo(TAG_PAGE) == 0) {
						b_page = true;
						page_num++;
					}
					else if (myParser.getName().compareTo(TAG_BGIMAGE) == 0) {
						b_bgimage = true;
					}
					else if (myParser.getName().compareTo(TAG_BGSOUND) == 0) {
						b_bgsound = true;
					}
					else if (myParser.getName().compareTo(TAG_BGTEXT) == 0) {
						b_bgtext = true;
					}
					else if (myParser.getName().compareTo(TAG_EFFECT) == 0) {
						b_effect = true;
						effect_num++;
					}
					else if (myParser.getName().compareTo(TAG_LEFT) == 0) {
						b_left = true;
					}
					else if (myParser.getName().compareTo(TAG_TOP) == 0) {
						b_top = true;
					}
					else if (myParser.getName().compareTo(TAG_RIGHT) == 0) {
						b_right = true;
					}
					else if (myParser.getName().compareTo(TAG_BOTTOM) == 0) {
						b_bottom = true;
					}
					else if (myParser.getName().compareTo(TAG_PATH) == 0) {
						b_path = true;
					}
					break;

				case XmlPullParser.END_TAG:
					if (myParser.getName().compareTo(TAG_BOOKDATA) == 0) {
						if (b_bookdata == true) {
							b_bookdata = false;
						}
						page_num = 0;
					}
					else if (myParser.getName().compareTo(TAG_TITLE) == 0) {
						b_title = false;
					}
					else if (myParser.getName().compareTo(TAG_INTRO) == 0) {
						b_intro = false;
					}
					else if (myParser.getName().compareTo(TAG_PAGE) == 0) {
						b_page = false;
						effect_num = 0;
						String myKey = String.format(KEY_PAGE, TAG_PAGE);
						myEditor.putInt(myKey, page_num);
					}
					else if (myParser.getName().compareTo(TAG_BGIMAGE) == 0) {
						b_bgimage = false;
					}
					else if (myParser.getName().compareTo(TAG_BGSOUND) == 0) {
						b_bgsound = false;
					}
					else if (myParser.getName().compareTo(TAG_BGTEXT) == 0) {
						b_bgtext = false;
					}
					else if (myParser.getName().compareTo(TAG_EFFECT) == 0) {
						b_effect = false;
						String myKey = String.format(KEY_PAGE_DATA, TAG_PAGE, page_num, TAG_EFFECT);
						myEditor.putInt(myKey, effect_num);
					}
					else if (myParser.getName().compareTo(TAG_LEFT) == 0) {
						b_left = false;
					}
					else if (myParser.getName().compareTo(TAG_TOP) == 0) {
						b_top = false;
					}
					else if (myParser.getName().compareTo(TAG_RIGHT) == 0) {
						b_right = false;
					}
					else if (myParser.getName().compareTo(TAG_BOTTOM) == 0) {
						b_bottom = false;
					}
					else if (myParser.getName().compareTo(TAG_PATH) == 0) {
						b_path = false;
					}
					break;

				case XmlPullParser.TEXT:
					String myKey = null;
					String valueStr = myParser.getText();
					String pageStr = null;
					String dataStr = null;
					String subStr = null;
					if (b_title) {
						pageStr = TAG_TITLE;
						if (b_bgimage) {
							dataStr = TAG_BGIMAGE;
							myKey = String.format(KEY_PAGE_DATA, pageStr, 1, dataStr);
						}
						else if (b_bgsound) {
							dataStr = TAG_BGSOUND;
							myKey = String.format(KEY_PAGE_DATA, pageStr, 1, dataStr);
						}
						else {
							break;
						}
						myEditor.putString(myKey, valueStr);
					}
					else if (b_intro) {
						pageStr = TAG_INTRO;
						if (b_bgimage) {
							dataStr = TAG_BGIMAGE;
							myKey = String.format(KEY_PAGE_DATA, pageStr, 1, dataStr);
						}
						else if (b_bgsound) {
							dataStr = TAG_BGSOUND;
							myKey = String.format(KEY_PAGE_DATA, pageStr, 1, dataStr);
						}
						else {
							break;
						}
						myEditor.putString(myKey, valueStr);
					}
					else if (b_page) {
						pageStr = TAG_PAGE;
						if (b_bgimage) {
							dataStr = TAG_BGIMAGE;
							myKey = String.format(KEY_PAGE_DATA, pageStr, page_num, dataStr);
						}
						else if (b_bgsound) {
							dataStr = TAG_BGSOUND;
							myKey = String.format(KEY_PAGE_DATA, pageStr, page_num, dataStr);
						}
						else if (b_bgtext) {
							dataStr = TAG_BGTEXT;
							if (b_left) subStr = TAG_LEFT;
							else if (b_top) subStr = TAG_TOP;
							else if (b_right) subStr = TAG_RIGHT;
							else if (b_bottom) subStr = TAG_BOTTOM;
							else if (b_path) subStr = TAG_PATH;
							else break;
							myKey = String.format(KEY_PAGE_DATA_SUB, pageStr, page_num, dataStr, 1, subStr);
						}
						else if (b_effect) {
							dataStr = TAG_EFFECT;
							if (b_left) subStr = TAG_LEFT;
							else if (b_top) subStr = TAG_TOP;
							else if (b_right) subStr = TAG_RIGHT;
							else if (b_bottom) subStr = TAG_BOTTOM;
							else if (b_path) subStr = TAG_PATH;
							else break;
							myKey = String.format(KEY_PAGE_DATA_SUB, pageStr, page_num, dataStr, effect_num, subStr);
						}
						else {
							break;
						}
						myEditor.putString(myKey, valueStr);
					}
					else {
						break;
					}
					break;
				}
				eventType = myParser.next();
			}
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		myEditor.commit();
	}

	public String getTitleBgImage() {
		String myKey = String.format(KEY_PAGE_DATA, TAG_TITLE, 1, TAG_BGIMAGE);
		return myPrefs.getString(myKey, null);
	}

	public String getTitleBgSound() {
		String myKey = String.format(KEY_PAGE_DATA, TAG_TITLE, 1, TAG_BGSOUND);
		return myPrefs.getString(myKey, null);
	}

	public String getIntroBgImage() {
		String myKey = String.format(KEY_PAGE_DATA, TAG_INTRO, 1, TAG_BGIMAGE);
		return myPrefs.getString(myKey, null);
	}

	public String getIntroBgSound() {
		String myKey = String.format(KEY_PAGE_DATA, TAG_INTRO, 1, TAG_BGSOUND);
		return myPrefs.getString(myKey, null);
	}

	public int getPageNum() {
		String myKey = String.format(KEY_PAGE, TAG_PAGE);
		return myPrefs.getInt(myKey, 0);
	}

	public String getPageBgImage(int page) {
		if(page == 0) {
			String myKey = String.format(KEY_PAGE_DATA, TAG_INTRO, 1, TAG_BGIMAGE);
			return myPrefs.getString(myKey, null);
		}
		else {
			String myKey = String.format(KEY_PAGE_DATA, TAG_PAGE, page, TAG_BGIMAGE);
			return myPrefs.getString(myKey, null);
		}
	}

	public String getPageBgSound(int page) {
		if(page == 0) {
			String myKey = String.format(KEY_PAGE_DATA, TAG_INTRO, 1, TAG_BGSOUND);
			return myPrefs.getString(myKey, null);
		}
		else {
			String myKey = String.format(KEY_PAGE_DATA, TAG_PAGE, page, TAG_BGSOUND);
			return myPrefs.getString(myKey, null);
		}
	}

	public Rect getPageBgTextPosition(int page) {
		String myKeyLeft = String.format(KEY_PAGE_DATA_SUB, TAG_PAGE, page, TAG_BGTEXT, 1, TAG_LEFT);
		String myKeyTop = String.format(KEY_PAGE_DATA_SUB, TAG_PAGE, page, TAG_BGTEXT, 1, TAG_TOP);
		String myKeyRight = String.format(KEY_PAGE_DATA_SUB, TAG_PAGE, page, TAG_BGTEXT, 1, TAG_RIGHT);
		String myKeyBottom = String.format(KEY_PAGE_DATA_SUB, TAG_PAGE, page, TAG_BGTEXT, 1, TAG_BOTTOM);

		Rect rect = new Rect();
		rect.left = Integer.parseInt(myPrefs.getString(myKeyLeft, null));
		rect.top = Integer.parseInt(myPrefs.getString(myKeyTop, null));
		rect.right = Integer.parseInt(myPrefs.getString(myKeyRight, null));
		rect.bottom = Integer.parseInt(myPrefs.getString(myKeyBottom, null));
		return rect;
	}

	public String getPageBgTextPath(int page) {
		String myKey = String.format(KEY_PAGE_DATA_SUB, TAG_PAGE, page, TAG_BGTEXT, 1, TAG_PATH); 
		return myPrefs.getString(myKey, null);
	}

	public int getPageEffectNum(int page) {
		String myKey = String.format(KEY_PAGE_DATA, TAG_PAGE, page, TAG_EFFECT);
		return myPrefs.getInt(myKey, 0);
	}

	public Rect getPageEffectPosition(int page, int effect) {
		String myKeyLeft = String.format(KEY_PAGE_DATA_SUB, TAG_PAGE, page, TAG_EFFECT, effect, TAG_LEFT);
		String myKeyTop = String.format(KEY_PAGE_DATA_SUB, TAG_PAGE, page, TAG_EFFECT, effect, TAG_TOP);
		String myKeyRight = String.format(KEY_PAGE_DATA_SUB, TAG_PAGE, page, TAG_EFFECT, effect, TAG_RIGHT);
		String myKeyBottom = String.format(KEY_PAGE_DATA_SUB, TAG_PAGE, page, TAG_EFFECT, effect, TAG_BOTTOM);

		Rect rect = new Rect();
		rect.left = Integer.parseInt(myPrefs.getString(myKeyLeft, null));
		rect.top = Integer.parseInt(myPrefs.getString(myKeyTop, null));
		rect.right = Integer.parseInt(myPrefs.getString(myKeyRight, null));
		rect.bottom = Integer.parseInt(myPrefs.getString(myKeyBottom, null));
		return rect;
	}

	public String getPageEffectPath(int page, int effect) {
		String myKey = String.format(KEY_PAGE_DATA_SUB, TAG_PAGE, page, TAG_EFFECT, effect, TAG_PATH); 
		return myPrefs.getString(myKey, null);
	}

}
