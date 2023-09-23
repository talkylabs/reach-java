package com.talkylabs.reach.base;

import com.talkylabs.reach.Reach;
import com.talkylabs.reach.http.ReachRestClient;

import java.util.concurrent.CompletableFuture;

/**
 * Executor for unscheduling resources.
 *
 * @param <T> type of the resource
 */
public abstract class Unscheduler<T extends Resource> {

    /**
     * Execute an async request using default client.
     *
     * @return future that resolves to requested object
     */
    public CompletableFuture<T> unscheduleAsync() {
        return unscheduleAsync(Reach.getRestClient());
    }

    /**
     * Execute an async request using specified client.
     *
     * @param client client used to make request
     * @return future that resolves to requested object
     */
    public CompletableFuture<T> unscheduleAsync(final ReachRestClient client) {
        return CompletableFuture.supplyAsync(() -> unschedule(client), Reach.getExecutorService());
    }

    /**
     * Execute a request using default client.
     *
     * @return Requested object
     */
    public T unschedule() {
        return unschedule(Reach.getRestClient());
    }

    /**
     * Execute a request using specified client.
     *
     * @param client client used to make request
     * @return Requested object
     */
    public abstract T unschedule(final ReachRestClient client);
}
