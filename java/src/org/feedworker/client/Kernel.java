package org.feedworker.client;

//IMPORT JAVA
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import jcifs.smb.SmbException;

import org.eclipse.swt.widgets.Display;
import org.feedworker.client.frontend.events.MyJFrameEvent;
import org.feedworker.client.frontend.events.MyJFrameEventListener;
import org.feedworker.client.frontend.events.MyTextPaneEvent;
import org.feedworker.client.frontend.events.MyTextPaneEventListener;
import org.feedworker.client.frontend.events.TableRssEvent;
import org.feedworker.client.frontend.events.TableRssEventListener;
import org.feedworker.client.frontend.events.TableXmlEvent;
import org.feedworker.client.frontend.events.TableXmlEventListener;
import org.feedworker.util.AudioPlay;
import org.feedworker.util.Common;
import org.feedworker.util.ExtensionFilter;
import org.feedworker.util.KeyRule;
import org.feedworker.util.ManageException;
import org.feedworker.util.Quality;
import org.feedworker.util.Samba;
import org.feedworker.util.ValueRule;
import org.jdom.JDOMException;
import org.jfacility.lang.Lang;

import com.sun.syndication.io.FeedException;

import de.javasoft.plaf.synthetica.SyntheticaBlackMoonLookAndFeel;
import de.javasoft.plaf.synthetica.SyntheticaBlackStarLookAndFeel;
import de.javasoft.plaf.synthetica.SyntheticaBlueIceLookAndFeel;
import de.javasoft.plaf.synthetica.SyntheticaBlueMoonLookAndFeel;
import de.javasoft.plaf.synthetica.SyntheticaBlueSteelLookAndFeel;
import de.javasoft.plaf.synthetica.SyntheticaGreenDreamLookAndFeel;
import de.javasoft.plaf.synthetica.SyntheticaSilverMoonLookAndFeel;
import de.javasoft.plaf.synthetica.SyntheticaStandardLookAndFeel;

/**
 * Motore di Feedworker
 * 
 * @author luca
 */
public class Kernel {
    // PUBLIC FINAL VARIABLES

    public final String ITASA = "Itasa";
    public final String SUBSF = "Subsf";
    public final String EZTV = "Eztv";
    public final String BTCHAT = "Btchat";
    public final String MYITASA = "MyItasa";
    public final String MYSUBSF = "MySubsf";
    // PRIVATE FINAL VARIABLES
    private final String RSS_TORRENT_EZTV = "http://ezrss.it/feed/";
    private final String RSS_TORRENT_BTCHAT = "http://rss.bt-chat.com/?cat=9";
    private final String SPLIT_HDTV = ".hdtv";
    private final String SPLIT_POINT = "\\.";
    private final String[] QUALITY = new String[]{Quality.ALL.toString(),
        Quality.NORMAL.toString(), Quality.FORM_720p.toString(),
        Quality.FORM_1080p.toString(), Quality.BLURAY.toString(),
        Quality.DVDRIP.toString(), Quality.HR.toString(),
        Quality.DIFF.toString()};
    // PRIVATE STATIC VARIABLES
    private static Kernel core = null;
    // PRIVATE VARIABLES
    private ApplicationSettings prop = ApplicationSettings.getIstance();
    private List listenerTableRss = new ArrayList();
    private List listenerTableXml = new ArrayList();
    private List listenerTextPane = new ArrayList();
    private List listenerJFrame = new ArrayList();
    private Timer timer;
    private String lastItasa = null, lastMyItasa = null, lastSubsf = null,
            lastEztv = null, lastBtchat = null;
    private TreeMap<KeyRule, ValueRule> mapRules;
    private ManageException error = ManageException.getIstance();
    private MyTextPaneEventListener mytpel;

    /**
     * Restituisce l'istanza corrente del kernel
     *
     * @return istanza kernel
     */
    public static Kernel getIstance() {
        if (core == null) {
            core = new Kernel();
        }
        return core;
    }

    /**
     * Scarica lo zip, estrae i sub e invoca l'analizzatore del path di
     * destinazione
     *
     * @param als
     *            arraylist di link
     * @param itasa
     */
    public void downloadSub(ArrayList<String> als, boolean itasa) {
        DownloadThread dt = new DownloadThread(mapRules, als, itasa);
        Thread t = new Thread(dt, "Thread download");
        dt.addMyTextPaneEventListener(mytpel);
        t.start();
    }

