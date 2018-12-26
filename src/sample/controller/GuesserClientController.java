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

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sample.Database.MyDataBase;
import sample.Util.PicReceiver;
import sample.Util.PlayMusic;
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
    @FXML
    private Label lbState;
    @FXML
    private VBox vBox;
    @FXML
    private MenuItem bg;

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
        Platform.runLater(()-> PlayMusic.getInstance().play());
        try {
            System.out.println("name="+name);
            chattingSocket = new Socket("localhost",8888);
            gc = canvas.getGraphicsContext2D();
            System.out.println(".."+gc==null);
            canvas.widthProperty().bind(vBox.widthProperty().subtract(1));
            canvas.heightProperty().bind(vBox.heightProperty().subtract(1));
            new Thread(new Receive(chattingSocket, text)).start();
            new Thread(new PicReceiver(gc)).start();
            new AnimationTimer() {
                @Override
                public void handle(long now) {
                    if(!lbState.getText().equals("网络状态:未连接"))
                        lbState.setText("网络状态:已连接"+" 当前在线人数:"+ MyDataBase.getInstance().getOnlineNum());
                }
            }.start();
        } catch (Exception e) {
            //System.out.println("图片服务器连接失败");
        }

    }
    /**
     * 背景音乐控制
     */
    @FXML
    public void change(){
        if(bg.getText().equals("背景音乐-暂停")){
            PlayMusic.getInstance().pause();
            bg.setText("背景音乐-开始");
        }else{
            PlayMusic.getInstance().play();
            bg.setText("背景音乐-暂停");
        }
    }
    /**
     * 消息发送（按钮）
     */
    @FXML
    public void send(){
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
        MyDataBase.getInstance().update("update user set flag=0 where username='"+name+"'");
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