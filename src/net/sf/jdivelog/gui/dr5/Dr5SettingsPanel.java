/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: Dr5SettingsPanel.java
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
package net.sf.jdivelog.gui.dr5;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.NumberFormatter;

import net.sf.jdivelog.gui.LogbookChangeEvent;
import net.sf.jdivelog.gui.LogbookChangeListener;
import net.sf.jdivelog.gui.LogbookChangeNotifier;
import net.sf.jdivelog.gui.LogbookReference;
import net.sf.jdivelog.gui.MixField;
import net.sf.jdivelog.gui.commands.CommandManager;
import net.sf.jdivelog.gui.commands.UndoableCommand;
import net.sf.jdivelog.gui.resources.Messages;
import net.sf.jdivelog.model.JDiveLog;
import net.sf.jdivelog.model.Mix;
import net.sf.jdivelog.model.MixDatabase;
import net.sf.jdivelog.model.dr5.Dr5Settings;
import net.sf.jdivelog.model.dr5.Dr5Settings.DiveMode;
import net.sf.jdivelog.model.dr5.Dr5Settings.Dr5DiveView;
import net.sf.jdivelog.model.dr5.Dr5SettingsBuehlmann;
import net.sf.jdivelog.model.dr5.Dr5SettingsStorage;

public class Dr5SettingsPanel extends JPanel implements LogbookChangeListener {

    private static final int NUM_MIXES = 12;
    private final LogbookReference logbookRef;
    private int line;
    private JButton loadSettingsButton;
    private JButton saveSettingsButton;
    private JButton loadOxygenButton;
    private JButton saveOxygenButton;
    private JButton setDateButton;
    private JCheckBox flipScreen;
    private JCheckBox flipScreenNextStart;
    private JComboBox brightMin;
    private JComboBox brightMax;
    private JComboBox defaultView;
    private JComboBox defaultCcrView;
    private JFormattedTextField defaultFallback;
    private JTextField dr5PathField;
    private JButton dr5PathButton;
    private MixField[] mix;
    private JPanel settingsPanel;
    private JComboBox mode;
    private JFormattedTextField normalGfLo;
    private JFormattedTextField normalGfHi;
    private JFormattedTextField emergencyGfLo;
    private JFormattedTextField emergencyGfHi;
    private JFormattedTextField desaturationMultiplier;
    private JFormattedTextField saturationMultiplier;
    private JFormattedTextField gfLoPosMin;
    private JFormattedTextField gfLoPosMax;
    private JFormattedTextField lastStop;
    private JFormattedTextField safetyDistanceDecoStop;

    public Dr5SettingsPanel(LogbookReference logbookRef) {
        this.logbookRef = logbookRef;
        logbookRef.getLogbookChangeNotifier().addLogbookChangeListener(this);
        init();
        loadDr5Path();
        if (logbookRef.getLogBook() != null) {
            loadSettingsFromLogbook(logbookRef.getLogBook());
        }
    }

    @Override
    public void logbookChanged(LogbookChangeEvent e) {
        if (LogbookChangeEvent.EventType.LOGBOOK_LOADED.equals(e.getType())) {
            loadDr5Path();
            loadSettingsFromLogbook(logbookRef.getLogBook());
        }
    }

    private void loadDr5Path() {
        if (logbookRef.getLogBook() != null && logbookRef.getLogBook().getDr5Directory() != null) {
            getDr5PathField().setText(logbookRef.getLogBook().getDr5Directory());
        }
    }

    private void saveDr5Path() {
        CommandManager.getInstance().execute(new CommandSaveDr5Path());
    }

