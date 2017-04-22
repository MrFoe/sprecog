/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.util;

public abstract class WindowFunction {

	/** The float value of 2*PI. Provided as a convenience for subclasses. */
	protected static final float TWO_PI = (float) (2 * Math.PI);
	protected int length;

	public WindowFunction() {
	}

	/**
	 * Apply the window function to a sample buffer.
	 * 
	 * @param samples
	 *            a sample buffer
	 */
	public void apply(float[] samples) {
		this.length = samples.length;

		for (int n = 0; n < samples.length; n++) {
			samples[n] *= value(samples.length, n);
		}
	}

	/**
	 * Generates the curve of the window function.
	 * 
	 * @param length
	 *            the length of the window
	 * @return the shape of the window function
	 */
	public float[] generateCurve(int length) {
		float[] samples = new float[length];
		for (int n = 0; n < length; n++) {
			samples[n] = 1f * value(length, n);
		}
		return samples;
	}

	protected abstract float value(int length, int index);
}
