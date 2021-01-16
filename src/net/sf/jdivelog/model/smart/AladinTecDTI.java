package net.sf.jdivelog.model.smart;

/**
 * Description: enumeration for the data type indicators of an Aladin Tec 2G
 * file.
 * 
 * @author Andr&eacute; Schenk
 * @version $Revision: 423 $
 */
public enum AladinTecDTI {
    // @formatter:off
    DELTA_DEPTH (0),
    DELTA_TEMPERATURE (0),
    TIME (0),
    ALARMS (0),
    DELTA_DEPTH2 (1),
    DELTA_TEMPERATURE2 (1),
    ABSOLUTE_DEPTH (2),
    ABSOLUTE_TEMPERATURE (2),
    ALARMS2 (1);
    // @formatter:on

    private final int extraBytes;

    AladinTecDTI(int extraBytes) {
        this.extraBytes = extraBytes;
    }

    public int getExtraBytes() {
        return extraBytes;
    }
}
