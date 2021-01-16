/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: Version.java
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

import net.sf.jdivelog.ci.InvalidConfigurationException;
import net.sf.jdivelog.ci.SuuntoComputerType;

/**
 * Description: container for the version information of a Suunto D6
 * 
 * @author Andr&eacute; Schenk
 * @version $Revision: 1.3 $
 */
public class Version {
    public static final int SIZE = 4;

    public final SuuntoComputerType id;

    public final byte high;

    public final byte mid;

    public final byte low;

    public Version(byte[] bytes) {
        if ((bytes == null) || (bytes.length < SIZE)) {
            throw new IllegalArgumentException("parameter \"bytes\" too short");
        }
        try {
            id = SuuntoComputerType.getFromIdentifier(bytes[0]);
        }
        catch (InvalidConfigurationException e) {
            throw new IllegalArgumentException(e);
        }
        high = bytes[1];
        mid = bytes[2];
        low = bytes[3];
    }
}
