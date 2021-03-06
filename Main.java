import com.sleepycat.je.Environment;

import java.awt.image.renderable.ContextualRenderedImageFactory;
import java.util.*;

public class Main {



    public static void userInterraction(Controller controller) throws Exception {

        System.out.println("Isten hozott a Sasa és Matyi fantsztikusan hatékony full table scan mentes adatbázis kezelő rendszerébe!");

        System.out.println("Adatbázisaink:");
        controller.printDatabases();


        System.out.println("A szia adatbázis táblái(éppen a szia adatbázist használod):");
        controller.parseSQL("use szia");
        controller.getActiveEnviornment().printTables();

        Scanner scanner = new Scanner(System.in);
        String userCommand = "";
        while (!(userCommand = scanner.nextLine()).equalsIgnoreCase("exit")) {

            try {
                controller.parseSQL(userCommand);
            } catch (Exception e) {
                System.out.println("Ez a parancs hibas :(...");
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args){


        Controller controller = new Controller();
        try
        {


            if(false){

                controller.parseSQL("create database szia");

                controller.parseSQL("create table Tabla1 (nev string , kor int, id int primary, kedvencCsapat string, kedvencSzam int)");
                controller.parseSQL("insert into Tabla1 values (\"Foszto MatyasOreg\",60, 60, \"ghetto masters\", 8547)");
                controller.parseSQL("insert into Tabla1 values (\"Foszto MatyasBarca\",1, 1, \"barca\", 8547)");
                controller.parseSQL("insert into Tabla1 values (\"Foszto Matyas30\",20, 30, \"ghetto masters\", 8547)");
                controller.parseSQL("insert into Tabla1 values (\"Foszto MatyasTeszteliAzIndexBeszurast\",67, 67, \"ghetto masters\", 8547)");
                controller.parseSQL("insert into Tabla1 values (\"Foszto Matyas77\",20, 77, \"ghetto masters\", 8547)");
                controller.parseSQL("insert into Tabla1 values (\"Foszto Matyas3\",3, 3, \"ghetto masters\", 3333)");

                controller.parseSQL("create index testnev on Tabla1 kedvencCsapat");

                controller.parseSQL("create table Tabla2 (alma string primary, korte string, dbszam int)");
                controller.parseSQL("insert into Tabla2 values (\"ElsoAlma\", \"barca\", 60)");
                controller.parseSQL("insert into Tabla2 values (\"MasodikAlma\", \"ghetto masters\", 20)");

                controller.parseSQL("create table Tabla3 (szo string, szam int primary)");
                controller.parseSQL("insert into Tabla3 values (\"T3/1sz=60\", 60)");
                controller.parseSQL("insert into Tabla3 values (\"T3/2sz=20\", 20)");



            }
            else {
                //controller.parseSQL("use szia");

                userInterraction(controller);
                

            }



            //controller.parseSQL("create table Tabla1 (nev string , kor int, id int primary, kedvencCsapat string, kedvencSzam int)");
            //controller.parseSQL("create table Tabla2 (nev string unique , kor int, id int primary, masikNev string references Tabla1.nev)");
            //controller.parseSQL("insert into Tabla1 values (\"Foszto Matyas25\",20, 45, \"ghetto masters\", 8547)");
            //controller.parseSQL("insert into Tabla1 values (\"Foszto Matyas Teszt Insert\",19, 88, \"barca\", 8547)");
            //controller.parseSQL("insert into Tabla1 values (\"Foszto Matyas CFR Cluj\",19, 56, \"CFR Cluj\", 8547)");
//
//           controller.parseSQL("insert into Tabla2 values (\"Foszto MatyasOreg\",60, 60, \"Foszto MatyasOreg\")");
            //          controller.parseSQL("insert into Tabla2 values (\"Foszto MatyasOreg2\",70, 70, \"Foszto MatyasOreg\")");
            //controller.getActiveEnviornment().printTables();


            //controller.parseSQL("insert into Tabla1 values (\"Foszto Matyas Teszt Insertrossz\",19, 98, \"barca\", 8547)");
            //controller.parseSQL("use szia");


            //System.out.println(controller.getSqlDatabaseStructure());




            /*controller.getActiveEnviornment().printDataByKey("Tabla1", "77");
            controller.getActiveEnviornment().printDataByKey("Tabla1", "30");
            controller.getActiveEnviornment().printDataByKey("Tabla1", "25");
            controller.getActiveEnviornment().printDataByKey("Tabla1", "1");
            controller.getActiveEnviornment().printDataByKey("Tabla1","60");
            //controller.getActiveEnviornment().printDataByKey("Tabla2","60");
            //controller.getActiveEnviornment().printDataByKey("Tabla2","70");
            controller.getActiveEnviornment().printDataByKey(Finals.INDEX_FILE_NAME + "Tabla1_kedvencCsapat" , "ghetto masters");
            controller.getActiveEnviornment().printDataByKey(Finals.INDEX_FILE_NAME + "Tabla1_kedvencCsapat" , "barca");
            controller.getActiveEnviornment().printDataByKey(Finals.INDEX_FILE_NAME + "Tabla1_kedvencCsapat" , "CFR Cluj");
            //controller.parseSQL("create table szia        (      szerussz       int unique  primary    ,     szia string    )");*/

           // controller.parseSQL("create index testnev on Tabla1 kedvencCsapat");

            //int a = Integer.parseInt("szia");

            //controller.getActiveEnviornment().printTables();
            //System.out.println("Elso command vege");
            //controller.parseSQL("Create Table Table1");
            //controller.parseSQL("Create Table Table2");
            //controller.parseSQL("insert into Table1 values (alma, \"alma\"    ,   5    )");
            //controller.parseSQL("create database szia");


        }
        catch (InvalidSQLCommandException isqlce)
        {
            //isqlce.printStackTrace();
            //System.out.println(isqlce.msg);
            System.out.println("Ez a parancs hibas :(..");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}