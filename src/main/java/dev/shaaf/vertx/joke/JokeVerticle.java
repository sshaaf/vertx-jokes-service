package dev.shaaf.vertx.joke;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.net.HttpURLConnection;

public class JokeVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    Router router = Router.router(vertx);

    router.get("/").handler(rc -> {
      rc.response().putHeader("content-type", "text/html")
        .end(" Joke Service");
    });


    router.route().handler(BodyHandler.create());
    router.get("/joke").handler(this::getRandomJoke);

    vertx.createHttpServer().requestHandler(router).listen(8080, http -> {
      if (http.succeeded()) {
        startPromise.complete();
        System.out.println("HTTP server started on port 8080");
      } else {
        startPromise.fail(http.cause());
      }
    });
  }


  public void getRandomJoke(RoutingContext routingContext) {
    String firstName = routingContext.request().getParam("firstName");
    String lastName = routingContext.request().getParam("lastName");

    // Just make sure the requests done fail
    if(firstName == null)
      firstName = "John";
    if (lastName == null)
      lastName = "Doe";

    WebClient client = WebClient.create(vertx);
    client
      .get(80, "api.icndb.com", "/jokes/random")
      .addQueryParam("firstName", firstName)
      .addQueryParam("lastName", lastName)
      .addQueryParam("exclude", "[explicit]")
      .as(BodyCodec.jsonObject())
      .send()
      .onSuccess(res -> {
        JsonObject body = res.body();
        routingContext.response()
          .setStatusCode(HttpURLConnection.HTTP_CREATED)
          .putHeader("content-type", "application/json; charset=utf-8")
          .end(Json.encodePrettily(body));
      })
      .onFailure(err ->
        routingContext.response()
          .setStatusCode(HttpURLConnection.HTTP_NO_CONTENT)
          .end("Response:" + HttpURLConnection.HTTP_NO_CONTENT)

      );
  }

}



