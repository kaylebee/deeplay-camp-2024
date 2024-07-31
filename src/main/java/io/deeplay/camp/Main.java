package io.deeplay.camp;

import config.LoadServerProperties;
import io.deeplay.camp.handlers.MainHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Main class for starting the TCP server.
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static String serverIp;
    private static int serverPort;
    private static final int maxLengthQueue = 50;
    private static final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    /**
     * Main method to start the server.
     *
     * @param args command line arguments
     * @throws IOException if an I/O error occurs
     */
    public static void main(String[] args) throws IOException {
        var properties = LoadServerProperties.loadConfig();

        if (!Objects.equals(properties.getProperty("server.port"), "bad") &&
                !Objects.equals(properties.getProperty("server.ip"), "bad")) {

            serverIp = properties.getProperty("server.ip");
            serverPort = Integer.parseInt(properties.getProperty("server.port"));

            ServerSocket serverSocket = new ServerSocket(serverPort, maxLengthQueue, InetAddress.getByName(serverIp));
            logger.info("Server started on IP: " + serverIp + ", Port: " + serverPort);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                logger.info("New client connected: " + clientSocket.getRemoteSocketAddress());
                executor.execute(new MainHandler(clientSocket));
            }
        } else {
            logger.error("Server IP or port is not configured correctly.");
        }
    }
}
