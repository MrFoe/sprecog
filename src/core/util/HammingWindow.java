/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.util;

public class HammingWindow extends WindowFunction {
	/** Constructs a Hamming window. */
	public HammingWindow() {
	}

	protected float value(int length, int index) {
		return 1;//0.54f - 0.46f * (float) Math.cos(TWO_PI * index / (length - 1));
	}
}
