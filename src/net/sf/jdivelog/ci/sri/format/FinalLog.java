/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: FinalLog.java
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
package net.sf.jdivelog.ci.sri.format;

/**
 * Final log.
 * 
 * @author Kasra F.
 */
public class FinalLog {

	private Integer computerSerialNum;
	private Integer reserved1;
	private Integer reserved2;
	private Integer softwareVersion;
	private Integer computerModel;
	private Integer logVersion;
	private Integer product; // 5
	private Integer features; // 5

	public Integer getComputerSerialNum() {
		return computerSerialNum;
	}

	public void setComputerSerialNum(Integer computerSerialNum) {
		this.computerSerialNum = computerSerialNum;
	}

	public Integer getReserved1() {
		return reserved1;
	}

	public void setReserved1(Integer reserved1) {
		this.reserved1 = reserved1;
	}

	public Integer getReserved2() {
		return reserved2;
	}

	public void setReserved2(Integer reserved2) {
		this.reserved2 = reserved2;
	}

	public Integer getSoftwareVersion() {
		return softwareVersion;
	}

	public void setSoftwareVersion(Integer softwareVersion) {
		this.softwareVersion = softwareVersion;
	}

	public Integer getComputerModel() {
		return computerModel;
	}

	public void setComputerModel(Integer computerModel) {
		this.computerModel = computerModel;
	}

	public Integer getLogVersion() {
		return logVersion;
	}

	public void setLogVersion(Integer logVersion) {
		this.logVersion = logVersion;
	}

	public Integer getProduct() {
		return product;
	}

	public void setProduct(Integer product) {
		this.product = product;
	}

	public Integer getFeatures() {
		return features;
	}

	public void setFeatures(Integer features) {
		this.features = features;
	}

}
