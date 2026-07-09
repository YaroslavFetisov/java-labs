package com.mathpar.NAUKMA.Exam;

import mpi.MPI;
import mpi.MPIException;

public class GaussSingleProcessor {

    public static void main(String[] args) throws MPIException {
        MPI.Init(args);

        int rank = MPI.COMM_WORLD.getRank();
        int size = MPI.COMM_WORLD.getSize();

        // Програма працює лише на одному процесі
        if (size != 1) {
            if (rank == 0) {
                System.err.println("Error: This program must be run with 1 process.");
            }
            MPI.Finalize();
            return;
        }

        // Перевірка на кількість параметрів командного рядка
        if (args.length != 2) {
            if (rank == 0) {
                System.err.println("Usage: java GaussSingleProcessor <rows> <columns>");
            }
            MPI.Finalize();
            return;
        }

        // Зчитуємо кількість рядків та стовпців з командного рядка
        int rowCount = Integer.parseInt(args[0]);
        int columnCount = Integer.parseInt(args[1]);

        // Генерація початкової матриці
        double[][] matrix = MatrixUtils.generateRandomMatrix(rowCount, columnCount);
        double[][] initMatrix = new double[rowCount][columnCount];

        // Глибока копія значень з matrix в initMatrix
        for (int i = 0; i < rowCount; i++) {
            System.arraycopy(matrix[i], 0, initMatrix[i], 0, columnCount);
        }

        System.out.println("\nПочаткова матриця:");
        MatrixUtils.printMatrix(matrix);
        System.out.println("\n");

        // Замір часу
        long startTime = System.nanoTime();

        // Прямий хід
        for (int i = 0; i < Math.min(rowCount, columnCount - 1); i++) {
            // Знаходимо рядок із максимальним опорним елементом (pivot)
            int maxRow = i;
            for (int k = i + 1; k < rowCount; k++) {
                if (Math.abs(matrix[k][i]) > Math.abs(matrix[maxRow][i])) {
                    maxRow = k;
                }
            }

            // Переставляємо рядки, якщо потрібно
            if (maxRow != i) {
                double[] temp = matrix[i];
                matrix[i] = matrix[maxRow];
                matrix[maxRow] = temp;
            }

            // Нормалізуємо опорний рядок
            double[] oneRow = MatrixUtils.divideRowByElement(matrix[i], i);
            matrix[i] = oneRow;

            // Оновлюємо рядки нижче
            for (int row = i + 1; row < rowCount; row++) {
                double[] resultRow = MatrixUtils.addScaledRow(matrix[row], oneRow, i);
                matrix[row] = resultRow;
            }
        }

        System.out.println("\nМатриця після прямого ходу:");
        MatrixUtils.printMatrix(matrix);
        System.out.println("\n");

        // Зворотний хід
        for (int i = Math.min(rowCount, columnCount - 1) - 1; i >= 0; i--) {
            double[] oneRow = MatrixUtils.divideRowByElement(matrix[i], i); // Отримуємо "еталонний" рядок
            matrix[i] = oneRow;

            for (int row = i - 1; row >= 0; row--) {
                double[] resultRow = MatrixUtils.addScaledRow(matrix[row], oneRow, i);
                matrix[row] = resultRow;
            }
        }

        System.out.println("\nМатриця після зворотнього ходу:");
        MatrixUtils.printMatrix(matrix);
        System.out.println("\n");

        // Перевірка рішення
        VerifySolution.checkSolution(initMatrix, matrix);

        // Замір часу завершення виконання
        long endTime = System.nanoTime();
        double durationInSeconds = (endTime - startTime) / 1_000_000_000.0;
        System.out.println("Час виконання: " + durationInSeconds + " секунд.");

        MPI.Finalize();
    }
}
