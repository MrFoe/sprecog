/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

/**
 *
 * @author Shaplygin
 */
import core.util.ExecuteData;
import java.sql.*;
import java.util.Arrays;

public class DB {

    public static Connection conn;
    public static Statement statmt;
    public static Statement stat;
    public static ResultSet resSet;
    public static ResultSet resSt;
	
    public DB() throws ClassNotFoundException,SQLException{ //Подключение Базы данны
        conn = null;
	statmt = null;
        stat = null;
	resSet = null;
		
    }
	
    public static void openDB() throws ClassNotFoundException, SQLException
        {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:SpeechCommands.s3db");
            statmt = conn.createStatement();
            
            stat = conn.createStatement();
        }
        
    public static void addRecord(String nameFile, String nameComm,  float[] value, int speakerId) throws ClassNotFoundException,SQLException
    {
        String sqlStr = "SELECT * FROM commands";
        //nameComm = nameComm.toLowerCase();
        resSet = statmt.executeQuery(sqlStr);
        boolean exist = false;
        int exId = 0;
        //System.out.println(nameComm);
        //System.out.println(nameFile);
        while(resSet.next()){
            if (nameComm.compareTo(resSet.getString("name")) == 0){
                exist = true;
                exId = resSet.getInt("id");
                break;
            }
        }
        //System.out.println(exist);
        if (exist){
            int id = getMaxId("mfcc");
            id++;
            int id_prop = getMaxId("property_commands");
            id_prop++;
            for (int i = 0 ; i < value.length; i++){
                statmt.execute("INSERT INTO mfcc ('id','id_prop','mfcc_values','id_samples') VALUES ("+(id+i)+','+ id_prop + ',' + value[i]+','+ i + ")");
            }
            statmt.execute("INSERT INTO property_commands ('id','name','c_id', 's_id') VALUES ("+id_prop+", \'"+nameFile+"\',"+exId+","+speakerId+")");
                
        }else{
            int idC = getMaxId("commands");
            idC++;
            statmt.execute("INSERT INTO commands ('id','name') VALUES ("+idC+","+"\'"+nameComm+"\')");
            int id = getMaxId("mfcc");
            id++;
            int id_prop = getMaxId("property_commands");
            id_prop++;
            for (int i = 0 ; i < value.length; i++){
                statmt.execute("INSERT INTO mfcc ('id','id_prop','mfcc_values','id_samples') VALUES ("+(id+i)+','+ id_prop + ',' + value[i]+','+ i + ")");
            }
            statmt.execute("INSERT INTO property_commands ('id','name','c_id','s_id') VALUES ("+id_prop+", \'"+nameFile+"\',"+idC+","+speakerId+")");
        }
	//statmt.execute("INSERT INTO 'commands' ('id','name') VALUES (13,'stroka')");
            //statmt.execute("INSERT INTO commands ('id','name') VALUES ("+id+", '"+str+"')");
    }
	
    public static void separateRecord() throws ClassNotFoundException, SQLException{
        
        statmt.execute("delete from samples");
        statmt.execute("delete from s_content");
        int maxId = getMaxId("samples");
        maxId++;
        statmt.execute("INSERT INTO samples ('id','name_alg','samples_type') VALUES ("+maxId+", \'"+"DTW"+"\',"+1+")");
        maxId++;
        statmt.execute("INSERT INTO samples ('id','name_alg','samples_type') VALUES ("+maxId+", \'"+"DTW"+"\',"+2+")");
        maxId++;
        int maxComm = getMaxId("commands");
        int value;
        //System.out.println(maxComm);
        for (int i = 1; i < maxComm+1; i++){
            resSet =  statmt.executeQuery("SELECT count(C_ID) From property_commands WHERE C_ID =="+i);
            int maxP = resSet.getInt("count(C_ID)");
            value = (int) (1 + Math.random()*maxP);
            int m = getMaxId("s_content");
            resSet =  statmt.executeQuery("SELECT * From property_commands WHERE C_ID == "+i);
            int res;
            int c = 1;
            while(resSet.next()){
                m++;
                res = resSet.getInt("ID");
                
                //System.out.println(res);
                if (c == value) 
                    stat.execute("INSERT INTO s_content ('id','s_id','c_id') VALUES ("+m+", "+2+" ,"+res+")");
                else
                    stat.execute("INSERT INTO s_content ('id','s_id','c_id') VALUES ("+m+", "+1+" ,"+res+")");
                c++;
            }
            //System.out.println();
            //System.out.println(i);
        }
        
        
        statmt.execute("INSERT INTO samples ('id','name_alg','samples_type') VALUES ("+maxId+", \'"+"Neuron"+"\',"+1+")");
        maxId++;
        statmt.execute("INSERT INTO samples ('id','name_alg','samples_type') VALUES ("+maxId+", \'"+"Neuron"+"\',"+2+")");
        maxId++;
        statmt.execute("INSERT INTO samples ('id','name_alg','samples_type') VALUES ("+maxId+", \'"+"Neuron"+"\',"+3+")");
        maxId++;
        
    }
    
