/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: MyDataBase
 * Author:   Zephon
 * Date:     2018/11/29 22:04
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package sample.Database;

import java.sql.*;
import java.util.*;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author Zephon
 * @create 2018/11/29
 * @since 1.0.0
 */
public class MyDataBase {
    private static String driver = "com.mysql.jdbc.Driver";
    private static String url = "jdbc:mysql://localhost:3333/";
    private static String db = "javawork";
    private static String character = "?useUnicode=true&characterEncoding=gbk";
    private static String user = "root";
    private static String pass = "";

    static Connection conn = null;
    static Statement statement = null;
    static PreparedStatement ps = null;
    static ResultSet rs = null;
    List<Map> list = new ArrayList<Map>();//返回所有记录

    public MyDataBase(){
    }
    public boolean checkedUser(String name,String pwd){
        list = query("select * from user");
        boolean flag = false;
        for(Map map:list){
            if (map.get(name)!=null) {
                if(map.get(name).equals(pwd)){
                    flag = true;
                }
            }

        }
        return flag;
    }

    public static void main(String[] args){
        MyDataBase m = new MyDataBase();
        m.query("select * from user");
       // m.insert("insert into title_table (id,title) value (10,'三长两短');");
       /* m.getTitle().forEach(a->System.out.println(a));
        List list = m.getTitle().subList(0, 4);
        list.forEach(a->System.out.println(a));
        String str = (String) list.get(0);
        System.out.println("asd"+str);
        m.delete("delete from title_table where title="+"'"+str+"'");*/
       // m.update("update title_table set id = -1 while title="+str);
        //System.out.println(m.getCurrentTitle());

    }

    public String getCurrentTitle(){
        String str="";
        list = query("select * from title_table");
        for(Map m:list){
            if(m.get(-1)!=null)
                str= (String) m.get(-1);
        }
        return str;
    }

    public List getTitle(){
        List<String> l = new ArrayList<>();
        list = query("select * from title_table");
        list.forEach(map->{
            l.add((String)map.get(list.indexOf(map)));
        });
        Collections.shuffle(l);
        return l;
    }

    /**
     * 连接数据库
     */
    public static void connDB() {
        try {
            Class.forName(driver).newInstance();
            conn = DriverManager.getConnection(url+db+character, user, pass);
            if (!conn.isClosed()) {
                System.out.println("Succeeded connecting to MySQL!");
            }

            statement = conn.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*
     * 关闭数据库
     */
    public static void closeDB() {
        if(rs != null ){
            try {
                rs.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if(statement != null){
            try {
                statement.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if(conn != null){
            try {
                conn.close();
                System.out.println("Database connection terminated!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * 查询数据表
     */
    public List query(String sql){
        connDB();
        int count;
        try {
            rs = statement.executeQuery(sql);
            while(rs.next()){
                Map  map = new HashMap();
                map.put(rs.getObject(1),rs.getObject(2));
                list.add(map);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            closeDB();
        }
        return list;
    }

    /*
     * 数据插入
     */
    public void insert(String sql){
        connDB();
        try {
            statement.execute(sql);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally {
            closeDB();
        }
    }

    /*
     * 数据更新
     */
    public void update(String sql){
        connDB();
        try {
            statement.execute(sql);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally {
            closeDB();
        }
    }

    /*
     * 数据删除
     */
    public void delete(String sql){
        connDB();
        try {
            statement.execute(sql);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally {
            closeDB();
        }
    }
}
