package basic;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

class Table {
    private String tableName;
    private List<String> schema; // Column names
    private File file;

    public Table(String tableName, List<String> schema) throws IOException {
        this.tableName = tableName;
        this.schema = schema;
        this.file = new File(tableName + ".csv");

        if (!file.exists()) {
            file.createNewFile();
            writeSchemaToFile();
        }
    }

    private void writeSchemaToFile() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(String.join(",", schema));
            writer.newLine();
        }
    }

    public void insertRow(List<String> row) throws IOException {
        if (row.size() != schema.size()) {
            throw new IllegalArgumentException("Row size doesn't match schema size.");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(String.join(",", row));
            writer.newLine();
        }
    }

    public List<List<String>> retrieveRows() throws IOException {
        List<List<String>> rows = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            reader.readLine(); // Skip header line
            while ((line = reader.readLine()) != null) {
                String[] row = line.split(",");
                rows.add(List.of(row));
            }
        }
        return rows;
    }

    public void printTable() throws IOException {
        List<List<String>> rows = retrieveRows();
        System.out.println(String.join(", ", schema));
        for (List<String> row : rows) {
            System.out.println(String.join(", ", row));
        }
    }

    // New method for printing rows that match a specific condition
 // In Table.java
    public void printFilteredTable(String column, String value) throws IOException {
        int columnIndex = -1;

        // Find the column index for the specified column name
        for (int i = 0; i < schema.size(); i++) {
            if (schema.get(i).equalsIgnoreCase(column)) {
                columnIndex = i;
                break;
            }
        }

        // If the column does not exist, output a message and return
        if (columnIndex == -1) {
            System.out.println("Column " + column + " does not exist.");
            return;
        }

        List<List<String>> rows = retrieveRows();
        System.out.println(String.join(", ", schema));  // Print the schema header

        boolean recordFound = false;
        
        // Clean the condition value to remove any unwanted characters (like semicolons)
        String cleanedValue = value.trim().replace(";", "").replace("'", "");

        for (List<String> row : rows) {
            String cellValue = row.get(columnIndex).trim();  // Trim whitespace around the cell value
            String trimmedValue = cleanedValue.trim();  // Trim value to compare

            boolean matches = false;

            // Debugging output to check row values and column data types
//            System.out.println("Checking row: " + row);
//            System.out.println("Comparing value: '" + cellValue + "' with condition value: '" + trimmedValue + "'");

            try {
                // Handle integer comparison for columns like id and age
                if (column.equalsIgnoreCase("id") || column.equalsIgnoreCase("age")) {
                    int intValue = Integer.parseInt(trimmedValue);
                    int rowValue = Integer.parseInt(cellValue);
                    matches = rowValue == intValue;
                } else {
                    // Case-insensitive string comparison for other columns
                    matches = cellValue.equalsIgnoreCase(trimmedValue);
                }
            } catch (NumberFormatException e) {
                // Fallback for string comparison if a NumberFormatException occurs
                matches = cellValue.equalsIgnoreCase(trimmedValue);
            }

            if (matches) {
                System.out.println(String.join(", ", row));
                recordFound = true;
            }
        }

        if (!recordFound) {
            System.out.println("No matching records found for condition: " + column + " = " + cleanedValue);
        }
    }






    public void deleteRows(String column, String value) throws IOException {
        int columnIndex = schema.indexOf(column);
        if (columnIndex == -1) {
            System.out.println("Column " + column + " does not exist.");
            return;
        }

        List<List<String>> rows = retrieveRows();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(String.join(",", schema)); // Write header
            writer.newLine();
            for (List<String> row : rows) {
                if (!row.get(columnIndex).equals(value)) {
                    writer.write(String.join(",", row));
                    writer.newLine();
                }
            }
        }
        System.out.println("Rows deleted where " + column + " = " + value);
    }

    public void updateRows(String column, String value, String targetColumn, String newValue) throws IOException {
        int columnIndex = schema.indexOf(column);
        int targetIndex = schema.indexOf(targetColumn);

        if (columnIndex == -1 || targetIndex == -1) {
            System.out.println("Invalid column name(s).");
            return;
        }

        List<List<String>> rows = retrieveRows();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(String.join(",", schema)); // Write header
            writer.newLine();
            for (List<String> row : rows) {
                List<String> updatedRow = new ArrayList<>(row);
                if (updatedRow.get(columnIndex).equals(value)) {
                    updatedRow.set(targetIndex, newValue);
                }
                writer.write(String.join(",", updatedRow));
                writer.newLine();
            }
        }
        System.out.println("Rows updated where " + column + " = " + value);
    }

    public String getTableName() {
        return this.tableName;
    }
}
