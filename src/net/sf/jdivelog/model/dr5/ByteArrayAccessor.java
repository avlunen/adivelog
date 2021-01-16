/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: ByteArrayAccessor.java
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
package net.sf.jdivelog.model.dr5;


public class ByteArrayAccessor {
    
    private byte[] buffer;
    
    public ByteArrayAccessor(byte[] buffer) {
        this.buffer = buffer;
    }
    
    public int readUInt(int offset) {
        byte[] data = read(offset, 2);
        int i = data[0];
        i &= 0xff;
        int j = data[1];
        j &= 0xff;
        int k = i;
        k <<= 8;
        k |= j;
        return k;
    }
    
    private byte[] read(int offset, int len) {
        byte[] result = new byte[len];
        System.arraycopy(buffer, offset, result, 0, len);
        return result;
    }

    public void writeBool(int offset, boolean bool) {
        buffer[offset] = bool ? (byte)1 : (byte)0;
    }

    public void writeUByte(int offset, int val) {
        buffer[offset] = (byte)val;
    }

    public void writeUInt(int offset, int val) {
        buffer[offset] = (byte)((val & 0xff00) >> 8);
        buffer[offset+1] = (byte)(val & 0xff);
    }

    public void writeByte(int offset, byte val) {
        buffer[offset] = val;
    }

}
