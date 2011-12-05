package org.feedworker.client.frontend;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.TreeMap;

import java.util.TreeSet;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableModel;

import org.feedworker.client.ApplicationSettings;
import org.feedworker.client.frontend.events.ComboboxEventListener;
import org.feedworker.client.frontend.events.EditorPaneEventListener;
import org.feedworker.client.frontend.events.FrameEventListener;
import org.feedworker.client.frontend.events.ListEventListener;
import org.feedworker.client.frontend.events.StatusBarEventListener;
import org.feedworker.client.frontend.events.TabbedPaneEventListener;
import org.feedworker.client.frontend.events.TableEventListener;
import org.feedworker.client.frontend.events.TextPaneEventListener;
import org.feedworker.client.frontend.panel.tabShowInfo;
import org.feedworker.client.frontend.panel.tabShowList;
import org.feedworker.core.ManageListener;
import org.feedworker.object.KeyRule;
import org.feedworker.object.Quality;
import org.feedworker.object.ValueRule;
import org.feedworker.util.Common;

import org.jfacility.java.awt.AWT;
import org.jfacility.java.lang.Lang;
import org.jfacility.java.lang.SystemProperty;
import org.jfacility.javax.swing.Swing;
/**
 *
 * @author Administrator
 */
public class GuiCore {
    private final String ENABLE_BUTTON = "enableButton";
    private final String IMAGE_FEED_NEW = "feed2.png";
    private final String IMAGE_FEED_NORMAL = "ApplicationIcon.png";
    //private final String IMAGE_FEED_NORMAL = "feed1.png";
    private final String IMAGE_ADD = "add.png";
    private final String IMAGE_CLEAN1 = "clean1.png";
    private final String IMAGE_CLEAN2 = "clean2.png";
    private final String IMAGE_CLOSE = "close.png";
    private final String IMAGE_COPY_LINK = "clipboard.png";
    private final String IMAGE_DOWNLOAD = "download.png";
    private final String IMAGE_FOLDER = "folder.png";
    private final String IMAGE_IMPORT1 = "import1.png";
    private final String IMAGE_IMPORT2 = "import2.png";
    private final String IMAGE_NAS = "nas.png";
    private final String IMAGE_REFRESH1 = "refresh1.png";
    private final String IMAGE_REFRESH2 = "refresh2.png";
    private final String IMAGE_REMOVE = "remove.png";
    private final String IMAGE_REMOVE_ALL = "remove_all.png";
    private final String IMAGE_RESET = "reset.png";
    private final String IMAGE_SAVE = "save.png";
    private final String IMAGE_SEARCH = "search.png";
    private final String IMAGE_SEE = "see.png";
    private final String IMAGE_SELECT1 = "select1.png";
    private final String IMAGE_SELECT2 = "select2.png";
    private final String IMAGE_TAB_ADD = "tab_add.png";
    private final String IMAGE_TAB_DEL = "tab_del.png";
    private final String IMAGE_TAB_EDIT = "tab_edit.png";
    private final String IMAGE_UNDO = "undo1.png";
    private final String IMAGE_WWW = "www.png";
    
    private final FileNameExtensionFilter fnfeZIP =
                                new FileNameExtensionFilter("ZIP file", "zip");
    
    private static GuiCore core = null;
    private Mediator proxy = Mediator.getIstance();
    private TreeMap<Object, tabShowInfo> mapPaneShows = 
                                    new TreeMap<Object, tabShowInfo>();
    private TreeSet<String> setListShows = new TreeSet<String>();
    private ApplicationSettings prop = proxy.getSettings();
    
    public static GuiCore getInstance(){
        if (core==null)
            core = new GuiCore();
        return core;
    }
    
    public void setTableListener(TableEventListener listener) {
        ManageListener.addTableEventListener(listener);
    }

    public void setTextPaneListener(TextPaneEventListener listener) {
        ManageListener.addTextPaneEventListener(listener);
    }
    
    public void setStatusBarListener(StatusBarEventListener listener) {
        ManageListener.addStatusBarEventListener(listener);
    }

