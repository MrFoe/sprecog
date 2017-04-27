/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package forms;

import code.dtw.DTW;
import core.mfcc.MFCC;
import core.util.ExecuteData;
import core.util.Resamples;
import static forms.Function.db;
import static forms.Function.function;
import static forms.Function.workdb;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.UnsupportedAudioFileException;
import nn.BackpropNetwork;
import nn.Network;
import nn.SigmoidLayer;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import sound.WaveFile;

/**
 *
 * @author Shaplygin
 */
public class WorkDB extends javax.swing.JFrame {

    /**
     * Creates new form WorkDB
     */
    public WorkDB() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jSpinner1 = new javax.swing.JSpinner();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton1.setText("Загрузка команд в БД");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Рабиение выборок");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Назад");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel1.setText("Номер диктора");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jButton3)
                .addGap(21, 21, 21))
            .addGroup(layout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addComponent(jLabel1)
                .addGap(51, 51, 51)
                .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(64, 64, 64)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 332, Short.MAX_VALUE)
                .addComponent(jButton2)
                .addGap(121, 121, 121))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(143, 143, 143)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(101, 101, 101)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 123, Short.MAX_VALUE)
                .addComponent(jButton3)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String currPath  = System.getProperty("user.dir");
        javax.swing.JFileChooser fileCh = new javax.swing.JFileChooser(currPath); 
        fileCh.setMultiSelectionEnabled(true);
        WaveFile wf;
        int ret = fileCh.showDialog(null, "Открыть файл");      
        if (ret == fileCh.APPROVE_OPTION) {
            importFiles = fileCh.getSelectedFiles();
            try {
                long startTime = System.currentTimeMillis();
                db.openDB();
                //db.deleteAllCommands();
                for (int l = 0; l < importFiles.length; l++){
                    wf =  new WaveFile(importFiles[l]);
                    //System.out.println(wf.getAudioFormat());
                    short[] dataFromImport = wf.getData();
                    float[] FdataFromImport = new float[dataFromImport.length];
                
                    for(int i = 0;i<dataFromImport.length;i++){
                        FdataFromImport[i] = dataFromImport[i];
                    }
                    
                    float longSignal = (float) wf.getDurationTime();
                    float crossing  = Float.parseFloat(function.jTextField5.getText());
                    float freamLngth  = Float.parseFloat(function.jTextField6.getText());
                    Resamples rs = new Resamples(FdataFromImport, crossing, longSignal*100, freamLngth, false); 
                    float[][] resData = rs.count();
         
                    float sampleRate = 22050.0f; // для конкретной моей выборки
                    int amountOfCepstrumCoef = Integer.parseInt(function.jTextField1.getText());
                    int amountOfMelFilters = Integer.parseInt(function.jTextField9.getText());
                    float lowerFilterFreq = Float.parseFloat(function.jTextField10.getText());
                    float upperFilterFreq = Float.parseFloat(function.jTextField11.getText());
                
                    //Расчет MFCC коэфициетов для записи
                    MFCC mfcc;
                    float[] ot = new float [resData[0].length];
                    float[] fullOt =  new float[amountOfCepstrumCoef*resData.length]; //Конечный вектор MFCC коэфициентов фреймов
                    int c = 0;
        
                    for (int i = 0; i < resData.length; i++){
                        for (int j = 0; j < ot.length; j++)
                            ot[j] = resData[i][j];
                
                        mfcc = new MFCC(ot, ot.length,  sampleRate,  amountOfCepstrumCoef,  amountOfMelFilters,  lowerFilterFreq,  upperFilterFreq);
                        mfcc.processCount();
                        float[] mf1 = mfcc.getMFCC(); //вектор с MFCC коффициентами по окнам
                        for (int j = 0; j < mf1.length; j++){
                            fullOt[c] = mf1[j];
                            c++;
                        }   
                    }
                    // Нахождение команды и имя файл
                    String fileName = importFiles[l].getName();
                    fileName = fileName.toLowerCase();
                    int pos = fileName.indexOf(".");
                    String name = fileName.substring(0, pos);
                    pos = name.indexOf("_");
                    String nameCommand = name.substring(0,pos);
                    
                    // Загурзка данных в БД
                    //1 - номер диктора
                    db.addRecord(name, nameCommand, fullOt, 1);
                    //System.out.println(name+"    "+nameCommand);
                }
                db.closeDB();
                long spentTime = System.currentTimeMillis() - startTime;
                System.out.println("Загрузка завершена \nВремя записи выборки в БД  "+spentTime);
                } catch (UnsupportedAudioFileException | IOException | ClassNotFoundException |SQLException ex) {
                    Logger.getLogger(WorkDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        try {
            db.openDB();
            db.separateRecord();
            db.closeDB();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(WorkDB.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(WorkDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        workdb.setVisible(false);
        function.setVisible(true);
    }//GEN-LAST:event_jButton3ActionPerformed

    /**
     * @param args the command line arguments
     */
    private BackpropNetwork bpw = null;
    private double[] yData = new double[0];
    private double[] xData = new double[0];
    private File [] importFiles;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JSpinner jSpinner1;
    // End of variables declaration//GEN-END:variables
}
