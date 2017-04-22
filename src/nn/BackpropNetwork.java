/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nn;

/**
 *
 * @author Shaplygin
 * Нейронная сеть обучаемая по алгоритму обратного распространения ошибки
 */
public final class BackpropNetwork extends Network {
    /**
     * Констрирует нейронную сеть с заданными слоями
     * @param layers
     */
    public BackpropNetwork(Layer[] layers) {
        // передадим родителям
        super(layers);
        // рандомизируем веса
        randomize(0,0.3f);
    }

    /**
     * Придает случайные значения весам нейроннов в сети
     * @param min
     * @param max
     */
    public void randomize(float min,float max) {
        // придаем случайные значения весам в сети
        final int size = getSize();
        for (int i = 0; i < size; i++) {
            Layer layer = getLayer(i);
            if (layer instanceof BackpropLayer) ((BackpropLayer)layer).randomize(min,max);
        }
    }

    /**
     * Обучает сеть паттерну
     * @param input Входной вектор
     * @param goal Заданный выходной вектор
     * @param rate Скорость обучения
     * @param momentum Моментум
     * @return Текущую ошибку обучения
     */
    public float learnPattern(float[] input,float[] goal,float rate,float momentum) {
        // проверки
        if (input == null || input.length != getInputSize() || 
                goal == null || goal.length != getOutputSize()) throw new IllegalArgumentException();

        // делаем проход вперед
        final int size = getSize();
        float[][] outputs = new float[size][];

        outputs[0] = getLayer(0).computeOutput(input);
        for (int i = 1; i < size; i++)
            outputs[i] = getLayer(i).computeOutput(outputs[i - 1]);

        // вычислим ошибку выходного слоя
        Layer layer = getLayer(size - 1);
        final int layerSize = layer.getSize();
        float[] error = new float[layerSize];
        float totalError = 0;

        for (int i = 0; i < layerSize; i++) {
            error[i] = goal[i] - outputs[size - 1][i];
            totalError += Math.abs(error[i]);
        }

        // обновим выходной слой
        if (layer instanceof BackpropLayer)
            ((BackpropLayer)layer).adjust(size == 1 ? input : outputs[size - 2],error,rate,momentum);

        // идем по скрытым слоям
        float[] prevError = error;
        Layer prevLayer = layer;

        for (int i = size - 2; i >= 0; i--,prevError = error,prevLayer = layer) {
            // получим очередной слой
            layer = getLayer(i);
            // вычислим для него ошибку
            if (prevLayer instanceof BackpropLayer)
                error = ((BackpropLayer)prevLayer).computeBackwardError(outputs[i],prevError);
            else
                error = prevError;
            // обновим слой
            if (layer instanceof BackpropLayer)
                ((BackpropLayer)layer).adjust(i == 0 ? input : outputs[i - 1],error,rate,momentum);
        }

        // вернем суммарную ошибку
        return totalError;
    }
}
