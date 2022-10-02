package net.sf.jdivelog.model.ostc;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.jdivelog.ci.OSTCInterface;
import net.sf.jdivelog.ci.ostc.AbstractOSTCProtocol;
import net.sf.jdivelog.ci.ostc.VersionRange;
import net.sf.jdivelog.gui.DiveImportWindow;
import net.sf.jdivelog.gui.MainWindow;
import net.sf.jdivelog.gui.resources.Messages;
import net.sf.jdivelog.model.JDive;
import net.sf.jdivelog.model.OstcAdapter;

/**
 * Description: loads OSTC data files and converts them into JDiveLog format.
 * 
 * @author Andr&eacute; Schenk
 * @version $Revision: 1.10 $
 */
public class OstcFileLoader {
    private static final Logger LOGGER = Logger.getLogger(OstcFileLoader.class.getName());

    public OstcFileLoader(MainWindow mainWindow, File[] files) {
        ArrayList<JDive> dives = new ArrayList<JDive>();

        for (int i = 0; i < files.length; i++) {
            try {
                byte[] bytes = new byte[(int) files[i].length()];
                FileInputStream input = new FileInputStream(files[i]);

                input.read(bytes);
                input.close();

                String protocolName = OSTCInterface.getDriverNameByFirmwareVersion(bytes[264], bytes[265]);
                AbstractOSTCProtocol protocol = OSTCInterface.getDriver(protocolName);

                if (protocol != null) {
                    dives.addAll(new OstcAdapter(protocol.parseAll(bytes)));
                }
                else {
                    LOGGER.log(
                            Level.SEVERE,
                            "could not find an OSTC protocol for firmware version "
                                    + VersionRange.getVersion(bytes[264], bytes[265]));
                }
            }
            catch (Exception e) {
                LOGGER.log(Level.SEVERE, "failed to load OSTC file", e);
            }
        }

        // open the dive import window to mark the dives for import
        if (mainWindow != null) {
            DiveImportWindow daw = new DiveImportWindow(mainWindow, dives, Messages.getString("diveimportostc"));

            daw.setVisible(true);
        }
    }
}
