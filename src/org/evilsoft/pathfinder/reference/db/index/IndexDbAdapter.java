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

	public ApiSectionListAdapter getApiClassListAdapter() {
		return new ApiSectionListAdapter(database, context);
	}

	public ApiClassSpellListAdapter getApiClassSpellListAdapter() {
		return new ApiClassSpellListAdapter(database, context);
	}

	public ApiFilteredClassSpellListAdapter getApiFilteredClassSpellListAdapter() {
		return new ApiFilteredClassSpellListAdapter(database, context);
	}

	public ApiCreatureListAdapter getApiCreatureListAdapter() {
		return new ApiCreatureListAdapter(database, context);
	}

	public ApiFeatListAdapter getApiFeatListAdapter() {
		return new ApiFeatListAdapter(database, context);
	}

	public ApiSkillListAdapter getApiSkillListAdapter() {
		return new ApiSkillListAdapter(database, context);
	}

	public ApiSpellListAdapter getApiSpellListAdapter() {
		return new ApiSpellListAdapter(database, context);
	}

	public ApiFilteredSpellListAdapter getApiFilteredSpellListAdapter() {
		return new ApiFilteredSpellListAdapter(database, context);
	}

	public MenuAdapter getMenuAdapter() {
		return new MenuAdapter(database, context);
	}

	public FeatTypeAdapter getFeatTypeAdapter() {
		return new FeatTypeAdapter(database, context);
	}

	public CreatureTypeAdapter getCreatureTypeAdapter() {
		return new CreatureTypeAdapter(database, context);
	}

	public SpellClassAdapter getSpellClassAdapter() {
		return new SpellClassAdapter(database, context);
	}

	public SpellListAdapter getSpellListAdapter() {
		return new SpellListAdapter(database, context);
	}

	public IndexGroupAdapter getIndexGroupAdapter() {
		return new IndexGroupAdapter(database, context);
	}

	public BooksAdapter getBooksAdapter() {
		return new BooksAdapter(database, context);
	}

	public SearchAdapter getSearchAdapter() {
		return new SearchAdapter(database, context);
	}

	public CountAdapter getCountAdapter() {
		return new CountAdapter(database, context);
	}

	public UrlReferenceAdapter getUrlReferenceAdapter() {
		return new UrlReferenceAdapter(database, context);
	}
}
