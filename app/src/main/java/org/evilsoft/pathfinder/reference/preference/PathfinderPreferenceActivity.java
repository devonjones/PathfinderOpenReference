/***
Copyright (c) 2008-2012 CommonsWare, LLC
Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain	a copy
of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
by applicable law or agreed to in writing, software distributed under the
License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
OF ANY KIND, either express or implied. See the License for the specific
language governing permissions and limitations under the License.
	
From _The Busy Coder's Guide to Android Development_
  http://commonsware.com/Android
 */

package org.evilsoft.pathfinder.reference.preference;

import java.util.List;

import org.evilsoft.pathfinder.reference.R;

import android.os.Build;
import android.os.Bundle;
import android.widget.CheckBox;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

public class PathfinderPreferenceActivity extends SherlockPreferenceActivity {

	CheckBox prefCheckBox;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// If the device is pre-Honeycomb, just add each of the sets of
		// preferences to the single preference page in turn
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			addPreferencesFromResource(R.xml.source_filter);
		}

	}

	@Override
	public void onBuildHeaders(List<Header> target) {
		// If the device is Honeycomb or higher, this method will be called,
		// which will load the preference headers, which will in turn load the
		// preferences
		loadHeadersFromResource(R.xml.preference_headers, target);
	}
}
