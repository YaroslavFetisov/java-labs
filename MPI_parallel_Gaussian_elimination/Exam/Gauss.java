package com.mathpar.NAUKMA.Exam;

import com.mathpar.parallel.utils.MPITransport;
import mpi.MPI;
import mpi.MPIException;

import java.io.IOException;

public class Gauss {

    public static void main(String[] args) throws MPIException, ClassNotFoundException, IOException {
        MPI.Init(args);

        int rank = MPI.COMM_WORLD.getRank();
        int size = MPI.COMM_WORLD.getSize();

        // Перевірка на кількість процесів
        if (size != 4) {
            if (rank == 0) {
                System.err.println("Error: This program requires exactly 4 processes.");
            }
            MPI.Finalize();
            return;
        }

        // Перевірка на кількість параметрів командного рядка
        if (args.length != 2) {
            if (rank == 0) {
                System.err.println("Usage: java Gauss <rows> <columns>");
            }
            MPI.Finalize();
            return;
        }

        // Зчитуємо кількість рядків та стовпців з командного рядка
        int rowCount = Integer.parseInt(args[0]);
        int columnCount = Integer.parseInt(args[1]);

        double[][] matrix = null;
        double[][] initMatrix = null;

        if (rank == 0) {
            matrix = MatrixUtils.generateRandomMatrix(rowCount, columnCount);
            initMatrix = new double[rowCount][columnCount];

            // Глибока копія значень з matrix в initMatrix
            for (int i = 0; i < rowCount; i++) {
                System.arraycopy(matrix[i], 0, initMatrix[i], 0, columnCount);
            }
            System.out.println("\nПочаткова матриця:");
            MatrixUtils.printMatrix(matrix);
            System.out.println("\n");
        }

        // Замір часу
        long startTime = System.nanoTime();

        // Прямий хід
        for (int i = 0; i < Math.min(rowCount, columnCount - 1); i++) {
            int core = 0;

            if (rank == 0) {
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
                    core = (core + 1) % 4; // зміна номера процесора від 0 до 3
                    if (core == 0) {
                        double[] resultRow = MatrixUtils.addScaledRow(matrix[row], oneRow, i);
                        matrix[row] = resultRow;
                    } else {
                        MPITransport.sendObjectArray(new Object[]{oneRow, matrix[row]}, 0, 2, core, row);
                        double[] resultRow = (double[]) MPITransport.recvObject(core, row + rowCount);
                        matrix[row] = resultRow;
                    }
                }

            } else {
                for (int row = i + 1; row < rowCount; row++) {
                    core = (core + 1) % 4;
                    if (core == rank) {
                        Object[] data = new Object[2];
                        MPITransport.recvObjectArray(data, 0, 2, 0, row);
                        double[] oneRow = (double[]) data[0];
                        double[] tempRow = (double[]) data[1];

                        double[] resultRow = MatrixUtils.addScaledRow(tempRow, oneRow, i);
                        MPITransport.sendObject(resultRow, 0, row + rowCount);
                    }
                }
            }
        }

        // Матриця після прямого ходу
        if (rank == 0) {
            System.out.println("\nМатриця після прямого ходу:");
            MatrixUtils.printMatrix(matrix);
            System.out.println("\n");
        }

        // Зворотний хід
        for (int i = Math.min(rowCount, columnCount - 1) - 1; i >= 0; i--) {
            int core = 0;
            if (rank == 0) {
                double[] oneRow = MatrixUtils.divideRowByElement(matrix[i], i); // Отримуємо "еталонний" рядок
                matrix[i] = oneRow;

                for (int row = i - 1; row >= 0; row--) {
                    core = (core + 1) % 4;
                    if (core == 0) {
                        double[] resultRow = MatrixUtils.addScaledRow(matrix[row], oneRow, i);
                        matrix[row] = resultRow;
                    } else {
                        MPITransport.sendObjectArray(new Object[]{oneRow, matrix[row]}, 0, 2, core, row);
                        double[] resultRow = (double[]) MPITransport.recvObject(core, row + rowCount);
                        matrix[row] = resultRow;
                    }
                }

            } else {
                for (int row = i - 1; row >= 0; row--) {
                    core = (core + 1) % 4;
                    if (core == rank) {
                        Object[] data = new Object[2];
                        MPITransport.recvObjectArray(data, 0, 2, 0, row);
                        double[] oneRow = (double[]) data[0];
                        double[] tempRow = (double[]) data[1];

                        double[] resultRow = MatrixUtils.addScaledRow(tempRow, oneRow, i);
                        MPITransport.sendObject(resultRow, 0, row + rowCount);
                    }
                }
            }
        }

        // Матриця після зворотнього ходу
        if (rank == 0) {
            System.out.println("\nМатриця після зворотнього ходу:");
            MatrixUtils.printMatrix(matrix);
            System.out.println("\n");

            VerifySolution.checkSolution(initMatrix, matrix);

            // Замір часу завершення виконання
            long endTime = System.nanoTime();
            double durationInSeconds = (endTime - startTime) / 1_000_000_000.0;
            System.out.println("Час виконання: " + durationInSeconds + " секунд.");
        }

        MPI.Finalize();
    }
}
