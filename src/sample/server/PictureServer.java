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
 * 〈转发图片的服务器〉
 *
 * @author Zephon
 * @create 2018/11/26
 * @since 1.0.0
 */
public class PictureServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9999);
        List<PicChannel> list = new ArrayList<>();
        while (true) {
            Socket socket = serverSocket.accept();
            //一个accecpt,一个客户端
            PicChannel channel = new PicChannel(socket);
            list.add(channel);
            channel.setList(list);
            new Thread(channel).start();//一条道路
        }
    }
}

/**
 * 每个PicChannel代表一个客户端
 */
class PicChannel implements Runnable {
    private boolean isRunning = true;
    private InputStream is;
    private DataOutputStream dos;
    private List<PicChannel> list;
    private String title;
    private Socket s;

    /**
     * 初始化
     *
     * @param socket
     */
    public PicChannel(Socket socket) {
        try {
            this.s = socket;
            dos = new DataOutputStream(socket.getOutputStream());
            is = socket.getInputStream();
        } catch (IOException e) {
            // e.printStackTrace();
            isRunning = false;
            list.remove(this);
        }
    }

    /**
     * 因为没有用内部类，所以要将list数据传输进来
     *
     * @param list
     */
    public void setList(List<PicChannel> list) {
        this.list = list;
    }

    /**
     * 接受图片并在服务器缓存中暂时保存，此处用ServerCache作服务器缓存文件
     */
    private void receive() {
        byte[] buf = new byte[1024];
        int len = 0;
        FileOutputStream fos = null;
        try {
            while ((len = is.read(buf)) != -1) {  //阻塞
                if (fos == null) {
                    fos = new FileOutputStream("D:\\JavaCode\\DrawGuess2.0\\src\\sample\\server\\ServerCache\\b.png");
                }
                fos.write(buf, 0, len);
                //怎样判断文件接收完成？
                //1. 如果对方断开连接，本循环自动退出
                //2. 如果对方保持连接，用超时进行判断
                s.setSoTimeout(20); //设置50ms超时
            }
            System.out.println("完成接收");
        } catch (SocketTimeoutException eTimeout) {

        } catch (IOException e) {
            isRunning = false;
            list.remove(this);
        }
    }

    /**
     * 将接收的图片发送给客户端
     */
    private void send() {
        int length = 0;
        FileInputStream fis;
        try {
            File file = new File("D:\\JavaCode\\DrawGuess2.0\\src\\sample\\server\\ServerCache\\b.png");
            fis = new FileInputStream(file);
            byte[] sendBytes = new byte[1024];
            while ((length = fis.read(sendBytes, 0, sendBytes.length)) > 0) {
                dos.write(sendBytes, 0, length);
                dos.flush();
            }
        } catch (Exception e) {
             isRunning=false;
             list.remove(this);
        }
    }

    /**
     * 将图片发给除了自己以外的其它客户端
     */
    private void sendAll() {
        receive();
        for (PicChannel c : list) {
            if (c != this)
                c.send();
        }
    }

    /**
     * 循环执行服务器功能
     */
    @Override
    public void run() {
        while (isRunning) {
            sendAll();
        }
    }
}