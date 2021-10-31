package chapter08;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;


import java.io.*;
import java.net.URL;
import java.util.regex.Pattern;


import static java.lang.Thread.sleep;

public class URLClientFX extends Application {

    private final Button btnExit = new Button("退出");
    private final Button btnSend = new Button("发送");

    //待发送信息的文本框
    private final TextField tfSend = new TextField();
    //显示信息的文本区域
    private final TextArea taDisplay = new TextArea();

    // TCP客户端
    private HTTPClient httpClient = new HTTPClient();

    // 线程
    private Thread receiveThread;

    public static void main(String[] args) {
        launch(args);
    }

    public void exitSocket() throws InterruptedException {
        System.exit(0);
    }

    public void sendSocketMsg() {
        taDisplay.clear();
        String address = tfSend.getText().trim();

        String regex = "^([hH][tT]{2}[pP]:/*|[hH][tT]{2}[pP][sS]:/*|[fF][tT][pP]:/*)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+(\\?{0,1}(([A-Za-z0-9-~]+\\={0,1})([A-Za-z0-9-~]*)\\&{0,1})*)$";
        Pattern pattern = Pattern.compile(regex);
        if (!pattern.matcher(address).matches()) {
            taDisplay.appendText("非法网址！");
        } else {
            try {
                URL url = new URL(address);
                System.out.printf("连接%s成功！", address);
                //获得url的字节流输入
                InputStream in = url.openStream();
                //装饰成字符输入流
                httpClient.setBr(new BufferedReader(new InputStreamReader(in, "utf-8")));
            } catch (Exception e) {
                e.printStackTrace();
            }
            startReceiveThread();
        }
    }

    @Override
    public void start(Stage primaryStage) {

        // 事件
        btnExit.setOnAction((event) -> {
                    try {
                        exitSocket();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        );
        btnSend.setOnAction((event) -> {
            sendSocketMsg();
        });

        // 快捷键监听
        tfSend.setOnKeyPressed((KeyEvent keyEvent) -> {
                    if (keyEvent.getCode() == KeyCode.ENTER) {
                        sendSocketMsg();
                    }
                }
        );


        BorderPane mainPane = new BorderPane();

        //内容显示区域
        VBox vBox = new VBox();
        vBox.setSpacing(10);//各控件之间的间隔
        //VBox面板中的内容距离四周的留空区域
        vBox.setPadding(new Insets(10, 20, 10, 20));
        vBox.getChildren().addAll(new Label("信息显示区："),
                taDisplay, new Label("输入URL地址："), tfSend);

        //设置显示信息区的文本区域可以纵向自动扩充范围
        VBox.setVgrow(taDisplay, Priority.ALWAYS);
        mainPane.setCenter(vBox);

        //底部按钮区域
        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(10, 20, 10, 20));
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.getChildren().addAll(btnSend, btnExit);
        mainPane.setBottom(hBox);
        Scene scene = new Scene(mainPane, 700, 400);

        primaryStage.setScene(scene);

        primaryStage.setOnCloseRequest(event -> {
            try {
                exitSocket();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        primaryStage.show();

    }

    // 启动一个线程来接受信息，解决主线程阻塞和多行读取的问题
    public void startReceiveThread() {
        receiveThread = new Thread(() -> {
            String msg = null;
            try {
                while ((msg = httpClient.receive()) != null) {
                    // 有效final使得lambda可以访问外部的局部变量
                    String msgTemp = msg;
                    Platform.runLater(() -> {
                        taDisplay.appendText(msgTemp + "\n");
                    });
                    sleep(1);
                }
            } catch (InterruptedException e) {
                System.out.println("子线程结束");
                // 跳出循环
                Platform.runLater(() -> {
                    taDisplay.appendText("对话已关闭!\n");
                });
            }
        });
        receiveThread.start();
    }

}
