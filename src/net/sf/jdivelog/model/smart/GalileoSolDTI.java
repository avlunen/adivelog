package net.sf.jdivelog.model.smart;

/**
 * Description: enumeration for the data type indicators of a Galileo Sol file.
 * 
 * @author Andr&eacute; Schenk
 * @version $Revision: 423 $
 */
public enum GalileoSolDTI {
    // @formatter:off
    DELTA_DEPTH (0),
    DELTA_RBT (0),
    DELTA_TANK_PRESSURE (0),
    DELTA_TEMPERATURE (0),
    TIME (0),
    DELTA_HEARTRATE (0),
    ALARMS (0),
    ALARMS2 (1),
    ABSOLUTE_DEPTH (2),
    ABSOLUTE_RBT (1),
    ABSOLUTE_TEMPERATURE (2),
    ABSOLUTE_TANK_1_PRESSURE (2),
    ABSOLUTE_TANK_2_PRESSURE (2),
    ABSOLUTE_TANK_3_PRESSURE (2),
    ABSOLUTE_HEARTRATE (1),
    BEARING (2),
    ALARMS3 (1);
    // @formatter:on

    private final int extraBytes;

    GalileoSolDTI(int extraBytes) {
        this.extraBytes = extraBytes;
    }

    public int getExtraBytes() {
        return extraBytes;
    }
}
