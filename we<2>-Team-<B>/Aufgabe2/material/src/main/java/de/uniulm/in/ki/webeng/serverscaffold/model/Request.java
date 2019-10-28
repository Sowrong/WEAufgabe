package de.uniulm.in.ki.webeng.serverscaffold.model;

import java.util.Collections;
import java.util.Map;

/**
 * Represents a http 1.1 request issued by a client. This class is immutable and
 * therefore read-only
 *
 * Created by Markus Brenner on 07.09.2016.
 */
public class Request {
    /**
     * The http method string of the request
     */
    public final String method;
    /**
     * The resource of the request
     */
    public final String resource;
    /**
     * The protocol of this request, usually HTTP/1.1
     */
    public final String protocol;
    /**
     * The headers set for the request in a property => value mapping
     */
    public final Map<String, String> headers;

    /**
     * The body of the request
     */
    public final byte[] body;

    /**
     * Constructs a new request
     * 
     * @param method
     *            The http method
     * @param resource
     *            The resource of the http request
     * @param protocol
     *            The protocol used, usually HTTP/1.1
     * @param headers
     *            The headers set in the request
     * @param body
     *            The body of the request
     */
    public Request(String method, String resource, String protocol,
            Map<String, String> headers, byte[] body) {
        this.method = method;
        this.resource = resource;
        this.protocol = protocol;
        this.headers = Collections.unmodifiableMap(headers);
        this.body = body;
    }

    @Override
    public String toString() {
        return method + " " + resource + " " + protocol + "\r\n"
                + headers.entrySet().stream()
                        .map(x -> x.getKey() + ":" + x.getValue() + "\r\n")
                        .reduce("", (x, y) -> x + y)
                + "\r\n\r\n" + new String(body);
    }
}
