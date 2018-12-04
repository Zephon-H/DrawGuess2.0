/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: ManageController
 * Author:   Zephon
 * Date:     2018/12/4 18:12
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package sample.controller;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sample.Database.MyDataBase;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author Zephon
 * @create 2018/12/4
 * @since 1.0.0
 */
public class ManageController implements Initializable {
    @FXML
    AnchorPane pane;
    @FXML
    TextField path;
    @FXML
    ListView list;
    List<String> l;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        l = new ArrayList<>();
    }

    @FXML
    public void addWord(Event event) {
        l.clear();
        Stage stage = (Stage) pane.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(stage);
        path.setText(file.getName());
        try {
            BufferedReader bfr = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
            String str = null;
            int lineNumber = 0;
            while ((str = bfr.readLine()) != null) {
                lineNumber++;
                l.add(str);
            }
            ObservableList<String> items = FXCollections.observableArrayList(l);
            list.setItems(items);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void view() {
        MyDataBase m = new MyDataBase();
        List l = m.getTitle();
        ObservableList<String> items = FXCollections.observableArrayList(l);
        list.setItems(items);
    }

    @FXML
    public void ok() {
        new Thread(() -> {
            MyDataBase m = new MyDataBase();
            System.out.println(l);
            int len = m.getTitle().size();
            if (!l.isEmpty()) {
                System.out.println(l);
                for (String str : l) {
                    System.out.println(str);
                    System.out.println(m.checkedWord(str));
                    if (!m.checkedWord(str)) {
                        m.insert("insert into title_table values(" + (++len) + ",'" + str + "')");
                    }
                }
            }
        }).start();
    }
}