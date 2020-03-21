import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SystemDependency {
	enum COMMAND {
		DEPEND, INSTALL, REMOVE, LIST, END
	}

	private static Map<String, Software> allSoftwares = new HashMap<>();

	private static List<Software> installedSoftwares = new ArrayList<>();
	
	private static String string = "INS";
	private static String stringRem = "REM";
	private static String stringList = "LIS";
	
	public static void main(String[] args) {
		String[] inp = new String[] { "DEPEND   TELNET TCPIP NETCARD", "DEPEND TCPIP NETCARD",
				"DEPEND DNS TCPIP NETCARD", "DEPEND  BROWSER   TCPIP  HTML", "INSTALL NETCARD", "INSTALL TELNET",
				"INSTALL foo", "REMOVE NETCARD", "INSTALL BROWSER", "INSTALL DNS", "LIST", "REMOVE TELNET",
				"REMOVE NETCARD", "REMOVE DNS", "REMOVE NETCARD", "INSTALL NETCARD", "REMOVE TCPIP", "REMOVE BROWSER",
				"REMOVE TCPIP", "END", };
		performOperations(inp);
	}

	private static void performOperations(String[] input) {
		for (String inp : input) {
			System.out.println(inp);

			final String[] commandTokens = inp.split("\\s+");
			final String commandString = commandTokens[0];
			final COMMAND command = COMMAND.valueOf(commandString);

			switch (command) {
			case DEPEND:
				final String softwareName = commandTokens[1];
				buildDependencies(softwareName, commandTokens);
				break;
			case INSTALL:
				final Software softwareToBeInstalled = getSoftware(commandTokens[1]);
				if (isAlreadyInstalled(softwareToBeInstalled)) {
					System.out.println(getWhiteSpace(string.length()) + softwareToBeInstalled + " is already installed.");
				} else {
					// Install dependencies first
					final List<Software> softwareDependenciesToBeInstalled = softwareToBeInstalled.getDependencies();
					for (Software softwareDependency : softwareDependenciesToBeInstalled) {
						if (!isAlreadyInstalled(softwareDependency)) {
							install(softwareDependency);
						}
					}
					// Once the dependencies are installed, install the software
					install(softwareToBeInstalled);
				}
				break;
			case REMOVE:
				final Software softwareToBeRemoved = getSoftware(commandTokens[1]);
				if (!isAlreadyInstalled(softwareToBeRemoved)) {
					System.out.println(getWhiteSpace(stringRem.length()) + softwareToBeRemoved + " is not installed.");
				} else if (canRemoveSoftware(softwareToBeRemoved)) {
					System.out.println(getWhiteSpace(stringRem.length()) + "Removing " + softwareToBeRemoved);
					installedSoftwares.remove(softwareToBeRemoved);
					final List<Software> currentSoftwareDependencies = softwareToBeRemoved.getDependencies();
					for (Software dependency : currentSoftwareDependencies) {
						if (canRemoveSoftware(dependency)) {
							System.out.println(getWhiteSpace(stringRem.length()) + "Removing " + dependency);
							installedSoftwares.remove(dependency);
						}
					}
				} else {
					System.out.println(getWhiteSpace(stringRem.length()) + softwareToBeRemoved + " is still needed.");
				}
				break;
			case LIST:
				for (Software installedSoftware : installedSoftwares) {
					System.out.println(getWhiteSpace(stringList.length()) + installedSoftware);
				}
				break;
			case END:
				break;
			}
		}
	}

	private static void buildDependencies(String softwareName, String[] commandTokens) {
		// The dependencies of the current command are available from 3rd position
		// onwards
		for (int i = 2; i < commandTokens.length; i++) {
			final String currentDependency = commandTokens[i];
			final List<Software> dependenciesOfdependency = getSoftware(currentDependency).getDependencies();

			if (dependenciesOfdependency != null && dependenciesOfdependency.contains(getSoftware(softwareName))) {
				// do nothing
			} else {
				getSoftware(softwareName).addDependencies(getSoftware(currentDependency));
			}
		}
	}
	
	private static Software getSoftware(String name) {
		Software software = allSoftwares.get(name);
		if (software == null) {
			software = new Software(name);
			allSoftwares.put(name, software);
		}
		return software;
	}
	
	private static boolean isAlreadyInstalled(Software softwareToBeInstalled) {
		return installedSoftwares.contains(softwareToBeInstalled);
	}

	private static void install(Software software) {
		System.out.println(getWhiteSpace(string.length()) + "Installing " + software);
		installedSoftwares.add(software);
	}

	private static boolean canRemoveSoftware(Software softwareToBeRemoved) {
		for (Software installedSoftware : installedSoftwares) {
			final List<Software> requiredDependencies = installedSoftware.getDependencies();
			if (requiredDependencies != null) {
				for (Software dependency : requiredDependencies) {
					if (softwareToBeRemoved.equals(dependency)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	private static String getWhiteSpace(int size) {
		StringBuilder builder = new StringBuilder(size);
		for (int i = 0; i < size; i++) {
			builder.append(' ');
		}
		return builder.toString();
	}
	
}
