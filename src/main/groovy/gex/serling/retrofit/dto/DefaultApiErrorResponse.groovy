package gex.serling.retrofit.dto

public class DefaultApiErrorResponse implements Serializable, MessageExtractable {

  /* Describes type of error */
  String error

  /* Type of exception */
  String exception

  /* Detail of exception */
  String message

  /* The path throwing the exception */
  String path

  /* http error code */
  Long status

  /* Timestamp of error */
  String timestamp

}

