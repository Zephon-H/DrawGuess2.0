package sample.controller;


import javafx.application.Platform;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableNumberValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import sample.Util.Receive;
import sample.Util.Send;

import javax.imageio.ImageIO;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.ResourceBundle;

public class DrawerClientController implements Initializable {
    @FXML
    private Canvas canvas;
    @FXML
    private Button send;
    @FXML
    private TextArea input;
    @FXML
    private TextArea text;
    @FXML
    private VBox vBox;
    @FXML
    private ColorPicker colorPicker;
    @FXML
    private Slider slThickness;
    @FXML
    private MenuButton menuThickness;

    private GraphicsContext gc;
    private Paint color;
    private double startX, startY, endX, endY;
    private double thickness;
    private Socket picSocket;
    private Socket chattingSocket;
    private String name;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        color = Color.BLACK;
        gc = canvas.getGraphicsContext2D();
        gc.save();
        thickness = 2;
        canvas.widthProperty().bind(vBox.widthProperty().subtract(1));
        canvas.heightProperty().bind(vBox.heightProperty().subtract(1));
        startX = startY = endX = endY = 0;
        colorPicker.setValue(Color.BLACK);
        clear();
        draw();
        try {
            picSocket = new Socket("localhost", 9999);
            chattingSocket = new Socket("localhost", 8888);
            Send s = new Send(chattingSocket, input, send, "");
            s.send();
            new Thread(new Receive(chattingSocket, text)).start();
        } catch (Exception e) {
            System.out.println("图片服务器连接失败");
        }
    }

    @FXML
    public void slideChange() {
        slThickness.valueProperty().addListener((ov, old, n) -> {
            this.thickness = (double) n;
            menuThickness.setText("画笔" + String.format("%.2f", thickness));
        });
        gc.setLineWidth(thickness);
        gc.setStroke(color);
        paint();//
    }

    /**
     * 画笔---设置颜色---宽度
     */
    boolean flag = true;
    @FXML
    private void draw() {
        if(flag){
            Platform.runLater(this::clear);
            flag = false;
        }
        Image img = new Image("file:D:\\JavaCode\\DrawGuess2.0\\src\\sample\\images\\paint.png");
        canvas.setCursor(new ImageCursor(img, 12, 12));
        gc.setLineWidth(2);
        gc.setLineDashes(0);
        gc.setStroke(color);
        paint();
    }

    /**
     * 橡皮擦
     */
    @FXML
    public void erase() {
        Image img = new Image("file:D:\\JavaCode\\DrawGuess2.0\\src\\sample\\images\\cursor.png");
        canvas.setCursor(new ImageCursor(img, 5, 5));
        gc.setStroke(Color.rgb(80, 113, 91));
        gc.setLineWidth(20);
        paint();
    }

    public void paint() {
        sendPic();
        canvas.setOnMousePressed(event -> {
            startX = event.getX();
            startY = event.getY();
            sendPic();
        });
        canvas.setOnMouseDragged(event -> {
            endX = event.getX();
            endY = event.getY();
            gc.strokeLine(startX, startY, endX, endY);
            startX = endX;
            startY = endY;
            sendPic();
        });
        canvas.setOnMouseReleased(event -> {
            sendPic();
        });
    }

    /**
     * 设置颜色
     */
    @FXML
    public void setColor() {
        this.color = colorPicker.getValue();
        draw();
    }

    private int count = 0;

    @FXML
    public void drawLine() {
        draw();
        canvas.setOnMousePressed(event -> {
            sendPic();
            count++;
            if (count % 2 != 0) {
                startX = event.getX();
                startY = event.getY();
            } else {
                endX = event.getX();
                endY = event.getY();
            }
        });
        canvas.setOnMouseReleased(event -> {
            sendPic();
            gc.strokeOval(event.getX(), event.getY(), 0.5, 0.5);
            if (count % 2 == 0)
                gc.strokeLine(startX, startY, endX, endY);
        });
    }

    @FXML
    public void drawDottedLine() {
        draw();
        gc.setLineDashes(4);
    }

    /**
     * 画矩形，重复画时有bug
     */
    @FXML
    public void drawRec() {
        draw();
        canvas.setOnMousePressed(event -> {
            startX = event.getX();
            startY = event.getY();
            sendPic();
        });
        canvas.setOnMouseReleased(event -> {
            gc.strokeRect(startX, startY, Math.abs(endX - startX), Math.abs(endY - startY));
            sendPic();
        });
        canvas.setOnMouseDragged(event -> {
            gc.setStroke(Color.rgb(80, 113, 91));
            gc.strokeRect(startX, startY, Math.abs(endX - startX), Math.abs(endY - startY));
            endX = event.getX();
            endY = event.getY();
            gc.setStroke(color);
            gc.strokeRect(startX, startY, Math.abs(endX - startX), Math.abs(endY - startY));
            sendPic();
        });
    }

    /**
     * 画圆
     * 功能未完全实现
     */
    @FXML
    public void drawCircle() {
        draw();
        canvas.setOnMousePressed(event -> {
            startX = event.getX();
            startY = event.getY();
            sendPic();
        });
        canvas.setOnMouseReleased(event -> {
            gc.strokeOval(startX, startY, Math.abs(endX - startX), Math.abs(endY - startY));
            sendPic();
        });
        canvas.setOnMouseDragged(event -> {
            /*gc.setStroke(Color.WHITE);
            gc.strokeOval(startX,startY,Math.abs(endX-startX),Math.abs(endY-startY));*/
            endX = event.getX();
            endY = event.getY();
            // gc.setStroke(color);
            //gc.strokeOval(startX,startY,Math.abs(endX-startX),Math.abs(endY-startY));
            sendPic();
        });
    }


    /**
     * 清屏
     */
    @FXML
    public void clear() {
        gc.setFill(Color.rgb(80, 113, 91));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        sendPic();
    }

    public void sendPic() {
        WritableImage image = canvas.snapshot(new SnapshotParameters(), null);
        File file = new File("src/sample/images/pic.png");
        int length = 0;
        byte[] sendBytes = null;
        DataOutputStream dos = null;
        FileInputStream fis = null;
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            dos = new DataOutputStream(picSocket.getOutputStream());
            fis = new FileInputStream(file);
            sendBytes = new byte[1024];
            while ((length = fis.read(sendBytes, 0, sendBytes.length)) > 0) {
                dos.write(sendBytes, 0, length);
                dos.flush();
            }
            System.out.println("保存成功");
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("图片保存失败");
        }
    }
}
