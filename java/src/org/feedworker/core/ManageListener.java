package org.feedworker.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.feedworker.client.frontend.events.*;

/**
 *
 * @author luca
 */
public class ManageListener {

    private static List listenerTable = new ArrayList();
    private static List listenerJFrameO = new ArrayList();
    private static List listenerJFrameID = new ArrayList();
    private static List listenerTextPane = new ArrayList();
    private static List listenerStatusBar = new ArrayList();

    public static synchronized void addTableEventListener(
                                                    TableEventListener listener) {
        listenerTable.add(listener);
    }

    public static synchronized void addJFrameEventIconDateListener(
                                            JFrameEventIconDateListener listener) {
        listenerJFrameID.add(listener);
    }
    
    public static synchronized void addJFrameEventOperationListener(
                                            JFrameEventOperationListener listener) {
        listenerJFrameO.add(listener);
    }
    
    public static synchronized void addTextPaneEventListener(
                                                TextPaneEventListener listener) {
        listenerTextPane.add(listener);
    }
    
    public static synchronized void addStatusBarEventListener(
                                                StatusBarEventListener listener) {
        listenerStatusBar.add(listener);
    }

    public static synchronized void fireTableEvent(Object from, ArrayList<Object[]> alObj, 
            String dest) {
        TableEvent event = new TableEvent(from, alObj, dest);
        Iterator listeners = listenerTable.iterator();
        while (listeners.hasNext()) {
            TableEventListener myel = (TableEventListener) listeners.next();
            myel.objReceived(event);
        }
    }
    
    public static synchronized void fireJFrameEventIconData(Object from, boolean _icontray,
                                                            final String _data) {
        JFrameEventIconDate event = new JFrameEventIconDate(from, _icontray, _data);
        Iterator listeners = listenerJFrameID.iterator();
        while (listeners.hasNext()) {
            JFrameEventIconDateListener myel = (JFrameEventIconDateListener) listeners.next();
            myel.objReceived(event);
        }
    }

    public static synchronized void fireJFrameEventOperation(Object from, String oper) {
        JFrameEventOperation event = new JFrameEventOperation(from, oper);
        Iterator listeners = listenerJFrameO.iterator();
        while (listeners.hasNext()) {
            JFrameEventOperationListener myel = (JFrameEventOperationListener) listeners.next();
            myel.objReceived(event);
        }
    }

    public static synchronized void fireJFrameEventOperation(Object from, String oper, int max) {
        JFrameEventOperation event = new JFrameEventOperation(from, oper, max);
        Iterator listeners = listenerJFrameO.iterator();
        while (listeners.hasNext()) {
            JFrameEventOperationListener myel = (JFrameEventOperationListener) listeners.next();
            myel.objReceived(event);
        }
    }

    public static synchronized void fireTextPaneEvent(Object from, String msg, String type) {
        TextPaneEvent event = new TextPaneEvent(from, msg, type);
        Iterator listeners = listenerTextPane.iterator();
        while (listeners.hasNext()) {
            TextPaneEventListener myel = (TextPaneEventListener) listeners.next();
            myel.objReceived(event);
        }
        if (!msg.substring(0, 7).equals("Timeout"))
            fireStatusBarEvent(from, msg);
    }
    
    private static synchronized void fireStatusBarEvent(Object from, String msg) {
        StatusBarEvent event = new StatusBarEvent(from, msg);
        Iterator listeners = listenerStatusBar.iterator();
        while (listeners.hasNext()) {
            StatusBarEventListener myel = (StatusBarEventListener) listeners.next();
            myel.objReceived(event);
        }
    }
}
/*
    public static synchronized void removeMyTextPaneEventListener(
                                                    TextPaneEventListener listener) {
        listenerTP.remove(listener);
    }
*/