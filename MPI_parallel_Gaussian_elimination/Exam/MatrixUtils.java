package com.mathpar.NAUKMA.Exam;

import java.util.Arrays;
import java.util.Random;

public class MatrixUtils {

    // Метод, який ділить кожен елемент переданого рядка на n-й елемент цього рядка
    public static double[] divideRowByElement(double[] row, int n) {
        double[] newRow = Arrays.copyOf(row, row.length);  // Створюємо новий масив для результату
        if (n >= 0 && n < newRow.length) {
            double divisor = newRow[n];  // Вибір n-го елемента для ділення
            if (divisor != 0) {  // Перевірка на нуль, щоб уникнути ділення на нуль
                for (int i = 0; i < newRow.length; i++) {
                    newRow[i] /= divisor;  // Ділимо кожен елемент нового рядка на n-й елемент
                    // Перевіряємо на -0.0 і замінюємо на 0.0
                    if (newRow[i] == -0.0) {
                        newRow[i] = 0.0;
                    }
                }
            } else {
                //empty
            }
        } else {
            System.out.println("Невірний індекс елемента для ділення.");
        }
        return newRow;  // Повертаємо новий рядок
    }

    // Метод для додавання до рядка a рядка b, помноженого на певний елемент з a
    public static double[] addScaledRow(double[] a, double[] b, int index) {
        double[] newRow = Arrays.copyOf(a, a.length);  // Створюємо новий масив для результату
        if (index >= 0 && index < a.length) {
            double factor = a[index];  // Вибираємо елемент з a для множення
            for (int i = 0; i < newRow.length; i++) {
                newRow[i] += b[i] * -factor;  // Додаємо до елемента newRow[i] елемент b[i], помножений на -factor
            }
        } else {
            System.out.println("Невірний індекс.");
        }
        return newRow;  // Повертаємо новий масив
    }

    // Метод для виведення матриці
    public static void printMatrix(double[][] matrix) {
        for (double[] doubles : matrix) {
            System.out.print(" [");
            for (int j = 0; j < doubles.length; j++) {
                if (j == doubles.length - 1) {
                    System.out.printf(" | %10.3f", doubles[j]);
                } else {
                    System.out.printf("%10.3f, ", doubles[j]);
                }
            }
            System.out.println(" ]");
        }
    }

    // Метод для виведення рядка матриці в такому ж форматі, як і printMatrix
    public static void printRow(double[] row) {
        System.out.print(" [");
        for (int j = 0; j < row.length; j++) {
            if (j == row.length - 1) {
                // Останній елемент з вертикальною лінією
                System.out.printf(" | %10.3f", row[j]);
            } else {
                // Інші елементи з комами
                System.out.printf("%10.3f, ", row[j]);
            }
        }
        System.out.println(" ]");
    }

    //Метод перевіряє кількість рядків приведених до одиниці в матриці
    public static int oneRowsCheck(double[][] matrix) {
        int res = 0;
        int max_ones = Math.min(matrix.length, matrix[0].length);
        for (int i = 0; i < max_ones; i++) {
            if (matrix[i][i] == 1.0) {
                res += 1;
            }
        }
        if (res == 0){
            System.out.println("Кількість одиничних рядків - 0!");
        }
        return res;
    }

    // Генерація випадкової матриці
    public static double[][] generateRandomMatrix(int rowCount, int columnCount) {
        Random rand = new Random();
        double[][] matrix = new double[rowCount][columnCount];
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                matrix[i][j] = rand.nextInt(21) - 10; // Генерація цілих чисел від -10 до 10
            }
        }
        return matrix;
    }
}
