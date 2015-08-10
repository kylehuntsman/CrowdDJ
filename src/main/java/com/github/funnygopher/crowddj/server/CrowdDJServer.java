package com.github.funnygopher.crowddj.server;

import com.github.funnygopher.crowddj.CrowdDJ;
import com.github.funnygopher.crowddj.jetty.AnybodyHomeHandler;
import com.github.funnygopher.crowddj.jetty.PlaybackHandler;
import com.github.funnygopher.crowddj.jetty.PlaylistHandler;
import com.github.funnygopher.crowddj.player.Player;
import com.github.funnygopher.crowddj.playlist.Playlist;
import com.github.funnygopher.crowddj.util.Property;
import com.github.funnygopher.crowddj.voting.VotingBooth;
import javafx.scene.control.TextInputDialog;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;

import java.net.BindException;
import java.util.Optional;

public class CrowdDJServer {

    private Server server;

    public CrowdDJServer(Player player, Playlist playlist, VotingBooth votingBooth) {
        int port = CrowdDJ.getProperties().getIntProperty(Property.PORT);
        server = new Server(port);

        ContextHandler playbackContext = new ContextHandler();
        playbackContext.setContextPath("/playback");
        playbackContext.setHandler(new PlaybackHandler(player));

        ContextHandler playlistContext = new ContextHandler();
        playlistContext.setContextPath("/playlist");
        playlistContext.setHandler(new PlaylistHandler(playlist, votingBooth));

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
