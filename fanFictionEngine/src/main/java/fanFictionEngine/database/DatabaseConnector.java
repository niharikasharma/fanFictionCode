package fanFictionEngine.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class DatabaseConnector {

	private Connection connection = null;
	private Statement statement;
	private ResultSet resultSet;

	public Connection connector() {
		System.out.println("-------- MySQL JDBC Connection Testing ------------");

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("Where is your MySQL JDBC Driver?");
			e.printStackTrace();
			return null;
		}

		System.out.println("MySQL JDBC Driver Registered!");

		try {
			
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/fanfictiondrg201605", "",
					"");

		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return null;
		}

		if (connection != null) {
			System.out.println("You made it, take control your database now!");
			return connection;

		} else {
			System.out.println("Failed to make connection!");
			return null;

		}
		
		
	}

	public ResultSet readData(String query) {
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			return resultSet;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	public static void main(String args[]) {
		DatabaseConnector con = new DatabaseConnector();
		con.connector();
		ResultSet result = con.readData("select * from fanfictiondrg201610.user limit 10;");
		System.out.println("Result");
		try {
			while (result.next()) {
				int id = result.getInt("id");
				System.out.println(id);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
