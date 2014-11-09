package com.jiit.fileXplorer;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Category extends Activity {

	private ListView mDrawerList;
	private String[] mDrawerelements;
	private String basedir;

	ListView listview;
	Context context;
	public static String Title;
	public static List<String> msqPattern;
	private ArrayAdapter<Item> adapter;

	ProgressDialog dialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		basedir = Environment.getExternalStorageDirectory().getName()
				.toString();

		context = getApplicationContext();
		listview = (ListView) findViewById(R.id.listview);
		basedir = Environment.getExternalStorageDirectory().getName()
				.toString();

		fls = new ArrayList<Item>();
		mDrawerelements = getResources()
				.getStringArray(R.array.mDrawerelements);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		mDrawerList.setAdapter(new ArrayAdapter<String>(context,
				R.layout.drawer_list_item, mDrawerelements));
		// Set the list's click listener
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener(context,
				mDrawerelements));
		if (Title.length() < 3) {
			Title = "Custom Category";
		}
		// msqPattern = ".mp3";
		dialog = new ProgressDialog(Category.this);
		dialog.setMessage("Please wait");
	}

	@Override
	protected void onStart() {
		dialog.show();
		this.setTitle(Title);
		try {
			walkdir(new File(basedir));
			Collections.sort(fls);
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onStart();
	}

	@Override
	protected void onResume() {

		super.onResume();

		if (dialog.isShowing()) {
			dialog.dismiss();
		}
		// dialog.show();
		try {
			adapter = new FileArrayAdapter(context, R.layout.file_view, fls);
			listview.setAdapter(adapter);
			// this.setListAdapter(adapter);
			listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, final View view,
						int position, long id) {
					Item o = (Item) parent.getItemAtPosition(position);
					Intent intent = new Intent();
					intent.setAction(android.content.Intent.ACTION_VIEW);
					String url = o.getPath().toString();
					File file = new File(url);
					String extension = url.substring(url.lastIndexOf("."))
							.toLowerCase();
					String mimeTypeMap = MimeTypeMap
							.getFileExtensionFromUrl(extension);
					String mimeType = MimeTypeMap.getSingleton()
							.getMimeTypeFromExtension(mimeTypeMap);
					intent.setDataAndType(Uri.fromFile(file), mimeType);
					startActivity(intent);
				}

			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		// if (dialog.isShowing()) {
		// dialog.dismiss();
		// }

		// AsyncTask<Void, Void, Void> sendtoserver = new AsyncTask<Void, Void,
		// Void>() {
		// ProgressDialog dialog;
		//
		// @Override
		// protected void onPreExecute() {
		// dialog = new ProgressDialog(Category.this);
		// dialog.setMessage("Please wait");
		// dialog.show();
		// super.onPreExecute();
		// }
		//
		// @Override
		// protected Void doInBackground(Void... params1) {
		//
		// try {
		// walkdir(new File(basedir));
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// return null;
		// }
		//
		// @Override
		// protected void onPostExecute(Void result) {
		// if (dialog.isShowing()) {
		// dialog.dismiss();
		// }
		//
		// super.onPostExecute(result);
		// }
		// };
		//
		// sendtoserver.execute(null, null, null);

	}

	File listFile[];

	List<Item> fls;
	String path;

	public void walkdir(File dir) {
		File[] listFile = dir.listFiles();

		try {
			if (listFile != null) {
				for (int i = 0; i < listFile.length; i++) {

					if (listFile[i].isDirectory()) {
						walkdir(listFile[i]);
					} else {
						path = listFile[i].getName().toLowerCase();
						if (msqPattern.contains(path.substring(path
								.lastIndexOf(".")))) {
							DateFormat formater = DateFormat
									.getDateTimeInstance();
							String date_modify = formater.format(listFile[i]
									.lastModified());
							String filepath = listFile[i].getAbsolutePath();
							String duration;
							try {
								MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
								metaRetriever.setDataSource(filepath);
								duration = metaRetriever
										.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
								long dur = Long.parseLong(duration);
								String seconds = String
										.valueOf((dur % 60000) / 1000);

								String minutes = String.valueOf(dur / 60000);
								duration = minutes + ":" + seconds;
							} catch (Exception e) {
								duration = "";
							}
							fls.add(new Item(listFile[i].getName(), duration,
									date_modify, filepath, Title.toLowerCase()));

						}
					}

				}
			}
		} catch (Exception e) {

		}

	}
}
