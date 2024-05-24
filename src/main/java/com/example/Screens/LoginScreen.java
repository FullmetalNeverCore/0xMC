package com.mcl;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.application.Application;
import javafx.scene.layout.StackPane;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import java.util.List;

import javafx.concurrent.Task;
import javafx.application.Platform;

import javafx.scene.text.Font;
import javafx.scene.text.Text;

import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;



public class LoginScreen extends Application {
    private Stage _primaryStage;
    private Scene _scene; 
    private List<String> _versions; 
    private DataExchange _dataExchange = new DataExchange();
    private StackPane _root;
    private Font _font; 

    private Thread _workerThread;
    private Thread _gameThread; 

    @Override 
    public void start(Stage primaryStage) {
        this._font = Font.loadFont(getClass().getResourceAsStream("/fonts/Minecraft_Evenings.ttf"), 40);
        this._primaryStage = primaryStage;
        this._primaryStage.setTitle("0xMC - free launcher for pre-release Minecraft versions.");

        this._versions = _dataExchange.get_versions();
        this._root = new StackPane();


        //adding login box to screen 
        this._root.getChildren().add(createBox());


        this._scene = new Scene(_root, 800, 600);
        this._scene.getStylesheets().add(getClass().getResource("/theme.css").toExternalForm());
        this._primaryStage.setScene(this._scene);
        this._primaryStage.setResizable(false);
        this._primaryStage.show();
    }


    //TODO: login and version caching
    public VBox createBox() {

        Text status = new Text("Status: Standby");
        status.setStyle("-fx-fill: yellow;");

        VBox vbox = new VBox();
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);
    
        TextField uname = new TextField();
        uname.setPromptText("Username");
        uname.setPrefWidth(100);
    
        ComboBox<String> versionList = new ComboBox<>();
        for (String x : this._versions) {
            if (x.startsWith("a") || x.startsWith("b")) {
                versionList.getItems().add(x);
            }
        }
        versionList.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (CheckVersionExistance.isVersionExists(item)) {
                        setStyle("-fx-text-fill: green;");
                    } else {
                        setStyle("-fx-text-fill: black;");
                    }
                }
            }
        });
        versionList.setValue("Choose version.");
        versionList.setPrefWidth(100);
    
        Button login = new Button("Play");
    
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
    
        long totalPhysicalMemorySize = osBean.getTotalPhysicalMemorySize();
    
        long totalMemory = totalPhysicalMemorySize / (1024 * 1024);
        
        Text name = new Text("0xMC");
        name.setStyle("-fx-fill: gray;");
        name.setFont(this._font);

        Slider ramSlider = new Slider(0, totalMemory, totalMemory / 2);
        ramSlider.setShowTickLabels(true);
        ramSlider.setShowTickMarks(true);
        ramSlider.setMajorTickUnit(totalMemory / 4);
        ramSlider.setBlockIncrement(totalMemory / 10);
    
        Text selectedRam = new Text("Selected RAM: " + ((int) ramSlider.getValue()) + "MB");
        selectedRam.setStyle("-fx-fill: white;");
        ramSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            selectedRam.setText("Allocated RAM: " + newVal.intValue() + "MB");
        });
        


        login.setOnAction(e -> {
            // TODO: Login
            System.out.println(uname.getText() + " " + versionList.getValue() + " " + (int) ramSlider.getValue() + "MB.");
            //checking if minecraft folder and the game exists
            boolean pathexist = ExistenceChecker.version_exist(versionList.getValue());
            System.out.println(String.format("Converted value - %s",pathexist));
            
            //creating settings.json
            if (!(this._gameThread != null && this._gameThread.isAlive()) && !(this._workerThread != null && this._workerThread.isAlive())){
                InitializationHandler.init_client(uname.getText(), versionList.getValue(), (int) ramSlider.getValue(),"none");
            }
            try
            {
                if (!pathexist) {
                    System.out.println("Version does not exist.");
                    status.setText("Status: Downloading chosen version, please wait until the process is finished. Currently, the window could freeze.");
                    status.setStyle("-fx-fill: yellow;");
                    
                    if (this._workerThread != null && this._workerThread.isAlive()) {
                        System.out.println("Thread is still running.");
                    } else {
                        Task<Void> downloadTask = new Task<Void>() { 
                            @Override
                            protected Void call() throws Exception {
                                DownloadHandler.downloader();
                                return null;
                            }
                        };
                        
                        downloadTask.setOnSucceeded(event -> { 
                            System.out.println("Task completed!");
                            status.setText("Status: Download complete, run the game.");
                            status.setStyle("-fx-fill: green;");
                        });
                        
                        this._workerThread = new Thread(downloadTask);
                        this._workerThread.setDaemon(true);
                        this._workerThread.start();
                    }
                }                
                else 
                {

                    if (this._gameThread != null && this._gameThread.isAlive()) {
                        System.out.println("Game is still running.");
                    }
                    else
                    {
                        Task<Void> gameTask = new Task<Void>() {
                            
                            @Override
                            protected Void call() throws Exception {
                                InitializationHandler.init_client(uname.getText(), versionList.getValue(), (int) ramSlider.getValue(),"start");
                                return null;
                            }
                        };

                        gameTask.setOnSucceeded(event -> {
                            System.out.println("Version exists. Run the game...");
                            status.setText("Game has been stopped.");
                            status.setStyle("-fx-fill: yellow;");
                        });

                        this._gameThread = new Thread(gameTask);
                        this._gameThread.setDaemon(true);
                        this._gameThread.start();
                    }
                }
            }
            catch(Exception ex){
                System.out.println(ex.getMessage());
                status.setText("Status: Error occured");
                status.setStyle("-fx-fill: red;");
            }
        });
    
        VBox.setMargin(uname, new Insets(0, 0, 10, 0)); 
        VBox.setMargin(login, new Insets(0, 0, 10, 0)); 
        VBox.setMargin(versionList, new Insets(0, 0, 10, 0)); 
        VBox.setMargin(ramSlider, new Insets(0, 0, 10, 0)); 
    
        vbox.getChildren().addAll(name,uname, login, versionList, ramSlider, selectedRam,status);
        vbox.setMaxSize(300, 300);
    
        return vbox;
    }
    
}
