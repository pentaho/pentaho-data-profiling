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

package org.pentaho.profiling.documentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.standard.Standard;
import org.pentaho.profiling.api.doc.rest.DocEntry;
import org.pentaho.profiling.api.doc.rest.DocParameter;

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

  public static Map<String, DocEntry> getCommentMap( RootDoc rootDoc ) {
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
