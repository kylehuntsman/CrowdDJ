package com.github.funnygopher.crowddj.client.voting;

import java.util.*;

public class SimpleVotingBooth<T extends Voteable> implements VotingBooth<T> {

    private List<T> votes;
    private Map<String, T> logs;
    private Random rand = new Random(System.currentTimeMillis());
    private boolean active;

    public SimpleVotingBooth() {
        votes = new ArrayList<>();
        logs = new HashMap<>();
        active = true;
    }

    public void vote(T voteable, String whoVoted) {
        if(logs.keySet().contains(whoVoted)) {
            Voteable prevVote = logs.get(whoVoted);
            prevVote.unvote();
            votes.remove(prevVote);
        }

        logs.put(whoVoted, voteable);
        voteable.vote();

        if(!votes.contains(voteable)) {
            votes.add(voteable);
        }
    }

    public List<T> tallyVotes() {
        List<T> winners = getWinners();
        votes.forEach(vote -> vote.clearVotes());
        votes.clear();
        logs.clear();
        return winners;
    }

    public T tallyVotesNoTies() {
        List<T> winners = tallyVotes();
        return winners.get(rand.nextInt(winners.size()));
    }

    public boolean isActive() {
        return active;
    }

    public int getNumberOfVotes() {
        return logs.keySet().size();
    }

    private List<T> getWinners() {
        List<T> winners = new ArrayList<>();
        Collections.sort(votes);
        int highestVote = votes.get(0).votesProperty().get();

        for (T vote : votes) {
            if(vote.votesProperty().get() == highestVote) {
                winners.add(vote);
            }
            if(vote.votesProperty().get() < highestVote) {
                break;
            }
        }

        return winners;
    }
}
