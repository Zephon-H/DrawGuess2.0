/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: AppModel
 * Author:   Zephon
 * Date:     2018/12/3 17:55
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package sample.Util;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author Zephon
 * @create 2018/12/3
 * @since 1.0.0
 */
public class AppModel {
    private final StringProperty text = new SimpleStringProperty();

    public StringProperty textProperty() {
        return text ;
    }

    public final String getText() {
        return textProperty().get();
    }

    public final void setText(String text) {
        textProperty().set(text);
    }
}