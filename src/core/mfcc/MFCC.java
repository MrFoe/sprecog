/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.mfcc;

/**
 *
 * @author Shaplygin
 */
import java.util.ArrayList;
import java.util.Arrays;

import core.util.FFT;
import core.util.HammingWindow;

public class MFCC{
	
    private int amountOfCepstrumCoef; //Number of MFCCs per frame
    protected int amountOfMelFilters; //Number of mel filters (SPHINX-III uses 40)
    protected float lowerFilterFreq; //lower limit of filter (or 64 Hz?)
    protected float upperFilterFreq; //upper limit of filter (or half of sampling freq.?)
    
    float[] FloatBuffer;
    float[] data;
    private float[] mfcc;
    
    int centerFrequencies[];

    private FFT fft;
    private int samplesPerFrame; 
    private float sampleRate;
    
    public MFCC(float[] data,int samplesPerFrame, float sampleRate, int amountOfCepstrumCoef, int amountOfMelFilters, float lowerFilterFreq, float upperFilterFreq) {
        this.data = Arrays.copyOf(data,data.length);
    	this.samplesPerFrame = samplesPerFrame; 
        this.sampleRate = sampleRate;
        this.amountOfCepstrumCoef = amountOfCepstrumCoef;
        this.amountOfMelFilters = amountOfMelFilters;
        this.fft = new FFT(samplesPerFrame, new HammingWindow());
        
        this.lowerFilterFreq = Math.max(lowerFilterFreq, 25);
        this.upperFilterFreq = Math.min(upperFilterFreq, sampleRate / 2);
        calculateFilterBanks();       
    }

	public boolean processCount() {
		//FloatBuffer = audioEvent.getFloatBuffer().clone();
		FloatBuffer = data;
        // Magnitude Spectrum
        float bin[] = magnitudeSpectrum(FloatBuffer);
        // get Mel Filterbank
        float fbank[] = melFilter(bin, centerFrequencies);
        // Non-linear transformation
        float f[] = nonLinearTransformation(fbank);
        // Cepstral coefficients
        mfcc = cepCoefficients(f);
        
		return true;
	}

    /**
     * computes the magnitude spectrum of the input frame<br>
     * calls: none<br>
     * called by: featureExtraction
     * @param frame Input frame signal
     * @return Magnitude Spectrum array
     */
    public float[] magnitudeSpectrum(float frame[]){
        // calculate FFT for current frame	
  
        fft.forwardTransform(frame);
        int size = frame.length;
        float nframe[] = new float[size+1];
        float flag;
        //float nframe[] = new float[size+1];
        //Мои правки
        for(int i=0; i<frame.length; i++){
        	nframe[i] = frame[i];
		}
        
        if (frame.length%2 != 0){		//Смещение массива
        	//nframe = frame;
        	flag = frame[1];
        	nframe[nframe.length-1] = flag;
        	nframe[1] = 0f;
        	size++;
        }
        
        float magSpectrum[] = new float[frame.length];
        magSpectrum[0] = fft.modulus(nframe, 0);
        for (int k = 1; k < magSpectrum.length/2; k++){
        	magSpectrum[k] = fft.modulus(nframe, k);
        	magSpectrum[magSpectrum.length-k] = magSpectrum[k];        	
        }
 
        return magSpectrum;
    }
	
    /**
     * calculates the FFT bin indices<br> calls: none<br> called by:
     * featureExtraction
     *
     */
 
