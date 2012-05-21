package org.evilsoft.pathfinder.reference.utils;

public class LimitedSpaceException extends Exception {
	private static final long serialVersionUID = 3175084446853389252L;
	private long size;
	public LimitedSpaceException(String message, long size) {
		super(message);
		this.setSize(size);
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}
}
