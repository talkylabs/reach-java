package com.talkylabs.reach;

import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mock;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.mockito.MockitoAnnotations;

import com.talkylabs.reach.exception.ApiException;
import com.talkylabs.reach.exception.AuthenticationException;
import com.talkylabs.reach.exception.CertificateValidationException;
import com.talkylabs.reach.http.HttpMethod;
import com.talkylabs.reach.http.NetworkHttpClient;
import com.talkylabs.reach.http.Request;
import com.talkylabs.reach.http.Response;
import com.talkylabs.reach.http.ReachRestClient;

import static org.mockito.Mockito.when;

public class ReachTest {

    private static final String USER_NAME = "UserName";

    private static final String TOKEN = "Password";

    public static String serialize(Object object) {
        return object.toString();
    }

    public static String serialize(List list) {
        // NOTE: This relies on the fact that integration tests only ever generate single element lists.
        return list.get(0).toString();
    }

    @Mock
    private NetworkHttpClient networkHttpClient;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetExecutorService() {
    	Assertions.assertNotNull(Reach.getExecutorService());
    }

    @Test
    public void testGetRestClientNullAccountSid() {
    	
        Assertions.assertThrows(AuthenticationException.class, () -> {
        	Reach.setRestClient(null);
        	Reach.setUsername(null);
        	Reach.setPassword(null);

        	Reach.getRestClient();
        	Assertions.fail("AuthenticationException was expected");
        });
    }

    @Test
    public void testSetAccountSidNull() {
    	Assertions.assertThrows(AuthenticationException.class, () -> {
	    	Reach.setUsername(null);
	    	Assertions.fail("AuthenticationException was expected");
    	});
    }

    @Test
    public void testSetAuthTokenNull() {
    	Assertions.assertThrows(AuthenticationException.class, () -> {
    		Reach.setPassword(null);
    		Assertions.fail("AuthenticationException was expected");
    	});
    }

    @Test
    public void testUserAgentExtensions() {
    	Reach.setUsername(USER_NAME);
    	Reach.setPassword(TOKEN);
    	Reach.setUserAgentExtensions(Arrays.asList("ce-appointment-reminders/1.0.0", "code-exchange"));
    	Reach.getRestClient();
    	Assertions.assertEquals(Arrays.asList("ce-appointment-reminders/1.0.0", "code-exchange"), Reach.getUserAgentExtensions());
    }

    @Test
    public void testUserAgentExtensionsEmpty() {
    	Reach.setUsername(USER_NAME);
    	Reach.setPassword(TOKEN);
    	Reach.setUserAgentExtensions(Collections.emptyList()); // Resetting userAgentExtension
    	Reach.getRestClient();
    	Assertions.assertNull(Reach.getUserAgentExtensions());
    }

    @Test
    public void testUserAgentExtensionsNull() {
    	Reach.setUsername(USER_NAME);
    	Reach.setPassword(TOKEN);
    	Reach.setUserAgentExtensions(null); // Resetting userAgentExtension
    	Reach.getRestClient();
    	Assertions.assertNull(Reach.getUserAgentExtensions());
    }

    @Test
    public void testSetExecutorService() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        Reach.setExecutorService(executorService);
        Assertions.assertEquals(executorService, Reach.getExecutorService());
    }

    @Test
    public void testDestroyExecutorService() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        Reach.setExecutorService(executorService);
        Reach.destroy();
        Assertions.assertTrue(Reach.getExecutorService().isShutdown());
    }

    @Test
    public void testSetRestClient() {
    	ReachRestClient reachRestClient = new ReachRestClient.Builder("AC123", "AUTH TOKEN").build();
        Reach.setRestClient(reachRestClient);
        Assertions.assertEquals(reachRestClient, Reach.getRestClient());
    }

    @Test
    public void testValidateSslCertificateError() {
        final Request request = new Request(HttpMethod.GET, "https://api.reach.talkylabs.com:8443");
        when(networkHttpClient.makeRequest(request)).thenReturn(new Response("", 500));
        try {
        	Reach.validateSslCertificate(networkHttpClient);
        	Assertions.fail("Excepted CertificateValidationException");
        } catch (final CertificateValidationException e) {
        	Assertions.assertEquals("Unexpected response from certificate endpoint", e.getMessage());
        }
    }

    @Test
    public void testValidateSslCertificateException() {
        final Request request = new Request(HttpMethod.GET, "https://api.reach.talkylabs.com:8443");
        when(networkHttpClient.makeRequest(request)).thenThrow(new ApiException("No"));

        try {
        	Reach.validateSslCertificate(networkHttpClient);
        	Assertions.fail("Excepted CertificateValidationException");
        } catch (final CertificateValidationException e) {
        	Assertions.assertEquals("Could not get response from certificate endpoint", e.getMessage());
        }
    }

    @Test
    public void testValidateSslCertificateSuccess() {
        final Request request = new Request(HttpMethod.GET, "https://api.reach.talkylabs.com:8443");
        when(networkHttpClient.makeRequest(request)).thenReturn(new Response("", 200));

        Reach.validateSslCertificate(networkHttpClient);
    }
}
