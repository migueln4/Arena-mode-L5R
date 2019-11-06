package utils;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Utils {

    private static final Scanner READ_CONSOLE = new Scanner(System.in);

    public static int readInteger(int min, int max) {
        int n = READ_CONSOLE.nextInt();
        while (n < min || n > max) {
            System.out.println("You must to choose a number between " + min + " and " + max + ".");
            n = READ_CONSOLE.nextInt();
        }
        return n;
    }

    public static int[] createArrayNumbers(int size) {
        int[] result = new int[size];
        for (int i = 0; i < size; i++)
            result[i] = i;
        return result;
    }

    public static List<Integer> orderMinToMax(List<Integer> indexes) {
        return indexes.stream().sorted().collect(Collectors.toList());
    }
}
