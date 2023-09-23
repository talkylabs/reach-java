# Custom HTTP Clients for the Reach Java Helper Library

If you are working with the Reach Java Helper Library, and you need to be able to modify the HTTP requests that the library makes to the Reach servers, you’re in the right place. The most common need to alter the HTTP request is to connect and authenticate with an enterprise’s proxy server. We’ll provide sample code that you can drop right into your app to handle this use case.

## Connect and authenticate with a proxy server

To connect and provide credentials to a proxy server that may be between your app and Reach, you need a way to modify the HTTP requests that the Reach helper library makes on your behalf to invoke the Reach REST API.

The Reach Java helper library uses the HttpClient interface (in the org.apache.http.client package) under the hood to make the HTTP requests. With this in mind, the following two facts should help us arrive at the solution:

1. Connecting to a proxy server with HttpClient is a [solved problem](https://hc.apache.org/httpcomponents-client-ga/examples.html).
1. The Reach Helper Library allows you to provide your own `HttpClient` for making API requests.

So the question becomes: how do we apply this to a typical Reach REST API example?

```java
Reach.init(API_USER, API_KEY);

MessagingItem item = MessagingItem.sender("+15558675310", "+15017122661", "Hey there!").send();
```

Where is `ReachRestClient` created and used? Out of the box, the helper library creates a default `ReachRestClient` for you, using the Reach credentials you pass to the `init` method. However, nothing is stopping you from creating your own and using that.

Once you have your own `ReachRestClient`, you can pass it to any Reach REST API resource action you want. Here’s an example of sending an SMS message with a custom client:

```java
// Install the Java helper library
import com.talkylabs.reach.Reach;
import com.talkylabs.reach.http.ReachRestClient;
import com.talkylabs.reach.rest.api.messaging.MessagingItem;
import io.github.cdimascio.dotenv.Dotenv;

public class Example {

  public static void main(String args[]) {
    Dotenv dotenv = Dotenv.configure().directory(".").load();
    String API_USER = dotenv.get("API_USER");
    String API_KEY = dotenv.get("API_KEY");
    String PROXY_HOST = dotenv.get("PROXY_HOST");
    int PROXY_PORT = Integer.parseInt(dotenv.get("PROXY_PORT"));

    Reach.init(API_USER, API_KEY);

    ProxiedReachClientCreator clientCreator = new ProxiedReachClientCreator(
      ACCOUNT_SID,
      AUTH_TOKEN,
      PROXY_HOST,
      PROXY_PORT
    );
    ReachRestClient reachRestClient = clientCreator.getClient();
    Reach.setRestClient(reachRestClient);

    MessagingItem item = MessagingItem
      .sender(
        "+15558675310",
        "+15017122661",
        "Hey there!"
      )
      .send();

    System.out.println(item.getMessageId());
  }
}
```

## Create your custom ReachRestClient

When you take a closer look at the constructor for `ReachRestClient`, you see that the `httpClient` parameter is actually of type `com.talkylabs.reach.http.HttpClient`.

`HttpClient` is an abstraction that allows plugging in any implementation of an HTTP client you want (or even creating a mocking layer for unit testing).

However, within the helper library, there is an implementation of `com.talkylabs.reach.http.HttpClient` called `NetworkHttpClient`. This class wraps the `org.apache.http.client.HttpClient` and provides it to the Reach helper library to make the necessary HTTP requests.

## Call Reach through the proxy server

Now that we understand how all the components fit together, we can create our own `ReachRestClient` that can connect through a proxy server. To make this reusable, here’s a class that you can use to create this `ReachRestClient` whenever you need one:

```java
import com.talkylabs.reach.http.HttpClient;
import com.talkylabs.reach.http.NetworkHttpClient;
import com.talkylabs.reach.http.ReachRestClient;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public class ProxiedReachClientCreator {

  private String username;
  private String password;
  private String proxyHost;
  private int proxyPort;
  private HttpClient httpClient;

  /**
   * Constructor for ProxiedReachClientCreator
   * @param username
   * @param password
   * @param proxyHost
   * @param proxyPort
   */
  public ProxiedReachClientCreator(
    String username,
    String password,
    String proxyHost,
    int proxyPort
  ) {
    this.username = username;
    this.password = password;
    this.proxyHost = proxyHost;
    this.proxyPort = proxyPort;
  }

  /**
   * Creates a custom HttpClient based on default config as seen on:
   * {@link com.talkylabs.reach.http.NetworkHttpClient#NetworkHttpClient() constructor}
   */
  private void createHttpClient() {
    RequestConfig config = RequestConfig
      .custom()
      .setConnectTimeout(10000)
      .setSocketTimeout(30500)
      .build();

    PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
    connectionManager.setDefaultMaxPerRoute(10);
    connectionManager.setMaxTotal(10 * 2);

    HttpHost proxy = new HttpHost(proxyHost, proxyPort);

    HttpClientBuilder clientBuilder = HttpClientBuilder.create();
    clientBuilder
      .setConnectionManager(connectionManager)
      .setProxy(proxy)
      .setDefaultRequestConfig(config);

    // Inclusion of Reach headers and build() is executed under this constructor
    this.httpClient = new NetworkHttpClient(clientBuilder);
  }

  /**
   * Get the custom client or builds a new one
   * @return a ReachRestClient object
   */
  public ReachRestClient getClient() {
    if (this.httpClient == null) {
      this.createHttpClient();
    }

    ReachRestClient.Builder builder = new ReachRestClient.Builder(
      username,
      password
    );
    return builder.httpClient(this.httpClient).build();
  }
}
```

In this example, we use some environment variables loaded at the program startup to retrieve various configuration settings:

- Your Reach Api user and key
- Your proxy server host
- Your proxy port

These settings are located in a file named `.env` like so:

```env
API_USER=XXxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
API_KEY=your_auth_token

PROXY_HOST=127.0.0.1
PROXY_PORT=8888
```

Here’s a console program that sends a text message and shows how it all can work together. It loads the `.env` file for us.

```java
// Install the Java helper library
import com.talkylabs.reach.Reach;
import com.talkylabs.reach.http.ReachRestClient;
import com.talkylabs.reach.rest.api.messaging.MessagingItem;
import io.github.cdimascio.dotenv.Dotenv;

public class Example {

  public static void main(String args[]) {
    Dotenv dotenv = Dotenv.configure().directory(".").load();
    String API_USER = dotenv.get("API_USER");
    String API_KEY = dotenv.get("API_KEY");
    String PROXY_HOST = dotenv.get("PROXY_HOST");
    int PROXY_PORT = Integer.parseInt(dotenv.get("PROXY_PORT"));

    Reach.init(API_USER, API_KEY);

    ProxiedReachClientCreator clientCreator = new ProxiedReachClientCreator(
      API_USER,
      API_KEY,
      PROXY_HOST,
      PROXY_PORT
    );
    ReachRestClient reachRestClient = clientCreator.getClient();
    Reach.setRestClient(reachRestClient);

    MessagingItem item = MessagingItem
      .sender(
        "+15558675310", "+15017122661", "Hey there!"
      )
      .send();

    System.out.println(message.getMessageId());
  }
}
```

## What else can this technique be used for?

Now that you know how to inject your own HttpClient into the Reach API request pipeline, you could use this technique to add custom HTTP headers and authorization to the requests, perhaps as required by an upstream proxy server.

You could also implement your own HttpClient to mock the Reach API responses so your unit and integration tests can run quickly without needing to make a connection to Reach.

We can’t wait to see what you build!
