package basic;

import java.io.IOException;
import java.util.List;
import java.util.Arrays;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            // Define schema for the table
            List<String> schema = Arrays.asList("id", "name", "age");

            // Create a table called "users"
            Table usersTable = new Table("users", schema);

            // Initialize the query parser for the table
            QueryParser parser = new QueryParser(usersTable);

            // Insert some data
            parser.parseQuery("INSERT INTO users VALUES (1, 'John Doe', 30);");
            parser.parseQuery("INSERT INTO users VALUES (2, 'Jane Smith', 25);");
            parser.parseQuery("INSERT INTO users VALUES (3, 'Alice Johnson', 30);");

            // Select all users
            parser.parseQuery("SELECT * FROM users;");

            // Select users where age = 30
            parser.parseQuery("SELECT * FROM users WHERE age = 30;");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



