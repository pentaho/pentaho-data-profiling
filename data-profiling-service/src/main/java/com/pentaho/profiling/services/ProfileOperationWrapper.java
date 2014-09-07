package com.pentaho.profiling.services;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by bryan on 9/8/14.
 */
@XmlRootElement
public class ProfileOperationWrapper {
  private String profileId;
  private String operationId;

  public ProfileOperationWrapper() {
  }

  public ProfileOperationWrapper( String profileId, String operationId ) {
    this.profileId = profileId;
    this.operationId = operationId;
  }

  public String getProfileId() {
    return profileId;
  }

  public void setProfileId( String profileId ) {
    this.profileId = profileId;
  }

  public String getOperationId() {
    return operationId;
  }

  public void setOperationId( String operationId ) {
    this.operationId = operationId;
  }
}