    void setFrameListener(FrameEventListener listener) {
        ManageListener.addFrameEventListener(listener);
    }
    
    public void setComboboxListener(ComboboxEventListener listener) {
        ManageListener.addComboBoxEventListener(listener);
    }
    
    public void setListListener(ListEventListener listener) {
        ManageListener.addListEventListener(listener);
    }
    
    public void setEditorPaneListener(EditorPaneEventListener listener) {
        ManageListener.addEditorPaneEventListener(listener);
    }
    
    public void setTabbedPaneListener(TabbedPaneEventListener listener) {
        ManageListener.addTabbedPaneEventListener(listener);
    }
    
    public void addNewSerial(){
        String tv = JOptionPane.showInputDialog(null,"Inserire nome serie tv");
        if (Lang.verifyTextNotNull(tv))
            proxy.searchTV(tv);
    }
    
    public tabShowInfo addNewTabShow(Object name){
        tabShowInfo pane;
        if (!mapPaneShows.containsKey(name)){
            pane = new tabShowInfo(name.toString());
            mapPaneShows.put(name, pane);
            proxy.infoShow(name.toString());
        } else 
           pane = (tabShowInfo) mapPaneShows.get(name);
        return pane;
    }
    
    public tabShowInfo refreshTabShow(Object name){
        tabShowInfo pane = (tabShowInfo) mapPaneShows.get(name);
        pane.reset();
        proxy.infoShow(name.toString());
        return pane;
    }
    
    public boolean checkTabListShow(String name){
        if (!setListShows.contains(name)){
            setListShows.add(name);
            return true;
        } else{
            printAlert("nome tab esistente");
            return false;
        }
    }
    
    /**Pulisce la tabella specificata dai check
     *
     * @param jt
     *            tabella
     */
    public void cleanSelect(JTable jt, int col) {
        for (int i = 0; i < jt.getRowCount(); i++) {
            jt.setValueAt(false, i, col);
        }
    }

    /** Copia nella clipboard i link torrent selezionati
     *
     * @param jt1 tabella1
     * @param jt2 tabella2
     */
    public void copyLinkTorrent(JTable jt1, JTable jt2) {
        String text = "";
        for (int i = 0; i < jt1.getRowCount(); i++) {
            if (jt1.getValueAt(i, 3) == Boolean.TRUE) {
                text += jt1.getValueAt(i, 0).toString() + "\n";
                jt1.setValueAt(false, i, 3);
            }
        }
        for (int i = 0; i < jt2.getRowCount(); i++) {
            if (jt2.getValueAt(i, 3) == Boolean.TRUE) {
                text += jt2.getValueAt(i, 0).toString() + "\n";
                jt2.setValueAt(false, i, 3);
            }
        }
        if (!text.equalsIgnoreCase("")) {
            AWT.setClipboard(text);
            proxy.printOk("link copiati nella clipboard");
        }
    }
    
    public Color searchVersion(String text) {
        Color col = Color.cyan;
        String[] temp = text.split(" ");
        String version = temp[temp.length - 1].toLowerCase();
        if (version.equals(Quality._1080p.toString())) {
            col = Color.blue;
        } else if (version.equals(Quality._1080i.toString())) {
            col = Color.orange;
        } else if (version.equals(Quality._720p.toString())) {
            col = Color.red;
        } else if (version.equals(Quality.DVDRIP.toString())) {
            col = new Color(183, 65, 14);
        } else if (version.equals(Quality.HR.toString())) {
            col = Color.green;
        } else if (version.equals(Quality.BLURAY.toString())) {
            col = Color.magenta;
        } else if (version.equals(Quality.WEB_DL.toString())) {
            col = Color.white;
        } else if (version.equals(Quality.BRRIP.toString())) {
            col = Color.black;
        } else if (version.equals(Quality.BDRIP.toString())) {
            col = Color.darkGray;
        }
        return col;
    }
    
