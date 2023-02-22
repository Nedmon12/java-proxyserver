import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class Client implements Runnable{
    private static String host;
    private ServerSocket serverSocket;
    private boolean running;
    public Client (int port) {
        new Thread(this).start();
        try {
            // Create the Server Socket for the Proxy 
            serverSocket = new ServerSocket(port);

            System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "..");
            running = true;
        } 

        // Catch exceptions associated with opening socket
        catch (SocketException se) {
            System.out.println("Socket Exception when connecting to client");
            se.printStackTrace();
        }
        catch (SocketTimeoutException ste) {
            System.out.println("Timeout occured while connecting to client");
        } 
        catch (IOException io) {
            System.out.println("IO exception when connecting to client");
        }
    }
    public static void main (String args []) {
        Client myProxy = new Client(8085);
        myProxy.listen();

}
    @Override
    public void run() {

    }
    public void listen(){
 
        while(running){

            try {
                // serverSocket.accpet() Blocks until a connection is made
                Socket socket = serverSocket.accept();
                
                // Create new Thread and pass it Runnable RequestHandler
                Thread thread = new Thread(new Handler(socket));
                
                
                thread.start();	
            } catch (SocketException e) {
                // Socket exception is triggered by management system to shut down the proxy 
                System.out.println("Server closed");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

// import java.util.regex.Matcher;
// import java.util.regex.Pattern;

// public class HTTPRequestParser {
//     public static void main(String[] args) {
//         String request = "GET http://www.merketermap.org:80/index.html HTTP/1.0\r\n";

//         Pattern pattern = Pattern.compile("GET http://.*?:(\\d+)/(.*?) HTTP/1.0\r\n");
//         Matcher matcher = pattern.matcher(request);

//         if (matcher.find()) {
//             int port = Integer.parseInt(matcher.group(1));
//             String path = matcher.group(2);

//             System.out.println("Port: " + port);
//             System.out.println("Path: " + path);
//         } else {
//             System.out.println("Could not parse the request.");
//         }
//     }
// }
