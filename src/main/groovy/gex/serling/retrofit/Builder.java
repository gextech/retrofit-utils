package gex.serling.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;
import gex.serling.retrofit.dto.DefaultApiErrorResponse;
import gex.serling.retrofit.dto.DefaultUserErrorResponse;
import gex.serling.retrofit.dto.MessageExtractable;
import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

import java.util.concurrent.TimeUnit;


/**
 * Created by Tsunllly on 3/31/15.
 */
public class Builder<Api, UserErrorDto extends MessageExtractable, ApiErrorDto extends MessageExtractable> {

  private Gson gson;
  private RestAdapter restAdapter;
  private OkHttpClient okHttpClient;

  private String baseUrl;


  private RestAdapter.LogLevel logLevel;
  private RestAdapter.Log log;
  public ErrorHandler errorHandler;
  private Class<UserErrorDto> userErrorDtoClazz;
  private Class<ApiErrorDto> apiErrorDtoClazz;


  private Builder() {
    okHttpClient = new OkHttpClient();
    okHttpClient.setReadTimeout(20, TimeUnit.SECONDS);
    okHttpClient.setConnectTimeout(5, TimeUnit.SECONDS);
    okHttpClient.setWriteTimeout(20, TimeUnit.SECONDS);
    this.withUserErrorDto(DefaultUserErrorResponse.class);
    this.withApiErrorDto(DefaultApiErrorResponse.class);
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

  public Builder withUserErrorDto(Class userErrorDtoClass) {
    this.userErrorDtoClazz = userErrorDtoClass;
    return this;
  }

  public Builder withApiErrorDto(Class apiErrorDtoClass) {
    this.apiErrorDtoClazz = apiErrorDtoClass;
    return this;
  }

  public String getDefaultBaseUrl(){
    String apiHost = System.getenv("DOCKER_HOST_TO_USE");
    apiHost = (apiHost != null) ? apiHost : "localhost";
    return "http://" + apiHost + ":9191";
  }

  public Gson getDefaultGson(){
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
        .setErrorHandler((errorHandler != null) ? errorHandler : new DefaultErrorHandler<UserErrorDto, ApiErrorDto>(userErrorDtoClazz, apiErrorDtoClazz))
        .setLogLevel((logLevel != null) ? logLevel : RestAdapter.LogLevel.FULL)
        .setLog((log != null) ? log : defaultLog)
      .build();
      withRestAdapter(defaultAdapter);
    }
  }


  public Api buildApi(Class<Api> api) {
    ensureRestAdapterExists();
    return restAdapter.create(api);
  }

}




