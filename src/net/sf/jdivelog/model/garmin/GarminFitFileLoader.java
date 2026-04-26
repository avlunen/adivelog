package net.sf.jdivelog.model.garmin;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.jdivelog.gui.DiveImportWindow;
import net.sf.jdivelog.gui.MainWindow;
import net.sf.jdivelog.gui.resources.Messages;
import net.sf.jdivelog.model.JDive;

/**
 * Class to load Garmin FIT files
 * 
 * If Scuba dives are present in the FIT file, they will be added to the dive dialog,
 * to then be imported into the ADiveLog app.
 * 
 * 
 * @author Alexander von Lünen
 * @version 1.0
 * @since 23 Apr 2026
 */

public class GarminFitFileLoader {
   /** parent window of this importer */
   MainWindow wnd = null;
   /** logger instance */
   private static final Logger LOGGER = Logger.getLogger(GarminFitFileLoader.class.getName());
   /** list of imported dives */
   private List<DepthProfileEntries> dives = new ArrayList<DepthProfileEntries>();

   public GarminFitFileLoader(MainWindow mainWindow, File[] files) throws FileNotFoundException {

      for (int i = 0; i < files.length; i++) {
         try {
            garminFitDecode gfd = new garminFitDecode();
            gfd.decodeFile(files[i].toString());
            //if(gfd.getDiveToAdd().getDiveNumber() >= 0) dives.add(gfd.getDiveToAdd());
            dives.add(gfd.getDpes());
         }
         catch (Exception e) {
            LOGGER.log(Level.SEVERE, "failed to load garmin fit file", e);
         }
      }
      GarminFitAdapter gfa = new GarminFitAdapter(dives, mainWindow.getLogBook().getLastDive().getDiveNumber());

      // open the diveImportDataTrak window to mark the dives for import
      DiveImportWindow diw = new DiveImportWindow(mainWindow, new ArrayList<JDive>(gfa), Messages.getString("diveimportgarmin"));

      diw.setVisible(true);
   }
}
