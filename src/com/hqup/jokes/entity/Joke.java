package com.hqup.jokes.entity;

import java.util.Comparator;

public class Joke {

	private String number;
	private String text;

	public Joke(String number, String text) {
		this.number = number;
		this.text = text;
	}

	// ===============Comparators================

	public static Comparator<Joke> byNumComparator = new Comparator<Joke>() {

		@Override
		public int compare(Joke j1, Joke j2) {
			// Larger number followed by smaller
			return (j2.number).compareTo(j1.number);
		}
	};

	public static Comparator<Joke> byAbcComparator = new Comparator<Joke>() {

		@Override
		public int compare(Joke j1, Joke j2) {
			return (j1.text).compareTo(j2.text);
		}
	};

	// ============Getters and Setters================

	public String getNumber() {
		return number;
	}

	public String getText() {
		return text;
	}

	// =========equals() and hashCode() and toString()===============

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((number == null) ? 0 : number.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Joke other = (Joke) obj;
		if (number == null) {
			if (other.number != null)
				return false;
		} else if (!number.equals(other.number))
			return false;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return number + "\n" + text;
	}

}
