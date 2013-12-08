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

public final class SectionContract {
	/** The authority for the contacts provider */
	public static final String AUTHORITY = "org.evilsoft.pathfinder.reference.api.section";

	/** A content:// style uri to the authority for this table */
	public static final Uri SECTION_LIST_URI = Uri.parse("content://"
			+ AUTHORITY + "/sections");

	/**
	 * The MIME type of {@link #CONTENT_URI} providing a directory of status
	 * messages.
	 */
	public static final String SECTION_LIST_CONTENT_TYPE = "vnd.android.cursor.dir/org.evilsoft.pathfinder.reference.api.section.list";

	public static final Uri getSectionHtmlUri(String sectionId) {
		return Uri.parse("content://" + AUTHORITY + "/sections/" + sectionId
				+ ".html");
	}

	public static final Uri getSectionJsonUri(String sectionId) {
		return Uri.parse("content://" + AUTHORITY + "/sections/" + sectionId
				+ ".json");
	}

	private SectionContract() {
	}

	public final class SectionListColumns implements BaseColumns {
		private SectionListColumns() {
		}

		/**
		 * Source book name
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String SOURCE = "source";

		/**
		 * Section type
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
		 * Section name
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String NAME = "name";

		/**
		 * section short description, probably null
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String DESCRIPTION = "description";

		/**
		 * PathfinderOpenReference unique pfsrd:// url for section
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String CONTENT_URL = "content_url";
	}
}
