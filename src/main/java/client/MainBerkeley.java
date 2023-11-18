package client;

import static common.AppConstants.formatter;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalTime;

import common.AppConstants;
import server.ServerTime;
import server.ServerTimeImpl;


public class MainBerkeley {

	public static void main(String[] args) {
		try {
			LocalTime localTime = LocalTime.parse(AppConstants.LOCAL_HOUR, formatter);
			System.out.println("Ora locala: " + formatter.format(localTime));

			// se ia legatura prin RMI cu cele 3 masini care ruleaza (a se vedea metoda "createMachineServer")
			// urmatorele 3 obiecte sunt doar o referinta la obiectele care exista pe cele 3 masini
			ServerTime machine1Server = createMachineServer(1);
			ServerTime machine2Server = createMachineServer(2);
			ServerTime machine3Server = createMachineServer(3);

			// determinarea orei medii pe cele 3 servere
			// obtinerea orei locale se face prin RMI
			var avgDiff = generateAverageTime(localTime,
					machine1Server.getLocalTime(),
					machine2Server.getLocalTime(),
					machine3Server.getLocalTime());

			// ajustarea orei locale pe cele 3 masini folosind RMI
			machine1Server.adjustTime(localTime, avgDiff);
			machine2Server.adjustTime(localTime, avgDiff);
			machine3Server.adjustTime(localTime, avgDiff);

			//ajustarea orei locale pe serverul central
			localTime = localTime.plusNanos(avgDiff);

			//Dupa ajustarea orei pe server si pe celelalte masini se mai obtine inca o data prin RMI ora acelor masini
			System.out.println("\nOra este actualizata!");
			System.out.println("Ora locala: " + formatter.format(localTime));
			System.out.println("Ora pe server 1: " + formatter.format(machine1Server.getLocalTime()));
			System.out.println("Ora pe server 2: " + formatter.format(machine2Server.getLocalTime()));
			System.out.println("Ora pe server 3: " + formatter.format(machine3Server.getLocalTime()));
		} catch (Exception ex) {
			System.out.println(ex);
		}
	}

	private static ServerTime createMachineServer(int machineNumber) throws Exception {

		// cautarea portului pe care ruleaza masina cu numarul "machineNumber"
		String serverName = AppConstants.SERVER_NAME;
		int serverPort = switch (machineNumber) {
			case 1 -> AppConstants.SERVER_PORT_1;
			case 2 -> AppConstants.SERVER_PORT_2;
			case 3 -> AppConstants.SERVER_PORT_3;
			default -> -1;
		};

		// se ia legatura prin RMI cu masina care ruleaza pe un anumit ip(localhost) si port
		// De fapt se obtine referinta la registrul aflat pe masina care ruleaza pe un anumit ip(localhost) si port
		Registry machineRegistry = LocateRegistry.getRegistry(serverName, serverPort);
		// din acel registru se obtine referinta la obiectul serverTime pe care registrul de masina respectiva ni-l pune la dispozitie
		ServerTime machineServerTime = (ServerTime) machineRegistry.lookup(ServerTimeImpl.class.getSimpleName());

		//prin RMI obtinem ora locala a acelei masini
		LocalTime machineTime = machineServerTime.getLocalTime();
		System.out.println("Conexiunea la masina " + machineNumber + " efectuata cu succes. Ora pe aceasta masina este: "
				+ formatter.format(machineTime));
		return machineServerTime;
	}

	/**
	 * @param localTime ora locala pe serverul central
	 * @param times     lista orelor locale de pe fiecare masina
	 * @return dierenta medie calculata a orelor
	 */
	private static long generateAverageTime(LocalTime localTime, LocalTime... times) {
		long nanoLocal = localTime.toNanoOfDay();	// calularea orei locale (primita ca parametru) in nanosecunde

		// calcularea dierentelor dintre ora locala a serverului si ora pe fiecare masina
		// suma acestor diferenete este impartita la numarul acestora pentru a afla
		// diferenta medie
		long difServer = 0;
		for (LocalTime t : times) {
			difServer += t.toNanoOfDay() - nanoLocal;
		}
		return difServer / times.length;
	}

}