package gex.serling.retrofit.dto;

import java.io.Serializable;

public class DefaultApiErrorResponse implements Serializable, MessageExtractable {

 private String message;

 public String getMessage() {
  return message;
 }

 public void setMessage(String message) {
  this.message = message;
 }


}
