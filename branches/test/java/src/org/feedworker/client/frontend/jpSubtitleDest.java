package org.feedworker.client.frontend;

//IMPORT JAVA
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;

import org.jfacility.javax.swing.Swing;

/**
 * 
 * 
 * @author luca
 */
class jpSubtitleDest extends jpAbstract {

    private static jpSubtitleDest jpanel = null;
    private jtSubtitleDest jtable;

    public static jpSubtitleDest getPanel() {
        if (jpanel == null)
            jpanel = new jpSubtitleDest();
        return jpanel;
    }

    private jpSubtitleDest() {
        super();
        initializePanel();
        initializeButtons();
    }

    @Override
    void initializePanel() {
        jtable = new jtSubtitleDest(proxy.getNameTableSubtitleDest());
        JScrollPane jScrollTable1 = new JScrollPane(jtable);
        jScrollTable1.setAutoscrolls(true);
        add(jScrollTable1, BorderLayout.CENTER);
        setVisible(true);
        proxy.setTableListener(jtable);
    }

    @Override
    void initializeButtons() {
        JButton jbAddRow = new JButton(" + ");
        jbAddRow.setToolTipText("Aggiungi riga alla tabella");
        jbAddRow.setBorder(BORDER);
        jbAddRow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbAddRowMouseClicked();
            }
        });

        JButton jbRemoveRow = new JButton(" - ");
        jbRemoveRow.setToolTipText("Rimuovi riga selezionata dalla tabella");
        jbRemoveRow.setBorder(BORDER);
        jbRemoveRow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbRemoveRowMouseClicked();
            }
        });

        JButton jbSaveRole = new JButton(" Salva ");
        jbSaveRole.setToolTipText("Salva impostazioni");
        jbSaveRole.setBorder(BORDER);
        jbSaveRole.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbSaveRoleMouseClicked();
            }
        });

        JButton jbAddDir = new JButton(" Destinazione ");
        jbAddDir.setToolTipText("");
        jbAddDir.setBorder(BORDER);
        jbAddDir.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbAddDirMouseClicked();
            }
        });

        JLabel jlTemp = new JLabel();
        jlTemp.setPreferredSize(new Dimension(700, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = BUTTON_SPACE_INSETS;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        actionJP.add(jbAddRow, gbc);
        gbc.gridx = 1;
        actionJP.add(jbRemoveRow, gbc);
        gbc.gridx = 2;
        actionJP.add(jbSaveRole, gbc);
        gbc.gridx = 3;
        actionJP.add(jbAddDir, gbc);
        gbc.gridx = 4;
        actionJP.add(jlTemp, gbc);

        add(actionJP, BorderLayout.NORTH);
    }

    private void jbAddRowMouseClicked() {
        DefaultTableModel dtm = (DefaultTableModel) jtable.getModel();
        dtm.insertRow(0, new Object[]{null, 1, "normale", null, null, null, false});
    }

    private void jbSaveRoleMouseClicked() {
        proxy.saveRules(jtable);
    }

    private void jbRemoveRowMouseClicked() {
        int row = jtable.getSelectedRow();
        if (row > -1)
            ((DefaultTableModel) jtable.getModel()).removeRow(jtable.convertRowIndexToModel(row));
    }

    private void jbAddDirMouseClicked() {
        int row = jtable.getSelectedRow();
        if (row > -1) {
            String dir = Swing.getDirectory(this, "");
            if (dir != null) {
                DefaultTableModel dtm = (DefaultTableModel) jtable.getModel();
                dtm.setValueAt(dir, row, 3);
            }
        }
    }
}