package mossimo.bianco.lab11.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import mossimo.bianco.lab11.model.Order;

public class SummaryController {

    // Labels used to display the order summary information.
    @FXML
    private Label entreeLabel;

    @FXML
    private Label drinkLabel;

    @FXML
    private Label requestsLabel;

    @FXML
    private Label waterLabel;

    @FXML
    private Label totalLabel;

    @FXML
    public void initialize() {

        // Apply the shared CSS theme once the summary window's scene is created.
        totalLabel.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null
                    && !newScene.getStylesheets().contains(OrderController.STYLESHEET)) {
                newScene.getStylesheets().add(OrderController.STYLESHEET);
            }
        });
    }

    // Receive the order and display its details in the summary window.
    public void setOrder(Order order) {

        // Display the selected entrée and its price.
        entreeLabel.setText(
                String.format(
                        "Entrée: %s - $%.2f",
                        order.getEntree(),
                        order.getEntreePrice()
                )
        );

        // Display the selected drink and its price.
        drinkLabel.setText(
                String.format(
                        "Drink: %s - $%.2f",
                        order.getDrink(),
                        order.getDrinkPrice()
                )
        );

        // Display "None" if no special requests were entered.
        String requests = order.getSpecialRequests().isBlank()
                ? "None"
                : order.getSpecialRequests();

        // Update the label with the customer's special requests.
        requestsLabel.setText(
                "Special Requests: " + requests
        );

        // Display whether free water was included in the order.
        waterLabel.setText(
                "Free Water: " + (order.isWater() ? "Yes" : "No")
        );

        // Display the final calculated order total.
        totalLabel.setText(
                String.format(
                        "Total: $%.2f",
                        order.getTotalPrice()
                )
        );
    }
}