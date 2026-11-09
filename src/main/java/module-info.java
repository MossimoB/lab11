module mossimo.bianco.lab11 {
    
    requires javafx.controls;
    requires javafx.fxml;

    exports mossimo.bianco.lab11;
    
    opens mossimo.bianco.lab11 to javafx.fxml;
    
    opens mossimo.bianco.lab11.controller to javafx.fxml;

    exports mossimo.bianco.lab11.model;
}
