package com.github.funnygopher.crowddj.jetty;

import com.github.funnygopher.crowddj.playlist.Playlist;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

public class PlaylistHandler extends AbstractHandler {

	private Playlist playlist;

	public PlaylistHandler(Playlist playlist) {
		this.playlist = playlist;
	}

	@Override
	public void handle(String s, Request request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
		httpServletResponse.setContentType("text/xml; charset=UTF-8");
		httpServletResponse.setStatus(HttpServletResponse.SC_OK);

		String fileURI = httpServletRequest.getParameter("vote");
		if(fileURI != null) {
			File songFile = new File(fileURI);
			playlist.vote(songFile);
		}

		String xml = playlist.toXML();
		httpServletResponse.getWriter().println(xml);
		request.setHandled(true);
	}
}
