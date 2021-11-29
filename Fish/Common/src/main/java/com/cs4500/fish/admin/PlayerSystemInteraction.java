package com.cs4500.fish.admin;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PlayerSystemInteraction {

  // Attempt to perform the given callable `f`.
  // Return `Optional.empty()` if the function times out after
  // `timeout` seconds, or some exception gets thrown in `f`.
  public static <T> Optional<T> requestResponseTimeout(Callable<T> f,
      int timeout) {
    ExecutorService service = Executors.newFixedThreadPool(1);
    Future<T> futureResult = service.submit(f);
    try {
      T result = futureResult.get(timeout, TimeUnit.SECONDS);
      return Optional.of(result);
    } catch(TimeoutException | ExecutionException | InterruptedException e) {
      // ExecutionException wraps whatever exception the submitted function
      // may throw.
    } finally { // don't waste resource
      futureResult.cancel(true);
    }
    return Optional.empty();
  }

}
