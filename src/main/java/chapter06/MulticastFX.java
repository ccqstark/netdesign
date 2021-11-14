package chapter06;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;

public class MulticastFX extends Application {

    //待发送信息的文本框
    private final TextField tfSend = new TextField();
    //显示信息的文本区域
    private final TextArea taDisplay = new TextArea();
    // TCP客户端
    private Multicast multicast = new Multicast();

    public MulticastFX() throws IOException {
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void exitSocket() {
        System.exit(0);
    }

    public void sendUDPtMsg() {
        String sendMsg = tfSend.getText();
        multicast.send(sendMsg);
//        taDisplay.appendText("客户端发送: " + sendMsg + "\n");

        tfSend.clear();
    }

    @Override
    public void start(Stage primaryStage) {

        // 快捷键监听
        tfSend.setOnKeyPressed((KeyEvent keyEvent) -> {
                    if (keyEvent.getCode() == KeyCode.ENTER) {
                        sendUDPtMsg();
                    }
                }
        );

        BorderPane mainPane = new BorderPane();

        // ip和端口输入区
        FlowPane flowPane = new FlowPane();
        flowPane.setHgap(10);
        flowPane.setVgap(20);
        flowPane.setPadding(new Insets(15, 15, 15, 15));
        mainPane.setTop(flowPane);

        //内容显示区域
        VBox vBox = new VBox();
        vBox.setSpacing(10);//各控件之间的间隔
        //VBox面板中的内容距离四周的留空区域
        vBox.setPadding(new Insets(5, 20, 10, 20));
        vBox.getChildren().addAll(new Label("组播对话："),
                taDisplay, new Label("信息输入区："), tfSend);

        //设置显示信息区的文本区域可以纵向自动扩充范围
        VBox.setVgrow(taDisplay, Priority.ALWAYS);
        mainPane.setCenter(vBox);

        //底部按钮区域
        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(10, 20, 10, 20));
        hBox.setAlignment(Pos.CENTER_RIGHT);
        mainPane.setBottom(hBox);
        Scene scene = new Scene(mainPane, 700, 400);

        primaryStage.setScene(scene);

        primaryStage.setOnCloseRequest(event -> {
            exitSocket();
        });

        primaryStage.show();

        new Thread(()->{
            while (true) {
                String receiveMsg = multicast.receive();
                Platform.runLater(() -> {
                    taDisplay.appendText(receiveMsg + "\n");
                });
            }
        }).start();
    }
}

