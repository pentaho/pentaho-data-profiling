/*******************************************************************************
 *
 * Pentaho Data Profiling
 *
 * Copyright (C) 2002-2015 by Pentaho : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package org.pentaho.profiling.api;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by bryan on 9/16/14.
 */
@XmlRootElement
public class ProfileStatusMessage {
  String messagePath;
  String messageKey;
  List<String> messageVariables;

  public ProfileStatusMessage( String messagePath, String messageKey, List<String> messageVariables ) {
    this.messagePath = messagePath;
    this.messageKey = messageKey;
    if ( messageVariables == null ) {
      messageVariables = new ArrayList<String>();
    }
    this.messageVariables = Collections.unmodifiableList( new ArrayList<String>( messageVariables ) );
  }

  public ProfileStatusMessage() {
    this( null, null );
  }

  public ProfileStatusMessage( String messagePath, String messageKey ) {
    this( messagePath, messageKey, null );
  }

  @XmlElement
  public String getMessagePath() {
    return messagePath;
  }

  @XmlElement
  public List<String> getMessageVariables() {
    return messageVariables;
  }

  @XmlElement
  public String getMessageKey() {
    return messageKey;
  }
}
