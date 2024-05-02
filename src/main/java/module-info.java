module fi.sisu {
    requires javafx.controls;
    exports fi.sisu;
    requires com.google.gson;
    requires javafx.fxml;
    requires org.jsoup;

    opens fi.sisu to javafx.fxml;
}
