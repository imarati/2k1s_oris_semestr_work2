module ru.itis.gartik_phone {
    requires javafx.controls;
    requires javafx.fxml;


    opens ru.itis.gartik_phone to javafx.fxml;
    exports ru.itis.gartik_phone;
}