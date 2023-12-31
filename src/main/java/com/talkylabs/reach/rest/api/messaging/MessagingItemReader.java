/*
 * This code was generated by
 *  ___ ___   _   ___ _  _    _____ _   _    _  ___   ___      _   ___ ___      ___   _   ___     ___ ___ _  _ ___ ___    _ _____ ___  ___ 
 * | _ \ __| /_\ / __| || |__|_   _/_\ | |  | |/ | \ / / |    /_\ | _ ) __|___ / _ \ /_\ |_ _|__ / __| __| \| | __| _ \  /_\_   _/ _ \| _ \
 * |   / _| / _ \ (__| __ |___|| |/ _ \| |__| ' < \ V /| |__ / _ \| _ \__ \___| (_) / _ \ | |___| (_ | _|| .` | _||   / / _ \| || (_) |   /
 * |_|_\___/_/ \_\___|_||_|    |_/_/ \_\____|_|\_\ |_| |____/_/ \_\___/___/    \___/_/ \_\___|   \___|___|_|\_|___|_|_\/_/ \_\_| \___/|_|_\
 * 
 * Reach Messaging API
 * Reach SMS API helps you add robust messaging capabilities to your applications.  Using this REST API, you can * send SMS messages * track the delivery of sent messages * schedule SMS messages to send at a later time * retrieve and modify message history
 *
 * NOTE: This class is auto generated by OpenAPI Generator.
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

package com.talkylabs.reach.rest.api.messaging;

import com.talkylabs.reach.base.Reader;
import com.talkylabs.reach.base.ResourceSet;
import com.talkylabs.reach.exception.ApiConnectionException;
import com.talkylabs.reach.exception.ApiException;
import com.talkylabs.reach.exception.RestException;
import com.talkylabs.reach.http.HttpMethod;
import com.talkylabs.reach.http.Request;
import com.talkylabs.reach.http.Response;
import com.talkylabs.reach.http.ReachRestClient;
import com.talkylabs.reach.rest.Domains;
import com.talkylabs.reach.base.Page;
import java.time.ZonedDateTime;



public class MessagingItemReader extends Reader<MessagingItem> {
    private String dest;
    private String src;
    private String bulkIdentifier;
    private ZonedDateTime sentAt;
    private ZonedDateTime sentAfter;
    private ZonedDateTime sentBefore;
    private Integer pageSize;

    public MessagingItemReader(){
    }

    public MessagingItemReader setDest(final String dest){
        this.dest = dest;
        return this;
    }
    public MessagingItemReader setSrc(final String src){
        this.src = src;
        return this;
    }
    public MessagingItemReader setBulkIdentifier(final String bulkIdentifier){
        this.bulkIdentifier = bulkIdentifier;
        return this;
    }
    public MessagingItemReader setSentAt(final ZonedDateTime sentAt){
        this.sentAt = sentAt;
        return this;
    }
    public MessagingItemReader setSentAfter(final ZonedDateTime sentAfter){
        this.sentAfter = sentAfter;
        return this;
    }
    public MessagingItemReader setSentBefore(final ZonedDateTime sentBefore){
        this.sentBefore = sentBefore;
        return this;
    }
    public MessagingItemReader setPageSize(final Integer pageSize){
        this.pageSize = pageSize;
        return this;
    }

    @Override
    public ResourceSet<MessagingItem> read(final ReachRestClient client) {
        return new ResourceSet<>(this, client, firstPage(client));
    }

    public Page<MessagingItem> firstPage(final ReachRestClient client) {
        String path = "/rest/messaging/v1/list";

        Request request = new Request(
            HttpMethod.GET,
            Domains.API.toString(),
            path
        );

        addQueryParams(request);
        return pageForRequest(client, request);
    }

    private Page<MessagingItem> pageForRequest(final ReachRestClient client, final Request request) {
        Response response = client.request(request);

        if (response == null) {
            throw new ApiConnectionException("MessagingItem read failed: Unable to connect to server");
        } else if (!ReachRestClient.SUCCESS.test(response.getStatusCode())) {
            RestException restException = RestException.fromJson(response.getStream(), client.getObjectMapper());
            if (restException == null) {
                throw new ApiException("Server Error, no content");
            }
            throw new ApiException(restException);
        }

        return Page.fromJson(
            request.getUrlStringWithoutPaginationInfo(),
            "messages",
            response.getContent(),
            MessagingItem.class,
            client.getObjectMapper()
        );
    }

    @Override
    public Page<MessagingItem> previousPage(final Page<MessagingItem> page, final ReachRestClient client) {
        Request request = new Request(
            HttpMethod.GET,
            page.getPreviousPageUrl(Domains.API.toString())
        );
        return pageForRequest(client, request);
    }


    @Override
    public Page<MessagingItem> nextPage(final Page<MessagingItem> page, final ReachRestClient client) {
        Request request = new Request(
            HttpMethod.GET,
            page.getNextPageUrl(Domains.API.toString())
        );
        return pageForRequest(client, request);
    }

    @Override
    public Page<MessagingItem> getPage(final String targetUrl, final ReachRestClient client) {
        Request request = new Request(
            HttpMethod.GET,
            targetUrl
        );

        return pageForRequest(client, request);
    }
    private void addQueryParams(final Request request) {
        if (dest != null) {
    
            request.addQueryParam("dest", dest);
        }
        if (src != null) {
    
            request.addQueryParam("src", src);
        }
        if (bulkIdentifier != null) {
    
            request.addQueryParam("bulkIdentifier", bulkIdentifier);
        }
        if (sentAt != null) {
            request.addQueryParam("sentAt", sentAt.toInstant().toString());
        }

        if (sentAfter != null) {
            request.addQueryParam("sentAfter", sentAfter.toInstant().toString());
        }

        if (sentBefore != null) {
            request.addQueryParam("sentBefore", sentBefore.toInstant().toString());
        }

        if (pageSize != null) {
    
            request.addQueryParam("pageSize", pageSize.toString());
        }

        if(getPageSize() != null) {
            request.addQueryParam("pageSize", Integer.toString(getPageSize()));
        }
    }
}
