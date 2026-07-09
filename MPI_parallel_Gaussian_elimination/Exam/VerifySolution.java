package com.mathpar.NAUKMA.Exam;

public class VerifySolution {

    // Функція для перевірки правильності розв'язку
    public static void checkSolution(double[][] startMatrix, double[][] finalMatrix) {
        // Перевірка на узгодженість розмірів
        int rowCount = startMatrix.length;
        int columnCount = startMatrix[0].length;
        int variableCount = Math.min(columnCount - 1, finalMatrix.length);

        if (variableCount > finalMatrix.length) {
            System.err.println("Помилка: Початкова матриця має більше змінних, ніж доступно у кінцевій матриці!");
            return;
        }

        // Перевірка на спеціальні випадки
        boolean hasNoSolution = false;
        boolean hasInfiniteSolutions = false;

        // Додаткова перевірка на "немає розв'язків"
        if (columnCount > rowCount + 1 && columnCount > 2) {
            // Якщо передостанній елемент першого рядка не дорівнює 0, то система має безліч розв'язків
            if (Math.abs(startMatrix[0][columnCount - 2]) > 1e-6 && Math.abs(startMatrix[0][columnCount - 1]) > 1e-6) {
                hasInfiniteSolutions = true;
            }
            // Якщо передостанній елемент першого рядка дорівнює 0, а вільний член не дорівнює 0, система не має розв'язків
            else if (Math.abs(startMatrix[0][columnCount - 2]) < 1e-6 && Math.abs(startMatrix[0][columnCount - 1]) > 1e-6) {
                hasNoSolution = true;
            }
        }

        for (int i = 0; i < finalMatrix.length; i++) {
            boolean isZeroRow = true; // Чи є рядок повністю нульовим
            for (int j = 0; j < variableCount; j++) {
                if (Math.abs(finalMatrix[i][j]) > 1e-6) {
                    isZeroRow = false;
                    break;
                }
            }

            // Випадок 1: Немає рішень
            if (isZeroRow && Math.abs(finalMatrix[i][columnCount - 1]) > 1e-6) {
                hasNoSolution = true;
                break;
            }

            // Випадок 2: Безліч розв'язків
            if (isZeroRow && Math.abs(finalMatrix[i][columnCount - 1]) < 1e-6) {
                hasInfiniteSolutions = true;
            }
        }

        // Виведення результатів перевірки
        if (hasNoSolution) {
            System.out.println("Система не має розв'язків.\n");
            return;
        }

        if (hasInfiniteSolutions) {
            System.out.println("Система має безліч розв'язків.\n");
        }

        // Перевірка розв'язків
        System.out.println("Перевірка рішення:");

        for (int i = 0; i < rowCount; i++) {
            double calculatedValue = 0;
            System.out.print("Рядок " + i + ": ");

            for (int j = 0; j < variableCount; j++) {
                double solution = finalMatrix[j][finalMatrix[0].length - 1]; // Значення змінної
                calculatedValue += startMatrix[i][j] * solution;

                // Вивід коефіцієнтів та змінних
                String coeff = String.format("%.3f", startMatrix[i][j]);
                String sol = String.format("%.3f", solution);

                if (j < variableCount - 1) {
                    System.out.print(coeff + " * " + sol + " + ");
                } else {
                    System.out.print(coeff + " * " + sol);
                }
            }

            // Виведення результату для рядка
            System.out.println(" = " + String.format("%.3f", calculatedValue));

            // Перевірка на відповідність правій частині
            double rightSide = startMatrix[i][startMatrix[i].length - 1];
            if (Math.abs(calculatedValue - rightSide) > 1e-6) {
                System.err.println("Перевірка рішення не пройшла для рядка " + i);
                System.err.println("Обчислений результат: " + String.format("%.3f", calculatedValue) + ", Очікуваний результат: " + String.format("%.3f", rightSide));
                return;
            } else {
                System.out.println("Рядок " + i + " правильний.\n");
            }
        }

        if (!hasInfiniteSolutions) {
            System.out.println("Рішення є коректним.");
        }
    }
}
