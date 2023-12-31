package ru.itis.gartik_phone;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class ClientHandler implements Runnable {
    private static final int MAX_PLAYERS = 4;
    private static final List<ClientHandler> clientHandlers = new ArrayList<>();
    private final Socket socket;
    private final BufferedReader bufferedReader;
    private final BufferedWriter bufferedWriter;
    private final String clientUsername;

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.clientUsername = bufferedReader.readLine();
        clientHandlers.add(this);

        if (clientHandlers.size() >= MAX_PLAYERS) {
            GarticServer.getInstance().startGame();
        }
    }

    @Override
    public void run() {
        String messageFromClient;
        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                broadcastMessage(clientUsername + ": " + messageFromClient, this);
            } catch (IOException e) {
                closeEverything();
                break;
            }
        }
    }

    private static void broadcastMessage(String messageToSend, ClientHandler sender) {
        for (ClientHandler client : clientHandlers) {
            if (client != sender) {
                client.sendMessage(messageToSend);
            }
        }
    }
    public String getClientUsername() {
        return clientUsername;
    }

    public void sendMessage(String messageToSend) {
        try {
            bufferedWriter.write(messageToSend);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            closeEverything();
        }
    }

    private void closeEverything() {
        GarticServer.getInstance().broadcastMessage("SERVER:" + clientUsername + " вышел");
        clientHandlers.remove(this);

        try {
            bufferedReader.close();
            bufferedWriter.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}



