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
 */

import java.io.Serializable;

/**
 * Интерфейс нейронного слоя
 */
public interface Layer extends Serializable {
    /**
     * Получает размер входного вектора
     * @return Размер входного вектора
     */
    int getInputSize();

    /**
     * Получает размер слоя
     * @return Размер слоя
     */
    int getSize();

    /**
     * Вычисляет отклик слоя
     * @param input Входной вектор
     * @return Выходной вектор
     */
    float[] computeOutput(float[] input);
}

