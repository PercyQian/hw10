import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;

public class ReservationSystemTest {

    @Test
    public void testValidNumberOfEvents() throws InvalidInputException {
        String input = "3\n";
        Scanner scanner = new Scanner(input);
        int num = InputValidator.readNumberOfEvents(scanner);
        assertEquals(3, num);
    }

    // test for no input of number of events
    @Test(expected = InvalidInputException.class)
    public void testNoInputOfNumberOfEvents() throws InvalidInputException {
        // create an empty input stream
        InputStream emptyStream = new ByteArrayInputStream(new byte[0]);
        Scanner scanner = new Scanner(emptyStream);
        
        // call the method, should throw InvalidInputException
        InputValidator.readNumberOfEvents(scanner);
    }

    // test for negative input of number of events
    @Test(expected = InvalidInputException.class)
    public void testNegativeInputOfNumberOfEvents() throws InvalidInputException {
        // create an input stream with negative number
        String input = "-3\n";
        Scanner scanner = new Scanner(input);
        
        // call the method, should throw InvalidInputException
        InputValidator.readNumberOfEvents(scanner);
    }

    @Test(expected = InvalidInputException.class)
    public void testInvalidNumberOfEvents() throws InvalidInputException {
        String input = "abc\n";
        Scanner scanner = new Scanner(input);
        InputValidator.readNumberOfEvents(scanner);
    }

    @Test
    public void testValidEventParsing() throws InvalidInputException {
        String line = "10 20";
        Event event = InputValidator.parseEvent(line, 1);
        assertEquals(10, event.getStart());
        assertEquals(20, event.getEnd());
    }

    @Test(expected = InvalidInputException.class)
    public void testEmptyEventLine() throws InvalidInputException {
        String line = "";
        InputValidator.parseEvent(line, 1);
    }

    @Test(expected = InvalidInputException.class)
    public void testNotEnoughTokens() throws InvalidInputException {
        String line = "10";
        InputValidator.parseEvent(line, 1);
    }

    @Test(expected = InvalidInputException.class)
    public void testNonIntegerInput() throws InvalidInputException {
        String line = "10 abc";
        InputValidator.parseEvent(line, 1);
    }

    @Test(expected = InvalidInputException.class)
    public void testOutOfRangeDate() throws InvalidInputException {
        String line = "0 20";
        InputValidator.parseEvent(line, 1);
    }

    @Test(expected = InvalidInputException.class)
    public void testStartAfterEnd() throws InvalidInputException {
        String line = "30 20";
        InputValidator.parseEvent(line, 1);
    }

    // test for null event line
    @Test(expected = InvalidInputException.class)
    public void testNullEventLine() throws InvalidInputException {
        String line = null;
        InputValidator.parseEvent(line, 1);
    }

    // test for no overlap
    @Test
    public void testNoOverlap() {
        List<Event> events = new ArrayList<>();
        events.add(new Event(1, 5));
        events.add(new Event(6, 10));
        events.add(new Event(11, 15));
        boolean conflict = ReservationSystem.checkOverlap(events);
        assertFalse(conflict);
    }

    @Test
    public void testOverlap() {
        List<Event> events = new ArrayList<>();
        events.add(new Event(1, 5));
        events.add(new Event(4, 10)); // conflict with the first event
        events.add(new Event(11, 15));
        boolean conflict = ReservationSystem.checkOverlap(events);
        assertTrue(conflict);
    }

    @Test
    public void testMainMethodWithValidInput() {
        // save the original System.in and System.out
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        
        try {
            // prepare the test input
            String input = "3\n5 10\n15 20\n25 30\n";
            InputStream testIn = new ByteArrayInputStream(input.getBytes());
            System.setIn(testIn);
            
            // capture the output
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outputStream));
            
            // run the main method
            ReservationSystem.main(new String[]{});
            
            // verify the output
            String output = outputStream.toString().trim();
            assertEquals("no conflict", output);
        } finally {
            // restore the original System.in and System.out
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
    }

    @Test
    public void testMainMethodWithOverlap() {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        
        try {
            // prepare the test input - contains overlapping events
            String input = "3\n5 10\n8 15\n20 25\n";
            InputStream testIn = new ByteArrayInputStream(input.getBytes());
            System.setIn(testIn);
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outputStream));
            
            ReservationSystem.main(new String[]{});
            
            String output = outputStream.toString().trim();
            assertEquals("conflict", output);
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
    }

    @Test
    public void testMainMethodWithInvalidInput() {
        // save the original System.in and System.err
        InputStream originalIn = System.in;
        PrintStream originalErr = System.err;
        
        try {
            // prepare the invalid test input (non-numeric number of events)
            String input = "abc\n";
            InputStream testIn = new ByteArrayInputStream(input.getBytes());
            System.setIn(testIn);
            
            // capture the error output
            ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
            System.setErr(new PrintStream(errorStream));
            
            // run the main method
            ReservationSystem.main(new String[]{});
            
            // verify the error output contains the expected message
            String errorOutput = errorStream.toString();
            assertTrue(errorOutput.contains("input error"));
        } finally {
            // restore the original System.in and System.err
            System.setIn(originalIn);
            System.setErr(originalErr);
        }
    }

    @Test
    public void testMainMethodWithUnexpectedException() {
        // save the original System.in and System.err
        InputStream originalIn = System.in;
        PrintStream originalErr = System.err;
        PrintStream originalOut = System.out;
        
        try {
            // create an incomplete input (declare 3 events but only provide 1)
            // this will cause NoSuchElementException when Scanner tries to read the next line
            String input = "3\n5 10\n";
            InputStream testIn = new ByteArrayInputStream(input.getBytes());
            System.setIn(testIn);
            
            // capture the standard output and error output
            ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
            System.setErr(new PrintStream(errorStream));
            
            // disable the standard output, making the test clearer
            System.setOut(new PrintStream(new ByteArrayOutputStream()));
            
            // run the main method
            ReservationSystem.main(new String[]{});
            
            // verify the error output contains the expected message
            String errorOutput = errorStream.toString();
            assertTrue(errorOutput.contains("unexpected error"));
        } finally {
            // restore the original System.in, System.out and System.err
            System.setIn(originalIn);
            System.setOut(originalOut);
            System.setErr(originalErr);
        }
    }

    @Test
    public void testCheckOverlapWithEmptyList() {
        List<Event> events = new ArrayList<>(); // create an empty list
        boolean conflict = ReservationSystem.checkOverlap(events);
        assertFalse(conflict); // verify the return is false
    }

    @Test
    public void testReservationSystemClassCoverage() {
        // directly instantiate the class, ensuring class level coverage
        ReservationSystem reservationSystem = new ReservationSystem();
        assertNotNull(reservationSystem);
    }

    @Test
    public void testInputValidatorClassCoverage() {
        // directly instantiate the class, ensuring class level coverage
        InputValidator inputValidator = new InputValidator();
        assertNotNull(inputValidator);
    }

    // test for valid start and invalid end date
    @Test(expected = InvalidInputException.class)
    public void testValidStartInvalidEndDate() throws InvalidInputException {
        // valid start date, but invalid end date
        String line = "100 367";
        InputValidator.parseEvent(line, 1);
    }
}
