package org.evilsoft.pathfinder.reference;

import java.io.IOException;

import org.evilsoft.pathfinder.reference.db.psrd.PsrdDbHelper;
import org.evilsoft.pathfinder.reference.db.user.PsrdUserDbAdapter;
import org.evilsoft.pathfinder.reference.utils.AvailableSpaceHandler;
import org.evilsoft.pathfinder.reference.utils.LimitedSpaceException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public class StartActivity extends Activity {
	private PsrdUserDbAdapter userDbAdapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PsrdDbHelper dbh = new PsrdDbHelper(this.getApplicationContext());
		userDbAdapter = new PsrdUserDbAdapter(this.getApplicationContext());
		userDbAdapter.open();
		boolean cont = true;
		try {
			dbh.createDataBase(userDbAdapter);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (LimitedSpaceException e) {
			cont = false;
			StringBuffer sb = new StringBuffer();
			sb.append("Error creating database.  This app requires at least ");
			sb.append(e.getSize()/AvailableSpaceHandler.SIZE_MB + 1);
			sb.append(" megs free in order to store articles.  Exiting.");
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(sb.toString())
				.setCancelable(false)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						StartActivity.this.finish();
					}
				});
			AlertDialog alert = builder.create();
			alert.show();
		}
		if(cont) {
			Intent showContent = new Intent(this.getApplicationContext(), PathfinderOpenReferenceActivity.class);
			startActivity(showContent);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (userDbAdapter != null) {
			userDbAdapter.close();
		}
	}
}
