package gex.serling.retrofit.exceptions;

import lombok.Getter;
import lombok.Setter;
import retrofit.client.Response;

public class ApiException extends RuntimeException {

  @Getter
  @Setter
  private String debugInfo;

  @Getter
  @Setter
  private Response response;

  @Getter
  @Setter
  private int statusCode;

  public ApiException() {
    super();
  }

  public ApiException(String message) {
    super(message);
  }

  public ApiException(String message, Response response, Throwable cause) {
    super(message, cause);
    this.response = response;
    if(response != null) {
      this.statusCode = response.getStatus();
    }
  }

}
