/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: DiveLogFooter.java
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

import java.util.List;

/**
 * Dive log footer.
 * 
 * @author Kasra F.
 */
public class DiveLogFooter {

	private Integer maxDiveDepth; // Max Dive Depth 
	private Integer diveTime; // Dive time in minutes 
	private Double internalBatteryVoltage;  // Internal battery voltage (x10, i.e. 1 implied decimal place. 34 = 3.4V)
	private Integer cnsPercent; // CNS % 
	private Integer timestamp; // Unix Time, seconds 
	private Boolean o2Sensor1Status; // O2 Sensor 1 voting status (1 = voted in. 0 = voted out or not calibrated.)
	private Boolean o2Sensor2Status; // O2 Sensor 2 voting status (1 = voted in. 0 = voted out or not calibrated.)
	private Boolean o2Sensor3Status; // O2 Sensor 3 voting status (1 = voted in. 0 = voted out or not calibrated.)
	private Double lowPPO2Setpoint; // Low PPO2 setpoint (x100, 130 = 1.30)
	private Double highPPO2Setpoint; // HighPPO2 setpoint (x100, 130 = 1.30)
	private Integer switchUpSetting; // Switch up (low sp->high sp) setting. (1 = auto, 0 = manual)
	private Integer switchUpDepth; // Switch up depth, in current units. Only valid if Switch up setting = 1.
	private Integer switchDownSetting; // Switch down (high sp->low sp) setting. (1 = auto, 0 = manual)
	private Integer switchDownDepth; // Switch down depth, in current units. Only valid if Switch down setting = 1.
	private Integer o2SensorMode; // Is Single PPO2 Sensor. 1 = single sensor mode. 0 = Normal (3 sensor) mode.
	private Integer surfacePressure; // Surface pressure in millibars 
	private Integer currentEventLogNumber; // Current Info Log Event Number
	private Integer averageDiveDepth; // Dive Average Depth 
	private Integer ocGas0O2Percent; // Gas 0 (OC), O2 percent 
	private Integer ocGas1O2Percent; // Gas 1 (OC), O2 percent 
	private Integer ocGas2O2Percent; // Gas 2 (OC), O2 percent 
	private Integer ocGas3O2Percent; // Gas 3 (OC), O2 percent 
	private Integer ocGas4O2Percent; // Gas 4 (OC), O2 percent 
	private Integer ocGas0HePercent; // Gas 0 (OC), He percent 
	private Integer ocGas1HePercent; // Gas 1 (OC), He percent 
	private Integer ocGas2HePercent; // Gas 2 (OC), He percent 
	private Integer ocGas3HePercent; // Gas 3 (OC), He percent 
	private Integer ocGas4HePercent; // Gas 4 (OC), He percent 
	private Integer ccGas0O2Percent; // Gas 0 (CC/SC), O2 percent 
	private Integer ccGas1O2Percent; // Gas 1 (CC/SC), O2 percent 
	private Integer ccGas2O2Percent; // Gas 2 (CC/SC), O2 percent 
	private Integer ccGas3O2Percent; // Gas 3 (CC/SC), O2 percent 
	private Integer ccGas4O2Percent; // Gas 4 (CC/SC), O2 percent 
	private Integer ccGas0HePercent; // Gas 0 (CC/SC), He percent 
	private Integer ccGas1HePercent; // Gas 1 (CC/SC), He percent 
	private Integer ccGas2HePercent; // Gas 2 (CC/SC), He percent 
	private Integer ccGas3HePercent; // Gas 3 (CC/SC), He percent 
	private Integer ccGas4HePercent; // Gas 4 (CC/SC), He percent 
	private List<Boolean> errorFlags;
	private List<Boolean> errorAcks;

	public Integer getMaxDiveDepth() {
		return maxDiveDepth;
	}

	public void setMaxDiveDepth(Integer maxDiveDepth) {
		this.maxDiveDepth = maxDiveDepth;
	}

	public Integer getDiveTime() {
		return diveTime;
	}

	public void setDiveTime(Integer diveTime) {
		this.diveTime = diveTime;
	}

	public Double getInternalBatteryVoltage() {
		return internalBatteryVoltage;
	}

