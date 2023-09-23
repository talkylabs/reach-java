package com.talkylabs.reach.base;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talkylabs.reach.exception.ApiConnectionException;
import com.talkylabs.reach.exception.ApiException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Page<T> {
    private final List<T> records;        
    private boolean  outOfPageRange;
    private int totalPages;
    private int currentPage;
    private final String url;
    private final int pageSize;

    private Page(Builder<T> b) {
        this.records = b.records;
        
        this.outOfPageRange = b.outOfPageRange;
        this.currentPage = b.currentPage;
        this.totalPages = b.totalPages;
        this.url = b.url;
        this.pageSize = b.pageSize;
    }


    public List<T> getRecords() {
        return records;
    }

    
    public int getPageSize() {
        return pageSize;
    }
    
    public int getTotalPages() {
        return totalPages;
    }
    
    public String getUrl() {
    	String query = "pageSize="+this.pageSize+"&page="+(this.currentPage);
    	try {
		final URL parsedUrl = new URL(url);
		String result = parsedUrl.getQuery() == null ? "?": (parsedUrl.getQuery().length()==0?"":"&");
		result = url + result + query;
		return result;
	} catch (MalformedURLException e) {
		throw new ApiException(e.getMessage());
	}
    }

    
    public boolean hasNextPage() {
        return !(outOfPageRange  || (currentPage + 1 >= totalPages));
    }
    

    public boolean hasPreviousPage() {
        return this.currentPage > 0;
    }
    
    public String getNextPageUrl(String domain) {
    	if(!hasNextPage()) {
    		throw new ApiException("No next page available");
    	}
    	String query = "pageSize="+this.pageSize+"&page="+(this.currentPage+1);
    	try {
			final URL parsedUrl = new URL(url);
			String result = parsedUrl.getQuery() == null ? "?": (parsedUrl.getQuery().length()==0?"":"&");
			result = url + result + query;
			return result;
		} catch (MalformedURLException e) {
			throw new ApiException(e.getMessage());
		}
    }
    
    public String getPreviousPageUrl(String domain) {
    	if(!hasPreviousPage()) {
    		throw new ApiException("No previous page available");
    	}
    	String query = "pageSize="+this.pageSize+"&page="+(this.currentPage-1);
    	try {
			final URL parsedUrl = new URL(url);
			String result = parsedUrl.getQuery() == null ? "?": (parsedUrl.getQuery().length()==0?"":"&");
			result = url + result + query;
			return result;
		} catch (MalformedURLException e) {
			throw new ApiException(e.getMessage());
		}
    }

    /**
     * Create a new page of data from a json blob.
     *
     * @param url        the url to get the page
     * @param recordKey  key which holds the records
     * @param json       json blob
     * @param recordType resource type
     * @param mapper     json parser
     * @param <T>        record class type
     * @return a page of records of type T
     */
    public static <T> Page<T> fromJson(String url, String recordKey, String json, Class<T> recordType, ObjectMapper mapper) {
        try {
            List<T> results = new ArrayList<>();
            JsonNode root = mapper.readTree(json);
            JsonNode records = root.get(recordKey);
            for (final JsonNode record : records) {
                results.add(mapper.readValue(record.toString(), recordType));
            }
            
            return buildPage(url, root, results);

           

        } catch (final IOException e) {
            throw new ApiConnectionException(
                "Unable to deserialize response: " + e.getMessage() + "\nJSON: " + json, e
            );
        }
    }

    private static <T> Page<T> buildPage(String url, JsonNode root, List<T> results) {
        Builder<T> builder = new Builder<T>().url(url);
        
        JsonNode pageSizeNode = root.get("pageSize");
        if (pageSizeNode != null && !pageSizeNode.isNull()) {
            builder.pageSize(pageSizeNode.asInt());
        } else {
            builder.pageSize(results.size());
        }
        

        JsonNode currentPageNode = root.get("page");
        if (currentPageNode != null && !currentPageNode.isNull()) {
            builder.currentPage(currentPageNode.asInt());
        } else {
            builder.currentPage(0);
        }
        
        JsonNode totalPagesNode = root.get("totalPages");
        if (totalPagesNode != null && !totalPagesNode.isNull()) {
            builder.totalPages(totalPagesNode.asInt());
        } else {
            builder.totalPages(1);
        }
        
        JsonNode outOfPageRangeNode = root.get("outOfPageRange");
        if (outOfPageRangeNode != null && !outOfPageRangeNode.isNull()) {
            builder.outOfPageRange(outOfPageRangeNode.asBoolean());
        } else {
            builder.outOfPageRange(true);
        }

        return builder.records(results).build();
    }

    

    private static class Builder<T> {
        private List<T> records;
        private boolean  outOfPageRange;
        private int totalPages;
        private int currentPage;
        private String url;
        private int pageSize;

        public Builder<T> records(List<T> records) {
            this.records = records;
            return this;
        }

        public Builder<T> currentPage(int currentPage) {
            this.currentPage = currentPage;
            return this;
        }

        public Builder<T> outOfPageRange(boolean outOfPageRange) {
            this.outOfPageRange = outOfPageRange;
            return this;
        }

        public Builder<T> totalPages(int totalPages) {
            this.totalPages = totalPages;
            return this;
        }

        public Builder<T> url(String url) {
            this.url = url;
            return this;
        }

        public Builder<T> pageSize(int pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public Page<T> build() {
            return new Page<>(this);
        }
    }
}
