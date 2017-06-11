package app.main;

import java.net.Socket;
import java.io.IOException;
import java.net.InetAddress;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Http client.
 *
 * @author  Erik Galloway <erik@fliplearning.com>
 * @author Miao Yu
 */
public class HTTPClient
{

    protected InetAddress address;

    protected Socket connection;

    /**
     * HTTPClient constructor.
     *
     * @return void
     */
    public HTTPClient()
    {
        
        System.out.println("HTTP Client Started");

        try {

            this.address = InetAddress.getByName("127.0.0.1");
            this.connection = new Socket(this.address, 8080);
            OutputStream out = this.connection.getOutputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(this.connection.getInputStream()));
            this.sendPostRequest(out);
            System.out.println(getResponse(in) + "\n");

            this.connection = new Socket(this.address, 8080);
            OutputStream output = this.connection.getOutputStream();
            BufferedReader input = new BufferedReader(new InputStreamReader(this.connection.getInputStream()));
            this.sendGet(output);
            System.out.println(getResponse(input) + "\n");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Send a get request to the server.
     *
     * @param OutputStream out
     */
    private void sendGet(OutputStream out)
    {

        try {
            out.write("GET /default\r\n".getBytes());
            out.write("User‐Agent: Mozilla/5.0\r\n".getBytes());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Get the HTTP response.
     *
     * @param BufferedReader in
     * @return String
     */
    private String getResponse(BufferedReader in)
    {
        try {
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine).append("\n");
            }
            return response.toString();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return "";
    }

    /**
     * Send a post request to the server.
     *
     * @param OutputStream out
     * @return void
     */
    private void sendPostRequest(OutputStream out)
    {

        try {
            out.write("POST /default\r\n".getBytes());
            out.write("User-Agent: Mozilla/5.0\r\n".getBytes());
            out.write("This is another diary entry.\r\n".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Run the http client.
     *
     * @param args
     * @return void
     */
    public static void main(String[] args)
    {
        new HTTPClient();
    }
}
