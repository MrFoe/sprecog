/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.util;

/**
 *
 * @author Shaplygin
 */
public class Resamples {
	
    private float overlap; 
    private float[] data;
    private float fullTime;
    private float freamTime;
	

    public Resamples(float[] data,float overlap,float fullTime, float freamTime){
	this.data = data.clone();
	this.overlap = overlap;
        this.fullTime = fullTime;
        this.freamTime = freamTime;
	}
	
    public float[][] count(){
		
	int n = (int)(fullTime/(freamTime*(overlap/100))-1);  //Кол-во фреймов
	int m = (int)(data.length/fullTime * freamTime);
	int post = (int)(m*(overlap/100));
	int count = 0;
	float[][] reSmpl = new float[n][m];
            for (int i = 0; i < n; i++){
		for (int j = 0; j < m; j++){
                    if (i == 0){
                        reSmpl [i][j] = data[j];
                        count++;
                    }else{
                        if (j+post < m){
			reSmpl [i][j] = reSmpl[i-1][j+post];
                        }else{
                            reSmpl [i][j] = data [count];
                            count++;
			}
                    }
		}
            }
    return reSmpl;
    }
}