    public void downloadSub(JTable jt1, JTable jt2, boolean itasa, boolean id) {
        ArrayList<String> alLinks = new ArrayList<String>();
        alLinks = addLinks(jt1);
        if (jt2!=null)
            alLinks.addAll(addLinks(jt2));
        if (alLinks.size() > 0)
            proxy.downloadSub(alLinks, itasa, id);
        else {
            String temp = "dalle tabelle";
            if (!itasa)
                temp = "dalla tabella";
            proxy.printAlert("Selezionare almeno un rigo " + temp);
        }
    }
    
    public void downloadTorrent(JTable jt1, JTable jt2) {
        if (Lang.verifyTextNotNull(prop.getTorrentDestinationFolder())) {
            ArrayList<String> alLinks = addLinks(jt1);
            alLinks.addAll(addLinks(jt2));
            if (alLinks.size() > 0)
                proxy.downloadTorrent(alLinks);
            else
                proxy.printAlert("Selezionare almeno un rigo dalle tabelle");
        } else 
            proxy.printAlert("Non posso salvare perchè non hai specificato "
                    + "una cartella dove scaricare i file.torrent");
    }
    
    public void saveRules(TableModel jtable) {
        boolean _break = false;
        TreeMap<KeyRule, ValueRule> temp = new TreeMap<KeyRule, ValueRule>();
        for (int r = 0; r < jtable.getRowCount(); r++) {
            int c = -1;
            String name = ((String) jtable.getValueAt(r, ++c));
            String season = jtable.getValueAt(r, ++c).toString();
            String quality = (String) jtable.getValueAt(r, ++c);
            String path = (String) jtable.getValueAt(r, ++c);
            boolean rename = false, delete = false;
            try {
                rename = Boolean.parseBoolean(jtable.getValueAt(r, ++c).toString());
            } catch (NullPointerException e) {
            }
            try {
                delete = Boolean.parseBoolean(jtable.getValueAt(r, ++c).toString());
            } catch (NullPointerException e) {
            }
            if (rename && delete) {
                proxy.printAlert("Riga: " + r + " non possono coesistere entrambi "
                                + "i flag true di rename e delete");
                _break = true;
                break;
            } else {
                if (Lang.verifyTextNotNull(name)) {
                    if (delete || Lang.verifyTextNotNull(path)) {
                        try {
                            if (Lang.verifyTextNotNull(season)) {
                                int s = Lang.stringToInt(season);
                                season = Lang.intToString(s);
                            } else {
                                proxy.printAlert("Riga: " + r
                                        + " immettere un numero alla stagione");
                                _break = true;
                                break;
                            }
                            KeyRule key = new KeyRule(name, season, quality);
                            ValueRule value = new ValueRule(path, rename, delete);
                            if (!temp.containsKey(key)) {
                                temp.put(key, value);
                            } else {
                                proxy.printAlert("Riga: " + r + " trovato "
                                        + "duplicato, si prega di correggerlo");
                                _break = true;
                                break;
                            }
                        } catch (NumberFormatException ex) {
                            proxy.getError().launch(ex, getClass(), Lang.intToString(r));
                            _break = true;
                            break;
                        }
                    } else {
                        proxy.printAlert("Riga: " + r
                                + " immettere la destinazione per la regola/sub");
                        _break = true;
                        break;
                    }
                } else {
                    proxy.printAlert("Riga: " + r
                            + " immettere il nome della regola/sub/serie");
                    _break = true;
                    break;
                }
            }
        } //end for
        if (!_break)
            proxy.saveMap(temp);
    }
    
    /**Invia alla download station del nas i link torrent selezionati
     *
     * @param jt1 tabella1
     * @param jt2 tabella2
     */
    public void fireTorrentToNas(JTable jt1, JTable jt2) {
        ArrayList<String> al = new ArrayList<String>();
        for (int i = 0; i < jt1.getRowCount(); i++) {
            if (jt1.getValueAt(i, 3) == Boolean.TRUE) {
                al.add(jt1.getValueAt(i, 0).toString());
                jt1.setValueAt(false, i, 3);
            }
        }
        for (int i = 0; i < jt2.getRowCount(); i++) {
            if (jt2.getValueAt(i, 3) == Boolean.TRUE) {
                al.add(jt2.getValueAt(i, 0).toString());
                jt2.setValueAt(false, i, 3);
            }
        }
        if (al.size() > 0)
            proxy.synoDownloadRedirectory(al);
    }
    
