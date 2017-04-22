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
public class ExecuteData {
    
    public ExecuteData(float[][] sempls,int[] commandsIds ){
        this.sempls = sempls.clone();
        this.commandsIds = commandsIds.clone();
    }
    public ExecuteData(){
        
    }
    public float[][] sempls;
    public int[] commandsIds;
}
