package ru.liga;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.meta.Tempo;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Offset;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.liga.songtask.content.Content;
import ru.liga.songtask.domain.Note;
import ru.liga.songtask.domain.SimpleMidiFile;
import ru.liga.tools.Analyzer;
import ru.liga.tools.MidiFileCreator;
import ru.liga.tools.Parser;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SomeTests {
    private static Logger logger;
    private static SimpleMidiFile simpleMidiFile;
    private static final String fileName = "zombie.mid";
    private static Analyzer analyzer;

    @BeforeClass
    public static void initLogger() {
        logger = LoggerFactory.getLogger(App.class);
        simpleMidiFile = MidiFileCreator.
                getSimpleMidiFile(fileName);
        analyzer = new Analyzer(logger, null);
    }

    @Test
    public void testIncorrectInput() {
        String[] args = {"qwerty"};
        assertThatThrownBy(() -> new Parser(args)).
                isExactlyInstanceOf(Exception.class);
    }

    @Test
    public void testWritingAnalysisInFile() throws Exception {
        String[] args = {"zombie.mid", "analyze", "-f"};
        Parser parser = new Parser(args);
        analyzer.perform(parser);
        assertThat((new File("zombie_analysis.txt")).exists()).isTrue();
    }

    /**
     * This test creates new file with trans += 5 and tempo += 35%
     * and compares it with the original file
     * @throws Exception
     */
    @Test
    public void testComparingDifferentFiles() throws Exception {
        String fileName = "zombie.mid";
        String[] args = {fileName, "change", "-trans", "5", "-tempo", "35"};
        Parser parser = new Parser(args);
        analyzer.perform(parser);
        analyzer.perform(new Parser(new String[]{fileName, "analyze", "-f"}));

        String newName = "zombie-trans5-tempo35.mid";
        analyzer.perform(new Parser(new String[]{newName, "analyze", "-f"}));

        assertThat((new File(newName)).exists()).isTrue();

        SimpleMidiFile newFile = MidiFileCreator.getSimpleMidiFile(newName);

        assertThat(Analyzer.compareMidiFiles(fileName, newName)).isFalse();

        for (int i = 0; i < simpleMidiFile.vocalNoteList().size(); i++) {
            Note note = simpleMidiFile.vocalNoteList().get(i),
            newNote = newFile.vocalNoteList().get(i);
            assertThat(note.sign().diffInSemitones(newNote.sign())).isEqualTo(5);
        }

        MidiFile midiFile1 = simpleMidiFile.getMidiFormat(),
                midiFile2 = newFile.getMidiFormat();

        Assertions.assertThat(midiFile1.getTracks().size()).
                isEqualTo(midiFile2.getTracks().size());
        for (int i = 0; i < midiFile1.getTracks().size(); i++) {
            MidiTrack midiTrack1 = midiFile1.getTracks().get(i),
                    midiTrack2 = midiFile2.getTracks().get(i);
            assertThat(midiTrack1.getEvents().size()).
                    isEqualTo(midiTrack2.getEvents().size());
            Iterator<MidiEvent> iterator1 = midiTrack1.getEvents().iterator(),
                    iterator2 = midiTrack2.getEvents().iterator();
            while (iterator1.hasNext() && iterator2.hasNext()) {
                MidiEvent midiEvent1 = iterator1.next(),
                        midiEvent2 = iterator2.next();
                if (midiEvent1.getClass().equals(Tempo.class) &&
                        midiEvent2.getClass().equals(Tempo.class)) {
                    Tempo tempoEvent1 = (Tempo)midiEvent1,
                            tempoEvent2 = (Tempo)midiEvent2;
                    assertThat(tempoEvent1.getBpm() * 135 / 100).
                            isCloseTo(tempoEvent2.getBpm(), Offset.offset(0.001f));
                }
                if (midiEvent1.getClass().equals(NoteOn.class) &&
                        midiEvent2.getClass().equals(NoteOn.class)) {
                    NoteOn noteOn1 = (NoteOn)midiEvent1,
                            noteOn2 = (NoteOn)midiEvent2;
                    assertThat(noteOn1.getNoteValue() + 5).
                            isEqualTo(noteOn2.getNoteValue());
                    continue;
                }
                if (midiEvent1.getClass().equals(NoteOff.class) &&
                        midiEvent2.getClass().equals(NoteOff.class)) {
                    NoteOff noteOff1 = (NoteOff)midiEvent1,
                            noteOff2 = (NoteOff)midiEvent2;
                    assertThat(noteOff1.getNoteValue() + 5).
                            isEqualTo(noteOff2.getNoteValue());
                }
            }
        }
    }
}
