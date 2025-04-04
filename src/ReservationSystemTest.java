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
        events.add(new Event(4, 10)); // 与第一个事件冲突
        events.add(new Event(11, 15));
        boolean conflict = ReservationSystem.checkOverlap(events);
        assertTrue(conflict);
    }

    @Test
    public void testMainMethodWithValidInput() {
        // 保存原始的System.in和System.out
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        
        try {
            // 准备测试输入
            String input = "3\n5 10\n15 20\n25 30\n";
            InputStream testIn = new ByteArrayInputStream(input.getBytes());
            System.setIn(testIn);
            
            // 捕获输出
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outputStream));
            
            // 运行main方法
            ReservationSystem.main(new String[]{});
            
            // 验证输出
            String output = outputStream.toString().trim();
            assertEquals("no conflict", output);
        } finally {
            // 恢复原始的System.in和System.out
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
    }

    @Test
    public void testMainMethodWithOverlap() {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        
        try {
            // 准备测试输入 - 包含重叠事件
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
        // 保存原始的System.in和System.err
        InputStream originalIn = System.in;
        PrintStream originalErr = System.err;
        
        try {
            // 准备无效的测试输入 (非数字的事件数量)
            String input = "abc\n";
            InputStream testIn = new ByteArrayInputStream(input.getBytes());
            System.setIn(testIn);
            
            // 捕获错误输出
            ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
            System.setErr(new PrintStream(errorStream));
            
            // 运行main方法
            ReservationSystem.main(new String[]{});
            
            // 验证错误输出包含预期的消息
            String errorOutput = errorStream.toString();
            assertTrue(errorOutput.contains("input error"));
        } finally {
            // 恢复原始的System.in和System.err
            System.setIn(originalIn);
            System.setErr(originalErr);
        }
    }

    @Test
    public void testMainMethodWithUnexpectedException() {
        // 保存原始的System.in和System.err
        InputStream originalIn = System.in;
        PrintStream originalErr = System.err;
        PrintStream originalOut = System.out;
        
        try {
            // 创建一个不完整的输入 (声明3个事件但只提供1个)
            // 这会导致当Scanner尝试读取不存在的下一行时抛出NoSuchElementException
            String input = "3\n5 10\n";
            InputStream testIn = new ByteArrayInputStream(input.getBytes());
            System.setIn(testIn);
            
            // 捕获标准输出和错误输出
            ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
            System.setErr(new PrintStream(errorStream));
            
            // 禁止标准输出，使测试更清晰
            System.setOut(new PrintStream(new ByteArrayOutputStream()));
            
            // 运行main方法
            ReservationSystem.main(new String[]{});
            
            // 验证错误输出包含预期的消息
            String errorOutput = errorStream.toString();
            assertTrue(errorOutput.contains("unexpected error"));
        } finally {
            // 恢复原始的System.in、System.out和System.err
            System.setIn(originalIn);
            System.setOut(originalOut);
            System.setErr(originalErr);
        }
    }

    @Test
    public void testCheckOverlapWithEmptyList() {
        List<Event> events = new ArrayList<>(); // 创建空列表
        boolean conflict = ReservationSystem.checkOverlap(events);
        assertFalse(conflict); // 验证返回false
    }

    @Test
    public void testReservationSystemClassCoverage() {
        // 直接实例化类，确保类级别覆盖率
        ReservationSystem reservationSystem = new ReservationSystem();
        assertNotNull(reservationSystem);
    }

    @Test
    public void testInputValidatorClassCoverage() {
        // 直接实例化类，确保类级别覆盖率
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