    private void init() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        addButtonPanel(new JLabel(Messages.getString("directory")), getDr5PathField(), getDr5PathButton());
        addButtonPanel(getLoadSettingsButton(), getSaveSettingsButton(), getLoadOxygenButton(), getSaveOxygenButton(), getSetDateButton());
        add(new JScrollPane(getSettingsPanel(), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
        line = 0;
        
        addTitle("system_settings");
        addLine("dive_mode", getMode());
        addLine("screen_orientation", getFlipScreen());
        addLine("screen_orientation", getFlipScreenNextStart());
        addLine("bright_min", getBrightMin());
        addLine("bright_max", getBrightMax());
        addLine("default_view", getDefaultView());
        addLine("default_ccr_view", getDefaultCcrView());
        addLine("default_fallback", getDefaultFallback());
        for (int i = 0; i < NUM_MIXES; i++) {
            addLine("gas", getMix(i));
        }
        
        addTitle("deco_settings");
        addLine("desaturation_percentage", getDesaturationMultiplier());
        addLine("saturation_percentage", getSaturationMultiplier());
        addLine("normal_gf_lo", getNormalGfLo());
        addLine("normal_gf_hi", getNormalGfHi());
        addLine("emergency_gf_lo", getEmergencyGfLo());
        addLine("emergency_gf_hi", getEmergencyGfHi());
        addLine("gf_lo_pos_min", getGfLoPosMin());
        addLine("gf_lo_pos_max", getGfLoPosMax());
        addLine("last_stop", getLastStop());
        addLine("safety_distance_to_decostop", getSafetyDistanceDecoStop());
    }

    private JComboBox getMode() {
        if (mode == null) {
            mode = new JComboBox(DiveMode.values());
        }
        return mode;
    }

    private MixField getMix(int idx) {
        if (mix == null) {
            mix = new MixField[NUM_MIXES];
        }
        if (mix[idx] == null) {
            mix[idx] = new MixField(SwingUtilities.getWindowAncestor(this), logbookRef.getGasDatabase());
        }
        return mix[idx];
    }

    private void loadSettingsFromLogbook(JDiveLog lb) {
        Dr5Settings s = lb.getDr5Settings();
        if (s == null) {
            s = new Dr5Settings();
        }
        getMode().setSelectedItem(s.getMode());
        getFlipScreen().setSelected(s.isFlipScreen());
        getFlipScreenNextStart().setSelected(s.isFlipScreenNextStart());
        getBrightMin().setSelectedItem(s.getBrightMin());
        getBrightMax().setSelectedItem(s.getBrightMax());
        getDefaultView().setSelectedItem(s.getDefaultView());
        getDefaultCcrView().setSelectedItem(s.getDefaultCcrView());
        getDefaultFallback().setText(String.valueOf(s.getDefaultViewFallback()));
        for (int i = 0; i < NUM_MIXES; i++) {
            getMix(i).setMix(s.getMixes()[i]);
        }
        if (s.getBuehlmann() != null) {
            Dr5SettingsBuehlmann b = s.getBuehlmann();
            getDesaturationMultiplier().setValue(b.getDesaturationMultiplier());
            getSaturationMultiplier().setValue(b.getSaturationMultiplier());
            getNormalGfLo().setValue(b.getNormalGfLo());
            getNormalGfHi().setValue(b.getNormalGfHi());
            getEmergencyGfLo().setValue(b.getEmergencyGfLo());
            getEmergencyGfHi().setValue(b.getEmergencyGfHi());
            getGfLoPosMin().setValue(b.getGfLoPosMin());
            getGfLoPosMax().setValue(b.getGfLoPosMax());
            getLastStop().setValue(b.getLastStop());
            getSafetyDistanceDecoStop().setValue(b.getSafetyDistanceDecoStop());
        }
    }

    private JTextField getDr5PathField() {
        if (dr5PathField == null) {
            dr5PathField = new JTextField(60);
            dr5PathField.getDocument().addDocumentListener(new DocumentListener() {

                @Override
                public void removeUpdate(DocumentEvent e) {
                    saveDr5Path();
                }

                @Override
                public void insertUpdate(DocumentEvent e) {
                    saveDr5Path();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    saveDr5Path();
                }
            });
        }
        return dr5PathField;
    }

    private JButton getDr5PathButton() {
        if (dr5PathButton == null) {
            dr5PathButton = new JButton(new ImageIcon(getClass().getResource("/net/sf/jdivelog/gui/resources/icons/open.gif"))); //$NON-NLS-1$
            dr5PathButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    JFileChooser fc = new JFileChooser();
                    File f = new File(getDr5PathField().getText());
                    if (f.exists() && f.isDirectory()) {
                        fc.setCurrentDirectory(f);
                    }
                    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    int ret = fc.showOpenDialog(Dr5SettingsPanel.this);
                    if (ret == JFileChooser.APPROVE_OPTION) {
                        getDr5PathField().setText(fc.getSelectedFile().getPath());
                    }
                }

            });
        }
        return dr5PathButton;
    }

    private File getDr5Path() {
        return new File(getDr5PathField().getText());
    }

    private void loadSettings() {
        CommandManager.getInstance().execute(new CommandLoadSettings());
    }

    private void saveSettings() {
        CommandManager.getInstance().execute(new CommandSaveSettings());
    }

    private JButton getLoadSettingsButton() {
        if (loadSettingsButton == null) {
            loadSettingsButton = new JButton(Messages.getString("load_settings"));
            loadSettingsButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    loadSettings();
                }
            });
        }
        return loadSettingsButton;
    }

    private JButton getSaveSettingsButton() {
        if (saveSettingsButton == null) {
            saveSettingsButton = new JButton(Messages.getString("save_settings"));
            saveSettingsButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    saveSettings();
                }
            });
        }
        return saveSettingsButton;
    }

    private JButton getLoadOxygenButton() {
        if (loadOxygenButton == null) {
            loadOxygenButton = new JButton(Messages.getString("load_oxygen"));
        }
        return loadOxygenButton;
    }

    private JButton getSaveOxygenButton() {
        if (saveOxygenButton == null) {
            saveOxygenButton = new JButton(Messages.getString("save_oxygen"));
        }
        return saveOxygenButton;
    }
    
    private JButton getSetDateButton() {
        if (setDateButton == null) {
            setDateButton = new JButton(Messages.getString("set_time"));
            setDateButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Dr5SettingsStorage ss = Dr5SettingsStorage.detect(getDr5Path());
                    ss.setDate();
                }
            });
        }
        return setDateButton;
    }

    private JFormattedTextField getDefaultFallback() {
        if (defaultFallback == null) {
            defaultFallback = createIntegerField(2, 0, 99);
        }
        return defaultFallback;
    }
    
    private int getInt(JFormattedTextField f) {
        Object val = f.getValue();
        if (val instanceof Number) {
            Number n = (Number) val;
            return n.intValue();
        }
        return 0;
    }
    
    private JFormattedTextField getDesaturationMultiplier() {
        if (desaturationMultiplier == null) {
            desaturationMultiplier = createIntegerField(3, 1, 255);
        }
        return desaturationMultiplier;
    }
    
    private JFormattedTextField getSaturationMultiplier() {
        if (saturationMultiplier == null) {
            saturationMultiplier = createIntegerField(3, 1, 255);
        }
        return saturationMultiplier;
    }
    
    private JFormattedTextField getNormalGfLo() {
        if (normalGfLo == null) {
            normalGfLo = createIntegerField(2, 0, 99);
        }
        return normalGfLo;
    }
    
    private JFormattedTextField getNormalGfHi() {
        if (normalGfHi == null) {
            normalGfHi = createIntegerField(2, 0, 99);
        }
        return normalGfHi;
    }
    
    private JFormattedTextField getEmergencyGfLo() {
        if (emergencyGfLo == null) {
            emergencyGfLo = createIntegerField(2, 0, 99);
        }
        return emergencyGfLo;
    }
    
    private JFormattedTextField getEmergencyGfHi() {
        if (emergencyGfHi == null) {
            emergencyGfHi = createIntegerField(2, 0, 99);
        }
        return emergencyGfHi;
    }
    
    private JFormattedTextField getGfLoPosMin() {
        if (gfLoPosMin == null) {
            gfLoPosMin = createIntegerField(2, 0, 99);
        }
        return gfLoPosMin;
    }
    
    private JFormattedTextField getGfLoPosMax() {
        if (gfLoPosMax == null) {
            gfLoPosMax = createIntegerField(2, 0, 99);
        }
        return gfLoPosMax;
    }
    
    private JFormattedTextField getLastStop() {
        if (lastStop == null) {
            lastStop = createIntegerField(2, 1, 99);
        }
        return lastStop;
    }
    
    private JFormattedTextField getSafetyDistanceDecoStop() {
        if (safetyDistanceDecoStop == null) {
            safetyDistanceDecoStop = createIntegerField(2, 0, 99);
        }
        return safetyDistanceDecoStop;
    }

    private JFormattedTextField createIntegerField(final int cols, final int min, final int max) {
        DecimalFormat df = new DecimalFormat("");
        df.setMaximumFractionDigits(0);
        df.setMaximumIntegerDigits(cols);
        df.setGroupingUsed(false);
        JFormattedTextField f = new JFormattedTextField(new NumberFormatter(df));
        f.setColumns(cols);
        f.setInputVerifier(new InputVerifier() {

            @Override
            public boolean verify(JComponent comp) {
                JTextField f = (JTextField) comp;
                String str = f.getText();
                try {
                    int num = Integer.parseInt(str);
                    return num >= min && num <= max;
                } catch (NumberFormatException e) {
                }
                return false;
            }

        });
        return f;
    }

    private JCheckBox getFlipScreen() {
        if (flipScreen == null) {
            flipScreen = new JCheckBox(Messages.getString("flipscreen"));
        }
        return flipScreen;
    }

    private JCheckBox getFlipScreenNextStart() {
        if (flipScreenNextStart == null) {
            flipScreenNextStart = new JCheckBox(Messages.getString("flipscreen_next_start"));
        }
        return flipScreenNextStart;
    }

    private JComboBox getBrightMin() {
        if (brightMin == null) {
            brightMin = new JComboBox(new Object[] { 0, 1, 2, 3 });
        }
        return brightMin;
    }

    private JComboBox getBrightMax() {
        if (brightMax == null) {
            brightMax = new JComboBox(new Object[] { 0, 1, 2, 3 });
        }
        return brightMax;
    }

    private JComboBox getDefaultView() {
        if (defaultView == null) {
            defaultView = new JComboBox(createViewList());
        }
        return defaultView;
    }

    private JComboBox getDefaultCcrView() {
        if (defaultCcrView == null) {
            defaultCcrView = new JComboBox(createViewList());
        }
        return defaultCcrView;
    }

    private Object[] createViewList() {
        return Dr5Settings.Dr5DiveView.values();
    }

    private JPanel getSettingsPanel() {
        if (settingsPanel == null) {
            settingsPanel = new JPanel();
            settingsPanel.setLayout(new GridBagLayout());
        }
        return settingsPanel;
    }

    private void addLine(String labelKey, JComponent comp) {
        GridBagConstraints gc = createGridBagConstraints();
        String label = Messages.getString(labelKey);
        getSettingsPanel().add(new JLabel(label), gc);
        gc.gridx = 1;
        getSettingsPanel().add(comp, gc);
    }

    private GridBagConstraints createGridBagConstraints() {
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = line++;
        gc.anchor = GridBagConstraints.NORTHWEST;
        gc.insets = new Insets(2, 2, 2, 2);
        return gc;
    }

    private void addButtonPanel(JComponent... buttons) {
        JPanel bp = new JPanel();
        bp.setLayout(new FlowLayout(FlowLayout.LEFT));
        for (JComponent b : buttons) {
            bp.add(b);
        }
        add(bp);
    }

    private void addTitle(String labelKey) {
        GridBagConstraints gc = createGridBagConstraints();
        gc.gridwidth = 2;
        JLabel label = new JLabel(Messages.getString(labelKey));
        Font f = label.getFont();
        f = f.deriveFont((float) 4.0 + f.getSize());
        label.setFont(f);
        getSettingsPanel().add(label, gc);
    }

    public static void main(String[] args) {
        String path = null;
        if (args.length == 1) {
            path = args[0];
        }
        final DummyLogbookReference logbookRef = new DummyLogbookReference();
        logbookRef.getLogBook().setDr5Directory(path);
        Runnable r = new Runnable() {

            public void run() {
                JFrame f = new JFrame();
                f.setContentPane(new Dr5SettingsPanel(logbookRef));
                f.setVisible(true);
                f.pack();
                f.addWindowListener(new WindowAdapter() {

                    @Override
                    public void windowClosing(WindowEvent e) {
                        super.windowClosing(e);
                        System.exit(0);
                    }
                });
            }
        };
        SwingUtilities.invokeLater(r);
    }

    private static class DummyLogbookReference implements LogbookReference {

        private final JDiveLog logbook = new JDiveLog();
        private final LogbookChangeNotifier notifier = new LogbookChangeNotifier();
        private final MixDatabase mixDatabase = new MixDatabase() {

            private final HashSet<Mix> mixes = new HashSet<Mix>();

            @Override
            public void removeFavorite(Mix m) {
                mixes.remove(m);
            }

            @Override
            public List<Mix> getFavorites() {
                return new ArrayList<Mix>(mixes);
            }

            @Override
            public void addFavorite(Mix m) {
                mixes.add(m);
            }
        };

        @Override
        public JDiveLog getLogBook() {
            return logbook;
        }

        @Override
        public LogbookChangeNotifier getLogbookChangeNotifier() {
            return notifier;
        }

        @Override
        public MixDatabase getGasDatabase() {
            return mixDatabase;
        }

    }

    private class CommandSaveSettings implements UndoableCommand {

        private boolean oldChanged;
        private DiveMode oldMode;
        private boolean oldFlipscreen;
        private boolean oldFlipscreenNextStart;
        private int oldBrightMin;
        private int oldBrightMax;
        private Dr5DiveView oldDefaultView;
        private Dr5DiveView oldDefaultCcrView;
        private int oldDefaultViewFallback;
        private Mix[] oldMixes;
        private int oldDesaturationMultiplier;
        private int oldSaturationMultiplier;
        private int oldNormalGfLo;
        private int oldNormalGfHi;
        private int oldEmergencyGfLo;
        private int oldEmergencyGfHi;
        private int oldGfLoPosMin;
        private int oldGfLoPosMax;
        private int oldLastStop;
        private int oldSafetyDistanceDecoStop;
        private DiveMode newMode;
        private boolean newFlipscreen;
        private boolean newFlipscreenNextStart;
        private int newBrightMin;
        private int newBrightMax;
        private Dr5DiveView newDefaultView;
        private Dr5DiveView newDefaultCcrView;
        private int newDefaultViewFallback;
        private Mix[] newMixes;
        private int newDesaturationMultiplier;
        private int newSaturationMultiplier;
        private int newNormalGfLo;
        private int newNormalGfHi;
        private int newEmergencyGfLo;
        private int newEmergencyGfHi;
        private int newGfLoPosMin;
        private int newGfLoPosMax;
        private int newLastStop;
        private int newSafetyDistanceDecoStop;

        @Override
        public void execute() {
            oldChanged = logbookRef.getLogbookChangeNotifier().isChanged();
            Dr5Settings oldSettings = logbookRef.getLogBook().getDr5Settings();
            if (logbookRef.getLogBook() != null && logbookRef.getLogBook().getDr5Settings() != null) {
                oldMode = oldSettings.getMode();
                oldFlipscreen = oldSettings.isFlipScreen();
                oldFlipscreenNextStart = oldSettings.isFlipScreenNextStart();
                oldBrightMin = oldSettings.getBrightMin();
                oldBrightMax = oldSettings.getBrightMax();
                oldDefaultView = oldSettings.getDefaultView();
                oldDefaultCcrView = oldSettings.getDefaultCcrView();
                oldDefaultViewFallback = oldSettings.getDefaultViewFallback();
                oldMixes = new Mix[NUM_MIXES];
                for (int i = 0; i < NUM_MIXES; i++) {
                    if (i < oldSettings.getMixes().length) {
                        oldMixes[i] = oldSettings.getMixes()[i];
                    }
                }
                Dr5SettingsBuehlmann oldBuehlmann = oldSettings.getBuehlmann();
                if (oldBuehlmann != null) {
                    oldDesaturationMultiplier = oldBuehlmann.getDesaturationMultiplier();
                    oldSaturationMultiplier = oldBuehlmann.getSaturationMultiplier();
                    oldNormalGfLo = oldBuehlmann.getNormalGfLo();
                    oldNormalGfHi= oldBuehlmann.getNormalGfHi();
                    oldEmergencyGfLo = oldBuehlmann.getEmergencyGfLo();
                    oldEmergencyGfHi = oldBuehlmann.getEmergencyGfHi();
                    oldGfLoPosMin = oldBuehlmann.getGfLoPosMin();
                    oldGfLoPosMax = oldBuehlmann.getGfLoPosMax();
                    oldLastStop = oldBuehlmann.getLastStop();
                    oldSafetyDistanceDecoStop = oldBuehlmann.getSafetyDistanceDecoStop();
                }

            }
            newMode = (DiveMode) getMode().getSelectedItem();
            newFlipscreen = getFlipScreen().isSelected();
            newFlipscreenNextStart = getFlipScreenNextStart().isSelected();
            newBrightMin = ((Integer) getBrightMin().getSelectedItem()).intValue();
            newBrightMax = ((Integer) getBrightMax().getSelectedItem()).intValue();
            newDefaultView = (Dr5DiveView) getDefaultView().getSelectedItem();
            newDefaultCcrView = (Dr5DiveView) getDefaultCcrView().getSelectedItem();
            newDefaultViewFallback = getInt(getDefaultFallback());
            newMixes = new Mix[NUM_MIXES];
            for (int i = 0; i < NUM_MIXES; i++) {
                newMixes[i] = getMix(i).getMix();
            }
            newDesaturationMultiplier = getInt(getDesaturationMultiplier());
            newSaturationMultiplier = getInt(getSaturationMultiplier());
            newNormalGfLo = getInt(getNormalGfLo());
            newNormalGfHi = getInt(getNormalGfHi());
            newEmergencyGfLo = getInt(getEmergencyGfLo());
            newEmergencyGfHi = getInt(getEmergencyGfHi());
            newGfLoPosMin = getInt(getGfLoPosMin());
            newGfLoPosMax = getInt(getGfLoPosMax());
            newLastStop = getInt(getLastStop());
            newSafetyDistanceDecoStop = getInt(getSafetyDistanceDecoStop());
            redo();
        }

        @Override
        public void undo() {
            Dr5SettingsStorage ss = Dr5SettingsStorage.detect(getDr5Path());
            Dr5Settings s = ss.readSettings();
            updateSettingsNew(s);
            ss.writeSettings(s);
            JDiveLog lb = logbookRef.getLogBook();
            if (lb == null) {
                lb = new JDiveLog();
            }
            if (lb.getDr5Settings() == null) {
                lb.setDr5Settings(new Dr5Settings());
            }
            s = lb.getDr5Settings();
            updateSettingsOld(s);
            loadSettingsFromLogbook(lb);
            logbookRef.getLogbookChangeNotifier().setChanged(oldChanged);
        }

        @Override
        public void redo() {
            Dr5SettingsStorage ss = Dr5SettingsStorage.detect(getDr5Path());
            Dr5Settings s = ss.readSettings();
            updateSettingsNew(s);
            ss.writeSettings(s);
            JDiveLog lb = logbookRef.getLogBook();
            if (lb == null) {
                lb = new JDiveLog();
            }
            if (lb.getDr5Settings() == null) {
                lb.setDr5Settings(new Dr5Settings());
            }
            s = lb.getDr5Settings();
            updateSettingsNew(s);
            loadSettingsFromLogbook(lb);
            logbookRef.getLogbookChangeNotifier().setChanged(true);
        }

        private void updateSettingsOld(Dr5Settings s) {
            s.setMode(oldMode);
            s.setFlipScreen(oldFlipscreen);
            s.setFlipScreenNextStart(oldFlipscreenNextStart);
            s.setBrightMin(oldBrightMin);
            s.setBrightMax(oldBrightMax);
            s.setDefaultView(oldDefaultView);
            s.setDefaultCcrView(oldDefaultCcrView);
            s.setDefaultViewFallback(oldDefaultViewFallback);
            s.setMixes(oldMixes);
            if (s.getBuehlmann() == null) {
                s.setBuehlmann(new Dr5SettingsBuehlmann());
            }
            Dr5SettingsBuehlmann b = s.getBuehlmann();
            b.setDesaturationMultiplier(oldDesaturationMultiplier);
            b.setSaturationMultiplier(oldSaturationMultiplier);
            b.setNormalGfLo(oldNormalGfLo);
            b.setNormalGfHi(oldNormalGfHi);
            b.setEmergencyGfLo(oldEmergencyGfLo);
            b.setEmergencyGfHi(oldEmergencyGfHi);
            b.setGfLoPosMin(oldGfLoPosMin);
            b.setGfLoPosMax(oldGfLoPosMax);
            b.setLastStop(oldLastStop);
            b.setSafetyDistanceDecoStop(oldSafetyDistanceDecoStop);
        }

        private void updateSettingsNew(Dr5Settings s) {
            s.setMode(newMode);
            s.setFlipScreen(newFlipscreen);
            s.setFlipScreenNextStart(newFlipscreenNextStart);
            s.setBrightMin(newBrightMin);
            s.setBrightMax(newBrightMax);
            s.setDefaultView(newDefaultView);
            s.setDefaultCcrView(newDefaultCcrView);
            s.setDefaultViewFallback(newDefaultViewFallback);
            s.setMixes(newMixes);
            Dr5SettingsBuehlmann b = s.getBuehlmann();
            b.setDesaturationMultiplier(newDesaturationMultiplier);
            b.setSaturationMultiplier(newSaturationMultiplier);
            b.setNormalGfLo(newNormalGfLo);
            b.setNormalGfHi(newNormalGfHi);
            b.setEmergencyGfLo(newEmergencyGfLo);
            b.setEmergencyGfHi(newEmergencyGfHi);
            b.setGfLoPosMin(newGfLoPosMin);
            b.setGfLoPosMax(newGfLoPosMax);
            b.setLastStop(newLastStop);
            b.setSafetyDistanceDecoStop(newSafetyDistanceDecoStop);
        }

    }

    private class CommandLoadSettings implements UndoableCommand {

        private boolean oldChanged;
        private DiveMode oldMode;
        private boolean oldFlipscreen;
        private boolean oldFlipscreenNextStart;
        private int oldBrightMin;
        private int oldBrightMax;
        private Dr5DiveView oldDefaultView;
        private Dr5DiveView oldDefaultCcrView;
        private int oldDefaultViewFallback;
        private Mix[] oldMixes;
        private int oldDesaturationMultiplier;
        private int oldSaturationMultiplier;
        private int oldNormalGfLo;
        private int oldNormalGfHi;
        private int oldEmergencyGfLo;
        private int oldEmergencyGfHi;
        private int oldGfLoPosMin;
        private int oldGfLoPosMax;
        private int oldLastStop;
        private int oldSafetyDistanceDecoStop;
        private DiveMode newMode;
        private boolean newFlipscreen;
        private boolean newFlipscreenNextStart;
        private int newBrightMin;
        private int newBrightMax;
        private Dr5DiveView newDefaultView;
        private Dr5DiveView newDefaultCcrView;
        private int newDefaultViewFallback;
        private Mix[] newMixes;
        private int newDesaturationMultiplier;
        private int newSaturationMultiplier;
        private int newNormalGfLo;
        private int newNormalGfHi;
        private int newEmergencyGfLo;
        private int newEmergencyGfHi;
        private int newGfLoPosMin;
        private int newGfLoPosMax;
        private int newLastStop;
        private int newSafetyDistanceDecoStop;

        @Override
        public void execute() {
            oldChanged = logbookRef.getLogbookChangeNotifier().isChanged();
            Dr5Settings oldSettings = logbookRef.getLogBook().getDr5Settings();
            if (logbookRef.getLogBook() != null && logbookRef.getLogBook().getDr5Settings() != null) {
                oldMode = oldSettings.getMode();
                oldFlipscreen = oldSettings.isFlipScreen();
                oldFlipscreenNextStart = oldSettings.isFlipScreenNextStart();
                oldBrightMin = oldSettings.getBrightMin();
                oldBrightMax = oldSettings.getBrightMax();
                oldDefaultView = oldSettings.getDefaultView();
                oldDefaultCcrView = oldSettings.getDefaultCcrView();
                oldDefaultViewFallback = oldSettings.getDefaultViewFallback();
                oldMixes = new Mix[NUM_MIXES];
                for (int i = 0; i < NUM_MIXES; i++) {
                    if (i < oldSettings.getMixes().length) {
                        oldMixes[i] = oldSettings.getMixes()[i];
                    }
                }
                Dr5SettingsBuehlmann oldBuehlmann = oldSettings.getBuehlmann();
                if (oldBuehlmann != null) {
                    oldDesaturationMultiplier = oldBuehlmann.getDesaturationMultiplier();
                    oldSaturationMultiplier = oldBuehlmann.getSaturationMultiplier();
                    oldNormalGfLo = oldBuehlmann.getNormalGfLo();
                    oldNormalGfHi= oldBuehlmann.getNormalGfHi();
                    oldEmergencyGfLo = oldBuehlmann.getEmergencyGfLo();
                    oldEmergencyGfHi = oldBuehlmann.getEmergencyGfHi();
                    oldGfLoPosMin = oldBuehlmann.getGfLoPosMin();
                    oldGfLoPosMax = oldBuehlmann.getGfLoPosMax();
                    oldLastStop = oldBuehlmann.getLastStop();
                    oldSafetyDistanceDecoStop = oldBuehlmann.getSafetyDistanceDecoStop();
                }
            }
            Dr5SettingsStorage ss = Dr5SettingsStorage.detect(getDr5Path());
            Dr5Settings settings = ss.readSettings();
            newMode = settings.getMode();
            newFlipscreen = settings.isFlipScreen();
            newFlipscreenNextStart = settings.isFlipScreenNextStart();
            newBrightMin = settings.getBrightMin();
            newBrightMax = settings.getBrightMax();
            newDefaultView = settings.getDefaultView();
            newDefaultCcrView = settings.getDefaultCcrView();
            newDefaultViewFallback = settings.getDefaultViewFallback();
            newMixes = new Mix[NUM_MIXES];
            for (int i = 0; i < NUM_MIXES; i++) {
                if (i < settings.getMixes().length) {
                    newMixes[i] = settings.getMixes()[i];
                }
            }
            Dr5SettingsBuehlmann newBuehlmann = settings.getBuehlmann();
            if (newBuehlmann != null) {
                newDesaturationMultiplier = newBuehlmann.getDesaturationMultiplier();
                newSaturationMultiplier = newBuehlmann.getSaturationMultiplier();
                newNormalGfLo = newBuehlmann.getNormalGfLo();
                newNormalGfHi= newBuehlmann.getNormalGfHi();
                newEmergencyGfLo = newBuehlmann.getEmergencyGfLo();
                newEmergencyGfHi = newBuehlmann.getEmergencyGfHi();
                newGfLoPosMin = newBuehlmann.getGfLoPosMin();
                newGfLoPosMax = newBuehlmann.getGfLoPosMax();
                newLastStop = newBuehlmann.getLastStop();
                newSafetyDistanceDecoStop = newBuehlmann.getSafetyDistanceDecoStop();
            }

            redo();
        }

        @Override
        public void undo() {
            JDiveLog lb = logbookRef.getLogBook();
            if (lb == null) {
                lb = new JDiveLog();
            }
            if (lb.getDr5Settings() == null) {
                lb.setDr5Settings(new Dr5Settings());
            }
            Dr5Settings s = lb.getDr5Settings();
            s.setMode(oldMode);
            s.setFlipScreen(oldFlipscreen);
            s.setFlipScreenNextStart(oldFlipscreenNextStart);
            s.setBrightMin(oldBrightMin);
            s.setBrightMax(oldBrightMax);
            s.setDefaultView(oldDefaultView);
            s.setDefaultCcrView(oldDefaultCcrView);
            s.setDefaultViewFallback(oldDefaultViewFallback);
            s.setMixes(oldMixes);
            Dr5SettingsBuehlmann b = s.getBuehlmann();
            b.setDesaturationMultiplier(oldDesaturationMultiplier);
            b.setSaturationMultiplier(oldSaturationMultiplier);
            b.setNormalGfLo(oldNormalGfLo);
            b.setNormalGfHi(oldNormalGfHi);
            b.setEmergencyGfLo(oldEmergencyGfLo);
            b.setEmergencyGfHi(oldEmergencyGfHi);
            b.setGfLoPosMin(oldGfLoPosMin);
            b.setGfLoPosMax(oldGfLoPosMax);
            b.setLastStop(oldLastStop);
            b.setSafetyDistanceDecoStop(oldSafetyDistanceDecoStop);
            loadSettingsFromLogbook(lb);
            logbookRef.getLogbookChangeNotifier().setChanged(oldChanged);
        }

        @Override
        public void redo() {
            JDiveLog lb = logbookRef.getLogBook();
            if (lb == null) {
                lb = new JDiveLog();
            }
            if (lb.getDr5Settings() == null) {
                lb.setDr5Settings(new Dr5Settings());
            }
            Dr5Settings s = lb.getDr5Settings();
            s.setMode(newMode);
            s.setFlipScreen(newFlipscreen);
            s.setFlipScreenNextStart(newFlipscreenNextStart);
            s.setBrightMin(newBrightMin);
            s.setBrightMax(newBrightMax);
            s.setDefaultView(newDefaultView);
            s.setDefaultCcrView(newDefaultCcrView);
            s.setDefaultViewFallback(newDefaultViewFallback);
            s.setMixes(newMixes);
            if (s.getBuehlmann() == null) {
                s.setBuehlmann(new Dr5SettingsBuehlmann());
            }
            Dr5SettingsBuehlmann b = s.getBuehlmann();
            b.setDesaturationMultiplier(newDesaturationMultiplier);
            b.setSaturationMultiplier(newSaturationMultiplier);
            b.setNormalGfLo(newNormalGfLo);
            b.setNormalGfHi(newNormalGfHi);
            b.setEmergencyGfLo(newEmergencyGfLo);
            b.setEmergencyGfHi(newEmergencyGfHi);
            b.setGfLoPosMin(newGfLoPosMin);
            b.setGfLoPosMax(newGfLoPosMax);
            b.setLastStop(newLastStop);
            b.setSafetyDistanceDecoStop(newSafetyDistanceDecoStop);
            loadSettingsFromLogbook(lb);
            logbookRef.getLogbookChangeNotifier().setChanged(true);
        }

    }

    private class CommandSaveDr5Path implements UndoableCommand {

        private String oldPath;
        private String newPath;
        private boolean oldChanged;

        @Override
        public void execute() {
            if (logbookRef != null && logbookRef.getLogBook() != null) {
                oldChanged = logbookRef.getLogbookChangeNotifier().isChanged();
                oldPath = logbookRef.getLogBook().getDr5Directory();
                newPath = getDr5Path() != null ? getDr5Path().getAbsolutePath() : null;
                redo();
            }
        }

        @Override
        public void undo() {
            if (logbookRef != null && logbookRef.getLogBook() != null) {
                logbookRef.getLogBook().setDr5Directory(oldPath);
                logbookRef.getLogbookChangeNotifier().setChanged(oldChanged);
            }
        }

        @Override
        public void redo() {
            if (logbookRef != null && logbookRef.getLogBook() != null) {
                boolean changed = (oldPath == null && newPath != null) || (oldPath != null && newPath == null) || (oldPath != null && !oldPath.equals(newPath));
                if (changed) {
                    logbookRef.getLogBook().setDr5Directory(newPath);
                    logbookRef.getLogbookChangeNotifier().setChanged(true);
                }
            }
        }

    }

}
