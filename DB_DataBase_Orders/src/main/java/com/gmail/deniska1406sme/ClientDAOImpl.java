package com.gmail.deniska1406sme;

import java.sql.Connection;

public class ClientDAOImpl extends AbstractDAO<Client>{
    public ClientDAOImpl(Connection conn, String tableName) {
        super(conn, tableName);
    }
}
