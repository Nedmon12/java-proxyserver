import java.net.*;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
public class Handler implements Runnable{
    BufferedReader clientReader;
    BufferedWriter clientWriter;
	DataOutputStream firefox;
    Socket socket;
    public Handler (Socket socket) {
        this.socket = socket;
        try{
            this.socket.setSoTimeout(4000);
            clientReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            clientWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			firefox = new DataOutputStream(this.socket.getOutputStream());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run () {
		String request;
        // String request = "GET http://www.aastu.edu.et/index.html HTTP/1.0\r\n";
        try{
            request = clientReader.readLine();
        }
        catch (IOException e) {
            System.out.println("error minamn : "+e.getMessage());
            return;
        }
        // Pattern pattern = Pattern.compile("GET http://.*?:(\\d+)/(.*?) HTTP/1.0\r\n");
        // // Pattern pattern = Pattern.compile("^(\\w+)\\s+http://(\\S+):(\\d+)\\s+(\\S+)$");
        // Matcher matcher = pattern.matcher(request);
        //  String host = "";
        //  int port = 80;
        // if (matcher.find()) {
        //     port = Integer.parseInt(matcher.group(1));
        //     String path = matcher.group(2);
        //     int startIndex = request.indexOf("http://") + 7;
        //     int endIndex = request.indexOf(":80/");
        //     host = request.substring(startIndex, endIndex);
        //     System.out.println("Port: " + port);
        //     System.out.println("Path: " + path);
        //     System.out.println("host : "+host);
        // } else {
        //     System.out.println("Could not parse the request.");
        // }
		String[] components = request.split("\\s+");
			String requestType = components[0];
			String requestRoute = components[1];
			String protocolVersion = components[2];

			// Extract the host from the request route
			int hostStart = requestRoute.indexOf("://") + 3;
			int hostEnd = requestRoute.indexOf("/", hostStart);
			String host = requestRoute.substring(hostStart, hostEnd);

			// Extract the request route from the request route
			int routeStart = requestRoute.indexOf("/", hostEnd);
			String route = requestRoute.substring(routeStart);

			// Print the parsed components
			System.out.println("Request Type: " + requestType);
			System.out.println("Host: " + host);
			System.out.println("Request Route: " + route);
        try (Socket socket = new Socket(host, 80)) {
            OutputStream output = socket.getOutputStream();
			System.out.println("Are you getting here?");
            InputStream input = socket.getInputStream();
            DataOutputStream dout = new DataOutputStream(output);
            // requestType+"/"+file+"HTTP/1.0\r\n";
            // dout.writeBytes("GET "+"/"+matcher.group(2)+"HTTP/1.0\r\n");
            // dout.writeBytes("GET / HTTP/1.0\r\nHost:165.232.112.241\r\n\r\n");
			// dout.writeBytes("GET "+route+"HTTP/1.0\r\n");
			// dout.writeBytes("GET "+route+"HTTP/1.0\r\n\r\n");
			dout.writeBytes("GET "+route+" HTTP/1.0\r\nHost: "+host+"\r\n\r\n");
			// System.out.println("GET "+route+"HTTP");
            
			//dout.writeBytes("GET / HTTP/1.0\r\n\r\n");
            //dout.writeBytes("GET / HTTP/1.0\r\n\r\n");
            // InputStream netIn = socket.getInputStream();
            DataInputStream netIn = new DataInputStream(input);
            OutputStream clientOut = System.out;
			// BufferedWriter clientWriter = new BufferedWriter(new OutputStreamWriter(output));
			// BufferedReader clientReader = new BufferedReader(new InputStreamReader(input));
            // to read from the Web server
            // to write to the Web client
            // Initialize netIn, clientOut
            byte [] buffer = new byte[4096];
            int bytes_read;
            while ((bytes_read = netIn.read(buffer)) != -1)
            {
            clientOut.write(buffer, 0, bytes_read);
			firefox.write(buffer, 0, bytes_read);
			firefox.flush();
            clientOut.flush();
            }
			// String line = "HTTP/1.0 200 OK\n" +
			// 			"Proxy-agent: ProxyServer/1.0\n" +
			// 			"\r\n";
			// 	clientWriter.write(line);
			// while ((line = clientReader.readLine()) != null) {
			// 	clientWriter.write(line);
			// }
			// while ((bytes_read = netIn.read(buffer)))
			// sendclientReader(request);
            // PrintWriter writer = new PrintWriter(output, true);
            // writer.println(message);

            // System.out.println("Sent message to server: " + message);
        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());      
    } catch (IOException ex) {
        System.out.println("I/O error : "+ex.getMessage());
    }
    }
	// private void meSendToFirefox(String urlsString) {
	// 	try {

	// 	}
	// 	catch () {

	// 	}
	// }
    private void sendclientReader(String urlString) {





			// Check if file is an image
			// if((fileExtension.contains(".png")) || fileExtension.contains(".jpg") ||
			// 		fileExtension.contains(".jpeg") || fileExtension.contains(".gif")){
			// 	// Create the URL
			// 	URL remoteURL = new URL(urlString);
			// 	BufferedImage image = ImageIO.read(remoteURL);

			// 	if(image != null) {
			// 		// Cache the image to disk
			// 		ImageIO.write(image, fileExtension.substring(1), fileToCache);

			// 		// Send response code to client
			// 		String line = "HTTP/1.0 200 OK\n" +
			// 				"Proxy-agent: ProxyServer/1.0\n" +
			// 				"\r\n";
			// 		clientWriter.write(line);  //is it really from Client though?
			// 		clientWriter.flush();

			// 		// Send them the image data
			// 		ImageIO.write(image, fileExtension.substring(1), socket.getOutputStream());

			// 	// No image received from remote server
			// 	} else {
			// 		System.out.println("Sending 404 to client as image wasn't received from server"
			// 				+ fileName);
			// 		String error = "HTTP/1.0 404 NOT FOUND\n" +
			// 				"Proxy-agent: ProxyServer/1.0\n" +
			// 				"\r\n";
			// 		clientWriter.write(error);
			// 		clientWriter.flush();
			// 		return;
			// 	}
			// } 

			// File is a text file
				try {
				// Create the URL
				URL remoteURL = new URL(urlString);
				// Create a connection to remote server
				HttpURLConnection proxyToServerCon = (HttpURLConnection)remoteURL.openConnection();
				proxyToServerCon.setRequestProperty("Content-Type", 
						"application/x-www-form-urlencoded");
				proxyToServerCon.setRequestProperty("Content-Language", "en-US");  
				proxyToServerCon.setUseCaches(false);
				proxyToServerCon.setDoOutput(true);
			
				// Create Buffered Reader from remote Server
				BufferedReader proxyToServerBR = new BufferedReader(new InputStreamReader(proxyToServerCon.getInputStream()));
				

				// Send success code to client
				String line = "HTTP/1.0 200 OK\n" +
						"Proxy-agent: ProxyServer/1.0\n" +
						"\r\n";
				clientWriter.write(line);
				

                //Remove lines from this point forward
				
				// Read from input stream between proxy and remote server
				while((line = proxyToServerBR.readLine()) != null){
					// Send on data to client
					clientWriter.write(line);

					// Write to our cached copy of the file
					// if(caching){
					// 	fileToCacheBW.write(line);
					// }
				}
				
				// Ensure all data is sent by this point
				clientWriter.flush();

				// Close Down Resources
				if(proxyToServerBR != null){
					proxyToServerBR.close();
				}
				if(clientWriter != null){
					clientWriter.close();
				}

			}
			catch (Exception e) {
				e.printStackTrace();
			}
			// if(caching){
			// 	// Ensure data written and add to our cached hash maps
			// 	fileToCacheBW.flush();
			// 	Proxy.addCachedPage(urlString, fileToCache);
			// }

			// Close down resources
			// if(fileToCacheBW != null){
			// 	fileToCacheBW.close();
			// }

			
		} 

		// catch (Exception e){
		// 	e.printStackTrace();
		// }

    }
    

