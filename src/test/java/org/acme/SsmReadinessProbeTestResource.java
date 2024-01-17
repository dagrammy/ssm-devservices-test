package org.acme;

import static org.awaitility.Awaitility.await;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.common.DevServicesContext;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SsmReadinessProbeTestResource
    implements QuarkusTestResourceLifecycleManager, DevServicesContext.ContextAware {

  private static final ObjectMapper objectMapper = new ObjectMapper();
  private String localstackEndpointUrl;

  @Override
  public void setIntegrationTestContext(DevServicesContext context) {
    localstackEndpointUrl = context.devServicesProperties().get("quarkus.ssm.endpoint-override");
  }

  @Override
  public Map<String, String> start() {
    if (localstackEndpointUrl == null) {
      return null;
    }
    HttpClient httpClient = HttpClient.newHttpClient();
    await().atMost(20, TimeUnit.SECONDS).pollInterval(1, TimeUnit.SECONDS)
        .until(() -> isLocalStackReady(httpClient));

    return null;
  }

  private boolean isLocalStackReady(HttpClient httpClient)
      throws IOException, InterruptedException, URISyntaxException {
    HttpResponse<String> response = httpClient.send(HttpRequest.newBuilder()
        .uri(new URI(localstackEndpointUrl + "/_localstack/init"))
        .GET()
        .build(), HttpResponse.BodyHandlers.ofString());

    boolean ready = objectMapper.readTree(response.body()).at("/completed/READY").asBoolean();
    if (!ready) {
      System.out.println("Localstack not ready yet, waiting...");
    }
    return ready;
  }

  @Override
  public void stop() {
  }

  @Override
  public int order() {
    return 10;
  }
}
