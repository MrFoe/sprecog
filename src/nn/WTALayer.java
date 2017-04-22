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
 * WTA слой
 * 
 * Реализация слоя победитель получает все. Победителем признаеться тот нейрон,
 * у которого выход больше всех нейроннов на величину не менее minLevel.
 * В противном случае все нейронны признаются проигравшими. 
 */
public final class WTALayer implements BackpropLayer {
    /**
     * Конструирует WTA слой заданного размера и уровнем доверия
     * @param size Размер слоя
     * @param minLevel Уровень доверия
     */
    public WTALayer(int size,float minLevel) {
        // проверки
        if (size < 1) throw new IllegalArgumentException();
        // запомним параметры слоя
        this.size = size;
        this.minLevel = minLevel;
    }

    public int getInputSize() {
        return size;
    }

    public int getSize() {
        return size;
    }

    public float[] computeOutput(float[] input) {
        // проверки
        if (input == null || input.length != size) throw new IllegalArgumentException();

        // найдем победителя
        int winner = 0;
        for (int i = 1; i < size; i++)
            if (input[i] > input[winner]) winner = i;

        // готовим ответ
        float[] output = new float[size];

        // проверим на минимальный уровень расхождения
        if (minLevel > 0) {
            float level = Float.MAX_VALUE;
            for (int i = 0; i < size; i++)
                if (i != winner && Math.abs(input[i] - input[winner]) < level)
                    level = Math.abs(input[i] - input[winner]);
            if (level < minLevel) return output;
        }

        // говорим кто победитель
        output[winner] = 1;

        // вернем отклик
        return output;
    }

    public void randomize(float min,float max) {
    }

    public float[] computeBackwardError(float[] input,float []error) {
        // проверки
        if (input == null || input.length != size || error == null ||
                error.length != size) throw new IllegalArgumentException();

        // расчитываем ошибку
        float[] backwardError = new float[size];
        float[] output = computeOutput(input);

        for (int i = 0; i < size; i++)
            backwardError[i] = error[i] + output[i] - input[i];

        // вернем входящую ошибку
        return backwardError;
    }

    public void adjust(float[] input,float[] error,float rate,float momentum) {
    }

    /**
     * Получает минимальный уровень между победителем и всеми остальными нейронами
     * @return Минимальный уровень между победителем и всеми остальными нейронами
     */
    public float getMinLevel() {
        return minLevel;
    }

    /**
     * Устанавливает минимальный уровень между победитлем и всеми остальными нейронами
     * @param minLevel Новый минимальный уровень
     */
    public void setMinLevel(float minLevel) {
        this.minLevel = minLevel;
    }

    /**
     * Размер слоя
     */
    private final int size;

    /**
     * Минимальный уровень между победителем и всеми остальными нейронами
     */
    private float minLevel;
}

