import java.util.Scanner;

public class InputValidator {

    /**
     * 读取第一行输入，并解析事件数量。
     *
     * @param scanner 输入扫描器
     * @return 正整数的事件数量
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
     * @param line 输入行（应包含两个整数）
     * @param lineNumber 当前行号，用于错误报告
     * @return 合法的 Event 对象
     * @throws InvalidInputException 当格式或数据不合法时
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
     * 检查日期是否在 1 到 366 范围内。
     *
     * @param day 日期
     * @return 合法返回 true，否则 false
     */
    private static boolean isValidDay(int day) {
        return day >= 1 && day <= 366;
    }
}