    /**
     * effettua il download automatico di myitasa comprende le fasi anche di
     * estrazione zip e analizzazione percorso definitivo.
     *
     * @param link
     *            link da analizzare
     */
    private void downItasaAuto(Object link) {
        ArrayList<String> als = new ArrayList<String>();
        als.add(link.toString());
        DownloadThread dt = new DownloadThread(mapRules, als, true);
        Thread t = new Thread(dt, "Thread download");
        dt.addMyTextPaneEventListener(mytpel);
        t.start();
    }

    /**
     * Scarica i torrent
     *
     * @param als
     *            arraylist di link
     */
    public void downloadTorrent(ArrayList<String> als) {
        int connection_Timeout = Lang.stringToInt(prop.getHttpTimeout()) * 1000;
        Http http = new Http(connection_Timeout);
        try {
            for (int i = 0; i < als.size(); i++) {
                InputStream is = http.getTorrent(als.get(i));
                if (is != null) {
                    File f = new File(prop.getTorrentDestinationFolder()
                            + File.separator + http.getNameFile());
                    Common.downloadSingle(is, f);
                    fireNewTextPaneEvent("Scaricato: " + http.getNameFile(),
                            MyTextPaneEvent.TORRENT);
                } else {
                    printAlert("Non posso gestire " + als.get(i).split(".")[1]);
                }
            }
        } catch (IOException ex) {
            error.launch(ex, getClass(), null);
        }
        http.closeClient();
    }

    /**
     * Restituisce il valore/percorso della chiave ad esso associato nella
     * treemap
     *
     * @param name
     *            nome del file da analizzare
     * @param parsing
     *            valore sul quale effettuare lo split
     * @return path di destinazione
     */
    private String mapPath(KeyRule key) {
        if (key != null && mapRules != null) {
            return mapRules.get(key).getPath();
        }
        return null;
    }

    /**Effettua l'analisi del nome del file restituendo l'oggetto filtro da confrontare
     *
     * @param name nome del file da analizzare
     * @param split stringa col quale effettuare lo split del nome del file
     * @return oggetto filtro
     */
    private KeyRule parsingNamefile(String namefile, String split) {
        String[] temp = (namefile.split(split))[0].split(SPLIT_POINT);
        int pos = temp.length - 1;
        String version = searchVersion(temp[pos]);
        String seriesNum;
        pos = Common.searchPosSeries(temp);
        if (pos > -1) {
            seriesNum = Common.searchNumberSeries(temp[pos]);
        } else {
            seriesNum = "1";
        }
        String name = temp[0];
        for (int i = 1; i < pos; i++) {
            name += " " + temp[i];
        }
        return new KeyRule(name, seriesNum, version);
    }

    /**cerca la versione/qualità del sub/video
     *
     * @param text testo da confrontare
     * @return versione video/sub
     */
    private String searchVersion(String text) {
        String version = null;
        for (int i = 0; i < QUALITY.length; i++) {
            if (text.toLowerCase().equalsIgnoreCase(QUALITY[i])) {
                version = QUALITY[i];
                break;
            }
        }
        if (version == null) {
            version = Quality.NORMAL.toString();
        }
        return version;
    }

    // TODO: non usata, se sarà implementata, cambiare la parte di stampa
    /**
     * effuetta la stampa dei file con l'estensione e la directory in cui
     * cercare
     *
     * @param dir
     *            directory su cui effettuare la ricerca
     * @param ext
     *            estensione dei file da cercare
     */
    private void listDir(String dir, String ext) {
        // Get list of names
        String[] list = new File(dir).list(new ExtensionFilter(ext));
        // Sort it (Data Structuring chapter))
        Arrays.sort(list);
        for (int i = 0; i < list.length; i++) {
            System.out.println(list[i]);
        }
    }

    /**testa la connessione a samba
     *
     * @param ip ip della macchina samba
     * @param dir directory condivisa
     * @param dom dominio
     * @param user utente
     * @param pwd password
     * @return true se positivo, false altrimenti
     */
    public boolean testSamba(String ip, String dir, String dom, String user,
            String pwd) {
        boolean test = false;
        Samba.resetInstance();
        Samba s = Samba.getIstance(ip, dir, dom, user, pwd);
        try {
            s.testConn();
            test = s.testConn();
        } catch (SmbException ex) {
            error.launch(ex, getClass(), null);
        } catch (IOException ex) {
            error.launch(ex, getClass(), null);
        }
        return test;
    }

