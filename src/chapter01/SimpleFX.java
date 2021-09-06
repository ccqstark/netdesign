package chapter01;

import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.awt.*;

public class SimpleFX extends Application {

  private Button btnExit = new Button("推出");
  private Button btnSend = new Button("发送");
  private Button btnOpen = new Button("发送");

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {

  }
}
