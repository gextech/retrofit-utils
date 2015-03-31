package gex.serling.retrofit;


import gex.serling.retrofit.dto.MessageExtractable;
import gex.serling.retrofit.exceptions.ApiException;
import gex.serling.retrofit.exceptions.UserErrorException;
import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.Response;


/**
 * Created by Tsunllly on 3/31/15.
 */
public class DefaultErrorHandler<UserErrorDto extends MessageExtractable, ApiErrorDto extends MessageExtractable>  implements ErrorHandler {

  private Class<UserErrorDto> userErrorDtoClazz;
  private Class<ApiErrorDto> apiErrorDtoClazz;

  DefaultErrorHandler(Class<UserErrorDto> userErrorDtoClazz, Class<ApiErrorDto> apiErrorDtoClazz ){
    this.userErrorDtoClazz = userErrorDtoClazz;
    this.apiErrorDtoClazz = apiErrorDtoClazz;
  }

  @Override
  public Throwable handleError(RetrofitError cause) {
    Response r = cause.getResponse();
    if (r != null && isUserError(r.getStatus())) {
      if (isJsonResponse(r)) {
        MessageExtractable errorResponse = (UserErrorDto) cause.getBodyAs(userErrorDtoClazz);
        if (errorResponse != null && errorResponse.getMessage() != null) {
          try {
            return new UserErrorException(errorResponse.getMessage(), errorResponse, r.getStatus(), null);
          } catch (Exception e) {
            System.out.println(e);
          }
        }
      }
    }

    MessageExtractable errorResponse;
    try {
      errorResponse = (ApiErrorDto) cause.getBodyAs(apiErrorDtoClazz);
    }catch(RuntimeException e){
      errorResponse = null;
    }
    if (errorResponse != null && errorResponse.getMessage() != null) {
      return new ApiException("Not valid response. " + errorResponse.getMessage(), r, cause);
    } else {
      return new ApiException("Not valid response. " + cause.getMessage(), r, cause);
    }
  }

  public static boolean isUserError(Integer statusCode) {
    return statusCode >= 400 && statusCode < 500;
  }

  private boolean isJsonResponse(Response response) {
    boolean json = false;
    for (Header h : response.getHeaders()) {
      if (h.getName() != null && h.getName().toLowerCase().equals("content-type")) {
        return h.getValue().contains("json");
      }
    }
    return json;
  }


}



