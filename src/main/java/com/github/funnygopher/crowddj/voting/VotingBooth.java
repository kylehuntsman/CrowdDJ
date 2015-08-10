package com.github.funnygopher.crowddj.voting;

import com.github.funnygopher.crowddj.voting.Voteable;

import java.util.List;

public interface VotingBooth<T extends Voteable> {

    void vote(T voteable, String whoVoted);
    List<T> tallyVotes();
    T tallyVotesNoTies();
    boolean isActive();
    int getNumberOfVotes();
}
