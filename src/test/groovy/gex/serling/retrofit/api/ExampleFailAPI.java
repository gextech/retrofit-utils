package gex.serling.retrofit.api;

import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

/**
 * Created by Tsunllly on 3/31/15.
 */
public interface ExampleFailAPI {

  @GET("/users/octocat/orgs/{idOrg}")
  Observable<Object> getOrg(@Path("idOrg") String idOrg);

}
