package com.example;

import java.io.*;
import java.util.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

public class VariableManager {

    static final String helferFile = "./Server/Info/Helfer.json";
    static final String mitgliederFile = "./Login/Mitglieder.json";
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            showMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> addSession();
                case "2" -> showSessions();
                case "3" -> editSession();
                case "4" -> deleteSession();
                case "5" -> {
                    System.out.println("👋 Beendet.");
                    return;
                }
                case "clear", "cls" -> clearConsole(); // versteckte Option
                default -> System.out.println("❌ Ungültige Eingabe.");
            }
        }
    }

    static void showMenu() {
        System.out.println("\n📋 Menü:");
        System.out.println("1 - Neue Sitzung erstellen");
        System.out.println("2 - Alle Sitzungen anzeigen");
        System.out.println("3 - Sitzung bearbeiten");
        System.out.println("4 - Sitzung löschen");
        System.out.println("5 - Beenden");
        System.out.print("> ");
    }

    static void addSession() {
        Map<String, String> session = inputSession();
        if (!session.isEmpty()) {
            List<Map<String, String>> helferList = loadJsonArray(helferFile);
            List<Map<String, String>> mitgliederList = loadJsonArray(mitgliederFile);

            helferList.add(session);
            mitgliederList.add(session);

            saveJsonArray(helferFile, helferList);
            saveJsonArray(mitgliederFile, mitgliederList);

            System.out.println("✅ Sitzung gespeichert.");
        }
    }

    static void showSessions() {
        List<Map<String, String>> sessions = loadJsonArray(helferFile);
        if (sessions.isEmpty()) {
            System.out.println("📭 Keine Sitzungen gefunden.");
        } else {
            int i = 1;
            for (Map<String, String> s : sessions) {
                System.out.println("\n🔹 Sitzung " + i++);
                s.forEach((k, v) -> System.out.println(k + ": " + v));
            }
        }
    }

    static void editSession() {
        List<Map<String, String>> sessions = loadJsonArray(helferFile);

        System.out.print("Name der Sitzung zum Bearbeiten: ");
        String name = scanner.nextLine().trim();

        boolean found = false;
        for (Map<String, String> session : sessions) {
            if (session.get("name").equalsIgnoreCase(name)) {
                System.out.print("Neuer Name (" + session.get("name") + "): ");
                session.put("name", scanner.nextLine().trim());
                System.out.print("Neue Rolle (" + session.get("rolle") + "): ");
                session.put("rolle", scanner.nextLine().trim());
                System.out.print("Neues Passwort (" + session.get("password") + "): ");
                session.put("password", scanner.nextLine().trim());
                System.out.print("Neuer Rang (" + session.get("rang") + "): ");
                session.put("rang", scanner.nextLine().trim());

                saveJsonArray(helferFile, sessions);
                saveJsonArray(mitgliederFile, sessions);
                System.out.println("✅ Sitzung aktualisiert.");
                found = true;
                break;
            }
        }

        if (!found) {
            System.out.println("❌ Keine Sitzung mit diesem Namen gefunden.");
        }
    }

    static void deleteSession() {
        List<Map<String, String>> sessions = loadJsonArray(helferFile);

        System.out.print("Name der Sitzung zum Löschen: ");
        String name = scanner.nextLine().trim();

        boolean removed = sessions.removeIf(s -> s.get("name").equalsIgnoreCase(name));

        if (removed) {
            saveJsonArray(helferFile, sessions);
            saveJsonArray(mitgliederFile, sessions);
            System.out.println("🗑️ Sitzung gelöscht.");
        } else {
            System.out.println("❌ Keine Sitzung mit diesem Namen gefunden.");
        }
    }

    static Map<String, String> inputSession() {
        Map<String, String> session = new LinkedHashMap<>();

        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Rolle: ");
        String rolle = scanner.nextLine().trim();
        System.out.print("Passwort: ");
        String password = scanner.nextLine().trim();
        System.out.print("Rang: ");
        String rang = scanner.nextLine().trim();

        System.out.println("\n📝 Zusammenfassung:");
        System.out.println("Name: " + name);
        System.out.println("Rolle: " + rolle);
        System.out.println("Passwort: " + password);
        System.out.println("Rang: " + rang);
        System.out.print("Sitzung speichern? (j/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (confirm.equals("j")) {
            session.put("name", name);
            session.put("rolle", rolle);
            session.put("password", password);
            session.put("rang", rang);
            return session;
        } else {
            return Collections.emptyMap();
        }
    }

    static List<Map<String, String>> loadJsonArray(String fileName) {
        try (Reader reader = new FileReader(fileName)) {
            return new Gson().fromJson(reader, new TypeToken<List<Map<String, String>>>() {}.getType());
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    static void saveJsonArray(String fileName, List<Map<String, String>> jsonArray) {
        try (Writer writer = new FileWriter(fileName)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(jsonArray, writer);
        } catch (IOException e) {
            System.out.println("❌ Fehler beim Speichern: " + fileName);
        }
    }

    static void clearConsole() {
        try {
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            System.out.println("❌ Fehler beim Leeren der Konsole.");
        }
    }
}