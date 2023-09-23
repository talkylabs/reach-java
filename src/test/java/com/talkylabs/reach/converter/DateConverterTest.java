package com.talkylabs.reach.converter;

import java.time.ZonedDateTime;
import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Locale;

/**
 * Test Class for {@link DateConverter}.
 */
public class DateConverterTest {

    @Test
    public void testRfc2822() {
        ZonedDateTime dt = DateConverter.rfc2822DateTimeFromString("Tue, 29 Mar 2016 13:00:05 +0000");

        Assertions.assertEquals(2, dt.getDayOfWeek().getValue());
        Assertions.assertEquals(29, dt.getDayOfMonth());
        Assertions.assertEquals(3, dt.getMonthValue());
        Assertions.assertEquals(2016, dt.getYear());
        Assertions.assertEquals(13, dt.getHour());
        Assertions.assertEquals(0, dt.getMinute());
        Assertions.assertEquals(5, dt.getSecond());
    }

    @Test
    public void testInvalidRfc2822() {
        ZonedDateTime dt = DateConverter.rfc2822DateTimeFromString("gibberish");
        Assertions.assertNull(dt);
    }

    @Test
    public void testIso8601() {
        ZonedDateTime dt = DateConverter.iso8601DateTimeFromString("2016-01-15T21:49:24Z");

        Assertions.assertEquals(15, dt.getDayOfMonth());
        Assertions.assertEquals(1, dt.getMonthValue());
        Assertions.assertEquals(2016, dt.getYear());
        Assertions.assertEquals(21, dt.getHour());
        Assertions.assertEquals(49, dt.getMinute());
        Assertions.assertEquals(24, dt.getSecond());
    }
    
    @Test
    public void testIso8601Bis() {
        ZonedDateTime dt = DateConverter.iso8601DateTimeFromString("2016-01-15T21:49:24.000Z");

        Assertions.assertEquals(15, dt.getDayOfMonth());
        Assertions.assertEquals(1, dt.getMonthValue());
        Assertions.assertEquals(2016, dt.getYear());
        Assertions.assertEquals(21, dt.getHour());
        Assertions.assertEquals(49, dt.getMinute());
        Assertions.assertEquals(24, dt.getSecond());
    }

    @Test
    public void testInvalidIso8601() {
        ZonedDateTime dt = DateConverter.iso8601DateTimeFromString("blanks");
        Assertions.assertNull(dt);
    }

    @Test
    public void testLocalDate() {
        LocalDate ld = DateConverter.localDateFromString("2016-11-11");

        Assertions.assertEquals(2016, ld.getYear());
        Assertions.assertEquals(11, ld.getMonthValue());
        Assertions.assertEquals(11, ld.getDayOfMonth());
    }

    @Test
    public void testInvalidLocalDate() {
        LocalDate date = DateConverter.localDateFromString("bad");
        Assertions.assertNull(date);
    }

    @Test
    public void testLocalDateToString() {
        String date = DateConverter.dateStringFromLocalDate(LocalDate.of(2016, 9, 21));
        Assertions.assertEquals("2016-09-21", date);
    }

    @Test
    public void testDifferentLocaleRFC2822() {
        Locale.setDefault(new Locale("fr", "CA"));
        ZonedDateTime dateTime = DateConverter.rfc2822DateTimeFromString("Tue, 29 Mar 2016 13:00:05 +0000");
        Assertions.assertNotNull(dateTime);
    }

    @Test
    public void testDifferentLocaleISO8601() {
        Locale.setDefault(new Locale("fr", "CA"));
        ZonedDateTime dateTime = DateConverter.iso8601DateTimeFromString("2016-01-15T21:49:24Z");
        Assertions.assertNotNull(dateTime);
    }

    @Test
    public void testISO8601DateTimeConversion() {
        String dateTimeString = "2016-01-15T21:49:00Z";
        ZonedDateTime dateTime = DateConverter.iso8601DateTimeFromString(dateTimeString);
        Assertions.assertEquals(dateTimeString, dateTime.toInstant().toString());
    }

}
