package com.gmail.deniska1406sme;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractDAO<T> {
    private final Connection conn;
    private final String tableName;

    public AbstractDAO(Connection conn, String tableName) {
        this.conn = conn;
        this.tableName = tableName;
    }

    public void createTable(Class<T> cls) {
        Field[] fields = cls.getDeclaredFields();
        Field id = getPrimaryKey(fields);

        StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (");
        sb.append(id.getName()).append(" ").append(" INT AUTO_INCREMENT PRIMARY KEY, ");

        for (Field field : fields) {
            if (field != id) {
                field.setAccessible(true);
                sb.append(field.getName()).append(" ");

                if (field.getType() == int.class) {
                    sb.append("INT,");
                } else if (field.getType() == double.class) {
                    sb.append("DOUBLE,");
                } else if (field.getType() == String.class) {
                    sb.append("VARCHAR(128),");
                } else
                    throw new RuntimeException("Unsupported field type: " + field.getType());
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")");

        try{
            try (Statement st = conn.createStatement()) {
                st.execute(sb.toString());
            }
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void checkQuantity(String name, int quantity){

    }

    public void add(T obj) {
        try {
            Field[] fields = obj.getClass().getDeclaredFields();
            Field id = getPrimaryKey(fields);

            StringBuilder names = new StringBuilder();
            StringBuilder values = new StringBuilder();

            for (Field field : fields) {
                if (field != id) {
                    field.setAccessible(true);

                    names.append(field.getName()).append(",");
                    values.append('"').append(field.get(obj)).append("\",");
                }
            }

            names.deleteCharAt(names.length() - 1);
            values.deleteCharAt(values.length() - 1);

            String sql = "INSERT INTO " + tableName + " (" + names.toString() +
                    ") VALUES (" + values.toString() + ")";

            try (Statement st = conn.createStatement()) {
                st.execute(sql, Statement.RETURN_GENERATED_KEYS);
                ResultSet generatedKeys = st.getGeneratedKeys();
                generatedKeys.next();
                id.setAccessible(true);
                id.set(obj, generatedKeys.getInt(1));
            }

        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public void update(T obj) {
        try {
            Field[] fields = obj.getClass().getDeclaredFields();
            Field id = getPrimaryKey(fields);

            StringBuilder sb = new StringBuilder();

            for (Field field : fields) {
                if (field != id) {
                    field.setAccessible(true);

                    sb.append(field.getName()).append(" = ").append('"').append(field.get(obj)).append('"').append(",");
                }
            }

            sb.deleteCharAt(sb.length() - 1);

            String sql = "UPDATE " + tableName + " SET " + sb.toString() + " WHERE "+ id.getName()+" = \"" + id.get(obj) + "\"";

            try (Statement st = conn.createStatement()) {
                st.execute(sql);
            }

        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public void delete(T obj) {
        try {
            Field[] fields = obj.getClass().getDeclaredFields();
            Field id = getPrimaryKey(fields);

            String sql = "DELETE FROM " + tableName + " WHERE " + id.getName()+" = \"" + id.get(obj) + "\"";

            try (Statement st = conn.createStatement()) {
                st.execute(sql);
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public List<T> getAll(Class<T> cls) {
        List<T> list = new ArrayList<>();

        try {
            try (Statement st = conn.createStatement()) {
                try (ResultSet rs = st.executeQuery("SELECT * FROM " + tableName)) {
                    ResultSetMetaData md = rs.getMetaData();

                    while (rs.next()) {
                        T obj = cls.newInstance();

                        for (int i = 1; i <= md.getColumnCount(); i++) {
                            String columnName = md.getColumnName(i);
                            Field field = cls.getDeclaredField(columnName);
                            field.setAccessible(true);
                            field.set(obj, rs.getObject(i));
                        }

                        list.add(obj);
                    }
                }
            }
            return list;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<T> getAll(Class<T> cls,String...parameters){
        List<T> list = new ArrayList<>();

        String params = Arrays.toString(parameters);
        params = params.substring(1, params.length() - 1);

        try {
            try (Statement st = conn.createStatement()){
                try (ResultSet rs = st.executeQuery("SELECT " + params + " FROM " + tableName)) {
                    ResultSetMetaData md = rs.getMetaData();

                    while (rs.next()) {
                        T obj = cls.getDeclaredConstructor().newInstance();
                        for (int i = 1; i <= md.getColumnCount(); i++) {
                            String columnName = md.getColumnName(i);
                            Field field = cls.getDeclaredField(columnName);
                            field.setAccessible(true);

                            field.set(obj, rs.getObject(i));
                        }

                        list.add(obj);
                    }
                }
            }
            return list;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Field getPrimaryKey(Field[] fields) {
        Field result = null;

        for (Field field : fields) {
            if (field.isAnnotationPresent(Id.class)) {
                result = field;
                result.setAccessible(true);
                break;
            }
        }
        if (result == null) {
            throw new RuntimeException("Unable to find primary key field");
        }
        return result;
    }
}
