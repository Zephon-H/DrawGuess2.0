/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: PicReceiver
 * Author:   Zephon
 * Date:     2018/12/3 13:41
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package sample.Util;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author Zephon
 * @create 2018/12/3
 * @since 1.0.0
 */
public class PicReceiver implements Runnable {
    private boolean isRunning = true;
    private InputStream is;
    private Socket socket;
    private GraphicsContext gc;
    public  PicReceiver(GraphicsContext gc){
        try {
            this.socket = new Socket("localhost",9999);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            this.gc = gc;
            is =socket.getInputStream();
        } catch (IOException e) {
            //e.printStackTrace();
            isRunning = false;
        }
    }
    private void receive(){
        byte[] buf = new byte[1024];
        int len = 0;
        FileOutputStream fos = null;
        try {
            System.out.println("正在接收数据...");
            while((len = is.read(buf)) != -1){  //阻塞
                if(fos == null) { fos = new FileOutputStream("D:\\JavaCode\\DrawGuess2.0\\src\\sample\\images\\receive.png"); }
                fos.write(buf, 0, len);
                //怎样判断文件接收完成？
                //1. 如果对方断开连接，本循环自动退出
                //2. 如果对方保持连接，用超时进行判断
                socket.setSoTimeout(20); //设置100ms超时
            }
        }  catch (SocketTimeoutException eTimeout) {
            gc.drawImage(new Image("file:D:\\JavaCode\\DrawGuess2.0\\src\\sample\\images\\receive.png"),0,0);
        } catch (IOException e){
            isRunning = false;
        }
    }
    @Override
    public void run() {
        while(isRunning){
            receive();

        }
    }

}
