import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;

public class ReservationSystemTest {

    @Test
    public void testValidNumberOfEvents() throws InvalidInputException {
        String input = "3\n";
        Scanner scanner = new Scanner(input);
        int num = InputValidator.readNumberOfEvents(scanner);
        assertEquals(3, num);
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
        events.add(new Event(4, 10)); // 与第一个事件冲突
        events.add(new Event(11, 15));
        boolean conflict = ReservationSystem.checkOverlap(events);
        assertTrue(conflict);
    }
}
