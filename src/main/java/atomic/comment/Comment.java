package atomic.comment;

import atomic.data.DatastoreEntity;
import atomic.data.EntityKind;
import atomic.json.JsonProperty;
import atomic.json.Jsonable;
import atomic.json.NoUniqueKeyException;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Query;
import com.google.gson.JsonObject;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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
    private String commentorGmail;

    /**
     * Private constructor used for building Comments in-house.
     */
    private Comment() {

        super(EntityKind.COMIC);

        this.commentorUserGmail = null;
        this.comicUserGmail = null;
        this.comicTitle = null;
        this.datePosted = null;
        this.comment = null;

    }

    /**
     * Constructor which initializes all data values for this comment.
     * @param commentorUserGmail Gmail of the user who posted the comment.
     * @param comicUserGmail Gmail of the user who created the comic.
     * @param comicTitle Title of the comic to be commented on.
     * @param datePosted Date the comment was posted.
     * @throws NoUniqueKeyException Thrown if any of the parameters are null.
     */
    private Comment(String commentorUserGmail, String comicUserGmail, String comicTitle, Date datePosted)
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

        // Set the required keys for the Comment.
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

    /**
     * Posts a new Comment on a Comic.
     * @param commentorGmail Gmail of the user who wishes to post the Comment.
     * @param userGmail Gmail of the user who created the Comic.
     * @param comicTitle Title of the Comic.
     * @param comment Comment to be posted.
     * @return The constructed Comment object which was posted.
     * @throws NoUniqueKeyException Thrown if any of the parameters are null.
     */
    public static Comment postNewComment(String commentorGmail, String userGmail, String comicTitle, String comment)
        throws NoUniqueKeyException {

        Comment c = new Comment(commentorGmail, userGmail, comicTitle, new Date());
        c.comment = comment;
        c.saveEntity();
        return c;

    }

    /**
     * Retrieves a list of Comments for a particular Comic (indicated by the userGmail and comicTitle pair).
     * @param userGmail Gmail of the user who created the comic.
     * @param comicTitle Title of the comic.
     * @return A list of Comments for the specified comic.
     */
    public static List<Comment> getCommentsForComic(String userGmail, String comicTitle) {

        // Filter based on gmail.
        Query.Filter userFilter = new Query.FilterPredicate(
                JsonProperty.USER_GMAIL.toString(),
                Query.FilterOperator.EQUAL,
                userGmail
        );

        // Filter based on title.
        Query.Filter titleFilter = new Query.FilterPredicate(
                JsonProperty.TITLE.toString(),
                Query.FilterOperator.EQUAL,
                comicTitle
        );

        // Filter combining gmail and title (sorted by date descending).
        Query.Filter commentFilter = Query.CompositeFilterOperator.and(userFilter, titleFilter);
        Query q = new Query(EntityKind.COMMENT.toString())
                .setFilter(commentFilter)
                .addSort(JsonProperty.DATE_POSTED.toString(),
                        Query.SortDirection.DESCENDING);

        // Execute the query and assemble the results.
        List<Entity> results = DatastoreEntity.executeQuery(q);

        // Return results.
        return Comment.listOfEntitiesToListOfComments(results);

    }

    /**
     * Generates a List of Comments posted by a particular user.
     * @param userGmail Gmail of the user to search for.
     * @return A List of the Comments posted by the particular user.
     */
    public static List<Comment> getCommentsFromUser(String userGmail) {

        // Filter based on gmail.
        Query.Filter userFilter = new Query.FilterPredicate(
                JsonProperty.USER_GMAIL.toString(),
                Query.FilterOperator.EQUAL,
                userGmail
        );

        // Filter combining gmail and title (sorted by date descending).
        Query q = new Query(EntityKind.COMIC.toString())
                .setFilter(userFilter)
                .addSort(JsonProperty.DATE_POSTED.toString(),
                        Query.SortDirection.DESCENDING);

        // Execute the query and assemble the results.
        List<Entity> results = DatastoreEntity.executeQuery(q);

        // Return results.
        return Comment.listOfEntitiesToListOfComments(results);
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
        entity.setProperty(JsonProperty.DATE_POSTED.toString(), this.datePosted);
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

    private static List<Comment> listOfEntitiesToListOfComments(List<Entity> entites) {

        List<Comment> comments = new LinkedList<>();
        for(Entity e : entites) {

            Comment c = new Comment();
            c.comicUserGmail = (String) e.getProperty(JsonProperty.USER_GMAIL.toString());;
            c.comicTitle = (String) e.getProperty(JsonProperty.TITLE.toString());;
            c.commentorUserGmail = (String) e.getProperty(JsonProperty.COMMENTOR_GMAIL.toString());
            c.datePosted = (Date) e.getProperty(JsonProperty.DATE_POSTED.toString());
            c.comment = (String) e.getProperty(JsonProperty.COMMENT.toString());

            comments.add(c);

        }

        return comments;

    }

    public String getCommentorGmail() {
        return commentorGmail;
    }

    public String getComment() {
        return comment;
    }

    public Date getDatePosted() {
        return datePosted;
    }
}
