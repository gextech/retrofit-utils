package gex.serling.retrofit.api

import gex.serling.retrofit.dto.MessageExtractable

public class CustomizableErrorResponse implements Serializable, MessageExtractable {

  String message

  String documentation_url
}

