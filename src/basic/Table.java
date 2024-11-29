package basic;

import java.io.*;
import java.util.*;

class Table {
    private String tableName;
    private List<String> schema; // Column names
    private File file;
    private Set<String> phoneNumbers = new HashSet<>();

    public Table(String tableName, List<String> schema) throws IOException {
        this.tableName = tableName;
        this.schema = schema; // Accept schema as a parameter
        this.file = new File(tableName + ".csv");

        if (!file.exists()) {
            file.createNewFile();
            writeSchemaToFile();
        } else {
            loadExistingPhoneNumbers();
        }
    }

    private void loadExistingPhoneNumbers() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            reader.readLine(); // Skip header line

            int phoneIndex = schema.indexOf("phone");
            if (phoneIndex != -1) {
                while ((line = reader.readLine()) != null) {
                    String[] row = line.split(",");
                    if (row.length > phoneIndex) {
                        String phone = row[phoneIndex].trim();
                        phoneNumbers.add(phone);
                    }
                }
            }
        }
    }

    private void writeSchemaToFile() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(String.join(",", schema));  // Write header
            writer.newLine();
        }
    }

    public boolean isPhoneNumberUnique(String phoneNumber) {
        return !phoneNumbers.contains(phoneNumber);
    }

    public void insertRow(List<String> row) throws IOException {
        if (row.size() != schema.size()) {
            throw new IllegalArgumentException("Row size doesn't match schema size.");
        }

        List<String> cleanedRow = new ArrayList<>();
        for (String cell : row) {
            cleanedRow.add(cell.trim().replaceAll("(?i)^insert\\s+into\\s+users\\s+values\\(", "").replaceAll("(?i)^nsert\\s+into\\s+users\\s+values\\(", "").replaceAll("(?i)insert\\s+into\\s+users\\s+values", "").replaceAll("\\)$", "").trim());
        }

        int phoneIndex = schema.indexOf("phone");
        if (phoneIndex != -1) {
            String phone = cleanedRow.get(phoneIndex).trim().replaceAll("[^\\d]", "");
            if (!phone.matches("^(\\(?\\d{4}\\)?[-\\s]?\\d{3}[-\\s]?\\d{3}|\\d{10})$")) {
                throw new IllegalArgumentException("Invalid phone number format.");
            }
            if (!isPhoneNumberUnique(phone)) {
                throw new IllegalArgumentException("Phone number must be unique.");
            }
            phoneNumbers.add(phone);
        }

        String rowData = String.join(",", cleanedRow);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(rowData);
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

    public void printFilteredTable(String column, String value) throws IOException {
        int columnIndex = schema.indexOf(column);
        if (columnIndex == -1) {
            System.out.println("Column " + column + " does not exist.");
            return;
        }

        List<List<String>> rows = retrieveRows();
        System.out.println(String.join(", ", schema));

        boolean recordFound = false;
        String cleanedValue = value.trim().replace(";", "").replace("'", "");

        for (List<String> row : rows) {
            String cellValue = row.get(columnIndex).trim();
            boolean matches = cellValue.equalsIgnoreCase(cleanedValue);
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
            writer.write(String.join(",", schema));
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
            writer.write(String.join(",", schema));
            writer.newLine();
            for (List<String> row : rows) {
                if (row.get(columnIndex).equals(value)) {
                    row.set(targetIndex, newValue);
                }
                writer.write(String.join(",", row));
                writer.newLine();
            }
        }
        System.out.println("Rows updated where " + column + " = " + value);
    }

    public String getTableName() {
        return tableName;
    }

}