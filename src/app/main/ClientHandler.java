/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.main;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.StringTokenizer;

/**
 *
 * @author Miao Yu
 */
public class ClientHandler implements Runnable {

    private final Socket socket;
    File fileName = new File("src/app/files/server/ServerDiaryCopy.txt");

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @
            Override
    public void run() {
        System.out.println("\nClientHandler Started for "
                + this.socket);
        handleRequest(this.socket);
        System.out.println("ClientHandler Terminated for "
                + this.socket + "\n");
    }

    public void handleRequest(Socket socket) {
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));) {
            String headerLine = in.readLine();
            StringTokenizer tokenizer
                    = new StringTokenizer(headerLine);
            String httpMethod = tokenizer.nextToken();
            if (httpMethod.equals("GET")) {
                System.out.println("Get method processed");
                String httpQueryString = tokenizer.nextToken();
                StringBuilder responseBuffer = new StringBuilder();
                responseBuffer
                        .append("<html><h1>WebServer Home Page.... </h1><br>")
                        .append("<b>Welcome to my web server!</b><BR>")
                        .append("<b>Diary: ")
                        .append(readFromDiary())
                        .append("</b><BR>")
                        .append("</html>");
                sendResponse(socket, 200, responseBuffer.toString());
            } 
            if (httpMethod.equals("POST")){
                System.out.println("Post method received");
                String httpQueryString = tokenizer.nextToken();
                writeToDiary(httpQueryString);
                StringBuilder responseBuffer = new StringBuilder();
                responseBuffer
                        .append("<html><h1>Successful Write to Server</h1><br>")
                        .append("<b>Thank you for updating the server!</b><BR>")
                        .append("<b>Updated Diary: ")
                        .append(readFromDiary())
                        .append("</b><BR>")
                        .append("</html>");
                sendResponse(socket, 201, responseBuffer.toString());
            }    
            else {
                System.out.println("The HTTP method is not recognized");
                sendResponse(socket, 405, "Method Not Allowed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Reads from diary.txt, file specified above
    public String readFromDiary() {
        
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            String readLine = "";
            String message = "";
            System.out.println("Reading file using Buffered Reader");
            while ((readLine = in.readLine()) != null) {
                System.out.println(readLine);
                message += readLine + "/n";
            }
            return message;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void sendResponse(Socket socket,
            int statusCode, String responseString) {
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
// Handle exception
        }
    }
}
