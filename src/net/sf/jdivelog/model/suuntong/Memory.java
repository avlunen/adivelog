/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: Memory.java
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

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import net.sf.jdivelog.ci.ChecksumException;
import net.sf.jdivelog.comm.SerialPort;

/**
 * Description: container for the whole memory of a Suunto D6
 * 
 * @author Andr&eacute; Schenk
 * @version $Revision: 1.9 $
 */
public class Memory {
	public static final int SIZE = 0x7FFE;

	private static final int NUMBER_OF_PAGES = SIZE / ReadMemory.PAGE_SIZE + 1;

	private final Page[] memory = new Page[NUMBER_OF_PAGES];

	private final ReadMemory readMemory;

	private class Page {
		private final byte[] content = new byte[ReadMemory.PAGE_SIZE];

		public byte[] read(int startAddressInPage, int count) {
			byte[] result = new byte[count];

			System.arraycopy(content, startAddressInPage, result, 0, count);
			return result;
		}

		public void write(byte[] content) {
			System.arraycopy(content, 0, this.content, 0, content.length);
		}
	}

	public Memory(SerialPort port) throws IOException {
		readMemory = new ReadMemory(port);
	}

	/**
	 * Get the address relative to the page.
	 * 
	 * @param address
	 * @return
	 */
	private int addressInPage(int address) {
		return address - firstAddress(pageNumber(address));
	}

	/**
	 * Get the first address in the given page.
	 * 
	 * @param pageNumber
	 * @return
	 */
	private static int firstAddress(int pageNumber) {
		return pageNumber * ReadMemory.PAGE_SIZE;
	}

	/**
	 * Get the last address in the given page.
	 * 
	 * @param pageNumber
	 * @return
	 */
	private static int lastAddress(int pageNumber) {
		return (pageNumber + 1) * ReadMemory.PAGE_SIZE - 1;
	}

	/**
	 * Create a Memory object and fill it with the contents of a Suunto data
	 * file.
	 * 
	 * @param filename
	 *            Suunto data file
	 * 
	 * @return Memory object
	 * @throws IOException
	 */
	public static Memory loadFromFile(String filename) throws IOException {
		Memory result = new Memory(null);
		FileInputStream input = new FileInputStream(filename);

		for (int pageNumber = 0; pageNumber < result.memory.length; pageNumber++) {
			byte[] bytes = new byte[ReadMemory.PAGE_SIZE];
			int count = input.read(bytes);
			Page page = result.new Page();

			page.write(bytes);
			result.memory[pageNumber] = page;
			if (count < ReadMemory.PAGE_SIZE) {
				break;
			}
		}
		input.close();
		return result;
	}

	/**
	 * Check if the page with the given number exists.
	 * 
	 * @param pageNumber
	 * @return
	 */
	private boolean pageExists(int pageNumber) {
		return memory[pageNumber] != null;
	}

	/**
	 * Get the page number from the given address.
	 * 
	 * @param address
	 * @return
	 */
	private static int pageNumber(int address) {
		return address / ReadMemory.PAGE_SIZE;
	}

	/**
	 * Read a memory block.
	 * 
	 * @param address
	 *            start address
	 * @param count
	 *            number of bytes to read
	 * 
	 * @return the memory block
	 * @throws IOException
	 *             Thrown if the communication to the dive computer failed.
	 * @throws ChecksumException
	 *             Thrown if the checksum was wrong.
	 * @throws InterruptedException
	 *             Thrown if any thread has interrupted the current thread.
	 */
	public byte[] read(int address, int count) throws IOException,
			ChecksumException, InterruptedException {
		ByteArrayOutputStream result = new ByteArrayOutputStream();

		if ((count < 1) || (count > 0x7FFC)) {
			throw new IllegalArgumentException(
					"parameter \"count\" out of bounds");
		}

		int startPage = pageNumber(address);
		int endPage = pageNumber(address + count - 1);

		if (startPage == endPage) {
			byte[] bytes = readMemory(address, count);

			result.write(bytes);
		} else {
			for (int page = startPage; page <= endPage; page++) {
				if (page == startPage) {
					int currentCount = lastAddress(page) - address + 1;
					byte[] bytes = readMemory(address, currentCount);

					result.write(bytes);
					count -= currentCount;
				} else if (page == endPage) {
					byte[] bytes = readMemory(firstAddress(page), count);

					result.write(bytes);
				} else {
					byte[] bytes = readMemory(firstAddress(page),
							ReadMemory.PAGE_SIZE);

					result.write(bytes);
					count -= ReadMemory.PAGE_SIZE;
				}
			}
		}
		return result.toByteArray();
	}

	/**
	 * Read a memory block within a page.
	 * 
	 * @param address
	 *            start address
	 * @param count
	 *            number of bytes to read
	 * 
	 * @return the memory block
	 * @throws IOException
	 *             Thrown if the communication to the dive computer failed.
	 * @throws ChecksumException
	 *             Thrown if the checksum was wrong.
	 * @throws InterruptedException
	 *             Thrown if any thread has interrupted the current thread.
	 */
	private byte[] readMemory(int address, int count) throws IOException,
			ChecksumException, InterruptedException {
		final int pageNumber = pageNumber(address);

		if (!pageExists(pageNumber)) {
			int firstAddress = firstAddress(pageNumber);
			byte[] bytes = readMemory.execute(firstAddress,
					ReadMemory.PAGE_SIZE);

			memory[pageNumber] = new Page();
			memory[pageNumber].write(bytes);
		}
		return memory[pageNumber].read(addressInPage(address), count);
	}

	/**
	 * Save the memory contents to the given file.
	 * 
	 * @param filename
	 *            Suunto data file
	 * @throws IOException
	 */
	public void saveToFile(String filename) throws IOException {
		FileOutputStream output = new FileOutputStream(filename);

		for (int pageNumber = 0; pageNumber < memory.length; pageNumber++) {
			byte[] bytes;

			if (pageExists(pageNumber)) {
				bytes = memory[pageNumber].content;
			} else {
				bytes = new byte[ReadMemory.PAGE_SIZE];
			}
			output.write(bytes);
		}
		output.flush();
		output.close();
	}
}
