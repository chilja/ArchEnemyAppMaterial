/**
 *
 */
package net.archenemy.archenemyapp.model;

import java.util.Date;

/**
 * <p>
 * Entity holding data of Facebook post
 * 
 * @author chiljagossow
 *         </p>
 * 
 */
public class Post {
  private Date date;
  private String imageUrl;
  private String message;
  private String link;
  private String userName;
  private String userId;
  private String postId;

  /**
   * Creates a new instance.
   * 
   * @param userName
   *          Name to be displayed
   * @param userId
   *          Facebook user ID
   * @param message
   *          Message to be displayed
   * @param date
   *          Date and time when post was published
   * @param imageUrl
   *          URL to image to be displayed
   * @param link
   *          Link associated with post
   * @param postId
   *          unique and stable ID of post
   */
  public Post(String userName, String userId, String message, Date date, String imageUrl,
      String link, String postId) {
    this.imageUrl = imageUrl;
    this.date = date;
    this.link = link;
    this.message = message;
    this.userName = userName;
    this.userId = userId;
    this.postId = postId;
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof Post) {
      if ((postId != null) && (((Post) object).postId != null)) {
        return postId.equals(((Post) object).postId);
      }
    }
    return false;
  }

  /**
   * @return the date of post
   */
  public Date getDate() {
    return date;
  }

  /**
   * @return the image url
   */
  public String getImageUrl() {
    return imageUrl;
  }

  /**
   * @return the link
   */
  public String getLink() {
    return link;
  }

  /**
   * @return the message
   */
  public String getMessage() {
    return message;
  }

  /**
   * @return the postId
   */
  public String getPostId() {
    return postId;
  }

  /**
   * @return the profileId
   */
  public String getUserId() {
    return userId;
  }

  /**
   * @return the name
   */
  public String getUserName() {
    return userName;
  }
}
