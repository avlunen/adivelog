/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: Delta.java
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
package net.sf.jdivelog.model.udcf;

/**
 * Description: Type storing temperatures
 * 
 * @author Volker Holthaus <volker.urlaub@gmx.de>
 */
public class PPO2 implements Sample {
    
    private String sensor;
    private Double value;

    /**
     * @return the sample type
     * @see net.sf.jdivelog.model.udcf.Sample#getType()
     * @see net.sf.jdivelog.model.udcf.Sample#TYPE_TEMPERATURE
     */
    public int getType() {
        return Sample.TYPE_PPO2;
    }

    /**
     * @return java.lang.Double The temperature as Double
     * @see net.sf.jdivelog.model.udcf.Sample#getValue()
     */
    public Double getValue() {
        return value;
    }
    
    /**
     * set the temperature.
     * @param d The new temperature.
     */
    public void setValue(Double d) {
        value = d;
    }

    /**
     * @return the sensor
     */
    public String getSensor() {
        return sensor;
    }

    /**
     * @param sensor the sensor to set
     */
    public void setSensor(String sensor) {
        this.sensor = sensor;
    }
    
    /**
     * get the xml representation of the element.
     * @return java.lang.String The XML representation of the element.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer("<PPO2");
        if (sensor!= null) {
            sb.append(" sensor=\"");
            sb.append(sensor);
            sb.append("\"");
        }
        sb.append(">");
        sb.append(value);
        sb.append("</PPO2>");
        return sb.toString();
    }

}
