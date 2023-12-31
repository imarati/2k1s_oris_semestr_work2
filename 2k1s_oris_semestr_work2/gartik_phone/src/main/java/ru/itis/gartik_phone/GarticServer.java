package ru.itis.gartik_phone;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GarticServer {
    private static final int PORT = 1234;
    private static GarticServer instance;

    private final List<ClientHandler> clients = new ArrayList<>();
    private boolean gameRunning = false;

    private GarticServer() {
    }

    public static GarticServer getInstance() {
        if (instance == null) {
            instance = new GarticServer();
        }
        return instance;
    }

    public void startServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                if (!gameRunning) {
                    // Если игра не запущена, создаем новый поток для обработки клиента
                    ClientHandler clientHandler = new ClientHandler(clientSocket);
                    clients.add(clientHandler);
                    new Thread(clientHandler).start();
                } else {
                    // Если игра уже запущена, отправляем сообщение клиенту, что игра идет
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                    writer.write("SERVER: Извините, игра уже идет. Пожалуйста, подключитесь позже.");
                    writer.newLine();
                    writer.flush();
                    clientSocket.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startGame() {
        gameRunning = true;
        Game game = new Game(new ArrayList<>(clients));
        game.startGame();
    }

    public void broadcastMessage(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    static List<String> loadWordsFromFile(String fileName) {
        List<String> words = new ArrayList<>();
        // Логика чтения слов из файла
        return words;
    }

    public static void main(String[] args) {
        GarticServer.getInstance().startServer();
    }
}
