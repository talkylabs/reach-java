package com.talkylabs.reach.http;

import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.talkylabs.reach.exception.ApiException;
import com.talkylabs.reach.exception.InvalidRequestException;

public class Request {

    public static final String QUERY_STRING_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String QUERY_STRING_DATE_FORMAT = "yyyy-MM-dd";

    private final HttpMethod method;
    private final String url;
    private final Map<String, List<String>> queryParams;
    private final Map<String, List<String>> postParams;
    private final Map<String, List<String>> headerParams;

    private String username;
    private String password;

    private List<String> userAgentExtensions;

    /**
     * Create a new API request.
     *
     * @param method HTTP method
     * @param url    url of request
     */
    public Request(final HttpMethod method, final String url) {
        this.method = method;
        this.url = url;
        this.queryParams = new HashMap<>();
        this.postParams = new HashMap<>();
        this.headerParams = new HashMap<>();
    }

    /**
     * Create a new API request.
     *
     * @param method HTTP Method
     * @param domain Twilio domain
     * @param uri    uri of request
     */
    public Request(
        final HttpMethod method,
        final String domain,
        final String uri
    ) {
        this.method = method;
        this.url = "https://" + domain + ".reach.talkylabs.com" + uri;
        this.queryParams = new HashMap<>();
        this.postParams = new HashMap<>();
        this.headerParams = new HashMap<>();
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public void setAuth(final String username, final String password) {
        this.username = username;
        this.password = password;
    }

    public void setUserAgentExtensions(List<String> userAgentExtensions) {
        this.userAgentExtensions = userAgentExtensions;
    }

    public List<String> getUserAgentExtensions() {
        return this.userAgentExtensions;
    }

    /**
     * Create auth string from username and password.
     *
     * @return basic authentication string
     */
    public String getAuthString() {
        final String credentials = this.username + ":" + this.password;
        final String encoded = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.US_ASCII));
        return "Basic " + encoded;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean requiresAuthentication() {
        return username != null || password != null;
    }

    /**
     * Build the URL for the request.
     *
     * @return URL for the request
     */
    public URL constructURL() {
        String params = encodeQueryParams();
        String stringUri = buildURL();

        if (params.length() > 0) {
            stringUri += "?" + params;
        }

        try {
            URI uri = new URI(stringUri);
            return uri.toURL();
        } catch (final URISyntaxException e) {
            throw new ApiException("Bad URI: " + e.getMessage());
        } catch (final MalformedURLException e) {
            throw new ApiException("Bad URL: " + e.getMessage());
        }
    }

    private String buildURL() {
        try {
            final URL parsedUrl = new URL(url);
            String host = parsedUrl.getHost();           

            String urlPort = parsedUrl.getPort() != -1 ? ":" + parsedUrl.getPort() : null;
            String protocol = parsedUrl.getProtocol() + "://";
            String[] pathPieces = parsedUrl.getPath().split("/");
            for (int i = 0; i < pathPieces.length; i++) {
                pathPieces[i] = URLEncoder.encode(pathPieces[i], "UTF-8");
            }
            String encodedPath = String.join("/", pathPieces);
            String query = parsedUrl.getQuery() != null ? "?" + parsedUrl.getQuery() : null;
            String ref = parsedUrl.getRef() != null ? "#" + parsedUrl.getRef() : null;
            String credentials = parsedUrl.getUserInfo() != null ? parsedUrl.getUserInfo() + "@" : null;
            return joinIgnoreNull("", protocol, credentials, host, urlPort, encodedPath, query, ref);
        } catch (final MalformedURLException | UnsupportedEncodingException e) {
            throw new ApiException("Bad URL: "+ e.getMessage());
        }
    }

    /**
     * Add query parameters for date ranges.
     *
     * @param name  name of query parameter
     * @param lowerBound lower bound of LocalDate range
     * @param upperBound upper bound of LocalDate range
     */
    public void addQueryDateRange(final String name, LocalDate lowerBound, LocalDate upperBound) {
        if (lowerBound != null) {
            String value = lowerBound.toString();
            addQueryParam(name + ">", value);
        }

        if (upperBound != null) {
            String value = upperBound.toString();
            addQueryParam(name + "<", value);
        }
    }

    /**
     * Add query parameters for date ranges.
     *
     * @param name  name of query parameter
     * @param lowerBound lower bound of ZonedDateTime range
     * @param upperBound upper bound of ZonedDateTime range
     */
    public void addQueryDateTimeRange(final String name, ZonedDateTime lowerBound, ZonedDateTime upperBound) {
        if (lowerBound != null) {
            String value = lowerBound.withZoneSameInstant(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern(QUERY_STRING_DATE_TIME_FORMAT));
            addQueryParam(name + ">", value);
        }

        if (upperBound != null) {
            String value = upperBound.withZoneSameInstant(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern(QUERY_STRING_DATE_TIME_FORMAT));
            addQueryParam(name + "<", value);
        }
    }

