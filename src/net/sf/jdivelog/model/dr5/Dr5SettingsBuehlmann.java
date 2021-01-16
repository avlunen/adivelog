/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: Dr5SettingsBuehlmann.java
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


public class Dr5SettingsBuehlmann {
    
    private int desaturationMultiplier;
    private int saturationMultiplier;
    private int normalGfLo;
    private int normalGfHi;
    private int emergencyGfLo;
    private int emergencyGfHi;
    private int gfLoPosMin;
    private int gfLoPosMax;
    private int lastStop;
    private int safetyDistanceDecoStop;
    
    /**
     * @return the desaturationMultiplier
     */
    public int getDesaturationMultiplier() {
        return desaturationMultiplier;
    }
    
    /**
     * @param desaturationMultiplier the desaturationMultiplier to set
     */
    public void setDesaturationMultiplier(int desaturationMultiplier) {
        this.desaturationMultiplier = desaturationMultiplier;
    }
    
    /**
     * @return the saturationMultiplier
     */
    public int getSaturationMultiplier() {
        return saturationMultiplier;
    }
    
    /**
     * @param saturationMultiplier the saturationMultiplier to set
     */
    public void setSaturationMultiplier(int saturationMultiplier) {
        this.saturationMultiplier = saturationMultiplier;
    }
    
    /**
     * @return the normalGfLo
     */
    public int getNormalGfLo() {
        return normalGfLo;
    }
    
    /**
     * @param normalGfLo the normalGfLo to set
     */
    public void setNormalGfLo(int normalGfLo) {
        this.normalGfLo = normalGfLo;
    }
    
    /**
     * @return the normalGfHi
     */
    public int getNormalGfHi() {
        return normalGfHi;
    }
    
    /**
     * @param normalGfHi the normalGfHi to set
     */
    public void setNormalGfHi(int normalGfHi) {
        this.normalGfHi = normalGfHi;
    }
    
    /**
     * @return the emergencyGfLo
     */
    public int getEmergencyGfLo() {
        return emergencyGfLo;
    }
    
    /**
     * @param emergencyGfLo the emergencyGfLo to set
     */
    public void setEmergencyGfLo(int emergencyGfLo) {
        this.emergencyGfLo = emergencyGfLo;
    }
    
    /**
     * @return the emergencyGfHi
     */
    public int getEmergencyGfHi() {
        return emergencyGfHi;
    }
    
    /**
     * @param emergencyGfHi the emergencyGfHi to set
     */
    public void setEmergencyGfHi(int emergencyGfHi) {
        this.emergencyGfHi = emergencyGfHi;
    }
    
    /**
     * @return the gfLoPosMin
     */
    public int getGfLoPosMin() {
        return gfLoPosMin;
    }
    
    /**
     * @param gfLoPosMin the gfLoPosMin to set
     */
    public void setGfLoPosMin(int gfLoPosMin) {
        this.gfLoPosMin = gfLoPosMin;
    }
    
    /**
     * @return the gfLoPosMax
     */
    public int getGfLoPosMax() {
        return gfLoPosMax;
    }
    
    /**
     * @param gfLoPosMax the gfLoPosMax to set
     */
    public void setGfLoPosMax(int gfLoPosMax) {
        this.gfLoPosMax = gfLoPosMax;
    }
    
    /**
     * @return the lastStop
     */
    public int getLastStop() {
        return lastStop;
    }
    
    /**
     * @param lastStop the lastStop to set
     */
    public void setLastStop(int lastStop) {
        this.lastStop = lastStop;
    }
    
    /**
     * @return the safetyDistanceDecoStop
     */
    public int getSafetyDistanceDecoStop() {
        return safetyDistanceDecoStop;
    }
    
    /**
     * @param safetyDistanceDecoStop the safetyDistanceDecoStop to set
     */
    public void setSafetyDistanceDecoStop(int safetyDistanceDecoStop) {
        this.safetyDistanceDecoStop = safetyDistanceDecoStop;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<Dr5SettingsBuehlmann>");
        sb.append("<desaturationMultiplier>");
        sb.append(desaturationMultiplier);
        sb.append("</desaturationMultiplier>");
        sb.append("<saturationMultiplier>");
        sb.append(saturationMultiplier);
        sb.append("</saturationMultiplier>");
        sb.append("<normalGfLo>");
        sb.append(normalGfLo);
        sb.append("</normalGfLo>");
        sb.append("<normalGfHi>");
        sb.append(normalGfHi);
        sb.append("</normalGfHi>");
        sb.append("<emergencyGfLo>");
        sb.append(emergencyGfLo);
        sb.append("</emergencyGfLo>");
        sb.append("<emergencyGfHi>");
        sb.append(emergencyGfHi);
        sb.append("</emergencyGfHi>");
        sb.append("<gfLoPosMin>");
        sb.append(gfLoPosMin);
        sb.append("</gfLoPosMin>");
        sb.append("<gfLoPosMax>");
        sb.append(gfLoPosMax);
        sb.append("</gfLoPosMax>");
        sb.append("<lastStop>");
        sb.append(lastStop);
        sb.append("</lastStop>");
        sb.append("<safetyDistanceDecoStop>");
        sb.append(safetyDistanceDecoStop);
        sb.append("</safetyDistanceDecoStop>");
        sb.append("</Dr5SettingsBuehlmann>");
        return sb.toString();
    }
}
