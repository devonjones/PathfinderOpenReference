package org.evilsoft.pathfinder.reference.preference;

import java.util.ArrayList;
import java.util.List;

import org.evilsoft.pathfinder.reference.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class FilterPreferenceManager {

	private FilterPreferenceManager() {
	}

	public static String getSourceFilter(Context context, List<String> args,
					String conjunction) {
		return getSourceFilter(context, args, conjunction, null);
	}

	public static String getSourceFilter(Context context, List<String> args,
			String conjunction, String tableName) {
		StringBuilder filter = new StringBuilder();
		ArrayList<String> sourceList = new ArrayList<String>();

		// The default values must be set here to bypass a bug in Android
		// See
		// http://stackoverflow.com/questions/3907830/android-checkboxpreference-default-value
		PreferenceManager.setDefaultValues(context, R.xml.source_filter, false);
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);

		if (preferences.getBoolean("source_APG", true) == false) {
			sourceList.add("Advanced Player's Guide");
		}
		if (preferences.getBoolean("source_ACG", true) == false) {
			sourceList.add("Advanced Class Guide");
		}
		if (preferences.getBoolean("source_ARG", true) == false) {
			sourceList.add("Advanced Race Guide");
		}
		if (preferences.getBoolean("source_UCA", true) == false) {
			sourceList.add("Ultimate Campaign");
		}
		if (preferences.getBoolean("source_UC", true) == false) {
			sourceList.add("Ultimate Combat");
		}
		if (preferences.getBoolean("source_UE", true) == false) {
			sourceList.add("Ultimate Equipment");
		}
		if (preferences.getBoolean("source_UM", true) == false) {
			sourceList.add("Ultimate Magic");
		}
		if (preferences.getBoolean("source_MA", true) == false) {
			sourceList.add("Mythic Adventures");
		}
		if (preferences.getBoolean("source_OA", true) == false) {
			sourceList.add("Occult Adventures");
		}
		if (preferences.getBoolean("source_TG", true) == false) {
			sourceList.add("Technology Guide");
		}
		if (preferences.getBoolean("source_B1", true) == false) {
			sourceList.add("Bestiary");
		}
		if (preferences.getBoolean("source_B2", true) == false) {
			sourceList.add("Bestiary 2");
		}
		if (preferences.getBoolean("source_B3", true) == false) {
			sourceList.add("Bestiary 3");
		}
		if (preferences.getBoolean("source_B4", true) == false) {
			sourceList.add("Bestiary 4");
		}
		if (preferences.getBoolean("source_GMG", true) == false) {
			sourceList.add("Game Mastery Guide");
		}
		if (preferences.getBoolean("source_PFU", true) == false) {
			sourceList.add("Pathfinder Unchained");
		}
		if (preferences.getBoolean("source_NPC", true) == false) {
			sourceList.add("NPC Codex");
		}
		if (preferences.getBoolean("source_MC", true) == false) {
			sourceList.add("Monster Codex");
		}
		if (sourceList.size() > 0) {
			// Create the start of the WHERE/AND clause
			filter.append(" ");
			filter.append(conjunction);
			filter.append("( ");
			if (tableName != null) {
				filter.append(tableName);
				filter.append(".");
			}
			filter.append("source NOT IN (");
			String comma = "";
			for (int numFilters = 0; numFilters < sourceList.size(); numFilters++) {
				filter.append(comma);
				filter.append("?");
				comma = ", ";
			}
			// End the WHERE/AND clause
			filter.append(") OR source IS NULL)");
		}
		args.addAll(sourceList);
		return filter.toString();
	}
}
