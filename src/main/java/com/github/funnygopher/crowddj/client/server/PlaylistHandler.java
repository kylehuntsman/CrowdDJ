package com.github.funnygopher.crowddj.client.server;

import com.github.funnygopher.crowddj.client.song.Song;
import com.github.funnygopher.crowddj.client.database.DatabaseManager;
import com.github.funnygopher.crowddj.client.database.SongDao;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class PlaylistHandler extends AbstractHandler {

    private DatabaseManager mDatabaseManager;
    private SongDao mSongDao;

	public PlaylistHandler(DatabaseManager databaseManager) {
        mDatabaseManager = databaseManager;
        mSongDao = new SongDao(mDatabaseManager);
	}

	@Override
	public void handle(String s, Request request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
        String id = httpServletRequest.getParameter("queue"); // The id of the song to queue
        String user = httpServletRequest.getParameter("user"); // The user who submitted the queue request

        // Ex: http://www.crowddj.com/playlist?queue=1&user=johndoe
        // id = 1
        // user = johndoe

		if(id != null && user != null) {
            Song song = mSongDao.get(Long.valueOf(id));
            if(song != null) {
                // TODO: Add song to jukebox queue. This needs to be a generic call to allow multiple jukebox modes.
            }
            request.setHandled(true);
		}

        httpServletResponse.setContentType("text/json; charset=UTF-8");
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        // TODO: This needs to return more information about the server. Current mode, version of software, etc.
		//String json = musicLibrary.toJson();
		//httpServletResponse.getWriter().println(json);
		request.setHandled(true);
	}
}
