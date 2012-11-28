package org.evilsoft.pathfinder.reference.preference;

import java.util.ArrayList;

import org.evilsoft.pathfinder.reference.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class FilterPreferenceManager {
	
	static Context context;

	public static String getSourceFilter(String conjunction) {
		String filter = "";
		ArrayList<String> sourceList = new ArrayList<String>();
		
		// The default values must be set here to bypass a bug in Android
		// See http://stackoverflow.com/questions/3907830/android-checkboxpreference-default-value
		PreferenceManager.setDefaultValues(context, R.xml.source_filter, false);
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		
		if(preferences.getBoolean("source_core", true))
			sourceList.add("Core Rulebook");
		
		if(preferences.getBoolean("source_GMG", true))
			sourceList.add("Game Mastery Guide");
		
		if(preferences.getBoolean("source_APG", true))
			sourceList.add("Advanced Player''s Guide");
		
		if(preferences.getBoolean("source_ARG", true))
			sourceList.add("Advanced Race Guide");

		if(preferences.getBoolean("source_UC", true))
			sourceList.add("Ultimate Combat");
		
		if(preferences.getBoolean("source_UM", true))
			sourceList.add("Ultimate Magic");
	
		if(preferences.getBoolean("source_B1", true))
			sourceList.add("Bestiary");
		
		if(preferences.getBoolean("source_B2", true))
			sourceList.add("Bestiary 2");
		
		if(preferences.getBoolean("source_B3", true))
			sourceList.add("Bestiary 3");
		
		if(preferences.getBoolean("source_OGL", true))
			sourceList.add("OGL");
		
		if(preferences.getBoolean("source_PFSRD", true))
			sourceList.add("PFSRD");
		
		if(sourceList.size() > 0) {
			// Create the start of the WHERE/AND clause
			filter = " "+ conjunction + " source in (";
			
			for(int numFilters = 0; numFilters < sourceList.size() - 1; numFilters++) {
				// For all but the last filter, add that filter followed by a comma
				filter += "'" + sourceList.get(numFilters) + "',";
			}
			
			// Add the final filter without a comma
			filter += "'" + sourceList.get(sourceList.size() - 1) + "'";
			
			// End the WHERE/AND clause
			filter += ")";
		}
		
		return filter;
	}
	
	public static void setContext(Context setContext) {
		context = setContext;
	}
	
}
