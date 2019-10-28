package de.uniulm.in.ki.webeng.serverscaffold.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import de.uniulm.in.ki.webeng.serverscaffold.ServerConfiguration;

/**
 * Represents the response to a http request. The response can be constructed
 * iteratively by adding headers and setting different properties
 *
 * Created by Markus Brenner on 07.09.2016.
 */
public class Response implements Serializable {
    private static final long serialVersionUID = 1L;
    private String responseCode;
    private final Map<String, String> headers;
    private byte[] body;

    /**
     * Constructs an empty response and adds certain default values
     */
    public Response() {
        headers = new HashMap<>();
        body = new byte[0];
        headers.put("content-length", "" + body.length);
        headers.put("connection", "Closed");
        responseCode = "404 Not Found";
    }

    /**
     * Sets the response code of this response
     * 
     * @param code
     *            The response code
     * @param msg
     *            The appropriate message belonging to the response code
     */
    public void setResponseCode(int code, String msg) {
        this.responseCode = code + " " + msg;
    }

    /**
     * Adds a header to the response
     * 
     * @param name
     *            The name of the header
     * @param content
     *            The value of the header
     */
    public void addHeader(String name, String content) {
        headers.put(name.toLowerCase(), content);
    }

    /**
     * Sets the body of the response
     * 
     * @param body
     *            The value of the body
     */
    public void setBody(byte[] body) {
        this.body = body;
        addHeader("content-length", "" + body.length);
    }

    public void setBody(String body) {
        setBody(body.getBytes());
    }

    public void setBody(Path file) throws IOException {
        if (Files.isDirectory(file)) {
            String s = "<html><head><title>Content</title></head><body><h1>Content of "
                    + ServerConfiguration.webRoot.relativize(file)
                    + ":</h1><ul>";
            try (DirectoryStream<Path> stream = Files
                    .newDirectoryStream(file)) {
                for (Path p : stream) {
                    s += "<li><a href=\"" + file.relativize(p) + "\">"
                            + p.getFileName() + "</a></li>";
                }
            } catch (IOException | DirectoryIteratorException x) {
                // IOException can never be thrown by the iteration.
                // In this snippet, it can only be thrown by newDirectoryStream.
                System.err.println(x);
            }
            s += "</ul></body></html>";
            setBody(s.getBytes());
        } else {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            Files.copy(file, bos);
            setBody(bos.toByteArray());
        }
    }

    /**
     * Sets the content of this response to the content of the provided response
     * 
     * @param other
     *            The response to be copied
     */
    public void setTo(Response other) {
        if (other != null) {
            this.responseCode = other.responseCode;
            this.body = other.body;
            this.headers.clear();
            for (Map.Entry<String, String> e : other.headers.entrySet()) {
                this.headers.put(e.getKey().toLowerCase(), e.getValue());
            }
        }
    }

    /**
     * Determines the content length for the current body of the response
     * 
     * @return The content length for the response body
     */
    public int contentLength() {
        return this.body.length;
    }

    @Override
    public String toString() {
        byte[] b = toBytes();
        return new String(toBytes(), 0, b.length);// -
                                                  // Integer.parseInt(headers.getOrDefault("Content-Length",
                                                  // "0"))) + new String(body);
    }

    /**
     * @return a String representation of the header
     */
    public String getHeader() {
        return "HTTP/1.1 " + responseCode + "\r\n"
                + headers.entrySet().stream()
                        .map(x -> x.getKey() + ": " + x.getValue() + "\r\n")
                        .reduce("", (x, y) -> x + y)
                + "\r\n";
    }

    /**
     * Obtains a byte representation of this response
     * 
     * @return This request as bytes
     */
    public byte[] toBytes() {
        byte[] head = ("HTTP/1.1 " + responseCode + "\r\n"
                + headers.entrySet().stream()
                        .map(x -> x.getKey() + ": " + x.getValue() + "\r\n")
                        .reduce("", (x, y) -> x + y)
                + "\r\n").getBytes();
        byte[] res = new byte[head.length + body.length];
        System.arraycopy(head, 0, res, 0, head.length);
        System.arraycopy(body, 0, res, head.length, body.length);
        return res;
    }

    /**
     * Provides the response code of this response
     * 
     * @return The response code of this response
     */
    public String getResponseCode() {
        return responseCode;
    }

    /**
     * Provides access to the body of this response
     * 
     * @return The body of this response
     */
    public byte[] getBody() {
        return body;
    }
}
