package ru.liga;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.meta.Tempo;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.slf4j.Logger;
import ru.liga.songtask.domain.Note;
import ru.liga.songtask.domain.SimpleMidiFile;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.*;
import java.io.File;

public class Analyzer {

    private Logger logger;
    private SimpleMidiFile simpleMidiFile;
    private String fileName;

    Analyzer(SimpleMidiFile simpleMidiFile, Logger logger, String fileName) {
        this.logger = logger;
        this.simpleMidiFile = simpleMidiFile;
        this.fileName = Parser.getName(fileName);
    }
    private void write(String s, Writer writer) {
        logger.info(s);
        if (writer != null) {
            try {
                writer.write(s + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void printRangeAnalysis(Writer writer) {
        List<Note> vocalNoteList = simpleMidiFile.vocalNoteList();
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
        write("Анализ диапазона:", writer);
        write("верхняя: " + bottomNote.sign().fullName(), writer);
        write("нижняя: " + topNote.sign().fullName(), writer);
        write("диапазон: " + topNote.sign().diffInSemitones(bottomNote.sign()), writer);
    }

    private void printNoteDurationAnalysis(Writer writer)  {
        List<Note> vocalNoteList = simpleMidiFile.vocalNoteList();
        write("Анализ длительности нот (мс):", writer);
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
                write(s + ": " + cnt, writer);
                cnt = 0;
            }
        }
    }
    private void printNoteHeightAnalysis(Writer writer)  {
        List<Note> vocalNoteList = simpleMidiFile.vocalNoteList();
        write("Анализ нот по высоте:", writer);
        List<Note> lst = new ArrayList<>(vocalNoteList);
        lst.sort(Comparator.comparingDouble(note -> note.sign().getFrequencyHz()));
        int cnt = 0;
        for (int i = 0; i < lst.size(); i++) {
            cnt++;
            Note curNote = lst.get(i);
            if (i == lst.size() - 1 ||
                    !lst.get(i + 1).sign().getFrequencyHz().
                            equals(curNote.sign().getFrequencyHz())) {
                write(curNote.sign().fullName() + ": " + cnt, writer);
                cnt = 0;
            }
        }
    }

    private void printIntervalAnalysis(Writer writer)
    {
        List<Note> vocalNoteList = simpleMidiFile.vocalNoteList();
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
        write("Анализ интервалов:", writer);
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            write(entry.getKey() + ": " + entry.getValue(), writer);
        }
    }

    public void analyze() {
        write("Длительность (сек): " + simpleMidiFile.durationMs() / 1000, null);
        write("Всего нот: " + simpleMidiFile.vocalNoteList().size(), null);
        printRangeAnalysis(null);
        printNoteDurationAnalysis(null);
        printNoteHeightAnalysis(null);
        printIntervalAnalysis(null);
    }
    public void analyzeCreateFile() {
        try (Writer writer = new FileWriter(fileName + "_analysis.txt")) {
            write("Длительность (сек): " + simpleMidiFile.durationMs() / 1000, writer);
            write("Всего нот: " + simpleMidiFile.vocalNoteList().size(), writer);
            printRangeAnalysis(writer);
            printNoteDurationAnalysis(writer);
            printNoteHeightAnalysis(writer);
            printIntervalAnalysis(writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void change(int trans, int tempo) {
        MidiFile midiFile = simpleMidiFile.getMidiFormat();

/*        for (MidiTrack midiTrack : midiFile.getTracks()) {
            for (MidiEvent midiEvent : midiTrack.getEvents()) {
                if (midiEvent.getClass().equals(Tempo.class)) {
                    Tempo tempoEvent = (Tempo) midiEvent;
                    tempoEvent.setBpm(tempoEvent.getBpm() * (100 + tempo) / 100);
                }
            }
        }*/

        for (MidiTrack midiTrack : midiFile.getTracks()) {
            for (MidiEvent midiEvent : midiTrack.getEvents()) {
                if (midiEvent.getClass().equals(NoteOn.class)) {
                    NoteOn noteOn = (NoteOn)midiEvent;
                    noteOn.setNoteValue(noteOn.getNoteValue() + trans);
                    System.out.println("ok1!");
                    noteOn.setVelocity(noteOn.getVelocity() * (100 + tempo) / 100);
                    continue;
                }
                if (midiEvent.getClass().equals(NoteOff.class)) {
                    NoteOff noteOff = (NoteOff)midiEvent;
                    noteOff.setNoteValue(noteOff.getNoteValue() + trans);
                    System.out.println("ok2!");
                    noteOff.setVelocity(noteOff.getVelocity() * (100 + tempo) / 100);
                }
            }
        }
        String newName = fileName + "-trans" + trans + "-tempo" + tempo + ".mid";
        File file = new File(newName);
        try {
            if (file.exists()) {
                if (!file.delete()) {
                    throw new Exception("Unable to delete previous file!");
                }
            }
            if (!file.createNewFile()) {
                throw new IOException("Unable to create new file!");
            } else {
                logger.info("File " + newName + " created!");
            }
            midiFile.writeToFile(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static Boolean compareMidiFiles(String fileName1, String fileName2) {
        try {
            SimpleMidiFile mf1 = new SimpleMidiFile(new File(fileName1)),
                    mf2 = new SimpleMidiFile(new File(fileName2));
            if (mf1.vocalNoteList().size() != mf2.vocalNoteList().size()) {
                return false;
            }
            for (int i = 0; i < mf1.vocalNoteList().size(); i++) {
                Note n1 = mf1.vocalNoteList().get(i),
                        n2 = mf2.vocalNoteList().get(i);
                if (!n1.durationTicks().equals(n2.startTick()) ||
                        !n1.startTick().equals(n2.startTick()) ||
                        !n1.sign().getFrequencyHz().equals(n2.sign().getFrequencyHz())) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
