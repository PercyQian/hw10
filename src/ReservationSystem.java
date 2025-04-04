import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

/**
 * ReservationSystem 实现了一个预订系统，
 * 检查输入的事件（起始日和结束日）中是否存在时间重叠。
 * 采用防御性编程设计，将不可信的输入通过输入验证层（barricade）过滤后再传递到内部处理逻辑。
 */
public class ReservationSystem {

    public static void main(String[] args) {
        List<Event> events = new ArrayList<>();
        try (Scanner scanner = new Scanner(System.in)) {
            // 读取第一行，解析出事件数量
            int numEvents = InputValidator.readNumberOfEvents(scanner);
            // 逐行读取事件信息，每行必须包含两个整数
            for (int i = 0; i < numEvents; i++) {
                String line = scanner.nextLine();
                Event event = InputValidator.parseEvent(line, i + 2); // 行号（第一行为事件数量）
                events.add(event);
            }
        } catch (InvalidInputException iie) {
            System.err.println("输入错误: " + iie.getMessage());
            return;
        } catch (Exception e) {
            System.err.println("发生未预料的错误: " + e.getMessage());
            return;
        }
        
        // 调用核心逻辑检测事件是否有重叠
        boolean conflict = checkOverlap(events);
        if (conflict) {
            System.out.println("存在事件冲突。");
        } else {
            System.out.println("无事件冲突。");
        }
    }

    /**
     * 检查事件列表中是否存在时间重叠。
     * 先将事件按开始日期排序，再逐个比较相邻事件的结束日期与下一个事件的开始日期。
     *
     * @param events 事件列表（均已通过验证）
     * @return 如果有重叠返回 true，否则返回 false
     */
    public static boolean checkOverlap(List<Event> events) {
        if (events.isEmpty()) {
            return false;
        }
        // 按照事件的开始日期排序
        events.sort(Comparator.comparingInt(Event::getStart));
        Event previous = events.get(0);
        // 从第二个事件开始逐个比较
        for (int i = 1; i < events.size(); i++) {
            Event current = events.get(i);
            if (current.getStart() <= previous.getEnd()) {
                return true;
            }
            previous = current;
        }
        return false;
    }

    /**
     * Event 类表示一个预订事件，包括起始日和结束日。
     */
    private static class Event {
        private final int start;
        private final int end;

        public Event(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }
    }

    /**
     * InputValidator 类负责验证和解析输入数据，
     * 保证只将合法且安全的 Event 对象传递给内部逻辑。
     */
    private static class InputValidator {
        /**
         * 读取第一行，解析出事件数量。
         *
         * @param scanner 输入扫描器
         * @return 正整数表示的事件数量
         * @throws InvalidInputException 当输入为空或格式错误时
         */
        public static int readNumberOfEvents(Scanner scanner) throws InvalidInputException {
            if (!scanner.hasNextLine()) {
                throw new InvalidInputException("未提供事件数量的输入。");
            }
            String line = scanner.nextLine().trim();
            try {
                int num = Integer.parseInt(line);
                if (num < 0) {
                    throw new InvalidInputException("事件数量不能为负数。");
                }
                return num;
            } catch (NumberFormatException e) {
                throw new InvalidInputException("事件数量格式不正确: " + line);
            }
        }

        /**
         * 解析单行输入为一个 Event 对象。
         *
         * @param line 输入行，应该包含两个整数（起始日和结束日）
         * @param lineNumber 当前行号，用于错误报告
         * @return 合法的 Event 对象
         * @throws InvalidInputException 当输入格式不符合要求时
         */
        public static Event parseEvent(String line, int lineNumber) throws InvalidInputException {
            if (line == null || line.isEmpty()) {
                throw new InvalidInputException("第 " + lineNumber + " 行输入为空。");
            }
            String[] tokens = line.trim().split("\\s+");
            if (tokens.length != 2) {
                throw new InvalidInputException("第 " + lineNumber + " 行必须包含两个整数。");
            }
            int start, end;
            try {
                start = Integer.parseInt(tokens[0]);
                end = Integer.parseInt(tokens[1]);
            } catch (NumberFormatException e) {
                throw new InvalidInputException("第 " + lineNumber + " 行包含非整数值。");
            }
            if (!isValidDay(start) || !isValidDay(end)) {
                throw new InvalidInputException("第 " + lineNumber + " 行的日期值必须在 1 到 366 之间。");
            }
            if (start > end) {
                throw new InvalidInputException("第 " + lineNumber + " 行中，开始日期不能大于结束日期。");
            }
            return new Event(start, end);
        }

        /**
         * 检查给定的日期是否在合法范围内（1到366）。
         *
         * @param day 日期
         * @return 合法返回 true，否则 false
         */
        private static boolean isValidDay(int day) {
            return day >= 1 && day <= 366;
        }
    }

    /**
     * 自定义异常类，用于输入验证错误。
     */
    private static class InvalidInputException extends Exception {
        public InvalidInputException(String message) {
            super(message);
        }
    }
}
