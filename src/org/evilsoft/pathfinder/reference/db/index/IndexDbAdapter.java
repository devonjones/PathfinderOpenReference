package org.evilsoft.pathfinder.reference.db.index;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class IndexDbAdapter {
	private Context context;
	public SQLiteDatabase database;
	private IndexDbHelper dbHelper;
	private boolean closed = true;

	public IndexDbAdapter(Context context) {
		this.context = context;
	}

	public IndexDbAdapter open() throws SQLException {
		dbHelper = new IndexDbHelper(context);
		database = dbHelper.openDatabase();
		closed = false;
		return this;
	}

	public void close() {
		dbHelper.close();
		dbHelper = null;
		database = null;
		closed = true;
	}

	public boolean isClosed() {
		return closed;
	}
	
	public MenuAdapter getMenuAdapter() {
		return new MenuAdapter(database);
	}

	public FeatTypeAdapter getFeatTypeAdapter() {
		return new FeatTypeAdapter(database);
	}

	public CreatureTypeAdapter getCreatureTypeAdapter() {
		return new CreatureTypeAdapter(database);
	}

	public SpellClassAdapter getSpellClassAdapter() {
		return new SpellClassAdapter(database);
	}
	
	public IndexGroupAdapter getIndexGroupAdapter() {
		return new IndexGroupAdapter(database);
	}
	
	public BooksAdapter getBooksAdapter() {
		return new BooksAdapter(database);
	}
	
	public SearchAdapter getSearchAdapter() {
		return new SearchAdapter(database);
	}

	public CountAdapter getCountAdapter() {
		return new CountAdapter(database);
	}
}
