package com.github.funnygopher.crowddj.jetty;

import com.github.funnygopher.crowddj.javafx.AudioPlayer;
import com.github.funnygopher.crowddj.CrowdDJ;
import com.github.funnygopher.crowddj.SearchParty;
import com.github.funnygopher.crowddj.Song;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

public class PlaylistHandler extends AbstractHandler {

	private CrowdDJ crowdDJ;

	public PlaylistHandler(CrowdDJ crowdDJ) {
		this.crowdDJ = crowdDJ;
	}

	@Override
	public void handle(String s, Request request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
		httpServletResponse.setContentType("text/xml; charset=UTF-8");
		httpServletResponse.setStatus(HttpServletResponse.SC_OK);

		String fileURI = httpServletRequest.getParameter("vote");
		if(fileURI != null) {
			File songFile = new File(fileURI);
			SearchParty<Song> party = crowdDJ.getController().getPlayer().search(songFile);
			if(party.found()) {
				Song song = party.rescue();
				crowdDJ.getController().getPlayer().vote(song);
			}
		}

		StringBuilder xmlBuilder = new StringBuilder();
		AudioPlayer player = crowdDJ.getController().getPlayer();
		xmlBuilder.append("<playlist>");
		for(Song song : player.getPlaylist()) {
			xmlBuilder.append(song.toXML());
		}
		xmlBuilder.append("</playlist>");
		String betterXML = xmlBuilder.toString().replaceAll("&", "&amp;");
		httpServletResponse.getWriter().println(
				betterXML
		);
		request.setHandled(true);
	}
}
