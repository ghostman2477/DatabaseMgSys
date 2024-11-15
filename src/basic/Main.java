package basic;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            // Define schema for the table
            List<String> schema = Arrays.asList("id", "name", "age","phone");

            // Create a table called "users"
            Table usersTable = new Table("users", schema);

            // Initialize the query parser for the table
            QueryParser parser = new QueryParser(usersTable);

            // Set up the console input loop
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter SQL queries (type 'EXIT' to quit):");

            while (true) {
                // Prompt for user input
                System.out.print("SQL> ");
                String query = scanner.nextLine();

                // Check for exit command
                if (query.equalsIgnoreCase("EXIT")) {
                    System.out.println("Exiting...");
                    break;
                }

                // Parse and execute the query
                try {
                    parser.parseQuery(query);
                } catch (Exception e) {
                    System.out.println("Error executing query: " + e.getMessage());
                }
            }

            scanner.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}