    /** imposta il LookAndFeel */
    public void setLookFeel() {        
            // String[] li = {"Licensee=AppWork UG",
            // "LicenseRegistrationNumber=289416475", "Product=Synthetica",
            // "LicenseType=Small Business License", "ExpireDate=--.--.----",
            // "MaxVersion=2.999.999"};
            // UIManager.put("Synthetica.license.info", li);
            // UIManager.put("Synthetica.license.key",
            // "C1410294-61B64AAC-4B7D3039-834A82A1-37E5D695");

            LookAndFeel laf = null;
            String lf = prop.getApplicationLookAndFeel();
            try {
            if (lf != null) {
                if (lf.equalsIgnoreCase("Nimbus")) {
                    // laf = new NimbusLookAndFeel();
                } else if (lf.equalsIgnoreCase("Synthetica Standard")) {
                    laf = new SyntheticaStandardLookAndFeel();
                } else if (lf.equalsIgnoreCase("Synthetica BlackEye")) {
                    // laf = new SyntheticaBlackEyeLookAndFeel();
                } else if (lf.equalsIgnoreCase("Synthetica BlackMoon")) {
                    laf = new SyntheticaBlackMoonLookAndFeel();
                } else if (lf.equalsIgnoreCase("Synthetica BlackStar")) {
                    laf = new SyntheticaBlackStarLookAndFeel();
                } else if (lf.equalsIgnoreCase("Synthetica BlueIce")) {
                    laf = new SyntheticaBlueIceLookAndFeel();
                } else if (lf.equalsIgnoreCase("Synthetica BlueMoon")) {
                    laf = new SyntheticaBlueMoonLookAndFeel();
                } else if (lf.equalsIgnoreCase("Synthetica BlueSteel")) {
                    laf = new SyntheticaBlueSteelLookAndFeel();
                } else if (lf.equalsIgnoreCase("Synthetica GreenDream")) {
                    laf = new SyntheticaGreenDreamLookAndFeel();
                } else if (lf.equalsIgnoreCase("Synthetica MaouveMetallic")) {
                    // laf = new SyntheticaMauveMetallicLookAndFeel();
                } else if (lf.equalsIgnoreCase("Synthetica OrangeMetallic")) {
                    // laf = new SyntheticaOrangeMetallicLookAndFeel();
                } else if (lf.equalsIgnoreCase("Synthetica SilverMoon")) {
                    laf = new SyntheticaSilverMoonLookAndFeel();
                } else if (lf.equalsIgnoreCase("Synthetica Simple2D")) {
                    // laf = new SyntheticaSimple2DLookAndFeel();
                } else if (lf.equalsIgnoreCase("Synthetica SkyMetallic")) {
                    // laf = new SyntheticaSkyMetallicLookAndFeel();
                } else if (lf.equalsIgnoreCase("Synthetica WhiteVision")) {
                    // laf = new SyntheticaWhiteVisionLookAndFeel();
                }
            }
            if (laf == null) {
                UIManager.setLookAndFeel(new SyntheticaStandardLookAndFeel());
            } else {
                UIManager.setLookAndFeel(laf);
            }
        } catch (UnsupportedLookAndFeelException e) {
            error.launch(e, getClass());
        } catch (ParseException e) {
            error.launch(e, getClass());
        }
    }

    /**
     * chiude l'applicazione salvando la data nel settings
     *
     * @param data
     */
    public void closeApp(String data) {
        prop.setLastDateTimeRefresh(data);
        if (!prop.isApplicationFirstTimeUsed()) {
            prop.writeOnlyLastDate();
        }
        System.exit(0);
    }

    /** Scrive le proprietà dell'applicazione nel file properties */
    public void writeProp() {
        prop.writeGeneralSettings();
        if (prop.hasItasaOption()) {
            prop.writeItasaSettings();
        }
        if (prop.hasSubsfactoryOption()) {
            prop.writeSubsfactorySettings();
        }
        if (prop.hasTorrentOption()) {
            prop.writeTorrentSettings();
        }
        if (prop.isApplicationFirstTimeUsed()) {
            prop.writeApplicationFirstTimeUsedFalse();
        }
    }

    /** Scrive l'ultima data d'aggiornamento nel file properties */
    public void writeLastDate() {
        prop.writeOnlyLastDate();
    }

