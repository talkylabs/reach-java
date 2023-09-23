package com.talkylabs.reach.http;

import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.talkylabs.reach.exception.ApiException;
import com.talkylabs.reach.rest.Domains;

import java.net.MalformedURLException;
import java.net.URL;

import static com.talkylabs.reach.Assert.assertQueryStringsEqual;
import static com.talkylabs.reach.Assert.assertUrlsEqual;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RequestTest {

    @Test
    public void testConstructorWithDomain() {
        Request request = new Request(HttpMethod.GET, Domains.API.toString(), "/v1/uri");
        assertNotNull(request);
        assertEquals(HttpMethod.GET, request.getMethod());
        assertEquals("https://api.reach.talkylabs.com/v1/uri", request.getUrl());
    }

    @Test
    public void testConstructURL() throws MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/v1/foobar");
        URL url = r.constructURL();
        URL expected = new URL("https://api.reach.talkylabs.com/v1/foobar");
        assertUrlsEqual(expected, url);
    }

    @Test
    public void testConstructURLURISyntaxException() {
    	Assertions.assertThrows(ApiException.class, () -> {
	        Request request = new Request(HttpMethod.DELETE, "http://{");
	        request.constructURL();
	        Assertions.fail("ApiException was expected");
    	});
    }

    @Test
    public void testConstructURLURISyntaxExceptionContent() {
        Request request = new Request(HttpMethod.DELETE, "http://{");
        ApiException e = assertThrows(ApiException.class, request::constructURL);
        assertEquals("Bad URI: Illegal character in authority at index 7: http://{", e.getMessage());
    }

    @Test
    public void testConstructURLMalformedExceptionContent(){
        Request request = new Request(HttpMethod.DELETE, "/v1/foo<>");
        ApiException e = assertThrows(ApiException.class, request::constructURL);
        assertEquals("Bad URL: no protocol: /v1/foo<>", e.getMessage());
    }

    @Test
    public void testConstructURLWithPipe() throws MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/v1/foo|bar");
        URL url = r.constructURL();
        URL expected = new URL("https://api.reach.talkylabs.com/v1/foo%7Cbar");
        assertUrlsEqual(expected, url);
    }

    @Test
    public void testConstructURLWithMultipleSlashes() throws MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/v1/foo|bar/bar|foo");
        URL url = r.constructURL();
        URL expected = new URL("https://api.reach.talkylabs.com/v1/foo%7Cbar/bar%7Cfoo");
        assertUrlsEqual(expected, url);
    }

    @Test
    public void testConstructURLWithCredentials() throws MalformedURLException {
        Request r = new Request(HttpMethod.GET, "user:pass@" + Domains.API.toString(), "/v1/foobar");
        URL url = r.constructURL();
        URL expected = new URL("https://user:pass@api.reach.talkylabs.com/v1/foobar");
        assertUrlsEqual(expected, url);
    }

    @Test
    public void testConstructURLWithParam() throws MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/v1/foobar");
        r.addQueryParam("baz", "quux");
        URL url = r.constructURL();
        URL expected = new URL("https://api.reach.talkylabs.com/v1/foobar?baz=quux");
        assertUrlsEqual(expected, url);
    }

    @Test
    public void testConstructURLWithParams() throws MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/v1/foobar");
        r.addQueryParam("baz", "quux");
        r.addQueryParam("garply", "xyzzy");
        URL url = r.constructURL();
        URL expected = new URL("https://api.reach.talkylabs.com/v1/foobar?baz=quux&garply=xyzzy");
        assertUrlsEqual(expected, url);
    }

    @Test
    public void testConstructURLWithPlusPrefix() {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/v1/foobar");
        r.addQueryParam("To", "+18888888888");
        URL url = r.constructURL();
        String expected = "https://api.reach.talkylabs.com/v1/foobar?To=%2B18888888888";
        assertEquals(expected, url.toString());
    }

    @Test
    public void testConstructURLWithMultivaluedParam() throws MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/v1/foobar");
        r.addQueryParam("baz", "quux");
        r.addQueryParam("baz", "xyzzy");
        URL url = r.constructURL();
        URL expected = new URL("https://api.reach.talkylabs.com/v1/foobar?baz=quux&baz=xyzzy");
        assertUrlsEqual(expected, url);
    }

    @Test
    public void testConstructURLWithInequalityParam() throws MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/v1/foobar");
        r.addQueryParam("baz>", "3");
        URL url = r.constructURL();
        URL expected = new URL("https://api.reach.talkylabs.com/v1/foobar?baz>=3");
        assertUrlsEqual(expected, url);
    }

    @Test
    public void testAddQueryDateRangeLowerBound() throws MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/v1/foobar");
        r.addQueryDateRange("baz", LocalDate.of(2014, 1, 1), null);
        URL url = r.constructURL();
        URL expected = new URL("https://api.reach.talkylabs.com/v1/foobar?baz>=2014-01-01");
        assertUrlsEqual(expected, url);
    }

    @Test
    public void testAddQueryDateRangeUpperBound() throws MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/v1/foobar");
        r.addQueryDateRange("baz", null, LocalDate.of(2014, 1, 1));
        URL url = r.constructURL();
        URL expected = new URL("https://api.reach.talkylabs.com/v1/foobar?baz<=2014-01-01");
        assertUrlsEqual(expected, url);
    }

    @Test
    public void testAddQueryDateRangeClosed() throws MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/v1/foobar");
        r.addQueryDateRange("baz", LocalDate.of(2014, 1, 10), LocalDate.of(2014, 6, 1));
        URL url = r.constructURL();
        URL expected = new URL("https://api.reach.talkylabs.com/v1/foobar?baz>=2014-01-10&baz<=2014-06-01");
        assertUrlsEqual(expected, url);
    }

    @Test
    public void testAddQueryDateRangeMismatchedBounds() throws MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/v1/foobar");
        LocalDate wrongLowerBound = LocalDate.of(2020, 6, 1);
        LocalDate wrongUpperBound = LocalDate.of(2014, 1, 10);
        r.addQueryDateRange("baz", wrongLowerBound, wrongUpperBound);
        URL url = r.constructURL();
        URL expected = new URL("https://api.reach.talkylabs.com/v1/foobar?baz>=2014-01-10&baz<=2020-06-01");
        assertNotEquals(expected, url);
    }

    @Test
    public void testAddQueryDateTimeRangeLowerBound() throws MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/v1/foobar");
        r.addQueryDateTimeRange("baz", ZonedDateTime.of(2014, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC), null);
        URL url = r.constructURL();
        URL expected = new URL("https://api.reach.talkylabs.com/v1/foobar?baz>=2014-01-01T00:00:00");
        assertUrlsEqual(expected, url);
    }

    @Test
    public void testAddQueryDateTimeRangeUpperBound() throws MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/v1/foobar");
        r.addQueryDateTimeRange("baz", null, ZonedDateTime.of(2014, 1, 1, 22, 0, 0, 0, ZoneOffset.UTC));
        URL url = r.constructURL();
        URL expected = new URL("https://api.reach.talkylabs.com/v1/foobar?baz<=2014-01-01T22:00:00");
        assertUrlsEqual(expected, url);
    }

    @Test
    public void testAddQueryDateTimeRangeClosed() throws MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/v1/foobar");
        r.addQueryDateTimeRange("baz", ZonedDateTime.of(2014, 1, 10, 14, 0, 0, 0, ZoneOffset.UTC),
            ZonedDateTime.of(2014, 6, 1, 16, 0, 0, 0, ZoneOffset.UTC));
        URL url = r.constructURL();
        URL expected = new URL("https://api.reach.talkylabs.com/v1/foobar?baz>=2014-01-10T14:00:00&baz<=2014-06-01T16:00:00");
        assertUrlsEqual(expected, url);
    }

    @Test
    public void testAddQueryDateTimeRangeClosedNotUTC() throws MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/v1/foobar");
        ZoneId z = ZoneId.of("America/Chicago");
        ZonedDateTime begin = ZonedDateTime.of(2014, 1, 10, 14, 0, 0, 0, z);
        ZonedDateTime end = ZonedDateTime.of(2014, 6, 1, 16, 0, 0, 0, z);
        r.addQueryDateTimeRange("baz", begin, end);
        URL url = r.constructURL();
        URL expected = new URL("https://api.reach.talkylabs.com/v1/foobar?baz>=2014-01-10T20:00:00&baz<=2014-06-01T21:00:00");
        assertUrlsEqual(expected, url);
    }

    @Test
    public void testAddQueryDateTimeRangeMismatchedBounds() throws MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/v1/foobar");
        ZonedDateTime wrongLowerBound = ZonedDateTime.of(2020, 6, 1, 16, 0, 0, 0, ZoneOffset.UTC);
        ZonedDateTime wrongUpperBound = ZonedDateTime.of(2014, 1, 10, 14, 0, 0, 0, ZoneOffset.UTC);
        r.addQueryDateTimeRange("baz", wrongLowerBound, wrongUpperBound);
        URL url = r.constructURL();
        URL expected = new URL("https://api.reach.talkylabs.com/v1/foobar?baz>=2014-01-10T14:00:00&baz<=2020-06-01T16:00:00");
        assertNotEquals(expected, url);
    }

    @Test
    public void testNoEdgeOrRegionInUrl() throws MalformedURLException {
        final Request request = new Request(HttpMethod.GET, "https://api.reach.talkylabs.com");

        assertUrlsEqual(new URL("https://api.reach.talkylabs.com"), request.constructURL());

    }

    @Test
    public void testRegionInUrl() throws MalformedURLException {
        final Request request = new Request(HttpMethod.GET, "https://api.urlRegion.reach.talkylabs.com");

        assertUrlsEqual(new URL("https://api.urlRegion.reach.talkylabs.com"), request.constructURL());

    }

    @Test
    public void testRegionAndEdgeInUrl() throws MalformedURLException {
        final Request request = new Request(HttpMethod.GET, "https://api.urlEdge.urlRegion.reach.talkylabs.com");

        assertUrlsEqual(new URL("https://api.urlEdge.urlRegion.reach.talkylabs.com"), request.constructURL());

    }


    @Test
    public void testEncodeFormBody() {
        Request r = new Request(HttpMethod.POST, "http://example.com/foobar");
        r.addPostParam("baz", "quux");
        r.addPostParam("garply", "xyzzy");
        String encoded = r.encodeFormBody();
        assertQueryStringsEqual("baz=quux&garply=xyzzy", encoded);
    }

    @Test
    public void testGetPassword() {
        Request request = new Request(HttpMethod.DELETE, "/uri");
        request.setAuth("username", "password");
        assertEquals("password", request.getPassword());
    }

    @Test
    public void testGetUsername() {
        Request request = new Request(HttpMethod.DELETE, "/uri");
        request.setAuth("username", "password");
        assertEquals("username", request.getUsername());
    }

    @Test
    public void testRequiresAuthentication() {
        Request request = new Request(HttpMethod.DELETE, "/uri");
        assertFalse(request.requiresAuthentication());
        request.setAuth("username", "password");
        assertTrue(request.requiresAuthentication());
    }

    @Test
    public void testEquals() {
        Request request = new Request(HttpMethod.DELETE, "/uri");
        request.setAuth("username", "password");
        assertNotEquals(request, new Object());
        assertNotEquals(null, request);
    }
}
