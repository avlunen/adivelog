/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: EventType.java
 *
 * @author Andr&eacute; Schenk <andre_schenk@users.sourceforge.net>
 *
 * This file is part of JDiveLog.
 * JDiveLog is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * JDiveLog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with JDiveLog; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.sf.jdivelog.model.suuntong;

/**
 * Description: enumeration of the events which may occur in the Suunto D6 log
 * 
 * @author Andr&eacute; Schenk
 * @version $Revision: 1.2 $
 */
public enum EventType {
	NEXT_EVENT_MARKER(0x01), SURFACED(0x02), EVENT(0x03), BOOKMARK(0x04);

	private final short eventByte;

	EventType(int eventByte) {
		this.eventByte = (byte) eventByte;
	}

	public short getEventByte() {
		return eventByte;
	}

	/**
	 * Create an EventType from the given byte.
	 * 
	 * @param eventByte
	 *            identifier for the EventType
	 * 
	 * @return EventType
	 */
	public static EventType valueOf(short eventByte) {
		EventType result = null;

		for (EventType e : EventType.values()) {
			if (e.eventByte == eventByte) {
				result = e;
				break;
			}
		}
		if (result == null) {
			throw new IllegalArgumentException("unknown event type "
					+ eventByte);
		}
		return result;
	}
}
