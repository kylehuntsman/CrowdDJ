package com.github.funnygopher.crowddj.server;

import com.github.funnygopher.crowddj.CrowdDJ;
import com.github.funnygopher.crowddj.jetty.AnybodyHomeHandler;
import com.github.funnygopher.crowddj.jetty.PlaybackHandler;
import com.github.funnygopher.crowddj.jetty.PlaylistHandler;
import com.github.funnygopher.crowddj.player.Player;
import com.github.funnygopher.crowddj.playlist.Playlist;
import com.github.funnygopher.crowddj.util.Property;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;

public class CrowdDJServer {

    private Server server;

    public CrowdDJServer(Player player, Playlist playlist) {
        // Starts the web server, telling it to look for playback commands
        try {
            int port = CrowdDJ.getProperties().getIntProperty(Property.PORT);
            server = new Server(port);
            ContextHandler playbackContext = new ContextHandler();
            playbackContext.setContextPath("/playback");
            playbackContext.setHandler(new PlaybackHandler(player));

            ContextHandler playlistContext = new ContextHandler();
            playlistContext.setContextPath("/playlist");
            playlistContext.setHandler(new PlaylistHandler(playlist));

            ContextHandler anybodyHomeContext = new ContextHandler();
            anybodyHomeContext.setContextPath("/anybodyhome");
            anybodyHomeContext.setHandler(new AnybodyHomeHandler());

            HandlerList handlers = new HandlerList();
            handlers.setHandlers(new Handler[]{playbackContext, playlistContext, anybodyHomeContext});
            server.setHandler(handlers);
        } catch (Exception e) {
            System.err.append("Could not start server. Something is wrong...\n");
            e.printStackTrace();
        }
    }

    public void start() {
        if(!server.isRunning()) {
            try {
                server.start();
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
}
