package chapter10;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jpcap.JpcapCaptor;
import jpcap.PacketReceiver;
import jpcap.packet.Packet;

public class PacketCaptureFX extends Application {

    private final Button btnStartCapture = new Button("开始抓包");
    private final Button btnStopCapture = new Button("停止抓包");
    private final Button btnClear = new Button("清空");
    private final Button btnConfig = new Button("设置");
    private final Button btnExit = new Button("退出");

    //显示信息的文本区域
    private final TextArea taDisplay = new TextArea();
    private ConfigDialog configDialog;

    // TCP客户端
    private JpcapCaptor jpcapCaptor;

    // 线程
    private Thread captureThread;


    public static void main(String[] args) {
        launch(args);
    }

    public void sendSocketMsg() {

    }

    @Override
    public void start(Stage primaryStage) {

        // 事件
        //设置按钮动作事件
        btnConfig.setOnAction(event -> {
            //还没有实例化对话框，则先实例化
            if (configDialog == null) {
                configDialog = new ConfigDialog(primaryStage);
            }
            //阻塞式显示，等待设置窗体完成设置
            configDialog.showAndWait();

            //获取设置后的JpcapCaptor对象实例
            jpcapCaptor = configDialog.getJpcapCaptor();
        });

        // 开始抓包 按钮动作事件
        btnStartCapture.setOnAction(event -> {

            System.out.println("开始抓包...");

            //还没有jpcapCaptor对象实例，则打开设置对话框
            if (jpcapCaptor == null) {
                configDialog.showAndWait();
                return;
            }

            //停止还没结束的抓包线程

            //开线程名为"captureThread"的新线程进行抓包
            captureThread = new Thread(() -> {
                while (true) {
                    //如果声明了本线程被中断，则退出循环
                    if (Thread.currentThread().isInterrupted())
                        break;

                    // 每次抓一个包，交给内部类PacketHandler的实例处理
                    // PacketHandler为接口PacketReceiver的实现类
                    jpcapCaptor.processPacket(1, new PacketHandler());
                }
            }, "captureThread");
            //降低线程优先级，避免抓包线程卡住资源
            captureThread.setPriority(Thread.MIN_PRIORITY);
            captureThread.start();
        });


        btnClear.setOnAction((event) ->
                taDisplay.clear()
        );

        BorderPane mainPane = new BorderPane();

        //内容显示区域
        VBox vBox = new VBox();
        vBox.setSpacing(10);//各控件之间的间隔
        //VBox面板中的内容距离四周的留空区域
        vBox.setPadding(new Insets(10, 20, 5, 20));
        vBox.getChildren().addAll(new Label("抓包信息："),
                taDisplay);
        //设置显示信息区的文本区域可以纵向自动扩充范围
        VBox.setVgrow(taDisplay, Priority.ALWAYS);
        taDisplay.setPrefSize(850, 300);
        mainPane.setCenter(vBox);

        FlowPane flowPane = new FlowPane();
        flowPane.setHgap(10);
        flowPane.setVgap(20);
        flowPane.setPadding(new Insets(15, 15, 15, 15));
        flowPane.getChildren().addAll(btnStartCapture, btnStopCapture, btnClear, btnConfig, btnExit);
        mainPane.setBottom(flowPane);
        flowPane.setAlignment(Pos.CENTER);

        Scene scene = new Scene(mainPane, 900, 630);

        primaryStage.setScene(scene);

        primaryStage.setOnCloseRequest(event -> {

        });

        primaryStage.show();
    }


    public void startCaptureThread() {
        captureThread = new Thread(() -> {

        });
        captureThread.start();
    }

    /**
     * 循环遍历指定线程名的线程列表，并声明关闭，需要关闭的线程需要在构造时指定线程名，作为参数传入
     */
    private void interrupt(String threadName) {
        ThreadGroup currentGroup =
                Thread.currentThread().getThreadGroup();
        //获取当前线程的线程组及其子线程组中活动线程数量
        int noThreads = currentGroup.activeCount();
        Thread[] lstThreads = new Thread[noThreads];
        currentGroup.enumerate(lstThreads);//将活动线程复制到线程数组
        //遍历这些活动线程，符合指定线程名的则声明关闭
        for (int i = 0; i < noThreads; i++) {
            if (lstThreads[i].getName().equals(threadName)) {
                lstThreads[i].interrupt();//声明线程关闭
            }
        }
    }

    class PacketHandler implements PacketReceiver {
        @Override
        public void receivePacket(Packet packet) {
            Platform.runLater(()->{
                System.out.println(packet.toString());
                // 在显示区显示抓包原始信息
                taDisplay.appendText(packet.toString());
            });
        }
    }

}
