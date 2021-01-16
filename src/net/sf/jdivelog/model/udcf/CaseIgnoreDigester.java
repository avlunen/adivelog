/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: CaseTransformDigester.java
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
package net.sf.jdivelog.model.udcf;

import org.apache.commons.digester.Digester;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Description: Digester which ignores the case of XML element names.
 * 
 * @author Andr&eacute; Schenk
 * @version $Revision: 823 $
 */
public class CaseIgnoreDigester extends Digester {
    /**
     * Process notification of the start of an XML element being reached.
     * 
     * @param namespaceURI
     *            The Namespace URI, or the empty string if the element has no
     *            Namespace URI or if Namespace processing is not being
     *            performed.
     * @param localName
     *            The local name (without prefix), or the empty string if
     *            Namespace processing is not being performed.
     * @param qName
     *            The qualified name (with prefix), or the empty string if
     *            qualified names are not available.
     * @param list
     *            The attributes attached to the element. If there are no
     *            attributes, it shall be an empty Attributes object.
     * 
     * @throws SAXException
     *             if a parsing error is to be reported
     */
    public void startElement(
        String namespaceURI, String localName, String qName, Attributes list)
        throws SAXException {
        super.startElement(namespaceURI, localName, qName.toUpperCase(), list);
    }
}
