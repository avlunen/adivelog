/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: DiveLogRecord.java
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
 * Dive log record (1 entry every 10 seconds).
 * 
 * @author Kasra F.
 */
public class DiveLogRecord {

	private Integer time; // time (in seconds -- set by code, not extracted from memory)
	private Double depth; // Depth, in current units 
	private Integer nextStopDepth; // Next Stop Depth, in current units 
	private Integer timeToSurface; // Time-To-Surface (TTS), minutes 
	private Double averagePPO2; // Average PPO2
	private Integer currentGasO2Percent; // Current gas percent O2
	private Integer currentGasHePercent; // Current gas percent He
	private Integer nextStopTime; // Next stop time, minutes (If Next Stop Depth is Zero, then this is the NDL time in minutes).
	private Double batteryVoltage; // v5 -- Battery voltage x100 (NOTE: The battery voltage is 9 bits, the top bit always 1. So battery voltage ranges from 256 to 511, or 2.56V to 5.11V)
	private Integer currentNoDecoLimit;
	private Boolean gasSwitchNeeded;
	private Boolean externalPPO2;
	private Integer setPointType;
	private Integer circuitMode;
	private Integer circuitSwitchType;
	private Integer waterTemperature; // Water temperature (Celsius when metric, Fahrenheit when imperial) 
	private Integer sensor1Millivolts; // v4 -- Sensor 1 millivolts
	private Integer sensor2Millivolts; // v4 -- Sensor 2 millivolts
	private Integer sensor3Millivolts; // v4 -- Sensor 3 millivolts

	public Integer getTime() {
		return time;
	}

	public void setTime(Integer time) {
		this.time = time;
	}

	public Double getDepth() {
		return depth;
	}

	public void setDepth(Double depth) {
		this.depth = depth;
	}

	public Integer getNextStopDepth() {
		return nextStopDepth;
	}

	public void setNextStopDepth(Integer nextStopDepth) {
		this.nextStopDepth = nextStopDepth;
	}

	public Integer getTimeToSurface() {
		return timeToSurface;
	}

	public void setTimeToSurface(Integer timeToSurface) {
		this.timeToSurface = timeToSurface;
	}

	public Double getAveragePPO2() {
		return averagePPO2;
	}

	public void setAveragePPO2(Double averagePPO2) {
		this.averagePPO2 = averagePPO2;
	}

	public Integer getCurrentGasO2Percent() {
		return currentGasO2Percent;
	}

	public void setCurrentGasO2Percent(Integer currentGasO2Percent) {
		this.currentGasO2Percent = currentGasO2Percent;
	}

	public Integer getCurrentGasHePercent() {
		return currentGasHePercent;
	}

	public void setCurrentGasHePercent(Integer currentGasHePercent) {
		this.currentGasHePercent = currentGasHePercent;
	}

	public Integer getNextStopTime() {
		return nextStopTime;
	}

	public void setNextStopTime(Integer nextStopTime) {
		this.nextStopTime = nextStopTime;
	}

	public Double getBatteryVoltage() {
		return batteryVoltage;
	}

	public void setBatteryVoltage(Double batteryVoltage) {
		this.batteryVoltage = batteryVoltage;
	}

	public Integer getCurrentNoDecoLimit() {
		return currentNoDecoLimit;
	}

	public void setCurrentNoDecoLimit(Integer currentNoDecoLimit) {
		this.currentNoDecoLimit = currentNoDecoLimit;
	}

	public Boolean getGasSwitchNeeded() {
		return gasSwitchNeeded;
	}

	public void setGasSwitchNeeded(Boolean gasSwitchNeeded) {
		this.gasSwitchNeeded = gasSwitchNeeded;
	}

	public Boolean getExternalPPO2() {
		return externalPPO2;
	}

	public void setExternalPPO2(Boolean externalPPO2) {
		this.externalPPO2 = externalPPO2;
	}

	public Integer getSetPointType() {
		return setPointType;
	}

	public void setSetPointType(Integer setPointType) {
		this.setPointType = setPointType;
	}

	public Integer getCircuitMode() {
		return circuitMode;
	}

	public void setCircuitMode(Integer circuitMode) {
		this.circuitMode = circuitMode;
	}

	public Integer getCircuitSwitchType() {
		return circuitSwitchType;
	}

	public void setCircuitSwitchType(Integer circuitSwitchType) {
		this.circuitSwitchType = circuitSwitchType;
	}

	public Integer getWaterTemperature() {
		return waterTemperature;
	}

	public void setWaterTemperature(Integer waterTemperature) {
		this.waterTemperature = waterTemperature;
	}

	public Integer getSensor1Millivolts() {
		return sensor1Millivolts;
	}

	public void setSensor1Millivolts(Integer sensor1Millivolts) {
		this.sensor1Millivolts = sensor1Millivolts;
	}

	public Integer getSensor2Millivolts() {
		return sensor2Millivolts;
	}

	public void setSensor2Millivolts(Integer sensor2Millivolts) {
		this.sensor2Millivolts = sensor2Millivolts;
	}

	public Integer getSensor3Millivolts() {
		return sensor3Millivolts;
	}

	public void setSensor3Millivolts(Integer sensor3Millivolts) {
		this.sensor3Millivolts = sensor3Millivolts;
	}

}
