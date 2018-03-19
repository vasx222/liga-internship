package ru.liga.tools;

import ru.liga.songtask.content.Content;
import ru.liga.songtask.domain.SimpleMidiFile;

import java.io.File;
import java.io.IOException;

public class MidiFileCreator {
    public static File createEmptyFile(String fileName) {
        try {
            File file = new File(fileName);
            if (file.exists()) {
                if (!file.delete()) {
                    return null;
                }
            }
            if (!file.createNewFile()) {
                return null;
            }
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static File createDefaultMidiFile(String fileName) {
        SimpleMidiFile simpleMidiFile = new SimpleMidiFile(Content.ZOMBIE);
        File file = createEmptyFile(fileName);
        try {
            simpleMidiFile.getMidiFormat().writeToFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new File(fileName);
    }

    public static File getMidiFile(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            return file;
        }
        return createDefaultMidiFile(fileName);
    }
    public static SimpleMidiFile getSimpleMidiFile(String fileName) {
        File file = getMidiFile(fileName);
        if (file == null) {
            return null;
        }
        return new SimpleMidiFile(file);
    }
}
