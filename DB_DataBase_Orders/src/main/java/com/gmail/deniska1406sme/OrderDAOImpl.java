package com.gmail.deniska1406sme;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OrderDAOImpl extends AbstractDAO<Order>{
    public OrderDAOImpl(Connection conn, String tableName) {
        super(conn, tableName);
    }

}