    public void searchSubItasa(Object show, Object version, boolean complete, 
                                String season, String episode) {
        try{
            boolean check = false;
            if (Lang.verifyTextNotNull(season)){
                Lang.stringToInt(season);
                check = true;
            }
            if (Lang.verifyTextNotNull(episode)){
                Lang.stringToInt(episode);
                check = true;
            }
            if (!check && complete)
                check = true;
            if (check)
                proxy.searchSubItasa(show, version, complete, season, episode);
            else 
                proxy.printAlert("Selezionare almeno un elemento di ricerca tra "
                        + "stagione completa, numero stagione e/o numero episodio");
        } catch (NumberFormatException e){
            proxy.printAlert("Immettere un numero alla stagione e/o episodio "
                        + "invece di una stringa");
        }
    }
    
    public boolean saveSettings(boolean dirLocal, String destSub, String sambaDomain,
            String sambaIP, String sambaDir, String sambaUser, String sambaPwd,
            String time, String timeout,
            boolean advancedDownload, boolean runIconized, String itasa,
            String myitasa, String user, String pwd, boolean autoMyitasa,
            boolean autoLoadMyItasa, String subsf, String mySubsf, String torrentDest,
            String mailTO,  String smtp, boolean paneLog, boolean paneSearch, 
            boolean paneSetting, boolean paneSubDest, boolean paneReminder, 
            boolean reminder, String googleUser, String googlePwd, String googleCalendar, 
            boolean paneTorrent, boolean paneCalendar, boolean torrentOption, 
            boolean paneShow, boolean blog, boolean paneBlog) {
                
        String oldMin = prop.getRefreshInterval();
        boolean first = prop.isApplicationFirstTimeUsed();
        boolean save = false;
        if (checkSaveGlobal(dirLocal, destSub, sambaDomain, sambaIP, sambaDir,
                sambaUser, sambaPwd)) {
            save = true;
            if (save)
                save = checkSaveItasa(itasa, myitasa, user, pwd);
            if (prop.isSubsfactoryOption() && save)
                save = checkSaveSubsf(subsf, mySubsf);
            if (prop.isTorrentOption() && save)
                checkSaveTorrent(torrentDest);
        }
        if (save) {
            setPropGlobal(dirLocal, destSub, sambaDomain, sambaIP, sambaDir,
                    sambaUser, sambaPwd, time, timeout, 
                    advancedDownload, runIconized, reminder);
            setPropItasa(itasa, myitasa, user, pwd, autoMyitasa, autoLoadMyItasa, blog);
            setPropSubsf(subsf, mySubsf);
            setPropTorrent(torrentDest, torrentOption);
            setPropAdvisor(mailTO, smtp, googleUser, googlePwd, googleCalendar);
            setPropVisiblePane(paneLog, paneSearch, paneSetting, paneSubDest, 
                            paneReminder, paneTorrent, paneCalendar, paneShow, paneBlog);
            proxy.writeSettings();
            if (!prop.isApplicationFirstTimeUsed() && first) {
                ManageListener.fireFrameEvent(this, ENABLE_BUTTON);
                proxy.runRss();
            } else if (Lang.verifyTextNotNull(oldMin) && 
                        !oldMin.equalsIgnoreCase(prop.getRefreshInterval()))
                proxy.restartRss();
            proxy.printOk("Impostazioni salvate in " + prop.getSettingsFilename());
        }
        return save;
    }
    
    public int requestRemoveSeries(Component from, boolean table){
        String msg = "Vuoi eliminare le serie dalla tabella?";
        if (!table)
            msg = "Vuoi eliminare le serie e la categoria selezionata?";
        return JOptionPane.showConfirmDialog(from, msg, "Info", 
                                            JOptionPane.YES_NO_OPTION);
    }
    
