# Web-Server---Single-Multi

Put an HTML file (e.g., hello.html) in the same directory that the server is in. Run the server program.
Determine the IP address of the host that is running the server (e.g., 192.168.0.26). From another
host, open a browser and provide the corresponding URL. For example:
http://192.168.0.26:4040/hello.html. ‘hello.html’ is the name of the file you placed in the server
directory. Note also the use of the port number after the colon. You need to replace this port
number with whatever port you have used in the server code. In the above example, the port
number is 4040. Note: you can also use the host name instead of the IP address (e.g., if you run the
server and the client on the same host you can use the hostname ‘localhost’
http://localhost:4040/hello.html). The browser should then display the contents of hello.html. If you
1 / 4
CS2005 Self-Study Exercise
omit ":4040", the browser will assume port 80 and you will get the web page from the server only if
your server is listening at port 80. Then try to get a file that is not present at the server. You should
get a “404 Not Found” message.
