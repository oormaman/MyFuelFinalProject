package client.gui.marketingmanager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import client.controller.ClientUI;
import client.controller.ObjectContainer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import entitys.Message;
import entitys.enums.MessageType;

public class ReportControler {

	@FXML
	private Pane reportPane;

	@FXML
	private Text txtReportType;

	@FXML
	private ChoiceBox<String> cbReportType;

	@FXML
	private Text txtStartDate;

	@FXML
	private DatePicker dpStartDate;

	@FXML
	private Text txtEndDate;

	@FXML
	private DatePicker dpEndDate;

	@FXML
	private Text txtSaleName;

	@FXML
	private ChoiceBox<String> cbSaleName;

	@FXML
	private Text txtQuarterly;

	@FXML
	private ChoiceBox<String> cbQuarterly;

	@FXML
	private Text txtYear;

	@FXML
	private ChoiceBox<String> cbYear;

	@FXML
	private Text txtFuelType;

	@FXML
	private ChoiceBox<String> cbFuelType;

	@FXML
	private Button btnGenerate;

	@FXML
	private Label lblErorrFields;

	@FXML
	void generateReport(ActionEvent event) {
		JsonObject request = new JsonObject();
		switch (ObjectContainer.currentUserLogin.getUserPermission()) {
		case MARKETING_MANAGER:
			if (checkInputForMarketingManager() == true) {
				createReportForMarketingManager(request);
			}
			break;
		case STATION_MANAGER:
			if (checkInputForStationManager() == true) {
				createReportForStationManager(request);
			}
			break;
		default:
		}
	}

	public void createReportForMarketingManager(JsonObject request) {
		int i;
		float countCustomer = 0;
		float countPurchases = 0;
		float countPayment = 0;
		
		request.addProperty("stationID", "station 1");
		if (cbReportType.getValue().equals(
				"Periodic characterization of clients")) {
			request.addProperty("reportType",
					"Periodic characterization of clients");
		} else {
			if (cbReportType.getValue().equals("Comments report")) {
				request.addProperty("reportType", "Comments report");
				request.addProperty("saleName", cbSaleName.getValue());
				Message msg = new Message(
						MessageType.GET_ORDERS_BY_STATIONID_AND_SALE_NAME,
						request.toString());
				ClientUI.accept(msg);

				JsonObject response = ObjectContainer.currentMessageFromServer
						.getMessageAsJsonObject();
				JsonArray homeHeatingFuelorders = response.get(
						"homeHeatingFuelOrders").getAsJsonArray();
				JsonArray fastFuelOrders = response.get("fastFuelOrders")
						.getAsJsonArray();

				HashMap<String, JsonObject> orders = new HashMap<>();
				for(i = 0; i < homeHeatingFuelorders.size(); i++){
					orders.put(homeHeatingFuelorders.get(i).getAsJsonObject().get("customerID").getAsString(),
							homeHeatingFuelorders.get(i).getAsJsonObject());
				}
				for(i = 0; i < fastFuelOrders.size(); i++){
					String customerID = fastFuelOrders.get(i).getAsJsonObject().get("customerID").getAsString();
					if(orders.containsKey(customerID)){
						JsonObject order = new JsonObject();
						float purchase1=Float.parseFloat(orders.get(customerID).get("sumOfPurchase").getAsString());
						float purchase2=Float.parseFloat(fastFuelOrders.get(i).getAsJsonObject().get("sumOfPurchase").getAsString());
						float sumPurchases=purchase1+purchase2;
						
						float pyment1=Float.parseFloat(orders.get(customerID).get("amountOfPayment").getAsString());
						float pyment2=Float.parseFloat(fastFuelOrders.get(i).getAsJsonObject().get("amountOfPayment").getAsString());
						float sumPayment=pyment1+pyment2;
						
						order.addProperty("customerID", customerID);
						order.addProperty("sumOfPurchase", sumPurchases );
						order.addProperty("amountOfPayment", sumPayment);
						orders.put(customerID, order);
					}else{
						orders.put(customerID, fastFuelOrders.get(i).getAsJsonObject());
					}
				}
				Set<String> keys = orders.keySet();
				for(String key : keys){
					countCustomer++;
					countPurchases+=orders.get(key).get("sumOfPurchase").getAsFloat();
					countPayment += orders.get(key).get("amountOfPayment").getAsFloat();
					System.out.println("key = " + key + " -> " +orders.get(key).toString());
				}
				System.out.println("total customer: "+countCustomer);
				System.out.println("total purchases: "+countPurchases);
				System.out.println("total pyment: "+countPayment);

			}
		}
	}

