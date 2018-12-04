package sample.controller;


import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import sample.Database.MyDataBase;
import sample.Util.Receive;

import javax.imageio.ImageIO;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * 〈一句话功能简述〉<br>
 * 〈画者界面控制器〉
 *
 * @author Zephon
 * @create 2018/12/2
 * @since 1.0.0
 */
public class DrawerClientController implements Initializable {
    @FXML
    private Canvas canvas;

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
    private int count = 0;
    private int time;

    /**
     * 通过构造函数，从登陆界面传输用户名数据到画手界面
     * @param name
     */
    public DrawerClientController(String name) {
        this.name = name;
    }

    /**
     * 初始化
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        isrunning = false;
        //hard();
        easy();
        tl = new Timeline();
        try {
            new Thread(() -> {
                try {
                    picSocket = new Socket("localhost", 9999);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            chattingSocket = new Socket("localhost", 8888);
            new Thread(new Receive(chattingSocket, text)).start();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("图片服务器连接失败");
        }
        System.out.println("name" + name + ".");
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

    /**
     * 点击“发送”按钮 实现聊天消息发送
     */
    @FXML
    public void Send() {
        try {
            DataOutputStream dos = new DataOutputStream(chattingSocket.getOutputStream());
            String msg = input.getText();
            dos.writeUTF("画者-" + name + ":" + msg + "\n");
        } catch (IOException e) {
            System.out.println("服务器异常");
        }
        input.setText("");
        input.requestFocus();
    }

    /**
     * 发送特定的消息
     *
     * @param title
     */
    public void Send(String title) {
        try {
            DataOutputStream dos = new DataOutputStream(chattingSocket.getOutputStream());
            String msg = title;
            dos.writeUTF(msg);
        } catch (IOException e) {
            System.out.println("服务器异常");
        }
    }


    /**
     * 修改画笔的粗细（按钮）
     */
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
     * 画笔按钮 默认画笔
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
     * 橡皮擦功能
     */
    @FXML
    public void erase() {
        Image img = new Image("file:D:\\JavaCode\\DrawGuess2.0\\src\\sample\\images\\cursor.png");
        canvas.setCursor(new ImageCursor(img, 5, 5));
        gc.setStroke(Color.rgb(80, 113, 91));
        gc.setLineWidth(20);
        paint();
    }

    /**
     * 实现鼠标绘制功能
     * 每次鼠标按下、松开、拖拽都对应相应的数据改变与发送
     * 实现同步
     */
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
     * 颜色按钮 设置颜色
     */
    @FXML
    public void setColor() {
        this.color = colorPicker.getValue();
        draw();
    }

    /**
     * 直线选项-画直线
     * 用count代表第几个点，点击两点连成一条直线
     */
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

    /**
     * 虚线选项-画虚线
     */
    @FXML
    public void drawDottedLine() {
        draw();
        gc.setLineDashes(4);
    }

    /**
     * 矩形选项-画矩形
     * 重复画时有bug
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
     * 画圆选项-画圆，但只能点击，看不到轨迹变化
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

    /**
     * 对canvas画布截图并保存成文件然后发送
     */
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
            System.out.println("图片保存失败");
        }
    }

    /**
     * 菜单中游戏中点击开始选项，开始计时
     */
    @FXML
    public void begin() {
        if (tl.getStatus().equals(Animation.Status.RUNNING)) {
            return;
        }
        isrunning = true;
        MyDataBase m = new MyDataBase();
        m.getTitle();
        String str = m.getCurrentTitle();
        titleLabel.setText("题目：" + str);
        Send(str);
        TimeStart();
    }

    /**
     * 菜单中游戏-退出
     */
    @FXML
    public void exit() {
        System.exit(0);
    }

    /**
     * 菜单中设置中简单选项，通过改变time倒计时时时间改变难度
     */
    @FXML
    public void easy() {
        if (!isrunning)
            time = 60;
    }

    /**
     * 中等难度
     */
    @FXML
    public void mid() {
        if (!isrunning)
            time = 40;
    }

    /**
     * 困难难度
     */
    @FXML
    public void hard() {
        if (!isrunning)
            time = 20;
    }

    /**
     * 使用Timeline实现倒计时
     */
    public void TimeStart() {
        int t =time;
        tl = new Timeline(new KeyFrame(Duration.millis(1000), e -> {
            timeLabel.setText("还剩:" + --time + "秒");
            if (time == 0) {
                System.out.println("over");
            }
        }));
        tl.setCycleCount(time);
        tl.play();
        tl.onFinishedProperty().set(event -> {
            Send("@#$%*&暂停");
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "时间到", new ButtonType("继续游戏", ButtonBar.ButtonData.YES), new ButtonType("休息一下", ButtonBar.ButtonData.NO));
            alert.setTitle("提示");
            alert.setOnHidden(ev -> {
                if (alert.getResult().getButtonData().equals(ButtonBar.ButtonData.YES)) {
                    time = t;
                    System.out.println("time"+time+"t"+t);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    begin();
                } else {
                    time = t;
                    System.out.println("休息");
                    isrunning = false;
                }
            });
            alert.show();
        });

    }

    /**
     * 菜单中帮助中关于选项
     */
    @FXML
    public void about() {
        Stage s = new Stage();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("../view/About.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        s.setTitle("你画我猜");
        s.getIcons().add(new Image("file:src/sample/images/icon.png"));
        Scene scene = new Scene(root, 300, 160);
        s.setScene(scene);
        s.setResizable(false);
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.ESCAPE), () -> {
            s.close();
        });
        s.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
            s.close();
        });
        s.initStyle(StageStyle.TRANSPARENT);
        s.show();
    }


}
