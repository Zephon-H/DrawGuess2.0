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

import sample.Database.MyDataBase;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author Zephon
 * @create 2018/11/26
 * @since 1.0.0
 */
public class ChattingServer {
    private List<Channel> list;
    private String title;
    private Set<String> right;
    public ChattingServer() throws IOException {
        ServerSocket serverSocket = new ServerSocket(8888);
        list = new ArrayList<>();
        right = new HashSet<>();
        title="";
        while (true) {
            Socket socket = serverSocket.accept();
            //一个accecpt,一个客户端
            Channel channel = new Channel(socket);
            list.add(channel);
            new Thread(channel).start();//一条道路
            //发送数据只能发给自己，为了发送给其它人，要用一个容器，每条数据遍历容器，发送给所有人
        }
    }

    public static void main(String[] args) throws IOException {
        new ChattingServer();
    }

    class Channel implements Runnable {
        private boolean isRunning = true;
        private DataInputStream dis;
        private DataOutputStream dos;
        private MyDataBase m;

        public Channel(Socket socket) {
            try {
                m = new MyDataBase();
                dos = new DataOutputStream(socket.getOutputStream());
                dis = new DataInputStream(socket.getInputStream());
            } catch (IOException e) {
                // e.printStackTrace();
                isRunning = false;
            }
        }

        private String receive() {
            String msg = "";
            try {
                    msg = dis.readUTF();
                    System.out.println(msg);
            } catch (IOException e) {
                e.printStackTrace();
                isRunning = false;
                list.remove(this);
            }
            String role = "";
            String str="";
            if (msg != null && !msg.equals("")) {
                str = msg.substring(0,2);
            }
            if(!str.equals("猜者")&&!str.equals("画者")&&str!=null&&!str.equals("")){
                title = str;
                System.out.println(str);
                String n="";
                for(String t:right)n+=(t+" ");
                System.out.println("正"+n);
                right.clear();
                if(!n.equals(""))
                return "上一局答对的人有"+n+"\n游戏开始\n";
                else return "游戏开始\n";
            }else{
                role = str;
            }
            String name = msg.substring(msg.indexOf('-')+1,msg.indexOf(':'));
            if (msg.contains(title) && role.equals("猜者") && title != null) {
               // m.delete("delete from title_table where title=" + "'" + title + "'");
                right.add(name);
                return msg + "系统消息：恭喜你答对了\n";
            } else return msg;
        }

        private void send(String msg) {
            if (msg != null && !msg.equals("")) {
                try {
                    dos.writeUTF(msg);
                    dos.flush();
                } catch (IOException e) {
                    // e.printStackTrace();
                    isRunning = false;
                    list.remove(this);
                }
            }
        }

        private void sendAll() {
            String msg = receive();
            if(msg.contains("答对了")){
                for (Channel c : list) {
                    if(c==this)
                    c.send(msg);
                }
            }
            else {
                for (Channel c : list) {
                    c.send(msg);
                }
            }
        }

        @Override
        public void run() {
            System.out.println(MyDataBase.current);
            while (isRunning) {
                sendAll();
            }
        }
    }
}

