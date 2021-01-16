/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: OSTCImporter.java
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
package net.sf.jdivelog.gui.ostc;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.TreeSet;

import net.sf.jdivelog.ci.ostc.OSTCProtocol110523Mk2;
import net.sf.jdivelog.gui.ImportWindow;
import net.sf.jdivelog.model.JDive;

import org.apache.commons.io.FileUtils;

public class OSTCImporter {
    
    public static void main(String[] args) throws IOException {
        TreeSet<JDive> dives = readFile(new File(args[0]));
        ImportWindow w = new ImportWindow(null, dives);
        w.setVisible(true);
    }

    private static TreeSet<JDive> readFile(File file) throws IOException {
        byte[] bytes = readBytes(file);
        return new OSTCProtocol110523Mk2().extractDives(bytes);
    }

    private static byte[] readBytes(File file) throws IOException {
        @SuppressWarnings("unchecked")
        List<String> lines = FileUtils.readLines(file, "UTF-8");
        ByteBuffer bb = ByteBuffer.allocate(100*1024);
        for (String line : lines) {
            for (int i=0; i+1<line.length(); i+=2) {
                String s = line.substring(i, i+2);
                byte b = (byte)Integer.parseInt(s, 16);
                bb.put(b);
            }
        }
        return bb.array();
    }

}
