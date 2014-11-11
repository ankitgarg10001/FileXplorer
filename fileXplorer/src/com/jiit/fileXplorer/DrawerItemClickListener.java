package com.jiit.fileXplorer;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class DrawerItemClickListener extends Activity implements
		ListView.OnItemClickListener {

	Context context;
	Intent intent;
	private String[] mDrawerelements;

	public DrawerItemClickListener(Context c, String[] l) {
		context = c;
		mDrawerelements = l;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Toast.makeText(
				context,
				"selected " + mDrawerelements[position] + " at position "
						+ position, Toast.LENGTH_SHORT).show();
		if (position == 1) {
			intent = new Intent(context, Category.class);
			Category.msqPattern = new ArrayList<String>();
			Category.msqPattern.add(".png");
			Category.msqPattern.add(".jpg");

		} else if (position == 2) {
			intent = new Intent(context, Category.class);
			Category.msqPattern = new ArrayList<String>();
			Category.msqPattern.add(".mp3");
			Category.msqPattern.add(".wma");
		} else if (position == 3) {
			intent = new Intent(context, Category.class);
			Category.msqPattern = new ArrayList<String>();
			Category.msqPattern.add(".mp4");
			Category.msqPattern.add(".avi");
			Category.msqPattern.add(".mpg");
			Category.msqPattern.add(".mpeg");
			Category.msqPattern.add(".mkv");
		} else {
			intent = new Intent(context, FileChooser.class);
		}
		Category.Title = mDrawerelements[position];
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);

	}
}
