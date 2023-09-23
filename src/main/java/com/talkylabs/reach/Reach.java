package com.talkylabs.reach;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.talkylabs.reach.exception.ApiException;
import com.talkylabs.reach.exception.AuthenticationException;
import com.talkylabs.reach.exception.CertificateValidationException;
import com.talkylabs.reach.http.HttpMethod;
import com.talkylabs.reach.http.NetworkHttpClient;
import com.talkylabs.reach.http.ReachRestClient;
import com.talkylabs.reach.http.Request;
import com.talkylabs.reach.http.Response;




/**
 * Singleton class to initialize Reach environment.
 */
public class Reach {

    public static final String VERSION = "1.0.0";
    public static final String JAVA_VERSION = System.getProperty("java.version");
    public static final String OS_NAME = System.getProperty("os.name");
    public static final String OS_ARCH = System.getProperty("os.arch");
    private static String username = System.getenv("REACH_TALKYLABS_API_USER");
    private static String password = System.getenv("REACH_TALKYLABS_API_KEY");

    
    
    private static List<String> userAgentExtensions;
    private static volatile ReachRestClient restClient;
    private static volatile ExecutorService executorService;
    
    
    private Reach() {
    }
    
    /*
     * Ensures that the ExecutorService is shutdown when the JVM exits.
     */
    static {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if (executorService != null) {
                    executorService.shutdownNow();
                }
            }
        });
    }

    /**
     * Initialize the Reach environment.
     *
     * @param username account to use
     * @param password auth token for the account
     */
    public static synchronized void init(final String username, final String password) {
    	Reach.setUsername(username);
    	Reach.setPassword(password);
    }

    /**
     * Set the username.
     *
     * @param username account to use
     * @throws AuthenticationException if username is null
     */
    public static synchronized void setUsername(final String username) {
        if (username == null) {
            throw new AuthenticationException("Username can not be null");
        }

        if (!username.equals(Reach.username)) {
        	Reach.invalidate();
        }

        Reach.username = username;
    }

    /**
     * Set the auth token.
     *
     * @param password auth token to use
     * @throws AuthenticationException if password is null
     */
    public static synchronized void setPassword(final String password) {
        if (password == null) {
            throw new AuthenticationException("Password can not be null");
        }

        if (!password.equals(Reach.password)) {
        	Reach.invalidate();
        }

        Reach.password = password;
    }

    
    public static synchronized void setUserAgentExtensions(final List<String> userAgentExtensions) {
        if (userAgentExtensions != null && !userAgentExtensions.isEmpty()) {
        	Reach.userAgentExtensions = new ArrayList<>(userAgentExtensions);
        } else {
            // In case a developer wants to reset userAgentExtensions
        	Reach.userAgentExtensions = null;
        }
    }
    
    public static synchronized List<String> getUserAgentExtensions() {
    	return Reach.userAgentExtensions;
    }

    

    /**
     * Returns (and initializes if not initialized) the Reach Rest Client.
     *
     * @return the Reach Rest Client
     * @throws AuthenticationException if initialization required and either accountSid or authToken is null
     */
    public static ReachRestClient getRestClient() {
        if (Reach.restClient == null) {
            synchronized (Reach.class) {
                if (Reach.restClient == null) {
                	Reach.restClient = buildRestClient();
                }
            }
        }

        return Reach.restClient;
    }

    private static ReachRestClient buildRestClient() {
        if (Reach.username == null || Reach.password == null) {
            throw new AuthenticationException(
                "ReachRestClient was used before ApiUser and ApiKey were set, please call Reach.init()"
            );
        }

        ReachRestClient.Builder builder = new ReachRestClient.Builder(Reach.username, Reach.password);

        if (userAgentExtensions != null) {
            builder.userAgentExtensions(Reach.userAgentExtensions);
        }


        return builder.build();
    }

    /**
     * Use a custom rest client.
     *
     * @param restClient rest client to use
     */
    public static void setRestClient(final ReachRestClient restClient) {
        synchronized (Reach.class) {
        	Reach.restClient = restClient;
        }
    }

    /**
     * Returns the Reach executor service.
     *
     * @return the Reach executor service
     */
    public static ExecutorService getExecutorService() {
        if (Reach.executorService == null) {
            synchronized (Reach.class) {
                if (Reach.executorService == null) {
                	Reach.executorService = Executors.newCachedThreadPool();
                }
            }
        }
        return Reach.executorService;
    }

    /**
     * Use a custom executor service.
     *
     * @param executorService executor service to use
     */
    public static void setExecutorService(final ExecutorService executorService) {
        synchronized (Reach.class) {
        	Reach.executorService = executorService;
        }
    }

    

    /**
     * Invalidates the volatile state held in the Reach singleton.
     */
    private static void invalidate() {
    	Reach.restClient = null;
    }

    /**
     * Attempts to gracefully shutdown the ExecutorService if it is present.
     */
    public static synchronized void destroy() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }
    
}
