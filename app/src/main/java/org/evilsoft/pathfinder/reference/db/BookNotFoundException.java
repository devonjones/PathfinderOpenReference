package org.evilsoft.pathfinder.reference.db;

public class BookNotFoundException extends Exception {
	private static final long serialVersionUID = 4238742662526288644L;

	public BookNotFoundException() {
	}

	public BookNotFoundException(String message) {
		super(message);
	}

}
