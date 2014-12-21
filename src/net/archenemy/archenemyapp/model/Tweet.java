/**
 *
 */
package net.archenemy.archenemyapp.model;

import java.util.Date;

/**
 * <p>
 * Entity that holds the data of a Tweet from Twitter.
 * </p>
 * @author chiljagossow
 * 
 */
public class Tweet {
  private String message;
  private String link;
  private String imageUrl;
  private String avatarUrl;
  private String userName;
  private Date date;
  private Long tweetId;

  /**
   * Creates a new instance.
   * @param tweetId unique and stable ID of tweet
   * @param userName Name to be displayed
   * @param message
   * @param date
   * @param link
   * @param avatarUrl
   */
  public Tweet(Long tweetId, String userName, String message, Date date, String link, String avatarUrl) {
    this.tweetId = tweetId;
    this.userName = userName;
    this.message = message;
    this.date = date;
    this.link = link;
    this.avatarUrl = avatarUrl;
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof Tweet) {
      if ((tweetId != null) && (((Tweet) object).tweetId != null)) {
        return tweetId.equals(((Tweet) object).tweetId);
      }
    }
    return false;
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
  public String getUserName() {
    return userName;
  }

  /**
   * @param imagerUrl
   *          the imageUrl to set
   */
  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }
}