    public void saveList(JTabbedPane jtp) {
        TreeMap<String, Object[]> map = new TreeMap<String, Object[]>();
        for (int i=0; i<jtp.getTabCount(); i++)
            map.put(jtp.getTitleAt(i), ((tabShowList) jtp.getComponentAt(i)).getArrayModel());
        proxy.saveList(map);
    }
    
    public ImageIcon getIconAdd() {
        return Common.getResourceImageButton(IMAGE_ADD);
    }
    
    public ImageIcon getIconClean1() {
        return Common.getResourceImageButton(IMAGE_CLEAN1);
    }
    
    public ImageIcon getIconClean2() {
        return Common.getResourceImageButton(IMAGE_CLEAN2);
    }
    
    public ImageIcon getIconClose() {
        return Common.getResourceImageButton(IMAGE_CLOSE);
    }
    
    public ImageIcon getIconCopy() {
        return Common.getResourceImageButton(IMAGE_COPY_LINK);
    }
    
    public ImageIcon getIconDownload() {
        return Common.getResourceImageButton(IMAGE_DOWNLOAD);
    }
    
    public ImageIcon getIconFolder() {
        return Common.getResourceImageButton(IMAGE_FOLDER);
    }
    
    public ImageIcon getIconImport1() {
        return Common.getResourceImageButton(IMAGE_IMPORT1);
    }
    
    public ImageIcon getIconImport2() {
        return Common.getResourceImageButton(IMAGE_IMPORT2);
    }
        
    public ImageIcon getIconNas() {
        return Common.getResourceImageButton(IMAGE_NAS);
    }
    
    public ImageIcon getIconRefresh1() {
        return Common.getResourceImageButton(IMAGE_REFRESH1);
    }
    
    public ImageIcon getIconRefresh2() {
        return Common.getResourceImageButton(IMAGE_REFRESH2);
    }
    
    public ImageIcon getIconRemove() {
        return Common.getResourceImageButton(IMAGE_REMOVE);
    }
    
    public ImageIcon getIconRemoveAll() {
        return Common.getResourceImageButton(IMAGE_REMOVE_ALL);
    }
    
    public ImageIcon getIconReset() {
        return Common.getResourceImageButton(IMAGE_RESET);
    }
    public ImageIcon getIconSave() {
        return Common.getResourceImageButton(IMAGE_SAVE);
    }
    
    public ImageIcon getIconSearch() {
        return Common.getResourceImageButton(IMAGE_SEARCH);
    }
    
    public ImageIcon getIconSee() {
        return Common.getResourceImageButton(IMAGE_SEE);
    }
    
    public ImageIcon getIconSelect1() {
        return Common.getResourceImageButton(IMAGE_SELECT1);
    }
    
    public ImageIcon getIconSelect2() {
        return Common.getResourceImageButton(IMAGE_SELECT2);
    }
    
    public ImageIcon getIconTabAdd() {
        return Common.getResourceImageButton(IMAGE_TAB_ADD);
    }
    
    public ImageIcon getIconTabDel() {
        return Common.getResourceImageButton(IMAGE_TAB_DEL);
    }
    
    public ImageIcon getIconTabEdit() {
        return Common.getResourceImageButton(IMAGE_TAB_EDIT);
    }
        
    public ImageIcon getIconUndo() {
        return Common.getResourceImageButton(IMAGE_UNDO);
    }
    
    public ImageIcon getIconWWW() {
        return Common.getResourceImageButton(IMAGE_WWW);
    }
    
    Image getIconFeedNew() {
        return Common.getResourceIcon(IMAGE_FEED_NEW);
    }
    
    Image getIconFeedNormal() {
        return Common.getResourceIcon(IMAGE_FEED_NORMAL);
    }
    
    String getOperationEnableButton() {
        return ENABLE_BUTTON;
    }
    
