import java.net.*;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
public class Handler implements Runnable{
    BufferedReader toClient;
    BufferedWriter fromClient;
    Socket socket;
    public Handler (Socket socket) {
        this.socket = socket;
        try{
            this.socket.setSoTimeout(2000);
            toClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            fromClient = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
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
            request = toClient.readLine();
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
            dout.writeBytes("GET /test HTTP/1.0\r\n\r\n");
            dout.writeBytes("GET / HTTP/1.0\r\n\r\n");
            // InputStream netIn = socket.getInputStream();
            DataInputStream netIn = new DataInputStream(input);
            OutputStream clientOut = System.out;
            // to read from the Web server
            // to write to the Web client
            // Initialize netIn, clientOut
            byte [] buffer = new byte[4096];
            int bytes_read;
            while ((bytes_read = netIn.read(buffer)) != -1)
            {
            clientOut.write(buffer, 0, bytes_read);
            clientOut.flush();
            }
            // PrintWriter writer = new PrintWriter(output, true);
            // writer.println(message);

            // System.out.println("Sent message to server: " + message);
        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());      
    } catch (IOException ex) {
        System.out.println("I/O error : "+ex.getMessage());
    }
    }
    private void sendToClient(String urlString) {
        try{
			
			// Compute a logical file name as per schema
			// This allows the files on stored on disk to resemble that of the URL it was taken from
			int fileExtensionIndex = urlString.lastIndexOf(".");
			String fileExtension;

			// Get the type of file
			fileExtension = urlString.substring(fileExtensionIndex, urlString.length());

			// Get the initial file name
			String fileName = urlString.substring(0,fileExtensionIndex);


			// Trim off http://www. as no need for it in file name
			fileName = fileName.substring(fileName.indexOf('.')+1);

			// Remove any illegal characters from file name
			fileName = fileName.replace("/", "__");
			fileName = fileName.replace('.','_');
			
			// Trailing / result in index.html of that directory being fetched
			if(fileExtension.contains("/")){
				fileExtension = fileExtension.replace("/", "__");
				fileExtension = fileExtension.replace('.','_');
				fileExtension += ".html";
			}
		
			fileName = fileName + fileExtension;



			// Attempt to create File to cache to
			boolean caching = true;
			File fileToCache = null;
			BufferedWriter fileToCacheBW = null;

			try{
				// Create File to cache 
				fileToCache = new File("cached/" + fileName);

				if(!fileToCache.exists()){
					fileToCache.createNewFile();
				}

				// Create Buffered output stream to write to cached copy of file
				fileToCacheBW = new BufferedWriter(new FileWriter(fileToCache));
			}
			catch (IOException e){
				System.out.println("Couldn't cache: " + fileName);
				caching = false;
				e.printStackTrace();
			} catch (NullPointerException e) {
				System.out.println("NPE opening file");
			}





			// Check if file is an image
			if((fileExtension.contains(".png")) || fileExtension.contains(".jpg") ||
					fileExtension.contains(".jpeg") || fileExtension.contains(".gif")){
				// Create the URL
				URL remoteURL = new URL(urlString);
				BufferedImage image = ImageIO.read(remoteURL);

				if(image != null) {
					// Cache the image to disk
					ImageIO.write(image, fileExtension.substring(1), fileToCache);

					// Send response code to client
					String line = "HTTP/1.0 200 OK\n" +
							"Proxy-agent: ProxyServer/1.0\n" +
							"\r\n";
					fromClient.write(line);  //is it really from Client though?
					fromClient.flush();

					// Send them the image data
					ImageIO.write(image, fileExtension.substring(1), socket.getOutputStream());

				// No image received from remote server
				} else {
					System.out.println("Sending 404 to client as image wasn't received from server"
							+ fileName);
					String error = "HTTP/1.0 404 NOT FOUND\n" +
							"Proxy-agent: ProxyServer/1.0\n" +
							"\r\n";
					fromClient.write(error);
					fromClient.flush();
					return;
				}
			} 

			// File is a text file
			else {
								
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
				fromClient.write(line);
				

                //Remove lines from this point forward
				
				// Read from input stream between proxy and remote server
				while((line = proxyToServerBR.readLine()) != null){
					// Send on data to client
					fromClient.write(line);

					// Write to our cached copy of the file
					if(caching){
						fileToCacheBW.write(line);
					}
				}
				
				// Ensure all data is sent by this point
				fromClient.flush();

				// Close Down Resources
				if(proxyToServerBR != null){
					proxyToServerBR.close();
				}
			}


			// if(caching){
			// 	// Ensure data written and add to our cached hash maps
			// 	fileToCacheBW.flush();
			// 	Proxy.addCachedPage(urlString, fileToCache);
			// }

			// Close down resources
			if(fileToCacheBW != null){
				fileToCacheBW.close();
			}

			if(fromClient != null){
				fromClient.close();
			}
		} 

		catch (Exception e){
			e.printStackTrace();
		}

    }
    
}
