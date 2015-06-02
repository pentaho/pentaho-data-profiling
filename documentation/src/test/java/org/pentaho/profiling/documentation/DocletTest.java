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

package org.pentaho.profiling.documentation;

import org.pentaho.profiling.api.doc.rest.DocEntry;
import org.pentaho.profiling.api.doc.rest.DocParameter;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Tag;
import org.junit.Test;

import javax.ws.rs.Path;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by bryan on 4/10/15.
 */
public class DocletTest {
  @Test
  public void testDoclet() {
    RootDoc rootDoc = mock( RootDoc.class );
    ClassDoc classDoc = mock( ClassDoc.class );
    MethodDoc methodDoc = mock( MethodDoc.class );
    Tag returnTag = mock( Tag.class );
    ParamTag paramTag = mock( ParamTag.class );
    Parameter parameter = mock( Parameter.class );
    Parameter parameter2 = mock( Parameter.class );
    AnnotationDesc annotationDesc = mock( AnnotationDesc.class );
    AnnotationTypeDoc annotationTypeDoc = mock( AnnotationTypeDoc.class );
    when( rootDoc.classes() ).thenReturn( new ClassDoc[] { classDoc } );
    when( classDoc.methods() ).thenReturn( new MethodDoc[] { methodDoc } );
    String methodName = "methodName";
    String signature = "methodSignature";
    String methodComment = "methodComment";
    when( methodDoc.qualifiedName() ).thenReturn( methodName );
    when( methodDoc.commentText() ).thenReturn( methodComment );
    when( methodDoc.signature() ).thenReturn( signature );
    when( methodDoc.tags() ).thenReturn( new Tag[] { returnTag, paramTag } );
    when( returnTag.name() ).thenReturn( "@return" );
    String returnText = "returnText";
    when( returnTag.text() ).thenReturn( returnText );
    String paramName = "paramName";
    String paramComment = "paramComment";
    String paramName2 = "paramName2";
    when( paramTag.parameterName() ).thenReturn( paramName );
    when( paramTag.parameterComment() ).thenReturn( paramComment );
    when( parameter.name() ).thenReturn( paramName );
    when( parameter2.name() ).thenReturn( paramName2 );
    when( methodDoc.paramTags() ).thenReturn( new ParamTag[] { paramTag } );
    when( methodDoc.parameters() ).thenReturn( new Parameter[] { parameter, parameter2 } );
    when( methodDoc.annotations() ).thenReturn( new AnnotationDesc[] { annotationDesc } );
    when( annotationDesc.annotationType() ).thenReturn( annotationTypeDoc );
    when( annotationTypeDoc.qualifiedTypeName() ).thenReturn( Path.class.getCanonicalName() );
    Map<String, DocEntry> commentMap = Doclet.getCommentMap( rootDoc );
    assertEquals( 1, commentMap.size() );
    DocEntry docEntry = commentMap.get( methodName + signature );
    assertNotNull( docEntry );
    assertEquals( methodComment, docEntry.getMessage() );
    assertEquals( returnText, docEntry.getReturnDescription() );
    List<DocParameter> parameters = docEntry.getParameters();
    assertEquals( 2, parameters.size() );
    DocParameter docParameter = parameters.get( 0 );
    assertEquals( paramName, docParameter.getName() );
    assertEquals( paramComment, docParameter.getComment() );
    DocParameter docParameter2 = parameters.get( 1 );
    assertEquals( paramName2, docParameter2.getName() );
    assertNull( docParameter2.getComment() );
  }
}
