package mossimo.bianco.lab11.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import mossimo.bianco.lab11.model.Order;
import mossimo.bianco.lab11.view.FallingLeaves;

public class OrderController {

    // Shared CSS theme for the order window, summary window and dialogs.
    private static final String CSS =
            ".root {"
            + " -fx-background-color: linear-gradient(to bottom, #fffaf4, #f6ebe0);"
            + " -fx-font-family: 'Segoe UI';"
            + " -fx-font-size: 13px; }"
            + ".label { -fx-text-fill: #2d2a32; }"

            // Menu bar
            + ".menu-bar { -fx-background-color: #c8553d; -fx-padding: 2 6; }"
            + ".menu-bar .menu .label { -fx-text-fill: white; }"
            + ".menu-bar .menu:hover, .menu-bar .menu:showing {"
            + " -fx-background-color: #a8432f; -fx-background-radius: 4; }"
            + ".context-menu { -fx-background-color: white;"
            + " -fx-background-radius: 6; -fx-border-color: #e3cfc0;"
            + " -fx-border-radius: 6; }"
            + ".menu-item .label { -fx-text-fill: #2d2a32; }"
            + ".menu-item:focused { -fx-background-color: #fbe9e2; }"

            // Entrée list
            + ".list-view { -fx-background-color: white;"
            + " -fx-background-radius: 8; -fx-border-color: #e3cfc0;"
            + " -fx-border-radius: 8; -fx-padding: 4; }"
            + ".list-cell { -fx-padding: 8 10; -fx-background-color: transparent;"
            + " -fx-text-fill: #2d2a32; }"
            + ".list-cell:filled:hover { -fx-background-color: #fbe9e2;"
            + " -fx-background-radius: 6; }"
            + ".list-cell:filled:selected { -fx-background-color: #c8553d;"
            + " -fx-background-radius: 6; -fx-text-fill: white; }"

            // Radio buttons and check box
            + ".radio-button, .check-box { -fx-cursor: hand; }"
            + ".radio-button .radio { -fx-background-color: #d9b8a5, white;"
            + " -fx-background-insets: 0, 1.5; -fx-background-radius: 1em; }"
            + ".radio-button:selected .radio { -fx-background-color: #c8553d, white; }"
            + ".radio-button:selected .dot { -fx-background-color: #c8553d; }"
            + ".check-box .box { -fx-background-color: #d9b8a5, white;"
            + " -fx-background-insets: 0, 1.5; -fx-background-radius: 4, 3; }"
            + ".check-box:selected .box { -fx-background-color: #c8553d, #c8553d; }"
            + ".check-box:selected .mark { -fx-background-color: white; }"

            // Special requests box
            + ".text-area { -fx-background-color: #e3cfc0, white;"
            + " -fx-background-insets: 0, 1; -fx-background-radius: 8, 7; }"
            + ".text-area:focused { -fx-background-color: #c8553d, white;"
            + " -fx-background-insets: 0, 2; }"
            + ".text-area .content { -fx-background-color: white;"
            + " -fx-background-radius: 7; }"

