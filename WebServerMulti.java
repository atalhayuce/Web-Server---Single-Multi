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

public class WebServerMulti implements Runnable {
	final static String CRLF = "\r\n";
	private Socket clientSocket;

	public WebServerMulti(Socket socket) {
		this.clientSocket = socket;
	}

	@SuppressWarnings("null")
	public static void main(String argv[]) throws Exception {
		// Establish the listen socket.
		ServerSocket serverSocket = null;
		int port = 4040; // Your desired port number

		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println("Could not listen on port: " + port);
			// Close server socket
			serverSocket.close();
			System.out.println("Web Server terminated");
			System.exit(1);
		}

		System.out.println("Web Server initialised and waiting for client connection on port " + port);

		while (true) {
			// Listen for a TCP connection request
			Socket clientSocket = serverSocket.accept();

			// Create a new thread to handle the request
			Thread thread = new Thread(new WebServerMulti(clientSocket));
			thread.start();
		}


	}

	public void run() {
		try {
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
			fileName = "." + fileName;

			// Open the requested file
			FileInputStream fileInputStream = null;
			boolean fileExists = true;

			try {
				fileInputStream = new FileInputStream("src/" + fileName);
			} catch (FileNotFoundException e) {
				fileExists = false;
			}

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
				contentTypeLine = "Content-type: " + contentType + CRLF;
			} else {
				statusLine = "HTTP/1.1 404 Not Found" + CRLF;
				contentTypeLine = "Content-type: text/html" + CRLF;
				messageBody = "<html><body>404 Not Found</body></html>";
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

			// Close streams
			outputStream.close();
			bufferReader.close();
			clientSocket.close();

		} catch (IOException e) {
			System.err.println("Error in thread: " + e);
		}
	}
}
