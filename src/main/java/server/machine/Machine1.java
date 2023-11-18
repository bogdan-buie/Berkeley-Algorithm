package server.machine;

import static common.AppConstants.formatter;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalTime;

import common.AppConstants;
import server.ServerTime;
import server.ServerTimeImpl;

public class Machine1 {

	public static void main(String[] args) {
		try {
			// obtinerea orei locale pe masina 1
			LocalTime hour = LocalTime.parse(AppConstants.MACHINE_1_HOUR, formatter);

			// creare obiect ServerTime care are rolul de a gestiona ora locala pe masina
			// se seteaza ora locala cu cea obtinuta mai sus
			ServerTime machineServer = new ServerTimeImpl(hour);

			// creare registru
			Registry registry = LocateRegistry.createRegistry(AppConstants.SERVER_PORT_1);

			//fac diponibil obiectul machineServer pentru acces la distanta prin RMI
			registry.rebind(ServerTimeImpl.class.getSimpleName(), machineServer);

			System.out.println(String.format("Masina 1 a fost pornita pe portul %s [Ora locala este: %s].",
					AppConstants.SERVER_PORT_1,
					AppConstants.formatter.format(hour)));
		} catch (Exception ex) {
			System.out.println(ex);
		}
	}

}
