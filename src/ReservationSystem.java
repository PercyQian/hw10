import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class ReservationSystem {

    public static void main(String[] args) {
        List<Event> events = new ArrayList<>();
        try (Scanner scanner = new Scanner(System.in)) {
            // 读取第一行，解析事件数量
            int numEvents = InputValidator.readNumberOfEvents(scanner);
            // 逐行读取事件信息
            for (int i = 0; i < numEvents; i++) {
                String line = scanner.nextLine();
                // 行号从第二行开始（第一行为事件数量）
                Event event = InputValidator.parseEvent(line, i + 2);
                events.add(event);
            }
        } catch (InvalidInputException e) {
            System.err.println("输入错误: " + e.getMessage());
            return;
        } catch (Exception e) {
            System.err.println("发生未预料的错误: " + e.getMessage());
            return;
        }
        
        // 检测事件是否存在冲突
        boolean conflict = checkOverlap(events);
        if (conflict) {
            System.out.println("存在事件冲突。");
        } else {
            System.out.println("无事件冲突。");
        }
    }

    /**
     * 检查事件列表中是否存在重叠冲突。
     * 先按开始日期排序，再比较相邻事件。
     *
     * @param events 已验证的事件列表
     * @return 存在重叠返回 true，否则 false
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
