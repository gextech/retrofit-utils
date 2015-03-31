package gex.serling.retrofit.dto;

import java.io.Serializable;
import java.util.Map;

public class DefaultUserErrorResponse implements Serializable, MessageExtractable{

 private String code;
 private String message;
 private Map extraData;

 public String getCode() {
  return code;
 }

 public void setCode(String code) {
  this.code = code;
 }

 public String getMessage() {
  return message;
 }

 public void setMessage(String message) {
  this.message = message;
 }

 public Map getExtraData() {
  return extraData;
 }

 public void setExtraData(Map extraData) {
  this.extraData = extraData;
 }

}
