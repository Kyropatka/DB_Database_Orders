package com.gmail.deniska1406sme;

import java.sql.Connection;

public class ProductDAOImpl extends AbstractDAO<Product>{
    public ProductDAOImpl(Connection conn, String tableName) {
        super(conn, tableName);
    }
}
