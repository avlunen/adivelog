/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: SalinityFixTool.java
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
package net.sf.jdivelog.gui.commands;

import java.util.ArrayList;

import net.sf.jdivelog.model.JDive;
import net.sf.jdivelog.model.udcf.Depth;
import net.sf.jdivelog.model.udcf.Dive;
import net.sf.jdivelog.model.udcf.Sample;

/**
 * Tool for fixing Salinity of a dive.
 * @author Pascal Pellmont <jdivelog@pellmont.dyndns.org>
 */
public class SalinityFixTool {
    
    private final double factor;

    public SalinityFixTool(double oldSalinity, double newSalinity) {
        factor = newSalinity / oldSalinity;
    }
    
    public JDive fix(JDive dive) {
        JDive result = null;
        if (dive != null) {
            result = dive.deepClone();
            scaleSamples(result);
            updateJDive(result);
        }
        return result;
    }

    private void scaleSamples(JDive dive) {
        Dive d = dive.getDive();
        if (d != null) {
            ArrayList<Sample> samples = d.getSamples();
            for (Sample sample : samples) {
                if (sample instanceof Depth) {
                    Depth depth = (Depth) sample;
                    if (depth.getValue() != null) {
                        depth.setValue(Double.valueOf(factor * depth.getValue().doubleValue()));
                    }
                }
            }
        }
    }

    private void updateJDive(JDive dive) {
        Dive d = dive.getDive();
        if (d != null) {
            dive.setAverageDepth(d.getAverageDepth());
            dive.setDepth(d.getMaxDepth());
            dive.setAMV((Double)null);
        } else {
            if (dive.getAverageDepth() != null) {
                dive.setAverageDepth(Double.valueOf(factor * dive.getAverageDepth().doubleValue()));
            }
            if (dive.getDepth() != null) {
                dive.setDepth(Double.valueOf(factor * dive.getDepth().doubleValue()));
            }
            if (dive.getAMV() != null) {
                dive.setAMV(Double.valueOf(factor * dive.getAMV()));
            }
        }
    }

}