    /**Restituisce l'arraylist contenente i feed rss
     *
     * @param urlRss url rss da analizzare
     * @param data data da confrontare
     * @param from provenienza
     * @param download download automatico
     * @return arraylist di feed(array di oggetti)
     */
    private ArrayList<Object[]> getFeedRss(String urlRss, String data,
            String from, boolean download) {
        RssParser rss = null;
        ArrayList<Object[]> matrice = null;
        int connection_Timeout = Lang.stringToInt(prop.getHttpTimeout()) * 1000;
        Http http = new Http(connection_Timeout);
        try {
            InputStream ist = http.getStreamRss(urlRss);
            if (ist != null) {
                File ft = File.createTempFile("rss", ".xml");
                Common.downloadSingle(ist, ft);
                rss = new RssParser(ft);
                matrice = rss.read();
                ft.delete();
                boolean continua = true;
                if (data != null) {
                    Date confronta = Common.stringToDate(data);
                    for (int i = matrice.size() - 1; i >= 0; i--) {
                        String date_matrix = String.valueOf(matrice.get(i)[1]);
                        if (confronta.before(Common.stringToDate(date_matrix))) {
                            if (continua) {
                                if (from.equals(ITASA)) {
                                    fireNewTextPaneEvent(
                                            "Nuovo/i feed " + from,
                                            MyTextPaneEvent.FEED_ITASA);
                                } else if (from.equals(MYITASA)
                                        && !prop.isAutoDownloadMyItasa()) {
                                    fireNewTextPaneEvent(
                                            "Nuovo/i feed " + from,
                                            MyTextPaneEvent.FEED_MYITASA);
                                } else if (from.equals(SUBSF)) {
                                    fireNewTextPaneEvent(
                                            "Nuovo/i feed " + from,
                                            MyTextPaneEvent.FEED_SUBSF);
                                } else if (from.equals(EZTV)) {
                                    fireNewTextPaneEvent(
                                            "Nuovo/i feed " + from,
                                            MyTextPaneEvent.FEED_TORRENT1);
                                } else if (from.equals(BTCHAT)) {
                                    fireNewTextPaneEvent(
                                            "Nuovo/i feed " + from,
                                            MyTextPaneEvent.FEED_TORRENT2);
                                }
                                continua = false;
                            }
                            if ((isNotStagione((String) matrice.get(i)[2]))
                                    && download) {
                                downItasaAuto(matrice.get(i)[0]);
                            }
                        } else {
                            matrice.remove(i);
                        }
                    }
                }
            }
        } catch (ParseException ex) {
            error.launch(ex, getClass());
        } catch (FeedException ex) {
            error.launch(ex, getClass(), from);
        } catch (IllegalArgumentException ex) {
            error.launch(ex, getClass());
        } catch (IOException ex) {
            error.launch(ex, getClass(), from);
        }
        return matrice;
    }

    /** Esegue gli rss */
    public void runRss() {
        if (!prop.isApplicationFirstTimeUsed()) {
            prop.setLastDateTimeRefresh(Common.actualTime());
            runItasa(true);
            runSubsfactory(true);
            runTorrent(true);
            int delay = Lang.stringToInt(prop.getRefreshInterval()) * 60000;
            runTimer(delay);
        }
    }

    /**
     * esegue gli rss sotto timer
     *
     * @param delay
     *            tempo in secondi per il timer
     */
    private void runTimer(int delay) {
        timer = new Timer();
        try {
            timer.scheduleAtFixedRate(new TimerTask() {

                @Override
                public void run() {
                    boolean icontray = false;
                    // String data = prop.getLastDate();
                    prop.setLastDateTimeRefresh(Common.actualTime());
                    if (runItasa(false)) {
                        icontray = true;
                    }
                    if (runSubsfactory(false)) {
                        icontray = true;
                    }
                    if (runTorrent(false)) {
                        icontray = true;
                    }
                    if ((icontray) && (prop.isEnabledAudioAdvisor())) {
                        try {
                            AudioPlay.playWav();
                        } catch (UnsupportedAudioFileException ex) {
                            error.launch(ex, getClass());
                        } catch (LineUnavailableException ex) {
                            error.launch(ex, getClass());
                        } catch (IOException ex) {
                            error.launch(ex, getClass(), null);
                        }
                    }
                    fireNewJFrameEvent(icontray, prop.getLastDateTimeRefresh());
                }// end run
            }, delay, delay);
        } catch (IllegalStateException ex) {
            error.launch(ex, getClass());
        }
    }

