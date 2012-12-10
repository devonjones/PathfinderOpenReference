package org.evilsoft.pathfinder.reference.db.book;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

public class BookDbAdapter {
	private static final String TAG = "BookDbAdapter";
	private Context context;
	public SQLiteDatabase database;
	private BookDbHelper dbHelper;
	private String dbName;
	private boolean closed = true;

	public BookDbAdapter(Context context, String dbName) {
		this.context = context;
		this.dbName = dbName;
	}

	public BookDbAdapter open() throws SQLException {
		dbHelper = new BookDbHelper(context, dbName);
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

	public ArrayList<HashMap<String, String>> getPathByUrl(String url) {
		if(url.indexOf("?") > -1) {
			url = TextUtils.split(url, "\\?")[0];
		}
		Cursor cursor = getSectionAdapter().fetchSectionByUrl(url);
		try {
			boolean has_rows = cursor.moveToFirst();
			if (has_rows) {
				Integer sectionId = SectionAdapter.SectionUtils.getSectionId(cursor);
				return getPath(sectionId);
			}
		} finally {
			cursor.close();
		}
		return null;
	}

	public ArrayList<HashMap<String, String>> getPath(Integer sectionId) {
		Cursor cursor = getSectionAdapter().fetchSectionBySectionId(sectionId);
		try {
			ArrayList<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();
			Log.d(TAG, sectionId.toString());
			boolean has_rows = cursor.moveToFirst();
			Log.d(TAG, ((Boolean) has_rows).toString());
			if (has_rows) {
				Integer parentId = SectionAdapter.SectionUtils.getParentId(cursor);
				HashMap<String, String> element = new HashMap<String, String>();
				element.put("id", SectionAdapter.SectionUtils.getSectionId(cursor).toString());
				element.put("name", SectionAdapter.SectionUtils.getName(cursor));
				element.put("url", SectionAdapter.SectionUtils.getUrl(cursor));
				element.put("type", SectionAdapter.SectionUtils.getType(cursor));
				path.add(element);
				while (parentId != 0) {
					Cursor cursor2 = getSectionAdapter().fetchSectionBySectionId(parentId);
					try {
						cursor2.moveToFirst();
						parentId = SectionAdapter.SectionUtils.getParentId(cursor2);
						element = new HashMap<String, String>();
						element.put("id", SectionAdapter.SectionUtils.getSectionId(cursor2).toString());
						element.put("name", SectionAdapter.SectionUtils.getName(cursor2));
						element.put("url", SectionAdapter.SectionUtils.getUrl(cursor2));
						element.put("type", SectionAdapter.SectionUtils.getType(cursor2));
						path.add(element);
					} finally {
						cursor2.close();
					}
				}
			}
			return path;
		} finally {
			cursor.close();
		}
	}

	public AbilityAdapter getAbilityAdapter() {
		return new AbilityAdapter(database, dbName);
	}

	public AfflictionAdapter getAfflictionAdapter() {
		return new AfflictionAdapter(database, dbName);
	}

	public AnimalCompanionAdapter getAnimalCompanionAdapter() {
		return new AnimalCompanionAdapter(database, dbName);
	}

	public ClassAdapter getClassAdapter() {
		return new ClassAdapter(database, dbName);
	}

	public CreatureAdapter getCreatureAdapter() {
		return new CreatureAdapter(database, dbName);
	}

	public FeatAdapter getFeatAdapter() {
		return new FeatAdapter(database, dbName);
	}

	public HauntAdapter getHauntAdapter() {
		return new HauntAdapter(database, dbName);
	}

	public ItemAdapter getItemAdapter() {
		return new ItemAdapter(database, dbName);
	}

	public LinkAdapter getLinkAdapter() {
		return new LinkAdapter(database, dbName);
	}

	public SectionAdapter getSectionAdapter() {
		return new SectionAdapter(database, dbName);
	}

	public FullSectionAdapter getFullSectionAdapter() {
		return new FullSectionAdapter(database, dbName);
	}

	public SectionIndexGroupAdapter getSectionIndexGroupAdapter() {
		return new SectionIndexGroupAdapter(database, dbName);
	}

	public SettlementAdapter getSettlementAdapter() {
		return new SettlementAdapter(database, dbName);
	}

	public SkillAdapter getSkillAdapter() {
		return new SkillAdapter(database, dbName);
	}

	public SpellComponentAdapter getSpellComponentAdapter() {
		return new SpellComponentAdapter(database, dbName);
	}

	public SpellDetailAdapter getSpellDetailAdapter() {
		return new SpellDetailAdapter(database, dbName);
	}

	public SpellEffectAdapter getSpellEffectAdapter() {
		return new SpellEffectAdapter(database, dbName);
	}

	public TrapAdapter getTrapAdapter() {
		return new TrapAdapter(database, dbName);
	}

	public VehicleAdapter getVehicleAdapter() {
		return new VehicleAdapter(database, dbName);
	}
}
