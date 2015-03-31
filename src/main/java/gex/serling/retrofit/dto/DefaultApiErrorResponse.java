package gex.serling.retrofit.dto;

import java.io.Serializable;


import lombok.Getter;
import lombok.Setter;

public class DefaultApiErrorResponse implements Serializable{

 @Getter
 @Setter
 private String message;

}
