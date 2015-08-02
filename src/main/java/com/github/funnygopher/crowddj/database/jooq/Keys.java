/**
 * This class is generated by jOOQ
 */
package com.github.funnygopher.crowddj.database.jooq;


import com.github.funnygopher.crowddj.database.jooq.tables.Playlist;
import com.github.funnygopher.crowddj.database.jooq.tables.records.PlaylistRecord;
import org.jooq.Identity;
import org.jooq.UniqueKey;
import org.jooq.impl.AbstractKeys;

import javax.annotation.Generated;


/**
 * A class modelling foreign key relationships between tables of the <code>PUBLIC</code> 
 * schema
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.2"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

	// -------------------------------------------------------------------------
	// IDENTITY definitions
	// -------------------------------------------------------------------------

	public static final Identity<PlaylistRecord, Integer> IDENTITY_PLAYLIST = Identities0.IDENTITY_PLAYLIST;

	// -------------------------------------------------------------------------
	// UNIQUE and PRIMARY KEY definitions
	// -------------------------------------------------------------------------

	public static final UniqueKey<PlaylistRecord> CONSTRAINT_9 = UniqueKeys0.CONSTRAINT_9;

	// -------------------------------------------------------------------------
	// FOREIGN KEY definitions
	// -------------------------------------------------------------------------


	// -------------------------------------------------------------------------
	// [#1459] distribute members to avoid static initialisers > 64kb
	// -------------------------------------------------------------------------

	private static class Identities0 extends AbstractKeys {
		public static Identity<PlaylistRecord, Integer> IDENTITY_PLAYLIST = createIdentity(Playlist.PLAYLIST, Playlist.PLAYLIST.ID);
	}

	private static class UniqueKeys0 extends AbstractKeys {
		public static final UniqueKey<PlaylistRecord> CONSTRAINT_9 = createUniqueKey(Playlist.PLAYLIST, Playlist.PLAYLIST.ID);
	}
}