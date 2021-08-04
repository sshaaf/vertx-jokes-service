package dev.shaaf.vertx.joke;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpMethod;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
public class TestMainVerticle {

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new JokeVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  void getARandomJoke(Vertx vertx, VertxTestContext testContext) throws Throwable{
    HttpClient client = vertx.createHttpClient();

    client.request(HttpMethod.GET, 8080, "localhost", "/")
      .compose(req -> req.send().compose(HttpClientResponse::body))
      .onComplete(testContext.succeeding(buffer -> testContext.verify(() -> {
        testContext.completeNow();
      })));
  }


  @Test
  void verticle_deployed(Vertx vertx, VertxTestContext testContext) throws Throwable {
    testContext.completeNow();
  }
}
