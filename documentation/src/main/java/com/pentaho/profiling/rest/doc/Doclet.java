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

package com.pentaho.profiling.rest.doc;

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
import com.sun.tools.doclets.standard.Standard;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bryan on 4/6/15.
 */
public class Doclet extends Standard {
  public static final String JAX_WS_RX_PREFIX = "javax.ws.rs.";

  public static boolean start( RootDoc rootDoc ) {
    Map<String, DocEntry> commentMap = getCommentMap( rootDoc );
    try {
      new ObjectMapper().writeValue( new File( "restDoc.js" ), commentMap );
    } catch ( IOException e ) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  private static Map<String, DocEntry> getCommentMap( RootDoc rootDoc ) {
    Map<String, DocEntry> commentMap = new HashMap<String, DocEntry>();
    for ( Map.Entry<ClassDoc, List<MethodDoc>> classDocListEntry : getRelevantMethods( rootDoc ).entrySet() ) {
      for ( MethodDoc methodDoc : classDocListEntry.getValue() ) {
        String returnDescription = null;
        for ( Tag tag : methodDoc.tags() ) {
          String tagName = tag.name();
          if ( "@return".equals( tagName ) ) {
            returnDescription = tag.text();
          }
        }
        Map<String, String> paramNameToCommentMap = new HashMap<String, String>();
        for ( ParamTag paramTag : methodDoc.paramTags() ) {
          paramNameToCommentMap.put( paramTag.parameterName(), paramTag.parameterComment() );
        }
        List<DocParameter> parameters = new ArrayList<DocParameter>();
        for ( Parameter parameter : methodDoc.parameters() ) {
          String name = parameter.name();
          parameters.add( new DocParameter( name, paramNameToCommentMap.get( name ) ) );
        }
        DocEntry docEntry = new DocEntry( methodDoc.commentText(), returnDescription, parameters );
        commentMap.put( methodDoc.qualifiedName() + methodDoc.signature(), docEntry );
      }
    }
    return commentMap;
  }

  private static Map<ClassDoc, List<MethodDoc>> getRelevantMethods( RootDoc rootDoc ) {
    Map<ClassDoc, List<MethodDoc>> jaxMethods = new HashMap<ClassDoc, List<MethodDoc>>();
    for ( ClassDoc classDoc : rootDoc.classes() ) {
      for ( MethodDoc methodDoc : classDoc.methods() ) {
        for ( AnnotationDesc annotationDesc : methodDoc.annotations() ) {
          AnnotationTypeDoc annotationTypeDoc = annotationDesc.annotationType();
          if ( annotationTypeDoc.qualifiedTypeName().startsWith( JAX_WS_RX_PREFIX ) ) {
            List<MethodDoc> methodDocs = jaxMethods.get( classDoc );
            if ( methodDocs == null ) {
              methodDocs = new ArrayList<MethodDoc>();
              jaxMethods.put( classDoc, methodDocs );
            }
            methodDocs.add( methodDoc );
            break;
          }
        }
      }
    }
    return jaxMethods;
  }
}
