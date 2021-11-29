package chapter12;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import rmi.HelloService;
import rmi.RmiMsgService;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RmiStudentClientFX extends Application {
    private TextArea taDisplay = new TextArea();
    private TextField tfMessage = new TextField();
    private TextField tfNO = new TextField();
    private TextField tfName = new TextField();
    Button btnSendMsg = new Button("发送信息");
    Button btnSendNoAndName = new Button("发送学号和姓名");
    //客户端也有一份相同的远程接口
    private RmiMsgService rmiMsgService;

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * 初始化rmi相关操作
     */
    public void rmiInit() {
        try {
            //(1)获取RMI注册器
            Registry registry = LocateRegistry.getRegistry("202.116.195.71", 1099);
            System.out.println("RMI远程服务别名列表：");
            for (String name : registry.list()) {
                System.out.println(name);
            }

            //(2)客户端(调用端)到注册器中使用助记符寻找并创建远程服务对象的客户端(调用端)stub，之后本地调用helloService的方法，实质就是调用了远程服务器上同名的远程接口下的同名方法
            rmiMsgService = (RmiMsgService) registry.lookup("RmiMsgService");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        //main区域
        VBox vBoxMain = new VBox();
        vBoxMain.setSpacing(10);//各控件之间的间隔
        //VBoxMain面板中的内容距离四周的留空区域
        vBoxMain.setPadding(new Insets(10, 20, 10, 20));
        HBox hBox = new HBox();
        hBox.setSpacing(10);//各控件之间的间隔
        //HBox面板中的内容距离四周的留空区域
        hBox.setPadding(new Insets(10, 20, 10, 20));
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(new Label("输入信息："), tfMessage,
                btnSendMsg, new Label("学号："), tfNO, new Label("姓名")
                , tfName, btnSendNoAndName);

        vBoxMain.getChildren().addAll(new Label("信息显示区："),
                taDisplay, hBox);
        Scene scene = new Scene(vBoxMain);
        primaryStage.setScene(scene);
        primaryStage.show();

        new Thread(() -> {
            rmiInit();
        }).start();

        btnSendMsg.setOnAction(event -> {
            try {
                String msg = rmiMsgService.send(tfMessage.getText());
                taDisplay.appendText(msg + "\n");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });

        btnSendNoAndName.setOnAction(event -> {
            try {
                String msg = rmiMsgService.send(tfNO.getText(), tfName.getText());
                taDisplay.appendText(msg + "\n");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });

    }
}
