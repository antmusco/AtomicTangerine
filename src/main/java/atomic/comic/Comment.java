package atomic.comic;

import atomic.data.DatastoreEntity;
import atomic.data.EntityKind;
import atomic.json.JsonProperty;
import atomic.json.Jsonable;
import atomic.json.NoUniqueKeyException;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.gson.JsonObject;

import java.util.Date;
import java.util.LinkedList;

/**
 * Class which represents a comment on a comic.
 *
 * @author Anthony G. Musco
 */
public class Comment extends DatastoreEntity implements Jsonable {

    /**
     * String representing the gmail of the user posting the comment.
     */
    private String commentorUserGmail;

    /**
     * String representing the gmail of the user who created the comic.
     */
    private String comicUserGmail;

    /**
     * String represnting the title of the comic.
     */
    private String comicTitle;

    /**
     * Date the comment was posted.
     */
    private Date datePosted;

    /**
     * String containing the comment itself.
     */
    private String comment;

    /**
     * Constructor which initializes all data values for this comment.
     * @param commentorUserGmail Gmail of the user who posted the comment.
     * @param comicUserGmail Gmail of the user who created the comic.
     * @param comicTitle Title of the comic to be commented on.
     * @param datePosted Date the comment was posted.
     * @throws NoUniqueKeyException Thrown if any of the parameters are null.
     */
    public Comment(String commentorUserGmail, String comicUserGmail, String comicTitle, Date datePosted)
        throws NoUniqueKeyException {

        super(EntityKind.COMMENT);

        // Make sure we have all the required keys.
        if(commentorUserGmail == null) {
            throw new NoUniqueKeyException("Comment - commentorUserGmail");
        }
        if(comicUserGmail == null) {
            throw new NoUniqueKeyException("Comment - comicUserGmail");
        }
        if(comicTitle == null) {
            throw new NoUniqueKeyException("Comment - comicTitle");
        }
        if(datePosted == null) {
            throw new NoUniqueKeyException("Comment - datePosted");
        }

        // Set the properties of the comment.
        this.commentorUserGmail = commentorUserGmail;
        this.comicUserGmail = comicUserGmail;
        this.comicTitle = comicTitle;
        this.datePosted = datePosted;

        // Attempt to retrieve the user from the datastore.
        try {

            fromEntity(retrieveEntity());

        } catch (EntityNotFoundException ex) {

            // Entity doesn't exist yet, init default values
            this.comment = "DEFAULT COMMENT.";
            saveEntity();

        }

    }

    public static Comment postNewComment(String commentorGmail, String userGmail, String comicTitle, String comment)
        throws NoUniqueKeyException {

        Comment c = new Comment(commentorGmail, userGmail, comicTitle, new Date());
        c.comment = comment;
        c.saveEntity();
        return c;

    }

    /*******************************************************************************************************************
     * Jsonable Methods
     ******************************************************************************************************************/

    @Override
    public JsonObject toJson() {

        // Create a new JsonObject.
        JsonObject obj = new JsonObject();

        // Add all the straightforward values.
        obj.addProperty(JsonProperty.COMMENTOR_GMAIL.toString(), commentorUserGmail);
        obj.addProperty(JsonProperty.USER_GMAIL.toString(), comicUserGmail);
        obj.addProperty(JsonProperty.TITLE.toString(), comicTitle);
        obj.addProperty(JsonProperty.DATE_POSTED.toString(), datePosted.getTime());
        obj.addProperty(JsonProperty.COMMENT.toString(), comment);

        // Return the JsonObject.
        return obj;

    }

    @Override
    public void fromJson(JsonObject obj) throws NoUniqueKeyException {

        if (obj.has(JsonProperty.COMMENTOR_GMAIL.toString())) {
            commentorUserGmail = obj.get(JsonProperty.COMMENTOR_GMAIL.toString()).getAsString();
        } else {
            throw new NoUniqueKeyException("Comment - commentorGmail");
        }

        if (obj.has(JsonProperty.USER_GMAIL.toString())) {
            commentorUserGmail = obj.get(JsonProperty.USER_GMAIL.toString()).getAsString();
        } else {
            throw new NoUniqueKeyException("Comment - userGmail");
        }

        if (obj.has(JsonProperty.TITLE.toString())) {
            comicTitle = obj.get(JsonProperty.TITLE.toString()).getAsString();
        } else {
            throw new NoUniqueKeyException("Comment - title");
        }

        if (obj.has(JsonProperty.DATE_POSTED.toString())) {
            datePosted = new Date(obj.get(JsonProperty.DATE_POSTED.toString()).getAsLong());
        } else {
            throw new NoUniqueKeyException("Comment - datePosted.");
        }

        if (obj.has(JsonProperty.COMMENT.toString())) {
            comment = obj.get(JsonProperty.COMMENT.toString()).getAsString();
        }

        saveEntity();

    }

    /*******************************************************************************************************************
     * DatastoreEntity Methods
     ******************************************************************************************************************/

    @Override
    protected String generateKeyString() {
        return commentorUserGmail + "_" + comicUserGmail + "_" + comicTitle + "_" + datePosted.getTime();
    }

    @Override
    public Entity toEntity() {

        Entity entity;

        // Attempt to load previous entity values from the datastore.
        try {
            entity = retrieveEntity();
        } catch (EntityNotFoundException e) {
            entity = new Entity(generateKey());
        }

        // Write each of the properties to the entity.
        entity.setProperty(JsonProperty.COMMENTOR_GMAIL.toString(), this.commentorUserGmail);
        entity.setProperty(JsonProperty.USER_GMAIL.toString(), this.comicUserGmail);
        entity.setProperty(JsonProperty.TITLE.toString(), this.comicTitle);
        entity.setProperty(JsonProperty.DATE_POSTED.toString(), this.datePosted.getTime());
        entity.setProperty(JsonProperty.COMMENT.toString(), this.comment);
        return entity;

    }

    @Override
    protected void fromEntity(Entity entity) {

        this.commentorUserGmail = (String) entity.getProperty(JsonProperty.COMMENTOR_GMAIL.toString());
        this.comicUserGmail = (String) entity.getProperty(JsonProperty.USER_GMAIL.toString());
        this.comicTitle = (String) entity.getProperty(JsonProperty.TITLE.toString());
        this.datePosted = (Date) entity.getProperty(JsonProperty.DATE_POSTED.toString());
        this.comment = (String) entity.getProperty(JsonProperty.COMMENT.toString());

    }

}
