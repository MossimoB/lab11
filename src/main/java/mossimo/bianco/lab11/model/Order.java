package mossimo.bianco.lab11.model;

import java.util.Map;

public class Order {

    // Stores the customer's selected items and special requests.
    private String entree;
    private String drink;
    private String specialRequests;
    private boolean water;

    // Stores the prices for each available entrée.
    private static final Map<String, Double> ENTREE_PRICES = Map.of(
            "Chicken Salad", 6.50,
            "Turkey Sandwich", 6.75,
            "Vegetable Wrap", 6.00,
            "Grilled Cheese", 5.00
    );

    // Stores the prices for each available drink.
    private static final Map<String, Double> DRINK_PRICES = Map.of(
            "Coffee", 1.50,
            "Tea", 1.50,
            "Soda", 2.00,
            "Juice", 1.75
    );

    // Initialize a new order with default values.
    public Order() {
        clearOrder();
    }

    // Getters

    // Return the currently selected entrée.
    public String getEntree() {
        return entree;
    }

    // Return the currently selected drink.
    public String getDrink() {
        return drink;
    }

    // Return the customer's special requests.
    public String getSpecialRequests() {
        return specialRequests;
    }

    // Return whether free water was selected.
    public boolean isWater() {
        return water;
    }

    // Setters

    // Update the selected entrée.
    public void setEntree(String entree) {
        this.entree = entree;
    }

    // Update the selected drink.
    public void setDrink(String drink) {
        this.drink = drink;
    }

    // Update the customer's special requests.
    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }

    // Update the free water selection.
    public void setWater(boolean water) {
        this.water = water;
    }

    // Get entrée price

    // Return the selected entrée's price, or zero if none is selected.
    public double getEntreePrice() {
        if (entree == null) {
            return 0.00;
        }

        return ENTREE_PRICES.get(entree);
    }

    // Get drink price

    // Return the selected drink's price, or zero if none is selected.
    public double getDrinkPrice() {
        if (drink == null) {
            return 0.00;
        }

        return DRINK_PRICES.get(drink);
    }

    // Calculate total

    // Add the entrée and drink prices to calculate the order total.
    public double getTotalPrice() {
        return getEntreePrice() + getDrinkPrice();
    }

    // Clear entire order

    // Reset all order selections and requests to their default values.
    public void clearOrder() {
        entree = null;
        drink = null;
        specialRequests = "";
        water = false;
    }

    // Clear individual selections

    // Remove the selected entrée.
    public void clearEntree() {
        entree = null;
    }

    // Remove the selected drink.
    public void clearDrink() {
        drink = null;
    }

    // Remove all special requests.
    public void clearSpecialRequests() {
        specialRequests = "";
    }
}