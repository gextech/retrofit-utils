package gex.serling.retrofit.api;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by Tsunllly on 3/31/15.
 */
public interface ExampleOkAPI {

  @GET("/ip")
  Observable<String> getIp();

}
