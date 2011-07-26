package org.feedworker.client;

import java.awt.EventQueue;
import java.awt.Image;
import java.net.URISyntaxException;

import javax.swing.JOptionPane;

import org.feedworker.client.frontend.jfMain;
import org.feedworker.core.Kernel;
import org.feedworker.util.Common;
import org.feedworker.exception.Logging;
import org.feedworker.util.ResourceLocator;

import org.jfacility.java.lang.JVM;

import org.opensanskrit.application.Application;
import org.opensanskrit.application.LookAndFeel;
import org.opensanskrit.exception.AlreadyStartedApplicationException;
import org.opensanskrit.exception.NotAvailableLookAndFeelException;
import org.opensanskrit.widget.SplashScreen;
import org.opensanskrit.widget.interfaces.SplashableWindow;

/**
 * Client
 * 
 * @author luca judge
 */
public class FeedWorkerClient {

    public static String ICON_FILENAME = "ApplicationIcon.png";
    private static Kernel core;
    private static Application feedWorker;
    private static ApplicationSettings feedWorkerSettings;
    private static int iteration = 11;
    private static boolean debug, autodownload;

    public static Application getApplication() {
        return feedWorker;
    }

    public static void main(String args[]) {
        debug = false;
        autodownload = true;
        for (int i=0; i<args.length; i++)
            verifyParams(args[i].toLowerCase());
        JVM jvm = new JVM();
        if (JVM.isRuntimeJavaSun()){
            final SplashableWindow splash;
            final Image splashImage = Common.getResourceImage("SplashImage.png");
            splash = SplashScreen.getInstance(iteration, splashImage);
            splash.start();
            splash.updateStartupState("Inizializzazione Feedworker");
            ResourceLocator.setWorkspace();

            feedWorker = Application.getInstance(debug);
            feedWorkerSettings = ApplicationSettings.getIstance();

            feedWorker.setName("FeedWorker");
            feedWorker.setAuthor("Luka Judge");
            feedWorker.setIcon(Common.getResourceIcon(ICON_FILENAME));
            feedWorker.enableSingleInstance(true);

            splash.updateStartupState("Checking JVM ...");
            if (!jvm.isOrLater(16)) {
                JOptionPane.showMessageDialog(null,
                        "E' necessario disporre di una versione della JVM >= 1.6",
                        feedWorker.getName(), JOptionPane.ERROR_MESSAGE);
                feedWorker.shutdown();
            } else {
                try {
                    splash.updateStartupState("Finding other FeedWorker instance ...");
                    feedWorker.start();
                    splash.updateStartupState("Preparing Kernel instance ...");
                    core = Kernel.getIstance(debug);
                    splash.updateStartupState("Setting Look & Feel ...");
                    LookAndFeel laf = feedWorker.getLookAndFeelInstance();
                    try {
                        laf.addJavaLAF();
                        laf.addJTattooLAF();
                        laf.addSyntheticaStandardLAF();
                        laf.addSyntheticaFreeLAF();
                        laf.addSyntheticaNotFreeLAF();
                        laf.setLookAndFeel(feedWorkerSettings.getApplicationLookAndFeel());
                    } catch (NotAvailableLookAndFeelException e) {
                        laf.setLookAndFeel();
                    }
                    splash.updateStartupState("Loading Application settings ...");
                    ApplicationSettings.getIstance();
                    splash.updateStartupState("Preparing Application logging ...");
                    Logging.getIstance();
                    splash.updateStartupState("Running ...");
                    final boolean auto = autodownload;

                    EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {                        
                            splash.updateStartupState("Loading GUI ...");
                            jfMain jframe = new jfMain();
                            splash.updateStartupState("Loading xml ...");
                            core.loadXml();
                            core.loadItasaSeries();
                            splash.updateStartupState("Initializing RSS...");
                            core.runRss(auto);
                            core.searchDay(0);
                            splash.close();
                            if (!ApplicationSettings.getIstance().isEnabledIconizedRun())
                                jframe.setVisible(true);
                            else {
                                try {
                                    jframe.initializeSystemTray();
                                } catch (URISyntaxException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }//end run
                    }); //end runnable EventQueue.invokeLater
                } catch (AlreadyStartedApplicationException e) {
                    JOptionPane.showMessageDialog(null,
                            "C'è già la stessa applicazione avviata.",
                            feedWorker.getName(), JOptionPane.ERROR_MESSAGE);
                    feedWorker.shutdown();
                }
            }
        } else if (JVM.isRuntimeOpenJDK()){
            System.out.println("Inizializzazione Feedworker");
            ResourceLocator.setWorkspace();

            feedWorker = Application.getInstance(debug);
            feedWorkerSettings = ApplicationSettings.getIstance();

            feedWorker.setName("FeedWorker");
            feedWorker.setAuthor("Luka Judge");
            feedWorker.setIcon(Common.getResourceIcon(ICON_FILENAME));
            feedWorker.enableSingleInstance(true);

            System.out.println("Checking JVM ...");
            if (!jvm.isOrLater(16)) {
                JOptionPane.showMessageDialog(null,
                        "E' necessario disporre di una versione della JVM >= 1.6",
                        feedWorker.getName(), JOptionPane.ERROR_MESSAGE);
                feedWorker.shutdown();
            } else {
                try {
                    System.out.println("Finding other FeedWorker instance ...");
                    feedWorker.start();
                    System.out.println("Preparing Kernel instance ...");
                    core = Kernel.getIstance(debug);
                    System.out.println("Setting Look & Feel ...");
                    LookAndFeel laf = feedWorker.getLookAndFeelInstance();
                    try {
                        laf.addJavaLAF();
                        laf.addJTattooLAF();
                        laf.addSyntheticaStandardLAF();
                        laf.addSyntheticaFreeLAF();
                        laf.addSyntheticaNotFreeLAF();
                        laf.setLookAndFeel(feedWorkerSettings.getApplicationLookAndFeel());
                    } catch (NotAvailableLookAndFeelException e) {
                        laf.setLookAndFeel();
                    }
                    System.out.println("Loading Application settings ...");
                    ApplicationSettings.getIstance();
                    System.out.println("Preparing Application logging ...");
                    Logging.getIstance();
                    System.out.println("Running ...");
                    final boolean auto = autodownload;

                    EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {                        
                            System.out.println("Loading GUI ...");
                            jfMain jframe = new jfMain();
                            System.out.println("Loading xml ...");
                            core.loadXml();
                            core.loadItasaSeries();
                            System.out.println("Initializing RSS...");
                            core.runRss(auto);
                            core.searchDay(0);
                            if (!ApplicationSettings.getIstance().isEnabledIconizedRun())
                                jframe.setVisible(true);
                            else {
                                try {
                                    jframe.initializeSystemTray();
                                } catch (URISyntaxException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }//end run
                    }); //end runnable EventQueue.invokeLater
                } catch (AlreadyStartedApplicationException e) {
                    JOptionPane.showMessageDialog(null,
                            "C'è già la stessa applicazione avviata.",
                            feedWorker.getName(), JOptionPane.ERROR_MESSAGE);
                    feedWorker.shutdown();
                }
            }
        } else {
            JOptionPane.showMessageDialog(null,
                    "E' necessario disporre di Java Sun/openJDK",
                    "FeedWorker", JOptionPane.ERROR_MESSAGE);
            Application.getInstance(debug).shutdown();
        }
    }
    
    private static void verifyParams(String param){
        if (param.equalsIgnoreCase("debug=true"))
            debug=true;
        else if (param.equalsIgnoreCase("autodownload=false"))
            autodownload=false;
    }
}