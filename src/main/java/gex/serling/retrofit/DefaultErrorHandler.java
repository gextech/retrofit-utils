package gex.serling.retrofit;


import gex.serling.retrofit.exceptions.ApiException;
import gex.serling.retrofit.exceptions.UserErrorException;
import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.Response;


/**
 * Created by Tsunllly on 3/31/15.
 */
public class DefaultErrorHandler<UserErrorDto, ApiErrorDto>  implements ErrorHandler {

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
        UserErrorDto errorResponse = (UserErrorDto) cause.getBodyAs(userErrorDtoClazz);
        String message = getMessageFromObject(errorResponse);
        if (errorResponse != null && message != null) {
          try {
            return new UserErrorException(message, errorResponse, r.getStatus(), null);
          } catch (Exception e) {
            System.out.println(e);
          }
        }
      }
    }

    ApiErrorDto errorResponse;
    String message = null;
    try {
      errorResponse = (ApiErrorDto) cause.getBodyAs(apiErrorDtoClazz);
      message = getMessageFromObject(errorResponse);
    }catch(RuntimeException e){
      errorResponse = null;
    }
    if (errorResponse != null  && message != null) {
      return new ApiException("Not valid response. " + message, r, cause);
    } else {
      return new ApiException("Not valid response. " + cause.getMessage(), r, cause);
    }
  }


  private String getMessageFromObject(Object object){
    String message = null;

    try {
      message = (String) userErrorDtoClazz.getDeclaredMethod("getMessage").invoke(object);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return message;
  }


  private static boolean isUserError(Integer statusCode) {
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



