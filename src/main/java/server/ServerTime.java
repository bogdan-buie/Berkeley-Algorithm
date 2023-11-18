package server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalTime;


public interface ServerTime extends Remote {

	/**
	 * @return ora locala
	 */
	LocalTime getLocalTime() throws RemoteException;

	/**
	 * Ajusteaza ora locala in functie de diferenta medie dintre orele locale ale masinilor
	 */
	void adjustTime(LocalTime localTime, long avgDiff) throws RemoteException;
}