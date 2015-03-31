package gex.serling.retrofit.exceptions;


import lombok.Getter;
import lombok.Setter;

public class UserErrorException extends RuntimeException{

  @Getter
  @Setter
  private Object errorResponse;

  @Getter
  @Setter
  private String debugInfo;

  @Getter
  @Setter
  private int statusCode;


  public UserErrorException(String message, Object errorResponse, Integer statusCode, String debugInfo) {
    super(message);
    if(statusCode != null) {
        this.statusCode = statusCode;
    }
    this.errorResponse = errorResponse;
    this.debugInfo = debugInfo;
  }
}
