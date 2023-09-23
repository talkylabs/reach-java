package com.talkylabs.reach.base;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.talkylabs.reach.http.ReachRestClient;
import com.talkylabs.reach.rest.api.messaging.MessagingItem;
import com.talkylabs.reach.rest.api.messaging.MessagingItemReader;

import java.util.Collections;

import static org.mockito.Mockito.when;

public class ReaderTest {

    @Mock
    ReachRestClient client;

    @Mock
    Page<MessagingItem> page;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testNoPagingDefaults() {
        Reader<MessagingItem> reader = new MessagingItemReader();
        Assertions.assertNull(reader.getLimit());
        Assertions.assertNull(reader.getPageSize());
    }

    @Test
    public void testSetPageSize() {
        Reader<MessagingItem> reader = new MessagingItemReader().pageSize(100);
        Assertions.assertEquals(100, reader.getPageSize().intValue());
        Assertions.assertNull(reader.getLimit());
    }
    
    @Test
    public void testMaxPageSize() {
        Reader<MessagingItem> reader = new MessagingItemReader().pageSize(Integer.MAX_VALUE);
        Assertions.assertEquals(Integer.MAX_VALUE, reader.getPageSize().intValue());
        Assertions.assertNull(reader.getLimit());
    }

    @Test
    public void testSetLimit() {
        Reader<MessagingItem> reader = new MessagingItemReader().limit(100);
        Assertions.assertEquals(100, reader.getLimit().intValue());
        Assertions.assertEquals(100, reader.getPageSize().intValue());
    }

    @Test
    public void testSetLimitMaxPageSize() {
        Reader<MessagingItem> reader = new MessagingItemReader().limit(Integer.MAX_VALUE);
        Assertions.assertEquals(Integer.MAX_VALUE, reader.getLimit().intValue());
        Assertions.assertEquals(Integer.MAX_VALUE, reader.getPageSize().intValue());
    }

    @Test
    public void testSetPageSizeLimit() {
        Reader<MessagingItem> reader = new MessagingItemReader().limit(1000).pageSize(5);
        Assertions.assertEquals(1000, reader.getLimit().intValue());
        Assertions.assertEquals(5, reader.getPageSize().intValue());
    }

    @Test
    public void testNoPageLimit() {
        when(page.getRecords()).thenReturn(Collections.emptyList());

        Reader<MessagingItem> reader = new MessagingItemReader();
        ResourceSet<MessagingItem> set = new ResourceSet<>(reader, client, page);
        Assertions.assertEquals(Long.MAX_VALUE, set.getPageLimit());
    }


    @Test
    public void testHasPageLimit() {
        when(page.getRecords()).thenReturn(Collections.emptyList());
        when(page.getPageSize()).thenReturn(50);

        Reader<MessagingItem> reader = new MessagingItemReader().limit(100);
        ResourceSet<MessagingItem> set = new ResourceSet<>(reader, client, page);
        Assertions.assertEquals(2, set.getPageLimit());
    }
    @Test
    public void testUnevenHasPageLimit() {
        when(page.getRecords()).thenReturn(Collections.emptyList());
        when(page.getPageSize()).thenReturn(50);
        Reader<MessagingItem> reader = new MessagingItemReader().limit(125);
        ResourceSet<MessagingItem> set = new ResourceSet<>(reader, client, page);
        Assertions.assertEquals(3, set.getPageLimit());
    }

}
