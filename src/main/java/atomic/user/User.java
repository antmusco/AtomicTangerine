package atomic.user;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.repackaged.org.joda.time.LocalDateTime;

import java.util.List;

/**
 * Created by Freya on 4/3/16.
 */
public class User {
    public User() {
    }

    private String gmail;
    private String username;
    private double expPoints;
    private LocalDateTime dateJoined;
    private Preferences preferences;
    private List<Key> comics;


    public String getGmail() {
        return gmail;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getExpPoints() {
        return expPoints;
    }

    public void setExpPoints(double expPoints) {
        this.expPoints = expPoints;
    }

    public LocalDateTime getDateJoined() {
        return dateJoined;
    }

    public void setDateJoined(LocalDateTime dateJoined) {
        this.dateJoined = dateJoined;
    }

    public Preferences getPreferences() {
        return preferences;
    }

    public void setPreferences(Preferences preferences) {
        this.preferences = preferences;
    }

    public List<Key> getComics() {
        return comics;
    }

    public void setComics(List<Key> comics) {
        this.comics = comics;
    }
}
