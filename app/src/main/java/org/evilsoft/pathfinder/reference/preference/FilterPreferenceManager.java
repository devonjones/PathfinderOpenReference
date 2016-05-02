package org.evilsoft.pathfinder.reference.preference;

import java.util.ArrayList;
import java.util.List;

import org.evilsoft.pathfinder.reference.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class FilterPreferenceManager {

	public static String getSourceFilter(Context context, List<String> args,
			String conjunction) {
		return getSourceFilter(context, args, conjunction, null);
	}

	public static String getSourceFilter(Context context, List<String> args,
			String conjunction, String tableName) {
		StringBuffer filter = new StringBuffer();
		ArrayList<String> sourceList = new ArrayList<String>();

		// The default values must be set here to bypass a bug in Android
		// See
		// http://stackoverflow.com/questions/3907830/android-checkboxpreference-default-value
		PreferenceManager.setDefaultValues(context, R.xml.source_filter, false);
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);

		if (!preferences.getBoolean("source_APG", true)) {
			sourceList.add("Advanced Player's Guide");
		}
		if (!preferences.getBoolean("source_ACG", true)) {
			sourceList.add("Advanced Class Guide");
		}
		if (!preferences.getBoolean("source_ARG", true)) {
			sourceList.add("Advanced Race Guide");
		}
		if (!preferences.getBoolean("source_UCA", true)) {
			sourceList.add("Ultimate Campaign");
		}
		if (!preferences.getBoolean("source_UC", true)) {
			sourceList.add("Ultimate Combat");
		}
		if (!preferences.getBoolean("source_UE", true)) {
			sourceList.add("Ultimate Equipment");
		}
		if (!preferences.getBoolean("source_UM", true)) {
			sourceList.add("Ultimate Magic");
		}
		if (!preferences.getBoolean("source_MA", true)) {
			sourceList.add("Mythic Adventures");
		}
		if (!preferences.getBoolean("source_OA", true)) {
			sourceList.add("Occult Adventures");
		}
		if (!preferences.getBoolean("source_TG", true)) {
			sourceList.add("Technology Guide");
		}
		if (!preferences.getBoolean("source_B1", true)) {
			sourceList.add("Bestiary");
		}
		if (!preferences.getBoolean("source_B2", true)) {
			sourceList.add("Bestiary 2");
		}
		if (!preferences.getBoolean("source_B3", true)) {
			sourceList.add("Bestiary 3");
		}
		if (!preferences.getBoolean("source_B4", true)) {
			sourceList.add("Bestiary 4");
		}
		if (!preferences.getBoolean("source_GMG", true)) {
			sourceList.add("Game Mastery Guide");
		}
		if (!preferences.getBoolean("source_PFU", true)) {
			sourceList.add("Pathfinder Unchained");
		}
		if (!preferences.getBoolean("source_NPC", true)) {
			sourceList.add("NPC Codex");
		}
		if (!preferences.getBoolean("source_MC", true)) {
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
