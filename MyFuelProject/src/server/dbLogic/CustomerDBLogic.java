package server.dbLogic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import entitys.CreditCard;

public class CustomerDBLogic {

	public boolean checkIfCustomerExist(String customerID) {

		boolean isExist = false;

		String query = "";
		Statement stmt = null;

		try {
			if (DBConnector.conn != null) {
				query = "SELECT * FROM  customer " +
						"WHERE customerID ='" + customerID + "';";
				stmt = DBConnector.conn.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				if (rs.next()) {
					isExist = true;
				}
			} else {
				System.out.println("Conn is null");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return isExist;
	}
	
	public JsonArray getCustomerTypes() {
		JsonArray types = new JsonArray();
		
		String query = "";
		Statement stmt = null;

		try {
			if (DBConnector.conn != null) {
				query = "SELECT * FROM  customer_type ";
				stmt = DBConnector.conn.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				while (rs.next()) {
					types.add(rs.getString("customerType"));
				}
			} else {
				System.out.println("Conn is null");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return types;
		
	}

	public void addVehicle(JsonObject vehicle) {
		String vehicleNumber = vehicle.get("vehicleNumber").getAsString();
		String fuelType = vehicle.get("fuelType").getAsString();
		String customerID = vehicle.get("customerID").getAsString();
		String query = "";

		Statement stmt = null;
		try {
			if (DBConnector.conn != null) {
				query = "INSERT INTO vehicles (vehicleNumber, fuelType, customerID) " + 
						"VALUES ('" + vehicleNumber + "','"+ fuelType + "','" + customerID + "');";
				System.out.println(query);
				stmt = DBConnector.conn.createStatement();
				stmt.execute(query);
			} else {
				System.out.println("Conn is null");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean checkIfVehicleExist(JsonObject vehicle) {
		String vehicleNumber = vehicle.get("vehicleNumber").getAsString();
		boolean isExist = false;
		String query = "";

		Statement stmt = null;
		try {
			if (DBConnector.conn != null) {
				query = "SELECT * FROM vehicles "
					  + "WHERE vehicleNumber = '" + vehicleNumber + "';";
				stmt = DBConnector.conn.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				if(rs.next()) {
					isExist = true;
				}				
			} else {
				System.out.println("Conn is null");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return isExist;
	}	
	
	public void addCreditCard(JsonObject creditCard) {
		String creditCardNumber = creditCard.get("creditCardNumber").getAsString();
		String cvv = creditCard.get("cvv").getAsString();
		String dateValidation = creditCard.get("dateValidation").getAsString();
		String customerID = creditCard.get("customerID").getAsString();
		
		String query = "";

		Statement stmt = null;
		try {
			if (DBConnector.conn != null) {
				query = "INSERT INTO credit_card (cardNumber, customerID, validationDate, cvvNumber) " + 
						"VALUES ('" + creditCardNumber + "','"+ customerID + "','"+ dateValidation + "','" + cvv + "');";
				System.out.println(query);
				stmt = DBConnector.conn.createStatement();
				stmt.execute(query);
			} else {
				System.out.println("Conn is null");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
