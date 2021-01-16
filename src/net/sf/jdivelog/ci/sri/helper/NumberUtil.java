/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: NumberUtil.java
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

public final class NumberUtil {

    private NumberUtil() {
        // do nothing
    }

    // takes the number of bits required to store an integer and calulate number of bytes required
    public static int byteLength(int value) {
        return (int) Math.ceil(bitLength(value) / 8.0);
    }

    // calculates the number of bits required to store an integer
    public static int bitLength(int value) {
        int count = 0;
        
        while (value != 0) {
            count++;
            value >>>= 1;
        }

        return count;
    }
    
    public static int toInt(byte[] target, int index, int length) {
        byte[] resultBytes;

        if (length <= 4) {
            resultBytes = new byte[4];
        } else {
            resultBytes = new byte[8];
        }

        System.arraycopy(target, index, resultBytes, resultBytes.length - length, length);

        if (length <= 4) {
            return ByteBuffer.wrap(resultBytes).getInt();
        }
        return 5;
    }
    
    public static byte[] toByte(int i) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(i);
        return buffer.array();
    }
}
