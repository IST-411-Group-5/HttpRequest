package app.main;

import java.io.File;
import java.net.Socket;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/**
 * Client handler.
 * 
 * @author Erik Galloway <erik@fliplearning.com>
 * @author Miao Yu
 */
public class ClientHandler implements Runnable
{

    private final Socket socket;
    protected File file;

    /**
     * ClientHandler constructor.
     *
     * @param Socket socket
     * @return void
     */
    public ClientHandler(Socket socket)
    {
        this.socket = socket;
        this.file = new File("src/app/files/server/ServerDiaryCopy.txt");
    }

    /**
     * Make a class runnable.
     *
     * @Override
     * @return void
     */
    public void run()
    {
        System.out.println("\nClientHandler Started for "
                + this.socket);
        handleRequest(this.socket);
        System.out.println("ClientHandler Terminated for "
                + this.socket + "\n");
    }

    /**
     * Handle a HTTP request.
     *
     * @param Socket socket
     * @return void
     */
    public void handleRequest(Socket socket)
    {
    
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            
            StringTokenizer tokenizer = new StringTokenizer(in.readLine());

            String httpMethod = tokenizer.nextToken();

            if (httpMethod.equals("GET")) {

                System.out.println("Get method processed");
                
                String httpQueryString = tokenizer.nextToken();
                
                StringBuilder responseBuffer = new StringBuilder();
                
                responseBuffer.append("<html><h1>WebServer Home Page.... </h1><br>")
                    .append("<b>Welcome to my web server!</b><BR>")
                    .append("<b>Diary: ")
                    .append(readFromDiary())
                    .append("</b><BR>")
                    .append("</html>");
                
                this.sendResponse(socket, 200, responseBuffer.toString());

            } else if (httpMethod.equals("POST")) {

                System.out.println("Post method received");
                
                String httpQueryString = tokenizer.nextToken();
                String userAgent = in.readLine();
                String requestData = in.readLine();
                this.writeToDiary(requestData);
                
                StringBuilder responseBuffer = new StringBuilder();
                
                responseBuffer
                        .append("<html><h1>Successful Write to Server</h1><br>")
                        .append("<b>Thank you for updating the server!</b><BR>")
                        .append("<b>Updated Diary: ")
                        .append(this.readFromDiary())
                        .append("</b><BR>")
                        .append("</html>");
                
                this.sendResponse(socket, 200, responseBuffer.toString());
            }    
            else {
                System.out.println("The HTTP method is not recognized");
                sendResponse(socket, 405, "Method Not Allowed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Read the text from the diary file.
     *
     * @return String
     */
    public String readFromDiary()
    {
        
        String message = "";
        String readLine = "";

        try {

            BufferedReader in = new BufferedReader(new FileReader(this.file));

            System.out.println("Reading file using Buffered Reader");

            while ((readLine = in.readLine()) != null) {
                System.out.println(readLine);
                message += readLine + "/n";
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return message;
    }
    
    /**
     * Send the HTTP response.
     *
     * @param Socket socket
     * @param int statusCode
     * @param String responseString
     */
    public void sendResponse(Socket socket,
            int statusCode, String responseString)
    {
        String statusLine;
        String serverHeader = "Server: WebServer\r\n";
        String contentTypeHeader = "Content‐Type: text/html\r\n";
        try (DataOutputStream out
                = new DataOutputStream(socket.getOutputStream());) {
            
            if (statusCode == 200) {
                statusLine = "HTTP/1.0 200 OK" + "\r\n";
                String contentLengthHeader = "Content‐Length: "
                        + responseString.length() + "\r\n";
                out.writeBytes(statusLine);
                out.writeBytes(serverHeader);
                out.writeBytes(contentTypeHeader);
                out.writeBytes(contentLengthHeader);
                out.writeBytes("\r\n");
                out.writeBytes(responseString);
            } else if (statusCode == 405) {
                statusLine = "HTTP/1.0 405 Method Not Allowed" + "\r\n";
                out.writeBytes(statusLine);
                out.writeBytes("\r\n");
            } else {
                statusLine = "HTTP/1.0 404 Not Found" + "\r\n";
                out.writeBytes(statusLine);
                out.writeBytes("\r\n");
            }
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Wrtie the content to the diary file.
     *
     * @param String content
     * @return void
     */
    protected void writeToDiary(String content)
    {
        try (FileWriter fw = new FileWriter("src/app/files/server/ServerDiaryCopy.txt", true)) {
            fw.write(content + "\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