    void checkMenuNotify(int i, boolean value) {
        boolean check = true;
        if (i==2){
            if (!Lang.verifyTextNotNull(prop.getMailTO())||
                    !Lang.verifyTextNotNull(prop.getMailSMTP())) {
                check = false;
                proxy.printAlert("Per usare le notifiche email devono essere "
                        + "impostati i campi MailTO & SMTP");
            }
        }
        if (i==3){
            if (!Lang.verifyTextNotNull(prop.getGoogleUser())||
                    !Lang.verifyTextNotNull(prop.getGooglePwd()) || 
                    !Lang.verifyTextNotNull(prop.getGoogleCalendar())) {
                check = false;
                proxy.printAlert("Per usare le notifiche sms devono essere "
                        + "impostati i campi Google User Password Calendar");
            }
        }
        if (check)
            proxy.setPropNotify(i,value);
    }
    
    void invokeBackup(Component parent) {
        String name = Swing.getFile(parent, "Creare il file zip per il backup",
                fnfeZIP, new File(SystemProperty.getUserDir() + File.separator));
        if (name != null)
            proxy.backup(name);
    }
    
    private void setPropGlobal(boolean dirLocal, String destSub,
            String sambaDomain, String sambaIP, String sambaDir,
            String sambaUser, String sambaPwd, String time,
            String timeout, boolean advancedDownload,
            boolean runIconized, boolean reminder) {
        prop.setLocalFolder(dirLocal);
        prop.setSubtitleDestinationFolder(destSub);
        prop.setRefreshInterval(time);
        prop.setCifsShareDomain(sambaDomain);
        prop.setCifsShareLocation(sambaIP);
        prop.setCifsSharePath(sambaDir);
        prop.setCifsShareUsername(sambaUser);
        prop.setCifsSharePassword(sambaPwd);
        prop.setHttpTimeout(timeout);
        prop.setEnableAdvancedDownload(advancedDownload);
        prop.setEnableIconizedRun(runIconized);
        prop.setReminderOption(reminder);
    }

    private void setPropItasa(String itasa, String myitasa, String user,
            String pwd, boolean auto, boolean autoload, boolean blog) {
        prop.setItasaFeedURL(itasa);
        prop.setMyitasaFeedURL(myitasa);
        prop.setItasaUsername(user);
        prop.setItasaPassword(pwd);
        prop.setAutoDownloadMyItasa(auto);
        prop.setAutoLoadDownloadMyItasa(autoload);
        prop.setBlogOption(blog);
    }
    
    private void setPropSubsf(String subsf, String mySubsf){
        prop.setSubsfactoryFeedURL(subsf);
        prop.setMySubsfactoryFeedUrl(mySubsf);
    }
    
    private void setPropAdvisor(String mailTO, String smtp, String googleUser,
            String googlePwd, String googleCalendar){
        prop.setMailTO(mailTO);
        prop.setMailSMTP(smtp);
        prop.setGoogleUser(googleUser);
        prop.setGooglePwd(googlePwd);
        prop.setGoogleCalendar(googleCalendar);
    }
    
    private void setPropVisiblePane(boolean log, boolean search, boolean setting, 
                                    boolean subdest, boolean reminder, boolean torrent,
                                    boolean calendar, boolean show, boolean blog){
        prop.setEnablePaneCalendar(calendar);
        prop.setEnablePaneLog(log);
        prop.setEnablePaneSearchSubItasa(search);
        prop.setEnablePaneSetting(setting);
        prop.setEnablePaneSubDestination(subdest);
        prop.setEnablePaneReminder(reminder);
        prop.setEnablePaneTorrent(torrent);
        prop.setEnablePaneShow(show);
        prop.setEnablePaneBlog(blog);
    }
    
    private void setPropTorrent(String dest, boolean option){
        prop.setTorrentDestinationFolder(dest);
        prop.setTorrentOption(option);
    }
    
    /**Aggiunge i link corrispondenti al true della colonna download nell'arraylist
     *
     * @param jt jtable su cui operare
     * @return Arraylist di stringhe
     */
    private ArrayList<String> addLinks(JTable jt) {
        ArrayList<String> alLinks = new ArrayList<String>();
        for (int i = 0; i < jt.getRowCount(); i++) {
            if (jt.getValueAt(i, 3) == Boolean.TRUE)
                alLinks.add(jt.getValueAt(i, 0).toString());
        }
        return alLinks;
    }
    
