package com.pentaho.profiling.services;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by bryan on 9/8/14.
 */
@XmlRootElement
public class ProfileIdWrapper {
  private String profileId;

  public ProfileIdWrapper() {
  }

  public String getProfileId() {
    return profileId;
  }

  public void setProfileId( String profileId ) {
    this.profileId = profileId;
  }
}
