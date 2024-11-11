package basic;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class QueryParser {

    private Table table;

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
        } else {
            System.out.println("Unknown command: " + query);
        }
    }

    // Handle INSERT INTO query
    private void handleInsertQuery(String query) throws IOException {
        if (!query.contains("VALUES")) {
            System.out.println("Invalid INSERT query");
            return;
        }

        String[] parts = query.split("VALUES");
        String valuesPart = parts[1].trim();

        if (valuesPart.startsWith("(") && valuesPart.endsWith(");")) {
            valuesPart = valuesPart.substring(1, valuesPart.length() - 2);

            String[] values = valuesPart.split(",");
            for (int i = 0; i < values.length; i++) {
                values[i] = values[i].trim().replace("'", "");
            }

            table.insertRow(Arrays.asList(values));
            System.out.println("Row inserted.");
        } else {
            System.out.println("Invalid INSERT syntax.");
        }
    }

    // Handle SELECT query with or without WHERE clause
 // Inside QueryParser class
    private void handleSelectQuery(String query) throws IOException {
        // Make parsing case-insensitive and remove extra spaces
        query = query.trim().replaceAll(" +", " ").toUpperCase();

        // Check for table name in SELECT query
        if (query.startsWith("SELECT * FROM " + table.getTableName().toUpperCase())) {
            if (query.contains("WHERE")) {
                // Split on WHERE to isolate the condition part
                String[] parts = query.split("WHERE");
                String condition = parts[1].trim();

                // Split condition into column and value, such as "age = 30"
                String[] conditionParts = condition.split("=");
                if (conditionParts.length != 2) {
                    System.out.println("Invalid WHERE condition.");
                    return;
                }

                String column = conditionParts[0].trim();
                String value = conditionParts[1].trim().replace("'", "");

                // Filter rows based on condition
                table.printFilteredTable(column, value);
            } else {
                // No WHERE clause, print entire table
                table.printTable();
            }
        } else {
            System.out.println("Invalid SELECT query.");
        }
    }

}

