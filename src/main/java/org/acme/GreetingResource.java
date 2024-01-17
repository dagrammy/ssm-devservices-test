package org.acme;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;

@Path("/hello")
public class GreetingResource {

  @Inject
  SsmClient ssmClient;

  @GET
  @Produces(MediaType.TEXT_PLAIN)


  public String hello() {
    return ssmClient.getParameter(generateGetParameterRequest("hello")).parameter().value();
  }

  private GetParameterRequest generateGetParameterRequest(String parameterName) {
    return GetParameterRequest.builder()
        .name(parameterName)
        .withDecryption(true)
        .build();
  }
}
