package com.example.demo;

import org.apache.log4j.BasicConfigurator;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static spark.Spark.*;


public class DollMain {
    private static final String connectionURL = "jdbc:mysql://localhost:3306/my_sql_demo";
    private static final String username = "root";
    private static final String password = "topsecretpassword";
    private static Connection connection;

    public static void main(String[] args) {
        BasicConfigurator.configure();
        try {
            connection = DriverManager.getConnection(connectionURL, username, password);
        } catch (SQLException e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }

        // check if spark works
        get("/hello", (req, res) -> "Dani Mocanu");

        // get all dolls
        get("/dolls", (req, res) -> getAllDolls());

        // get doll by id
        get("dolls/:id", (req, res) -> getDollById(Integer.parseInt(req.params(":id"))));

        // insert a doll into database
        post("/dolls/:nume/:pret/:stoc", (req, res) -> {
            insertDoll(req.params(":nume"), Double.parseDouble(req.params(":pret")), Integer.parseInt(req.params(":stoc")));
            return "Done insert";
        });

        // delete doll by id
        delete("/dolls/:id", (req, res) -> {
            deleteDoll(Integer.parseInt(req.params(":id")));
            return "Done delete";
        });

        // update Doll by id
        put("dolls/:id/:nume/:pret/:stoc", (req, res) -> {
            updateDoll(Integer.parseInt(req.params(":id")), req.params(":nume"), Double.parseDouble(req.params(":pret")),
                    Integer.parseInt(req.params(":stoc")));
            return "Done update";
        });
    }

    public static String getAllDolls() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("select * from doll;");

        List<Doll> dolls = new ArrayList<>();

        while (rs.next()) {
            Doll doll = new Doll(rs.getInt("id"), rs.getString("nume"), (double) rs.getInt("pret"),
                    rs.getInt("stoc"));
            dolls.add(doll);
        }

        return dolls.toString();
    }

    public static void insertDoll(String nume, Double pret, Integer stoc) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("insert into doll(nume, pret, stoc) values(?,?,?)");
        ps.setString(1, nume);
        ps.setDouble(2, pret);
        ps.setInt(3, stoc);
        ps.execute();
    }

    public static void deleteDoll(Integer id) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("delete from doll where id=?;");
        ps.setInt(1, id);
        ps.execute();
    }

    public static void updateDoll(Integer id, String nume, Double pret, Integer stoc) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("update doll set nume=?, pret=?, stoc=? where id=?;");
        ps.setString(1, nume);
        ps.setDouble(2, pret);
        ps.setInt(3, stoc);
        ps.setInt(4, id);
        ps.execute();
    }

    private static Doll getDollById(int id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM doll WHERE id = ? ;"
        );

        preparedStatement.setInt(1, id);

        ResultSet rs = preparedStatement.executeQuery();
        rs.next();

        return new Doll(rs.getInt("id"), rs.getString("nume"), rs.getDouble("pret"), rs.getInt("stoc"));
    }
}
