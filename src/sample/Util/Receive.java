/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: Receive
 * Author:   Zephon
 * Date:     2018/11/28 21:21
 * Description: 客户端接收线程类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package sample.Util;

import javafx.scene.control.TextArea;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * 〈一句话功能简述〉<br>
 * 〈客户端接收线程类〉
 *
 * @author Zephon
 * @create 2018/11/28
 * @since 1.0.0
 */
public class Receive implements Runnable {
    private boolean isRunning = true;
    private TextArea text;
    DataInputStream is;
    Socket socket;

    /**
     * 初始化
     *
     * @param socket
     * @param text
     */
    public Receive(Socket socket, TextArea text) {
        this.socket = socket;
        try {
            is = new DataInputStream(socket.getInputStream());
            this.text = text;
        } catch (IOException e) {
            // e.printStackTrace();
            System.out.println("接收失败");
            isRunning = false;
        }
    }

    /**
     * 接收消息
     */
    private void receive() {
        try {
            //System.out.println(is.readUTF());
            String msg = is.readUTF();
            text.appendText(msg);
        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("接受失败");
            isRunning = false;
        }
    }

    /**
     * 持续运行
     */
    @Override
    public void run() {
        while (isRunning) {
            receive();
        }
    }

}