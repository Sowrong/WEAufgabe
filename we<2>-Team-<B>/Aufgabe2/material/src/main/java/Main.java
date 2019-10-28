import de.uniulm.in.ki.webeng.serverscaffold.ServerThread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Main entry point Created by Markus Brenner on 07.09.2016.
 */
class Main {
    public static void main(String[] args) throws IOException {
        // start a new server
        ServerThread server = new ServerThread();
        new Thread(server).start();
        // wait for user input signaling the termination of the program
        BufferedReader br = new BufferedReader(
                new InputStreamReader(System.in));
        String s;
        while (true) {
            s = br.readLine();
            if (s.equals("exit")) {
                break;
            }
        }
        System.out.println("Exiting...");
        server.terminate();
    }
}
