package chapter10;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;

public class ConfigDialog {
    private JpcapCaptor jpcapCaptor;//用于返回给主窗体
    //网卡列表
    private NetworkInterface[] devices = JpcapCaptor.getDeviceList();
    private Stage stage = new Stage();//对话框窗体

    //parentStage表示抓包主程序(PacketCaptureFX)的stage，传值可通过这种构造方法参数的方式
    public ConfigDialog(Stage parentStage) {
        //设置该对话框的父窗体为调用者的那个窗体
        stage.initOwner(parentStage);
        //设置为模态窗体，即不关闭就不能切换焦点
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setResizable(false);
        stage.setTitle("选择网卡并设置参数");

        //窗体主容器
        VBox vBox = new VBox();
        //省略

        //网卡选择列表，使用组合下拉框控件
        ComboBox<String> cob = new ComboBox<>();
        cob.setMaxWidth(800);
        for (int i = 0; i < devices.length; i++) {
            cob.getItems().add( i + " : " + devices[i].description);
        }
        //默认选择第一项
        cob.getSelectionModel().selectFirst();

        //设置抓包过滤
        TextField tfFilter = new TextField();

        //设置抓包大小（一般建议在68-1514之间，默认1514）
        TextField tfSize = new TextField("1514");

        //是否设置混杂模式
        CheckBox cb = new CheckBox("是否设置为混杂模式");
        cb.setSelected(true); //默认选中
        //底部确定和取消按钮
        HBox hBoxBottom = new HBox();
        //省略

        Button btnConfirm = new Button("确定");
        Button btnCancel = new Button("取消");
        hBoxBottom.getChildren().addAll(btnConfirm,btnCancel);

        //将各组件添加到主容器
        vBox.getChildren().addAll(new Label("请选择网卡："),cob,
                new Label("设置抓包过滤器（例如 ip and tcp）："),tfFilter,
                new Label("设置抓包大小（建议介于68~1514之间）："),tfSize,cb,
                new Separator(),hBoxBottom);

        Scene scene = new Scene(vBox);
        stage.setScene(scene);
        //stage.show(); //不要显示对话框，由主窗体调用显示

        //**************事件响应部分***************************

        //确定按钮的动作事件
        btnConfirm.setOnAction(event -> {
            try {
                int index = cob.getSelectionModel().getSelectedIndex();
                //选择的网卡接口
                NetworkInterface networkInterface = devices[index];
                //抓包大小
                int snapLen = Integer.parseInt(tfSize.getText().trim());
                //是否混杂模式
                boolean promisc = cb.isSelected();
                jpcapCaptor = JpcapCaptor.openDevice(networkInterface,snapLen,
                        promisc,20);
                jpcapCaptor.setFilter(tfFilter.getText().trim(),true);
//                jpcapCaptor.setFilter("dst host 202.116.192.5",true);
//                jpcapCaptor.setFilter("dst port 443", true);
                stage.hide();

            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR,e.getMessage()).showAndWait();
            }
        });

        //取消按钮的动作事件
        btnCancel.setOnAction(event -> {
            stage.hide();
        });
    }

    //主程序调用，获取设置了参数的JpcapCaptor对象
    public JpcapCaptor getJpcapCaptor() {
        return jpcapCaptor;
    }

    //该方法由主程序调用，阻塞式显示本对话框界面
    public void showAndWait() {
        stage.showAndWait();
    }
}
