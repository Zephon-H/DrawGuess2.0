/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: Shape
 * Author:   Zephon
 * Date:     2018/12/14 9:14
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package sample.JavaBean;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author Zephon
 * @create 2018/12/14
 * @since 1.0.0
 */
public class Shape {
    double x,y;

    public Shape(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
    double width,height;

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }
}