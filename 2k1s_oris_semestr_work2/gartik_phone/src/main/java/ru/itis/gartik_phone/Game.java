package ru.itis.gartik_phone;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Game {
    private static final int MAX_ROUNDS = 10;
    private static final int WORDS_PER_GAME = 40;
    private List<ClientHandler> players;
    private int currentRound = 1;
    private int currentDrawingPlayerIndex = 0;
    private List<String> wordList;
    private List<String> usedWords;

    public Game(List<ClientHandler> players) {
        this.players = players;
        this.wordList = new ArrayList<>();
        this.usedWords = new ArrayList<>();
        loadWordsFromFile("words.txt");
    }


    public void startGame() {
        broadcastMessage("SERVER: Игра началась! Раунд " + currentRound);

        while (currentRound <= MAX_ROUNDS) {
            broadcastMessage("SERVER: Начало раунда " + currentRound);

            // Загадываем новые слова для каждого игрока
            List<String> wordsToGuess = getNextWords();
            for (int i = 0; i < players.size(); i++) {
                ClientHandler player = players.get(i);
                String wordToDraw = wordsToGuess.get(i);

                // Отправляем слово для рисования только текущему игроку
                player.sendMessage("SERVER: Загаданное слово: " + wordToDraw);

                // Отправляем метку "Только для рисующего" остальным игрокам
                broadcastMessageExceptCurrent("SERVER: Загаданное слово: *** (только для рисующего)", player);
            }

            startDrawingForCurrentPlayer();

            try {
                Thread.sleep(60_000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            broadcastMessage("SERVER: Раунд завершен!");
            currentRound++;

            switchDrawingPlayer();
        }

        broadcastMessage("SERVER: Игра завершена!");
        wordList.clear();
    }

    private void broadcastMessageExceptCurrent(String messageToSend, ClientHandler excludeClient) {
        for (ClientHandler client : players) {
            if (client != excludeClient) {
                client.sendMessage(messageToSend);
            }
        }
    }

    private void loadWordsFromFile(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                wordList.add(line.trim());
            }
            Collections.shuffle(wordList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> getNextWords() {
        List<String> words = new ArrayList<>();
        while (words.size() < WORDS_PER_GAME) {
            String word = getRandomWord();
            if (!usedWords.contains(word)) {
                words.add(word);
                usedWords.add(word);
            }
        }
        return words;
    }

    private String getRandomWord() {
        return wordList.get(new Random().nextInt(wordList.size()));
    }

    private void startDrawingForCurrentPlayer() {
        if (!players.isEmpty()) {
            ClientHandler currentDrawingPlayer = players.get(currentDrawingPlayerIndex);
            broadcastMessage("SERVER: Начинает рисовать: " + currentDrawingPlayer.getClientUsername());

            // Здесь вы можете добавить логику для начала рисования для текущего игрока
            // Например, отправить ему специальное сообщение, предложившее начать рисовать
        }
    }

    private void switchDrawingPlayer() {
        // Переключаем задание на следующего игрока
        currentDrawingPlayerIndex = (currentDrawingPlayerIndex + 1) % players.size();
    }

    private void broadcastMessage(String messageToSend) {
        for (ClientHandler client : players) {
            client.sendMessage(messageToSend);
        }
    }
}





