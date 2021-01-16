/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: Report.java
 * 
 * @author Pascal Pellmont <jdivelog@pellmont.dyndns.org>
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
package net.sf.jdivelog.printing;

import java.io.Reader;
import java.io.Writer;

public interface Report {
    
    /**
     * @return the report name.
     */
    public String getName();

    /**
     * runs the report
     * @param in input stream containing logbook as xml
     * @param out output stream for fo-document
     */
    public void run(Reader in, Writer out);

}
