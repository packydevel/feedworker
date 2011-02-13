package org.feedworker.client.frontend.panel;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JScrollPane;

import org.feedworker.client.frontend.table.jtRss;
/**
 * Pannello Itasa
 * 
 * @author luca
 */
public class paneItasa extends paneAbstract {

    private static paneItasa jpanel = null;
    private JButton jbAllItasa, jbDown, jbClean, jbAllMyItasa;
    private jtRss jtItasa, jtMyItasa;

    /** Costruttore privato */
    private paneItasa() {
        super();
        setName("Italiansubs");
        initializePanel();
        initializeButtons();
        proxy.setTableListener(jtItasa);
        proxy.setTableListener(jtMyItasa);
    }

    /**
     * Restituisce l'istanza del pannello itasa
     *
     * @return pannello itasa
     */
    public static paneItasa getPanel() {
        if (jpanel == null)
            jpanel = new paneItasa();
        return jpanel;
    }

    @Override
    void initializePanel() {
        jtItasa = new jtRss(proxy.getItasa());
        jtItasa.setTitleDescriptionColumn("Sottotitolo Itasa");
        JScrollPane jScrollTable1 = new JScrollPane(jtItasa);
        jScrollTable1.setPreferredSize(TABLE_SCROLL_SIZE);
        jScrollTable1.setAutoscrolls(true);

        jtMyItasa = new jtRss(proxy.getMyItasa());
        jtMyItasa.setTitleDescriptionColumn("Sottotitolo MyItasa");
        JScrollPane jScrollTable2 = new JScrollPane(jtMyItasa);
        jScrollTable2.setPreferredSize(TABLE_SCROLL_SIZE);
        jScrollTable2.setAutoscrolls(true);

        jpCenter.add(jScrollTable1);
        jpCenter.add(RIGID_AREA);
        jpCenter.add(jScrollTable2);
        add(jpCenter, BorderLayout.CENTER);

        setVisible(true);
    }

    @Override
    void initializeButtons() {
        jbAllItasa = new JButton(" Tutti Itasa ");
        jbAllItasa.setToolTipText("Seleziona tutti i sub itasa");
        jbAllItasa.setBorder(BORDER);
        jbAllItasa.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                selectAll(jtItasa);
            }
        });

        jbDown = new JButton(" Download ");
        jbDown.setToolTipText("Scarica tutti i sub selezionati");
        jbDown.setBorder(BORDER);
        jbDown.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbDownMouseClicked();
            }
        });

        jbClean = new JButton(" Pulisci ");
        jbClean.setToolTipText("Pulisce le righe selezionate");
        jbClean.setBorder(BORDER);
        jbClean.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbCleanMouseClicked();
            }
        });

        jbAllMyItasa = new JButton(" Tutti MyItasa ");
        jbAllMyItasa.setToolTipText("Seleziona tutti i sub myItasa");
        jbAllMyItasa.setBorder(BORDER);
        jbAllMyItasa.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                selectAll(jtMyItasa);
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = BUTTON_SPACE_INSETS;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        jpAction.add(jbAllItasa, gbc);
        gbc.gridx = 1;
        jpAction.add(jbDown, gbc);
        gbc.gridx = 2;
        jpAction.add(jbClean, gbc);
        gbc.gridx = 3;
        jpAction.add(jbAllMyItasa, gbc);

        add(jpAction, BorderLayout.NORTH);
    }

    public void setButtonEnabled(boolean e) {
        jbDown.setEnabled(e);
        jbClean.setEnabled(e);
        jbAllItasa.setEnabled(e);
        jbAllMyItasa.setEnabled(e);
    }

    private void jbDownMouseClicked() {
        if (jbDown.isEnabled()) {
            proxy.downloadSub(jtItasa, jtMyItasa, true);
            jbCleanMouseClicked();
        }
    }

    private void selectAll(jtRss jt) {
        for (int i = 0; i < jt.getRowCount(); i++)
            jt.setValueAt(true, i, 3);
    }

    private void jbCleanMouseClicked() {
        proxy.cleanSelect(jtItasa);
        proxy.cleanSelect(jtMyItasa);
    }
}