package Controllers;

import dao.SingletonConnection;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SignInController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    // Define a Connection variable to keep the connection open
    private Connection conn;

    public void handleSignIn() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Validate fields
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please fill in all fields.");
            return;
        }

        // Verify user credentials in the database
        try {
            conn = SingletonConnection.getConnection(); // Keep the connection open
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Success: Load the main page
                showAlert("Success", "Login successful!");
                loadMainPage();  // Redirect to the main page upon successful login
            } else {
                // Failure: Show error message
                showAlert("Error", "Invalid username or password.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while trying to sign in.");
        }
    }

    // Switch to the sign-up page
    public void handleGoToSignUp() {
        try {
            // Load the sign-up page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/signup.fxml"));
            Scene scene = new Scene(loader.load());

            // Get the current stage and set the new scene
            Stage currentStage = (Stage) usernameField.getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.setTitle("Sign Up");
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to load the sign-up page.");
        }
    }

    // Method to load the main page after a successful login
    private void loadMainPage() {
        try {
            // Load the MainPage FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainPage.fxml"));
            Scene scene = new Scene(loader.load());

            // Get the current stage (i.e., the sign-in window) and set the new scene
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);

            // Optionally, you can access the controller of the main page here if needed
            MainPageController controller = loader.getController();
            controller.initialize();  // Initialize the main page controller if needed

            stage.setTitle("Product Management");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the main page.");
        }
    }

    // Utility method to show alerts
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }

    // Optional: Manually close the connection when it's no longer needed (like on application exit)
    public void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
