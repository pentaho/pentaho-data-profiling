package com.pentaho.profiling.api;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by bryan on 9/7/14.
 */
@XmlRootElement
public class ProfileFieldDefinition {
  private List<ProfileFieldProperty> profileFieldProperties;

  public ProfileFieldDefinition() {
    this( null );
  }

  public ProfileFieldDefinition( List<ProfileFieldProperty> profileFieldProperties ) {
    this.profileFieldProperties = profileFieldProperties;
  }

  public List<ProfileFieldProperty> getProfileFieldProperties() {
    return profileFieldProperties;
  }

  public void setProfileFieldProperties( List<ProfileFieldProperty> profileFieldProperties ) {
    this.profileFieldProperties = profileFieldProperties;
  }
}
