package com.practice;

public class FizzBuzz {
    public static String convert(int number) {
        if (number < 1 || number > 100) {
            throw new IllegalArgumentException("Number should be between 1 and 100");
        }

        if (number % 15 == 0) {
            return "FizzBuzz";
        }

        if (number % 5 == 0) {
            return "Buzz";
        }

        if (number % 3 == 0) {
            return "Fizz";
        }

        return String.valueOf(number);
    }
}