    /**
     * Esegue la parte rss itasa
     *
     * @param first
     *            primo lancio
     * @return true se ci sono nuovi feed, false altrimenti
     */
    private boolean runItasa(boolean first) {
        boolean status = false;
        if (prop.hasItasaOption()) {
            ArrayList<Object[]> feedIta, feedMyita;
            if (Lang.verifyTextNotNull(prop.getItasaFeedURL())) {
                feedIta = getFeedRss(prop.getItasaFeedURL(), lastItasa, ITASA,
                        false);
                if ((feedIta != null) && (feedIta.size() > 0)) {
                    if (!first) {
                        status = true;
                    }
                    lastItasa = (String) feedIta.get(0)[1];
                    fireTableRssEvent(feedIta, ITASA);
                }
            }
            if (Lang.verifyTextNotNull(prop.getMyitasaFeedURL())) {
                feedMyita = getFeedRss(prop.getMyitasaFeedURL(), lastMyItasa,
                        MYITASA, prop.isAutoDownloadMyItasa());
                if ((feedMyita != null) && (feedMyita.size() > 0)) {
                    if (!first) {
                        status = true;
                    }
                    lastMyItasa = (String) feedMyita.get(0)[1];
                    fireTableRssEvent(feedMyita, MYITASA);
                }
            }
        }
        return status;
    }

    /**
     * Esegue la parte rss subsfactory
     *
     * @param first
     *            primo lancio
     * @return true se ci sono nuovi feed, false altrimenti
     */
    private boolean runSubsfactory(boolean first) {
        boolean status = false;
        if ((prop.hasSubsfactoryOption())
                && (Lang.verifyTextNotNull(prop.getSubsfactoryFeedURL()))) {
            ArrayList<Object[]> feed = getFeedRss(prop.getSubsfactoryFeedURL(),
                    lastSubsf, SUBSF, false);
            if ((feed != null) && (feed.size() > 0)) {
                if (!first) {
                    status = true;
                }
                lastSubsf = (String) feed.get(0)[1];
                fireTableRssEvent(feed, SUBSF);
            }
        }
        return status;
    }

    /**
     * Esegue la parte rss torrent
     *
     * @param first
     *            primo lancio
     * @return true se ci sono nuovi feed, false altrimenti
     */
    private boolean runTorrent(boolean first) {
        boolean status = false;
        if (prop.hasTorrentOption()) {
            ArrayList<Object[]> feedEz, feedBt;
            feedEz = getFeedRss(RSS_TORRENT_EZTV, lastEztv, EZTV, false);
            feedBt = getFeedRss(RSS_TORRENT_BTCHAT, lastBtchat, BTCHAT, false);
            if ((feedEz != null) && (feedEz.size() > 0)) {
                if (!first) {
                    status = true;
                }
                lastEztv = (String) feedEz.get(0)[1];
                fireTableRssEvent(feedEz, EZTV);
            }
            if ((feedBt != null) && (feedBt.size() > 0)) {
                if (!first) {
                    status = true;
                }
                lastBtchat = (String) feedBt.get(0)[1];
                fireTableRssEvent(feedBt, BTCHAT);
            }
        }
        return status;
    }

    /**
     * Interrompe il timer attuale e ne fa partire uno nuovo col nuovo
     * intervallo
     */
    public void stopAndRestartTimer() {
        timer.cancel();
        timer.purge();
        int delay = Lang.stringToInt(prop.getRefreshInterval()) * 60000;
        runTimer(delay);
    }

    /**
     * Sostituisce la treemap delle regole con quella creata dal mediator
     *
     * @param temp
     *            treepam regole
     */
    public void saveMap(TreeMap<KeyRule, ValueRule> temp) {
        try {
            new Xml().writeMap(temp);
            mapRules = temp;
            fireNewTextPaneEvent("Regola/e memorizzate", MyTextPaneEvent.OK);
        } catch (IOException ex) {
            error.launch(ex, getClass(), null);
        }
    }

