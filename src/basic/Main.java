package basic;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Database db = new Database();
        QueryParser parser = new QueryParser(db);
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter SQL queries (type 'EXIT' to quit):");

        while (true) {
            System.out.print("SQL> ");
            String query = scanner.nextLine();

            if (query.equalsIgnoreCase("EXIT")) {
                System.out.println("Exiting...");
                break;
            }

            try {
                parser.parseQuery(query);
            } catch (Exception e) {
                System.out.println("Error executing query: " + e.getMessage());
            }
        }

        scanner.close();
    }
}
