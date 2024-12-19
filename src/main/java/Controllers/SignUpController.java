package Controllers;

import dao.SingletonConnection;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class SignUpController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;

    public void handleSignUp() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Error", "Please fill in all fields.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert("Error", "Passwords do not match.");
            return;
        }

        try (Connection conn = SingletonConnection.getConnection()) {
            // Insert the username and password directly into the database (without hashing)
            String query = "INSERT INTO users (username, password) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);  // Store the plain password

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                showAlert("Success", "Account created successfully!");
                // Redirect to SignIn page
            } else {
                showAlert("Error", "Could not create account.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleGoToSignIn() {
        try {
            // Load the sign-in page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/signin.fxml"));
            Scene scene = new Scene(loader.load());

            // Get the current stage and set the new scene
            Stage currentStage = (Stage) usernameField.getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.setTitle("Sign In");
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to load the sign-in page.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
}
