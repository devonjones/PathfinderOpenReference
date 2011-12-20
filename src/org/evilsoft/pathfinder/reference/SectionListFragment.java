package org.evilsoft.pathfinder.reference;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.SimpleExpandableListAdapter;

public class SectionListFragment extends ExpandableListFragment {
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		PathfinderOpenReferenceActivity pora = (PathfinderOpenReferenceActivity) activity;
		SimpleExpandableListAdapter expListAdapter = new SimpleExpandableListAdapter(getActivity()
				.getApplicationContext(), pora.createGroupList(), // groupData
																	// describes
																	// the
																	// first-level
																	// entries
				R.layout.group_row, // Layout for the first-level entries
				new String[] { "sectionName" }, // Key in the groupData maps to
												// display
				new int[] { R.id.groupname }, // Data under "colorName" key goes
												// into this TextView
				pora.createChildList(), // childData describes second-level
										// entries
				R.layout.child_row, // Layout for second-level entries
				new String[] { "specificName" }, // Keys in childData maps to
													// display
				new int[] { R.id.childname } // Data under the keys above go
												// into these TextViews
		);
		setListAdapter(expListAdapter);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getExpandableListView().setCacheColorHint(Color.WHITE);
		getExpandableListView().setGroupIndicator(new ColorDrawable(Color.TRANSPARENT));
		getExpandableListView().setOnChildClickListener((PathfinderOpenReferenceActivity) getActivity());
		getExpandableListView().setOnGroupClickListener((PathfinderOpenReferenceActivity) getActivity());
	}
}
