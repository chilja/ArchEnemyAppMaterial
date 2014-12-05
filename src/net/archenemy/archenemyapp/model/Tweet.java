/**
 *
 */
package net.archenemy.archenemyapp.model;

import java.util.Date;

/**
 * @author chiljagossow
 *
 */
public class Tweet {
  private String message;
  private Date date;
  private String link;
  private String imageUrl ;
  private String avatarUrl;

  public Tweet(String name, String message, Date createdAt, String link, String avatarUrl) {
    date = createdAt;
    this.link = link;
    this.message = message;
    this.avatarUrl = avatarUrl;
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
   * @param imagerUrl the imageUrl to set
   */
  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }
}
