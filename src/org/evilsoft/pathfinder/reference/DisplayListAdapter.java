package org.evilsoft.pathfinder.reference;

import android.content.Context;
import android.database.Cursor;
import android.widget.BaseAdapter;

public abstract class DisplayListAdapter extends BaseAdapter {
	protected Cursor c;
	protected Context context;
	protected int currIndex = -1;

	public DisplayListAdapter(Context context, Cursor c) {
		this.c = c;
		this.context = context;
	}

	@Override
	public int getCount() {
		return c.getCount();
	}

	@Override
	public Object getItem(int index) {
		moveCursor(index);
		return buildItem(c);
	}

	@Override
	public long getItemId(int index) {
		c.moveToFirst();
		c.move(index);
		return c.getInt(0);
	}

	public void moveCursor(int index) {
		if (index == 0) {
			c.moveToFirst();
		} else if (index + 1 == currIndex) {
			c.moveToPrevious();
		} else if (index - 1 == currIndex) {
			c.moveToNext();
		} else {
			c.moveToPosition(index);
		}
		currIndex = index;
	}

	public abstract Object buildItem(Cursor c);
}
