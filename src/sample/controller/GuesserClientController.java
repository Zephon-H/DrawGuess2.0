/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: GuesserClientController
 * Author:   Zephon
 * Date:     2018/12/2 21:40
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package sample.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sample.Util.PicReceiver;
import sample.Util.Receive;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * 〈一句话功能简述〉<br> 
 * 〈猜者界面控制器〉
 *
 * @author Zephon
 * @create 2018/12/2
 * @since 1.0.0
 */
public class GuesserClientController implements Initializable {
    private GraphicsContext gc;
    private Socket chattingSocket;
    private String name;

    @FXML
    private Canvas canvas;
    @FXML
    private TextArea input;
    @FXML
    private TextArea text;
    @FXML
    private Button send;

    /**
     * 通过构造函数传输用户名的数据
     * @param name
     */
    public GuesserClientController(String name){
        this.name = name;
    }

    /**
     * 初始化
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            System.out.println("name="+name);
            chattingSocket = new Socket("localhost",8888);
            gc = canvas.getGraphicsContext2D();
            System.out.println(".."+gc==null);
            new Thread(new Receive(chattingSocket, text)).start();
            new Thread(new PicReceiver(gc)).start();
        } catch (Exception e) {
            //System.out.println("图片服务器连接失败");
        }

    }

    /**
     * 消息发送（按钮）
     */
    @FXML
    public void Send(){
        try {
            DataOutputStream dos = new DataOutputStream(chattingSocket.getOutputStream());
            String msg = input.getText();
            dos.writeUTF("猜者-"+name + ":" + msg + "\n");
        } catch (IOException e) {

        }
        input.setText("");
        input.requestFocus();
    }

    /**
     * 菜单中游戏-退出
     */
    @FXML
    public void exit() {
        System.exit(0);
    }
    /**
     * 菜单中帮助中关于选项
     */
    @FXML
    public void about() {
        Stage s = new Stage();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("../view/About.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        s.setTitle("你画我猜");
        s.getIcons().add(new Image("file:src/sample/images/icon.png"));
        Scene scene = new Scene(root, 300, 160);
        s.setScene(scene);
        s.setResizable(false);
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.ESCAPE), () -> {
            s.close();
        });
        s.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
            s.close();
        });
        s.initStyle(StageStyle.TRANSPARENT);
        s.show();
    }
}