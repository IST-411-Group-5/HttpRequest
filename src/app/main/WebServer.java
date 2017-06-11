package app.main;

import java.net.Socket;
import java.io.IOException;
import java.net.ServerSocket;

/**
 * Web server.
 *
 * @author Erik Galloway <erik@fliplearning.com>
 * @author Miao Yu
 */
public class WebServer
{

    /**
     * Webserver constructor.
     *
     * @return void
     */
    public WebServer()
    {

        System.out.println("Webserver Started");

        try (ServerSocket serverSocket = new ServerSocket(8080)) {
        
            while (true) {
                System.out.println("Waiting for client request");
                Socket remote = serverSocket.accept();
                System.out.println("Connection made");
                new Thread(new ClientHandler(remote)).start();
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Run the webserver.
     *
     * @param String[] args
     * @return void
     */
    public static void main(String[] args)
    {
        new WebServer();
    }
}
