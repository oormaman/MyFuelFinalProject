package client.gui.customer;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;

import com.google.gson.JsonObject;

import client.controller.ClientUI;
import client.controller.ObjectContainer;
import entitys.Customer;
import entitys.Message;
import entitys.enums.MessageType;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class HomeHeatingFuelController { // extends ComboBox<LocalDate> {

	@FXML
	private Text txtPurchaseSuccessful;
	@FXML
	private Pane panePurchaseInputs;
	@FXML
	private Pane panePurchaseSuccessful;
	@FXML
	private Label lblCityError;
	@FXML
	private Label lblCurrentPricePerLitter;
	@FXML
	private TextField txtAmount;
	@FXML
	private TextField txtDateSupply;
	@FXML
	private TextField txtStreet;
	@FXML
	private Button btnSubmit;
	@FXML
	private CheckBox boxUrgentOrder;
	@FXML
	private ChoiceBox<String> cbPaymentMethod;
	@FXML
	private TextField txtCardNumber;
	@FXML
	private TextField txtCVV;
	@FXML
	private TextField txtDateValidation;
	@FXML
	private TextField txtPaymentMethod;
	@FXML
	private Pane mainPane;
	// @FXML
	// private Label lblAmountError;
	@FXML
	private Label lblAmountErrorMsg;
	@FXML
	private Label lblStreetErrorMsg;
	@FXML
	private Label lblPaymentErrorMsg;
	@FXML
	private Label lblCardNumberErrorMsg;
	@FXML
	private Label lblCVVErrorMsg;
	@FXML
	private Label dateValidationError;
	@FXML
	private Label dateErrorMsg;
	@FXML
	private Text txtcvv;
	@FXML
	private Text txtCardNum;
	@FXML
	private Text txtDateVal;
	@FXML
	private DatePicker datePickerDateSupply;
	@FXML
	private DatePicker datePickerDateValidation;
	@FXML
	private ChoiceBox<String> cbCreditCardMonthValidation;
	@FXML
	private ChoiceBox<String> cbCreditCardYearValidation;
	@FXML
	private Text txtTotalPrice;
	@FXML
	private Label lblShowTotalPrice;
	@FXML
	private Label lbltotalPriceAfterDiscount;
	@FXML
	private Label lblSubTotalPriceBeforeDiscount;
	@FXML
	private Label lblShippingRate;
	@FXML
	private Label lblDiscountRate;
	@FXML
	private ImageView imgAmountError;
	@FXML
	private ImageView imgStreetError;
	@FXML
	private ImageView imgCityError;
	@FXML
	private ImageView imgDateSupllyError;
	@FXML
	private ImageView imgPaymentMethodError;
	@FXML
	private ImageView imgCVVError;
	@FXML
	private ImageView imgCardNumberCVV;
	@FXML
	private ImageView imgDateValidationError;
	// Purchase pane:
	@FXML
	private Label lblOrderNumberOnPurchasePain;
	@FXML
	private Label lblDateSupplyInPurchasePane;
	@FXML
	private Label lblPaymentMethodOnPurchasePane;
	@FXML
	private Label lblTotalPriceOnPurchasePane;
	@FXML
	private Text txtOrderNumber;
	@FXML
	private Text txtDeliveryDetails;
	@FXML
	private Text txtDateSupplyOnPurchaseDetails;
	@FXML
	private Text paymentMethodOnPurchaseDetails;
	@FXML
	private Text totalPriceOnPurchaseDetails;
	@FXML
	private TextField txtCity;
	// Class variables:
	public Boolean isPressed = false;
	public String amount;
	public String street;
	public String isUrgentOrder;
	public String dateSuplly;
	public String paymentMethod;
	public String cardNumber;
	public String cvv;
	public String suplayDateValidation;

	public String customerId;
	public String orderId;
	public String totalPrice;
	public String orderDate;
	public String saleTemplateName;
	public double discountrate = 0;

	private float pricePerLitter;

	// public double subTotalPriceBeforeDiscount=0;
	@FXML
	public void load(Pane changePane) {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("HomeHeatingFuelForm.fxml"));
		try {
			mainPane = loader.load();
			changePane.getChildren().add(mainPane);
			ObjectContainer.homeHeatingFuelController = loader.getController();
			ObjectContainer.homeHeatingFuelController.initUI();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@FXML
	void onSubmit(ActionEvent event) {
		setErorLablesToNull();// Clean back Error message
		Boolean flag = homeHeatingFuelFormTest();
		if (flag) {
			this.saleTemplateName = "15%discount";
			// this.dateSuplly=datePickerDateSupply.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			this.amount = txtAmount.getText().toString().trim();
			this.street = txtStreet.getText().toString().trim();
			this.paymentMethod = cbPaymentMethod.getValue().toString().trim();
			this.dateSuplly = datePickerDateSupply.getValue().toString().trim();
			this.totalPrice = calcTotalPrice(Float.parseFloat(txtAmount.getText().trim()));
			this.orderDate = ObjectContainer.getCurrentDate();
			Customer customer = (Customer) ObjectContainer.currentUserLogin;
			this.customerId = customer.getCustomerId();

			JsonObject json = new JsonObject();
			json.addProperty("orderID", this.orderId);
			json.addProperty("customerId", this.customerId);
			json.addProperty("amount", this.amount);
			json.addProperty("street", this.street);
			json.addProperty("isUrgentOrder", this.isPressed.toString());
			json.addProperty("paymentMethod", this.paymentMethod);
			json.addProperty("dateSupplay", this.dateSuplly);
			json.addProperty("totalPrice", this.totalPrice);
			json.addProperty("paymentMethod", this.paymentMethod);
			json.addProperty("orderDate", this.orderDate);
			json.addProperty("saleTemplateName", this.saleTemplateName);
			json.addProperty("city", "Kiriat Haim");
			json.addProperty("stationId", "staition 2");

			if (paymentMethod.equals("Credit Card")) {
				String month = cbCreditCardMonthValidation.getValue();
				String year = cbCreditCardYearValidation.getValue();
				this.suplayDateValidation = month + "/" + year;
				this.cardNumber = txtCardNumber.getText().trim();
				this.cvv = txtCVV.getText().trim();
				json.addProperty("cardNubmer", this.cardNumber);
				json.addProperty("cvv", this.cvv);
				json.addProperty("suplayValidation", this.suplayDateValidation);

			}

			// Set new order in db:
			Message msg = new Message(MessageType.SUBMIT_HOME_HEATING_FUEL_ORDER, json.toString());
			ClientUI.accept(msg);

			getOrderIdFromDB(json);
			showPurchaseDetails();
		}
	}

	public void showPurchaseDetails() {
		panePurchaseInputs.setVisible(false);
		panePurchaseSuccessful.setVisible(true);
		lblOrderNumberOnPurchasePain.setText("make query for this");
		lblDateSupplyInPurchasePane.setText(this.dateSuplly);
		lblPaymentMethodOnPurchasePane.setText(this.paymentMethod);
		lblTotalPriceOnPurchasePane.setText(this.totalPrice);
	}

	@FXML
	void checkBoxPressed(ActionEvent event) {
		this.isPressed = !this.isPressed;
		if (this.isPressed) {
			datePickerDateSupply.setDisable(true);
			datePickerDateSupply.setValue(LocalDate.now());
			this.discountrate = 2;// For urgent order
			lblDiscountRate.setText(String.valueOf(this.discountrate) + "%");
		} else {
			datePickerDateSupply.setDisable(false);
			datePickerDateSupply.setValue(null);
			this.discountrate = 0;// For urgent order
			lblDiscountRate.setText(String.valueOf(this.discountrate) + "%");
		}
		calcTotalPrice(Float.parseFloat(txtAmount.getText().trim()));
	}

	public void initUI() {
		btnSubmit.setId("dark-blue");
		initCreditCardFields();
		setErorLablesToNull();
		panePurchaseSuccessful.setVisible(false);
		showCreditCardFields(false); // Hide the credit card fields in initialize
		initOrderSummary();
		updatePaymentFormOnPaymentMethodClick();

	}

	// **************************************************Test
	// fun**************************************************
	public Boolean homeHeatingFuelFormTest() {
		Boolean flag = true;
		flag = checkAmountField();
		flag = checkStreetField() && flag;
		flag = checkCityField() && flag;
		flag = checkDateSupplyField() && flag;
		flag = checkPaymentMethodField() && flag;
		if (cbPaymentMethod.getValue().trim().equals("Credit Card")) {
			flag = checkCardNumberField() && flag;
			flag = checkCVVField() && flag;
			flag = checkDateValidationField() && flag;
		}
		return flag;

	}

	public Boolean checkAmountField() {
		float fuelAmount = -1;
		this.amount = txtAmount.getText().trim();
		if (txtAmount.getText().toString() == null || txtAmount.getText().isEmpty()) {
			lblAmountErrorMsg.setText("Please fill amount");
			lblSubTotalPriceBeforeDiscount.setText("0.00 $");
			lbltotalPriceAfterDiscount.setText("0.00 $");
			setErrorImage(imgAmountError, "../../../images/error_icon.png");
			return false;
		}

		fuelAmount = Float.parseFloat(this.amount);
		// Check positive amount:
		if (fuelAmount <= 0) {
			lblAmountErrorMsg.setText("Invalide amount");
			setErrorImage(imgAmountError, "../../../images/error_icon.png");
			return false;
		}
		lblAmountErrorMsg.setText("");
		setErrorImage(imgAmountError, "../../../images/v_icon.png");
		return true;
	}

	public Boolean checkStreetField() {
		if (txtStreet.getText().toString() == null || txtStreet.getText().toString().isEmpty()) {
			lblStreetErrorMsg.setText("Please fill street");
			setErrorImage(imgStreetError, "../../../images/error_icon.png");
			return false;
		}
		setErrorImage(imgStreetError, "../../../images/v_icon.png");
		lblStreetErrorMsg.setText("");
		return true;

	}

	public Boolean checkCityField() {
		if (txtCity.getText() == null || txtCity.getText().toString().isEmpty()) {
			lblCityError.setText("Please fill city");
			setErrorImage(imgCityError, "../../../images/error_icon.png");
			return false;
		}
		setErrorImage(imgCityError, "../../../images/v_icon.png");
		lblCityError.setText("");
		return true;

	}

	public Boolean checkDateSupplyField() {
		if (datePickerDateSupply.getValue() == null || datePickerDateSupply.getValue().toString().trim().isEmpty()) {
			dateErrorMsg.setText("Please fill date supply");
			setErrorImage(imgDateSupllyError, "../../../images/error_icon.png");
			return false;
		}
		setErrorImage(imgDateSupllyError, "../../../images/v_icon.png");
		dateErrorMsg.setText("");
		return true;
	}

	public Boolean checkPaymentMethodField() {
		if (cbPaymentMethod.getValue().trim().equals(cbPaymentMethod.getItems().get(0))) {
			lblPaymentErrorMsg.setText("Please fill payment type");
			setErrorImage(imgPaymentMethodError, "../../../images/error_icon.png");
			return false;
		}
		setErrorImage(imgPaymentMethodError, "../../../images/v_icon.png");
		lblPaymentErrorMsg.setText("");
		return true;
	}

	public Boolean checkCardNumberField() {
		if (txtCardNum.getText().trim() == null || txtCardNum.getText().trim().isEmpty()) {
			lblCardNumberErrorMsg.setText("Please fill card number");
			setErrorImage(imgCardNumberCVV, "../../../images/error_icon.png");
			return false;
		}

		if (txtCardNum.getText().trim().length() <= 17 && txtCardNum.getText().trim().length() > 1) {
			lblCardNumberErrorMsg.setText("Please enter the full card number");
			setErrorImage(imgCardNumberCVV, "../../../images/error_icon.png");
			return false;
		}
		setErrorImage(imgCardNumberCVV, "../../../images/v_icon.png");
		lblCardNumberErrorMsg.setText("");
		return true;

	}

	public Boolean checkCVVField() {
		if (txtCVV.getText().toString() == null || txtCVV.getText().toString().isEmpty()) {
			setErrorImage(imgCVVError, "../../../images/error_icon.png");
			lblCVVErrorMsg.setText("Please fill CVV");
			return false;
		} else if (txtCVV.getText().trim().length() < 3 && txtCVV.getText().trim().length() >= 1) {
			lblCVVErrorMsg.setText("Please enter 3 CVV digit ");
			setErrorImage(imgCVVError, "../../../images/error_icon.png");
			return false;
		}
		setErrorImage(imgCVVError, "../../../images/v_icon.png");
		lblCVVErrorMsg.setText("");
		return true;

	}

	public Boolean checkDateValidationField() {
		if (cbCreditCardMonthValidation.getValue().trim().equals(cbCreditCardMonthValidation.getItems().get(0))) {
			dateValidationError.setText("Please fill month");
			setErrorImage(imgDateValidationError, "../../../images/error_icon.png");
			return false;
		}

		else if (cbCreditCardYearValidation.getValue().trim().equals(cbCreditCardYearValidation.getItems().get(0))) {
			dateValidationError.setText("Please fill year");
			setErrorImage(imgDateValidationError, "../../../images/error_icon.png");
			return false;
		}
		setErrorImage(imgDateValidationError, "../../../images/v_icon.png");
		dateValidationError.setText("");
		return true;
	}

	// **************************************************End test
	// fun**************************************************

	// **************************************************Initialize
	// function**************************************************
//	
//	public void initDateValidation() {
//		datePickerDateValidation.setShowWeekNumbers(false);
//	}

	private void showCreditCardFields(boolean flag) {
		txtCardNumber.setVisible(flag);
		txtCardNum.setVisible(flag);
		txtCVV.setVisible(flag);
		txtcvv.setVisible(flag);
		txtDateVal.setVisible(flag);
		cbCreditCardMonthValidation.setVisible(flag);
		cbCreditCardYearValidation.setVisible(flag);
		dateValidationError.setVisible(flag);
		lblCardNumberErrorMsg.setVisible(flag);
		lblCVVErrorMsg.setVisible(flag);
		imgCardNumberCVV.setVisible(flag);
		imgCVVError.setVisible(flag);
		imgDateValidationError.setVisible(flag);
	}

	public void initCreditCardFields() {
		cbPaymentMethod.getItems().add("Choose type");
		cbPaymentMethod.getItems().add("Cash");
		cbPaymentMethod.getItems().add("Credit Card");
		cbPaymentMethod.setValue(cbPaymentMethod.getItems().get(0));

		cbCreditCardMonthValidation.getItems().add("Month:");
		for (int i = 1; i <= 12; i++) {
			if (i < 10)
				cbCreditCardMonthValidation.getItems().add("0" + i);
			else
				cbCreditCardMonthValidation.getItems().add("" + i);
		}
		// Show year choice box:
		cbCreditCardMonthValidation.setValue(cbCreditCardMonthValidation.getItems().get(0));
		cbCreditCardYearValidation.getItems().add("Year:");
		for (int i = 2020; i <= 2030; i++) {
			cbCreditCardYearValidation.getItems().add("" + i);
		}
		cbCreditCardYearValidation.setValue(cbCreditCardYearValidation.getItems().get(0));
	}

	public void updatePaymentFormOnPaymentMethodClick() {
		cbPaymentMethod.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
				String value = cbPaymentMethod.getItems().get((Integer) number2);
				if (value.equals("Choose type") || value.equals("Cash"))
					showCreditCardFields(false);
				else if (value.equals("Credit Card")) {
					showCreditCardFields(true);

				}
			}
		});

	}

	public void setErorLablesToNull() {
		lblAmountErrorMsg.setText(" ");
		lblStreetErrorMsg.setText(" ");
		dateErrorMsg.setText(" ");
		lblPaymentErrorMsg.setText("");
		lblCardNumberErrorMsg.setText("");
		lblCVVErrorMsg.setText("");
		dateValidationError.setText("");
		lblCityError.setText("");
	}

	public void initOrderSummary() {
		lblSubTotalPriceBeforeDiscount.setText("0.00" + " $");
		lblShippingRate.setText("0.00 $");
		lblDiscountRate.setText("0 %");
		lbltotalPriceAfterDiscount.setText("0.00 $");
		lblCurrentPricePerLitter.setText(String.valueOf(getFuelObjectByType("Home Heating Fuel")) + "$");
	}
