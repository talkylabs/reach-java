package com.talkylabs.reach.base;

import com.talkylabs.reach.Reach;
import com.talkylabs.reach.http.ReachRestClient;

import java.util.concurrent.CompletableFuture;

/**
 * Executor for updates of a resource.
 *
 * @param <T> type of the resource
 */
public abstract class Updater<T extends Resource> {

    /**
     * Execute an async request using default client.
     *
     * @return future that resolves to requested object
     */
    public CompletableFuture<T> updateAsync() {
        return updateAsync(Reach.getRestClient());
    }

    /**
     * Execute an async request using specified client.
     *
     * @param client client used to make request
     * @return future that resolves to requested object
     */
    public CompletableFuture<T> updateAsync(final ReachRestClient client) {
        return CompletableFuture.supplyAsync(() -> update(client), Reach.getExecutorService());
    }

    /**
     * Execute a request using default client.
     *
     * @return Requested object
     */
    public T update() {
        return update(Reach.getRestClient());
    }

    /**
     * Execute a request using specified client.
     *
     * @param client client used to make request
     * @return Requested object
     */
    public abstract T update(final ReachRestClient client);
}
