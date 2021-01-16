/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: GetVersion.java
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

import java.io.IOException;

import net.sf.jdivelog.ci.ChecksumException;
import net.sf.jdivelog.comm.SerialPort;

/**
 * Description: command to read the version information from a Suunto D6
 * 
 * @author Andr&eacute; Schenk
 * @version $Revision: 1.7 $
 */
public class GetVersion extends Command {
	public GetVersion(SerialPort port) throws IOException {
		super(CommandType.GETVERSION, port);
	}

	public Version execute() throws IOException, ChecksumException,
			InterruptedException {
		return new Version(execute(new byte[] {}));
	}
}
