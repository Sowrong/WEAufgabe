import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import de.uniulm.in.ki.webeng.serverscaffold.ServerConfiguration;
import de.uniulm.in.ki.webeng.serverscaffold.ServerThread;

public class TestHTTPMessageBuilder {

    public static final String HOST_NAME = "localhost";

    public static void main(String[] args) throws InterruptedException {
        ServerThread server = new ServerThread();
        new Thread(server).start();
        test1();
        test2();
        test3();
        test4();
        server.terminate();
    }

    private static void test1() throws InterruptedException {
        String method = "GET / HTTP/1.1\r\n";
        test(method, null, null);
    }

    private static void test2() throws InterruptedException {
        String method = "GET /favicon.ico HTTP/1.1\r\n";
        test(method, null, null);
    }

    private static void test3() throws InterruptedException {
        String method = "HEAD / HTTP/1.1\r\n";
        test(method, null, null);
    }

    private static void test4() throws InterruptedException {
        String method = "POST /index.html HTTP/1.1\r\n";
        String header = "Referer:http://localhost:"+ServerConfiguration.port+"/\r\n"
                        + "Content-Type:application/x-www-form-urlencoded\r\n";
        String body = "name=Birte+Glimm";
        test(method, header, body);
    }

    private static void test(String method, String header, String body) throws InterruptedException {
        try (
                Socket serverSocket = new Socket(HOST_NAME, ServerConfiguration.port);
                PrintWriter out =
                        new PrintWriter(serverSocket.getOutputStream(), true);
                ) {
            out.print(method);
            // send standard headers
            out.print("Accept:text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\r\n");
            out.print("Upgrade-Insecure-Requests:1\r\n");
            out.print("User-Agent:Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:48.0) Gecko/20100101 Firefox/48.0\r\n");
            out.print("Connection:keep-alive\r\n");
            out.print("Host:localhost:"+ServerConfiguration.port+"\r\n");
            out.print("Accept-Language:en-US,en;q=0.5\r\n");
            out.print("Accept-Encoding:gzip, deflate\r\n");
            // send test specific headers
            if (header != null)
                out.print(header);
            if (body != null)
                out.print("Content-Length:" + body.length() + "\r\n");
            out.print("\r\n");
            if (body != null)
                out.print(body);
            out.flush();
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + HOST_NAME);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    HOST_NAME);
            System.exit(1);
        }
        System.out.println();
        System.out.flush();
        Thread.sleep(2000); // wait 2 s to sort out output
    }
}
