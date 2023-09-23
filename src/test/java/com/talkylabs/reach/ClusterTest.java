package com.talkylabs.reach;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;

import com.talkylabs.reach.base.Page;
import com.talkylabs.reach.rest.api.messaging.MessagingItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ClusterTest {
    String fromNumber;
    String toNumber;

    @BeforeEach
    public void setUp() {
        // only run when ClusterTest property is passed (mvn test -Dtest="ClusterTest"), skip test run on mvn test
    	Assumptions.assumingThat("ClusterTest".equals(System.getProperty("Test")), ()->{
	        fromNumber = System.getenv("REACH_TALKYLABS_FROM_NUMBER");
	        toNumber = System.getenv("REACH_TALKYLABS_TO_NUMBER");
	        String apiKey = System.getenv("REACH_TALKYLABS_API_KEY");
	        String apiUser = System.getenv("REACH_TALKYLABS_API_USER");
	        Reach.init(apiUser, apiKey);
    	});
    }

    @Test
    public void testSendingAText() {
    	Assumptions.assumingThat("ClusterTest".equals(System.getProperty("Test")), ()->{
	        MessagingItem message = MessagingItem.sender(
	                toNumber, fromNumber, "Where's Wallace?")
	            .send();
	        Assertions.assertNotNull(message);
	        Assertions.assertTrue(message.getBody().contains("Where's Wallace?"));
	        Assertions.assertEquals(fromNumber, message.getSrc().toString());
	        Assertions.assertEquals(toNumber, message.getDest().toString());
    	});
    }


}