	public void setInternalBatteryVoltage(Double internalBatteryVoltage) {
		this.internalBatteryVoltage = internalBatteryVoltage;
	}

	public Integer getCnsPercent() {
		return cnsPercent;
	}

	public void setCnsPercent(Integer cnsPercent) {
		this.cnsPercent = cnsPercent;
	}

	public Integer getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Integer timestamp) {
		this.timestamp = timestamp;
	}

	public Boolean getO2Sensor1Status() {
		return o2Sensor1Status;
	}

	public void setO2Sensor1Status(Boolean o2Sensor1Status) {
		this.o2Sensor1Status = o2Sensor1Status;
	}

	public Boolean getO2Sensor2Status() {
		return o2Sensor2Status;
	}

	public void setO2Sensor2Status(Boolean o2Sensor2Status) {
		this.o2Sensor2Status = o2Sensor2Status;
	}

	public Boolean getO2Sensor3Status() {
		return o2Sensor3Status;
	}

	public void setO2Sensor3Status(Boolean o2Sensor3Status) {
		this.o2Sensor3Status = o2Sensor3Status;
	}

	public Double getLowPPO2Setpoint() {
		return lowPPO2Setpoint;
	}

	public void setLowPPO2Setpoint(Double lowPPO2Setpoint) {
		this.lowPPO2Setpoint = lowPPO2Setpoint;
	}

	public Double getHighPPO2Setpoint() {
		return highPPO2Setpoint;
	}

	public void setHighPPO2Setpoint(Double highPPO2Setpoint) {
		this.highPPO2Setpoint = highPPO2Setpoint;
	}

	public Integer getSwitchUpSetting() {
		return switchUpSetting;
	}

	public void setSwitchUpSetting(Integer switchUpSetting) {
		this.switchUpSetting = switchUpSetting;
	}

	public Integer getSwitchUpDepth() {
		return switchUpDepth;
	}

	public void setSwitchUpDepth(Integer switchUpDepth) {
		this.switchUpDepth = switchUpDepth;
	}

	public Integer getSwitchDownSetting() {
		return switchDownSetting;
	}

	public void setSwitchDownSetting(Integer switchDownSetting) {
		this.switchDownSetting = switchDownSetting;
	}

	public Integer getSwitchDownDepth() {
		return switchDownDepth;
	}

	public void setSwitchDownDepth(Integer switchDownDepth) {
		this.switchDownDepth = switchDownDepth;
	}

	public Integer getO2SensorMode() {
		return o2SensorMode;
	}

	public void setO2SensorMode(Integer o2SensorMode) {
		this.o2SensorMode = o2SensorMode;
	}

	public Integer getSurfacePressure() {
		return surfacePressure;
	}

	public void setSurfacePressure(Integer surfacePressure) {
		this.surfacePressure = surfacePressure;
	}

	public Integer getCurrentEventLogNumber() {
		return currentEventLogNumber;
	}

	public void setCurrentEventLogNumber(Integer currentEventLogNumber) {
		this.currentEventLogNumber = currentEventLogNumber;
	}

	public Integer getAverageDiveDepth() {
		return averageDiveDepth;
	}

	public void setAverageDiveDepth(Integer averageDiveDepth) {
		this.averageDiveDepth = averageDiveDepth;
	}

	public Integer getOcGas0O2Percent() {
		return ocGas0O2Percent;
	}

	public void setOcGas0O2Percent(Integer ocGas0O2Percent) {
		this.ocGas0O2Percent = ocGas0O2Percent;
	}

	public Integer getOcGas1O2Percent() {
		return ocGas1O2Percent;
	}

	public void setOcGas1O2Percent(Integer ocGas1O2Percent) {
		this.ocGas1O2Percent = ocGas1O2Percent;
	}

	public Integer getOcGas2O2Percent() {
		return ocGas2O2Percent;
	}

	public void setOcGas2O2Percent(Integer ocGas2O2Percent) {
		this.ocGas2O2Percent = ocGas2O2Percent;
	}

	public Integer getOcGas3O2Percent() {
		return ocGas3O2Percent;
	}

	public void setOcGas3O2Percent(Integer ocGas3O2Percent) {
		this.ocGas3O2Percent = ocGas3O2Percent;
	}

	public Integer getOcGas4O2Percent() {
		return ocGas4O2Percent;
	}

	public void setOcGas4O2Percent(Integer ocGas4O2Percent) {
		this.ocGas4O2Percent = ocGas4O2Percent;
	}

	public Integer getOcGas0HePercent() {
		return ocGas0HePercent;
	}

	public void setOcGas0HePercent(Integer ocGas0HePercent) {
		this.ocGas0HePercent = ocGas0HePercent;
	}

	public Integer getOcGas1HePercent() {
		return ocGas1HePercent;
	}

	public void setOcGas1HePercent(Integer ocGas1HePercent) {
		this.ocGas1HePercent = ocGas1HePercent;
	}

	public Integer getOcGas2HePercent() {
		return ocGas2HePercent;
	}

	public void setOcGas2HePercent(Integer ocGas2HePercent) {
		this.ocGas2HePercent = ocGas2HePercent;
	}

	public Integer getOcGas3HePercent() {
		return ocGas3HePercent;
	}

	public void setOcGas3HePercent(Integer ocGas3HePercent) {
		this.ocGas3HePercent = ocGas3HePercent;
	}

	public Integer getOcGas4HePercent() {
		return ocGas4HePercent;
	}

	public void setOcGas4HePercent(Integer ocGas4HePercent) {
		this.ocGas4HePercent = ocGas4HePercent;
	}

	public Integer getCcGas0O2Percent() {
		return ccGas0O2Percent;
	}

	public void setCcGas0O2Percent(Integer ccGas0O2Percent) {
		this.ccGas0O2Percent = ccGas0O2Percent;
	}

	public Integer getCcGas1O2Percent() {
		return ccGas1O2Percent;
	}

	public void setCcGas1O2Percent(Integer ccGas1O2Percent) {
		this.ccGas1O2Percent = ccGas1O2Percent;
	}

	public Integer getCcGas2O2Percent() {
		return ccGas2O2Percent;
	}

	public void setCcGas2O2Percent(Integer ccGas2O2Percent) {
		this.ccGas2O2Percent = ccGas2O2Percent;
	}

	public Integer getCcGas3O2Percent() {
		return ccGas3O2Percent;
	}

	public void setCcGas3O2Percent(Integer ccGas3O2Percent) {
		this.ccGas3O2Percent = ccGas3O2Percent;
	}

	public Integer getCcGas4O2Percent() {
		return ccGas4O2Percent;
	}

	public void setCcGas4O2Percent(Integer ccGas4O2Percent) {
		this.ccGas4O2Percent = ccGas4O2Percent;
	}

	public Integer getCcGas0HePercent() {
		return ccGas0HePercent;
	}

	public void setCcGas0HePercent(Integer ccGas0HePercent) {
		this.ccGas0HePercent = ccGas0HePercent;
	}

	public Integer getCcGas1HePercent() {
		return ccGas1HePercent;
	}

	public void setCcGas1HePercent(Integer ccGas1HePercent) {
		this.ccGas1HePercent = ccGas1HePercent;
	}

	public Integer getCcGas2HePercent() {
		return ccGas2HePercent;
	}

	public void setCcGas2HePercent(Integer ccGas2HePercent) {
		this.ccGas2HePercent = ccGas2HePercent;
	}

	public Integer getCcGas3HePercent() {
		return ccGas3HePercent;
	}

	public void setCcGas3HePercent(Integer ccGas3HePercent) {
		this.ccGas3HePercent = ccGas3HePercent;
	}

	public Integer getCcGas4HePercent() {
		return ccGas4HePercent;
	}

	public void setCcGas4HePercent(Integer ccGas4HePercent) {
		this.ccGas4HePercent = ccGas4HePercent;
	}

	public List<Boolean> getErrorFlags() {
		return errorFlags;
	}

	public void setErrorFlags(List<Boolean> errorFlags) {
		this.errorFlags = errorFlags;
	}

	public List<Boolean> getErrorAcks() {
		return errorAcks;
	}

	public void setErrorAcks(List<Boolean> errorAcks) {
		this.errorAcks = errorAcks;
	}

}
