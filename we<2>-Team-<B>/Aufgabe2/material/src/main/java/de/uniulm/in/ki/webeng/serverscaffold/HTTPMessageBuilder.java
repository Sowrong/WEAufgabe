package de.uniulm.in.ki.webeng.serverscaffold;

import de.uniulm.in.ki.webeng.serverscaffold.model.Request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Assembles a request byte by byte
 *
 * Created by Markus Brenner on 07.09.2016.
 * Updated by Surong Li on 27.10.2019.
 */

public class HTTPMessageBuilder {
    final int httpMessageBufferSize = 8192;
    byte[] httpMessageData;
    int httpMessageLength;
    int httpBodyLength;

    boolean requestComplete;

    String requestMethod;
    String requestResource;
    String requestProtocol;
    int requestBodyStartIndex;
    Map<String, String> requestHeaders;
    byte[] requestBody;

    HTTPMessageBuilder() {
        httpMessageData = new byte[httpMessageBufferSize];
        httpMessageLength = 0;
        httpBodyLength = 0;
        requestComplete = false;
    }

    private void createHeader() {
        requestHeaders = new HashMap<String, String>();

        String headString = new String(httpMessageData,0, httpMessageLength);

        String contentLines[] = headString.split("\\r\\n");

        System.out.println(headString);

        // process first line
        String splitLine[] = contentLines[0].split(" ");

        if (splitLine.length == 3) {
            requestMethod = splitLine[0].trim();
            requestResource = splitLine[1].trim();
            requestProtocol = splitLine[2].trim();
        }

        for (int lineIndex = 1; lineIndex < contentLines.length; lineIndex++) {
            splitLine = contentLines[lineIndex].split(":");
            if (splitLine.length == 2) {
                requestHeaders.put(splitLine[0].trim().toLowerCase(), splitLine[1].trim());
            }
        }
    }

    /**
     * Appends a character to the current request.
     *
     * @param c
     *            The next character
     * @return True, if the addition of the provided byte has completed the
     *         request
     */
    public boolean append(byte c) {
        httpMessageData[httpMessageLength++] = c;

        if (httpMessageLength > 4) {

            // already finished with header now processing body
            if (httpBodyLength > 0) {
                httpBodyLength--;

                if (httpBodyLength == 0) {
                    requestBody = Arrays.copyOfRange(httpMessageData, requestBodyStartIndex, httpMessageLength);
                    requestComplete = true;
                    return true;
                }
                else {
                    return false;
                }
            }

            // check if header is finished
            else {
                if ( (char)httpMessageData[httpMessageLength-4] == '\r' &&
                     (char)httpMessageData[httpMessageLength-3] == '\n' &&
                     (char)httpMessageData[httpMessageLength-2] == '\r' &&
                     (char)httpMessageData[httpMessageLength-1] == '\n'
                ) {
                    createHeader();

                    if (requestHeaders.containsKey("content-length")) {
                        httpBodyLength = Integer.valueOf(requestHeaders.get("content-length"));
                        if (httpBodyLength > 0) {
                            requestBodyStartIndex = httpMessageLength;
                            return false;
                        }
                        else {
                            requestBody = new byte[0];
                            requestComplete = true;
                            return true;
                        }
                    }

                    else {
                        requestBody = new byte[0];
                        requestComplete = true;
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Obtains the assembled request
     * 
     * @return The assembled request or null, if the request has not been
     *         completed yet
     */
    public Request getRequest() {
        if (requestComplete == true) {
            httpMessageLength = 0;
            httpBodyLength = 0;
            requestComplete = false;

            return new Request(requestMethod, requestResource, requestProtocol, requestHeaders, requestBody);
        }
        else {
            return null;
        }
    }
}
