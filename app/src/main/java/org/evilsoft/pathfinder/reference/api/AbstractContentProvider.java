package org.evilsoft.pathfinder.reference.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;
import org.evilsoft.pathfinder.reference.HtmlRenderFarm;
import org.evilsoft.pathfinder.reference.db.BookNotFoundException;
import org.evilsoft.pathfinder.reference.db.DbWrangler;
import org.evilsoft.pathfinder.reference.db.book.BookDbAdapter;
import org.evilsoft.pathfinder.reference.db.index.IndexGroupAdapter;
import org.evilsoft.pathfinder.reference.render.json.SectionRenderer;
import org.evilsoft.pathfinder.reference.utils.LimitedSpaceException;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.os.ParcelFileDescriptor.AutoCloseOutputStream;
import android.util.Log;

public abstract class AbstractContentProvider extends ContentProvider {
	public static final String TAG = "AbstractContentProvider";
	protected DbWrangler dbWrangler;
	protected static final UriMatcher uriMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);

	protected static final HashMap<String, String> MIME_TYPES = new HashMap<String, String>();

	static {
		MIME_TYPES.put(".json", "application/json");
		MIME_TYPES.put(".html", "text/html");
	}

	@Override
	public boolean onCreate() {
		return true;
	}

	public boolean initializeDatabase() {
		if (dbWrangler != null && dbWrangler.isClosed() == false) {
			return true;
		}
		boolean cont = true;
		try {
			DbWrangler dbw = new DbWrangler(getContext());
			dbw.checkDatabases();
		} catch (IOException e) {
			cont = false;
		} catch (LimitedSpaceException e) {
			// ignoring the big warning message that would normally accompany an
			// out of space issue with database creation. This is due to the
			// fact that this code is called from global search and the use case
			// for that error is a bit wonky
			cont = false;
		} finally {
		}
		if (cont) {
			openDb();
		}
		return cont;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		String path = uri.toString();

		for (String extension : MIME_TYPES.keySet()) {
			if (path.endsWith(extension)) {
				return MIME_TYPES.get(extension);
			}
		}

		return null;
	}

	public String renderHtmlByIndexId(String indexId) {
		String html = null;
		StringBuilder htmlparts = new StringBuilder();
		Cursor cursor = dbWrangler.getIndexDbAdapter().getIndexGroupAdapter()
				.fetchById(Integer.valueOf(indexId));
		try {
			boolean hasNext = cursor.moveToFirst();
			if (hasNext) {
				String source = IndexGroupAdapter.IndexGroupUtils
						.getSource(cursor);
				String newUrl = IndexGroupAdapter.IndexGroupUtils
						.getUrl(cursor);
				Integer sectionId = IndexGroupAdapter.IndexGroupUtils
						.getSectionId(cursor);
				BookDbAdapter bookDbAdapter;
				try {
					bookDbAdapter = dbWrangler.getBookDbAdapterByName(source);
					HtmlRenderFarm hrf = new HtmlRenderFarm(dbWrangler,
							bookDbAdapter, false, false);
					htmlparts.append(hrf.render(sectionId.toString(), newUrl,
							false));
				} catch (BookNotFoundException bnfe) {
					Log.e(TAG, "Book not found: " + bnfe.getMessage());
				}
			}
		} finally {
			html = htmlparts.toString();
			if (html.equals("")) {
				html = null;
			}
			cursor.close();
		}
		return html;
	}

	public JSONObject renderJsonByIndexId(String indexId) {
		JSONObject section = null;
		Cursor cursor = dbWrangler.getIndexDbAdapter().getIndexGroupAdapter()
				.fetchById(Integer.valueOf(indexId));
		try {
			boolean hasNext = cursor.moveToFirst();
			if (hasNext) {
				String source = IndexGroupAdapter.IndexGroupUtils
						.getSource(cursor);
				String newUrl = IndexGroupAdapter.IndexGroupUtils
						.getUrl(cursor);
				Integer sectionId = IndexGroupAdapter.IndexGroupUtils
						.getSectionId(cursor);
				BookDbAdapter bookDbAdapter;
				try {
					bookDbAdapter = dbWrangler.getBookDbAdapterByName(source);
					Cursor bookcurs = bookDbAdapter.getSectionAdapter()
							.fetchSectionBySectionId(sectionId);
					try {
						SectionRenderer renderer = new SectionRenderer(
								dbWrangler, bookDbAdapter);
						hasNext = bookcurs.moveToFirst();
						if (hasNext) {
							section = renderer.render(bookcurs);
						}
					} finally {
						bookcurs.close();
					}
				} catch (BookNotFoundException bnfe) {
					Log.e(TAG, "Book not found: " + bnfe.getMessage());
				} catch (JSONException je) {
					// TODO Auto-generated catch block
					Log.e(TAG,
							"Section failed to render as JSON "
									+ je.getMessage());
				}
			}
		} finally {
			cursor.close();
		}
		return section;
	}

	public InputStream getHtml(String id) {
		String html = renderHtmlByIndexId(id);
		return IOUtils.toInputStream(html);
	}

	public InputStream getJson(String id) {
		JSONObject section = renderJsonByIndexId(id);
		return IOUtils.toInputStream(section.toString());
	}

	public abstract String getFileId(Uri uri);

	@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode)
			throws FileNotFoundException {
		ParcelFileDescriptor[] pipe = null;

		try {
			pipe = ParcelFileDescriptor.createPipe();
			if (getType(uri).equals("application/json")) {
				new TransferThread(getJson(getFileId(uri)),
						new AutoCloseOutputStream(pipe[1])).start();
			} else if (getType(uri).equals("text/html")) {
				new TransferThread(getHtml(getFileId(uri)),
						new AutoCloseOutputStream(pipe[1])).start();
			}
		} catch (IOException e) {
			Log.e(getClass().getSimpleName(), "Exception opening pipe", e);
			throw new FileNotFoundException("Could not open pipe for: "
					+ uri.toString());
		}
		return pipe[0];
	}

	@Override
	public String[] getStreamTypes(Uri uri, String mimeTypeFilter) {
		String[] types = null;
		if (uriMatcher.match(stripExtension(uri)) >= 1000) {
			String path = uri.toString();

			for (String extension : MIME_TYPES.keySet()) {
				if (path.endsWith(extension)) {
					types = new String[1];
					types[0] = MIME_TYPES.get(extension);
					return types;
				}
			}
			if (mimeTypeFilter == null || mimeTypeFilter.equals("*/*")) {
				types = new String[2];
				types[0] = "text/html";
				types[1] = "application/json";
			} else if (mimeTypeFilter.equals("text/*")
					|| mimeTypeFilter.equals("text/html")) {
				types = new String[1];
				types[0] = "text/html";
			} else if (mimeTypeFilter.equals("application/*")
					|| mimeTypeFilter.equals("application/json")) {
				types = new String[1];
				types[0] = "application/json";
			}
		}
		return types;
	}

	public String getMimeFromExtension(Uri uri) {
		if (uri.getLastPathSegment().endsWith(".html")) {
			return "text/html";
		} else if (uri.getLastPathSegment().endsWith(".json")) {
			return "application/json";
		}
		return null;
	}

	public Uri stripExtension(Uri uri) {
		if (uri.getLastPathSegment() != null 
			&& (uri.getLastPathSegment().endsWith(".html") 
			|| uri.getLastPathSegment().endsWith(".json"))) {
				String url = uri.toString();
				Uri newUri = Uri.parse(url.substring(0, url.lastIndexOf(".")));
				return newUri;
		}
		return uri;
	}

	private void openDb() {
		if (dbWrangler == null) {
			dbWrangler = new DbWrangler(getContext());
		}
		if (dbWrangler.isClosed()) {
			dbWrangler.open();
		}
	}

	@Override
	public void shutdown() {
		if (dbWrangler != null) {
			dbWrangler.close();
		}
	}

	static class TransferThread extends Thread {
		InputStream in;
		OutputStream out;

		TransferThread(InputStream in, OutputStream out) {
			this.in = in;
			this.out = out;
		}

		@Override
		public void run() {
			byte[] buf = new byte[1024];
			int len;

			try {
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}

				in.close();
				out.flush();
				out.close();
			} catch (IOException e) {
				Log.e(getClass().getSimpleName(),
						"Exception transferring file", e);
			}
		}
	}
}
