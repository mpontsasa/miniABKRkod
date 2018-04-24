//import com.sleepycat.je.Environment;

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
            controller.parseSQL("create table Tabla1 (egy int primary, ketto int, harom string)");

            controller.getActiveEnviornment().printTables();

            controller.parseSQL("insert into Tabla1 values (1,2, \"hrm\")");
            //controller.parseSQL("use szia");
            //System.out.println(controller.getSqlDatabaseStructure());


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