    /**
     * converte la treemap delle regole in arraylist di String[]
     *
     * @return arraylist regole
     */
    private ArrayList<Object[]> convertTreemapToArraylist() {
        ArrayList<Object[]> matrix = null;
        if (mapRules.size() > 0) {
            Iterator it = mapRules.keySet().iterator();
            matrix = new ArrayList<Object[]>();
            while (it.hasNext()) {
                KeyRule key = (KeyRule) it.next();
                ValueRule value = mapRules.get(key);
                matrix.add(new Object[]{key.getName(), key.getSeason(),
                            key.getQuality(), value.getPath(), value.getStatus(),
                            value.getDay(), Boolean.valueOf(value.isRename())});
            }
        }
        return matrix;
    }

    /** Carica l'xml delle regole */
    public void loadXml() {
        if (prop.isEnabledCustomDestinationFolder()) {
            Xml x = new Xml();
            try {
                mapRules = x.initializeReader();
                if (mapRules != null) {
                    fireTableXmlEvent(convertTreemapToArraylist());
                }
            } catch (JDOMException ex) {
                error.launch(ex, getClass());
            } catch (IOException ex) {
                error.launch(ex, getClass(), null);
            }
        }
    }

    /**
     * Verifica se il nome non presenta la parola "stagione"
     *
     * @param name
     *            nome da controllare
     * @return risultato controllo
     */
    private boolean isNotStagione(String name) {
        boolean check = true;
        String[] array = name.split(" ");
        for (int i = 0; i < array.length; i++) {
            String confronta = array[i].toLowerCase();
            if (confronta.equals("stagione") || confronta.equals("season")
                    || confronta.equals("completa")) {
                check = false;
                break;
            }
        }
        return check;
    }

    public void bruteRefreshRSS() {
        fireNewTextPaneEvent("Timer in fase di reinizializzazione.",
                MyTextPaneEvent.OK);
        runItasa(false);
        runSubsfactory(false);
        runTorrent(false);
        stopAndRestartTimer();
        fireNewTextPaneEvent("Timer restart ok.", MyTextPaneEvent.OK);
    }

    public void restartApplication() {
        FeedWorkerClient.getApplication().restart(null);
    }

    /**
     * restituice l'array con le informazioni sulle versioni video
     *
     * @return array versioni video
     */
    public String[] getQuality() {
        return QUALITY;
    }

    private String getSynoId(InputStream is) {
        String id = null;
        /*
         * JSONValue value = JSONParser.parse(jSon); JSONArray arr =
         * value.isArray(); for (int i = 0; i < arr.size(); i++) { JSONObject
         * obj = arr.get(I).isObject(); if (obj != null)
         * recordList.add(getProductAsRecord(obj, false)); }
         * record.setAttribute("id", JSONUtil.getLong("id", prodObj));
         * record.setAttribute("code", JSONUtil.getString("code", prodObj));
         * record.setAttribute("name", JSONUtil.getString("name", prodObj));
         * record.setAttribute("creationDate", JSONUtil.getDate("creationDate",
         * prodObj));
         */
        return id;
    }

