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

package org.pentaho.profiling.api.metrics;

import org.apache.log4j.spi.LoggingEvent;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by bryan on 4/2/15.
 */
public class LoggingEventUtil {
  public static List<LoggingEvent> getMessageRecordingList( final Map<String, LoggingEvent> loggingEventMap ) {
    List<LoggingEvent> result = mock( List.class );
    when( result.add( isA( LoggingEvent.class ) ) ).thenAnswer( new Answer<Boolean>() {
      @Override public Boolean answer( InvocationOnMock invocation ) throws Throwable {
        LoggingEvent loggingEvent = (LoggingEvent) invocation.getArguments()[ 0 ];
        loggingEventMap.put( String.valueOf( loggingEvent.getMessage() ), loggingEvent );
        return true;
      }
    } );
    return result;
  }
}
