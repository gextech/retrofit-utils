package gex.serling.retrofit.exceptions;


import gex.serling.retrofit.dto.MessageExtractable;

public class UserErrorException extends RuntimeException {
  private MessageExtractable errorResponse;

  private String debugInfo;

  private int statusCode;


  public MessageExtractable getErrorResponse() {
    return errorResponse;
  }

  public UserErrorException(String message, MessageExtractable errorResponse, Integer statusCode, String debugInfo) {
    super(message);
    if(statusCode != null) {
        this.statusCode = statusCode;
    }
    this.errorResponse = errorResponse;
    this.debugInfo = debugInfo;
  }
}
