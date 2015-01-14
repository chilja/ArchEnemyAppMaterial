/**
 * Copyright 2014-present Chilja Gossow.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.archenemy.archenemyapp.model;

import java.util.Date;

/**
 * <p>
 * Entity that holds the data of a Tweet from Twitter.
 * </p>
 * 
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
   * 
   * @param tweetId
   *          unique and stable ID of tweet
   * @param userName
   *          Name to be displayed
   * @param message
   * @param date
   * @param link
   * @param avatarUrl
   */
  public Tweet(Long tweetId, String userName, String message, Date date, String link,
      String avatarUrl) {
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
