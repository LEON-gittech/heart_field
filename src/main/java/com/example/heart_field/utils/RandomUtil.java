package com.example.heart_field.utils;

import java.util.Random;

public class RandomUtil {
    public static long genRandomNum(int length) {
        long minValue = (long) Math.pow(10, length - 1); // 最小值
        long maxValue = (long) Math.pow(10, length) - 1; // 最大值

        Random random = new Random();
        long randomNum = minValue + ((long) (random.nextDouble() * (maxValue - minValue)));

        return randomNum;
    }

    public static int nextInt(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }
}
