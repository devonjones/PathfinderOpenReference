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
		if (closed) {
			dbHelper = new BookDbHelper(context, dbName);
			database = dbHelper.openDatabase();
			closed = false;
		}
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
		if (url.indexOf("?") > -1) {
			url = TextUtils.split(url, "\\?")[0];
		}
		Cursor cursor = getSectionAdapter().fetchSectionByUrl(url);
		try {
			boolean has_rows = cursor.moveToFirst();
			if (has_rows) {
				Integer sectionId = SectionAdapter.SectionUtils
						.getSectionId(cursor);
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
				Integer parentId = SectionAdapter.SectionUtils
						.getParentId(cursor);
				HashMap<String, String> element = new HashMap<String, String>();
				element.put("id",
						SectionAdapter.SectionUtils.getSectionId(cursor)
								.toString());
				element.put("name", SectionAdapter.SectionUtils.getName(cursor));
				element.put("url", SectionAdapter.SectionUtils.getUrl(cursor));
				element.put("type", SectionAdapter.SectionUtils.getType(cursor));
				path.add(element);
				while (parentId != 0) {
					Cursor cursor2 = getSectionAdapter()
							.fetchSectionBySectionId(parentId);
					try {
						cursor2.moveToFirst();
						parentId = SectionAdapter.SectionUtils
								.getParentId(cursor2);
						element = new HashMap<String, String>();
						element.put("id", SectionAdapter.SectionUtils
								.getSectionId(cursor2).toString());
						element.put("name",
								SectionAdapter.SectionUtils.getName(cursor2));
						element.put("url",
								SectionAdapter.SectionUtils.getUrl(cursor2));
						element.put("type",
								SectionAdapter.SectionUtils.getType(cursor2));
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
		open();
		return new AbilityAdapter(database, dbName);
	}

	public AfflictionAdapter getAfflictionAdapter() {
		open();
		return new AfflictionAdapter(database, dbName);
	}

	public AnimalCompanionAdapter getAnimalCompanionAdapter() {
		open();
		return new AnimalCompanionAdapter(database, dbName);
	}

	public ArmyAdapter getArmyAdapter() {
		open();
		return new ArmyAdapter(database, dbName);
	}

	public ClassAdapter getClassAdapter() {
		open();
		return new ClassAdapter(database, dbName);
	}

	public CreatureAdapter getCreatureAdapter() {
		open();
		return new CreatureAdapter(database, dbName);
	}

	public FeatAdapter getFeatAdapter() {
		open();
		return new FeatAdapter(database, dbName);
	}

	public HauntAdapter getHauntAdapter() {
		open();
		return new HauntAdapter(database, dbName);
	}

	public ItemAdapter getItemAdapter() {
		open();
		return new ItemAdapter(database, dbName);
	}

	public KingdomResourceAdapter getKingdomResourceAdapter() {
		open();
		return new KingdomResourceAdapter(database, dbName);
	}

	public LinkAdapter getLinkAdapter() {
		open();
		return new LinkAdapter(database, dbName);
	}

	public MythicSpellDetailAdapter getMythicSpellDetailAdapter() {
		open();
		return new MythicSpellDetailAdapter(database, dbName);
	}

	public ResourceAdapter getResourceAdapter() {
		open();
		return new ResourceAdapter(database, dbName);
	}

	public SectionAdapter getSectionAdapter() {
		open();
		return new SectionAdapter(database, dbName);
	}

	public FullSectionAdapter getFullSectionAdapter() {
		open();
		return new FullSectionAdapter(database, dbName);
	}

	public SectionIndexGroupAdapter getSectionIndexGroupAdapter() {
		open();
		return new SectionIndexGroupAdapter(database, dbName);
	}

	public SettlementAdapter getSettlementAdapter() {
		open();
		return new SettlementAdapter(database, dbName);
	}

	public SkillAdapter getSkillAdapter() {
		open();
		return new SkillAdapter(database, dbName);
	}

	public SpellComponentAdapter getSpellComponentAdapter() {
		open();
		return new SpellComponentAdapter(database, dbName);
	}

	public SpellDetailAdapter getSpellDetailAdapter() {
		open();
		return new SpellDetailAdapter(database, dbName);
	}

	public SpellDescriptorAdapter getSpellDescriptorAdapter() {
		open();
		return new SpellDescriptorAdapter(database, dbName);
	}

	public SpellEffectAdapter getSpellEffectAdapter() {
		open();
		return new SpellEffectAdapter(database, dbName);
	}

	public SpellListAdapter getSpellListAdapter() {
		open();
		return new SpellListAdapter(database, dbName);
	}

	public SpellSubschoolAdapter getSpellSubschoolAdapter() {
		open();
		return new SpellSubschoolAdapter(database, dbName);
	}

	public TalentAdapter getTalentAdapter() {
		open();
		return new TalentAdapter(database, dbName);
	}

	public TrapAdapter getTrapAdapter() {
		open();
		return new TrapAdapter(database, dbName);
	}

	public VehicleAdapter getVehicleAdapter() {
		open();
		return new VehicleAdapter(database, dbName);
	}
}
