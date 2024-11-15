package basic;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class QueryParser {

    private Table table;
    
    private Set<String> phoneNumbers = new HashSet<>(); 

    public QueryParser(Table table) {
        this.table = table;
    }

    // Parse the query and call appropriate methods
    public void parseQuery(String query) throws IOException {
        query = query.trim().toUpperCase();

        if (query.startsWith("INSERT INTO")) {
            handleInsertQuery(query);
        } else if (query.startsWith("SELECT")) {
            handleSelectQuery(query);
        } else if (query.startsWith("DELETE FROM")) {
            handleDeleteQuery(query);
        } else if (query.startsWith("UPDATE")) {
            handleUpdateQuery(query);
        } else {
            System.out.println("Unknown command: " + query);
        }
    }

   
    public void handleInsertQuery(String query) throws IOException {
        // Extract the values from the query
        query = query.trim().replace("insert into " + table.getTableName() + " values", "").trim();
        query = query.substring(1, query.length() - 1); // Remove the parentheses
        String[] values = query.split(",");

        // Trim values for safety
        for (int i = 0; i < values.length; i++) {
            values[i] = values[i].trim().replace("'", "");
        }

        List<String> row = List.of(values); // Convert to a list of values

        // Validate phone number format (this can be in the QueryParser)
        String phone = row.get(table.getSchema().indexOf("phone"));
        phone = phone.trim();  // Ensure no leading or trailing spaces
        phone = phone.replaceAll("[^\\d]", "");  // Remove any non-digit characters
        System.out.println("Phone after cleaning: '" + phone + "'");

        if (!phone.matches("^(\\(?\\d{4}\\)?[-\\s]?\\d{3}[-\\s]?\\d{3}|\\d{10})$")) {
            System.out.println("Invalid phone number format.");
            return;
        }
        // Check if phone number is unique
        if (!table.isPhoneNumberUnique(phone)) {
            System.out.println("Error executing query: Phone number must be unique.");
            return; // Return without inserting if phone number is not unique
        }

        // If phone number is valid and unique, insert the row into the table
        table.insertRow(row);
        System.out.println("Row inserted successfully!");
    }


    // Handle SELECT query with or without WHERE clause
    private void handleSelectQuery(String query) throws IOException {
        if (query.startsWith("SELECT * FROM " + table.getTableName().toUpperCase())) {
            if (query.contains("WHERE")) {
                String[] parts = query.split("WHERE");
                String condition = parts[1].trim();

                String[] conditionParts = condition.split("=");
                if (conditionParts.length != 2) {
                    System.out.println("Invalid WHERE condition.");
                    return;
                }

                String column = conditionParts[0].trim();
                String value = conditionParts[1].trim().replace("'", "");

                table.printFilteredTable(column, value);
            } else {
                table.printTable();
            }
        } else {
            System.out.println("Invalid SELECT query.");
        }
    }

    // Handle DELETE query with WHERE clause
    private void handleDeleteQuery(String query) throws IOException {
        if (!query.startsWith("DELETE FROM " + table.getTableName().toUpperCase())) {
            System.out.println("Invalid DELETE query.");
            return;
        }

        if (!query.contains("WHERE")) {
            System.out.println("DELETE requires a WHERE clause.");
            return;
        }

        String[] parts = query.split("WHERE");
        String condition = parts[1].trim();
        String[] conditionParts = condition.split("=");

        if (conditionParts.length != 2) {
            System.out.println("Invalid WHERE condition in DELETE.");
            return;
        }

        String column = conditionParts[0].trim();
        String value = conditionParts[1].trim().replace("'", "");

        table.deleteRows(column, value);
    }

    // Handle UPDATE query with SET and WHERE clause
    private void handleUpdateQuery(String query) throws IOException {
        if (!query.startsWith("UPDATE " + table.getTableName().toUpperCase())) {
            System.out.println("Invalid UPDATE query.");
            return;
        }

        if (!query.contains("SET") || !query.contains("WHERE")) {
            System.out.println("UPDATE requires SET and WHERE clauses.");
            return;
        }

        String[] parts = query.split("SET|WHERE");
        if (parts.length != 3) {
            System.out.println("Invalid UPDATE syntax.");
            return;
        }

        String setClause = parts[1].trim();
        String whereClause = parts[2].trim();

        String[] setParts = setClause.split("=");
        if (setParts.length != 2) {
            System.out.println("Invalid SET clause.");
            return;
        }

        String targetColumn = setParts[0].trim();
        String newValue = setParts[1].trim().replace("'", "");

        String[] whereParts = whereClause.split("=");
        if (whereParts.length != 2) {
            System.out.println("Invalid WHERE clause in UPDATE.");
            return;
        }

        String column = whereParts[0].trim();
        String value = whereParts[1].trim().replace("'", "");

        table.updateRows(column, value, targetColumn, newValue);
    }
}