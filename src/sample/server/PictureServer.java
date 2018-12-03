/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: Server01
 * Author:   Zephon
 * Date:     2018/11/26 22:33
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package sample.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author Zephon
 * @create 2018/11/26
 * @since 1.0.0
 */
public class PictureServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9999);
        List<PicChannel> list = new ArrayList<>();
        while(true) {
            Socket socket = serverSocket.accept();
            //一个accecpt,一个客户端
            PicChannel channel = new PicChannel(socket);
            list.add(channel);
            channel.setList(list);
            new Thread(channel).start();//一条道路
            //发送数据只能发给自己，为了发送给其它人，要用一个容器，每条数据遍历容器，发送给所有人
        }
    }
}
class PicChannel implements Runnable{
    private boolean isRunning = true;
    private InputStream is;
    private DataOutputStream dos;
    private List<PicChannel> list;
    private String title;
    private Socket s;

    public PicChannel(Socket socket){
        try {
            this.s = socket;
            dos = new DataOutputStream(socket.getOutputStream());
            is =socket.getInputStream();
        } catch (IOException e) {
            // e.printStackTrace();
            isRunning = false;
            list.remove(this);
        }
    }

    public void setList(List<PicChannel> list) {
        this.list = list;
    }

    private void receive(){
        byte[] buf = new byte[1024];
        int len = 0;
        FileOutputStream fos = null;
        try {
            while((len = is.read(buf)) != -1){  //阻塞
                if(fos == null) { fos = new FileOutputStream("D:\\JavaCode\\DrawGuess2.0\\src\\sample\\server\\ServerCache\\b.png"); }
                fos.write(buf, 0, len);
                //怎样判断文件接收完成？
                //1. 如果对方断开连接，本循环自动退出
                //2. 如果对方保持连接，用超时进行判断
                s.setSoTimeout(20); //设置50ms超时
            }
            System.out.println("完成接收");
        }  catch (SocketTimeoutException eTimeout) {

        } catch (IOException e){
            isRunning = false;
            list.remove(this);
        }
    }
    private void send(){
        int length = 0;
        FileInputStream fis;
        try {
            File file = new File("D:\\JavaCode\\DrawGuess2.0\\src\\sample\\server\\ServerCache\\b.png");
            fis = new FileInputStream(file);
            byte[] sendBytes = new byte[1024];
            while ((length = fis.read(sendBytes, 0, sendBytes.length)) > 0) {
                dos.write(sendBytes, 0, length);
                dos.flush();
                //s.setSoTimeout(50);
            }

        } catch (Exception e) {
            // isRunning=false;
            // list.remove(this);
        }

    }
    private void sendAll(){
        receive();
        for(PicChannel c:list){
            if(c!=this)
                c.send();
        }
    }

    @Override
    public void run() {
        while(isRunning){
            sendAll();
        }
    }
}