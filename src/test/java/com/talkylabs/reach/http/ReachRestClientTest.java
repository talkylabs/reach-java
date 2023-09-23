package com.talkylabs.reach.http;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.talkylabs.reach.rest.Domains;
import com.talkylabs.reach.rest.api.authentix.configurationitem.AuthenticationItem;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

public class ReachRestClientTest {
    private ReachRestClient reachRestClient;
    @Mock
    private HttpClient httpClient;

    private ReachRestClient reachRestClientExtension;

    private List<String> userAgentStringExtensions = Arrays.asList("ce-appointment-reminders/1.0.0", "code-exchange");

    private static final String USER_NAME = "AC123";

    private static final String TOKEN = "AUTH TOKEN";

    private static final String URI = "/v1/fetch?messageId=MM123";
    private static final String authResponse = "{" +
    		"  \"appletId\": \"AIDXXXXXXXXXXXX\"," +
    		"  \"apiVersion\": \"1.0.0\"," +
    		"  \"configurationId\": \"CIDXXXXXXXXXXXX\"," +
    		"  \"authenticationId\": \"VIDXXXXXXXXXXXX\"," +
    		"  \"status\": \"awaiting\"," +
    		"  \"dest\": \"+237671234567\"," +
    		"  \"channel\": \"sms\"," +
    		"  \"expiryTime\": 5," +
    		"  \"maxTrials\": 5," +
    		"  \"maxControls\": 3," +
    		"  \"paymentInfo\": {" +
    		"    \"payee\": \"ACME\"," +
    		"    \"amount\": 1000," +
    		"    \"currency\": \"xaf\"" +
    		"  }," +
    		"  \"trials\": [" +
    		"    {" +
    		"      \"dateCreated\": \"2016-08-29T09:12:33.001Z\"," +
    		"      \"trialId\": \"TRDXXXXXXXXXX\"," +
    		"      \"channel\": \"sms\"" +
    		"    }" +
    		"  ]," +
    		"  \"dateCreated\": \"2016-08-29T09:12:33.001Z\"," +
    		"  \"dateUpdated\": \"2016-08-29T09:12:35.001Z\"" +
    		"}";

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        reachRestClient = new ReachRestClient(new ReachRestClient.Builder("AC123", "AUTH TOKEN").httpClient(httpClient));
    }

    @Test
    public void testRequest() {
        Request request = new Request(
                HttpMethod.GET,
                Domains.API.toString(),
                URI
        );
        when(httpClient.reliableRequest(request)).thenReturn(new Response("", 200));

        Response resp = reachRestClient.request(request);
        assertNotNull(resp);
    }
    
    @Test
    public void testRequestDeserialization() {
        Request request = new Request(
                HttpMethod.GET,
                Domains.API.toString(),
                URI
        );
        when(httpClient.reliableRequest(request)).thenReturn(new Response(authResponse, 200));

        Response resp = reachRestClient.request(request);
        assertNotNull(resp);
        AuthenticationItem item = AuthenticationItem.fromJson(resp.getContent(), reachRestClient.getObjectMapper());
        assertNotNull(item);
    }

    @Test
    public void testRequestWithExtension() {
        Request request = new Request(
                HttpMethod.GET,
                Domains.API.toString(),
                URI
        );
        reachRestClientExtension = new ReachRestClient.Builder(USER_NAME, TOKEN)
                .userAgentExtensions(userAgentStringExtensions)
                .httpClient(httpClient)
                .build();
        
        when(httpClient.reliableRequest(request)).thenReturn(new Response("", 200));
        reachRestClientExtension.request(request);
        assertEquals(userAgentStringExtensions, request.getUserAgentExtensions());
    }

    @Test
    public void testRequestWithExtensionEmpty() {
        Request request = new Request(
                HttpMethod.GET,
                Domains.API.toString(),
                URI
        );
        reachRestClientExtension = new ReachRestClient.Builder(USER_NAME, TOKEN)
                .userAgentExtensions(Collections.emptyList())
                .httpClient(httpClient)
                .build();
        
        when(httpClient.reliableRequest(request)).thenReturn(new Response("", 200));
        reachRestClientExtension.request(request);
        assertNull(request.getUserAgentExtensions());
    }

    @Test
    public void testRequestWithExtensionNull() {
        Request request = new Request(
                HttpMethod.GET,
                Domains.API.toString(),
                URI
        );
        reachRestClientExtension = new ReachRestClient.Builder(USER_NAME, TOKEN)
                .userAgentExtensions(null)
                .httpClient(httpClient)
                .build();
        
        when(httpClient.reliableRequest(request)).thenReturn(new Response("", 200));
        reachRestClientExtension.request(request);
        assertNull(request.getUserAgentExtensions());
    }
}
