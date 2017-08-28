package com.example.gildo.quadcoptercontroller.models.entities.handlers;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Queue;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by gildo on 22/11/16.
 */

public class TextFileHandler {
    private static TextFileHandler instance;

    public static TextFileHandler getInstance() {
        if (instance == null) {
            instance = new TextFileHandler();
        }

        return instance;
    }

    public Queue<String> readFileLines(File file) {
        Queue<String> fileLines = null;
        FileInputStream fileInputStream;
        BufferedReader bufferedReader;
        String currentLine;

        if (file.exists()) {
            try {
                fileLines = new ConcurrentLinkedQueue<>();
                fileInputStream = new FileInputStream(file);

                //Construct BufferedReader from InputStreamReader
                bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));

                currentLine = bufferedReader.readLine();
                while (currentLine != null) {
                    fileLines.add(currentLine);
                    currentLine = bufferedReader.readLine();
                }

                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return fileLines;
    }

    public void saveFile(File file, final Queue<String> fileLines, final boolean append) {
        Thread thread;
        final File f;

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        f = file;

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedWriter bufferedWriter;

                try {
                    bufferedWriter = new BufferedWriter(new FileWriter(f, append));

                    for (CharSequence current : fileLines) {
                        bufferedWriter.append(current);
                        bufferedWriter.newLine();
                    }

                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.setName("TextFileHandlerSaveFileThread");
        thread.start();
    }
}
