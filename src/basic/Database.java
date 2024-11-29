package basic;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database {
    private Map<String, Table> tables = new HashMap<>();

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
