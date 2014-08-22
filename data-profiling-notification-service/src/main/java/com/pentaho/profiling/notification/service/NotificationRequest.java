/*!
 * PENTAHO CORPORATION PROPRIETARY AND CONFIDENTIAL
 *
 * Copyright 2002 - 2014 Pentaho Corporation (Pentaho). All rights reserved.
 *
 * NOTICE: All information including source code contained herein is, and
 * remains the sole property of Pentaho and its licensors. The intellectual
 * and technical concepts contained herein are proprietary and confidential
 * to, and are trade secrets of Pentaho and may be covered by U.S. and foreign
 * patents, or patents in process, and are protected by trade secret and
 * copyright laws. The receipt or possession of this source code and/or related
 * information does not convey or imply any rights to reproduce, disclose or
 * distribute its contents, or to manufacture, use, or sell anything that it
 * may describe, in whole or in part. Any reproduction, modification, distribution,
 * or public display of this information without the express written authorization
 * from Pentaho is strictly prohibited and in violation of applicable laws and
 * international treaties. Access to the source code contained herein is strictly
 * prohibited to anyone except those individuals and entities who have executed
 * confidentiality and non-disclosure agreements or other agreements with Pentaho,
 * explicitly covering such access.
 */

package com.pentaho.profiling.notification.service;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by bryan on 8/21/14.
 */
@XmlRootElement
public class NotificationRequest {
  private String notificationType;
  private List<NotificationRequestEntry> entries;

  public NotificationRequest() {
    this( null, null );
  }

  public NotificationRequest( String notificationType,
                              List<NotificationRequestEntry> entries ) {

    this.notificationType = notificationType;
    this.entries = entries;
  }

  public String getNotificationType() {
    return notificationType;
  }

  public void setNotificationType( String notificationType ) {
    this.notificationType = notificationType;
  }

  public List<NotificationRequestEntry> getEntries() {
    return entries;
  }

  public void setEntries( List<NotificationRequestEntry> entries ) {
    this.entries = entries;
  }
}
