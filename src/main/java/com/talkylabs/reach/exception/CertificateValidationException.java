package com.talkylabs.reach.exception;

import com.talkylabs.reach.http.Request;
import com.talkylabs.reach.http.Response;

public class CertificateValidationException extends ReachException {
	private static final long serialVersionUID = -1815389925556060379L;
	private final Request request;
    private final Response response;

    public CertificateValidationException(final String message, final Request request, final Response response) {
        super(message);

        this.request = request;
        this.response = response;
    }

    public CertificateValidationException(final String message, final Request request) {
        this(message, request, null);
    }

    public Request getRequest() { return this.request; }
    public Response getResponse() { return this.response; }
}
