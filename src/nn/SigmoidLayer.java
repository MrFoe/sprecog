/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nn;

/**
 *
 * @author Shaplygin
 *
 * Сигмоидальный слой
 */
public final class SigmoidLayer implements BackpropLayer {
    /**
     * Вес
     */
    private final int WEIGHT = 0;

    /**
     * Дельта
     */
    private final int DELTA = 1;

    /**
     * Констрирует сигмоидальный слой
     * @param inputSize Размер входного вектора
     * @param size Размер слоя
     * @param bipolar Флаг биполярного слоя
     */
    public SigmoidLayer(int inputSize,int size,boolean bipolar) {
        // проверки
        if (inputSize < 1 || size < 1) throw new IllegalArgumentException();

        // создаем слой
        matrix = new float[size][inputSize + 1][2];

        // запомним параметры
        this.inputSize = inputSize;
        this.bipolar = bipolar;
    }

    /**
     * Конструирует биполярный слой
     * @param inputSize Размер входного вектора
     * @param size Размер слоя
     */
    public SigmoidLayer(int inputSize,int size) {
        this(inputSize,size,true);
    }

    public int getInputSize() {
        return inputSize;
    }

    public int getSize() {
        return matrix.length;
    }

    public float[] computeOutput(float[] input) {
        // проверки
        if (input == null || input.length != inputSize)
                throw new IllegalArgumentException();

        // вычислим выход
        final int size = matrix.length;
        float[] output = new float[size];
        for (int i = 0; i < size; i++) {
            output[i] = matrix[i][0][WEIGHT];
            for (int j = 0; j < inputSize; j++)
                output[i] += input[j] * matrix[i][j + 1][WEIGHT];
            if (bipolar)
                output[i] = (float)Math.tanh(output[i]);
            else
                output[i] = 1 / (1 + (float)Math.exp(-output[i]));
        }

        // вернем оклик
        return output;
    }

    public void randomize(float min,float max) {
        final int size = matrix.length;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < inputSize + 1; j++) {
                matrix[i][j][WEIGHT] = min + (max - min) * (float)Math.random();
                matrix[i][j][DELTA] = 0;
            }
        }
    }

    public float[] computeBackwardError(float[] input,float[] error) {
        // проверки
        if (input == null || input.length != inputSize ||
                error == null || error.length != matrix.length) throw new IllegalArgumentException();

        // вычислим входящую ошибку
        float[] output = computeOutput(input);
        final int size = matrix.length;
        float[] backwardError = new float[inputSize];

        for (int i = 0; i < inputSize; i++) {
            backwardError[i] = 0;
            for (int j = 0; j < size; j++)
                backwardError[i] += error[j] * matrix[j][i + 1][WEIGHT] *
                        (bipolar ? 1 - output[j] * output[j] : output[j] * (1 - output[j]));
        }

        // вернем ошибку
        return backwardError;
    }

    public void adjust(float[] input,float[] error,float rate,float momentum) {
        // проверки
        if (input == null || input.length != inputSize || 
                error == null || error.length != matrix.length) throw new IllegalArgumentException();

        // обновляем веса
        float[] output = computeOutput(input);
        final int size = matrix.length;

        for (int i = 0; i < size; i++) {
            final float grad = error[i] * (bipolar ? 1 - output[i] * output[i] : output[i] * (1 - output[i]));
            // обновляем нулевой вес
            matrix[i][0][DELTA] = rate * grad + momentum * matrix[i][0][DELTA];
            matrix[i][0][WEIGHT] += matrix[i][0][DELTA];
            // обновим остальные веса
            for (int j = 0; j < inputSize; j++) {
                matrix[i][j + 1][DELTA] = rate * input[j] * grad + momentum * matrix[i][j + 1][DELTA];
                matrix[i][j + 1][WEIGHT] += matrix[i][j + 1][DELTA];
            }
        }
    }

    /**
     * Размер входного вектора
     */
    private final int inputSize;

    /**
     * Флаг биполярного слоя
     */
    private final boolean bipolar;

    /**
     * Матрица слоя
     */
    private float[][][] matrix;
}

