package utility;

import java.util.Iterator;

public class IteratorUtility {
	public static <T> IterableIterator<T> adapt(final Iterator<T> iter) {
		return new IterableIterator<T>() {

			@Override
			public boolean hasNext() {
				return iter.hasNext();
			}

			@Override
			public T next() {
				return iter.next();
			}

			@Override
			public void remove() {
				iter.remove();
			}

			@Override
			public Iterator<T> iterator() {
				return iter;
			}
		};
		
	}
}
