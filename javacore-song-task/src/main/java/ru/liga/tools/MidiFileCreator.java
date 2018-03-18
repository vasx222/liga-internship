package ru.liga.tools;

import com.leff.midi.MidiFile;
import ru.liga.songtask.domain.SimpleMidiFile;

import java.io.File;
import java.io.IOException;

public class MidiFileCreator {
    static private File createEmptyFile(String fileName) {
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

    static File createMidiFile(String fileName, String content) {
        SimpleMidiFile simpleMidiFile = new SimpleMidiFile(content);
        File file = createEmptyFile(fileName);
        try {
            simpleMidiFile.getMidiFormat().writeToFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new File(fileName);
    }

    static File getMidiFile(String fileName) {
        return getMidiFile(fileName, null);
    }

    static File getMidiFile(String fileName, String content) {
        File file = new File(fileName);
        if (file.exists()) {
            return file;
        }
        if (content == null) {
            return null;
        }
        return createMidiFile(fileName, content);
    }
    public static SimpleMidiFile getSimpleMidiFile(String fileName, String content) {
        File file = getMidiFile(fileName, content);
        if (file == null) {
            return null;
        }
        return new SimpleMidiFile(file);
    }
}
