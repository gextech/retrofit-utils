package gex.serling.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;
import gex.serling.retrofit.dto.APIExceptionResponse;
import gex.serling.retrofit.dto.ErrorResponse;
import gex.serling.retrofit.exceptions.ApiException;
import gex.serling.retrofit.exceptions.UserErrorException;
import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


/**
 * Created by Tsunllly on 3/31/15.
 */
public class Builder<Api> {

  private Gson gson;
  private RestAdapter restAdapter;
  private OkHttpClient okHttpClient;

  private final static Logger LOGGER = Logger.getLogger(Builder.class.getName());
  private String baseUrl;


  private GsonBuilder gsonBuilder;
  private RestAdapter.LogLevel logLevel;
  private RestAdapter.Log log;
  private ErrorHandler errorHandler;



  private Builder() {
    okHttpClient = new OkHttpClient();
    okHttpClient.setReadTimeout(20, TimeUnit.SECONDS);
    okHttpClient.setConnectTimeout(5, TimeUnit.SECONDS);
    okHttpClient.setWriteTimeout(20, TimeUnit.SECONDS);
  }

  public static Builder create() {
    Builder builder = new Builder();
    builder.setDefaultGson();
    builder.setDefaultApiConfig();
    return builder;
  }

  public Builder withBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
    return this;
  }

  public String getDefaultBaseUrl(){
    String apiHost = System.getenv("DOCKER_HOST_TO_USE");
    apiHost = (apiHost != null) ? apiHost : "localhost";
    return "http://" + apiHost + ":9191";
  }

  public Gson getDefaultGson(){
    // TODO: Again we are defining a different GSON :(
    return new GsonBuilder().create();
  }

  public Builder setDefaultApiConfig() {
    return withBaseUrl(getDefaultBaseUrl());
  }

  public Builder setDefaultGson() {
    setGson(getDefaultGson());
    return this;
  }

  public Builder setGson(Gson gson) {
    this.gson = gson;
    return this;
  }

  public Builder withRestAdapter(RestAdapter restAdapter) {
    this.restAdapter = restAdapter;
    return this;
  }

  public Builder withLogLevel(RestAdapter.LogLevel logLevel) {
    this.logLevel = logLevel;
    return this;
  }

  public Builder withLog(RestAdapter.Log log) {
    this.log = log;
    return this;
  }

  public Builder withErrorHandler(ErrorHandler errorHandler) {
    this.errorHandler = errorHandler;
    return this;
  }


  class CrawlErrorHandler implements ErrorHandler {
    @Override
    public Throwable handleError(RetrofitError cause) {
      Response r = cause.getResponse();
      if (r != null && isUserError(r.getStatus())) {
        if (isJsonResponse(r)) {
          ErrorResponse errorResponse = (ErrorResponse) cause.getBodyAs(ErrorResponse.class);
          if (errorResponse != null && errorResponse.getMessage() != null) {
            try {
              return new UserErrorException(errorResponse.getMessage(), errorResponse, r.getStatus(), null);
            }catch(Exception e){
              System.out.println(e);
            }
          }
        }
      }

      APIExceptionResponse errorResponse = (APIExceptionResponse) cause.getBodyAs(APIExceptionResponse.class);
      if (errorResponse != null && errorResponse.getMessage() != null) {
        return new ApiException("Respuesta invalida. " + errorResponse.getMessage(), r, cause);
      } else {
        return new ApiException("Respuesta invalida. " + cause.getMessage(), r, cause);
      }
    }
  }


  private void ensureRestAdapterExists() {

    RestAdapter.Log defaultLog = new RestAdapter.Log() {
      @Override
      public void log(String msg) {
        System.out.println(msg);
      }
    };

    if (restAdapter == null) {
      RestAdapter defaultAdapter = new RestAdapter.Builder()
        .setEndpoint((baseUrl != null) ? baseUrl : getDefaultBaseUrl())
        .setConverter(new GsonConverter((gson != null) ? gson : getDefaultGson()))
        .setClient(new OkClient(okHttpClient))
        .setErrorHandler((errorHandler != null) ? errorHandler : new CrawlErrorHandler())
        .setLogLevel((logLevel != null) ? logLevel : RestAdapter.LogLevel.FULL)
        .setLog((log != null) ? log : defaultLog)
        .build();
      withRestAdapter(defaultAdapter);
    }
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

  private boolean isUserError(Integer statusCode) {
    return statusCode >= 400 && statusCode < 500;
  }

  public Api buildApi(Class<Api> api) {
    ensureRestAdapterExists();
    return restAdapter.create(api);
  }

}
