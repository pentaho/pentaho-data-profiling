/*!
 * PENTAHO CORPORATION PROPRIETARY AND CONFIDENTIAL
 *
 * Copyright 2002 - 2015 Pentaho Corporation (Pentaho). All rights reserved.
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

package com.pentaho.profiling.documentation;

import com.pentaho.profiling.api.doc.rest.DocEntry;
import com.pentaho.profiling.api.doc.rest.DocParameter;
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
