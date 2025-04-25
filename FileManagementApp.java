import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class FileManagementApp extends Application {

    private TreeView<String> directoryTreeView;
    private TextField fileNameField;
    private ChoiceBox<String> operationChoice;
    private Label statusLabel;
    
    // For operation-specific fields
    private TextField newNameField;
    private TextField writePositionField;
    private TextField textToWriteField;
    private TextField moveFromField;
    private TextField moveToField;
    private TextField truncateFromField;
    private TextField truncateToField;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Initialize UI components
        initializeComponents();
        
        // Create main layout
        BorderPane root = new BorderPane();
        
        // Left side - Directory Tree
        VBox leftPane = new VBox(10, new Label("Directory Tree"), directoryTreeView);
        leftPane.setPadding(new Insets(10));
        root.setLeft(leftPane);
        
        // Right side - Operation Panel
        VBox rightPane = createOperationPanel();
        rightPane.setPadding(new Insets(10));
        root.setCenter(rightPane);
        
        // Bottom - File/Directory Management
        HBox bottomBar = createBottomBar();
        root.setBottom(bottomBar);
        
        // Set up stage
        Scene scene = new Scene(root, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("File Management System");
        primaryStage.show();
        
        // Initialize directory tree with sample data
        updateDirectoryTree();
    }

    private void initializeComponents() {
        directoryTreeView = new TreeView<>();
        fileNameField = new TextField();
        fileNameField.setPromptText("Enter file name");
        
        operationChoice = new ChoiceBox<>();
        operationChoice.getItems().addAll(
            "Open", 
            "Move/Rename", 
            "Write to file", 
            "Append to file", 
            "Show details", 
            "Move text within file", 
            "Truncate file"
        );
        operationChoice.setValue("Open");
        
        statusLabel = new Label("Ready");
        
        // Initialize operation-specific fields
        newNameField = new TextField();
        newNameField.setPromptText("New file name");
        
        writePositionField = new TextField();
        writePositionField.setPromptText("Position (bytes)");
        
        textToWriteField = new TextField();
        textToWriteField.setPromptText("Text to write");
        
        moveFromField = new TextField();
        moveFromField.setPromptText("From position");
        
        moveToField = new TextField();
        moveToField.setPromptText("To position");
        
        truncateFromField = new TextField();
        truncateFromField.setPromptText("From position");
        
        truncateToField = new TextField();
        truncateToField.setPromptText("To position");
    }

    private VBox createOperationPanel() {
        VBox operationPanel = new VBox(10);
        
        // File selection
        HBox fileSelectionBox = new HBox(10, new Label("File:"), fileNameField);
        
        // Operation selection
        HBox operationSelectionBox = new HBox(10, new Label("Operation:"), operationChoice);
        
        // Dynamic operation fields (initially hidden)
        VBox operationFields = new VBox(10);
        operationFields.setVisible(false);
        
        // Show appropriate fields based on selected operation
        operationChoice.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            operationFields.getChildren().clear();
            
            switch(newVal) {
                case "Move/Rename":
                    operationFields.getChildren().addAll(
                        new Label("New name:"),
                        newNameField
                    );
                    break;
                    
                case "Write to file":
                    operationFields.getChildren().addAll(
                        new Label("Position:"),
                        writePositionField,
                        new Label("Text:"),
                        textToWriteField
                    );
                    break;
                    
                case "Append to file":
                    operationFields.getChildren().addAll(
                        new Label("Text to append:"),
                        textToWriteField
                    );
                    break;
                    
                case "Move text within file":
                    operationFields.getChildren().addAll(
                        new Label("From position:"),
                        moveFromField,
                        new Label("To position:"),
                        moveToField,
                        new Label("Size:"),
                        textToWriteField
                    );
                    break;
                    
                case "Truncate file":
                    operationFields.getChildren().addAll(
                        new Label("From position:"),
                        truncateFromField,
                        new Label("To position:"),
                        truncateToField
                    );
                    break;
            }
            
            operationFields.setVisible(!newVal.equals("Open") && !newVal.equals("Show details"));
        });
        
        // Execute button
        Button executeButton = new Button("Execute");
        executeButton.setOnAction(e -> executeOperation());
        
        // Status label
        HBox statusBox = new HBox(statusLabel);
        
        operationPanel.getChildren().addAll(
            fileSelectionBox,
            operationSelectionBox,
            operationFields,
            executeButton,
            statusBox
        );
        
        return operationPanel;
    }

    private HBox createBottomBar() {
        // File creation
        TextField createNameField = new TextField();
        createNameField.setPromptText("Name");
        
        Button createFileButton = new Button("Create File");
        createFileButton.setOnAction(e -> {
            String fileName = createNameField.getText();
            if (!fileName.isEmpty()) {
                String result = runPythonCommand("create", fileName);
                statusLabel.setText(result);
                updateDirectoryTree();
                createNameField.clear();
            }
        });
        
        // Directory creation
        Button createDirButton = new Button("Create Directory");
        createDirButton.setOnAction(e -> {
            String dirName = createNameField.getText();
            if (!dirName.isEmpty()) {
                String result = runPythonCommand("mkdir", dirName);
                statusLabel.setText(result);
                updateDirectoryTree();
                createNameField.clear();
            }
        });
        
        // Delete
        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            String name = fileNameField.getText();
            if (!name.isEmpty()) {
                String result = runPythonCommand("delete", name);
                statusLabel.setText(result);
                updateDirectoryTree();
            }
        });
        
        // Change directory
        Button changeDirButton = new Button("Change Directory");
        changeDirButton.setOnAction(e -> {
            String dirName = showInputDialog("Enter directory name:");
            if (dirName != null && !dirName.isEmpty()) {
                String result = runPythonCommand("cd", dirName);
                statusLabel.setText(result);
                updateDirectoryTree();
            }
        });
        
        HBox bottomBar = new HBox(10, 
            createNameField, 
            createFileButton, 
            createDirButton, 
            deleteButton, 
            changeDirButton
        );
        bottomBar.setPadding(new Insets(10));
        
        return bottomBar;
    }

    private void executeOperation() {
        String fileName = fileNameField.getText();
        if (fileName.isEmpty()) {
            statusLabel.setText("Please enter a file name");
            return;
        }
        
        String operation = operationChoice.getValue();
        String result = "";
        
        switch(operation) {
            case "Open":
                openFileWindow(fileName);
                break;
                
            case "Move/Rename":
                String newName = newNameField.getText();
                if (newName.isEmpty()) {
                    statusLabel.setText("Please enter a new name");
                    return;
                }
                result = runPythonCommand("move", fileName, newName);
                fileNameField.setText(newName);
                newNameField.clear();
                break;
                
            case "Write to file":
                String position = writePositionField.getText();
                String text = textToWriteField.getText();
                if (position.isEmpty() || text.isEmpty()) {
                    statusLabel.setText("Please enter both position and text");
                    return;
                }
                result = runPythonCommand("write_at", fileName, position, text);
                writePositionField.clear();
                textToWriteField.clear();
                break;
                
            case "Append to file":
                text = textToWriteField.getText();
                if (text.isEmpty()) {
                    statusLabel.setText("Please enter text to append");
                    return;
                }
                result = runPythonCommand("append", fileName, text);
                textToWriteField.clear();
                break;
                
            case "Show details":
                result = runPythonCommand("details", fileName);
                break;
                
            case "Move text within file":
                String from = moveFromField.getText();
                String to = moveToField.getText();
                String size = textToWriteField.getText();
                if (from.isEmpty() || to.isEmpty() || size.isEmpty()) {
                    statusLabel.setText("Please enter all parameters");
                    return;
                }
                result = runPythonCommand("move_within_file", fileName, from, to, size);
                moveFromField.clear();
                moveToField.clear();
                textToWriteField.clear();
                break;
                
            case "Truncate file":
                from = truncateFromField.getText();
                to = truncateToField.getText();
                if (from.isEmpty() || to.isEmpty()) {
                    statusLabel.setText("Please enter both positions");
                    return;
                }
                result = runPythonCommand("truncate", fileName, from, to);
                truncateFromField.clear();
                truncateToField.clear();
                break;
        }
        
        statusLabel.setText(result);
        updateDirectoryTree();
    }

    private void openFileWindow(String fileName) {
        Stage fileWindow = new Stage();
        fileWindow.setTitle("File Content: " + fileName);
        
        TextArea contentArea = new TextArea();
        contentArea.setEditable(false);
        contentArea.setText(getFileContent(fileName));
        
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> fileWindow.close());
        
        VBox layout = new VBox(10, contentArea, closeButton);
        layout.setPadding(new Insets(10));
        
        fileWindow.setScene(new Scene(layout, 400, 300));
        fileWindow.show();
    }

    private String showInputDialog(String message) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Input");
        dialog.setHeaderText(null);
        dialog.setContentText(message);
        
        return dialog.showAndWait().orElse("");
    }
    private void updateDirectoryTree() {
        String output = runPythonCommand("list");
        String[] fileNames = output.split("\n");
    
        TreeItem<String> root = new TreeItem<>("Root");
        for (String name : fileNames) {
            root.getChildren().add(new TreeItem<>(name));
        }
        root.setExpanded(true);
        directoryTreeView.setRoot(root);
    }
    

    private String getFileContent(String fileName) {
        return runPythonCommand("read", fileName);
    }
    

    public static String runPythonCommand(String... args) {
        try {
            List<String> command = new ArrayList<>();
            command.add("python");
            command.add("interface.py");
            command.addAll(Arrays.asList(args));
    
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            Process process = pb.start();
    
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
    
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                return "Error: Process exited with code " + exitCode;
            }
    
            return output.toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error running command: " + e.getMessage();
        }
    }
}