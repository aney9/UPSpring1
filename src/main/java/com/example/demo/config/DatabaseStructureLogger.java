package com.example.demo.config;

import jakarta.annotation.PostConstruct;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

@Component
public class DatabaseStructureLogger {
    
    @Autowired
    private DataSource dataSource;
    
    @PostConstruct
    public void logDatabaseStructure() {
        try {
            DatabaseMetaData metaData = dataSource.getConnection().getMetaData();
            
            System.out.println("\n========== DATABASE STRUCTURE: zoo_shop ==========");
            
            ResultSet tables = metaData.getTables(null, null, null, new String[]{"TABLE"});
            
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                System.out.println("\nTable: " + tableName);
                
                ResultSet columns = metaData.getColumns(null, null, tableName, null);
                System.out.println("Columns:");
                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    String dataType = columns.getString("TYPE_NAME");
                    int columnSize = columns.getInt("COLUMN_SIZE");
                    int nullable = columns.getInt("NULLABLE");
                    String isNullable = nullable == DatabaseMetaData.columnNullable ? "YES" : "NO";
                    System.out.println("  - " + columnName + ": " + dataType + 
                                     "(" + columnSize + ") [Nullable: " + isNullable + "]");
                }
                columns.close();
                
                ResultSet pk = metaData.getPrimaryKeys(null, null, tableName);
                System.out.print("Primary Keys: ");
                boolean hasPk = false;
                while (pk.next()) {
                    if (hasPk) System.out.print(", ");
                    System.out.print(pk.getString("COLUMN_NAME"));
                    hasPk = true;
                }
                if (!hasPk) {
                    System.out.print("(none)");
                }
                System.out.println();
                pk.close();
            }
            tables.close();
            
            System.out.println("\n==================================================\n");
            
        } catch (Exception e) {
            System.err.println("Error logging database structure: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