    /**
     * Add a query parameter.
     *
     * @param name  name of parameter
     * @param value value of parameter
     */
    public void addQueryParam(final String name, final String value) {
        addParam(queryParams, name, value);
    }

    /**
     * Add a form parameter.
     *
     * @param name  name of parameter
     * @param value value of parameter
     */
    public void addPostParam(final String name, final String value) {
        addParam(postParams, name, value);
    }

    /**
     * Add a header parameter.
     *
     * @param name  name of parameter
     * @param value value of parameter
     */
    public void addHeaderParam(final String name, final String value) {
        addParam(headerParams, name, value);
    }

    private void addParam(final Map<String, List<String>> params, final String name, final String value) {
        if (value == null || value.equals("null"))
            return;

        if (!params.containsKey(name)) {
            params.put(name, new ArrayList<String>());
        }

        params.get(name).add(value);
    }

    /**
     * Encode the form body.
     *
     * @return url encoded form body
     */
    public String encodeFormBody() {
        return encodeParameters(postParams);
    }

    /**
     * Encode the query parameters.
     *
     * @return url encoded query parameters
     */
    public String encodeQueryParams() {
        return encodeParameters(queryParams);
    }

    private static String encodeParameters(final Map<String, List<String>> params) {
        List<String> parameters = new ArrayList<>();

        for (final Map.Entry<String, List<String>> entry : params.entrySet()) {
            try {
                String encodedName = URLEncoder.encode(entry.getKey(), "UTF-8");
                for (final String value : entry.getValue()) {
                    if (value == null) {
                        continue;
                    }

                    String encodedValue = URLEncoder.encode(value, "UTF-8");
                    parameters.add(encodedName + "=" + encodedValue);
                }
            } catch (final UnsupportedEncodingException e) {
                throw new InvalidRequestException("Couldn't encode params", entry.getKey(), e);
            }
        }
        return joinIgnoreNull("&", parameters);
    }

    private static String joinIgnoreNull(final String separator, final String... items) {
        return joinIgnoreNull(separator, Arrays.asList(items));
    }

    private static String joinIgnoreNull(final String separator, final List<String> items) {
        final StringBuilder builder = new StringBuilder();

        for (final String item : items) {
            if (item != null) {
                if (builder.length() > 0) {
                    builder.append(separator);
                }

                builder.append(item);
            }
        }

        return builder.toString();
    }

    public Map<String, List<String>> getQueryParams() {
        return queryParams;
    }

    public Map<String, List<String>> getPostParams() {
        return postParams;
    }

    public Map<String, List<String>> getHeaderParams() { return headerParams; }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Request other = (Request) o;
        return Objects.equals(this.method, other.method) &&
               Objects.equals(this.buildURL(), other.buildURL()) &&
               Objects.equals(this.username, other.username) &&
               Objects.equals(this.password, other.password) &&
               Objects.equals(this.queryParams, other.queryParams) &&
               Objects.equals(this.postParams, other.postParams) &&
               Objects.equals(this.headerParams, other.headerParams);
    }
    
    private String getUrlStringWithoutSpecificParameters(List<String> params) {
    	URI url;
		try {
			url = constructURL().toURI();
		} catch (URISyntaxException e1) {
			throw new ApiException("Bad URL: "+ e1.getMessage());
		}
    	if(params ==null || params.size()==0) {
    		return url.toString();
    	}
    	String query = url.getQuery();
    	if(query==null)
    		return url.toString();
    	String[] tmpParams = query.split("&");
    	List<String> queryParams = new ArrayList(Arrays.asList(tmpParams));
    	for(String par:params) {
    		String prefix = par+"=";
    		int i = 0;
    		while(i<queryParams.size()) {
    			if(queryParams.get(i).startsWith(prefix)) {
    				queryParams.remove(i);
    			}else {
    				i++;
    			}
    		}    		
    	}
    	query = queryParams.size()==0?null:joinIgnoreNull("&", queryParams);
    	try {
			return (new URI(url.getScheme(), url.getAuthority(), url.getPath(), query, url.getFragment())).toString();
		} catch (URISyntaxException e) {
			throw new ApiException("Bad URL: "+ e.getMessage());
		}   	
    }
    
    public String getUrlStringWithoutPaginationInfo() {
    	return getUrlStringWithoutSpecificParameters(Arrays.asList("page", "pageSize"));
    }
}
