package com.talkylabs.reach.converter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Collections;

/**
 * Test class for {@link Promoter}
 */
public class PromoterTest {

    @Test
    public void testPromoteUri() {
        URI uri = Promoter.uriFromString("https://api.reach.talkylabs.com/v1/Trunks/TK123/OriginationUrls");
        Assertions.assertEquals(
            "https://api.reach.talkylabs.com/v1/Trunks/TK123/OriginationUrls",
            uri.toString()
        );
    }

    
    @Test
    public void testPromoteList() {
        String s = "hi";
        Assertions.assertEquals(
            Collections.singletonList(s),
            Promoter.listOfOne(s)
        );
    }
}
