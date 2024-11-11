package basic;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

class Table {
    private String tableName;
    private List<String> schema;  // List of column names
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
            reader.readLine();  // Skip the schema line (header)
            while ((line = reader.readLine()) != null) {
                String[] row = line.split(",");
                rows.add(List.of(row));
            }
        }
        return rows;
    }

    public void printTable() throws IOException {
        List<List<String>> rows = retrieveRows();
        System.out.println(String.join(", ", schema));  // Print the schema
        for (List<String> row : rows) {
            System.out.println(String.join(", ", row));
        }
    }

    // Print table content that matches a specific condition
    public void printFilteredTable(String column, String value) throws IOException {
        int columnIndex = schema.indexOf(column);

        if (columnIndex == -1) {
            System.out.println("Column " + column + " does not exist.");
            return;
        }

        List<List<String>> rows = retrieveRows();
        System.out.println(String.join(", ", schema));  // Print the schema

        for (List<String> row : rows) {
            if (row.get(columnIndex).equals(value)) {
                System.out.println(String.join(", ", row));
            }
        }
    }

    public String getTableName() {
        return this.tableName;
    }
}

