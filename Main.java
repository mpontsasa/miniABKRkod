import com.sleepycat.je.Environment;

import java.awt.image.renderable.ContextualRenderedImageFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Main {

    public static void main(String[] args){

        Controller controller = new Controller();
        try
        {

            //controller.parseSQL("create database szia");
            controller.parseSQL("use szia");
            //controller.parseSQL("create table Tabla1 (nev string , kor int, id int primary, kedvencCsapat string, kedvencSzam int)");
            //controller.parseSQL("create table Tabla1 (nev string , kor int, id int primary, kedvencCsapat string, kedvencSzam int)");

            //controller.getActiveEnviornment().printTables();

            //controller.parseSQL("insert into Tabla1 values (\"Foszto Matyas\",20, 77, \"ghetto masters\", 8547)");
            //controller.parseSQL("insert into Tabla1 values (\"Foszto Matyas30\",20, 30, \"ghetto masters\", 8547)");
            //controller.parseSQL("insert into Tabla1 values (\"Foszto Matyas25\",20, 25, \"ghetto masters\", 8547)");
            //controller.parseSQL("use szia");
            //System.out.println(controller.getSqlDatabaseStructure());

            controller.getActiveEnviornment().getValueByKey("Tabla1", "77");
            controller.getActiveEnviornment().getValueByKey("Tabla1", "30");
            controller.getActiveEnviornment().getValueByKey("Tabla1", "25");
            //controller.parseSQL("create table szia        (      szerussz       int unique  primary    ,     szia string    )");




            //int a = Integer.parseInt("szia");

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