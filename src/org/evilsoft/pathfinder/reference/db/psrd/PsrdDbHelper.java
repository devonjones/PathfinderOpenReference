package org.evilsoft.pathfinder.reference.db.psrd;

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
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class PsrdDbHelper extends SQLiteOpenHelper {
	private static String DB_PATH = "/data/data/org.evilsoft.pathfinder.reference/databases/";
	private static String DB_NAME = "psrd.db";
	private static final Integer VERSION = 55561;
	private SQLiteDatabase db;
	private final Context context;

	public PsrdDbHelper(Context context) {
		super(context, DB_NAME, null, 1);
		this.context = context;
	}

	public void createDataBase(PsrdUserDbAdapter userDbAdapter) throws IOException, LimitedSpaceException {
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

	private void checkDatabaseSize() throws IOException, LimitedSpaceException {
		InputStream sizeInput = context.getAssets().open(DB_NAME);
		long size = 0;
		
		byte[] ibuffer = new byte[1024];
		int length;
		while ((length = sizeInput.read(ibuffer)) > 0) {
			size += length;
		}
		sizeInput.close();

		long buffer = (long) (size * 0.1);
		long free = AvailableSpaceHandler.getExternalAvailableSpaceInBytes();
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
