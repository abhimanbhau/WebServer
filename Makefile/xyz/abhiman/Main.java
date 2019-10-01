package xyz.abhiman;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    private static ServerSocket serverSocket;


    // Program arguments
    // Argument 1: Absolute path of document directory to serve without the trailing slash
    // Argument 2: Port number

    public static void main(String[] args) throws IOException {
        serverSocket=new ServerSocket(Integer.valueOf(args[1]));
        long totalThreadsCount = 0;
        //serverSocket = new ServerSocket(1234);
        while (true) {
            try {
                Socket s=serverSocket.accept();

                //Start a new thread to handle the incoming request
                new ConnectionHandler(s, args[0]).start();
                //System.out.println("Total threads spawned: " + ++totalThreadsCount);
            }
            catch (Exception x) {
                System.out.println(x);
            }
        }
    }
}
