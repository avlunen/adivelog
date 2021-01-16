/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: SalinityFixDialog.java
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
package net.sf.jdivelog.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.NumberFormat;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import net.sf.jdivelog.gui.commands.CommandManager;
import net.sf.jdivelog.gui.commands.SalinityFixCommand;
import net.sf.jdivelog.gui.resources.Messages;
import net.sf.jdivelog.model.JDive;

import org.apache.batik.ext.swing.GridBagConstants;

/**
 * Utility to fix the salinity of a dive (UI Representation)
 * 
 * @author Pascal Pellmont <jdivelog@pellmont.dyndns.org>
 */
public class SalinityFixDialog extends JDialog {
    
    private static final long serialVersionUID = 1L;
    private final MainWindow mainWindow;
    private final List<JDive> dives;
    private JFormattedTextField oldSalinity;
    private JFormattedTextField newSalinity;
    private JPanel buttonPanel;
    private JButton closeButton;
    private JButton cancelButton;

    public SalinityFixDialog(MainWindow mainWindow, List<JDive> dives) {
        super(mainWindow, ModalityType.APPLICATION_MODAL);
        setTitle(Messages.getString("salinityfix.title"));
        setSize(400, 300);
        this.mainWindow = mainWindow;
        this.dives = dives;
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.ipadx = 0;
        gc.fill = GridBagConstants.BOTH;
        gc.anchor = GridBagConstants.CENTER;
        gc.gridy = 0;
        gc.gridx = 0;
        gc.gridwidth = 2;
        gc.weighty = 1;
        gc.weightx = 1;
        gc.insets = new Insets(5,5,5,5);
        JTextArea info = new JTextArea(Messages.getString("salinityfix.instructions"));
        info.setEditable(false);
        info.setLineWrap(true);
        info.setWrapStyleWord(true);
        panel.add(info, gc);
        gc.weighty = 0;
        gc.gridwidth = 1;
        gc.gridy = 1;
        gc.gridx = 0;
        panel.add(new JLabel(Messages.getString("salinityfix.oldsalinity")), gc);
        gc.gridx = 1;
        panel.add(getOldSalinity(), gc);
        gc.gridy = 2;
        gc.gridx = 0;
        panel.add(new JLabel(Messages.getString("salinityfix.newsalinity")), gc);
        gc.gridx = 1;
        panel.add(getNewSalinity(), gc);
        gc.gridy = 3;
        gc.gridx = 0;
        gc.gridwidth = 2;
        panel.add(getButtonPanel(), gc);
        getContentPane().add(panel);
        getOldSalinity().setValue(Double.valueOf(1.0));
        getNewSalinity().setValue(Double.valueOf(1.0));
        new MnemonicFactory(this);
    }

    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.weightx = 1;
            buttonPanel = new JPanel();
            buttonPanel.setLayout(new GridBagLayout());
            buttonPanel
                    .setComponentOrientation(java.awt.ComponentOrientation.LEFT_TO_RIGHT);
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.gridy = 0;
            gridBagConstraints1.insets = new java.awt.Insets(5, 50, 5, 5);
            buttonPanel.add(getCloseButton(), gridBagConstraints1);
            gridBagConstraints1.gridx = 1;
            gridBagConstraints1.gridy = 0;
            gridBagConstraints1.insets = new java.awt.Insets(5, 5, 5, 50);            
            buttonPanel.add(getCancelButton(), gridBagConstraints1);
        }
        return buttonPanel;
    }

    private JButton getCloseButton() {
        if (closeButton == null) {
            closeButton = new JButton();
            closeButton.setText(Messages.getString("close")); //$NON-NLS-1$
            closeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    close();
                }
            });
        }
        return closeButton;
    }

    private JButton getCancelButton() {
        if (cancelButton == null) {
            cancelButton = new JButton();
            cancelButton.setText(Messages.getString("cancel")); //$NON-NLS-1$
            cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    cancel();
                }
            });
        }
        return cancelButton;
    }
    
    private void close() {
        Number os = (Number) getOldSalinity().getValue();
        Number ns = (Number) getNewSalinity().getValue();
        if (os == null || os.doubleValue() == 0.0) {
            JOptionPane.showConfirmDialog(this, Messages.getString("salinityfix.nooldsalinity"));
        } else if (ns == null || ns.doubleValue() == 0.0) {
            JOptionPane.showConfirmDialog(this, Messages.getString("salinityfix.nonewsalinity"));
        } else {
            SalinityFixCommand cmd = new SalinityFixCommand(mainWindow, dives, os.doubleValue(), ns.doubleValue());
            CommandManager.getInstance().execute(cmd);
            cancel();
        }
    }
    
    private void cancel() {
        dispose();
    }

    private JFormattedTextField getOldSalinity() {
        if (oldSalinity == null) {
            NumberFormat nf = NumberFormat.getNumberInstance();
            nf.setMinimumFractionDigits(2);
            oldSalinity = new JFormattedTextField(nf);
            oldSalinity.addFocusListener(new FocusAdapter() {
               @Override
                public void focusGained(FocusEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            oldSalinity.selectAll();
                        }
                    });
                } 
            });
        }
        return oldSalinity;
    }
    
    private JFormattedTextField getNewSalinity() {
        if (newSalinity == null) {
            NumberFormat nf = NumberFormat.getNumberInstance();
            nf.setMinimumFractionDigits(2);
            newSalinity = new JFormattedTextField(nf);
            newSalinity.addFocusListener(new FocusAdapter() {
                @Override
                 public void focusGained(FocusEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            newSalinity.selectAll();
                        }
                    });
                 } 
             });
        }
        return newSalinity;
    }

}
