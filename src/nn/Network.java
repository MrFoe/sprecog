/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nn;

/**
 *
 * @author Shaplygin
 */
import java.io.*;

/**
 * Базовая реализация нейронной сети
 */
public class Network implements Serializable {
    /**
     * Конструирует нейронную сеть с заданными слоями
     * @param layers Нейронные слои
     */
    public Network(Layer[] layers) {
        // проверки
        if (layers == null || layers.length == 0) throw new IllegalArgumentException();
        // проверим детально
        final int size = layers.length;
        for (int i = 0; i < size; i++)
            if (layers[i] == null || (i > 1 && layers[i].getInputSize() != layers[i - 1].getSize()))
                throw new IllegalArgumentException();

        // запомним слои
        this.layers = layers;
    }

    /**
     * Получает размер входного вектора
     * @return Размер входного вектора
     */
    public final int getInputSize() {
        return layers[0].getInputSize();
    }

    /**
     * Получает размер выходного вектора
     * @return Размер выходного вектора
     */
    public final int getOutputSize() {
        return layers[layers.length - 1].getSize();
    }

    /**
     * Получает размер сети
     * @return Размер сети
     */
    public final int getSize() {
        return layers.length;
    }

    /**
     * Получает нейронный слой по индексу
     * @param index Индекс слоя
     * @return Нейронный слой
     */
    public final Layer getLayer(int index) {
        return layers[index];
    }

    /**
     * Вычисляет отклик сети
     * @param input Входной вектор
     * @return Выходной вектор
     */
    public float[] computeOutput(float[] input) {
        // проверки
        if (input == null || input.length != getInputSize())
                throw new IllegalArgumentException();

        // вычислим выходной отклик сети
        float[] output = input;
        final int size = layers.length;
        for (int i = 0; i < size; i++)
            output = layers[i].computeOutput(output);

        // вернем выход
        return output;
    }

    /**
     * Сохраняет нейронну сеть в файл
     * @param fileName Имя файла
     */
    public void saveToFile(String fileName) {
        // проверки
        if (fileName == null) throw new IllegalArgumentException();

        // сохраняем
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(fileName));
            outputStream.writeObject(this);
            outputStream.close();
        }
        catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Загружает нейронную сеть из файла
     * @param fileName Имя файла
     * @return Нейронную сеть
     */
    public static Network loadFromFile(String fileName) {
        // проверки
        if (fileName == null) throw new IllegalArgumentException();

        // загружаем
        Object network = null;
        try {
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(fileName));
            network = inputStream.readObject();
            inputStream.close();
        }
        catch (Exception e) {
            throw new IllegalArgumentException(e);
        }

        // отдадим сеть
        return (Network)network;
    }

    /**
     * Слои
     */
    private Layer[] layers;
}
