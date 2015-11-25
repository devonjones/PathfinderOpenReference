package org.evilsoft.pathfinder.reference.api.contracts;

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

public final class FeatContract {
	/** The authority for the contacts provider */
	public static final String AUTHORITY = "org.evilsoft.pathfinder.reference.api.feat";

	/** A content:// style uri to the authority for this table */
	public static final Uri FEAT_LIST_URI = Uri.parse("content://" + AUTHORITY
			+ "/feats");

	/**
	 * The MIME type of {@link #CONTENT_URI} providing a directory of status
	 * messages.
	 */
	public static final String FEAT_LIST_CONTENT_TYPE = "vnd.android.cursor.dir/org.evilsoft.pathfinder.reference.api.feat.list";

	public static final Uri getFeatHtmlUri(String featId) {
		return Uri.parse("content://" + AUTHORITY + "/feats/" + featId
				+ ".html");
	}

	public static final Uri getFeatJsonUri(String featId) {
		return Uri.parse("content://" + AUTHORITY + "/feats/" + featId
				+ ".json");
	}

	private FeatContract() {
	}

	public final class FeatListColumns implements BaseColumns {
		private FeatListColumns() {
		}

		/**
		 * Source book name
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String SOURCE = "source";

		/**
		 * Section type 'feat'
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String TYPE = "type";

		/**
		 * Section subtype
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String SUBTYPE = "subtype";

		/**
		 * Class name
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String NAME = "name";

		/**
		 * class short description, probably null
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String DESCRIPTION = "description";

		/**
		 * PathfinderOpenReference unique pfsrd:// url for class
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String CONTENT_URL = "content_url";

		/**
		 * Feat types
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String FEAT_TYPES = "feat_types";

		/**
		 * Feat prerequisites
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String PREREQUISITES = "prerequisites";

	}
}
