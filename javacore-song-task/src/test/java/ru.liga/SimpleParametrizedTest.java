package ru.liga;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.liga.songtask.content.Content;
import ru.liga.songtask.domain.SimpleMidiFile;
import ru.liga.tools.Analyzer;
import ru.liga.tools.MidiFileCreator;
import ru.liga.tools.Parser;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class SimpleParametrizedTest {

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

    @Parameterized.Parameter
    public String[] args;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        String[][][] data = new String[][][]{
                {{fileName, "analyze"}},
                {{fileName, "analyze", "-f"}},
                {{fileName, "change", "-trans", "2", "-tempo", "20"}}
        };
        return Arrays.asList(data);
    }

    @Test
    public void primaryTest() throws Exception  {
        Parser parser = new Parser(args);
        analyzer.perform(parser);
    }
}