    public final void calculateFilterBanks() {
        centerFrequencies = new int[amountOfMelFilters + 2];

        centerFrequencies[0] = Math.round(lowerFilterFreq / sampleRate * samplesPerFrame);
        centerFrequencies[centerFrequencies.length - 1] = (int) (samplesPerFrame / 2);

        double mel[] = new double[2];
        mel[0] = freqToMel(lowerFilterFreq);
        mel[1] = freqToMel(upperFilterFreq);
        
  
        float factor = (float)((mel[1] - mel[0]) / (amountOfMelFilters + 1));
        //Calculates te centerfrequencies.
        for (int i = 1; i <= amountOfMelFilters; i++) {
            float fc = (inverseMel(mel[0] + factor * i) / sampleRate) * samplesPerFrame;
            centerFrequencies[i] = Math.round(fc);
        }

    }
    
	
    /**
     * the output of mel filtering is subjected to a logarithm function (natural logarithm)<br>
     * calls: none<br>
     * called by: featureExtraction
     * @param fbank Output of mel filtering
     * @return Natural log of the output of mel filtering
     */
    public float[] nonLinearTransformation(float fbank[]){
    	/*for (int i = 0; i<fbank.length; i++)
    		System.out.println(fbank[i]);*/
        float f[] = new float[fbank.length];
        final float FLOOR = -50;
       
        for (int i = 0; i < fbank.length; i++){
            f[i] = (float) Math.log(fbank[i]);
            // check if ln() returns a value less than the floor
            if (f[i] < FLOOR) f[i] = FLOOR;
        }
        
        return f;
    }
    
    /**
     * Calculate the output of the mel filter<br> calls: none called by:
     * featureExtraction
     * @param bin The bins.
     * @param centerFrequencies  The frequency centers.
     * @return Output of mel filter.
     */
    public float[] melFilter(float bin[], int centerFrequencies[]) {
        float temp[] = new float[amountOfMelFilters + 2];

        for (int k = 1; k <= amountOfMelFilters; k++) {
            float num1 = 0, num2 = 0;

            float den = (centerFrequencies[k] - centerFrequencies[k - 1] + 1);

            for (int i = centerFrequencies[k - 1]; i <= centerFrequencies[k]; i++) {
                num1 += bin[i] * (i - centerFrequencies[k - 1] + 1);
            }
            num1 /= den;

            den = (centerFrequencies[k + 1] - centerFrequencies[k] + 1);

            for (int i = centerFrequencies[k] + 1; i <= centerFrequencies[k + 1]; i++) {
                num2 += bin[i] * (1 - ((i - centerFrequencies[k]) / den));
            }

            temp[k] = num1 + num2;
        }

        float fbank[] = new float[amountOfMelFilters];
        
        for (int i = 0; i < amountOfMelFilters; i++) {
            fbank[i] = temp[i + 1];
        }

        return fbank;
    }
    
    
    /**
     * Cepstral coefficients are calculated from the output of the Non-linear Transformation method<br>
     * calls: none<br>
     * called by: featureExtraction
     * @param f Output of the Non-linear Transformation method
     * @return Cepstral Coefficients
     */
    public float[] cepCoefficients(float f[]){
        float cepc[] = new float[amountOfCepstrumCoef];
        
        for (int i = 0; i < cepc.length; i++){
        	//System.out.println(f[i]);
            for (int j = 0; j < f.length; j++){
                cepc[i] += f[j] * Math.cos((Math.PI * (i + 1))/ f.length * (j + 0.5));
            }
        }
        
        return cepc;
    }
    /**
     * convert frequency to mel-frequency<br>
     * calls: none<br>
     * called by: featureExtraction
     * @param freq Frequency
     * @return Mel-Frequency
     */
    protected static float freqToMel(float freq){
        return (float) (2595 * log10(1 + freq / 700));
    }
    
    /**
     * calculates the inverse of Mel Frequency<br>
     * calls: none<br>
     * called by: featureExtraction
     */
    private static float inverseMel(double x) {
        return (float) (700 * (Math.pow(10, x / 2595) - 1));
    }
    
    /**
     * calculates logarithm with base 10<br>
     * calls: none<br>
     * called by: featureExtraction
     * @param value Number to take the log of
     * @return base 10 logarithm of the input values
     */
    protected static float log10(float value){
        return (float) (Math.log(value) / Math.log(10));
    }

	public float[] getMFCC() {
		return mfcc.clone();
	}

	public int[] getCenterFrequencies() {
		return centerFrequencies;
	}
}
