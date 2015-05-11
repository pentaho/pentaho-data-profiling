package com.pentaho.profiling.api;

/**
 * Created by bryan on 5/1/15.
 */
public class IllegalTransactionException extends Exception {
  public IllegalTransactionException( String message ) {
    super( message );
  }
}
