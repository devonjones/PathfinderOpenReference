package org.evilsoft.pathfinder.reference.api.contracts;

import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

/*
 *   Copyright 2013 Devon D. Jones <devon.jones@gmail.com>
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *   
 *   Obviously the application Pathfinder Open Reference is licensed under
 *   the GPLv3, and when distributed as a part of that application, the
 *   GPLv3 must be obeyed.  However, when distributed on it's own, or in
 *   another application, it is the opinion of the author that usage of
 *   this file and accessing the ContentProvider it is for via the android
 *   API does not constitute a breach of the GPLv3, regardless of the license
 *   of the application doing so.
 */

public final class CasterContract {
	/** The authority for the contacts provider */
	public static final String AUTHORITY = "org.evilsoft.pathfinder.reference.api.caster";

	/** A content:// style uri to the authority for this table */
	public static final Uri CASTER_LIST_URI = Uri.parse("content://"
			+ AUTHORITY + "/casters");

	/**
	 * The MIME type of {@link #CONTENT_URI} providing a directory of status
	 * messages.
	 */
	public static final String CASTER_LIST_CONTENT_TYPE = "vnd.android.cursor.dir/org.evilsoft.pathfinder.reference.api.caster.list";

	private CasterContract() {
	}

	public final class CasterListColumns implements BaseColumns {
		private CasterListColumns() {
		}

		/**
		 * Class name
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String CLASS = "class";

		/**
		 * Class level
		 * <P>
		 * Type: NUMBER
		 * </P>
		 */
		public static final String LEVEL = "level";

		/**
		 * Class magic type: Arcane or Divine
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String MAGIC_TYPE = "magic_type";
	}

	public static class CasterContractUtils {
		public static Integer getId(Cursor cursor) {
			return cursor.getInt(0);
		}

		public static String getClass(Cursor cursor) {
			return cursor.getString(1);
		}

		public static Integer getLevel(Cursor cursor) {
			return cursor.getInt(2);
		}

		public static String getMagicType(Cursor cursor) {
			return cursor.getString(3);
		}
	}
}
