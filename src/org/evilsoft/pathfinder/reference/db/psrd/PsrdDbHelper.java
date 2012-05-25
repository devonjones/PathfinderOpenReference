package org.evilsoft.pathfinder.reference.db.psrd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.evilsoft.pathfinder.reference.db.user.PsrdUserDbAdapter;
import org.evilsoft.pathfinder.reference.utils.AvailableSpaceHandler;
import org.evilsoft.pathfinder.reference.utils.LimitedSpaceException;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

public class PsrdDbHelper extends SQLiteOpenHelper {
	private final String DB_FILENAME;
	private final String DB_PATH;
	private static String DB_NAME = "psrd.db";
	private static int DB_CHUNKS = 17;
	private static final Integer VERSION = 55561;
	private SQLiteDatabase db;
	private final Context context;

	public PsrdDbHelper(Context context) {
		super(context, DB_NAME, null, 1);
		this.context = context;
		
		manageDatabase();
		
		this.DB_PATH = dbPath();
		this.DB_FILENAME = DB_PATH + File.separator + DB_NAME;
	}

	public void createDatabase(PsrdUserDbAdapter userDbAdapter) throws IOException, LimitedSpaceException {
		boolean dbExists = checkDatabase();
		if (dbExists) {
			Integer currVersion = userDbAdapter.getPsrdDbVersion();
			if(VERSION > currVersion) {
				buildDatabase();
				userDbAdapter.updatePsrdDbVersion(VERSION);
			}
		} else {
			buildDatabase();
			userDbAdapter.updatePsrdDbVersion(VERSION);
		}
	}
	
	private void buildDatabase() throws IOException, LimitedSpaceException {
		// By calling this method and empty database will be created into
		// the default system path
		// of your application so we are going to be able to overwrite that
		// database with our database.
		this.getReadableDatabase();
		try {
			copyDatabase();
		} finally {
			this.close();
		}
	}

	private boolean checkDatabase() {
		SQLiteDatabase checkDb = null;
		try {
			String myPath = DB_FILENAME;
			checkDb = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
		} catch (Exception e) {
			// database does't exist yet.
		}
		if (checkDb != null) {
			checkDb.close();
		}
		return checkDb != null ? true : false;
	}

	private String getFileName(String base, int chunk) {
		StringBuffer sb = new StringBuffer();
		sb.append(base);
		sb.append(".");
		if (chunk < 10) {
			sb.append("0");
		}
		sb.append(chunk);
		return sb.toString();
	}

	private void checkDatabaseSize() throws IOException, LimitedSpaceException {
		long size = 0;
		for (int chunk = 0; chunk < DB_CHUNKS; chunk++) {
			InputStream sizeInput = context.getAssets().open(getFileName(DB_NAME, chunk));
			
			byte[] ibuffer = new byte[1024];
			int length;
			while ((length = sizeInput.read(ibuffer)) > 0) {
				size += length;
			}
			sizeInput.close();
		}

		long buffer = (long) (size * 0.1);
		long free = AvailableSpaceHandler.getAvailableSpaceInBytes(DB_PATH);
		if (size + buffer > free) {
			throw new LimitedSpaceException("Not enough free space.", size + buffer);
		}
	}

	/**
	 * Copies your database from your local assets-folder to the just created
	 * empty database in the system folder, from where it can be accessed and
	 * handled. This is done by transferring bytestream.
	 * @throws LimitedSpaceException 
	 * @throws IOException 
	 * */
	private void copyDatabase() throws LimitedSpaceException, IOException {
		checkDatabaseSize();

		String outFileName = DB_FILENAME;
		OutputStream out = new FileOutputStream(outFileName);

		for (int chunk = 0; chunk < DB_CHUNKS; chunk++) {
			InputStream in = context.getAssets().open(getFileName(DB_NAME, chunk));
			byte[] buffer = new byte[1024];
			int length;
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}
			in.close();
		}
		out.flush();
		out.close();
	}

	public SQLiteDatabase openDatabase() throws SQLException {
		// Open the database
		String myPath = DB_FILENAME;
		db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
		return db;
	}
	
	/**
	 * Looks for the database in local storage as well as something that looks like
	 * an SD card (though it may not really be one).  A best-case option for managing
	 * the database is implemented:
	 * 
	 * 1. See if the SD card is available (mounted)
	 * 2. if so, see if the DB exists on local storage
	 * 3. if so delete the copy in local storage.
	 * 
	 * @return false indicates a problem deleting a file
	 */
	private boolean manageDatabase() {
		File dbFile;
		
		// Step 1, see if there is an SD card mounted
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			// Step 2, see if the DB exists on local storage
			dbFile = new File(context.getFilesDir(), DB_NAME);
			if(dbFile.exists()) {
				// Step 3, delete the copy on local storage
				return dbFile.delete();
			}
		}
		return true;
	}
	
	/**
	 * Returns the path for the database being used.
	 * SD card storage is preferred over internal storage.
	 * 
	 * @return absolute path and filename for the database
	 */
	private String dbPath() {
		String dbPath;
		
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			dbPath = context.getExternalFilesDir(null).getAbsolutePath();
		} else {
			dbPath = context.getFilesDir().getAbsolutePath();
		}
		
		return dbPath;
	}

	@Override
	public synchronized void close() {
		if (db != null) {
			db.close();
		}
		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}
