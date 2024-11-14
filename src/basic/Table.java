package basic;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Table {
    private String tableName;
    private List<String> schema; // Column names
    private File file;
    private Set<String> phoneNumbers = new HashSet<>();

    public Table(String tableName, List<String> schema) throws IOException {
        this.tableName = tableName;
        this.schema = Arrays.asList("id", "name", "age", "phone");
        this.file = new File(tableName + ".csv");

        if (!file.exists()) {
            file.createNewFile();
            writeSchemaToFile();
        } else {
        	loadExistingPhoneNumbers();
        }
        
        
    }
    
//    private void loadExistingPhoneNumbers() throws IOException {
//        // The column index for 'phone' should correspond to the schema index
//        int phoneColumnIndex = schema.indexOf("phone");
//        
//        if (phoneColumnIndex == -1) {
//            throw new IllegalArgumentException("Phone column not found in the schema.");
//        }
//
//        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
//            String line;
//            reader.readLine(); // Skip header line
//
//            // Read all rows and load phone numbers
//            while ((line = reader.readLine()) != null) {
//                String[] row = line.split(",");
//                
//                // Ensure row has enough columns (length of schema)
//                if (row.length > phoneColumnIndex) {
//                    String phone = row[phoneColumnIndex].trim();
//                    phoneNumbers.add(phone);
//                } else {
//                    System.out.println("Warning: Skipping row with insufficient columns: " + Arrays.toString(row));
//                }
//            }
//        }
//    }
    
//    private void loadExistingPhoneNumbers() throws IOException {
//        List<List<String>> rows = retrieveRows();
//        int phoneIndex = schema.indexOf("phone");
//
//        // Debugging: Print schema and rows
//        System.out.println("Schema: " + schema);
//        System.out.println("Rows:");
//        for (List<String> row : rows) {
//            System.out.println(row);
//        }
//
//        // Check if the row contains a valid phone number
//        for (List<String> row : rows) {
//            if (row.size() > phoneIndex) { // Ensure row is long enough to contain a phone number
//                String phone = row.get(phoneIndex).trim();
//                phoneNumbers.add(phone);
//            } else {
//                System.out.println("Skipping row due to insufficient columns: " + row);
//            }
//        }
//    }
    private void loadExistingPhoneNumbers() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            reader.readLine(); // Skip header line

            int phoneIndex = schema.indexOf("phone");
            if (phoneIndex == -1) {
                throw new IllegalArgumentException("Phone column not found in the schema.");
            }

            while ((line = reader.readLine()) != null) {
                String[] row = line.split(",");
                if (row.length > phoneIndex) {
                    String phone = row[phoneIndex].trim();
                    phoneNumbers.add(phone);
                }
            }
        }
    }



//    private void writeSchemaToFile() throws IOException {
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
//            writer.write(String.join(",", schema));
//            writer.newLine();
//        }
//    }
    
    private void writeSchemaToFile() throws IOException {
        // Trim spaces from each column name in the schema
        List<String> trimmedSchema = new ArrayList<>();
        for (String column : schema) {
            trimmedSchema.add(column.trim());  // Trim spaces from column names
        }

        // Write the schema (header) to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(String.join(",", trimmedSchema));  // Write header without extra spaces
            writer.newLine();
        }
    }
    
    public boolean isPhoneNumberUnique(String phoneNumber) {
        return !phoneNumbers.contains(phoneNumber);
    }
    
//    public void insertRow(List<String> row) throws IOException {
//        if (row.size() != schema.size()) {
//            throw new IllegalArgumentException("Row size doesn't match schema size.");
//        }
//
//        // Check if the row already exists before inserting
//        List<List<String>> rows = retrieveRows();
//        if (rows.contains(row)) {
//            System.out.println("Error: Duplicate row, not inserting.");
//            return;  // Avoid inserting duplicate row
//        }
//
//        // If the row is not a duplicate, proceed with insertion
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
//            writer.write(String.join(",", row));
//            writer.newLine();
//        }
//    }
    public void insertRow(List<String> row) throws IOException {
        System.out.println("Original row before trimming: " + row);

        // Validate row size
        if (row.size() != schema.size()) {
            throw new IllegalArgumentException("Row size doesn't match schema size.");
        }

        // Create a cleaned version of the row
        List<String> cleanedRow = new ArrayList<>();
        for (String cell : row) {
            System.out.println("Cell before trim: '" + cell + "'");

            // General cleanup for SQL queries with missing letters or unwanted parts
            String cleanedCell = cell.trim()
                                     .replaceAll("(?i)^insert\\s+into\\s+users\\s+values\\(", "")  // Remove 'insert into users values(' at the start
                                     .replaceAll("(?i)^nsert\\s+into\\s+users\\s+values\\(", "")  // Handle 'NSERT INTO USERS VALUES('
                                     .replaceAll("(?i)insert\\s+into\\s+users\\s+values", "")  // Clean up 'insert into users values' anywhere
                                     .replaceAll("\\)$", "")  // Remove closing parenthesis at the end
                                     .trim();  // Trim spaces from each cell value

            // Add cleaned cell to the cleaned row
            cleanedRow.add(cleanedCell);
        }

        // Print the cleaned row for debugging
        System.out.println("Cleaned row: " + cleanedRow);

        // Extract and validate the phone number
        String phone = cleanedRow.get(schema.indexOf("phone")).trim();
        System.out.println("Phone before validation: '" + phone + "'");

        // Validate if phone number is unique
        if (!isPhoneNumberUnique(phone)) {
            throw new IllegalArgumentException("Phone number must be unique.");
        }

        // Add phone number to the set
        phoneNumbers.add(phone);

        // Join the cleaned row into a CSV line
        String rowData = String.join(",", cleanedRow);

        // Print row data before writing
        System.out.println("Row data before writing: " + rowData);

        // Write to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(rowData);  // Write the cleaned row to the file
            writer.newLine();  // Add a new line after the row
            System.out.println("Row inserted successfully.");
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
    
    public List<String> getSchema() {
        return schema;
    }
    public Set<String> getPhoneNumbers() {
        return phoneNumbers;
    }
}
