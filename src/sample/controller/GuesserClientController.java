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
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import sample.Util.PicReceiver;
import sample.Util.Receive;
import sample.Util.Send;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author Zephon
 * @create 2018/12/2
 * @since 1.0.0
 */
public class GuesserClientController implements Initializable {
    private Socket picSocket;
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



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            name="";
            System.out.println("name="+name);
            chattingSocket = new Socket("localhost",8888);
            gc = canvas.getGraphicsContext2D();
            System.out.println(".."+gc==null);
            new Thread(new PicReceiver(gc)).start();
            new Thread(new Receive(chattingSocket,text)).start();
        } catch (Exception e) {
            //System.out.println("图片服务器连接失败");
        }

    }
    @FXML
    public void Send(){

        try {
            DataOutputStream dos = new DataOutputStream(chattingSocket.getOutputStream());
            String msg = input.getText();
            dos.writeUTF(name + ":" + msg + "\n");
        } catch (IOException e) {

        }
        input.setText("");
        input.requestFocus();
    }
}