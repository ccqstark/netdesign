package chapter01;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDateTime;

import static javafx.scene.input.KeyCode.*;


public class SimpleFX extends Application {

  private Button btnExit = new Button("退出");
  private Button btnSend = new Button("发送");
  private Button btnOpen = new Button("加载");
  private Button btnSave = new Button("保存");

  //待发送信息的文本框
  private TextField tfSend = new TextField();
  //显示信息的文本区域
  private TextArea taDisplay = new TextArea();

  // 快捷键
//  private KeyCombination shift_enter = new KeyCombination(KeyCode.X,KeyCombination.CONTROL_DOWN);


  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {

    // 事件
    btnExit.setOnAction(event -> {
      System.exit(0);
    });
    btnSend.setOnAction(event -> {
      String msg = tfSend.getText();
      taDisplay.appendText(msg + "\n");
      tfSend.clear();
    });
    TextFileIO textFileIO = new TextFileIO();
    btnSave.setOnAction(event -> {
      textFileIO.append(
        LocalDateTime.now().withNano(0) + " " + taDisplay.getText()
      );
    });
    btnOpen.setOnAction(event -> {
      String msg = textFileIO.load();
      if (msg != null) {
        taDisplay.clear();
        taDisplay.setText(msg);
      }
    });

    // 回车监听
    tfSend.setOnKeyPressed((KeyEvent keyEvent)-> {
        if (keyEvent.getCode() == ENTER)  {
          String msg = tfSend.getText();
          taDisplay.appendText(msg + "\n");
          tfSend.clear();
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
      taDisplay, new Label("信息输入区："), tfSend);

    //设置显示信息区的文本区域可以纵向自动扩充范围
    VBox.setVgrow(taDisplay, Priority.ALWAYS);
    mainPane.setCenter(vBox);

    //底部按钮区域
    HBox hBox = new HBox();
    hBox.setSpacing(10);
    hBox.setPadding(new Insets(10, 20, 10, 20));
    hBox.setAlignment(Pos.CENTER_RIGHT);
    hBox.getChildren().addAll(btnSend, btnSave, btnOpen, btnExit);
    mainPane.setBottom(hBox);
    Scene scene = new Scene(mainPane, 700, 400);

//    scene.set
//    scene.getAccelerators().put(new KeyCombination() {
//      @Override
//      public boolean match(KeyEvent event) {
//        return super.match(event);
//      }
//    }, new Runnable() {
//      public void run() {
//        String msg = tfSend.getText();
//        taDisplay.appendText("echo:" + msg + "\n");
//        tfSend.clear();
//      }
//    });

    primaryStage.setScene(scene);
    primaryStage.show();

  }
}