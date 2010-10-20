package org.feedworker.client.frontend;

//IMPORT JAVA
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.URISyntaxException;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import org.feedworker.client.ApplicationSettings;
import org.feedworker.client.FeedWorkerClient;
import org.feedworker.client.frontend.events.MyJFrameEvent;
import org.feedworker.client.frontend.events.MyJFrameEventListener;
import org.jfacility.java.lang.MySystem;

/**Gui base per java 1.5
 * 
 * @author luca judge
 */
public class MainJF extends JFrame implements WindowListener, MyJFrameEventListener {

    private final Dimension SCREEN_SIZE = new Dimension(1024, 768);
    private final Dimension TAB_SIZE = new Dimension(1024, 580);
    protected paneSetting settingsJP;
    protected JTabbedPane mainJTP;
    protected LogJTP logJTP;
    protected Mediator proxy = Mediator.getIstance();
    private ApplicationSettings prop = ApplicationSettings.getIstance();
    private ItasaJP itasaJP;
    private TorrentJP torrentJP;
    private SubsfactoryJP subsfactoryJP;

    /** Costruttore */
    public MainJF() {
        super();
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setPreferredSize(SCREEN_SIZE);
        this.setMinimumSize(SCREEN_SIZE);
        this.setTitle(FeedWorkerClient.getApplication().getName() + " build "
                + FeedWorkerClient.getApplication().getBuildNumber() + " by "
                + FeedWorkerClient.getApplication().getAuthor());
        this.setIconImage(FeedWorkerClient.getApplication().getIcon());
        initializeMenuBar();
        initializeComponents();
        initListeners();
    }

    /** Inizializzo i componenti per la GUI */
    private void initializeComponents() {
        this.setLayout(new BorderLayout());
        mainJTP = new JTabbedPane();
        mainJTP.setPreferredSize(TAB_SIZE);
        this.add(mainJTP, BorderLayout.CENTER);
        settingsJP = paneSetting.getPanel();

        if (prop.hasItasaOption()) {
            itasaJP = ItasaJP.getPanel();
            mainJTP.addTab("Itasa", itasaJP);
        }
        if (prop.hasSubsfactoryOption()) {
            subsfactoryJP = SubsfactoryJP.getPanel();
            mainJTP.addTab("Subsfactory", subsfactoryJP);
        }
        if (prop.hasTorrentOption()) {
            torrentJP = TorrentJP.getPanel();
            mainJTP.addTab("Torrent", torrentJP);
        }
        if (prop.isEnabledCustomDestinationFolder()) {
            mainJTP.addTab("Subtitle Destination", paneRules.getPanel());
        }
        mainJTP.addTab("Settings", settingsJP);

        logJTP = new LogJTP();
        JScrollPane jScrollText1 = new JScrollPane(logJTP);
        jScrollText1.setPreferredSize(new Dimension(1000, 140));
        add(jScrollText1, BorderLayout.SOUTH);

        logJTP.appendOK("Versione java in uso: " + MySystem.getJavaVersion());

        if (prop.isApplicationFirstTimeUsed()) {
            mainJTP.setSelectedComponent(settingsJP);
            changeEnabledButton(false);
            logJTP.appendOK("Benvenuto al primo utilizzo.");
            logJTP.appendAlert("Per poter usare il client, "
                    + "devi configurare le impostazioni presenti nella specifica sezione");
        } else {
            settingsJP.settingsValue();
            logJTP.appendOK("Ciao " + prop.getItasaUsername());
            logJTP.appendOK("Impostazioni caricate da "
                    + prop.getSettingsFilename());
        }

        pack();
    }

    public void initializeSysTray() throws URISyntaxException {
        setVisible(true);
        setState(Frame.ICONIFIED);
    }

