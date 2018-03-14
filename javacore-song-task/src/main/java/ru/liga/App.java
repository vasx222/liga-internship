package ru.liga;

import com.leff.midi.MidiFile;
import ru.liga.songtask.content.Content;
import ru.liga.songtask.domain.Note;
import ru.liga.songtask.domain.SimpleMidiFile;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Всего нот: 15
 * <p>
 * Анализ диапазона:
 * верхняя: E4
 * нижняя: F3
 * диапазон: 11
 * <p>
 * Анализ длительности нот (мс):
 * 4285: 10
 * 2144: 5
 * <p>
 * Анализ нот по высоте:
 * E4: 3
 * D4: 3
 * A3: 3
 * G3: 3
 * F3: 3
 * <p>
 * Анализ интервалов:
 * 2: 9
 * 5: 3
 * 11: 2
 */
public class App {

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(App.class);
        Integer oper = Parser.parseArgs(args);
        if (oper == null) {
            logger.info("Incorrect input");
            return;
        }

        String fileName = args[0];
        /*SimpleMidiFile simpleMidiFile = new SimpleMidiFile(MidiFileCreator.
                createMidiFile(Content.ZOMBIE, fileName));*/
        SimpleMidiFile simpleMidiFile = new SimpleMidiFile(new File(fileName));
        Analyzer analyzer = new Analyzer(simpleMidiFile, logger, fileName);
        if (oper.equals(Parser.ANALYZE_CREATE_FILE)) {
            analyzer.analyzeCreateFile();
            return;
        }
        if (oper.equals(Parser.ANALYZE)) {
            analyzer.analyze();
            return;
        }
        if (oper.equals(Parser.CHANGE)) {
            analyzer.change(Integer.valueOf(args[3]), Integer.valueOf(args[5]));
        }
    }
}
