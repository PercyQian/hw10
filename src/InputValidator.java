import java.util.Scanner;

public class InputValidator {

    /**
     * read the first line of the input
     *
     * @param scanner the input scanner
     * @return the number of events
     * @throws InvalidInputException when the input is empty or the format is incorrect
     */
    public static int readNumberOfEvents(Scanner scanner) throws InvalidInputException {
        if (!scanner.hasNextLine()) {
            throw new InvalidInputException("no input for the number of events.");
        }
        String line = scanner.nextLine().trim();
        try {
            int num = Integer.parseInt(line);
            if (num < 0) {
                throw new InvalidInputException("the number of events cannot be negative.");
            }
            return num;
        } catch (NumberFormatException e) {
            throw new InvalidInputException("the format of the number of events is incorrect: " + line);
        }
    }

    /**
     * parse a single line of input into an Event object.
     *
     * @param line the input line (should contain two integers)
     * @param lineNumber the current line number, for error reporting
     * @return a valid Event object
     * @throws InvalidInputException when the format or data is invalid
     */
    public static Event parseEvent(String line, int lineNumber) throws InvalidInputException {
        if (line == null || line.isEmpty()) {
            throw new InvalidInputException("the " + lineNumber + " line is empty.");
        }
        String[] tokens = line.trim().split("\\s+");
        if (tokens.length != 2) {
            throw new InvalidInputException("the " + lineNumber + " line must contain two integers.");
        }
        int start, end;
        try {
            start = Integer.parseInt(tokens[0]);
            end = Integer.parseInt(tokens[1]);
        } catch (NumberFormatException e) {
            throw new InvalidInputException("the " + lineNumber + " line contains non-integer values.");
        }
        if (!isValidDay(start) || !isValidDay(end)) {
            throw new InvalidInputException("the date value of the " + lineNumber + " line must be between 1 and 366.");
        }
        if (start > end) {
            throw new InvalidInputException("the start date of the " + lineNumber + " line cannot be greater than the end date.");
        }
        return new Event(start, end);
    }

    /**
     * check if the date is between 1 and 366.
     *
     * @param day the date
     * @return true if the date is valid, otherwise false
     */
    private static boolean isValidDay(int day) {
        return day >= 1 && day <= 366;
    }
}
