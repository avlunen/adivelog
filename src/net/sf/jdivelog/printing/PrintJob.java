/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: PrintJob.java
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;

import net.sf.jdivelog.printing.fop.FopUtil;

public class PrintJob {
    
    private String xmlData;
    private Report report;
    private OutputDevice outputDevice;
    
    public PrintJob(String xmlData, Report report, OutputDevice outDevice) {
        this.xmlData = xmlData;
        this.report = report;
        this.outputDevice = outDevice;
    }
    
    public void execute() {
        try {
            File tmp = File.createTempFile("jdl", ".fo");
            Writer bos = new BufferedWriter(new FileWriter(tmp));
            report.run(new StringReader(xmlData), bos);
            FopUtil.convert(new BufferedReader(new FileReader(tmp)), outputDevice.getOutputStream(), outputDevice.getExpectedMimeType());
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

}
