package com.pentaho.profiling.model;

import com.pentaho.profiling.notification.api.NotificationProvider;

/**
 * Created by bryan on 9/8/14.
 */
public interface ProfileNotificationProvider extends NotificationProvider {
  public void notify( String id );
}
