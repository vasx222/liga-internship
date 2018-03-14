package ru.liga;

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

    static File createMidiFile(String content, String fileName) {
        SimpleMidiFile simpleMidiFile = new SimpleMidiFile(content);
        File file = createEmptyFile(fileName);
        try {
            simpleMidiFile.getMidiFormat().writeToFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new File(fileName);
    }
}
