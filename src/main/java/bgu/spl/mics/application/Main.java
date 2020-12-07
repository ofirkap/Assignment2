package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;

/**
 * This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {
    public static void main(String[] args) {

        Input myInput = null;
        try {
            myInput = JsonInputReader.getInputFromJson("/home/spl211/IdeaProjects/Assignment2/input.json");
        } catch (IOException e) {
            System.out.println("Input not found");
            System.exit(0);
        }

        Ewoks village = Ewoks.getInstance();
        village.setEwoksVillage(myInput.getEwoks());
        Attack[] attacks = myInput.getAttacks();
        Thread leia = new Thread(new LeiaMicroservice(attacks), "Leia");
        Thread han = new Thread(new HanSoloMicroservice(), "HanSolo");
        Thread c3po = new Thread(new C3POMicroservice(), "C3PO");
        Thread r2d2 = new Thread(new R2D2Microservice(myInput.getR2D2()), "R2D2");
        Thread lando = new Thread(new LandoMicroservice(myInput.getLando()), "Lando");

        Diary myDiary = Diary.getInstance();

        leia.start();
        han.start();
        c3po.start();
        r2d2.start();
        lando.start();

        try {
            leia.join();
            han.join();
            c3po.join();
            r2d2.join();
            lando.join();
        } catch (InterruptedException ignored) {}

        Gson myOutput = new GsonBuilder().setPrettyPrinting().create();
        try {
            FileWriter writer = new FileWriter("/home/spl211/IdeaProjects/Assignment2/Output.json");
            myOutput.toJson(myDiary, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.out.println("Output not found");
            System.exit(0);
        }
    }
}
