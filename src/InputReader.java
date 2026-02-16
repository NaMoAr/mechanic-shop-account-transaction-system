import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Date;

public class InputReader {
    private final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    public int readInt(String prompt) throws IOException {
        while (true) {
            System.out.println(prompt);
            try {
                return Integer.parseInt(in.readLine().trim());
            } catch (NumberFormatException ex) {
                System.out.println("Invalid number. Please try again.");
            }
        }
    }

    public String readLine(String prompt) throws IOException {
        System.out.println(prompt);
        return in.readLine().trim();
    }

    public Date readDate(String prompt) throws IOException {
        while (true) {
            System.out.println(prompt + " (yyyy-mm-dd)");
            String input = in.readLine().trim();
            try {
                return Date.valueOf(input);
            } catch (IllegalArgumentException ex) {
                System.out.println("Invalid date. Use format yyyy-mm-dd.");
            }
        }
    }
}
