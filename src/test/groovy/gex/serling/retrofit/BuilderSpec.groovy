package gex.serling.retrofit

import gex.serling.retrofit.api.CustomizableErrorResponse
import gex.serling.retrofit.api.ExampleFailAPI
import gex.serling.retrofit.api.ExampleOKAPI
import gex.serling.retrofit.dto.DefaultUserErrorResponse
import gex.serling.retrofit.exceptions.ApiException
import gex.serling.retrofit.exceptions.UserErrorException
import retrofit.ErrorHandler
import retrofit.RestAdapter
import retrofit.RetrofitError
import retrofit.client.Response
import spock.lang.Shared
import spock.lang.Specification

import java.util.concurrent.TimeUnit

class BuilderSpec extends Specification {

  @Shared
  String ipifyBaseUrl

  @Shared
  String octocatBaseUrl

  @Shared
  String customCodeBaseUrl

  @Shared
  String ipRegex

  @Shared
  RestAdapter.Log prettyLog

  void setup() {
    ipifyBaseUrl = "http://api.ipify.org"
    octocatBaseUrl = "https://api.github.com"
    customCodeBaseUrl = "http://httpstat.us"

    ipRegex = "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)"
    prettyLog = new RestAdapter.Log() {
      private static List<String> logAccumulator = []
      private static int count = 0

      @Override
      public void log(String msg) {
        def message = "Msg ${count++}: " + msg
        logAccumulator << message
        System.out.println(message)
      }

      def restart(){
        logAccumulator = []
        count = 0
      }
    }
  }


  def 'Default url is localhost'() {
    when:
      Builder api = new Builder<ExampleFailAPI>().create()

    then:
      api.getDefaultBaseUrl() == "http://localhost:9191"
  }

  def 'It builds a valid api able to return valid response'(){
    given:
      ExampleOKAPI ipApi =  new Builder()
        .withBaseUrl(ipifyBaseUrl)
        .buildApi(ExampleOKAPI)

    when:
      def response = ipApi.getIp().timeout(5, TimeUnit.SECONDS).toBlocking().first()

    then: 'it responses not null like ip string'
      response
      response.matches(ipRegex)
  }


  def 'Test Builder with custom log'() {
    given:
      def RestAdapter.LogLevel logLevelBasic =  RestAdapter.LogLevel.BASIC;
      def RestAdapter.LogLevel logLevelNone =  RestAdapter.LogLevel.NONE;

      def numberOfMessages = 0

    when: 'Pretty log'
      def apiPrettyLog =  new Builder()
        .withBaseUrl(ipifyBaseUrl)
        .withLog(prettyLog)
        .buildApi(ExampleOKAPI)

      apiPrettyLog.getIp().timeout(5, TimeUnit.SECONDS).toBlocking().first()

    then:
      !prettyLog.logAccumulator.empty
      prettyLog.logAccumulator.eachWithIndex(){ msg, index ->
        assert msg.startsWith("Msg ${index}:")
      }

    when: 'pretty log / log level basic'
      prettyLog.restart()
      numberOfMessages = prettyLog.logAccumulator.size()

      def apiPrettyLogOnlyBasics =  new Builder()
        .withBaseUrl(ipifyBaseUrl)
        .withLog(prettyLog)
        .withLogLevel(logLevelBasic)
        .buildApi(ExampleOKAPI)

      apiPrettyLogOnlyBasics.getIp().timeout(5, TimeUnit.SECONDS).toBlocking().first()

    then:
      !prettyLog.logAccumulator.empty
      prettyLog.logAccumulator.eachWithIndex(){ msg, index ->
        assert msg.startsWith("Msg ${index}:")
      }
      numberOfMessages < prettyLog.logAccumulator.size()

    when: 'log level none'
      prettyLog.restart()

      def apiLogLevelNone =  new Builder()
        .withBaseUrl(ipifyBaseUrl)
        .withLog(prettyLog)
        .withLogLevel(logLevelNone)
        .buildApi(ExampleOKAPI)

      apiLogLevelNone.getIp().timeout(5, TimeUnit.SECONDS).toBlocking().first()

    then:
      prettyLog.logAccumulator.empty
  }


  def 'Default userError response works correctly :)'() {
    given:
      ExampleFailAPI api =  new Builder<ExampleFailAPI>()
        .withBaseUrl(octocatBaseUrl)
        .buildApi(ExampleFailAPI)

    when:
      api.getOrg(UUID.randomUUID().toString()).timeout(10, TimeUnit.SECONDS).toBlocking().first()

    then:  // Now, all the exceptions are NullPointerExceptions O.o !!!
      UserErrorException e = thrown()
      e.message == "Not Found" ||  e.message.contains( "API rate limit exceeded" )
      DefaultUserErrorResponse errorResponse = e.errorResponse
      errorResponse.message == "Not Found" ||  errorResponse.message.contains( "API rate limit exceeded" )
      errorResponse.extraData == null
  }

  def 'DefaultUserError can be happily changed'() {
    given:
      ExampleFailAPI api = new Builder<ExampleFailAPI>()
        .withBaseUrl(octocatBaseUrl)
        .withUserErrorDto(CustomizableErrorResponse.class)
        .buildApi(ExampleFailAPI)

    when:
      api.getOrg(UUID.randomUUID().toString()).timeout(5, TimeUnit.SECONDS).toBlocking().first()

    then:
      UserErrorException e = thrown()
      e.message == "Not Found" ||  e.message.contains( "API rate limit exceeded" )
      CustomizableErrorResponse errorResponse = e.errorResponse
      errorResponse.message == "Not Found" ||  errorResponse.message.contains( "API rate limit exceeded" )
      errorResponse.documentation_url.contains("https://developer.github.com/v3")
  }


  def 'DefaultApiError response works correctly :)'() {
    given:
      ExampleFailAPI api = new Builder<ExampleFailAPI>()
        .withBaseUrl(customCodeBaseUrl)
        .buildApi(ExampleFailAPI)

    when:
      api.get500().timeout(5, TimeUnit.SECONDS).toBlocking().first()

    then:
      ApiException e = thrown()
      e.message == "Not valid response. 500 Internal Server Error"
  }


  def 'Test Builder with custom error handling'() {
    given:
      ExampleFailAPI api =  new Builder()
        .withBaseUrl(octocatBaseUrl)
        .withErrorHandler(new TestErrorHandler())
        .buildApi(ExampleFailAPI)

    when:
      api.getOrg(UUID.randomUUID().toString()).timeout(5, TimeUnit.SECONDS).toBlocking().first()

    then:  // Now, all the exceptions are NullPointerExceptions O.o !!!
      NullPointerException e = thrown()
      e.message == "Status: 404" || e.message == "Status: 403"
  }


  class TestErrorHandler implements ErrorHandler {
    @Override
    public Throwable handleError(RetrofitError cause) {
      Response r = cause.getResponse();
      return new NullPointerException("Status: ${r.status}")
    }
  }



}
