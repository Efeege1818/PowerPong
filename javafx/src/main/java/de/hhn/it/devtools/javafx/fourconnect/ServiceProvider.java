package de.hhn.it.devtools.javafx.fourconnect;

import de.hhn.it.devtools.apis.fourconnect.ConnectFourService;
import de.hhn.it.devtools.components.fourconnect.provider.ConnectFourServiceImpl;  // ✅ wichtig

public final class ServiceProvider {
	private static ConnectFourService INSTANCE;

	private ServiceProvider() {
	}

	public static ConnectFourService getConnectFourService() {
		if (INSTANCE == null) {
			INSTANCE = new ConnectFourServiceImpl();
		}
		return INSTANCE;
	}
}
