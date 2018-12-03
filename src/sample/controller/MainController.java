/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: MainController
 * Author:   Zephon
 * Date:     2018/12/3 14:44
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package sample.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author Zephon
 * @create 2018/12/3
 * @since 1.0.0
 */
public class MainController {
    @FXML
    private GuesserClientController guesserClientController;

    @FXML
    private DrawerClientController drawerClientController;

    @FXML
    private LoginController loginController;

    @FXML
    private void initialize() {
        loginController.injectMainController(this);
    }
    TextField getUsername(){
        return loginController.textUser;
    }
}