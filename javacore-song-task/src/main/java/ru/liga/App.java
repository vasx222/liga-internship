package ru.liga;

import ru.liga.songtask.content.Content;
import ru.liga.songtask.domain.SimpleMidiFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.liga.tools.Analyzer;
import ru.liga.tools.MidiFileCreator;
import ru.liga.tools.Parser;

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
        Parser parser;
        try {
            parser = new Parser(args);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        SimpleMidiFile simpleMidiFile = MidiFileCreator.
                getSimpleMidiFile(parser.getFileFullName(), Content.ZOMBIE);
        Analyzer analyzer = new Analyzer(simpleMidiFile, logger, parser);
        analyzer.perform();
    }
}
