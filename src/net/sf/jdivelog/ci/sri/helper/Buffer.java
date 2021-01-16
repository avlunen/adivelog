/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: Buffer.java
 * 
 * @author Kasra F (Shearwater Research Inc) <kfaghihi@shearwaterresearch.com>
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
package net.sf.jdivelog.ci.sri.helper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class Buffer {

    private ByteBuffer bb;

    public Buffer(byte[] data, int offset) {
        this(data, offset, data.length - offset);
    }

    public Buffer(byte[] data, int offset, int length) {
        bb = ByteBuffer.wrap(data, offset, length);
    }

    public void setPosition(int newPosition) {
        bb.position(newPosition);
    }

    public int getPosition() {
        return bb.position();
    }

    public int readBeInt32() {
        bb.order(ByteOrder.BIG_ENDIAN);
        return bb.getInt();
    }

    public int readBeInt24() {
        byte[] out = new byte[4];
        bb.get(out, 1, 3);
        ByteBuffer tempBb = ByteBuffer.wrap(out);
        tempBb.order(ByteOrder.BIG_ENDIAN);
        return tempBb.getInt();
    }

    public int readBeInt16() {
        bb.order(ByteOrder.BIG_ENDIAN);
        return bb.getShort() & 0xFFFF;
    }

    public int getBeInt16() {
        return getBeInt16(bb.position());
    }

    public int getBeInt16(int offset) {
        // does not move internal bb pointer
        bb.order(ByteOrder.BIG_ENDIAN);
        return bb.getShort(offset) & 0xFFFF;
    }

    public int readByte() {
        return bb.get() & 0xFF;
    }

    public byte[] readBytes(int cnt) {
        byte[] ret = new byte[cnt];
        bb.get(ret);
        return ret;
    }

    public int getByte() {
        return getByte(bb.position());
    }

    public int getByte(int offset) {
        return bb.get(offset) & 0xFF;
    }

    public void skip(int cnt) {
        bb.position(bb.position() + cnt);
    }
}
