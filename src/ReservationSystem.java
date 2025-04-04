import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class ReservationSystem {

    public static void main(String[] args) {
        List<Event> events = new ArrayList<>();
        try (Scanner scanner = new Scanner(System.in)) {
            // read the number of events
            int numEvents = InputValidator.readNumberOfEvents(scanner);
            // read the events
            for (int i = 0; i < numEvents; i++) {
                String line = scanner.nextLine();
                // the line number starts from the second line (the first line is the number of events)
                Event event = InputValidator.parseEvent(line, i + 2);
                events.add(event);
            }
        } catch (InvalidInputException e) {
            System.err.println("input error: " + e.getMessage());
            return;
        } catch (Exception e) {
            System.err.println("unexpected error: " + e.getMessage());
            return;
        }
        
        // check if there is any conflict
        boolean conflict = checkOverlap(events);
        if (conflict) {
            System.out.println("conflict");
        } else {
            System.out.println("no conflict");
        }
    }

    /**
     * check if there is any conflict
     * sort the events by the start date, then compare the adjacent events
     *
     * @param events the list of events
     * @return true if there is any conflict, otherwise false
     */
    public static boolean checkOverlap(List<Event> events) {
        if (events.isEmpty()) {
            return false;
        }
        events.sort(Comparator.comparingInt(Event::getStart));
        Event previous = events.get(0);
        for (int i = 1; i < events.size(); i++) {
            Event current = events.get(i);
            if (current.getStart() <= previous.getEnd()) {
                return true;
            }
            previous = current;
        }
        return false;
    }
}
