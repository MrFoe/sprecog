/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package code.dtw;

import java.util.Stack;

/*
 *
 * @author Shaplygin
 */


public class DTW {
    
    public DTW(){
    }
    
    public void dtw(float a[],float b[],float dw[][], Stack<Float> w){
        // a,b - the sequences, dw - the minimal distances matrix
        // w - the warping path
        int n = a.length,m = b.length;
        float d[][]=new float[n][m]; // the euclidian distances matrix
        for(int i=0;i<n;i++)
            for(int j=0;j<m;j++)d[i][j] = Math.abs(a[i]-b[j]);
            // determinate of minimal distance
                dw[0][0]=d[0][0];
                for(int i=1;i<n;i++)
                    dw[i][0] = d[i][0]+dw[i-1][0];
                        for(int j=1;j<m;j++)
                            dw[0][j] = d[0][j]+dw[0][j-1];
                        for(int i=1;i<n;i++)
                            for(int j=1;j<m;j++)
                                if(dw[i-1][j-1] <= dw[i-1][j])
                                    if(dw[i-1][j-1] <= dw[i][j-1])
                                        dw[i][j] = d[i][j] + dw[i-1][j-1];
                                    else dw[i][j] = d[i][j]+dw[i][j-1];
                                else
                                    if(dw[i-1][j] <= dw[i][j-1])
                                        dw[i][j] = d[i][j] + dw[i-1][j];
                                    else dw[i][j] = d[i][j] + dw[i][j-1];
                int i = n-1,j = m-1;
                float element = dw[i][j];
                // determinate of warping path
                w.push(new Float(dw[i][j]));
                do{
                    if(i>0&&j>0)
                        if(dw[i-1][j-1] <= dw[i-1][j])
                            if(dw[i-1][j-1] <= dw[i][j-1])
                                {i--;j--;} 
                            else j--;
                            else if(dw[i-1][j] <= dw[i][j-1])
                                i--; 
                                else j--;
                        else if( i == 0 ) j--; 
                    else i--;
                        w.push(new Float(dw[i][j]));
                }
                while(i!=0 || j!=0);
    }
}