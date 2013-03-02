package org.matheusdev.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Thanks to the mighty Riven ;)
 * <a href="http://www.java-gaming.org/index.php/topic,28660.0.html">The Forum link</a>
 * @author Riven.
 */
public class ReadWriteCollection<E> implements Iterator<E>, Iterable<E> {
	private int iterateIndex, retainIndex, endIndex;
	private E[] items;
	private boolean doRetain;

	@SuppressWarnings("unchecked")
	public ReadWriteCollection() {
		items = (E[]) new Object[4];
	}

	public E[] getArrayPointer() {
		return items;
	}

	public void add(E item) {
		if (endIndex == items.length) {
			items = Arrays.copyOf(items, Math.max(4, items.length << 1));
		}
		items[endIndex++] = item;
	}

	@Override
	public Iterator<E> iterator() {
		this.prepareForAccess();
		return this;
	}

	@Override
	public boolean hasNext() {
		if (doRetain) {
			items[retainIndex++] = items[iterateIndex - 1];
			doRetain = false;
		}

		if (iterateIndex == endIndex) {
			flip();
			return false;
		}

		return endIndex > 0;
	}

	@Override
	public E next() {
		if (iterateIndex == endIndex) {
			throw new NoSuchElementException();
		}
		doRetain = true;
		return items[iterateIndex++];
	}

	@Override
	public void remove() {
		if (iterateIndex == 0) {
			throw new NoSuchElementException();
		}
		if (!doRetain) {
			throw new IllegalStateException("already removed");
		}
		doRetain = false;
	}

	public void clear() {
		retainIndex = 0;
		flip();
	}

	public void trimToSize() {
		this.prepareForAccess();
		this.items = Arrays.copyOf(items, endIndex);
	}

	private void prepareForAccess() {
		if ((iterateIndex | retainIndex) == 0) {
			// no need to init
			return;
		}

		// process any unprocessed value from the previous iteration
		int off = iterateIndex + (doRetain ? 0 : 1);
		int len = endIndex - off;
		if (off != retainIndex) {
			System.arraycopy(items, off, items, retainIndex, len);
		}
		retainIndex += len;

		this.flip();
	}

	private void flip() {
		Arrays.fill(items, retainIndex, endIndex, null);
		endIndex = retainIndex;
		retainIndex = 0;
		iterateIndex = 0;
		doRetain = false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("[");
		for (int i = 0; i < retainIndex; i++) {
			sb.append(items[i]).append(",");
		}
		for (int i = iterateIndex - (doRetain ? 1 : 0); i < endIndex; i++) {
			sb.append(items[i]).append(",");
		}
		if (sb.length() > 1) {
			sb.setLength(sb.length() - 1);
		}
		return sb.append("]").toString();
	}
}
