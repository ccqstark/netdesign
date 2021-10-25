package chapter07;

import chapter01.TextFileIO;
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

import java.io.IOException;
import java.time.LocalDateTime;

import static java.lang.Thread.sleep;

public class TCPMailClientFX2 extends Application {

    private final Button btnExit = new Button("退出");
    private final Button btnSend = new Button("发送");
    private final Button btnOpen = new Button("加载");
    private final Button btnSave = new Button("保存");
    private final Button btnConnect = new Button("连接");

    // 记录连接状态
    private static boolean connectState = false;

    //待发送信息的文本框
    private final TextArea tfSend = new TextArea("20191002914&陈楚权");
    //显示信息的文本区域
    private final TextArea taDisplay = new TextArea();
    // IP地址输入框
    private final TextField ipInput = new TextField("smtp.qq.com");
    // 端口输入框
    private final TextField portInput = new TextField("25");
    // 邮件相关信息输入框
    private final TextField sendEmailInput = new TextField("1367305698@qq.com");
    private final TextField receiveEmailInput = new TextField("luo_hai_jiao@163.com");
    private final TextField titleInput = new TextField("20191002914给老师的一封邮件");

    // TCP客户端
    private TCPClient tcpClient = new TCPClient("smtp.qq.com", "25");

    // 线程
    private Thread receiveThread;

    public TCPMailClientFX2() throws IOException {
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void exitSocket() throws InterruptedException, IOException {
        if (tcpClient != null) {
            tcpClient.send("bye");
            // 避免tcpClient关闭后，子线程还在读取导致错误
            // 方法1：主线程sleep
//            sleep(500);
            // 方法2：interrupt结束子线程
            receiveThread.interrupt();
            receiveThread.join();

            tcpClient.close();
        }
        System.exit(0);
    }

    public void sendSocketMsg() throws IOException, InterruptedException {
        if (!connectState) {
            taDisplay.appendText("未连接服务器\n");
            tfSend.clear();
            return;
        }

        String sendMsg = tfSend.getText();
        if (sendMsg.equals("bye")) {
            // 断开连接 重新启用按钮
            btnConnect.setDisable(false);
            connectState = false;
        }
        tcpClient.send(sendMsg);
        taDisplay.appendText(sendMsg + "\n");

//        String receiveMsg = tcpClient.receive();
//        taDisplay.appendText(receiveMsg + "\n");
        tfSend.clear();
    }

    @Override
    public void start(Stage primaryStage) {

        // 事件
        btnExit.setOnAction((event) -> {
                    try {
                        exitSocket();
                    } catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                    }
                }
        );
        btnSend.setOnAction((event) -> {
            try {
                tcpClient.sendMail(ipInput.getText(), receiveEmailInput.getText(),
                        titleInput.getText(), tfSend.getText());
            } catch (Exception e) {
                e.printStackTrace();
            }
            startReceiveThread();
        });
        TextFileIO textFileIO = new TextFileIO();
        btnSave.setOnAction((event) ->
                textFileIO.append(
                        LocalDateTime.now().withNano(0) + " " + taDisplay.getText()
                )
        );
        btnOpen.setOnAction(event -> {
            String msg = textFileIO.load();
            if (msg != null) {
                taDisplay.clear();
                taDisplay.setText(msg);
            }
        });

        // 快捷键监听
        tfSend.setOnKeyPressed((KeyEvent keyEvent) -> {
                    if (keyEvent.getCode() == KeyCode.ENTER) {
                        try {
                            sendSocketMsg();
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        // 连接按钮
        btnConnect.setOnAction(event -> {
            String ip = ipInput.getText().trim();
            String port = portInput.getText().trim();
            try {
                tcpClient = new TCPClient(ip, port);
                // 成功连接服务器接收欢迎信息
                String firstMsg = tcpClient.receive();
                taDisplay.appendText(firstMsg + "\n");
                // 改变连接状态
                connectState = true;
                // 禁用按钮
                btnConnect.setDisable(true);
                startReceiveThread();
            } catch (IOException e) {
                taDisplay.appendText("服务器连接失败!" + e.getMessage() + "\n");
            }
        });

        BorderPane mainPane = new BorderPane();

        // ip和端口输入区
        VBox topVbox = new VBox();
        FlowPane flowPane1 = new FlowPane();
        flowPane1.setHgap(10);
        flowPane1.setVgap(20);
        flowPane1.setPadding(new Insets(15, 15, 15, 15));
        flowPane1.getChildren().addAll(new Label("IP地址:"), ipInput, new Label("端口:"), portInput);

        FlowPane flowPane2 = new FlowPane();
        flowPane2.setHgap(10);
        flowPane2.setVgap(20);
        flowPane2.setPadding(new Insets(15, 15, 15, 15));
        flowPane2.getChildren().addAll(new Label("邮件发送者地址："), sendEmailInput, new Label("邮件接收者地址："), receiveEmailInput);

        FlowPane flowPane3 = new FlowPane();
        flowPane3.setHgap(10);
        flowPane3.setVgap(20);
        flowPane3.setPadding(new Insets(15, 15, 15, 15));
        flowPane3.getChildren().addAll(new Label("邮件标题："), titleInput);

        topVbox.getChildren().addAll(flowPane1, flowPane2, flowPane3);
        mainPane.setTop(topVbox);

        //内容显示区域
        HBox hbox = new HBox();
        hbox.setSpacing(20);//各控件之间的间隔
        //VBox面板中的内容距离四周的留空区域
        hbox.setPadding(new Insets(10, 20, 10, 20));
        VBox emailTextBox = new VBox();
        emailTextBox.getChildren().addAll(new Label("邮件正文："), tfSend);
        VBox feedbackBox = new VBox();
        feedbackBox.getChildren().addAll(new Label("服务器反馈："), taDisplay);
        hbox.getChildren().addAll(emailTextBox, feedbackBox);

        //设置显示信息区的文本区域可以纵向自动扩充范围
        VBox.setVgrow(tfSend, Priority.ALWAYS);
        VBox.setVgrow(taDisplay, Priority.ALWAYS);
        mainPane.setCenter(hbox);

        //底部按钮区域
        HBox buttonBox = new HBox();
        buttonBox.setSpacing(10);
        buttonBox.setPadding(new Insets(10, 20, 10, 20));
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getChildren().addAll(btnSend, btnExit);
        mainPane.setBottom(buttonBox);
        Scene scene = new Scene(mainPane, 700, 550);

        primaryStage.setScene(scene);

        primaryStage.setOnCloseRequest(event -> {
            try {
                exitSocket();
            } catch (InterruptedException | IOException e) {
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
                while ((msg = tcpClient.receive()) != null) {
                    System.out.println(msg);
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
