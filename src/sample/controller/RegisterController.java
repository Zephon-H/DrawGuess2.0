/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: RegisterController
 * Author:   Zephon
 * Date:     2018/12/4 17:00
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package sample.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import sample.Database.MyDataBase;

import java.net.URL;
import java.util.Observable;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author Zephon
 * @create 2018/12/4
 * @since 1.0.0
 */
public class RegisterController implements Initializable {
    @FXML
    private TextField username;
    @FXML
    private PasswordField pwd;
    @FXML
    private Label label;
    @FXML
    private GridPane gp;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        pwd.textProperty().addListener((observable,old,newValue)->{
            check();
            pwd.setText(newValue);
        });
    }
    public boolean check(){
        MyDataBase d = new MyDataBase();
        if(d.checkedUser(username.getText())){
            label.setText("用户名已存在");
            return false;
        }else if(username.getText().length()<6||username.getText().trim().equals("")){
            label.setText("用户名不合法");
            return false;
        }else{
            label.setText("");
            return true;
        }
    }

    @FXML
    public void ok(){
        MyDataBase d = new MyDataBase();

        String msg="";
        if(!check()){
            msg = "用户名不合法";
        }else if(!d.checkedUser(username.getText())){
            d.insert("insert into user values('"+username.getText()+"','"+pwd.getText()+"')");
            msg = "注册成功";
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION,msg,new ButtonType("确定", ButtonBar.ButtonData.YES));
        alert.setTitle("提示");
        Optional<ButtonType> bt = alert.showAndWait();
        if(bt.get().getButtonData().equals(ButtonBar.ButtonData.YES)){
            if(msg.equals("注册成功"))
                cancel();
        }
    }
    @FXML
    public void cancel(){
        Stage s = (Stage) gp.getScene().getWindow();
        s.close();
    }

}