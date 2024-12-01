package basic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database {
    private Map<String, Table> tables = new HashMap<>();
    private final String dataDirectory = "E:\\RuleEngine\\Workspace\\DB";
    
    public Database() {
        try {
            loadExistingTables();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void loadExistingTables() throws IOException {
        File dir = new File(dataDirectory);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".csv"));
        if (files != null) {
            for (File file : files) {
                String tableName = file.getName().replace(".csv", "");
                // Attempt to read the schema from the existing file
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String header = reader.readLine();
                    if (header != null) {
                        List<String> schema = Arrays.asList(header.split(","));
                        Table table = new Table(tableName, schema);
                        tables.put(tableName.toUpperCase(), table);
                    }
                }
            }
        }
    }

    public void createTable(String tableName, List<String> schema) throws IOException {
        if (tables.containsKey(tableName.toUpperCase())) {
            System.out.println("Table " + tableName + " already exists.");
            return;
        }
        Table table = new Table(tableName, schema);
        tables.put(tableName.toUpperCase(), table);
        System.out.println("Table " + tableName + " created with schema: " + schema);
    }

    public Table getTable(String tableName) {
        return tables.get(tableName.toUpperCase());
    }
}
