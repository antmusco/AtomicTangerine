package atomic.comic;

import java.util.Map;
import java.util.TreeMap;

/**
 * String enumeration which indicates the type of votes users can make for a Comic.
 *
 * @author Anthony G. Musco
 */
public enum ComicVote {

    UPVOTE("UPVOTE"),
    DOWNVOTE("DOWNVOTE");

    private String vote;
    private static Map<String, ComicVote> voteMap = new TreeMap<>();

    /**
     * Static operation which inserts all enum values into the vote map.
     */
    static {
        for (ComicVote jp : ComicVote.values()) {
            voteMap.put(jp.toString(), jp);
        }
    }

    /**
     * Constructor which binds the vote String to it's enum value.
     *
     * @param request The String representation of the vote.
     */
    ComicVote(String request) {
        this.vote = request;
    }

    /**
     * Static function which parses a vote from a String parameter.
     *
     * @param vote The String representation of the vote, such as what is submitted in JSON.
     * @return The appropriate ComicRequest enum.
     */
    public static ComicVote fromString(String vote) {

        if (voteMap.containsKey(vote))
            return voteMap.get(vote);
        else
            throw new IllegalArgumentException("ComicVote not found: " + vote);

    }

    /**
     * Public method which returns the indicated vote in it's String form.
     *
     * @return The vote as a String.
     */
    public String toString() {

        return vote;

    }

}
