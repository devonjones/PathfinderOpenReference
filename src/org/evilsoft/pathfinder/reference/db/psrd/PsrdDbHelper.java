package org.evilsoft.pathfinder.reference.db.psrd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.acra.ErrorReporter;
import org.evilsoft.pathfinder.reference.db.user.PsrdUserDbAdapter;
import org.evilsoft.pathfinder.reference.utils.AvailableSpaceHandler;
import org.evilsoft.pathfinder.reference.utils.LimitedSpaceException;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

public class PsrdDbHelper extends SQLiteOpenHelper {
	private static String OLD_DB_PATH = "/data/data/org.evilsoft.pathfinder.reference/databases/";

	private final String DB_FILENAME;
	private final String DB_PATH;
	private static String DB_NAME = "psrd.db";
	private static int DB_CHUNKS = 21;
	private static final Integer VERSION = 55581;
	private SQLiteDatabase db;
	private final Context context;

	public PsrdDbHelper(Context context) {
		super(context, DB_NAME, null, 1);
		this.context = context;

		File db = manageDatabase();

		this.DB_PATH = db.getParent();
		this.DB_FILENAME = db.getAbsolutePath();
	}

	public void createDatabase(PsrdUserDbAdapter userDbAdapter)
			throws IOException, LimitedSpaceException {
		boolean dbExists = checkDatabase();
		if (dbExists) {
			Integer currVersion = userDbAdapter.getPsrdDbVersion();
			if (VERSION > currVersion) {
				buildDatabase();
				userDbAdapter.updatePsrdDbVersion(VERSION);
			}
		} else {
			buildDatabase();
			userDbAdapter.updatePsrdDbVersion(VERSION);
		}
	}

	private void buildDatabase() throws IOException, LimitedSpaceException {
		buildDatabase(true);
	}

	private void buildDatabase(boolean retry) throws IOException,
			LimitedSpaceException {
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
		if (retry) {
			boolean dbExists = checkDatabase();
			if (dbExists == false) {
				buildDatabase(false);
			}
		}
	}

	private boolean checkDatabase() {
		SQLiteDatabase checkDb = null;
		try {
			String myPath = DB_FILENAME;
			checkDb = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.NO_LOCALIZED_COLLATORS);
			testDb(checkDb);
		} catch (Exception e) {
			// database does't exist yet, or is broken.
			checkDb = null;
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
			InputStream sizeInput = context.getAssets().open(
					getFileName(DB_NAME, chunk));

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
			throw new LimitedSpaceException(
					"Not enough free space.", size + buffer);
		}
	}

	private long calcDatabaseSize() throws IOException {
		long size = 0;
		for (int chunk = 0; chunk < DB_CHUNKS; chunk++) {
			InputStream sizeInput = context.getAssets().open(
					getFileName(DB_NAME, chunk));

			byte[] ibuffer = new byte[1024];
			int length;
			while ((length = sizeInput.read(ibuffer)) > 0) {
				size += length;
			}
			sizeInput.close();
		}

		return size + (long) (size * 0.1);
	}

	/**
	 * Copies your database from your local assets-folder to the just created
	 * empty database in the system folder, from where it can be accessed and
	 * handled. This is done by transferring bytestream.
	 * 
	 * @throws LimitedSpaceException
	 * @throws IOException
	 * */
	private void copyDatabase() throws LimitedSpaceException, IOException {
		checkDatabaseSize();

		String outFileName = DB_FILENAME;
		OutputStream out = new FileOutputStream(outFileName);

		for (int chunk = 0; chunk < DB_CHUNKS; chunk++) {
			InputStream in = context.getAssets().open(
					getFileName(DB_NAME, chunk));
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
		db = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.NO_LOCALIZED_COLLATORS);
		return db;
	}

	/**
	 * Determines an appropriate location for the reference database.
	 * 
	 * 1. Is external storage available (mounted) 2. if so, does the db already
	 * exist? 3. if not, is there space for the database here? 4. failing the
	 * availability of external storage, sort out the location for internal
	 * storage 5. if the database is destined for external storage, but exists
	 * internally, delete internal copy
	 * 
	 * @return file pointing to where the database is destined to exist
	 */
	private File manageDatabase() {
		File tmpFile, retFile = null;
		long dbSize, free;

		// Look for old databases that may still exist, delete them in found
		tmpFile = new File(OLD_DB_PATH, DB_NAME);
		if (tmpFile.exists()) {
			tmpFile.delete();
		}

		// see if external storage is mounted
		try {
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				tmpFile = new File(context.getExternalFilesDir(null)
						.getAbsolutePath(), DB_NAME);
				if (tmpFile.exists()) {
					// db already exists
					retFile = tmpFile;
				} else {
					// see if there is enough space to hold the db on external
					// storage
					free = AvailableSpaceHandler
							.getAvailableSpaceInBytes(tmpFile
									.getParent());

					try {
						dbSize = calcDatabaseSize();
					} catch (IOException e) {
						// unable to calculate database size
						// proper exception handling for this will take place
						// when
						// the db is created
						dbSize = 0;
					}

					if (dbSize < free) {
						retFile = tmpFile;
					}
				}
			}
		} catch (Exception e) {
			// Trying SD broke, move to using internal
			ErrorReporter.getInstance().putCustomData(
					"Situation", "Failed to write to SD");
			ErrorReporter.getInstance().handleException(e);
		}

		// check internal storage
		tmpFile = new File(context.getFilesDir().getAbsolutePath(), DB_NAME);
		// see if the db exists on internal storage
		if (tmpFile.exists()) {
			if (null != retFile) {
				// if the db is on internal *and* external storage, delete the
				// internal storage copy
				tmpFile.delete();
			}
		}

		// if we got this far with retFile undefined, there either isn't
		// external storage
		// or insufficient space there. default to internal storage
		if (null == retFile) {
			retFile = tmpFile;
		}

		return retFile;
	}

	public void testDb(SQLiteDatabase database) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT count(*)");
		sb.append(" FROM section_index");
		String sql = sb.toString();
		Cursor curs = database.rawQuery(sql, new String[0]);
		try {
			curs.moveToFirst();
			curs.getInt(0);
		} finally {
			curs.close();
		}
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
