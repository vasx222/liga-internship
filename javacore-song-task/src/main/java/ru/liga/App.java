package ru.liga;

import ru.liga.songtask.content.Content;
import ru.liga.songtask.domain.Note;
import ru.liga.songtask.domain.SimpleMidiFile;

import java.io.FileWriter;
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
    private static void printRangeAnalysis(List<Note> vocalNoteList, Writer writer)
            throws IOException {
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
        writer.write("Анализ диапазона:" + "\n");
        writer.write("верхняя: " + bottomNote.sign().fullName() + "\n");
        writer.write("нижняя: " + topNote.sign().fullName() + "\n");
        writer.write("диапазон: " + topNote.sign().diffInSemitones(bottomNote.sign()) + "\n");
    }

    private static void printNoteDurationAnalysis(List<Note> vocalNoteList,
                                                  SimpleMidiFile simpleMidiFile,
                                                  Writer writer) throws IOException {
        writer.write("Анализ длительности нот (мс):" + "\n");
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
                writer.write(s + ": " + cnt + "\n");
                cnt = 0;
            }
        }
    }

    private static void printNoteHeightAnalysis(List<Note> vocalNoteList, Writer writer) throws IOException {
        writer.write("Анализ нот по высоте:" + "\n");
        List<Note> lst = new ArrayList<>(vocalNoteList);
        lst.sort(Comparator.comparingDouble(note -> note.sign().getFrequencyHz()));
        int cnt = 0;
        for (int i = 0; i < lst.size(); i++) {
            cnt++;
            Note curNote = lst.get(i);
            if (i == lst.size() - 1 ||
                    !lst.get(i + 1).sign().getFrequencyHz().
                            equals(curNote.sign().getFrequencyHz())) {
                writer.write(curNote.sign().fullName() + ": " + cnt + "\n");
                cnt = 0;
            }
        }
    }

    private static void printIntervalAnalysis(List<Note> vocalNoteList, Writer writer) throws IOException {
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
        writer.write("Анализ интервалов:" + "\n");
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            writer.write(entry.getKey() + ": " + entry.getValue() + "\n");
        }
    }

    private static Logger logger = LoggerFactory.getLogger( App.class );

    public static void main(String[] args) {
        logger.info("Check my logger");

        String fileName = "log.txt";
        try (Writer writer = new FileWriter(fileName, false)) {
            SimpleMidiFile simpleMidiFile = new SimpleMidiFile(Content.ZOMBIE);
            writer.write("Длительность (сек): " +
                    simpleMidiFile.durationMs() / 1000 + "\n");
            List<Note> vocalNoteList = simpleMidiFile.vocalNoteList();
            writer.write("Всего нот: " + vocalNoteList.size() + "\n");
            writer.write("\n");
            printRangeAnalysis(vocalNoteList, writer);
            writer.write("\n");
            printNoteDurationAnalysis(vocalNoteList, simpleMidiFile, writer);
            writer.write("\n");
            printNoteHeightAnalysis(vocalNoteList, writer);
            writer.write("\n");
            printIntervalAnalysis(vocalNoteList, writer);

            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
