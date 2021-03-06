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

/**
 * This program provides tools for working with midi files.
 * It allows to analyze the content, write it to file and
 * change some characteristics.
 *
 * @author N. Vasily
 * @version 1.0
 * @since 2018, March
 */

public class App {

    public static void main(String[] args) throws Exception {
        Logger logger = LoggerFactory.getLogger(App.class);
        Parser parser;
        try {
            parser = new Parser(args);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        Analyzer analyzer = new Analyzer(logger, parser);
        analyzer.perform();
    }
}
