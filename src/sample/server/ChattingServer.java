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
import sample.Util.CloseUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * 〈一句话功能简述〉<br>
 * 〈聊天服务器〉
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
        list = new ArrayList<>();//list中放channel，代表各个客户端，以便将消息发送到每一个客户端
        right = new HashSet<>();//right中放答题正确的客户端对应的名字，并用Set保证不重复
        title = "";
        while (true) {
            Socket socket = serverSocket.accept();
            //一个accecpt,一个客户端
            Channel channel = new Channel(socket);
            list.add(channel);
            new Thread(channel).start();//一条道路
        }
    }

    /**
     * 启动服务器
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        new ChattingServer();
    }

    /**
     * 每个Channel代表一个客户端
     */
    class Channel implements Runnable {
        private boolean isRunning = true;//为true代表服务器在运行
        private DataInputStream dis;
        private DataOutputStream dos;

        /**
         * 初始化服务器
         *
         * @param socket
         */
        public Channel(Socket socket) {
            try {
                dos = new DataOutputStream(socket.getOutputStream());
                dis = new DataInputStream(socket.getInputStream());
            } catch (IOException e) {
                // e.printStackTrace();
                isRunning = false;
                list.remove(this);
                CloseUtil.closeAll(dos,dis);
            }
        }

        /**
         * 接受数据
         *
         * @return
         */
        private String receive() {
            String msg = "";
            try {
                msg = dis.readUTF();
                //System.out.println(msg);
            } catch (IOException e) {
                //e.printStackTrace();
                isRunning = false;
                list.remove(this);
                CloseUtil.closeAll(dos,dis);
            }
            String role = "";
            String str = "";

            if (msg.equals("@#$%*&暂停")) {
                title = "$#";
                return "游戏结束\n";//判断是否暂停
            }

            if (msg != null && !msg.equals("")) {
                str = msg.substring(0, 2);
            }
            if (!str.equals("猜者") && !str.equals("画者") && str != null && !str.equals("")) {
                title = str;
                String n = "";
                for (String t : right) n += (t + " ");
                right.clear();
                if (!n.equals(""))
                    return "上一局答对的人有" + n + "\n游戏开始\n";
                else return "游戏开始\n";
            } else {
                role = str;
            }
            try {
                String name = msg.substring(msg.indexOf('-') + 1, msg.indexOf(':'));
                if (msg.contains(title) && role.equals("猜者") && title != null) {
                    right.add(name);
                    return msg + "系统消息：恭喜你答对了\n";
                } else return msg;
            } catch (StringIndexOutOfBoundsException s) {
                isRunning = false;
                list.remove(this);
                CloseUtil.closeAll(dos,dis);
            }
            return msg;
        }

        /**
         * 发送数据只能发给自己，为了发送给其它人，要用一个容器，每条数据遍历容器，发送给所有人
         * 即sendAll()中实现的
         *
         * @param msg
         */
        private void send(String msg) {
            if (msg != null && !msg.equals("")) {
                try {
                    dos.writeUTF(msg);
                    dos.flush();
                } catch (IOException e) {
                    // e.printStackTrace();
                    isRunning = false;
                    list.remove(this);
                    CloseUtil.closeAll(dos,dis);
                }
            }
        }

        /**
         * 将数据发送给所有客户端
         */
        private void sendAll() {
            String msg = receive();
            if (msg.contains("答对了")) {
                for (Channel c : list) {//如果答对了，只将消息发送给答对者，以免妨碍其余人答题
                    if (c == this)
                        c.send(msg);
                }
            } else {
                for (Channel c : list) {
                    c.send(msg);
                }
            }
        }

        /**
         * 当isrunning为true时，服务器一直运行
         */
        @Override
        public void run() {
            while (isRunning) {
                sendAll();
            }
        }
    }
}

