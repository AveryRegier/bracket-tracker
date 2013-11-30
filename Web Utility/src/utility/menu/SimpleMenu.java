/* 
Copyright (C) 2003-2008 Avery J. Regier.

This file is part of the Web Utility.

Web Utility is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Web Utility is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>. 

Linking this library statically or dynamically with other modules is
making a combined work based on this library.  Thus, the terms and
conditions of the GNU General Public License cover the whole
combination.

As a special exception, the copyright holders of this library give you
permission to link this library with independent modules to produce an
executable, regardless of the license terms of these independent
modules, and to copy and distribute the resulting executable under
terms of your choice, provided that you also meet, for each linked
independent module, the terms and conditions of the license of that
module.  An independent module is a module which is not derived from
or based on this library.  If you modify this library, you may extend
this exception to your version of the library, but you are not
obligated to do so.  If you do not wish to do so, delete this
exception statement from your version. */

/*
 * Created on Oct 1, 2004
 */
package utility.menu;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Avery J. Regier
 */
public class SimpleMenu implements Menu {
	private String name;
	private Map menuItems;
	private Comparator comparator;
	
	/**
	 * @param name
	 * @param menuItems
	 * @param comparator
	 */
	public SimpleMenu(String name, Map menuItems, Comparator comparator) {
		this.name = name;
		this.menuItems = new TreeMap(comparator);
		this.menuItems.putAll(menuItems);
	}

	/**
	 * @param name
	 * @param menuItems
	 * @param comparator
	 */
	public SimpleMenu(String name, Iterator iter, Comparator comparator) {
		this.name = name;
		this.menuItems = new TreeMap(comparator);
		while (iter.hasNext()) {
			MenuItem item = (MenuItem) iter.next();
			this.menuItems.put(item.getValue(), item.getName());
		}
	}

	/**
	 * Default to the value comparator
	 * @param name
	 * @param menuItems
	 */
	public SimpleMenu(String name, Map menuItems) {
		this(name, menuItems, MenuValueComparator.INSTANCE);
	}
	
	public String getName() {
		return name;
	}

	public Iterator getItems() {
		return menuItems.values().iterator();
	}

	public MenuItem resolve(String value) {
		return (MenuItem)menuItems.get(value);
	}
}
