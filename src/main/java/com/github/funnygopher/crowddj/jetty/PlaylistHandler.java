package com.github.funnygopher.crowddj.jetty;

import com.github.funnygopher.crowddj.playlist.Playlist;
import com.github.funnygopher.crowddj.playlist.Song;
import com.github.funnygopher.crowddj.util.SearchParty;
import com.github.funnygopher.crowddj.voting.VotingBooth;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

public class PlaylistHandler extends AbstractHandler {

    private Playlist playlist;
    private VotingBooth<Song> votingBooth;

	public PlaylistHandler(Playlist playlist, VotingBooth<Song> votingBooth) {
        this.playlist = playlist;
        this.votingBooth = votingBooth;
	}

	@Override
	public void handle(String s, Request request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
		httpServletResponse.setContentType("text/xml; charset=UTF-8");
		httpServletResponse.setStatus(HttpServletResponse.SC_OK);

		String fileURI = httpServletRequest.getParameter("vote");
        String user = httpServletRequest.getParameter("user");

		if(fileURI != null && user != null) {
			File songFile = new File(fileURI);
            SearchParty<Song> party = playlist.search(songFile);
            if(party.found()) {
                Song song = party.rescue();
                votingBooth.vote(song, user);
            }
		}

		String xml = playlist.toXML();
		httpServletResponse.getWriter().println(xml);
		request.setHandled(true);
	}
}
