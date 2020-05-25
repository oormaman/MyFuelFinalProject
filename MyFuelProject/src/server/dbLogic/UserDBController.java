package server.dbLogic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.google.gson.JsonObject;

import entitys.enums.UserPermission;

public class UserDBController {

	// User Table structure
	// (userName, password, userPermisson, name , email , phoneNumber ,
	// lastLoginTime, isLogin)
	// (varChar , varChar , varChar , varChar , varChar , varChar , long , boolean)

	public boolean checkIfUsernameExist(String userName) {
		boolean isExist = false;

		String query = "";
		Statement stmt = null;

		try {
			if (DBConnector.conn != null) {
				query = "SELECT * FROM  users " + "WHERE userName ='" + userName + "';";
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

	// get json object that contains the username and the password.
	public JsonObject checkLogin(String userName, String password) {
		JsonObject response = new JsonObject();
		String errorMessage = "";
		boolean isValid = false;
		String query = "";
		Statement stmt = null;

		try {
			if (DBConnector.conn != null) {
				query = "SELECT * FROM  users " + 
						"WHERE userName ='" + userName + "' AND password = '" + password + "';";
				stmt = DBConnector.conn.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				if (rs.next()) {
					int isLogin = rs.getInt("isLogin");
					if (isLogin == 1) {
						errorMessage = "User is already logged in..";
					} else {
						isValid = true;
					}
				} else {
					errorMessage = "Invalid inputs..";
				}
			} else {
				System.out.println("Conn is null");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		response.addProperty("isValid", isValid);
		response.addProperty("errorMessage", errorMessage);
		return response;
	}

	public JsonObject getUserDetails(JsonObject json) {
		String query = "";

		String userName = json.get("userName").getAsString();
		JsonObject user = new JsonObject();

		Statement stmt = null;
		try {
			if (DBConnector.conn != null) {
				query = "SELECT * FROM  users " + 
						"WHERE userName ='" + userName + "';";
				stmt = DBConnector.conn.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				if (rs.next()) {
					user.addProperty("name", rs.getString("name"));
					user.addProperty("userPermission", rs.getString("userPermission"));
				}
			} else {
				System.out.println("Conn is null");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return user;
	}

	public boolean checkIfUserAlreadyLoggedIn(String userName) {
		boolean isLogin = false;
		String query = "";

		Statement stmt = null;
		try {
			if (DBConnector.conn != null) {
				query = "SELECT * FROM users " + "WHERE userName = '" + userName + "' AND isLogin = '1';";
				stmt = DBConnector.conn.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				if (rs.next()) {
					isLogin = true;
				}
			} else {
				System.out.println("Conn is null");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return isLogin;
	}

	// isLoginValue - 1 for login , 0 for logout.
	public void updateLoginFlag(String userName, int isLoginValue) {
		String query = "";
		Statement stmt = null;
		try {
			if (DBConnector.conn != null) {
				stmt = DBConnector.conn.createStatement();
				query = "UPDATE users " + "SET isLogin = " + isLoginValue + " WHERE userName = '" + userName + "';";
				stmt.executeUpdate(query);
			} else {
				System.out.println("Conn is null");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
