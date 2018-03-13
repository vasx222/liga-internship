package ru.liga;

import ru.liga.songtask.content.Content;
import ru.liga.songtask.domain.Note;
import ru.liga.songtask.domain.SimpleMidiFile;

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
    private static void printRangeAnalysis(List<Note> vocalNoteList) {
        Note bottomNote = null, topNote = null;
        for (Note note : vocalNoteList) {
            if (topNote == null ||
                    topNote.sign().getFrequencyHz() > note.sign().getFrequencyHz()) {
                topNote = note;
            }
            if (bottomNote == null ||
                    bottomNote.sign().getFrequencyHz() < note.sign().getFrequencyHz()) {
                bottomNote = note;
            }
        }
        logger.info("Анализ диапазона:");
        logger.info("верхняя: " + bottomNote.sign().fullName());
        logger.info("нижняя: " + topNote.sign().fullName());
        logger.info("диапазон: " + topNote.sign().diffInSemitones(bottomNote.sign()));
    }

    private static void printNoteDurationAnalysis(List<Note> vocalNoteList,
                                                  SimpleMidiFile simpleMidiFile)  {
        logger.info("Анализ длительности нот (мс):");
        List<Note> lst = new ArrayList<>(vocalNoteList);
        lst.sort(Comparator.comparingLong(Note::durationTicks).reversed());
        int cnt = 0;
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        for (int i = 0; i < lst.size(); i++) {
            cnt++;
            Note curNote = lst.get(i);
            if (i == lst.size() - 1 ||
                    !lst.get(i + 1).durationTicks().equals(curNote.durationTicks())) {
                Float duration = curNote.durationTicks() * simpleMidiFile.tickInMs();
                String s = decimalFormat.format(duration);
                logger.info(s + ": " + cnt);
                cnt = 0;
            }
        }
    }

    private static void printNoteHeightAnalysis(List<Note> vocalNoteList)  {
        logger.info("Анализ нот по высоте:");
        List<Note> lst = new ArrayList<>(vocalNoteList);
        lst.sort(Comparator.comparingDouble(note -> note.sign().getFrequencyHz()));
        int cnt = 0;
        for (int i = 0; i < lst.size(); i++) {
            cnt++;
            Note curNote = lst.get(i);
            if (i == lst.size() - 1 ||
                    !lst.get(i + 1).sign().getFrequencyHz().
                            equals(curNote.sign().getFrequencyHz())) {
                logger.info(curNote.sign().fullName() + ": " + cnt);
                cnt = 0;
            }
        }
    }

    private static void printIntervalAnalysis(List<Note> vocalNoteList)
             {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < vocalNoteList.size() - 1; i++) {
            Note curNote = vocalNoteList.get(i);
            Note nextNote = vocalNoteList.get(i + 1);
            Integer diff = curNote.sign().diffInSemitones(nextNote.sign());
            Integer cnt = map.get(diff);
            if (cnt == null) {
                cnt = 0;
            }
            map.put(diff, cnt + 1);
        }
        logger.info("Анализ интервалов:");
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            logger.info(entry.getKey() + ": " + entry.getValue());
        }
    }

    private static Logger logger = LoggerFactory.getLogger( App.class );

    public static void main(String[] args) {
            SimpleMidiFile simpleMidiFile = new SimpleMidiFile(Content.ZOMBIE);
            logger.info("Длительность (сек): " +
                    simpleMidiFile.durationMs() / 1000);
            List<Note> vocalNoteList = simpleMidiFile.vocalNoteList();
            logger.info("Всего нот: " + vocalNoteList.size());
            printRangeAnalysis(vocalNoteList);
            printNoteDurationAnalysis(vocalNoteList, simpleMidiFile);
            printNoteHeightAnalysis(vocalNoteList);
            printIntervalAnalysis(vocalNoteList);
    }
}
