package com.safari.traderbot.rest;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import java.net.URI;

public class CoinExHttpDelete extends HttpEntityEnclosingRequestBase {
    public static final String METHOD_NAME = "DELETE";

    public String getMethod() {
        return METHOD_NAME;
    }

    public CoinExHttpDelete(final String uri) {
        super();
        setURI(URI.create(uri));
    }

    public CoinExHttpDelete(final URI uri) {
        super();
        setURI(uri);
    }

    public CoinExHttpDelete() {
        super();
    }
}
