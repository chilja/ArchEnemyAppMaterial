/**
 *
 */
package net.archenemy.archenemyapp.model;

import java.util.Date;

/**
 * @author chiljagossow
 *
 */
public class Post {
  private String imageUrl ;
  private String message;
  private Date date;
  private String link;
  private String name;
  private String profileId;
  private String avatarUrl;

  public Post(String name, String id, String message, String createdAt, String imageUrl, String link, String avatarUrl) {
    this.imageUrl = imageUrl;
    date = FacebookAdapter.getDate(createdAt);
    this.link = link;
    this.message = message;
    this.name = name;
    profileId = id;
  }

  /**
   * @return the avatarUrl
   */
  public String getAvatarUrl() {
    return avatarUrl;
  }

  /**
   * @return the date
   */
  public Date getDate() {
    return date;
  }

  /**
   * @return the imageUrl
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
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @return the profileId
   */
  public String getProfileId() {
    return profileId;
  }
}
