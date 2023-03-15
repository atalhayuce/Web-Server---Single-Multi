
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

public class WebServerSingle {
	final static String CRLF = "\r\n";

	public final static void main(String argv[]) throws Exception {
		// Establish the listen socket.
		ServerSocket serverSocket = null;

		try {
			serverSocket = new ServerSocket(4040);
		} catch (IOException e) {
			System.err.println("Could not listen on port: 4040.");
			System.exit(1);
		}

		System.out.println("Web Server initialized and waiting for client connection on <4040>");

		// Wait for client connection
		// When a client connects, make the link and carry on
		Socket clientSocket = null;

		try {
			clientSocket = serverSocket.accept();
		} catch (IOException e) {
			System.err.println("Accept failed.");
			System.exit(1);
		}

		// Get a reference to the socket's input and output streams
		InputStream inputStream = clientSocket.getInputStream();
		DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());

		// Create buffers
		BufferedReader bufferReader = new BufferedReader(new InputStreamReader(inputStream));

		// Get the HTTP request line
		String requestLine = bufferReader.readLine();

		// Extract the filename from the request line
		StringTokenizer tokens = new StringTokenizer(requestLine);
		tokens.nextToken(); // skip over the method, which should be "GET"
		String fileName = tokens.nextToken();

		// Prepend a "." so that file request is within the current directory.
		// My HTML file in under src folder with my java class.
		fileName = "src/" + fileName;

		// Open the requested file
		FileInputStream fileInputStream = null;
		boolean fileExists = true;



		try {
			fileInputStream = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			fileExists = false;
		}
		System.out.println("File var mi?" + fileExists);

		// Get file type
		String contentType;
		if (fileName.endsWith(".htm") || fileName.endsWith(".html")) {
			contentType = "text/html";
		} else {
			contentType = "application/octet-stream";
		}

		// Construct the response message
		String statusLine = null;
		String contentTypeLine = null;
		String messageBody = null;

		// Construct the HTTP header lines
		if (fileExists) {
			statusLine = "HTTP/1.1 200 OK" + CRLF;
			contentTypeLine = "Content-Type: " + contentType + CRLF;
		} else {
			statusLine = "HTTP/1.1 404 Not Found" + CRLF;
			contentTypeLine = "Content-Type: text/html" + CRLF;
			messageBody = "<HTML>" + "<HEAD><TITLE>Not Found</TITLE></HEAD>" + "<BODY>Not Found</BODY></HTML>";
		}

		// Send the response message to the client

		// Send the status line
		outputStream.writeBytes(statusLine);

		// Send the content type line
		outputStream.writeBytes(contentTypeLine);

		// Send a blank line to indicate the end of the header lines
		outputStream.writeBytes(CRLF);

		// Send the message body
		if (fileExists) {
			byte[] buffer = new byte[1024];
			int bytesRead;

			while ((bytesRead = fileInputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}

			fileInputStream.close();
		} else {
			outputStream.writeBytes(messageBody);
		}

		// Close

//Close streams
outputStream.close();
bufferReader.close();
//Close client socket
clientSocket.close();
//Close server socket
serverSocket.close();
System.out.println("Web Server terminated");
}
}
