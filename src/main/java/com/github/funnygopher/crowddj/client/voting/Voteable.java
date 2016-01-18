package com.github.funnygopher.crowddj.client.voting;

import javafx.beans.property.ReadOnlyIntegerProperty;

public interface Voteable extends Comparable<Voteable> {

    ReadOnlyIntegerProperty votesProperty();
    void vote();
    void unvote();
    void clearVotes();
}
