package com.jiit.fileXplorer;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.jiit.fileXplorer.R;

public class FileChooser extends Activity {

	private File currentDir;
	private FileArrayAdapter adapter;
	private String basedir;
	ListView listview;
	Context context;
	SharedPreferences mPrefs;
	SharedPreferences.Editor mEditor;

	private ListView mDrawerList;
	private String[] mDrawerelements;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		basedir = Environment.getExternalStorageDirectory().getName()
				.toString();
		context = getApplicationContext();
		mPrefs = context.getSharedPreferences("loc", Context.MODE_PRIVATE);
		// Get a SharedPreferences editor
		mEditor = mPrefs.edit();

		listview = (ListView) findViewById(R.id.listview);

		mDrawerelements = getResources()
				.getStringArray(R.array.mDrawerelements);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		mDrawerList.setAdapter(new ArrayAdapter<String>(context,
				R.layout.drawer_list_item, mDrawerelements));
		// Set the list's click listener
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener(context,mDrawerelements));

	}

	@Override
	protected void onPause() {
		super.onPause();
		saveloc();
	}

	void saveloc() {
		mEditor.putString("loc", currentDir.getPath().toString());
		mEditor.commit();
	}

	void initloc() {

		if (mPrefs.contains("loc")) {
			currentDir = new File(mPrefs.getString("loc", Environment
					.getExternalStorageDirectory().getName().toString()));

			// Otherwise, turn off location updates
		} else {
			mEditor.putString("loc", Environment.getExternalStorageDirectory()
					.getName().toString());
			mEditor.commit();
			currentDir = new File(Environment.getExternalStorageDirectory()
					.toString());
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		initloc();
		fill(currentDir);
	}

	@Override
	protected void onStart() {
		super.onStart();

	}

	private void fill(File f) {
		currentDir = f;
		File[] dirs = f.listFiles();
		this.setTitle("Current Dir: " + f.getName());
		List<Item> dir = new ArrayList<Item>();
		List<Item> fls = new ArrayList<Item>();
		try {
			for (File ff : dirs) {
				DateFormat formater = DateFormat.getDateTimeInstance();
				String date_modify = formater.format(ff.lastModified());
				if (ff.isDirectory()) {

					int buf = ff.listFiles().length;
					// if (fbuf != null) {
					// buf = fbuf.length;
					// } else
					// buf = 0;
					String num_item = String.valueOf(buf);
					if (buf < 2)
						num_item = num_item + " item";
					else
						num_item = num_item + " items";

					// String formated = lastModDate.toString();
					dir.add(new Item(ff.getName(), num_item, date_modify, ff
							.getAbsolutePath(), "directory_icon"));
				} else {
					long siz = ff.length();
					String s;
					if (siz > 1074000000) {
						s = String.format("%.1f", siz / (float) 1074000000)
								+ " GB";
					} else if (siz > 1049000) {
						s = String.format("%.1f", siz / (float) 1049000)
								+ " MB";
					} else if (siz > 1024) {
						s = String.format("%.1f", siz / (float) 1024) + " KB";
					} else {
						s = siz + " Bytes";
					}

					fls.add(new Item(ff.getName(), s, date_modify, ff
							.getAbsolutePath(), "file_icon"));
				}
			}
		} catch (Exception e) {

		}
		Collections.sort(dir);
		Collections.sort(fls);
		dir.addAll(fls);
		if (!f.getName().equalsIgnoreCase(basedir))
			dir.add(0,
					new Item("Go up ..", "Parent Directory", "", f.getParent(),
							"directory_up"));
		adapter = new FileArrayAdapter(context, R.layout.file_view, dir);
		listview.setAdapter(adapter);
		// this.setListAdapter(adapter);
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) {
				Item o = (Item) parent.getItemAtPosition(position);
				if (o.getImage().equalsIgnoreCase("directory_icon")
						|| o.getImage().equalsIgnoreCase("directory_up")) {
					fill(new File(o.getPath()));
				} else {
					onFileClick(o);
				}
			}

		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Log.d("CDA", "onKeyDown Called:back key");
			if (!currentDir.getName().toString().equalsIgnoreCase(basedir)) {
				Log.d("dir", currentDir.getName().toString() + "   :   "
						+ basedir);
				fill(new File(adapter.getItem(0).getPath()));
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public static String getMimeType(String url) {
		String extension = url.substring(url.lastIndexOf(".")).toLowerCase();
		String mimeTypeMap = MimeTypeMap.getFileExtensionFromUrl(extension);
		String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
				mimeTypeMap);
		return mimeType;
	}

	@SuppressLint("NewApi")
	private void onFileClick(Item o) {

		String filetype = "Invalid";
		try {
			filetype = getMimeType(o.getPath());
			Log.d("filetype", "Type: " + filetype);
		} catch (Exception e) {

		}

		try {

			Intent intent = new Intent();
			intent.setAction(android.content.Intent.ACTION_VIEW);
			File file = new File(o.getPath().toString());
			intent.setDataAndType(Uri.fromFile(file), filetype);

			startActivity(intent);

			// if (filetype.substring(0,
			// filetype.indexOf("/")).equalsIgnoreCase(
			// "audio")) {
			// if (android.os.Build.VERSION.SDK_INT >= 15) {
			// intent = Intent.makeMainSelectorActivity(
			// Intent.ACTION_MAIN, Intent.CATEGORY_APP_MUSIC);
			// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// File file = new File(o.getPath().toString());
			// intent.setDataAndType(Uri.fromFile(file), "audio/*");
			//
			// startActivity(intent);
			// } else {
			// intent = new Intent("android.intent.action.MUSIC_PLAYER");
			// intent.setAction(android.content.Intent.ACTION_VIEW);
			// File file = new File(o.getPath().toString());
			// intent.setDataAndType(Uri.fromFile(file), "audio/*");
			//
			// startActivity(intent);
			// }
			// } else {
			//
			//
			// intent = new Intent();
			// intent.setAction(Intent.ACTION_VIEW);
			// File file = new File(o.getPath().toString());
			// intent.setDataAndType(Uri.fromFile(file), filetype);
			// startActivity(intent);
			// }

		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(),
					"Sorry No application available", Toast.LENGTH_SHORT)
					.show();
		}
	}

}
