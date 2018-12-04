package sample.controller;


import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableNumberValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import sample.Database.MyDataBase;
import sample.Util.AppModel;
import sample.Util.Receive;
import sample.Util.Send;

import javax.imageio.ImageIO;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
    @FXML
    private Label titleLabel;
    @FXML
    private Label timeLabel;

    private GraphicsContext gc;
    private Paint color;
    private double startX, startY, endX, endY;
    private double thickness;
    private Socket picSocket;
    private Socket chattingSocket;
    private String name;
    private boolean isrunning;
    Timeline tl;

    public DrawerClientController(String name){
        this.name = name;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        time = 60;
        isrunning = false;
        tl = new Timeline();
        try {
            new Thread(()->{
                try {
                    picSocket = new Socket("localhost", 9999);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            //picSocket = new Socket("localhost", 9999);
            chattingSocket = new Socket("localhost", 8888);
            new Thread(new Receive(chattingSocket, text)).start();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("图片服务器连接失败");
        }
        System.out.println("name"+name+".");
        color = Color.BLACK;
        gc = canvas.getGraphicsContext2D();
        gc.save();
        thickness = 2;
        canvas.widthProperty().bind(vBox.widthProperty().subtract(1));
        canvas.heightProperty().bind(vBox.heightProperty().subtract(1));
        startX = startY = endX = endY = 0;
        colorPicker.setValue(Color.BLACK);
        Platform.runLater(this::clear);
        draw();
    }
    @FXML
    public void Send(){
        try {
            DataOutputStream dos = new DataOutputStream(chattingSocket.getOutputStream());
            String msg = input.getText();
            dos.writeUTF("画者-"+name + ":" + msg + "\n");
        } catch (IOException e) {

        }
        input.setText("");
        input.requestFocus();
    }

    public void Send(String title){
        try {
            DataOutputStream dos = new DataOutputStream(chattingSocket.getOutputStream());
            String msg = title;
            dos.writeUTF( msg);
        } catch (IOException e) {

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
    @FXML
    private void draw() {
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
    @FXML
    public void begin(){
        if(tl.getStatus().equals(Animation.Status.RUNNING)){
            return ;
        }
        isrunning = true;
        MyDataBase m = new MyDataBase();
        m.getTitle();
        String str = m.getCurrentTitle();
        titleLabel.setText("题目："+str);
        Send(str);
        TimeStart();
    }
    private int time;
    @FXML
    public void easy(){
        if(!isrunning)
        time = 90;
    }
    @FXML
    public void mid(){
        if(!isrunning)
        time = 60;
    }
    @FXML
    public void hard(){
        if(!isrunning)
        time = 30;
    }

    public void TimeStart(){
        tl = new Timeline(new KeyFrame(Duration.millis(1000), e->{
            timeLabel.setText("还剩:"+--time+"秒");
            if(time==0){
                System.out.println("over");
            }
        }));
        tl.setCycleCount(time);
        tl.play();
        tl.onFinishedProperty().set(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION,"时间到",new ButtonType("继续游戏",ButtonBar.ButtonData.YES),new ButtonType("休息一下",ButtonBar.ButtonData.NO));
            alert.setTitle("提示");

            alert.setOnHidden(ev->{
                if( alert.getResult().getButtonData().equals(ButtonBar.ButtonData.YES)){
                    begin();
                }else{
                    System.out.println("休息");
                    isrunning = false;
                }
            });
            alert.show();
        });

    }
    @FXML
    public void about(){
        Stage s = new Stage();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("../view/About.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        s.setTitle("你画我猜");
        s.getIcons().add(new Image("file:src/sample/images/icon.png"));
        Scene scene = new Scene(root,300,160);
        s.setScene(scene);
        s.setResizable(false);
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.ESCAPE ), ()->{
            s.close();
        });
        s.addEventHandler(MouseEvent.MOUSE_CLICKED, (event)->{
            s.close();
        });
        s.initStyle(StageStyle.TRANSPARENT);
        s.show();
    }
    @FXML
    public void exit(){
        System.exit(0);
    }

}
