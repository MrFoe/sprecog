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
import java.util.HashMap;
import java.util.Map;

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
      
    public static void appendSpeaker(int speakerId, String nameSpeaker, int sex) throws ClassNotFoundException, SQLException{
        //Проверка наличие appendSpeaker == id ОШИБКА В ЭТОМ МЕТОДЕ
        int id  = getMaxId("speakers");
        String sqlStr;
        if (id == 0 ){
            id++;
            sqlStr = "INSERT INTO speakers (id, name, sex) VALUES("+ id +",\'"+nameSpeaker +"\',"+ sex +")";
        }else {
            sqlStr = "select id from speakers where name = \'"+nameSpeaker+"\'";
            resSet = statmt.executeQuery(sqlStr);
            while (resSet.next())
                if (resSet.getInt("id") == speakerId)
                    id = resSet.getInt("id");
            if (id == getMaxId("speakers"))
                id++;
            sqlStr = "INSERT INTO speakers (id, name, sex) VALUES("+ id +",\'"+nameSpeaker +"\',"+ sex +")";
        }
        statmt.execute(sqlStr);
    }
    
    public static void addRecord(String nameFile, String nameComm,  float[] value, int speakerId, String nameSpeaker, String fileInfo) throws ClassNotFoundException,SQLException
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
                statmt.execute("INSERT INTO mfcc ('id','id_prop','mfcc_values','id_samples')"
                        + " VALUES ("+(id+i)+','+ id_prop + ',' + value[i]+','+ i + ")");
            }
            statmt.execute("INSERT INTO property_commands ('id','name','c_id', 's_id', 'prop_cmd')"
                    + " VALUES ("+id_prop+", \'"+nameFile+"\',"+exId+","+speakerId+", \'"+fileInfo+"\'"+")");
                
        }else{
            int idC = getMaxId("commands");
            idC++;
            statmt.execute("INSERT INTO commands ('id','name') VALUES ("+idC+","+"\'"+nameComm+"\')");
            int id = getMaxId("mfcc");
            id++;
            int id_prop = getMaxId("property_commands");
            id_prop++;
            for (int i = 0 ; i < value.length; i++){
                statmt.execute("INSERT INTO mfcc ('id','id_prop','mfcc_values','id_samples')"
                        + " VALUES ("+(id+i)+','+ id_prop + ',' + value[i]+','+ i + ")");
            }
            statmt.execute("INSERT INTO property_commands ('id','name','c_id','s_id', 'prop_cmd') VALUES ("+id_prop+
                    ", \'"+nameFile+"\',"+idC+','+speakerId+", \'"+fileInfo+'\''+")");
        }
	//statmt.execute("INSERT INTO 'commands' ('id','name') VALUES (13,'stroka')");
        //statmt.execute("INSERT INTO commands ('id','name') VALUES ("+id+", '"+str+"')");
    }
	
    public static void separateRecord(int percent) throws ClassNotFoundException, SQLException{
        
        //statmt.execute("delete from samples");
        //statmt.execute("delete from s_content");
        //flag - разбиение для какого алгорима верно - dtw
        int maxId = getMaxId("samples");
        int dtwIDl = ++maxId;
        statmt.execute("INSERT INTO samples ('id','name_alg','samples_type') VALUES ("+maxId+", \'"+"DTW"+"\',"+1+")");
        int dtwIDt = ++maxId;
        statmt.execute("INSERT INTO samples ('id','name_alg','samples_type') VALUES ("+maxId+", \'"+"DTW"+"\',"+2+")");
        int nwIDl = ++maxId;
        statmt.execute("INSERT INTO samples ('id','name_alg','samples_type') VALUES ("+maxId+", \'"+"Neuron"+"\',"+1+")");
        int nwIDt = ++maxId;
        statmt.execute("INSERT INTO samples ('id','name_alg','samples_type') VALUES ("+maxId+", \'"+"Neuron"+"\',"+2+")");
        int nwIDv = ++maxId;
        statmt.execute("INSERT INTO samples ('id','name_alg','samples_type') VALUES ("+maxId+", \'"+"Neuron"+"\',"+3+")");
        //
        int maxComm = getMaxId("commands");
        float p = percent;
        float TVpercent = (1-p/100)/2; // test and verif
        TVpercent += 0.00001;
        System.out.println(TVpercent);
        System.out.println((int)TVpercent*5);
        Map<Integer, String> hashmap = new HashMap<>(); // Для хранения номер айди значений для верификации и теста 
        for (int i = 1; i < maxComm+1; i++){
            resSet =  statmt.executeQuery("SELECT count(C_ID) From property_commands WHERE C_ID = "+i);
            int maxP = resSet.getInt("count(C_ID)");
            int cntSmpl = (int)(maxP*TVpercent);
            System.out.println(cntSmpl);
            //распределение номеров для тестовой выборки и верификации
            for (int j = 0; j < cntSmpl; j++){
                int valueTest = 0;
                int valueVerific = 0;
                valueTest = (int) (1 + Math.random()*maxP);
                if (hashmap.isEmpty())
                    hashmap.put(valueTest, "test");
                else{
                    while (hashmap.containsKey(valueTest))
                        valueTest = (int) (1 + Math.random()*maxP);
                    hashmap.put(valueTest, "test");
                }
                valueVerific = (int) (1 + Math.random()*maxP);
                while (hashmap.containsKey(valueVerific)){
                    valueVerific = (int) (1 + Math.random()*maxP);
                }
                hashmap.put(valueVerific, "verif");
            }
            int m = getMaxId("s_content");
            resSet =  statmt.executeQuery("SELECT * From property_commands WHERE C_ID = "+i);
            int res;
            int c = 1;
            while(resSet.next()){
                m++;
                res = resSet.getInt("ID");
                if (hashmap.containsKey(c)){
                    String str = hashmap.get(c);
                    if (str.compareTo("test") == 0 ){
                        // Заносим тестовые команды
                        stat.execute("INSERT INTO s_content ('id','s_id','c_id') VALUES ("+m+", "+dtwIDt+" ,"+res+")");
                        m++;
                        stat.execute("INSERT INTO s_content ('id','s_id','c_id') VALUES ("+m+", "+nwIDt+" ,"+res+")");
                    }
                    else if (str.compareTo("verif") == 0){
                        //Заносим команды верификации
                        stat.execute("INSERT INTO s_content ('id','s_id','c_id') VALUES ("+m+", "+nwIDv+" ,"+res+")");
                    }
                }
                else {
                    //Заносим обучающие команды
                    stat.execute("INSERT INTO s_content ('id','s_id','c_id') VALUES ("+m+", "+dtwIDl+" ,"+res+")");
                    m++;
                    stat.execute("INSERT INTO s_content ('id','s_id','c_id') VALUES ("+m+", "+nwIDl+" ,"+res+")");
                }
                c++;
            }
            hashmap.clear();
        }
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
	
    public static String getInfoSample(int samplesId) throws ClassNotFoundException, SQLException{
        //prototype method
        String sqlStr = "select pc.prop_cmd from property_commands pc"
                + " join s_content sc on sc.c_id = pc.id"
                + " join samples s on s.id = sc.s_id where s.id = "+ samplesId +" order by pc.id";
	resSet = statmt.executeQuery(sqlStr);
        String infoSample = resSet.getString("prop_cmd");
        return infoSample;
    }
    
    public static void deleteAllCommands() throws ClassNotFoundException,SQLException
    {
	String sqlStr = "delete from mfcc";
	statmt.execute(sqlStr);
        sqlStr = "delete from property_commands";
	statmt.execute(sqlStr);
        sqlStr = "delete from commands";
	statmt.execute(sqlStr);
        sqlStr = "delete from samples";
        statmt.execute(sqlStr);
        sqlStr = "delete from s_content";
        statmt.execute(sqlStr);
        sqlStr = "delete from speakers";
        statmt.execute(sqlStr);
    }
    
    public static void ap123() throws SQLException, ClassNotFoundException {
        String sqlStr= "INSERT INTO speakers ('id', 'name', 'sex') VALUES (2, 'Ярушко', 1)";
        
        statmt.execute(sqlStr);
    }
    
    public static void deleteSeparation() throws ClassNotFoundException, SQLException{
        String sqlStr = "delete from samples";
        statmt.execute(sqlStr);
        sqlStr = "delete from s_content";
        statmt.execute(sqlStr);
    }
    
    public static void editCommand(String strFrom,String strTo) throws ClassNotFoundException,SQLException
    {
        String sqlStr = "UPDATE 'commands' SET name = '"+strTo+"' WHERE name ='"+strFrom+"'"; 
        statmt.execute(sqlStr);
    }
        
    public static int getMaxId(String table) throws SQLException, ClassNotFoundException{
        int max_id = 0;
        String sqlStr = "SELECT MAX(id) FROM "+table;
        resSet = statmt.executeQuery(sqlStr);
        if (resSet.next())
            max_id = resSet.getInt("MAX(ID)");
        return max_id;
    }

    public static int maxLengthMfcc() throws ClassNotFoundException,SQLException{
        int maxValue = 0;
        String sqlStr = "select count(m.id), m.id_prop from mfcc m\n" +
                    "join property_commands pc on m.id_prop = pc.id\n" +
                    "group by m.id_prop order by 1 desc";
        resSet = statmt.executeQuery(sqlStr);
        if (resSet.next())
            maxValue = resSet.getInt("count(m.id)");
        return maxValue;
    }
    
    public static String getCommand(int ID)throws ClassNotFoundException,SQLException{
        String command = "";
        resSet = statmt.executeQuery("SELECT name FROM commands WHERE ID = "+ID);
        command = resSet.getString("name");
        return command;
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
    public static void closeDB() throws ClassNotFoundException,SQLException{
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