//**************************************************End Initialize function**************************************************

	// **************************************************Data Base
	// function**************************************************

	public float getFuelObjectByType(String fuelType) {
		JsonObject json = new JsonObject();
		json.addProperty("fuelType", fuelType);
		Message msg = new Message(MessageType.GET_FUEL_BY_TYPE, json.toString());
		ClientUI.accept(msg);

		JsonObject response = ObjectContainer.currentMessageFromServer.getMessageAsJsonObject();
		fuelType = response.get("fuelType").getAsString();
//		FuelType fuelTypeResponse = FuelType.stringToEnumVal(fuelType);
		pricePerLitter = response.get("pricePerLitter").getAsFloat();
		return pricePerLitter;
	}

	public void getOrderIdFromDB(JsonObject json) {
		// get last order id from db:
		Message msg = new Message(MessageType.GET_ORDER_ID, json.toString());
		ClientUI.accept(msg);
		JsonObject response = ObjectContainer.currentMessageFromServer.getMessageAsJsonObject();
		this.orderId = response.get("orderId").getAsString();
		this.orderId = response.get("orderId").getAsString();
	}
	// **************************************************End Initialize
	// function**************************************************

	@FXML
	void updateOrderSummary(KeyEvent event) {
		if (checkAmountField()) {
			calcTotalPrice(Float.parseFloat(txtAmount.getText().trim()));
		}
	}

	@FXML
	void PaymentMethodChosen(MouseDragEvent event) {// Don't delete!!
	}

	public void setErrorImage(ImageView img, String url) {
		Image image = new Image(getClass().getResource(url).toString());
		img.setImage(image);
	}

	public String calcTotalPrice(float amountOfLitters) {
		Boolean discountFlag = false;// If we have any discount change to true;
		double totalPriceAfterDiscount = 0, subTotalPriceBeforeDiscount = 0, commissionForUrgentorder = 0;
		if (checkAmountField()) {
			if (this.isPressed) {
				this.discountrate = 0;
				commissionForUrgentorder = 2;
				discountFlag = true;
				lblDiscountRate.setText(String.valueOf(this.discountrate) + " %");
			}
			if (amountOfLitters >= 600 && amountOfLitters <= 800) {
				this.discountrate = 3;
				discountFlag = true;
				lblDiscountRate.setText(String.valueOf(this.discountrate) + " %");
			} else if (amountOfLitters >= 800) {
				this.discountrate = 4;
				discountFlag = true;
				lblDiscountRate.setText(String.valueOf(this.discountrate) + " %");
			}
			// Calculate the price:
			subTotalPriceBeforeDiscount = (double) amountOfLitters * (double) pricePerLitter
					* (100 + commissionForUrgentorder) * 0.01;
			subTotalPriceBeforeDiscount = Double
					.parseDouble(new DecimalFormat("##.##").format(subTotalPriceBeforeDiscount));
			totalPriceAfterDiscount = Double.parseDouble(new DecimalFormat("##.##").format(totalPriceAfterDiscount));
			totalPriceAfterDiscount = subTotalPriceBeforeDiscount * (100 - this.discountrate) * 0.01;
			lblSubTotalPriceBeforeDiscount.setText(String.valueOf(subTotalPriceBeforeDiscount));
			lbltotalPriceAfterDiscount.setText(String.valueOf(totalPriceAfterDiscount));

			if (discountFlag)
				return String.valueOf(totalPriceAfterDiscount);
			return String.valueOf(subTotalPriceBeforeDiscount);
		}
		return null;
	}

}
