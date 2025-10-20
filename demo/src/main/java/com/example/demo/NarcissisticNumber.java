package com.example.demo;

/**
 * 水仙花数算法演示
 * 水仙花数是指一个 n 位数，其各位数字的 n 次方之和等于它本身。
 * 例如：153 = 1^3 + 5^3 + 3^3
 */
public class NarcissisticNumber {

    /**
     * 判断一个数是否为水仙花数
     * @param num 待判断的数字
     * @return 是否为水仙花数
     */
    public static boolean isNarcissistic(int num) {
        int digits = String.valueOf(num).length();
        int sum = 0, temp = num;
        while (temp > 0) {
            int d = temp % 10;
            sum += Math.pow(d, digits);
            temp /= 10;
        }
        return sum == num;
    }

    /**
     * 打印所有三位数的水仙花数
     */
    public static void printThreeDigitNarcissisticNumbers() {
        System.out.println("三位数的水仙花数有：");
        for (int i = 100; i < 1000; i++) {
            if (isNarcissistic(i)) {
                System.out.println(i);
            }
        }
    }

    public static void main(String[] args) {
        printThreeDigitNarcissisticNumbers();
    }
}