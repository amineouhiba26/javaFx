package Controllers;

import dao.SingletonConnection;
import entities.Product;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.TextField;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.lang.Double.parseDouble;

public class MainPageController {

    @FXML
    private Button addProductButton; // Button to add a new product
    @FXML
    private Button showProductsButton; // Button to show all products
    @FXML
    private Button deleteProductButton; // Button to delete selected product
    @FXML
    private ListView<String> productsListView; // ListView to display the list of products
    @FXML
    private VBox mainVBox; // Main VBox container for layout
    @FXML
    private TextField productNameField; // TextField for entering product name
    @FXML
    private TextField productPriceField; // TextField for entering product price
    @FXML
    private TextField productCategoryField; // TextField for entering product category

    // Define a Connection variable to keep the connection open, similar to SignInController
    private Connection conn;

    /**
     * Initializes the controller and sets up button actions.
     */
    public void initialize() {
        // Get the connection through the SingletonConnection
        conn = SingletonConnection.getConnection();

        // Action to add a new product
        addProductButton.setOnAction(event -> {
            String productName = productNameField.getText();
            double price = parseDouble(productPriceField.getText()); // Parse price input to double
            String category = productCategoryField.getText();
            Product p = new Product(productName, price, category);
            try {
                addNewProduct(p); // Add the product to the database
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error", "An error occurred while adding the product.");
            }
        });

        // Action to display all products
        showProductsButton.setOnAction(event -> showAllProducts());

        // Action to delete the selected product
        deleteProductButton.setOnAction(event -> handleDeleteProduct());
    }

    /**
     * Handles the deletion of the selected product.
     */
    public void handleDeleteProduct() {
        // Get the selected product from the ListView
        String selectedProduct = productsListView.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            showAlert("Error", "Please select a product to delete.");
            return;
        }

        // Extract the product ID from the selected product details
        int productId = extractProductId(selectedProduct);

        // Ask for confirmation before deletion
        boolean confirmation = showConfirmationDialog("Confirm Deletion", "Are you sure you want to delete this product?");

        if (confirmation) {
            try {
                deleteProductById(productId); // Delete the product by ID
                showAlert("Success", "Product deleted successfully!");
                showAllProducts();  // Refresh the product list after deletion
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to delete the product.");
            }
        }
    }

    /**
     * Displays a confirmation dialog with the given title and content.
     *
     * @param title The title of the confirmation dialog.
     * @param content The content of the confirmation dialog.
     * @return true if the user confirmed the action, false otherwise.
     */
    private boolean showConfirmationDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
        return alert.getResult().getText().equals("OK");
    }

    /**
     * Extracts the product ID from the selected product details string.
     *
     * @param productDetails The product details string (ID, name, price, etc.).
     * @return The extracted product ID.
     */
    private int extractProductId(String productDetails) {
        // Assuming product details include ID in the format: "ID: 1 | Name: ..."
        try {
            String[] details = productDetails.split(" \\| "); // Ensure the split works correctly
            if (details.length < 4) {
                throw new IllegalArgumentException("Invalid product format");
            }
            return Integer.parseInt(details[0].split(":")[1].trim()); // Extracting ID
        } catch (Exception e) {
            showAlert("Error", "Failed to extract product ID. Invalid format.");
            e.printStackTrace();
            return -1; // Return a sentinel value indicating failure
        }
    }

    /**
     * Adds a new product to the database.
     *
     * @param p The product to be added.
     * @throws SQLException if any SQL error occurs.
     */
    public void addNewProduct(Product p) throws SQLException {
        // Ensure the connection is open via SingletonConnection
        if (conn == null) {
            conn = SingletonConnection.getConnection();  // Get the connection if not already open
        }

        // Insert the new product into the database
        String insertQuery = "INSERT INTO products (name, price, category) VALUES (?, ?, ?)";
        try (PreparedStatement st = conn.prepareStatement(insertQuery)) {
            st.setString(1, p.getName());
            st.setDouble(2, p.getPrice());
            st.setString(3, p.getCategory());
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while adding the product.");
        }
    }

    /**
     * Displays all products in the ListView.
     */
    private void showAllProducts() {
        String query = "SELECT * FROM products";

        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            productsListView.getItems().clear(); // Clear the list view

            // Check if no products are found
            if (!rs.next()) {
                showAlert("Info", "No products found.");
                return;
            }

            // Add each product to the list view
            do {
                String productDetails = String.format("ID: %d | Name: %s | Price: %.2f | Category: %s",
                        rs.getInt("id"), rs.getString("name"), rs.getDouble("price"), rs.getString("category"));
                productsListView.getItems().add(productDetails);
            } while (rs.next());

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while retrieving the products: " + e.getMessage());
        }
    }

    /**
     * Clears the input fields.
     */
    private void clearFields() {
        productNameField.clear();
        productPriceField.clear();
        productCategoryField.clear();
    }

    /**
     * Displays an alert with the given title and content.
     *
     * @param title The title of the alert.
     * @param content The content of the alert.
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }

    /**
     * Deletes a product from the database by its ID.
     *
     * @param productId The ID of the product to be deleted.
     * @throws SQLException if any SQL error occurs.
     */
    public void deleteProductById(int productId) throws SQLException {
        // Ensure the connection is open via SingletonConnection
        if (conn == null) {
            conn = SingletonConnection.getConnection();  // Get the connection if not already open
        }

        String deleteQuery = "DELETE FROM products WHERE id=?";
        try (PreparedStatement st = conn.prepareStatement(deleteQuery)) {
            st.setInt(1, productId);
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to delete the product.");
        }
    }

    /**
     * Optional: Manually close the connection when it's no longer needed (like on application exit).
     */
    public void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
