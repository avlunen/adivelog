/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: CommandType.java
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
 * Description: enumeration of the commands which may be sent to the Suunto D6
 * 
 * @author Andr&eacute; Schenk
 * @version $Revision: 1.3 $
 */
public enum CommandType {
    GETVERSION(0x0F), READMEMORY(0x05), RESETMAXDEPTH(0x20), WRITEMEMORY(0x06);

    private final byte commandByte;

    /**
     * Create a command.
     * 
     * @param commandByte
     *            numeric value of the command
     */
    CommandType(int commandByte) {
        this.commandByte = (byte) commandByte;
    }

    /**
     * Get the numeric value of the command.
     * 
     * @return numeric value of the command
     */
    public byte getCommandByte() {
        return commandByte;
    }
}