    /**verifica impostazioni torrent
     *
     * @return booleano che le impostazioni sono ok
     */
    private boolean checkSaveTorrent(String text) {
        if (!Lang.verifyTextNotNull(text))
            proxy.printAlert("Avviso: Non immettendo la Destinazione dei Torrent non potrai "
                    + "scaricare .torrent");
        return true;
    }

    /**verifica impostazioni subsf
     *
     * @return booleano che le impostazioni sono ok
     */
    private boolean checkSaveSubsf(String subsf, String mySubsf) {
        boolean check = true;
        if (!Lang.verifyTextNotNull(subsf) && !Lang.verifyTextNotNull(mySubsf))
            proxy.printAlert("Avviso: Non immettendo link RSS Subsfactory non potrai " + 
                    "usare i feed Subsfactory");
        else {
            try {
                if (Lang.verifyTextNotNull(subsf))
                    check = proxy.testRss(subsf, "subsfactory");
                if (check && Lang.verifyTextNotNull(mySubsf))
                    check = proxy.testRss(mySubsf, "mysubsfactory");
            } catch (MalformedURLException e) {
                proxy.getError().launch(e, getClass(), "subsfactory");
                check = false;
            }
        }
        return check;
    }

    /**verifica impostazioni itasa
     *
     * @return booleano che le impostazioni sono ok
     */
    private boolean checkSaveItasa(String itasa, String myitasa, String user, String pwd) {
        boolean check = true;
        try {
            if (!Lang.verifyTextNotNull(itasa) && !Lang.verifyTextNotNull(myitasa)) {
                printAlert("Avviso: Non immettendo link RSS itasa e/o myitasa " + 
                        "non potrai usare i feed italiansubs");
            } else {
                if (Lang.verifyTextNotNull(itasa))
                    check = proxy.testRss(itasa, "itasa");
                if (check) {
                    if (Lang.verifyTextNotNull(myitasa))
                        check = proxy.testRss(myitasa, "myitasa");
                    if (check) {
                        if (!Lang.verifyTextNotNull(user))
                            printAlert("Avviso: senza Username Itasa non " + 
                                    "potrai scaricare i subs");
                        else if (!Lang.verifyTextNotNull(new String(pwd)))
                            printAlert("Avviso: senza Password Itasa non " +
                                    "potrai scaricare i subs");
                    }
                }
            }
        } catch (MalformedURLException ex) {
            proxy.getError().launch(ex, getClass(), "Itasa");
            check = false;
        }
        return check;
    }

    private boolean checkSaveGlobal(boolean dirLocal, String destSub,
            String sambaDomain, String sambaIP, String sambaDir,
            String sambaUser, String sambaPwd) {
        boolean check = false;
        if (dirLocal) {
            if (!Lang.verifyTextNotNull(destSub))
                printAlert("INPUT OBBLIGATORIO: La Destinazione Locale non può "
                                                                + "essere vuota.");
            else
                check = true;
        } else { // SAMBA selected
            if (!Lang.verifyTextNotNull(sambaDomain))
                printAlert("INPUT OBBLIGATORIO: Il Dominio Samba non può essere vuoto.");
            else if (!Lang.verifyTextNotNull(sambaIP))
                printAlert("INPUT OBBLIGATORIO: L'ip Samba non può essere vuoto.");
            else if (!Lang.verifyTextNotNull(sambaDir))
                printAlert("INPUT OBBLIGATORIO: La cartella condivisa Samba non può essere "
                        + "vuota.");
            else if (!Lang.verifyTextNotNull(sambaUser))
                printAlert("INPUT OBBLIGATORIO: L'utente Samba non può essere vuoto.");
            else if (!Lang.verifyTextNotNull(sambaPwd))
                printAlert("INPUT OBBLIGATORIO: La password Samba non può essere vuota.");
            else if (!proxy.testSamba(sambaIP, sambaDir, sambaDomain,
                    sambaUser, sambaPwd)) {
                printAlert("Impossibile connettermi al server/dir condivisa Samba");
            } else {
                check = true;
            }
        }
        return check;
    }
    
    private void printAlert(String msg){
        proxy.printAlert(msg);
    }
}