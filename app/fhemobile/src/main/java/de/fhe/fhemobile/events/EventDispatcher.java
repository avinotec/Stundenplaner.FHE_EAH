/*
 *  Copyright (c) 2014-2022 Fachhochschule Erfurt, Ernst-Abbe-Hochschule Jena
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package de.fhe.fhemobile.events;

import android.util.Log;

import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;


public class EventDispatcher implements Dispatcher {
	
	private static final String TAG = EventDispatcher.class.getSimpleName();
	
	private final HashMap<String, CopyOnWriteArrayList<EventListener>> listenerMap;
	//MS private final Dispatcher target;
	
	protected EventDispatcher() {
		listenerMap = new HashMap<String, CopyOnWriteArrayList<EventListener>>();
		//MS this.target = this;
	}
	
	@Override
	public void addListener(final String type, final EventListener listener) {
		synchronized (listenerMap) {
			CopyOnWriteArrayList<EventListener> list = listenerMap.get(type);
			if (list == null) {
				list = new CopyOnWriteArrayList<>();
				listenerMap.put(type, list);
			}
			list.add(listener);
		}
	}
	
	@Override
	public void removeListener(final String type, final EventListener listener) {
		synchronized (listenerMap) {
			final CopyOnWriteArrayList<EventListener> list = listenerMap.get(type);
			if (list == null) return;
			list.remove(listener);
			if (list.isEmpty()) {
				listenerMap.remove(type);
			}
		}
	}
	
	@Override
	public boolean hasListener(final String type, final EventListener listener) {
		synchronized (listenerMap) {
			final CopyOnWriteArrayList<EventListener> list = listenerMap.get(type);
			if (list == null) return false;
			return list.contains(listener);
		}
	}
	
	@Override
	public void dispatchEvent(final Event event) {
		if (event == null) {
			Log.e(TAG, "can not dispatch null event");
			return;
		}
		final String type = event.getType();
		//MS event.setSource(target);
		event.setSource(this);
		final CopyOnWriteArrayList<EventListener> list;
		synchronized (listenerMap) {
			list = listenerMap.get(type);
		}
		if (list == null) return;
		for (final EventListener listener : list) {
			listener.onEvent(event);
		}
	}
}
