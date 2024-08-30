package com.gmail.deniska1406sme;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class Main
{
    public static void main( String[] args ) throws SQLException {
        try (Connection conn = ConnectionFactory.getConnection()){
            try {
                try (Statement st = conn.createStatement()){
                    st.execute("DROP TABLE IF EXISTS Orders");
                    st.execute("DROP TABLE IF EXISTS Clients");
                    st.execute("DROP TABLE IF EXISTS Products");
                }
            }catch (SQLException e){
                throw new SQLException(e);
            }

            ClientDAOImpl clientDAO = new ClientDAOImpl(conn,"Clients");
            ProductDAOImpl productDAO = new ProductDAOImpl(conn,"Products");
            OrderDAOImpl orderDAO = new OrderDAOImpl(conn,"Orders");

            clientDAO.createTable(Client.class);
            productDAO.createTable(Product.class);
            orderDAO.createTable(Order.class);

            Client client1 = new Client("Denys","main st 1","38091");
            Client client2 = new Client("Olga","main st 2","38092");
            Client client3 = new Client("Victor","main st 3","38093");

            clientDAO.add(client1);
            clientDAO.add(client2);
            clientDAO.add(client3);

            Product product1 = new Product("iPhone 15 128GB",1000.0);
            Product product2 = new Product("Samsung S24 256GB",950.0);
            Product product3 = new Product("Xiaomi 14 512GB",1075.0);

            productDAO.add(product1);
            productDAO.add(product2);
            productDAO.add(product3);

            Order order1 = new Order(client1.getId(),product2.getId(), 5);
            Order order2 = new Order(client2.getId(),product3.getId(), 5);
            Order order3 = new Order(client3.getId(),product1.getId(), 7);

            orderDAO.add(order1);
            orderDAO.add(order2);
            orderDAO.add(order3);

            List<Client> list = clientDAO.getAll(Client.class, "id","name","phone");
            for(Client cli: list)
                System.out.println(cli.toString());
            System.out.println();

            List<Product> list1 = productDAO.getAll(Product.class);
            for(Product prod: list1)
                System.out.println(prod);
            System.out.println();

            List<Order> list2 = orderDAO.getAll(Order.class);
            for(Order ord: list2)
                System.out.println(ord);
            System.out.println();

            orderDAO.delete(order2);

            List<Order> list3 = orderDAO.getAll(Order.class);
            for(Order ord: list3)
                System.out.println(ord);
            System.out.println();

        }
    }
}
