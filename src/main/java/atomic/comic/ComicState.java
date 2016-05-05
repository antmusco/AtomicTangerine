package atomic.comic;

import atomic.json.JsonProperty;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Gustavo on 4/14/16.
 */
public enum ComicState {

    DRAFT(JsonProperty.DRAFT.toString()),
    PUBLISHED(JsonProperty.PUBLISHED.toString());

    /**
     * The string representation of the kind of entity.
     */
    private String comicStateString;
    private static Map<String, ComicState> stateMap = new TreeMap<>();

    static {
        for (ComicState cs : ComicState.values()) {
            stateMap.put(cs.toString(), cs);
        }
    }

    /**
     * Constructor to initialize the comic state string.
     *
     * @param comicStateString The name of the comic state to initialize.
     */
    ComicState(String comicStateString) {
        this.comicStateString = comicStateString;
    }

    /**
     * Converts a String parameter to a ComicState enumeration value
     *
     * @param comicStateString The String to convert to a ComicState.
     * @return The ComicState represented by the String.
     * @throws IllegalArgumentException If the indicated String could not be matched to a ComicState.
     */
    public static ComicState fromString(String comicStateString) {
        if (stateMap.containsKey(comicStateString)) {
            return stateMap.get(comicStateString);
        } else {
            throw new IllegalArgumentException("Comic State not found: " + comicStateString);
        }
    }

    /**
     * @return The String representation of the ComicState.
     */
    public String toString() {
        return this.comicStateString;
    }
}