    public static ExecuteData executeRecord(int smpl) throws ClassNotFoundException, SQLException{
        //Выбока тестовых коэфициентов
        float[][] sempl = new float[0][0];
        resSet = statmt.executeQuery("select count(*) from s_content s\n" +
                    "where s.s_id = "+smpl);
        int testN;
        testN = resSet.getInt("count(*)");
        int i = 0;
        resSt = statmt.executeQuery("select count(m.id), m.id_prop, pc.c_id from mfcc m\n" +
                    "join property_commands pc on m.id_prop = pc.id\n" +
                    "join s_content sc on sc.c_id = pc.id\n" +
                    "where sc.s_id = "+smpl+" group by m.id_prop, pc.c_id order by m.id_prop");
        sempl = new float [testN][];
        int[] commands_id = new int[testN];
        while(resSt.next()){
            int testM;
            testM =  resSt.getInt("count(m.id)");
            sempl[i] = new float [testM];
            
            commands_id[i] = resSt.getInt("c_id");
            int idComm;
            idComm = resSt.getInt("id_prop");
            resSet = stat.executeQuery("select m.* from mfcc m\n" +
                    "join property_commands pc on m.id_prop = pc.id\n" +
                    "join s_content sc on sc.c_id = pc.id\n" +
                    "where sc.s_id = "+smpl+" order by m.id_prop");
            int j = 0;
            int idC = 0;
            while(resSet.next()){
                idC = resSet.getInt("id_prop");
                if ((idC == idComm) && (j < testM)){
                    sempl[i][j] = resSet.getFloat("mfcc_values");
                    j++;
                }
            }
            i++;
        }
        ExecuteData exData = new ExecuteData(sempl, commands_id);
        
        return exData;
    }
	
    public static void deleteAllCommands() throws ClassNotFoundException,SQLException
    {
	String sqlStr = "delete from mfcc";
	statmt.execute(sqlStr);
        sqlStr = "delete from property_commands";
	statmt.execute(sqlStr);
        sqlStr = "delete from commands";
	statmt.execute(sqlStr);
    }
	
    public static void editCommand(String strFrom,String strTo) throws ClassNotFoundException,SQLException
    {
        String sqlStr = "UPDATE 'commands' SET name = '"+strTo+"' WHERE name ='"+strFrom+"'"; 
        statmt.execute(sqlStr);
    }
        
    private static int getMaxId(String table) throws SQLException, ClassNotFoundException{
        int max_id;
        String sqlStr = "SELECT MAX(id) FROM "+table;
        resSet = statmt.executeQuery(sqlStr);
        max_id = resSet.getInt("MAX(ID)");
        return max_id;
    }
    
    public static int maxLengthMfcc() throws ClassNotFoundException,SQLException{
        int maxValue;
        String sqlStr = "select count(m.id), m.id_prop from mfcc m\n" +
                    "join property_commands pc on m.id_prop = pc.id\n" +
                    "group by m.id_prop order by 1 desc";
        resSet = statmt.executeQuery(sqlStr);
        maxValue = resSet.getInt("count(m.id)");
        return maxValue;
    }
    
    public static String[] getCommands() throws ClassNotFoundException,SQLException
    {
        int id = getMaxId("commands"); 
        String [] commands = new String [0];
        if (id != 0){
            commands = new String[id];
            int i = 0;
                resSet = statmt.executeQuery("SELECT * FROM commands");
                while(resSet.next()){
                    commands[i] = resSet.getString("name");
                    i++;
                }
            }  
            return commands;       
	}
	public static int minLengthMfcc()throws SQLException, ClassNotFoundException{
            int minValue;
            String sqlStr = "select count(m.id), m.id_prop from mfcc m\n" +
                    "join property_commands pc on m.id_prop = pc.id\n" +
                    "group by m.id_prop order by 1";
            resSet = statmt.executeQuery(sqlStr);
            minValue = resSet.getInt("count(m.id)");
            return minValue;
        }
        
    public static int countCommands()throws SQLException, ClassNotFoundException{
        int cComm;
            
        String sqlStr = "SELECT count(id) FROM commands";
        resSet = statmt.executeQuery(sqlStr);
        cComm = resSet.getInt("count(id)");
        return cComm;
    }
	//Закрытие Базы данны
	public static void closeDB() throws ClassNotFoundException,SQLException
	{
            conn.close();
            if (statmt != null){
                statmt.close();  
            }
            if (statmt != null){
                stat.close(); 
            }
            if (resSet != null)
		resSet.close();
            if (resSt != null)
		resSt.close();
	}
}

