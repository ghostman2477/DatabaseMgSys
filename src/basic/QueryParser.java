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

    // Handle INSERT INTO query
//    public void handleInsertQuery(String query) throws IOException {
//        if (!query.contains("VALUES")) {
//            System.out.println("Invalid INSERT query");
//            return;
//        }
//
//        // Split the query at the VALUES keyword
//        String[] parts = query.split("VALUES");
//        String valuesPart = parts[1].trim();
//
//        // Ensure the values part is properly formatted
//        if (valuesPart.startsWith("(") && valuesPart.endsWith(");")) {
//            // Remove the parentheses and semicolon
//            valuesPart = valuesPart.substring(1, valuesPart.length() - 2);
//            String[] values = valuesPart.split(",");
//
//            // Ensure the number of values matches the schema length
//            if (values.length != table.getSchema().size()) {
//                System.out.println("Incorrect number of values for insert.");
//                return;
//            }
//
//            // Clean up each value (trim spaces and remove unwanted characters)
//            for (int i = 0; i < values.length; i++) {
//                values[i] = values[i].trim().replace("'", "").trim();
//            }
//
//            // Extract the phone number (assuming it's the 4th column)
//            String phoneNumber = values[3]; // Phone number is the 4th column (index 3)
//
//            // Check if the phone number already exists
//            if (table.getPhoneNumbers().contains(phoneNumber)) {
//                System.out.println("Phone number already exists. Please use a unique phone number.");
//                return;
//            }
//
//            // Add the phone number to the set of existing phone numbers
//            table.getPhoneNumbers().add(phoneNumber);
//
//            // Insert the row into the table
//            table.insertRow(Arrays.asList(values)); // Insert the cleaned row into the file
//            System.out.println("Row inserted.");
//        } else {
//            System.out.println("Invalid INSERT syntax.");
//        }
//    }
//
//
//
//    private boolean isPhoneNumberUnique(String phoneNumber) throws IOException {
//        // Load all rows from the table and check if the phone number already exists
//        List<List<String>> rows = table.retrieveRows();
//        int phoneIndex = table.getSchema().indexOf("phone");
//
//        for (List<String> row : rows) {
//            if (row.get(phoneIndex).equals(phoneNumber)) {
//                return false;
//            }
//        }
//        return true;
//    }
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