            // Separators, order card and totals
            + ".separator .line { -fx-border-color: #e3cfc0; }"
            + ".order-card { -fx-background-color: white;"
            + " -fx-background-radius: 12; -fx-border-color: #ecd9cc;"
            + " -fx-border-radius: 12;"
            + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 14, 0, 0, 3); }"
            + "#totalLabel { -fx-text-fill: #2a7f62; }"

            // Buttons and dialogs
            + ".button { -fx-background-color: #c8553d; -fx-text-fill: white;"
            + " -fx-background-radius: 6; -fx-padding: 6 18; -fx-cursor: hand; }"
            + ".button:hover { -fx-background-color: #a8432f; }"
            + ".dialog-pane { -fx-background-color: #fffaf4; }"
            + ".dialog-pane .header-panel { -fx-background-color: #c8553d; }"
            + ".dialog-pane .header-panel .label { -fx-text-fill: white;"
            + " -fx-font-size: 15px; -fx-font-weight: bold; }";

    // The CSS above, packed into a data URL so no separate file is needed.
    static final String STYLESHEET = "data:text/css;base64,"
            + Base64.getEncoder().encodeToString(
                    CSS.getBytes(StandardCharsets.UTF_8));

    // FXML controls used to collect and display order information.
    @FXML
    private ListView<String> entreeList;

    @FXML
    private RadioButton coffeeButton;

    @FXML
    private RadioButton teaButton;

    @FXML
    private RadioButton sodaButton;

    @FXML
    private RadioButton juiceButton;

    @FXML
    private ToggleGroup drinkGroup;

    @FXML
    private CheckBox waterCheckBox;

    @FXML
    private TextArea specialRequestsArea;

    @FXML
    private Label currentOrderLabel;

    @FXML
    private Label totalLabel;

    // Stores the current order and its information.
    private Order model;

    @FXML
    public void initialize() {

        // Create a new order when the controller starts.
        model = new Order();

        // Apply the CSS theme once the window's scene is created.
        entreeList.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null && !newScene.getStylesheets().contains(STYLESHEET)) {
                newScene.getStylesheets().add(STYLESHEET);
            }
            if (newScene != null) {
                FallingLeaves.addTo(newScene);
            }
        });

        // Turn the gray "Current Order" box into a white card.
        Region orderCard = (Region) currentOrderLabel.getParent();
        orderCard.setStyle("");
        orderCard.getStyleClass().add("order-card");

        // Put every drink button in the same group so only one can be selected.
        coffeeButton.setToggleGroup(drinkGroup);
        teaButton.setToggleGroup(drinkGroup);
        sodaButton.setToggleGroup(drinkGroup);
        juiceButton.setToggleGroup(drinkGroup);

        // Add Place Order and Clear Order buttons below the current order.
        Button placeOrderButton = new Button("Place Order");
        placeOrderButton.setOnAction(event -> placeOrder());

        Button clearOrderButton = new Button("Clear Order");
        clearOrderButton.setOnAction(event -> clearOrder());

        HBox buttonBar = new HBox(15, clearOrderButton, placeOrderButton);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);
        ((VBox) orderCard.getParent()).getChildren().add(buttonBar);

        // Populate the list with available entrées.
        entreeList.getItems().addAll(
                "Chicken Salad",
                "Turkey Sandwich",
                "Vegetable Wrap",
                "Grilled Cheese"
        );

        // Update the order whenever an entrée is selected.
        entreeList.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    model.setEntree(newValue);
                    updateOrderDisplay();
                });

        // Update the order whenever the selected drink changes.
        drinkGroup.selectedToggleProperty()
                .addListener((observable, oldValue, newValue) -> {

                    if (newValue != null) {
                        // Extract the drink name without its price.
                        RadioButton selectedButton =
                                (RadioButton) newValue;
                        model.setDrink(selectedButton.getText()
                                .substring(0, selectedButton.getText().indexOf(" (")));
                    } else {
                        model.setDrink(null);
                    }
                    updateOrderDisplay();
                });

        // Update the order when the water checkbox changes.
        waterCheckBox.selectedProperty()
                .addListener((observable, oldValue, newValue) -> {
                    model.setWater(newValue);
                    updateOrderDisplay();
                });

        // Save special requests whenever the text changes.
        specialRequestsArea.textProperty()
                .addListener((observable, oldValue, newValue) -> {
                    model.setSpecialRequests(newValue);
                    updateOrderDisplay();
                });

        // Display the initial order information and total.
        updateOrderDisplay();
    }

    // Refresh the order summary and calculate the displayed total.
    private void updateOrderDisplay() {
        StringBuilder orderText = new StringBuilder();

        // Display the selected entrée, if one exists.
        if (model.getEntree() != null) {
            orderText.append("Entrée: ")
                    .append(model.getEntree())
                    .append("\n");
        }

        // Display the selected drink, if one exists.
        if (model.getDrink() != null) {
            orderText.append("Drink: ")
                    .append(model.getDrink())
                    .append("\n");
        }

        // Display whether free water was selected.
        if (model.isWater()) {
            orderText.append("Free Water: Yes\n");
        }

        // Display any special requests entered by the customer.
        if (!model.getSpecialRequests().isBlank()) {
            orderText.append("Special Requests: ")
                    .append(model.getSpecialRequests())
                    .append("\n");
        }

        // Show a default message if no selections have been made.
        if (orderText.length() == 0) {
            orderText.append("No selections yet.");
        }

        // Update the order summary and total price labels.
        currentOrderLabel.setText(orderText.toString());
        totalLabel.setText(
                String.format("Total: $%.2f", model.getTotalPrice())
        );
    }

    // Validate the order and open the order summary window.
    @FXML
    private void placeOrder() {

        // Require an entrée before placing the order.
        if (model.getEntree() == null) {
            showError("Please select an entrée.");
            return;
        }

        // Require a drink before placing the order.
        if (model.getDrink() == null) {
            showError("Please select a drink.");
            return;
        }

        try {
            // Load the FXML file for the order summary window.
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/mossimo/bianco/lab11/view/secondary.fxml"
                    )
            );

            Parent root = loader.load();

            // Pass the current order to the summary controller.
            SummaryController controller = loader.getController();
            controller.setOrder(model);

            // Create and display the summary window.
            Stage summaryStage = new Stage();
            summaryStage.setTitle("Order Summary");
            summaryStage.initModality(Modality.NONE);
            summaryStage.setScene(new Scene(root));
            summaryStage.show();

        } catch (IOException e) {
            // Handle errors that occur while loading the summary window.
            e.printStackTrace();
            showError("Unable to open the order summary.");
        }
    }

    // Reset all selections and clear the current order.
    @FXML
    private void clearOrder() {
        model.clearOrder();
        entreeList.getSelectionModel().clearSelection();
        drinkGroup.selectToggle(null);
        waterCheckBox.setSelected(false);
        specialRequestsArea.clear();
        updateOrderDisplay();
    }

    // Remove the selected entrée from the order.
    @FXML
    private void clearEntree() {
        model.clearEntree();
        entreeList.getSelectionModel().clearSelection();
        updateOrderDisplay();
    }

    // Remove the selected drink from the order.
    @FXML
    private void clearDrink() {
        model.clearDrink();
        drinkGroup.selectToggle(null);
        updateOrderDisplay();
    }

    // Remove all special requests from the order.
    @FXML
    private void clearSpecialRequests() {
        model.clearSpecialRequests();
        specialRequestsArea.clear();
        updateOrderDisplay();
    }

    // Close the application.
    @FXML
    private void exitApplication() {
        System.exit(0);
    }

    // Display application information in an information dialog.
    @FXML
    private void showAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Campus Restaurant Ordering System");
        alert.setContentText(
                "Application: Campus Restaurant Ordering System\n"
                + "Version: 1.0\n"
                + "Developer: Mossimo Bianco\n"
                + "Course: Program Development in a Graphical Environment"
        );
        alert.getDialogPane().getStylesheets().add(STYLESHEET);
        alert.showAndWait();
    }

    // Display an error dialog with the provided message.
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Order");
        alert.setHeaderText("Order Incomplete");
        alert.setContentText(message);
        alert.getDialogPane().getStylesheets().add(STYLESHEET);
        alert.showAndWait();
    }
}