package com.talkylabs.reach.base;

import com.talkylabs.reach.Reach;
import com.talkylabs.reach.http.ReachRestClient;

import java.util.concurrent.CompletableFuture;

/**
 * Executor for creation of a resource.
 *
 * @param <T> type of the resource
 */
public abstract class Starter<T extends Resource> {

    /**
     * Execute an async request using default client.
     *
     * @return future that resolves to requested object
     */
    public CompletableFuture<T> startAsync() {
        return startAsync(Reach.getRestClient());
    }

    /**
     * Execute an async request using specified client.
     *
     * @param client client used to make request
     * @return future that resolves to requested object
     */
    public CompletableFuture<T> startAsync(final ReachRestClient client) {
        return CompletableFuture.supplyAsync(() -> start(client), Reach.getExecutorService());
    }

    /**
     * Execute a request using default client.
     *
     * @return Requested object
     */
    public T start() {
        return start(Reach.getRestClient());
    }

    /**
     * Execute a request using specified client.
     *
     * @param client client used to make request
     * @return Requested object
     */
    public abstract T start(final ReachRestClient client);
}
