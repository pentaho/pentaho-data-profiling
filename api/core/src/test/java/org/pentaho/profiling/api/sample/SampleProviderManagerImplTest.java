/*******************************************************************************
 *
 * Pentaho Data Profiling
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
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

package org.pentaho.profiling.api.sample;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by bryan on 4/10/15.
 */
public class SampleProviderManagerImplTest {
  @Test
  public void testProvide() {
    List<SampleProvider> providers = new ArrayList<SampleProvider>();
    SampleProvider nullProvider = mock( SampleProvider.class );
    SampleProvider sampleProvider = mock( SampleProvider.class );
    providers.add( nullProvider );
    providers.add( sampleProvider );
    String test = "test";
    when( sampleProvider.provide( String.class ) ).thenReturn( Arrays.asList( test ) );
    when( nullProvider.provide( String.class ) ).thenReturn( null );
    List<String> samples = new SampleProviderManagerImpl( providers ).provide( String.class );
    assertEquals( 1, samples.size() );
    assertEquals( test, samples.get( 0 ) );
  }
}
