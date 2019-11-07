/*
 * Copyright (c) 2014-2019 Fachhochschule Erfurt, Ernst-Abbe-Hochschule Jena
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.fhe.fhemobile.events;

import android.util.Log;

import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;


public class EventDispatcher implements Dispatcher {
	
	private static final String TAG = EventDispatcher.class.getSimpleName();
	
	private final HashMap<String, CopyOnWriteArrayList<EventListener>> listenerMap;
	private Dispatcher target;
	
	protected EventDispatcher() {
		this(null);
	}
	private EventDispatcher(Dispatcher target) {
		listenerMap = new HashMap<String, CopyOnWriteArrayList<EventListener>>();
		this.target = (target != null) ? target : this;
	}
	
	@Override
	public void addListener(String type, EventListener listener) {
		synchronized (listenerMap) {
			CopyOnWriteArrayList<EventListener> list = listenerMap.get(type);
			if (list == null) {
				list = new CopyOnWriteArrayList<EventListener>();
				listenerMap.put(type, list);
			}
			list.add(listener);
		}
	}
	
	@Override
	public void removeListener(String type, EventListener listener) {
		synchronized (listenerMap) {
			CopyOnWriteArrayList<EventListener> list = listenerMap.get(type);
			if (list == null) return;
			list.remove(listener);
			if (list.size() == 0) {
				listenerMap.remove(type);
			}
		}
	}
	
	@Override
	public boolean hasListener(String type, EventListener listener) {
		synchronized (listenerMap) {
			CopyOnWriteArrayList<EventListener> list = listenerMap.get(type);
			if (list == null) return false;
			return list.contains(listener);
		}
	}
	
	@Override
	public void dispatchEvent(Event event) {
		if (event == null) {
			Log.e(TAG, "can not dispatch null event");
			return;
		}
		String type = event.getType();
		event.setSource(target);
		CopyOnWriteArrayList<EventListener> list;
		synchronized (listenerMap) {
			list = listenerMap.get(type);
		}
		if (list == null) return;
		for (EventListener l : list) {
			l.onEvent(event);
		}
	}
	
	public void dispose() {
		synchronized (listenerMap) {
			listenerMap.clear();
		}
		target = null;
	}
}
