package com.github.funnygopher.crowddj.server;

import com.github.funnygopher.crowddj.Jukebox;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class PlaybackHandler extends AbstractHandler {

    private Jukebox mJukebox;

    public PlaybackHandler(Jukebox jukebox) {
		mJukebox = jukebox;
    }

    public void handle(String s, Request request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
        httpServletResponse.setContentType("text/html; charset=utf-8");
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);

		String command = httpServletRequest.getParameter("command");
		switch (command) {
			case "play":
				mJukebox.play();
				break;

			case "pause":
				mJukebox.pause();
				break;

			case "stop":
				mJukebox.stop();
				break;

			default:
				break;
		}

        httpServletResponse.getWriter().println(s);
        request.setHandled(true);
    }
}
