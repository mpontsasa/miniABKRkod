import com.sleepycat.je.Environment;

import java.awt.image.renderable.ContextualRenderedImageFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class Main {

    public static void main(String[] args){

        Controller controller = new Controller();
        try
        {

            //controller.parseSQL("create database szia");
            controller.parseSQL("use szia");
            //controller.parseSQL("create table Tabla1 (nev string , kor int, id int primary, kedvencCsapat string, kedvencSzam int)");
            //controller.parseSQL("create table Tabla1 (nev string , kor int, id int primary, kedvencCsapat string, kedvencSzam int)");
            //controller.parseSQL("create table Tabla2 (nev string unique , kor int, id int primary, masikNev string references Tabla1.nev)");

//            controller.parseSQL("insert into Tabla1 values (\"Foszto MatyasOreg\",60, 60, \"ghetto masters\", 8547)");
//            controller.parseSQL("insert into Tabla1 values (\"Foszto MatyasBarca\",1, 1, \"barca\", 8547)");
//            controller.parseSQL("insert into Tabla1 values (\"Foszto Matyas30\",20, 30, \"ghetto masters\", 8547)");
//            controller.parseSQL("insert into Tabla1 values (\"Foszto Matyas25\",20, 77, \"ghetto masters\", 8547)");
//            controller.parseSQL("insert into Tabla1 values (\"Foszto Matyas25\",20, 25, \"ghetto masters\", 8547)");
//            controller.parseSQL("insert into Tabla1 values (\"Foszto Matyas Teszt Insert\",19, 88, \"barca\", 8547)");
//
//            controller.parseSQL("insert into Tabla2 values (\"Foszto MatyasOreg\",60, 60, \"Foszto MatyasOreg\")");
//            controller.parseSQL("insert into Tabla2 values (\"Foszto MatyasOreg2\",70, 70, \"Foszto MatyasOreg\")");
            //controller.getActiveEnviornment().printTables();


            //controller.parseSQL("insert into Tabla1 values (\"Foszto Matyas Teszt Insertrossz\",19, 88, \"barca\", 8547)");
            //controller.parseSQL("use szia");


            //System.out.println(controller.getSqlDatabaseStructure());

            //controller.parseSQL("create index testnev on Tabla1 kedvencCsapat");

            controller.getActiveEnviornment().getValueByKey("Tabla1", "77");
            controller.getActiveEnviornment().getValueByKey("Tabla1", "30");
            controller.getActiveEnviornment().getValueByKey("Tabla1", "25");
            controller.getActiveEnviornment().getValueByKey("Tabla1", "1");
            controller.getActiveEnviornment().getValueByKey("Tabla1","60");
            controller.getActiveEnviornment().getValueByKey("Tabla2","60");
            controller.getActiveEnviornment().getValueByKey("Tabla2","70");
            controller.getActiveEnviornment().getValueByKey(Finals.INDEX_FILE_NAME + "Tabla1_kedvencCsapat" , "ghetto masters");
            controller.getActiveEnviornment().getValueByKey(Finals.INDEX_FILE_NAME + "Tabla1_kedvencCsapat" , "barca");

            //controller.parseSQL("create table szia        (      szerussz       int unique  primary    ,     szia string    )");

           // controller.parseSQL("create index testnev on Tabla1 kedvencCsapat");

            //int a = Integer.parseInt("szia");

            controller.getActiveEnviornment().printTables();
            //System.out.println("Elso command vege");
            //controller.parseSQL("Create Table Table1");
            //controller.parseSQL("Create Table Table2");
            //controller.parseSQL("insert into Table1 values (alma, \"alma\"    ,   5    )");
            //controller.parseSQL("create database szia");


        }
        catch (InvalidSQLCommandException isqlce)
        {
            isqlce.printStackTrace();
            System.out.println(isqlce.msg);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}