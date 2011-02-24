package org.feedworker.client.frontend.panel;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;

import org.feedworker.client.frontend.table.tableCalendar;
import org.jfacility.java.lang.Lang;
/**
 *
 * @author luca
 */
public class paneCalendar extends paneAbstract{

    private static paneCalendar jpanel = null;
    private tableCalendar jtable;

    private paneCalendar(){
        super("Calendar");
        initializePanel();
        initializeButtons();
    }

    public static paneCalendar getPanel(){
        if (jpanel==null)
            jpanel = new paneCalendar();
        return jpanel;
    }

    @Override
    void initializePanel() {
        jtable = new tableCalendar(proxy.getNameTableCalendar());
        JScrollPane jScrollTable1 = new JScrollPane(jtable);
        jScrollTable1.setAutoscrolls(true);
        add(jScrollTable1, BorderLayout.CENTER);
        proxy.setTableListener(jtable);
        setVisible(true);
    }

    @Override
    void initializeButtons() {
        JButton jbAddRow = new JButton(" + ");
        jbAddRow.setToolTipText("Aggiungi riga/serie alla tabella");
        jbAddRow.setBorder(BORDER);
        jbAddRow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbAddRowMouseClicked();
            }
        });

        JButton jbRemoveRow = new JButton(" - ");
        jbRemoveRow.setToolTipText("Rimuovi riga/serie selezionata dalla tabella");
        jbRemoveRow.setBorder(BORDER);
        jbRemoveRow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbRemoveRowMouseClicked();
            }
        });
        
        JButton jbRemoveAll = new JButton(" Remove All ");
        jbRemoveAll.setToolTipText("Rimuove tutte le serie dalla tabella");
        jbRemoveAll.setBorder(BORDER);
        jbRemoveAll.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbRemoveAllRowsMouseClick();
            }
        });

        JButton jbRefresh = new JButton(" Aggiorna ");
        jbRefresh.setToolTipText("Aggiorna le informazioni sulle serie che hanno data "
                + "prossima puntata bianca o minore odierna");
        jbRefresh.setBorder(BORDER);
        jbRefresh.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbRefreshCalendarMouseClick();
            }
        });
        
        JButton jbSingleRefresh = new JButton(" Aggiorna Singolo ");
        jbSingleRefresh.setEnabled(false);
        jbSingleRefresh.setToolTipText("Aggiorna le informazioni sulla serie selezionata");
        jbSingleRefresh.setBorder(BORDER);
        jbSingleRefresh.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbRefreshSingleMouseClick();
            }
        });

        JButton jbImport = new JButton(" Importa ");
        jbImport.setToolTipText("Importa dai nomi serie di subtitle destination");
        jbImport.setBorder(BORDER);
        jbImport.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbImportCalendarMouseClick();
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = BUTTON_SPACE_INSETS;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        int x=1;
        jpAction.add(jbAddRow, gbc);
        gbc.gridx = x++;
        jpAction.add(jbRemoveRow, gbc);
        gbc.gridx = x++;
        jpAction.add(jbRemoveAll, gbc);
        gbc.gridx = x++;
        jpAction.add(jbRefresh, gbc);
        gbc.gridx = x++;
        jpAction.add(jbSingleRefresh, gbc);
        gbc.gridx = x++;
        jpAction.add(jbImport, gbc);
        
        add(jpAction, BorderLayout.NORTH);
    }

    private void jbAddRowMouseClicked() {
        String tv = JOptionPane.showInputDialog(null,"Inserire nome serie tv");
        if (Lang.verifyTextNotNull(tv))
            proxy.searchTV(tv);
    }

    private void jbRemoveRowMouseClicked() {
        int row = jtable.getSelectedRow();
        if (row > -1){
            row = jtable.convertRowIndexToModel(row);
            proxy.removeSingleShowCalendar(row, jtable.getValueAt(row, 0));
            ((DefaultTableModel) jtable.getModel()).removeRow(row);
        }
    }
    
    private void jbRemoveAllRowsMouseClick(){
        proxy.removeAllShowCalendar();
        jtable.removeAllRows();
    }
    
    private void jbRefreshCalendarMouseClick() {
        if (jtable.getRowCount()>0){
            ((DefaultTableModel) jtable.getModel()).setRowCount(0);
            proxy.refreshCalendar();
        }
    }

    private void jbRefreshSingleMouseClick(){
        int row = jtable.getSelectedRow();
        if (row > -1){
            row = jtable.convertRowIndexToModel(row);
            //TODO
        }
    }
    
    private void jbImportCalendarMouseClick() {
        if (jtable.getRowCount()==0)
            proxy.importFromSubDest();
    }
}