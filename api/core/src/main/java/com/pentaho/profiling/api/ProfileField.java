package com.pentaho.profiling.api;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by bryan on 4/30/15.
 */
public interface ProfileField extends PublicCloneable {
  String getPhysicalName();

  String getLogicalName();

  Map<String, String> getProperties();

  List<ProfileFieldValueType> getTypes();

  ProfileFieldValueType getType( String name );

  Set<String> typeKeys();
}
