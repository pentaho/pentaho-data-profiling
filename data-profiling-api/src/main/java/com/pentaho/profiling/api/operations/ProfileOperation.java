package com.pentaho.profiling.api.operations;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by bryan on 9/8/14.
 */
@XmlRootElement
public class ProfileOperation {
  private String id;
  private String namePath;
  private String nameKey;

  public ProfileOperation() {
    this( null, null, null );
  }

  public ProfileOperation( String id, String namePath, String nameKey ) {
    this.id = id;
    this.namePath = namePath;
    this.nameKey = nameKey;
  }

  public String getNamePath() {
    return namePath;
  }

  public void setNamePath( String namePath ) {
    this.namePath = namePath;
  }

  public String getNameKey() {
    return nameKey;
  }

  public void setNameKey( String nameKey ) {
    this.nameKey = nameKey;
  }

  public String getId() {
    return id;
  }

  public void setId( String id ) {
    this.id = id;
  }
}
