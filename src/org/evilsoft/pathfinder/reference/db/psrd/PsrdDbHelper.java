package org.evilsoft.pathfinder.reference.db.psrd;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.evilsoft.pathfinder.reference.db.user.PsrdUserDbAdapter;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class PsrdDbHelper extends SQLiteOpenHelper {
	private static String DB_PATH = "/data/data/org.evilsoft.pathfinder.reference/databases/";
	private static String DB_NAME = "psrd.db";
	private static final Integer VERSION = 55555;
	private SQLiteDatabase db;
	private final Context context;

	public PsrdDbHelper(Context context) {
		super(context, DB_NAME, null, 1);
		this.context = context;
	}

	public void createDataBase(PsrdUserDbAdapter userDbAdapter) throws IOException {
		boolean dbExists = checkDatabase();
		if (dbExists) {
			Integer currVersion = userDbAdapter.getPsrdDbVersion();
			if(VERSION > currVersion) {
				buildDatabase();
				userDbAdapter.updatePsrdDbVersion(VERSION);
			}
			else {
				// do nothing - database already exists and is of the right version
			}
		} else {
			buildDatabase();
			userDbAdapter.updatePsrdDbVersion(VERSION);
		}
	}
	
	private void buildDatabase() {
		// By calling this method and empty database will be created into
		// the default system path
		// of your application so we are going to be able to overwrite that
		// database with our database.
		this.getReadableDatabase();
		try {
			copyDatabase();
		} catch (IOException e) {
			throw new Error("Error copying database");
		} finally {
			this.close();
		}
	}

	private boolean checkDatabase() {
		SQLiteDatabase checkDb = null;
		try {
			String myPath = DB_PATH + DB_NAME;
			checkDb = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
		} catch (SQLiteException e) {
			// database does't exist yet.
		}
		if (checkDb != null) {
			checkDb.close();
		}
		return checkDb != null ? true : false;
	}

	/**
	 * Copies your database from your local assets-folder to the just created
	 * empty database in the system folder, from where it can be accessed and
	 * handled. This is done by transferring bytestream.
	 * */
	private void copyDatabase() throws IOException {
		// Open your local db as the input stream
		InputStream myInput = context.getAssets().open(DB_NAME);

		// Path to the just created empty db
		String outFileName = DB_PATH + DB_NAME;

		// Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);

		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}

		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	public SQLiteDatabase openDatabase() throws SQLException {
		// Open the database
		String myPath = DB_PATH + DB_NAME;
		db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
		return db;
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