	public void createReportForStationManager(JsonObject request) {
		request.addProperty("stationID", "station 1");
		if (cbReportType.getValue().equals("Purchases By Type")) {
			request.addProperty("reportType", "Purchases By Type");
			request.addProperty("fuelType", cbFuelType.getValue());
			Message msg = new Message(
					MessageType.GET_ORDERS_BY_STATIONID_AND_FUEL_TYPE,
					request.toString());
			ClientUI.accept(msg);
			JsonObject response = ObjectContainer.currentMessageFromServer
					.getMessageAsJsonObject();
			JsonArray orders = response.get("orders").getAsJsonArray();

			for (int i = 0; i < orders.size(); i++) {
				JsonObject order = orders.get(i).getAsJsonObject();
				System.out.println(order.toString());
			}
		} else if (cbReportType.getValue().equals("Inventory items")) {
			// Transfer to a separate function -
			// "createInventoryItemsReport
			request.addProperty("stationID", "station 1");// delete
			Message msg = new Message(
					MessageType.GET_FUEL_INVENTORY_PER_STATION,
					request.toString());
			ClientUI.accept(msg);

			JsonObject response = ObjectContainer.currentMessageFromServer
					.getMessageAsJsonObject();
			JsonArray fuelInventory = response.get("fuelInventory")
					.getAsJsonArray();
			for (int i = 0; i < fuelInventory.size(); i++) {
				JsonObject order = fuelInventory.get(i).getAsJsonObject();
				System.out.println(order.toString());
			}
		} else {
			if (cbReportType.getValue().equals("Quarterly Revenue")) {
				request.addProperty("stationID", "station 1");
				request.addProperty("fuelType", "");
				request.addProperty("year", cbYear.getValue().toString());
				request.addProperty("quarter", cbQuarterly.getValue());
				Message msg = new Message(
						MessageType.GET_ORDERS_BY_STATIONID_AND_QUARTER,
						request.toString());
				ClientUI.accept(msg);

				JsonObject response = ObjectContainer.currentMessageFromServer
						.getMessageAsJsonObject();
				JsonArray homeHeatingFuelorders = response.get(
						"homeHeatingFuelOrders").getAsJsonArray();
				JsonArray fastFuelorders = response.get("fastFuelOrders")
						.getAsJsonArray();
				for (int i = 0; i < homeHeatingFuelorders.size(); i++) {
					JsonObject order = homeHeatingFuelorders.get(i)
							.getAsJsonObject();
					System.out.println(order.toString());
				}
				for (int i = 0; i < fastFuelorders.size(); i++) {
					JsonObject order = fastFuelorders.get(i).getAsJsonObject();
					System.out.println(order.toString());
				}
			}
		}
	}

	public void setReportTypeByUserPermissions() {
		switch (ObjectContainer.currentUserLogin.getUserPermission()) {
		case MARKETING_MANAGER:
			setOptionOfReportTypeOfMarketingManager();
			break;
		case STATION_MANAGER:
			setOptionOfReportTypeOfStationManager();
			break;
		default:
		}

	}

	public boolean checkInputForMarketingManager() {

		lblErorrFields.setText("");

		if (cbReportType.getValue().equals(
				"Periodic characterization of clients")) {
			if ((dpStartDate.getValue() == null)
					|| (dpEndDate.getValue() == null)) {
				lblErorrFields.setText("Please fill all fields!");
				return false;
			}
			// need add more test of date

			else {
				lblErorrFields.setText("");
				return true;
			}
		} else if (cbReportType.getValue().equals("Comments report")) {
			if (cbSaleName.getValue().equals("Choose sale name")) {
				lblErorrFields.setText("Please choose sale!");
				return false;
			} else {
				lblErorrFields.setText("");
				return true;
			}
		} else {
			lblErorrFields.setText("Please choose report!");
			return false;
		}
	}

	public boolean checkInputForStationManager() {

		lblErorrFields.setText("");

		if ((cbReportType.getValue().equals("Quarterly Revenue"))) {
			if (cbQuarterly.getValue().equals("Choose quarter")) {
				lblErorrFields.setText("Please choose quarter!");
				return false;
			} else if (cbYear.getValue().equals("")) {
				lblErorrFields.setText("Please choose year!");
				return false;
			} else {
				lblErorrFields.setText("");
				return true;
			}
		}

		else if (cbReportType.getValue().equals("Purchases By Type")) {
			if (cbFuelType.getValue().equals("Choose fuel type")) {
				lblErorrFields.setText("Please choose fuel type!");
				return false;
			} else {
				lblErorrFields.setText("");
				return true;
			}
		} else if (cbReportType.getValue().equals("Inventory items")) {
			lblErorrFields.setText("");
			return true;
		} else {
			lblErorrFields.setText("Please choose report type!");
			return false;
		}
	}

	public void setChoiceOptionOfChoiceBox(ChoiceBox<String> choiceBox,
			JsonArray choiceOption, String defualtValue) {
		int i;
		choiceBox.getItems().add(defualtValue);
		for (i = 0; i < choiceOption.size(); i++) {
			choiceBox.getItems().add(choiceOption.get(i).getAsString());
		}
		choiceBox.setValue(defualtValue);
	}

