package org.github.fukkit.patch2mixin;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import org.github.fukkit.patch2mixin.util.MultiOutputStream;
import java.io.*;
import java.util.Scanner;
import java.util.logging.Logger;

public class Patch2Mixin {
	private static final Logger LOGGER = Logger.getLogger("Patch2Mixin");
	public static boolean overwrite;
	public static void main(String[] args) {
		Config config = parseConfig(args);
		overwrite = config.overwrite;
		setLogFile(config.logFile);

	}

	public static void setLogFile(String path) {
		if(path != null) {
			File logFile = new File(path);
			if (logFile.exists()) {
				Scanner scanner = new Scanner(System.in);
				System.out.println("Log file already exists, overwrite? (y/n): ");
				String next = scanner.nextLine();
				if (!"yes".contains(next)) {
					System.out.println("Shutting down...");
					System.exit(0);
				}


				try (FileOutputStream out = new FileOutputStream(logFile)) {
					// set log
					PrintStream stream = System.out;
					System.setOut(new PrintStream(new MultiOutputStream(out, stream)));
				} catch (FileNotFoundException e) {
					System.out.println("Log file not found!");
					System.exit(0);
				} catch (IOException e) {
					System.out.println("Unable to write log file!");
					System.exit(0);
				}
			}
		}
	}
	public static Config parseConfig(String[] args) {
		File configFile = args.length == 0 ? new File("config.json") : new File(args[0]);
		Gson gson = new Gson();
		try (FileReader fileReader = new FileReader(configFile)) {
			// return only if no exception, or terminate
			return gson.fromJson(fileReader, Config.class);
		} catch (FileNotFoundException e) {
			System.out.println(configFile + "does not exist!");
		} catch (IOException | JsonIOException e) {
			System.out.println("Unable to read config!");
		} catch (JsonSyntaxException e) {
			System.out.println("Config is not valid json!");
		}
		System.exit(0);
		throw new IllegalStateException();
	}

}
