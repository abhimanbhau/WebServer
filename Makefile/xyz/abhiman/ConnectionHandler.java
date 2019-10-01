package xyz.abhiman;

import java.io.*;
import java.net.Socket;
import java.nio.file.AccessDeniedException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;

public class ConnectionHandler extends Thread {
    private Socket socket;
    private String _documentRoot;

    ConnectionHandler(Socket s, String documentRoot) {
        socket = s;
        if(documentRoot.equals("/")) {
            _documentRoot = "";
        }
        else
            _documentRoot = documentRoot;
    }

    public void run() {
        try {
            // Intercept the socket's incoming and outgoing stream

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            PrintStream out = new PrintStream(new BufferedOutputStream(
                    socket.getOutputStream()));

            String s = in.readLine();

            if(!(s.startsWith("GET") || s.startsWith ("HEAD") && s.endsWith("HTTP/1.0") || s.endsWith("HTTP/1.1"))) {
                out.println("HTTP/1.0 400 Bad Request\r\n" +
                        "Content-type: text/html\r\n\r\n" +
                        "<html><h1> Malformed Request</h1><br /><br /><br /><br /><h6>Abhiman Kolte 00001414541</h6></html>\n");
                out.close();
                return;
            }

            // Attempt to serve the file.  Catch FileNotFoundException and
            // return an HTTP error "404 Not Found".  Treat invalid requests
            // the same way.
            String filename = "";
            StringTokenizer st = new StringTokenizer(s);
            try {

                // Parse the filename from the GET command
                if (st.hasMoreElements() && st.nextToken().equalsIgnoreCase("GET")
                        && st.hasMoreElements())
                    filename = st.nextToken();
                else
                    throw new FileNotFoundException();  // Bad request

                // Append trailing "/" with "index.html"
                if (filename.endsWith("/"))
                    filename += "index.html";

                // Remove leading / from filename
                while (filename.indexOf("/") == 0)
                    filename = filename.substring(1);

                // If a directory is requested and the trailing / is missing,
                // send the client an HTTP request to append it.  (This is
                // necessary for relative links to work correctly in the client).
                if (new File(filename).isDirectory()) {
                    filename = filename.replace('\\', '/');
                    out.print("HTTP/1.0 301 Moved Permanently\r\n" +
                            "Location: /" + filename + "/\r\n\r\n");
                    out.close();
                    return;
                }

                // Convert relative file path to absolute file path
                filename = _documentRoot + "/" + filename;

                System.out.println("Current Thread ID: " + Thread.currentThread().getId() + " | " + s);

                InputStream f = new FileInputStream(filename);

                String mimeType = "text/plain";
                if (filename.endsWith(".html"))
                    mimeType = "text/html";
                else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg"))
                    mimeType = "image/jpeg";
                else if (filename.endsWith(".gif"))
                    mimeType = "image/gif";
                else if (filename.endsWith(".css"))
                    mimeType = "text/css";
                out.print("HTTP/1.0 200 OK\r\n" +
                        "Content-type: " + mimeType + "\r\nContent-Length: "+ new File(filename).length() + "\r\n" +
                        "Date: " + getServerTime() + "\r\n\r\n");

                // Create byte array of length equal to the file size and read file into it
                byte[] a = new byte[(int) new File(filename).length()];
                int n;
                while ((n = f.read(a)) > 0)
                    out.write(a, 0, n);
                out.close();
            } catch (FileNotFoundException ex) {
                if (!ex.getMessage().contains("Permission denied")) {
                    out.println("HTTP/1.0 404 Not Found\r\n" +
                            "Content-type: text/html\r\n\r\n" +
                            "<html><h1>" + filename + " not found</h1><h4>Server Message: " + ex + "</h4><br /><br /><br /><br /><h6>Abhiman Kolte 00001414541</h6></html>\n");
                    out.close();
                } else {
                    out.println("HTTP/1.0 403 Permission Denied\r\n" +
                            "Content-type: text/html\r\n\r\n" +
                            "<html><h1>" + filename + " Not Allowed</h1><h4>Server Message: " + ex + "</h4><br /><br /><br /><br /><h6>Abhiman Kolte 00001414541</h6></html>\n");
                    out.close();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    String getServerTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(calendar.getTime());
    }
}