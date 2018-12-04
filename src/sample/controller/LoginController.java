package sample.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import sample.Database.MyDataBase;
import sample.Util.AppModel;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    private int count;

    @FXML
    private GridPane gp;
    @FXML
    private Button btLogin;
    @FXML
    public TextField textUser;
    @FXML
    private TextField textPwd;
    @FXML
    private MenuButton menu;
    private Stage nstage;


    private MainController mainController;
    //注入MainController
    void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }
    @FXML
    private TextField getTextUser(){
        return this.mainController.getUsername();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        count = 0;
        nstage = new Stage();
        drawClient();
    }

    @FXML
    public void loginClicked(){
        login();
    }
    public void login(){
        Stage stage = (Stage) gp.getScene().getWindow();
        count++;
        String username = textUser.getText();
        String pwd = textPwd.getText();
        MyDataBase m = new MyDataBase();
        if(m.checkedUser(username,pwd)){
            if(menu.getText().equals("画手")){
                draw();
            }else if(menu.getText().equals("猜者")){
                guess();
            }else if(menu.getText().equals("管理员")){
                manage();
            }
                nstage.show();
                stage.close();
        }else if(count<3){
            Alert alert = new Alert(Alert.AlertType.INFORMATION,"用户名或密码错误",new ButtonType("确定", ButtonBar.ButtonData.YES));
            alert.setTitle("提示");
            Optional<ButtonType> bt = alert.showAndWait();
            if(bt.get().getButtonData().equals(ButtonBar.ButtonData.YES)){
                textUser.clear();
                textPwd.clear();
                textUser.requestFocus();
            }
        }else{
            Alert alert = new Alert(Alert.AlertType.WARNING,"用户名或密码错误次数过多",new ButtonType("退出",ButtonBar.ButtonData.YES));
            alert.setTitle("提示");
            Optional<ButtonType> bt = alert.showAndWait();
            if(bt.get().getButtonData().equals(ButtonBar.ButtonData.YES)){
                Platform.exit();
            }
        }
    }

    @FXML
    public void loginPressed(KeyEvent event){
        if(event.getCode()==KeyCode.ENTER){
            login();
        }
    }


    public void exitClicked(){
        System.exit(0);
    }

    @FXML
    public void drawClient(){
        menu.setText("画手");
    }
    @FXML
    public void guessClient(){
        menu.setText("猜者");
    }
    @FXML
    public void manageClient(){menu.setText("管理员");}
    public void draw(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/DrawerClient.fxml"));
        loader.setController(new DrawerClientController(textUser.getText()));
        setting(loader);
    }

    public void guess(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/GuesserClient.fxml"));
        loader.setController(new GuesserClientController(textUser.getText()));
        setting(loader);
    }
    public void manage(){
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("../view/Manager.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        nstage.setTitle("你画我猜");
        nstage.getIcons().add(new Image("file:src/sample/images/icon.png"));
        Scene scene = new Scene(root,500,300);
        nstage.setScene(scene);
        nstage.setResizable(false);
    }

    public void setting(FXMLLoader loader){
        try {
            Parent root = loader.load();
            nstage.setTitle("你画我猜");
            nstage.setScene(new Scene(root, 700, 450));
            nstage.setOnCloseRequest(event -> System.exit(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void register(){
        Stage s = new Stage();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("../view/Register.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        s.setTitle("你画我猜");
        s.getIcons().add(new Image("file:src/sample/images/icon.png"));
        Scene scene = new Scene(root,400,220);
        s.setScene(scene);
        s.setResizable(false);
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.ESCAPE ), ()->{
            s.close();
        });
        s.show();
    }


}
