package org.feedworker.client.frontend.events;

import java.util.EventListener;

/**
 *
 * @author luca
 */
public interface FrameEventListener extends EventListener {
    /** intercetta l'evento frame e lo gestisce */
    public void objReceived(FrameEvent evt);
}