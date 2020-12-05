package bgu.spl.mics.application;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Ewoks;
import bgu.spl.mics.application.passiveObjects.Input;
import bgu.spl.mics.application.passiveObjects.JsonInputReader;
import bgu.spl.mics.application.services.*;

import java.io.IOException;

/** This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {
	public static void main(String[] args) {
		MessageBusImpl messageBus = MessageBusImpl.getInstance();
		Ewoks givenVillage = Ewoks.getInstance(1);
		Attack[] attacks = {null};
		Thread leia = new Thread(new LeiaMicroservice(attacks));
		Thread han = new Thread(new HanSoloMicroservice(givenVillage));
		Thread c3po = new Thread(new C3POMicroservice(givenVillage));
		Thread r2d2 = new Thread(new R2D2Microservice(100));
		Thread lando = new Thread(new LandoMicroservice(100));


	}
}
