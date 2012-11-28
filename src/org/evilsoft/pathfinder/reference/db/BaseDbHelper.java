package org.evilsoft.pathfinder.reference.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.acra.ErrorReporter;
import org.evilsoft.pathfinder.reference.utils.AvailableSpaceHandler;
import org.evilsoft.pathfinder.reference.utils.LimitedSpaceException;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

public abstract class BaseDbHelper extends SQLiteOpenHelper {
	private final String DB_FILENAME;
	private final String DB_PATH;
	private final String dbName;
	protected SQLiteDatabase db;
	protected final Context context;

	public BaseDbHelper(Context context, String DB_NAME) {
		super(context, DB_NAME, null, 1);
		this.context = context;
		this.dbName = DB_NAME;

		File db = manageDatabase();

		this.DB_PATH = db.getParent();
		this.DB_FILENAME = db.getAbsolutePath();
	}

	public void createDatabase(boolean isCurrent)
			throws IOException, LimitedSpaceException {
		boolean dbExists = checkDatabase();
		if (dbExists) {
			if (!isCurrent) {
				buildDatabase();
				this.openDatabase();
				try {
					onCreate();
				} finally {
					this.close();
				}
			}
		} else {
			buildDatabase();
		}
	}

	/* Can be overridden to trigger other events after creation.*/
	protected void onCreate() {
	}

	private void buildDatabase() throws IOException, LimitedSpaceException {
		buildDatabase(true);
	}

	private void buildDatabase(boolean retry) throws IOException,
			LimitedSpaceException {
		// By calling this method and empty database will be created into
		// the default system path of your application so we are going to
		// be able to overwrite that database with our database.
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

	private void checkDatabaseSize() throws IOException, LimitedSpaceException {
		long size = 0;
		InputStream sizeInput = context.getAssets().open(dbName);

		byte[] ibuffer = new byte[1024];
		int length;
		while ((length = sizeInput.read(ibuffer)) > 0) {
			size += length;
		}
		sizeInput.close();

		long buffer = (long) (size * 0.1);
		long free = AvailableSpaceHandler.getAvailableSpaceInBytes(DB_PATH);
		if (size + buffer > free) {
			throw new LimitedSpaceException(
					"Not enough free space.", size + buffer);
		}
	}

	private long calcDatabaseSize() throws IOException {
		long size = 0;
		InputStream sizeInput = context.getAssets().open(dbName);

		byte[] ibuffer = new byte[1024];
		int length;
		while ((length = sizeInput.read(ibuffer)) > 0) {
			size += length;
		}
		sizeInput.close();

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

		InputStream in = context.getAssets().open(dbName);
		byte[] buffer = new byte[1024];
		int length;
		while ((length = in.read(buffer)) > 0) {
			out.write(buffer, 0, length);
		}
		in.close();
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
	 * Determines an appropriate location for the database.
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

		// see if external storage is mounted
		try {
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				tmpFile = new File(context.getExternalFilesDir(null), dbName);
				if (tmpFile != null) {
					if (tmpFile.exists()) {
						// db already exists
						retFile = tmpFile;
					} else {
						// see if there is enough space to hold the db on
						// external storage
						free = AvailableSpaceHandler
								.getAvailableSpaceInBytes(tmpFile.getParent());

						try {
							dbSize = calcDatabaseSize();
						} catch (IOException e) {
							// unable to calculate database size
							// proper exception handling for this will take
							// place when the db is created
							dbSize = 0;
						}

						if (dbSize < free) {
							retFile = tmpFile;
						}
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
		tmpFile = new File(context.getFilesDir().getAbsolutePath(), dbName);
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

	public abstract boolean testDb(SQLiteDatabase database);

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

	public static String[] toStringArray(List<String> input) {
		String[] retarr = new String[input.size()];
		for (int i = 0; i < input.size(); i++) {
			retarr[i] = input.get(i);
		}
		return retarr;
	}
}
