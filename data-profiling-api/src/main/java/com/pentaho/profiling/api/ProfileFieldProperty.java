package com.pentaho.profiling.api;

import java.util.List;

/**
 * Created by bryan on 9/7/14.
 */
public class ProfileFieldProperty<T> {
  private String namePath;
  private String nameKey;
  private List<String> pathToProperty;
  private Class<T> type;

  public ProfileFieldProperty() {
    this( null, null, null, null );
  }

  public ProfileFieldProperty( String namePath, String nameKey, List<String> pathToProperty, Class<T> type ) {
    this.namePath = namePath;
    this.nameKey = nameKey;
    this.pathToProperty = pathToProperty;
    this.type = type;
  }

  public String getNamePath() {
    return namePath;
  }

  public String getNameKey() {
    return nameKey;
  }

  public void setNameKey( String nameKey ) {
    this.nameKey = nameKey;
  }

  public List<String> getPathToProperty() {
    return pathToProperty;
  }

  public void setPathToProperty( List<String> pathToProperty ) {
    this.pathToProperty = pathToProperty;
  }

  /*public Set<T> getValue( Map<String, Object> values ) {
    Map<String, Object> currentMap = values;
    for ( int i = 0; i < pathToProperty.size() - 1; i++ ) {
      String part = pathToProperty.get( i );
      Object value = values.get( part );
      if ( value == null ) {
        return null;
      } else if ( value instanceof Map ) {
        currentMap = (Map<String, Object>) value;
      } else {
        throw new IllegalStateException( "Expected map at this level, possible conflicting field properties" );
      }
    }
    Object result = currentMap.get( pathToProperty.get( pathToProperty.size() - 1 ) );
    if ( result == null || type.isAssignableFrom( result.getClass() ) ) {
      return (T) result;
    } else {
      throw new IllegalStateException( "Expected type " + type + " but found " + result.getClass() );
    }
  }

  private void getValueHelper( Map<String, Object> currentMap, Set<T> result, int startIndex ) {
    for ( int i = startIndex; i < pathToProperty.size() - 1; i++ ) {
      String part = pathToProperty.get( i );
      Object value = currentMap.get( part );
      if ( value == null ) {
        return;
      } else if ( value instanceof Map ) {
        currentMap = (Map<String, Object>) value;
      } else if ( value instanceof Collection ) {
        Collection<Map<String, Object>>
      } else {
        throw new IllegalStateException( "Expected map at this level, possible conflicting field properties" );
      }
    }
    Object result = currentMap.get( pathToProperty.get( pathToProperty.size() - 1 ) );
    if ( result == null || type.isAssignableFrom( result.getClass() ) ) {
      return (T) result;
    } else {
      throw new IllegalStateException( "Expected type " + type + " but found " + result.getClass() );
    }
  }*/

  @Override
  public boolean equals( Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    ProfileFieldProperty that = (ProfileFieldProperty) o;

    if ( !nameKey.equals( that.nameKey ) ) {
      return false;
    }
    if ( !namePath.equals( that.namePath ) ) {
      return false;
    }
    if ( !pathToProperty.equals( that.pathToProperty ) ) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = namePath.hashCode();
    result = 31 * result + nameKey.hashCode();
    result = 31 * result + pathToProperty.hashCode();
    return result;
  }
}
