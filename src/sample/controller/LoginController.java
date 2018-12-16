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

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * 〈一句话功能简述〉<br>
 * 〈登陆界面控制器〉
 *
 * @author Zephon
 * @create 2018/11/30
 * @since 1.0.0
 */
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

    /**
     * 初始化
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        count = 0;
        nstage = new Stage();
        drawClient();
    }

    /**
     * 登陆判定（按钮）
     */
    @FXML
    public void loginClicked() {
        login();
    }

    /**
     * 登陆判定具体
     */
    String username;
    public void login() {
        Stage stage = (Stage) gp.getScene().getWindow();
        count++;
        username = textUser.getText();
        String pwd = textPwd.getText();
        if(MyDataBase.getInstance().checkedFlag(username)){
            Alert alert = new Alert(Alert.AlertType.WARNING, "该用户已被登陆", new ButtonType("退出", ButtonBar.ButtonData.YES));
            alert.setTitle("提示");
            Optional<ButtonType> bt = alert.showAndWait();
            if (bt.get().getButtonData().equals(ButtonBar.ButtonData.YES)) {
                alert.close();
            }
        }else if (MyDataBase.getInstance().checkedUser(username, pwd)) {
            if (menu.getText().equals("画手")) {
                draw();
            } else if (menu.getText().equals("猜者")) {
                guess();
            } else if (menu.getText().equals("管理员")) {
                manage();
            }
            MyDataBase.getInstance().update("update user set flag=1 where username='"+username+"'");
            nstage.show();
            stage.close();
        } else if (count < 3) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "用户名或密码错误", new ButtonType("确定", ButtonBar.ButtonData.YES));
            alert.setTitle("提示");
            Optional<ButtonType> bt = alert.showAndWait();
            if (bt.get().getButtonData().equals(ButtonBar.ButtonData.YES)) {
                textUser.clear();
                textPwd.clear();
                textUser.requestFocus();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "用户名或密码错误次数过多", new ButtonType("退出", ButtonBar.ButtonData.YES));
            alert.setTitle("提示");
            Optional<ButtonType> bt = alert.showAndWait();
            if (bt.get().getButtonData().equals(ButtonBar.ButtonData.YES)) {
                Platform.exit();
            }
        }
    }

    /**
     * 登陆设置快捷键
     *
     * @param event
     */
    @FXML
    public void loginPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            login();
        }
    }

    /**
     * 退出（按钮）
     */
    public void exitClicked() {
        System.exit(0);
    }

    /**
     * 画者-猜者-管理员选择框
     * 菜单选择框-通过设置名字进行login中的判定
     */
    @FXML
    public void drawClient() {
        menu.setText("画手");
    }

    /**
     * 画者-猜者-管理员选择框
     */
    @FXML
    public void guessClient() {
        menu.setText("猜者");
    }

    /**
     * 画者-猜者-管理员选择框
     */
    @FXML
    public void manageClient() {
        menu.setText("管理员");
    }

    /**
     * 设置画者界面属性
     */
    public void draw() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/DrawerClient.fxml"));
        loader.setController(new DrawerClientController(textUser.getText()));
        setting(loader);
    }

    /**
     * 设置猜者界面属性
     */
    public void guess() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/GuesserClient.fxml"));
        loader.setController(new GuesserClientController(textUser.getText()));
        setting(loader);
    }

    /**
     * 设置管理员界面属性
     */
    public void manage() {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("../view/Manager.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            nstage.setOnCloseRequest(event -> {
                MyDataBase.getInstance().update("update user set flag=0 where username='"+username+"'");
                System.exit(0);
            });
        }
        nstage.setTitle("你画我猜");
        nstage.getIcons().add(new Image("file:src/sample/images/icon.png"));
        Scene scene = new Scene(root, 500, 300);
        nstage.setScene(scene);
        nstage.setResizable(false);
    }

    /**
     * 画者与猜者界面共同方法的提取
     *
     * @param loader
     */
    public void setting(FXMLLoader loader) {
        try {
            Parent root = loader.load();
            nstage.setTitle("你画我猜");
            nstage.getIcons().add(new Image("file:src/sample/images/icon.png"));
            nstage.setScene(new Scene(root, 700, 460));
            nstage.setOnCloseRequest(event -> System.exit(0));
            nstage.setResizable(false);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            nstage.setOnCloseRequest(event -> {
                MyDataBase.getInstance().update("update user set flag=0 where username='"+username+"'");
                System.exit(0);
            });
        }
    }

    /**
     * 注册功能
     */
    @FXML
    public void register() {
        Stage s = new Stage();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("../view/Register.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        s.setTitle("你画我猜");
        s.getIcons().add(new Image("file:src/sample/images/icon.png"));
        Scene scene = new Scene(root, 400, 220);
        s.setScene(scene);
        s.setResizable(false);
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.ESCAPE), () -> {
            s.close();
        });
        s.show();
    }

}
