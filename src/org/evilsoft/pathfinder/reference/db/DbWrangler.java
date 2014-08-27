package org.evilsoft.pathfinder.reference.db;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.evilsoft.pathfinder.reference.db.book.BookDbAdapter;
import org.evilsoft.pathfinder.reference.db.book.BookDbHelper;
import org.evilsoft.pathfinder.reference.db.index.BooksAdapter;
import org.evilsoft.pathfinder.reference.db.index.IndexDbAdapter;
import org.evilsoft.pathfinder.reference.db.index.IndexDbHelper;
import org.evilsoft.pathfinder.reference.db.user.UserDbAdapter;
import org.evilsoft.pathfinder.reference.utils.AvailableSpaceHandler;
import org.evilsoft.pathfinder.reference.utils.LimitedSpaceException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;

public class DbWrangler {
	public static final String CURRENT_VERSION = "CURRENT_VERSION";
	public static final String PREFS_NAME = "psrd.prefs";
	public static final String TAG = "DbWrangler";
	private Context context;
	private static final Integer VERSION = 77;
	private HashMap<String, BookDbAdapter> bookDbAdapters;
	private IndexDbAdapter indexDbAdapter;
	private UserDbAdapter userDbAdapter;
	private boolean closed = true;

	public DbWrangler(Context context) {
		this.context = context;
		bookDbAdapters = new HashMap<String, BookDbAdapter>();
	}

	public void checkDatabases() throws IOException, LimitedSpaceException {
		handleOldDatabases();
		SharedPreferences settings = context
				.getSharedPreferences(PREFS_NAME, 0);
		Integer version = settings.getInt(CURRENT_VERSION, 0);
		boolean current = true;
		if (version < VERSION) {
			current = false;
		}
		checkIndexDb(current);
		checkBookDbs(current);
		if (current == false) {
			SharedPreferences.Editor editor = settings.edit();
			editor.putInt(CURRENT_VERSION, VERSION);
			editor.commit();
		}
	}

	public void checkIndexDb(boolean isCurrent) throws IOException,
			LimitedSpaceException {
		IndexDbHelper helper = new IndexDbHelper(context);
		helper.createDatabase(isCurrent);
		if (!isCurrent) {
			UserDbAdapter udba = this.getUserDbAdapter();
			udba.updateBookmarks(this);
		}
	}

	public void checkBookDbs(boolean isCurrent) throws IOException,
			LimitedSpaceException {
		List<String> files = getDbList();
		for (int i = 0; i < files.size(); i++) {
			BookDbHelper bookHelper = new BookDbHelper(context, files.get(i));
			bookHelper.createDatabase(isCurrent);
		}
	}

	public List<String> getDbList() {
		AssetManager man = context.getAssets();
		String[] filelist = new String[0];
		try {
			filelist = man.list("");
		} catch (IOException ioe) {
			Log.e(TAG, "Cannot read list of db assets.", ioe);
		}
		List<String> fileList = new ArrayList<String>();
		for (int i = 0; i < filelist.length; i++) {
			String filename = filelist[i];
			if (filename.endsWith(".db") && filename.startsWith("book-")) {
				fileList.add(filename);
			}
		}
		return fileList;
	}

	public void open() {
		List<String> files = getDbList();
		for (int i = 0; i < files.size(); i++) {
			openBookDbAdapter(files.get(i));
		}
		openIndexDbAdapter();
		closed = false;
	}

	public boolean isClosed() {
		return closed;
	}

	private void openBookDbAdapter(String db) {
		BookDbAdapter adapter = bookDbAdapters.get(db);
		if (adapter == null) {
			adapter = new BookDbAdapter(context, db);
			adapter.open();
			bookDbAdapters.put(db, adapter);
		}
	}

	public BookDbAdapter getBookDbAdapter(String db) {
		BookDbAdapter adapter = bookDbAdapters.get(db);
		if (adapter == null || isClosed()) {
			openBookDbAdapter(db);
		}
		return adapter;
	}

	public BookDbAdapter getBookDbAdapterByName(String source)
			throws BookNotFoundException {
		source = source.replaceAll("'", "");
		Cursor curs = getIndexDbAdapter().getBooksAdapter().fetchBook(source);
		String bookDb;
		if (source.equals("Open Game License")) {
			bookDb = "book-ogl.db";
		} else {
			try {
				bookDb = BooksAdapter.BookUtils.getBookDb(curs);
				if (bookDb == null) {
					throw new BookNotFoundException(source);
				}
			} finally {
				curs.close();
			}
		}
		return getBookDbAdapter(bookDb);
	}

	public BookDbAdapter getBookDbAdapterByUrl(String url)
			throws BookNotFoundException {
		String[] parts = url.split("\\/");
		return getBookDbAdapterByName(parts[2]);
	}

	private void openIndexDbAdapter() {
		if (indexDbAdapter == null) {
			indexDbAdapter = new IndexDbAdapter(context);
			indexDbAdapter.open();
		}
	}

	public UserDbAdapter getUserDbAdapter() {
		if (userDbAdapter == null || isClosed()) {
			openUserDbAdapter();
		}
		return userDbAdapter;
	}

	private void openUserDbAdapter() {
		if (userDbAdapter == null) {
			userDbAdapter = new UserDbAdapter(context);
			userDbAdapter.open();
		}
	}

	public IndexDbAdapter getIndexDbAdapter() {
		if (indexDbAdapter == null || isClosed()) {
			openIndexDbAdapter();
		}
		return indexDbAdapter;
	}

	public void close() {
		if (indexDbAdapter != null) {
			indexDbAdapter.close();
		}
		if (userDbAdapter != null) {
			userDbAdapter.close();
		}
		Iterator<BookDbAdapter> i = bookDbAdapters.values().iterator();
		BookDbAdapter adapter;
		while (i.hasNext()) {
			adapter = i.next();
			adapter.close();
		}
		closed = true;
	}

	public static void showLowSpaceError(Activity runningActivity,
			LimitedSpaceException e,
			DialogInterface.OnClickListener clickListener) {
		StringBuffer sb = new StringBuffer();
		sb.append("Error creating database.  This app requires at least ");
		sb.append(e.getSize() / AvailableSpaceHandler.SIZE_MB + 1);
		sb.append(" megs free in order to store articles.  Exiting.");
		AlertDialog.Builder builder = new AlertDialog.Builder(runningActivity);
		builder.setMessage(sb.toString()).setCancelable(false)
				.setPositiveButton("Ok", clickListener);
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void handleOldDatabases() {
		String oldDbPath = "/data/data/org.evilsoft.pathfinder.reference/databases/";
		String dbName = "psrd.db";
		File tmpFile = null;

		// Look for old databases that may still exist, delete them in found
		tmpFile = new File(oldDbPath, dbName);
		if (tmpFile.exists()) {
			tmpFile.delete();
		}

		// see if external storage is mounted
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			tmpFile = new File(context.getExternalFilesDir(null), dbName);
			if (tmpFile.exists()) {
				tmpFile.delete();
			}
		}

		// check internal storage
		tmpFile = new File(context.getFilesDir().getAbsolutePath(), dbName);
		if (tmpFile.exists()) {
			tmpFile.delete();
		}
	}
}