    /**
     * Stampa lo stato del download redirectory coi download in corso o nessun
     * download
     */
    public void synoStatus() {
        String url = "http://" + prop.getCifsShareLocation()
                + ":5000/download/download_redirector.cgi";
        String filename = "         \"filename\" : \"";
        String progress = "         \"progress\" : \"";
        String itemsNull = "   \"items\" : [],";
        String dss = "Download Station Synology: ";
        try {
            Http http = new Http();
            String synoID = http.synoConnectGetID(url,
                    prop.getCifsShareUsername(), prop.getCifsSharePassword());
            http.closeClient();
            if (Lang.verifyTextNotNull(synoID)) {
                http = new Http();
                InputStream is = http.synoStatus(url, synoID);
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(is));
                String line, _filename = null, _progress = null;
                while ((line = br.readLine()) != null) {
                    if (line.equals(itemsNull)) {
                        fireNewTextPaneEvent(dss
                                + "Non ci sono download in corso",
                                MyTextPaneEvent.SYNOLOGY);
                        break;
                    } else if (line.length() > filename.length()) {
                        String _substring = line.substring(0, filename.length());
                        if (_substring.equals(filename)) {
                            _filename = line.substring(filename.length(),
                                    line.length() - 2);
                        } else if (_substring.equals(progress)) {
                            _progress = line.substring(progress.length(),
                                    line.length() - 2);
                        }
                    }
                    if (Lang.verifyTextNotNull(_progress)) {
                        fireNewTextPaneEvent(dss + _filename + " " + _progress,
                                MyTextPaneEvent.SYNOLOGY);
                        _progress = null;
                    }
                }
            }
        } catch (IllegalStateException ex) {
            error.launch(ex, getClass());
        } catch (IOException ex) {
            error.launch(ex, getClass(), null);
        }
    }

    /** effettua la move video sul synology */
    public void synoMoveVideo() {
        Samba s = Samba.getIstance(prop.getCifsShareLocation(),
                prop.getCifsSharePath(), prop.getCifsShareDomain(),
                prop.getCifsShareUsername(), prop.getCifsSharePassword());
        try {
            analyzeVideoSamba(s, s.listDir(null, "avi"));
            analyzeVideoSamba(s, s.listDir(null, "mkv"));
        } catch (MalformedURLException ex) {
            error.launch(ex, getClass(), null);
        } catch (SmbException ex) {
            error.launch(ex, getClass(), null);
        }
    }

    /**
     * Analizza i nomi dei file e se per ciascuno trova una corrispondenza tra
     * le regole, sposta il file nel path opportuno
     *
     * @param s
     *            istanza samba
     * @param fileList
     *            array di nomi di file
     */
    private void analyzeVideoSamba(Samba s, String[] fileList) {
        for (int i = 0; i < fileList.length; i++) {
            String name = fileList[i];
            if (name.toLowerCase().contains(SPLIT_HDTV)) {
                KeyRule key = parsingNamefile(name.toLowerCase(), SPLIT_HDTV);
                String dest = mapPath(key);
                if (dest != null) {
                    try {
                        String[] _array = name.toLowerCase().split(SPLIT_HDTV)[0].split("\\.");
                        int pos = Common.searchPosSeries(_array);
                        int conta = 0;
                        for (int j = 0; j < pos; j++) {
                            conta += _array[j].length() + 1;
                        }
                        String newName = name.substring(conta + 4);
                        newName.replaceAll("\\.", " ");
                        s.moveFile(name, dest, newName);
                        fireNewTextPaneEvent(
                                "Spostato " + name + " in " + dest,
                                MyTextPaneEvent.SYNOLOGY);
                    } catch (SmbException ex) {
                        error.launch(ex, getClass(), name);
                    } catch (IOException ex) {
                        error.launch(ex, getClass(), null);
                    }
                }
            }
        }
    }

    /**
     * Effettua l'inserimento dei link al download redirectory del synology
     *
     * @param link
     *            Arraylist di link
     */
    public void synoDownloadRedirectory(ArrayList<String> link) {
        Http http = new Http();
        String url = "http://" + prop.getCifsShareLocation()
                + ":5000/download/download_redirector.cgi";
        try {
            String synoID = http.synoConnectGetID(url,
                    prop.getCifsShareUsername(), prop.getCifsSharePassword());
            http.closeClient();
            if (Lang.verifyTextNotNull(synoID)) {
                for (int i = 0; i < link.size(); i++) {
                    http = new Http();
                    http.synoAddLink(url, synoID, link.get(i));
                    http.closeClient();
                }
                fireNewTextPaneEvent(
                        "link inviati al download redirectory Synology",
                        MyTextPaneEvent.SYNOLOGY);
            }
        } catch (IOException ex) {
            error.launch(ex, getClass(), null);
        }
    }

    /** Pulisce i task completati */
    public void synoClearFinish() {
        Http http = new Http();
        String url = "http://" + prop.getCifsShareLocation()
                + ":5000/download/download_redirector.cgi";
        try {
            String synoID = http.synoConnectGetID(url,
                    prop.getCifsShareUsername(), prop.getCifsSharePassword());
            http.closeClient();
            if (Lang.verifyTextNotNull(synoID)) {
                http = new Http();
                http.synoClearTask(url, synoID, prop.getCifsShareUsername());
                http.closeClient();
                fireNewTextPaneEvent(
                        "Download Station Synology: cancellati task completati.",
                        MyTextPaneEvent.SYNOLOGY);
            }
        } catch (IOException ex) {
            error.launch(ex, getClass(), null);
        }
    }

    public void setDownloadThreadListener(MyTextPaneEventListener listener) {
        mytpel = listener;
    }

    /**
     * Stampa il messaggio di alert invocando il metodo fire opportuno
     *
     * @param msg
     *            testo da stampare
     */
    private void printAlert(String msg) {
        fireNewTextPaneEvent(msg, MyTextPaneEvent.ALERT);
    }

    /**
     * Permette alla classe di registrarsi per l'evento tablerss
     *
     * @param listener
     *            evento tablerss
     */
    public synchronized void addTableRssEventListener(
            TableRssEventListener listener) {
        listenerTableRss.add(listener);
    }

    /**
     * Permette alla classe di de-registrarsi per l'evento tablerss
     *
     * @param listener
     *            evento tablerss
     */
    public synchronized void removeTableRssEventListener(
            TableRssEventListener listener) {
        listenerTableRss.remove(listener);
    }

    private synchronized void fireTableRssEvent(ArrayList<Object[]> alObj,
            String source) {
        TableRssEvent event = new TableRssEvent(this, alObj, source);
        Iterator listeners = listenerTableRss.iterator();
        while (listeners.hasNext()) {
            TableRssEventListener myel = (TableRssEventListener) listeners.next();
            if (myel != null) {
                myel.objReceived(event);
            }
        }
    }

    /**
     * Permette alla classe di registrarsi per l'evento tablexml
     *
     * @param listener
     *            evento tablexml
     */
    public synchronized void addTableXmlEventListener(
            TableXmlEventListener listener) {
        listenerTableXml.add(listener);
    }

    /**
     * Permette alla classe di de-registrarsi per l'evento tablexml
     *
     * @param listener
     *            evento tablexml
     */
    public synchronized void removeTableXmlEventListener(
            TableXmlEventListener listener) {
        listenerTableXml.remove(listener);
    }

    private synchronized void fireTableXmlEvent(ArrayList<Object[]> alObj) {
        TableXmlEvent event = new TableXmlEvent(this, alObj);
        Iterator listeners = listenerTableXml.iterator();
        while (listeners.hasNext()) {
            TableXmlEventListener myel = (TableXmlEventListener) listeners.next();
            if (myel != null) {
                myel.objReceived(event);
            }
        }
    }

    /**
     * Permette alla classe di registrarsi per l'evento textpane
     *
     * @param listener
     *            evento textpane
     */
    public synchronized void addMyTextPaneEventListener(
            MyTextPaneEventListener listener) {
        listenerTextPane.add(listener);
    }

    /**
     * Permette alla classe di de-registrarsi per l'evento textpane
     *
     * @param listener
     *            evento textpane
     */
    public synchronized void removeMyTextPaneEventListener(
            MyTextPaneEventListener listener) {
        listenerTextPane.remove(listener);
    }

    private synchronized void fireNewTextPaneEvent(String msg, String type) {
        MyTextPaneEvent event = new MyTextPaneEvent(this, msg, type);
        Iterator listeners = listenerTextPane.iterator();
        while (listeners.hasNext()) {
            MyTextPaneEventListener myel = (MyTextPaneEventListener) listeners.next();
            myel.objReceived(event);
        }
    }

    /**
     * Permette alla classe di registrarsi per l'evento jframe
     *
     * @param listener
     *            evento jframe
     */
    public synchronized void addMyJFrameEventListener(
            MyJFrameEventListener listener) {
        listenerJFrame.add(listener);
    }

    /**
     * Permette alla classe di de-registrarsi per l'evento jframe
     *
     * @param listener
     *            evento jframe
     */
    public synchronized void removeMyJFrameEventListener(
            MyJFrameEventListener listener) {
        listenerJFrame.remove(listener);
    }

    private synchronized void fireNewJFrameEvent(boolean _icontray,
            final String _data) {
        final boolean icontray = _icontray;
        final String data = _data;
        /*
         * Nota: E' stato necessario utilizzare il metodo syncExec perchè
         * l'interazione con il thread UI di SWT che gestisce la SysTray può
         * avvenire solo in questo modo. In caso contrario l'azione su oggetti
         * SWT che vivono in uno specifico thread (UI thread) da un altro thread
         * come in questo caso causa una SWTException: Invalid Thread Access.
         */
        Display.getDefault().syncExec(new Runnable() {

            @Override
            public void run() {
                MyJFrameEvent event = new MyJFrameEvent(this, icontray, data);
                Iterator listeners = listenerJFrame.iterator();
                while (listeners.hasNext()) {
                    MyJFrameEventListener myel = (MyJFrameEventListener) listeners.next();
                    myel.objReceived(event);
                }
            }
        });
    }
}