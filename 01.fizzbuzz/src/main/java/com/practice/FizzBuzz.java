package com.practice;

public class FizzBuzz {
    public static final int MINIMUM = 1;
    public static final int MAXIMUM = 100;

    private FizzBuzz() {}

    public static String convert(int number) {
        checkValidInput(number);
        return convertSafely(number);
    }

    private static String convertSafely(int number) {
        if (isDivisibleBy(number, 15)) return "FizzBuzz";
        if (isDivisibleBy(number, 5)) return "Buzz";
        if (isDivisibleBy(number, 3)) return "Fizz";

        return String.valueOf(number);
    }

    private static boolean isDivisibleBy(int number, int x) {
        return number % x == 0;
    }

    private static void checkValidInput(int number) {
        if (number < MINIMUM || number > MAXIMUM) {
            throw new IllegalArgumentException("Number should be between 1 and 100");
        }
    }
}