    /** inizializza la barra di menù */
    private void initializeMenuBar() {
        JMenuBar applicationJMB = new JMenuBar();

        JMenu fileJM = new JMenu(" Operazioni ");
        applicationJMB.add(fileJM);

        JMenuItem clearLogJMI = new JMenuItem(" Pulisci log ");
        clearLogJMI.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logJTP.setText(null);
            }
        });
        fileJM.add(clearLogJMI);

        JMenuItem bruteRefreshJMI = new JMenuItem(" Forza aggiornamento RSS");
        bruteRefreshJMI.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                proxy.bruteRefresh();
            }
        });
        fileJM.add(bruteRefreshJMI);

        JMenuItem restartJMI = new JMenuItem(" Riavvio ");
        restartJMI.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                proxy.restartApplication();
            }
        });
        fileJM.add(restartJMI);

        JMenuItem closeJMI = new JMenuItem(" Esci ");
        closeJMI.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                WindowEvent we = new WindowEvent(MainJF.this,
                        WindowEvent.WINDOW_CLOSING);
                we.setSource(new JMenuItem());
                MainJF.this.dispatchEvent(we);
            }
        });
        fileJM.add(closeJMI);

        JMenu nasJM = new JMenu(" NAS ");
        JMenuItem videoMoveJMI = new JMenuItem(" Video move ");
        JMenuItem taskStatusJMI = new JMenuItem(" Task status ");
        JMenuItem deleteCompletedTaskJMI = new JMenuItem(
                " Delete completed task ");
        videoMoveJMI.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                proxy.synoMoveVideo();
            }
        });
        taskStatusJMI.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                proxy.synoStatus();
            }
        });
        deleteCompletedTaskJMI.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                proxy.synoClearFinish();
            }
        });
        nasJM.add(videoMoveJMI);
        nasJM.add(taskStatusJMI);
        nasJM.add(deleteCompletedTaskJMI);
        applicationJMB.add(nasJM);

        JMenu helpJM = new JMenu(" Help ");
        applicationJMB.add(helpJM);
        // JMenuItem helpGeneralSettingsJMI = new
        // JMenuItem(" General Settings ");
        // JMenuItem helpItasaSettingsJMI = new JMenuItem(" Itasa Settings ");
        JMenuItem helpSubtitleRoleJMI = new JMenuItem(" Subtitle Rule ");
        helpSubtitleRoleJMI.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(getParent(), showRuleHelpPopup(),
                        "Come creare le regole per i sottotitoli",
                        JOptionPane.PLAIN_MESSAGE);
            }
        });
        helpJM.add(helpSubtitleRoleJMI);
        // helpJM.add(helpGeneralSettingsJMI);
        // helpJM.add(helpItasaSettingsJMI);
        // Install the menu bar in the frame
        setJMenuBar(applicationJMB);
    }

    /**
     * inizializza il pannello per l'help regola sub
     *
     * @return pannello helprolesub
     */
    private JPanel showRuleHelpPopup() {
        Dimension dim = new Dimension(550, 300);
        JPanel jpanel = new JPanel();
        jpanel.setPreferredSize(dim);
        jpanel.setVisible(true);
        JTextArea jtaHelp = new JTextArea();
        jtaHelp.setPreferredSize(dim);
        jtaHelp.append("Serie: inserire il nome del file scaricato sostituendo i \".\" con lo spazio");
        jtaHelp.append("\nesempio: Lost.s06e06.sub.itasa.srt -->lost (n.b. no spazio)");
        jtaHelp.append("\nesempio: Spartacus.Blood.And.Sand.s01e06.sub.itasa.srt -->"
                + "spartacus blood and sand");
        jtaHelp.append("\n\nStagione: immettere il numero della stagione per la quale si vuole "
                + "creare la regola");
        jtaHelp.append("\nad eccezione degli anime/cartoon dove la stagione molto probabilmente è "
                + "unica e quindi 1");
        jtaHelp.append("\n\nQualità: * = tutte le versioni dei sub ; normale = sub qualità standard");
        jtaHelp.append("\n720p = versione 720p; 1080p = versione 1080p; dvdrip = versione dvdrip");
        jtaHelp.append("\nbluray = versione bluray; hr = versione hr");
        jtaHelp.append("\n\\ = se avete già impostato una regola per una versione e volete che le "
                + "altre versioni");
        jtaHelp.append("\nvadino in un'altra cartella basterà selezionare questa opzione "
                + "\"differenziale\"");
        jtaHelp.append("\n\nPercorso: specificare il percorso dove desiderate che vi estragga il sub");
        jpanel.add(jtaHelp);
        return jpanel;
    }

    /** chiude l'applicazione */
    protected void applicationClose() {
        String temp = settingsJP.getDataAggiornamento();
        this.dispose();
        proxy.closeApp(temp);
    }

    @Override
    public void windowClosing(WindowEvent e) {
        int returnCode = JOptionPane.showConfirmDialog(this,
                "Sei sicuro di uscire?", "Info", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (returnCode == 1) {
            return;
        }
        applicationClose();
    }

    @Override
    public void objReceived(MyJFrameEvent evt) {
        if (evt.getDate() != null) {
            settingsJP.setDataAggiornamento(evt.getDate());
        }
        if (evt.getOperaz() != null) {
            if (evt.getOperaz().equalsIgnoreCase("ADD_PANE_RULEZ")) {
                mainJTP.addTab("Destinazione avanzata", paneRules.getPanel());
            } else if (evt.getOperaz().equalsIgnoreCase("REMOVE_PANE_RULEZ")) {
                mainJTP.remove(paneRules.getPanel());
            } else if (evt.getOperaz().equalsIgnoreCase("ENABLED_BUTTON")) {
                changeEnabledButton(true);
            }
        }
    }

    /**
     * risponde all'evento di cambiare lo stato (abilitato/disabilitato) dei
     * bottoni
     *
     * @param e
     *            stato
     */
    protected void changeEnabledButton(boolean e) {
        if (prop.hasItasaOption()) {
            itasaJP.setButtonEnabled(e);
        }
        if (prop.hasSubsfactoryOption()) {
            subsfactoryJP.setEnableButton(e);
        }
        if (prop.hasTorrentOption()) {
            torrentJP.setButtonEnabled(e);
        }
    }

    /** inizializza i listener per l'ascolto */
    private void initListeners() {
        addWindowListener(this);
        proxy.setTextPaneListener(logJTP);
        proxy.setFrameListener(this);
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }
} // end class
