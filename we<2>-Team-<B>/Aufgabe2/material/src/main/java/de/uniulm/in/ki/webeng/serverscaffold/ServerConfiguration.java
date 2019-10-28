package de.uniulm.in.ki.webeng.serverscaffold;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Stores the server configuration Created by Markus Brenner on 08.09.2016.
 */
public class ServerConfiguration {
    /**
     * Default port
     */
    public static final int port = 1339;
    /**
     * Server root
     */
    public static final Path webRoot = Paths.get("http/");
}