	public void setOptionOfReportTypeOfMarketingManager() {
		cbReportType.getItems().add("Choose type");
		cbReportType.getItems().add("Periodic characterization of clients");
		cbReportType.getItems().add("Comments report");
		cbReportType.setValue(cbReportType.getItems().get(0));

		Message msg = new Message(MessageType.GET_SALE_NAMES, "");
		ClientUI.accept(msg);

		// set option of sale
		JsonObject response = ObjectContainer.currentMessageFromServer
				.getMessageAsJsonObject();
		JsonArray saleNames = response.get("saleNames").getAsJsonArray();
		setChoiceOptionOfChoiceBox(cbSaleName, saleNames, "Choose sale name");
	}

	public void setOptionOfReportTypeOfStationManager() {

		cbReportType.getItems().add("Choose type");
		cbReportType.getItems().add("Quarterly Revenue");
		cbReportType.getItems().add("Purchases By Type");
		cbReportType.getItems().add("Inventory items");

		cbReportType.setValue(cbReportType.getItems().get(0));

		// set option of Quarterly
		cbQuarterly.getItems().add("Choose quarter");
		cbQuarterly.getItems().add("January - March");
		cbQuarterly.getItems().add("April - June");
		cbQuarterly.getItems().add("July - September");
		cbQuarterly.getItems().add("October - December");
		cbQuarterly.setValue(cbQuarterly.getItems().get(0));

		// set options of years
		for (int i = 2020; i <= 2030; i++)
			cbYear.getItems().add(i + "");
		cbYear.setValue(cbYear.getItems().get(0));

		// set option of fuel type
		Message msg = new Message(MessageType.GET_FUEL_TYPES, "");
		ClientUI.accept(msg);

		JsonObject response = ObjectContainer.currentMessageFromServer
				.getMessageAsJsonObject();
		JsonArray fuelTypes = response.get("fuelTypes").getAsJsonArray();
		setChoiceOptionOfChoiceBox(cbFuelType, fuelTypes, "Choose fuel type");
	}

	public void setVisibleCommentsReport(boolean flag) {
		txtSaleName.setVisible(flag);
		cbSaleName.setVisible(flag);
		if (flag)
			cbSaleName.setValue(cbSaleName.getItems().get(0));
	}

	public void setVisibleFuelTypeReport(boolean flag) {
		txtFuelType.setVisible(flag);
		cbFuelType.setVisible(flag);
		if (flag)
			cbFuelType.setValue(cbFuelType.getItems().get(0));
	}

	public void setVisibleQuartlyReport(boolean flag) {
		txtQuarterly.setVisible(flag);
		cbQuarterly.setVisible(flag);
		txtYear.setVisible(flag);
		cbYear.setVisible(flag);
		if (flag)
			cbQuarterly.setValue(cbQuarterly.getItems().get(0));
	}

	public void setVisiblePeriodicReport(boolean flag) {
		txtStartDate.setVisible(flag);
		dpStartDate.setVisible(flag);
		txtEndDate.setVisible(flag);
		dpEndDate.setVisible(flag);
	}

	public void showFieldsByReoprtType() {
		cbReportType.getSelectionModel().selectedIndexProperty()
				.addListener(new ChangeListener<Number>() {
					@Override
					public void changed(
							ObservableValue<? extends Number> observableValue,
							Number number, Number number2) {
						String value = cbReportType.getItems().get(
								(Integer) number2);

						lblErorrFields.setText("");
						if (value
								.equals("Periodic characterization of clients")) {
							setVisiblePeriodicReport(true);
							setVisibleFuelTypeReport(false);
							setVisibleCommentsReport(false);
							setVisibleQuartlyReport(false);
						} else if (value.equals("Comments report")) {
							setVisibleCommentsReport(true);
							setVisiblePeriodicReport(false);
							setVisibleFuelTypeReport(false);
							setVisibleQuartlyReport(false);

						} else if (value.equals("Quarterly Revenue")) {
							setVisibleQuartlyReport(true);
							setVisiblePeriodicReport(false);
							setVisibleFuelTypeReport(false);
							setVisibleCommentsReport(false);
						} else if (value.equals("Purchases By Type")) {
							setVisibleFuelTypeReport(true);
							setVisibleQuartlyReport(false);
							setVisiblePeriodicReport(false);
							setVisibleCommentsReport(false);
						} else {
							setVisibleQuartlyReport(false);
							setVisiblePeriodicReport(false);
							setVisibleFuelTypeReport(false);
							setVisibleCommentsReport(false);
						}
					}
				});
	}

	public String getMonth(String date) {
		String month = "";
		month = date.substring(3, 5);
		return month;
	}

	public void load(Pane paneChange) {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("ReportGeneration.fxml"));

		try {
			reportPane = loader.load();
			paneChange.getChildren().add(reportPane);
			ObjectContainer.reportController = loader.getController();
			ObjectContainer.reportController.initUI();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void initUI() {

		lblErorrFields.setText("");
		lblErorrFields.setVisible(true);
		setReportTypeByUserPermissions();
		showFieldsByReoprtType();

	}

}
