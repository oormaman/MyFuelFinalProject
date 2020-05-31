package server.dbLogic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class OrderDBLogic {

	public JsonArray getFuelInventoryOrders(String supplierID){
		JsonArray fuelInventoryOrders = new JsonArray();

		String query = "";
		Statement stmt = null;
		try {
			if(DBConnector.conn != null) {
				query = "SELECT * FROM  fuel_inventory_orders "
						+ "WHERE supplierID="+supplierID;
				stmt = DBConnector.conn.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				while(rs.next()) {
					JsonObject order = new JsonObject();
					order.addProperty("orderID", rs.getString("orderID"));
					order.addProperty("stationID", rs.getString("stationID"));
					order.addProperty("orderDate", rs.getString("orderDate"));
					order.addProperty("orderStatus", rs.getString("orderStatus"));
					order.addProperty("fuelAmount", rs.getString("fuelAmount"));
					order.addProperty("totalPrice", rs.getString("totalPrice"));
					System.out.println(order.toString());
					fuelInventoryOrders.add(order);
				}
			}else {
				System.out.println("Conn is null");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		
		return fuelInventoryOrders;
		
	}
	

	public void insertHomeHeatingFuelOrder(JsonObject order) {
		String query = " ";
		Statement stmt = null;
		String orderID = order.get("orderID").getAsString();
		String customerId = order.get("customerId").getAsString();
		String amount = order.get("amount").getAsString();
		String Street = order.get("street").getAsString();
		String dateSupply = order.get("dateSupplay").getAsString();
		String urgentOrder = order.get("isUrgentOrder").getAsString();
		String saleTemplateName = order.get("dateSupplay").getAsString();
		try {
			if (DBConnector.conn != null) {
				query = "INSERT INTO  home_heating_fuel_orders(orderID,customerId,amount,Street,dateSupply,urgentOrder,saleTemplateName)"
						+ "VALUES(" + "'" + orderID + "'" + "," + "'" + customerId + "'" + "," + "'" + amount + "'"
						+ "," + "'" + Street + "'" + "," + "'" + dateSupply + "'" + "," + "'" + urgentOrder + "'" + ","
						+ "'" + saleTemplateName + "'" + ");";
				System.out.println(query);
				stmt = DBConnector.conn.createStatement();
				stmt.execute(query);
				System.out.println(query);
			} else {
				System.out.println("Conn is null");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public JsonArray getFastFuelOrdersByStationID(String stationID) {
		JsonArray fastFuelOrders = new JsonArray();

		String query = "";
		Statement stmt = null;
		try {
			if (DBConnector.conn != null) {
				query = "SELECT * FROM fast_fuel_orders " + "WHERE stationID='" + stationID + "';";
				stmt = DBConnector.conn.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				while (rs.next()) {
					JsonObject order = new JsonObject();
					order.addProperty("orderID", rs.getString("orderID"));
					order.addProperty("orderDate", rs.getString("orderDate"));
					order.addProperty("customerID", rs.getString("customerID"));
					order.addProperty("fuelAmount", rs.getString("fuelAmount"));
					order.addProperty("totalPrice", rs.getString("totalPrice"));
					System.out.println(order.toString());
					fastFuelOrders.add(order);
				}
			} else {
				System.out.println("Conn is null");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return fastFuelOrders;

	}

	public String createDate(String quarter, String year) {
		if (quarter.isEmpty() || year.isEmpty())
			return "";
		String startDate = "";
		String endDate = "";
		if (quarter.equals("January - March")) {
			startDate = year + "-01-01";
			endDate = year + "-03-31";
		} else if (quarter.equals("April - June")) {
			startDate = year + "-04-01";
			endDate = year + "-06-30";
		} else if (quarter.equals("July - September")) {
			startDate = year + "-07-01";
			endDate = year + "-09-30";
		} else {
			startDate = year + "-10-01";
			endDate = year + "-12-31";
		}
		return startDate + "_" + endDate;
	}

	public JsonArray getHomeHeatingFuelOrdersByStationID(JsonObject requestJson){
		JsonArray homeHeatingFuelOrders = new JsonArray();
		String stationID = requestJson.get("stationID").getAsString();
		String quarter = requestJson.get("quarter").getAsString();
		String year = requestJson.get("year").getAsString();
		String dates = createDate(quarter, year);
		System.out.println(dates);
		
		String query = "SELECT * FROM home_heating_fuel_orders WHERE "+
				"stationID = '"+stationID +"' " +
				(quarter.isEmpty() ? "" : "AND orderDate between '"+ 
							dates.split("_")[0] + "' and '" + dates.split("_")[1])+"'"+
						";";
		System.out.println(query);
		Statement stmt=null;
		try {
			if(DBConnector.conn != null) {
				stmt = DBConnector.conn.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				while(rs.next()) {
					JsonObject order = new JsonObject();
					order.addProperty("orderID", rs.getString("orderID"));
					order.addProperty("orderDate", rs.getString("orderDate"));
					order.addProperty("customerID", rs.getString("customerID"));
					order.addProperty("fuelAmount", rs.getString("fuelAmount"));
					order.addProperty("totalPrice", rs.getString("totalPrice"));
					System.out.println(order.toString());
					homeHeatingFuelOrders.add(order);
				}
			}else {
				System.out.println("Conn is null");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		
		return homeHeatingFuelOrders;
		
	}
	
	
	
	public JsonArray GetHomeHeatingFuelOrder() {
		JsonArray HHFOrders = new JsonArray();

		String query = "";
		Statement stmt = null;

		try {
			if (DBConnector.conn != null) {
				query = "SELECT * FROM  home_heating_fuel_orders";
				stmt = DBConnector.conn.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				while (rs.next()) {
					JsonObject HHFOrder = new JsonObject();
					HHFOrder.addProperty("orderID", rs.getString("orderID"));
					HHFOrder.addProperty("customerID", rs.getString("customerID"));
					HHFOrder.addProperty("saleTemplateName", rs.getString("saleTemplateName"));
					HHFOrder.addProperty("orderDate", rs.getString("orderDate"));
					HHFOrder.addProperty("orderStatus", rs.getString("orderStatus"));
					HHFOrder.addProperty("fuelAmount", rs.getString("fuelAmount"));
					HHFOrder.addProperty("totalPrice", rs.getString("totalPrice"));
					HHFOrder.addProperty("paymentMethod", rs.getString("paymentMethod"));
					HHFOrder.addProperty("dateSupply", rs.getString("dateSupply"));
					HHFOrder.addProperty("address", rs.getString("address"));
					HHFOrder.addProperty("urgentOrder", rs.getString("urgentOrder"));
					HHFOrders.add(HHFOrder);
				}
			} else {
				System.out.println("Conn is null");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return HHFOrders;
	}
}
