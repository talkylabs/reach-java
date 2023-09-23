# reach-java


## Documentation

The documentation for the Reach@Talkylabs API can be found [here][apidocs].

The Java library documentation can be found [here][libdocs].

## Versions

`reach-java` uses a modified version of [Semantic Versioning](https://semver.org) for all changes. [See this document](VERSIONS.md) for details.

### TLS 1.2 Requirements

It is required to use TLS 1.2 when accessing the REST API. "Upgrade Required" errors indicate that TLS 1.0/1.1 is being used. 

### Supported Java Versions

This library supports the following Java implementations:

- OpenJDK 8
- OpenJDK 11
- OpenJDK 17
- OracleJDK 8
- OracleJDK 11
- OracleJDK 17


## Installation

`reach-java` uses Maven. At present the jars _are_ available from a public [maven](https://mvnrepository.com/artifact/com.talkylabs.sdk/reach) repository.

Use the following dependency in your project to grab via Maven:

```xml
<dependency>
  <groupId>com.talkylabs.sdk</groupId>
  <artifactId>reach</artifactId>
  <version>1.X.X</version>
  <scope>compile</scope>
</dependency>
```

or Gradle:

```groovy
implementation "com.talkylabs.sdk:reach:1.X.X"
```

If you want to compile it yourself, here's how:

```shell
git clone git@github.com:talkylabs/reach-java
cd reach-java
mvn install       # Requires maven, download from https://maven.apache.org/download.html
```

If you want to build your own .jar, execute the following from within the cloned directory:

```shell
mvn package
```

If you run into trouble with local tests, use:

```shell
mvn package -Dmaven.test.skip=true
```

### Test your installation

Try sending yourself an SMS message, like this:

```java
import com.talkylabs.reach.Reach;
import com.talkylabs.reach.rest.api.messaging.MessagingItem;

public class BasicUsage {

  // Find your apiUser and apiKey at the web application
  public static final String API_USER = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
  public static final String API_KEY = "your_auth_token";

  public static void main(String[] args) {
    Reach.init(API_USER, API_KEY);

    MessagingItem message = MessagingItem
      .sender(
        "+15558675309", "+15017250604", "Reach is great!"
      )
      .send();

    System.out.println(message.getMessageId());
  }
}
```

> **Warning**
> It's okay to hardcode your credentials when testing locally, but you should use environment variables to keep them secret before committing any code or deploying to production.

## Usage

### Initialize the Client

```java
import com.talkylabs.reach.Reach;
import com.talkylabs.reach.exception.AuthenticationException;

public class BasicUsage {

  private static final String API_USER =
    "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
  private static final String API_KEY = "your_auth_token";

  public static void main(String[] args) throws AuthenticationException {
    Reach.init(API_USER, API_KEY);
  }
}
```

### Environment Variables

`reach-java` supports the credentials values stored in the following environment variables:

- `REACH_TALKYLABS_API_USER`
- `REACH_TALKYLABS_API_KEY`

If using these variables, the above client initialization can be skipped.


### Iterate through records

The library automatically handles paging for you. With the `read` method, you can specify the number of records you want to receive (`limit`) and the maximum size you want each page fetch to be (`pageSize`). The library will then handle the task for you, fetching new pages under the hood as you iterate over the records.

#### Use the `read` method

```java
import com.talkylabs.reach.Reach;
import com.talkylabs.reach.rest.api.messaging.MessagingItem;
import com.talkylabs.reach.base.ResourceSet;

public class BasicUsage {

  public static final String API_USER = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
  public static final String API_KEY = "your_auth_token";

  public static void main(String[] args) {
    Reach.init(API_USER, API_KEY);

    ResourceSet<MessagingItem> messagingItems = MessagingItem.reader().read();

    for (MessagingItem item : messagingItems) {
      System.out.println(item.getMessageId());
    }
  }
}
```

### Enable Debug Logging

This library uses SLF4J for logging. Consult the [SFL4J documentation](http://slf4j.org/docs.html) for information about logging configuration.

For example, if you are using `log4j`:

- Make sure you have `log4j-slf4j-impl`, `log4j-core` and `log4j-api` in your `pom.xml` file
- Define the logging level for the Reach HTTP client in your configuration. For example, in `src/main/resources/log4j2.xml`:

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <Configuration status="WARN">
      <Appenders>
          <Console name="Console" target="SYSTEM_OUT">
              <PatternLayout pattern="%d{HH:mm:ss.SSS} %-5level - %msg%n"/>
          </Console>
      </Appenders>
      <Loggers>
          <!--Your Reach logging configuration goes here-->
          <Logger name="com.talkylabs.reach.http" level="debug" additivity="false">
              <AppenderRef ref="Console"/>
          </Logger>
          <Root level="info">
              <AppenderRef ref="Console"/>
          </Root>
      </Loggers>
  </Configuration>
  ```

### Handle Exceptions

```java
import com.talkylabs.reach.exception.ApiException;

try {
    Message message = Message.creator(
        new PhoneNumber("+15558881234"),  // To number
        new PhoneNumber("+15559994321"),  // From number
        "Hello world!"                    // SMS body
    ).create();

    System.out.println(message.getSid());
} catch (final ApiException e) {
    System.err.println(e);
}
```


### Use a custom HTTP Client

To use a custom HTTP client with this helper library, please see the [advanced example of how to do so](./advanced-examples/custom-http-client.md).

## Docker image

The `Dockerfile` present in this repository and its respective `talkylabs/reach-java` Docker image are currently used by TalkyLabs for testing purposes only.

## Getting Help

If you've found a bug in the library or would like new features added, go ahead and open issues or pull requests against this repo!

[apidocs]: https://www.reach.talkylabs.com/docs/api
[libdocs]: https://talkylabs.github.io/reach-java
