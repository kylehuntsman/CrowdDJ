package com.github.funnygopher.crowddj.client.server;

import com.github.funnygopher.crowddj.client.Jukebox;
import com.github.funnygopher.crowddj.client.database.DatabaseManager;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;

import java.net.BindException;

public class CrowdDJServer {

    private Server server;

    public CrowdDJServer(Jukebox jukebox, DatabaseManager databaseManager, int port) {
        server = new Server(port);

        ContextHandler playbackContext = new ContextHandler();
        playbackContext.setContextPath("/playback");
        playbackContext.setHandler(new PlaybackHandler(jukebox));

        ContextHandler playlistContext = new ContextHandler();
        playlistContext.setContextPath("/playlist");
        playlistContext.setHandler(new PlaylistHandler(databaseManager));

        ContextHandler anybodyHomeContext = new ContextHandler();
        anybodyHomeContext.setContextPath("/anybodyhome");
        anybodyHomeContext.setHandler(new AnybodyHomeHandler());

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{playbackContext, playlistContext, anybodyHomeContext});
        server.setHandler(handlers);
    }

    public void start() throws BindException {
        if(!server.isRunning()) {
            try {
                server.start();
            } catch (BindException e) {
                throw e;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        if(server.isRunning()) {
            try {
                server.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void forceStop() {
        try {
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
