package gex.serling.retrofit.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

public class DefaultUserErrorResponse implements Serializable{

 @Getter
 @Setter
 private String code;

 @Getter
 @Setter
 private String message;

 @Getter
 @Setter
 private Map extraData;

}
