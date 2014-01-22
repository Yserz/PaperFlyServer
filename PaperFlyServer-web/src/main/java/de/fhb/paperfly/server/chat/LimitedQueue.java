/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fhb.paperfly.server.chat;

import java.util.LinkedList;

/**
 *
 * @author Michael Koppen <michael.koppen@googlemail.com>
 */
public class LimitedQueue<E> extends LinkedList<E> {

	private int limit;

	public LimitedQueue(int limit) {
		this.limit = limit;
	}

	@Override
	public boolean add(E o) {
		boolean added = super.add(o);
		while (added && size() > limit) {
			super.remove();
		}
		return added;
	}

	public void setLimit(int limit) {
		while (this.limit > limit) {
			super.remove();
		}
		this.limit = limit;
	}

	public int getLimit() {
		return limit;
	}
}
