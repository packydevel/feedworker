package org.feedworker.client.frontend.panel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.DefaultListCellRenderer;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;

import org.feedworker.client.frontend.events.ComboboxEvent;
import org.feedworker.client.frontend.events.ComboboxEventListener;
import org.feedworker.client.frontend.events.EditorPaneEvent;
import org.feedworker.client.frontend.events.EditorPaneEventListener;
import org.feedworker.client.frontend.events.ListEvent;
import org.feedworker.client.frontend.events.ListEventListener;

import org.jfacility.javax.swing.ButtonTabComponent;
import org.jfacility.javax.swing.Swing;

/**TODO: lista ordinata e senza duplicati,
 * @author luca
 */
public class paneShow extends paneAbstract implements ComboboxEventListener, 
                                    EditorPaneEventListener, ListEventListener {
    
    private static paneShow jpanel = null;
    private JList jlSeries;
    private JTabbedPane jtpShows;
    private JComboBox jcbShows;
    private DefaultListModel listModel;
    
    private paneShow(){
        super("Show");
        initializePanel();
        initializeButtons();
        core.setListListener(this);
        core.setComboboxListener(this);
        core.setEditorPaneListener(this);
    }
    
    public static paneShow getPanel(){
        if (jpanel==null)
            jpanel = new paneShow();
        return jpanel;
    }

    @Override
    void initializePanel() {
        JButton jbImport = new JButton(core.getIconImport2());
        jbImport.setToolTipText("Importa da myItasa");
        jbImport.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                proxy.importNameListFromMyItasa();
            }
        });
        
        JButton jbSave = new JButton(core.getIconSave());
        jbSave.setToolTipText("Salva Lista");
        jbSave.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (listModel.getSize()>0)
                    proxy.saveList(listModel.toArray());
            }
        });
        
        JButton jbRemove = new JButton(core.getIconRemove());
        jbRemove.setToolTipText("Rimuovi selezionato");
        jbRemove.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                int index = jlSeries.getSelectedIndex();
                if (index>-1)
                    listModel.remove(index);
            }
        });
        
        listModel = new DefaultListModel();
        jlSeries = new JList(listModel);
        jlSeries.setCellRenderer(new GraphicList());
        jlSeries.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        jlSeries.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        jlSeries.setVisibleRowCount(-1);
        jlSeries.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) // Double-click 
                    newTabShow(((Object[]) jlSeries.getSelectedValue())[0]);
            }
        });
        JScrollPane jspList = new JScrollPane(jlSeries);
        jspList.setPreferredSize(new Dimension(190,850));
        jspList.setMinimumSize(new Dimension(190,590));
        
        JPanel jpWest = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        int x=-1;
        gbc.gridx = ++x;
        gbc.gridy = 0;
        gbc.insets = BUTTON_SPACE_INSETS;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        jpWest.add(jbImport, gbc);
        gbc.gridx = ++x;
        jpWest.add(jbSave, gbc);
        gbc.gridx = ++x;
        jpWest.add(jbRemove, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        jpWest.add(jspList, gbc);
        
        jtpShows = new JTabbedPane();
        
        JPanel pane = new JPanel(new BorderLayout());
        
        pane.add(jpWest, BorderLayout.WEST);
        pane.add(jtpShows, BorderLayout.CENTER);
        jpCenter.add(pane);
    }

    @Override
    void initializeButtons() {
        jcbShows = new JComboBox();
        jcbShows.setMaximumRowCount(20);
        
        JButton jbAdd = new JButton(core.getIconAdd());
        jbAdd.setBorder(BORDER);
        jbAdd.setToolTipText("Aggiunge il rigo selezionato alle mie serie");
        jbAdd.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                proxy.requestSingleAddList(jcbShows.getSelectedItem());
            }
        });
        
        JButton jbSee = new JButton(core.getIconSee());
        jbSee.setBorder(BORDER);
        jbSee.setToolTipText("Visualizza info serie selezionata");
        jbSee.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (jcbShows.getSelectedIndex()>-1)
                    newTabShow(jcbShows.getSelectedItem());
            }
        });
        
        JButton jbRefresh = new JButton(core.getIconRefresh2());
        jbRefresh.setBorder(BORDER);
        jbRefresh.setToolTipText("Aggiorna i dati del tab attivo");
        jbRefresh.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                //if (jtpShows.getSelectedIndex()>-1)
                    //TODO
            }
        });
        
        JButton jbBrowse = new JButton(core.getIconWWW());
        jbBrowse.setBorder(BORDER);
        jbBrowse.setToolTipText("Visualizza la scheda itasa del tab attivo nel browser");
        jbBrowse.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (jtpShows.getSelectedIndex()>-1)
                    proxy.openFormTV(jtpShows.getSelectedComponent().getName());
            }
        });
        
        JButton jbClose = new JButton(core.getIconClose());
        jbClose.setBorder(BORDER);
        jbClose.setToolTipText("Chiude tutti i tab aperti");
        jbClose.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jtpShows.removeAll();
            }
        });
        
        int x=0;
        jpAction.add(jcbShows, gbcAction);
        gbcAction.gridx = ++x;
        jpAction.add(jbSee, gbcAction);
        gbcAction.gridx = ++x;
        jpAction.add(jbAdd, gbcAction);
        gbcAction.gridx = ++x;
        jpAction.add(jbRefresh, gbcAction);
        gbcAction.gridx = ++x;
        jpAction.add(jbBrowse, gbcAction);
        gbcAction.gridx = ++x;
        jpAction.add(jbClose, gbcAction);
    }
    
    private void newTabShow(Object name){
        tabInternalShow pane = (tabInternalShow) core.addNewTabShow(name);
        if (!jtpShows.isAncestorOf(pane)) {
            jtpShows.addTab(name.toString(), pane);
            jtpShows.setTabComponentAt(jtpShows.getTabCount() - 1,
                    new ButtonTabComponent(jtpShows));
        }
        jtpShows.setSelectedComponent(pane);
    }

    @Override
    public void objReceived(ListEvent evt) {
        for (int i=0; i<evt.getArray().length; i++)
            listModel.addElement(evt.getArray()[i]);
    }

    @Override
    public void objReceived(ComboboxEvent evt) {
        for (int i=0; i<evt.getArray().length; i++)
            jcbShows.addItem(evt.getArray()[i]);
        jcbShows.addItem(null);
        jcbShows.setSelectedItem(null);
    }

    @Override
    public void objReceived(EditorPaneEvent evt) {
        tabInternalShow pane = (tabInternalShow) core.addNewTabShow(evt.getDest());
        if (evt.getTable().equals("show"))
            pane.setHtmlShow(evt.getHtml());
        else if (evt.getTable().equals("actors"))
            pane.setHtmlActors(evt.getHtml());
    }
    
    class GraphicList extends DefaultListCellRenderer{
        @Override
        public Component getListCellRendererComponent(JList list, Object value, 
                            int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, 
                                                    index, isSelected, cellHasFocus);
            label.setPreferredSize(new Dimension(55, 55));
            Object values[] = (Object[]) value;
            label.setName(values[0].toString());
            label.setOpaque(false);
            setName(values[0].toString());
            label.setText(null);
            label.setToolTipText(values[0].toString());          
            label.setIcon(Swing.scaleImageARGB((ImageIcon)values[1], 55, 55));
            return label;
        }
    }
} //end class