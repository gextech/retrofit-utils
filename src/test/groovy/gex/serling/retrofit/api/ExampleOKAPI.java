package gex.serling.retrofit.api;

import retrofit.http.GET;
import rx.Observable;

/**
 * Created by Tsunllly on 3/31/15.
 */
public interface ExampleOKAPI {

  @GET("/ip")
  Observable<String> getIp();

}
