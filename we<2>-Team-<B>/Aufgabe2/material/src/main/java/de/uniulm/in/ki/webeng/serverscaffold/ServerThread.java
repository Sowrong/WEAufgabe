package de.uniulm.in.ki.webeng.serverscaffold;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

/**
 * Simple, threaded http 1.1 web server Waits client connections and spawns a
 * new {@link ClientHandler} to handle the request.
 *
 * Created by Markus Brenner on 07.09.2016.
 */
public class ServerThread implements Runnable {
    private boolean terminate;
    private final List<ClientHandler> clients;

    /**
     * Creates a new ServerThread
     */
    public ServerThread() {
        this.terminate = false;
        this.clients = new LinkedList<>();
    }

    /**
     * Terminates the web server and all client threads
     */
    public void terminate() {
        this.terminate = true;
        clients.forEach(ClientHandler::terminate);
    }

    @Override
    public void run() {
        // Open a new server socket to wait for connections
        ServerSocket socket;
        try {
            socket = new ServerSocket(ServerConfiguration.port);
            // to ensure termination, we only wait 1 second for connections
            // until we retry
            socket.setSoTimeout(1000);
        } catch (IOException io) {
            System.out.println("Could not start server on port "
                    + ServerConfiguration.port + ": " + io.getMessage());
            return;
        }
        System.out.println("Server started on port " + ServerConfiguration.port
                + " with root directory "
                + ServerConfiguration.webRoot.toAbsolutePath());

        // until the server has been terminated, continue to accept connections
        while (!terminate) {
            try {
                // wait for new connections and create a new client thread
                Socket clientSocket = socket.accept();
                ClientHandler handler = new ClientHandler(clientSocket);
                clients.add(handler);
                new Thread(handler).start();
            } catch (IOException exc) {
                // timeouts trigger exceptions, we can safely ignore them
            }
        }
        try {
            socket.close();
        } catch (IOException e) {
            System.out
                    .println("Error closing server socket: " + e.getMessage());
        }
        System.out.println("Server thread terminated");
    }
}
