package server.dbLogic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import client.controller.ObjectContainer;
import entitys.DeterminingRateRequests;
import entitys.Fuel;
import entitys.enums.FuelType;

public class FuelDBLogic {

	public JsonArray getFuelInventoryByUserName(String userName) {
		JsonArray array = new JsonArray();
		
		String query = "";
		Statement stmt = null;
		try {
			if (DBConnector.conn != null) {
				//stationID, fuelType, currentFuelAmount, thresholdAmount, maxFuelAmount
				query = "SELECT fuelType, currentFuelAmount, thresholdAmount, maxFuelAmount "
					+   "FROM  fuel_inventorys, employees, fuel_stations "
					+   "WHERE employees.userName = '"+userName +"' AND "
							+ "employees.employeeNumber = fuel_stations.managerID AND "
							+ "fuel_stations.stationID = fuel_inventorys.stationID;";
				stmt = DBConnector.conn.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				while (rs.next()) {
					JsonObject json = new JsonObject();
					json.addProperty("fuelType", rs.getString("fuelType"));
					json.addProperty("currentFuelAmount", rs.getFloat("currentFuelAmount"));
					json.addProperty("thresholdAmount", rs.getFloat("thresholdAmount"));
					json.addProperty("maxFuelAmount", rs.getFloat("maxFuelAmount"));
					array.add(json);
				}
			} else {
				System.out.println("Conn is null");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return array;
	}
	public JsonArray getFuelTypes() {
		JsonArray fuelTypes = new JsonArray();

		String query = "";
		Statement stmt = null;
		try {
			if (DBConnector.conn != null) {
				query = "SELECT * FROM  fuel ";
				stmt = DBConnector.conn.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				while (rs.next()) {
					fuelTypes.add(rs.getString("fuelType"));
				}
			} else {
				System.out.println("Conn is null");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return fuelTypes;

	}

	public Fuel getFuelObjectByType(String fuelType) {
		Fuel fuel = null;

		String query = "";
		Statement stmt = null;
		try {
			if (DBConnector.conn != null) {
				query = "SELECT * FROM  fuel " + "WHERE fuelType ='" + fuelType + "';";
				stmt = DBConnector.conn.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				if (rs.next()) {
					float pricePerLitter = rs.getFloat("pricePerLitter");
					float maxPricePerLitter = rs.getFloat("maxPricePerLitter");
					fuel = new Fuel(FuelType.stringToEnumVal(fuelType), pricePerLitter, maxPricePerLitter);
				}
			} else {
				System.out.println("Conn is null");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return fuel;
	}

	public void updateFuel(Fuel fuel, String newPrice) {

		float PriceToUpdate = 0;
		PriceToUpdate = Float.parseFloat(newPrice);
		String fueltype = fuel.getFuelType().toString();
		String query = "";
		Statement stmt = null;
		try {
			if (DBConnector.conn != null) {
				stmt = DBConnector.conn.createStatement();
				query = "UPDATE fuel " + "SET pricePerLitter = " + PriceToUpdate + " WHERE fuelType = '" + fueltype
						+ "';";
				stmt.executeUpdate(query);
			} else {
				System.out.println("Conn is null");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void SendRateRequest(DeterminingRateRequests request, String newPrice) {

		float PriceToUpdate = 0;
		PriceToUpdate = Float.parseFloat(newPrice);
		float currentPrice = request.getCurrentPrice();
		String fueltype = request.getFuelType();
		String status = request.getRequestStatus().enumToString(request.getRequestStatus());
		String createTime = request.getCreateTime();
		String query = "";
		Statement stmt = null;

		try {
			if (DBConnector.conn != null) {
				stmt = DBConnector.conn.createStatement();
				query="DELETE FROM determining_rate_requests WHERE fuelType = '" + fueltype + "';";
				stmt.execute(query);

				query = "INSERT INTO determining_rate_requests ( currentPrice, newPrice, requestStatus, fuelType, createTime) "
						+ "VALUES ('" + currentPrice + "','" + PriceToUpdate + "','" + status + "','" + fueltype + "','"
						+ createTime + "');";
				stmt = DBConnector.conn.createStatement();
				stmt.execute(query);
			} else {
				System.out.println("Conn is null");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public JsonArray getFuelCompanyNames() {
		JsonArray companyNames = new JsonArray();

		String query = "";

		Statement stmt = null;
		try {
			if (DBConnector.conn != null) {
				query = "SELECT * FROM fuel_company;";
				stmt = DBConnector.conn.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				while (rs.next()) {
					companyNames.add(rs.getString("companyName"));
				}
			} else {
				System.out.println("Conn is null");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return companyNames;
	}

	public JsonArray getRateRequests() {
		JsonArray rateRequests = new JsonArray();

		String query = "";
		Statement stmt = null;
		try {
			if (DBConnector.conn != null) {
				query = "SELECT * FROM  determining_rate_requests " + " WHERE requestStatus ='Waiting To Approve';";
				stmt = DBConnector.conn.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				while (rs.next()) {
					JsonObject rateRequest = new JsonObject();
					rateRequest.addProperty("requestID", rs.getString("requestID"));
					rateRequest.addProperty("currentPrice", rs.getString("currentPrice"));
					rateRequest.addProperty("newPrice", rs.getString("newPrice"));
					rateRequest.addProperty("requestStatus", rs.getString("requestStatus"));
					rateRequest.addProperty("fuelType", rs.getString("fuelType"));
					rateRequest.addProperty("createTime", rs.getString("createTime"));

					rateRequests.add(rateRequest);
				}
			} else {
				System.out.println("Conn is null");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rateRequests;
	}

	public void UpdateDecline(String decline, boolean decision, String ID) {

		String query = "";
		Statement stmt = null;
		try {

			if (DBConnector.conn != null) {
				if (decision == false) {
					stmt = DBConnector.conn.createStatement();
					query = "UPDATE  determining_rate_requests " + "SET reasonOfDecline = '" + decline
							+ "', requestStatus = 'Not Approved' " + " WHERE requestID = '" + ID + "';";

					stmt.executeUpdate(query);
				} else if (decision == true) {
					stmt = DBConnector.conn.createStatement();
					query = "UPDATE  determining_rate_requests SET requestStatus = 'Approved' " + " WHERE requestID = '"
							+ ID + "';";

					stmt.executeUpdate(query);
				}
			} else {
				System.out.println("Conn is null");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String getStationIDbyManagerID(String managerID) {
		String query = "";
		Statement stmt = null;
		String stationID="";
		try {
			if(DBConnector.conn != null) {
				query = "SELECT * FROM  fuel_stations "
						+ "WHERE managerID='"+managerID+"';";
				stmt = DBConnector.conn.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				while(rs.next()) {
					
					stationID=rs.getString("stationID");
				}
			}else {
				System.out.println("Conn is null");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		return stationID;
	}
	public JsonArray getFuelInventoryPerStation(String stationID){
		JsonArray fuelInventories = new JsonArray();

		String query = "";
		Statement stmt = null;
		try {
			if(DBConnector.conn != null) {
				query = "SELECT * FROM  fuel_inventorys "
						+ "WHERE stationID='"+stationID+"';";
				stmt = DBConnector.conn.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				while(rs.next()) {
					JsonObject fuelInventory = new JsonObject();
					fuelInventory.addProperty("stationID", rs.getString("stationID"));
					fuelInventory.addProperty("fuelType", rs.getString("fuelType"));
					fuelInventory.addProperty("currentFuelAmount", rs.getString("currentFuelAmount"));
					fuelInventory.addProperty("thresholdAmount", rs.getString("thresholdAmount"));
					fuelInventory.addProperty("maxFuelAmount", rs.getString("maxFuelAmount"));
					fuelInventories.add(fuelInventory);
				}
			}else {
				System.out.println("Conn is null");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		
		return fuelInventories;
	}
	
	public JsonObject getFuelInventoryByStationIDAndFuelType(String stationID, String fuelType) {

		JsonObject inventory = new JsonObject();
		String query = "";
		Statement stmt = null;
		try {
			if(DBConnector.conn != null) {
				query = "SELECT * FROM  fuel_inventorys "
						+ "WHERE stationID='"+stationID+"' AND fuelType = '" + fuelType + "';";
				stmt = DBConnector.conn.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				if(rs.next()) {
					inventory.addProperty("stationID", rs.getString("stationID"));
					inventory.addProperty("fuelType", rs.getString("fuelType"));
					inventory.addProperty("currentFuelAmount", rs.getFloat("currentFuelAmount"));
					inventory.addProperty("thresholdAmount", rs.getFloat("thresholdAmount"));
					inventory.addProperty("maxFuelAmount", rs.getFloat("maxFuelAmount"));
				}
			}else {
				System.out.println("Conn is null");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		
		return inventory;
	}
	// TODO - FIX IT !! NEED TO GET STATIONID 
	public void updateFuelInventory(String threshold, String maxAmount,String fuelType) {

		String query = "";
		Statement stmt = null;
		try {
			if (DBConnector.conn != null) {
				stmt = DBConnector.conn.createStatement();
				query = "UPDATE fuel_inventorys " + "SET thresholdAmount = '" + threshold +
						"', maxFuelAmount = '"+ maxAmount+"' "
						+	" WHERE fuelType = '" + fuelType
						+ "';";
				stmt.executeUpdate(query);
			} else {
				System.out.println("Conn is null");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public JsonArray getFullFuelInventory() {

		JsonArray fuelInventory = new JsonArray();

		String query = "";
		Statement stmt = null;
		try {
			if(DBConnector.conn != null) {
				query = "SELECT * FROM  fuel_inventorys;";
				stmt = DBConnector.conn.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				while(rs.next()) {
					JsonObject order = new JsonObject();
					order.addProperty("stationID", rs.getString("stationID"));
					order.addProperty("fuelType", rs.getString("fuelType"));
					order.addProperty("currentFuelAmount", rs.getFloat("currentFuelAmount"));
					order.addProperty("thresholdAmount", rs.getFloat("thresholdAmount"));
					order.addProperty("maxFuelAmount", rs.getFloat("maxFuelAmount"));
					fuelInventory.add(order);
				}
			}else {
				System.out.println("Conn is null");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		return fuelInventory;
	}
	public float getAvailableAmountOfFuelByTypeAndStationID(String fuelType, String stationID) {
		float availableAmount = 0;
		
		String query = "";
		Statement stmt = null;
		try {
			if(DBConnector.conn != null) {
				query = "SELECT * FROM  fuel_inventorys "
					  + "WHERE stationID = '" + stationID + "' AND "
					  		+ "fuelType = '" + fuelType + "';";
				stmt = DBConnector.conn.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				if(rs.next()) {
					availableAmount = rs.getFloat("currentFuelAmount");
				}
			}else {
				System.out.println("Conn is null");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		return availableAmount;
	}

	public void updateFuelAmountByStationIDFuelTypeAndAmount(String fuelType, String stationID, float amount) {
		float newAmount = getAvailableAmountOfFuelByTypeAndStationID(fuelType, stationID) - amount;
		String query = "";
		Statement stmt = null;
		try {
			if(DBConnector.conn != null) {
				query = "UPDATE fuel_inventorys "
						+ "SET currentFuelAmount = " + newAmount + " "
					  + "WHERE stationID = '" + stationID + "' AND "
					  		+ "fuelType = '" + fuelType + "';";
				stmt = DBConnector.conn.createStatement();
				stmt.executeUpdate(query);
			}else {
				System.out.println("Conn is null");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}
	
	public String getSupplierID() {
		return "777";
	}
	
	public boolean checkIfNeedToCreateInventoryOrder(String stationID, String fuelType) {
		// return true if need to create inventory order.
		boolean isValid = false;
		float currentFuelAmount = 0;
		float thresholdAmount = -1;
		//stationID, fuelType, currentFuelAmount, thresholdAmount, maxFuelAmount
		String query = "";
		Statement stmt = null;
		try {
			if(DBConnector.conn != null) {
				query = "SELECT * FROM  fuel_inventorys "
					  + "WHERE stationID = '" + stationID + "' AND "
					  		+ "fuelType = '" + fuelType + "';";
				stmt = DBConnector.conn.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				if(rs.next()) {
					currentFuelAmount = rs.getFloat("currentFuelAmount");
					thresholdAmount = rs.getFloat("thresholdAmount");
					if(currentFuelAmount < thresholdAmount)
						isValid = true;
				}
			}else {
				System.out.println("Conn is null");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		
		// doing this to ignore from create another order for the same fuel type.
		isValid = isValid && checkIfInventoryOrderAlreadyCreatedForNewOrders(stationID, fuelType);
		return isValid;
	}

	public boolean checkIfInventoryOrderAlreadyCreatedForNewOrders(String stationID, String fuelType) {
		boolean isValid = true;
		
		String query = "";
		Statement stmt = null;
		
		try {
			if (DBConnector.conn != null) {
				query = "SELECT * FROM myfuel.fuel_inventory_orders "
						+ "WHERE stationID = '" + stationID + "' AND fuelType = '" + fuelType + "';";
				stmt = DBConnector.conn.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				if (rs.next()) {
					isValid = false;
				}
			} else {
				System.out.println("Conn is null");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return isValid;
	}
	
	public void createInventoryOrderByFuelTypeAndStationID(String stationID, String fuelType) {
		JsonObject json = getFuelInventoryByStationIDAndFuelType(stationID, fuelType);
		//stationID, fuelType, currentFuelAmount, thresholdAmount, maxFuelAmount
		String orderDate = ObjectContainer.getCurrentDate();
		String orderStatus = "SENT_TO_STATION_MANAGER";
		String supplierID = getSupplierID();
		float fuelAmount = json.get("maxFuelAmount").getAsFloat() - json.get("currentFuelAmount").getAsFloat();
		
		float pricePerLitter = getFuelObjectByType(fuelType).getPricePerLitter();
		float totalPrice = fuelAmount * pricePerLitter;
		
		String query = "";
		Statement stmt = null;

		try {
			if (DBConnector.conn != null) {
				stmt = DBConnector.conn.createStatement();
				query = "INSERT INTO fuel_inventory_orders (stationID, supplierID, orderDate, orderStatus, fuelType, fuelAmount, totalPrice) "
						+ "VALUES ('" + stationID + "','" + supplierID + "','" + orderDate + "','"
						+ orderStatus + "','" + fuelType + "'," + fuelAmount + "," + totalPrice + ");";
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
