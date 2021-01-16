package net.sf.jdivelog.model;

import java.util.Map;
import java.util.TreeSet;

import net.sf.jdivelog.ci.ostc.AbstractOSTCProtocol;
import net.sf.jdivelog.ci.ostc.Feature;
import net.sf.jdivelog.ci.ostc.OSTCValue;
import net.sf.jdivelog.ci.ostc.Profiles;

/**
 * Description: adapts the OSTC data structure to JDiveLog format
 * 
 * @author Andr&eacute; Schenk
 * @version $Revision: 548 $
 */
public class OstcAdapter extends TreeSet<JDive> {
    private static final long serialVersionUID = -692133991373600243L;

    public OstcAdapter(Map<Feature, OSTCValue> ostcData) {
        if (ostcData == null) {
            throw new IllegalArgumentException("parameter ostcData is null");
        }

        Profiles profiles = (Profiles) ostcData.get(AbstractOSTCProtocol.FEATURE_PROFILES);

        if (profiles != null) {
            addAll(profiles.getDives());
        }
    }
}
