import java.util.Scanner;

public class Test {
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        String MESSAGE = in.nextLine();

        for (char c : MESSAGE.toCharArray()) {
            String rep = Integer.toBinaryString(c);
            System.err.println(rep);

            while (rep.length() > 0) {
                String curChar = rep.substring(0, 1);
                String nextChar = curChar.equals("0") ? "1" : "0";
                int lastCurChar = rep.indexOf(nextChar, 1);
                if (lastCurChar < 0) {
                    lastCurChar = rep.length();
                }
                rep = rep.substring(lastCurChar);
                System.out.print(
                        curChar.equals("0") ? "0 " : "00 "
                );
                for (int i = 0; i < lastCurChar; i++) {
                    System.out.print("0");
                }
                System.out.print(" ");

            }
        }
    }
}
