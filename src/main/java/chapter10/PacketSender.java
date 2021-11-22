package chapter10;

import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;
import jpcap.packet.EthernetPacket;
import jpcap.packet.IPPacket;
import jpcap.packet.TCPPacket;

import java.io.IOException;
import java.net.InetAddress;

/**
 * 封装Jpcap发包功能
 */
public class PacketSender {

    public static void sendTCPPacket(String srcMAC, String dstMAC) throws IOException {

        //open a network interface to send a packet
        NetworkInterface[] devices = JpcapCaptor.getDeviceList();
        JpcapSender sender = JpcapSender.openDevice(devices[0]);
        //或者用 jpcapCaptor.getJpcapSenderInstance();获得对象实例
        TCPPacket tcp = new TCPPacket(8000,8008,56,78,false,
                false,false,false,true,false,true,true,200,10);

        //specify IPv4 header parameters
        tcp.setIPv4Parameter(0,false,false,false,0,false
                ,false,false,0,1010101,100, IPPacket.IPPROTO_TCP, InetAddress.getByName("192.168.236.133"),
                InetAddress.getByName ("202.116.195.71"));

        tcp.data = "20191002914&陈楚权".getBytes("utf-8");//字节数组型的填充数据

        //create an Ethernet packet (frame)
        EthernetPacket ether = new EthernetPacket();

        //set frame type as IP
        ether.frametype = EthernetPacket.ETHERTYPE_IP;

        //set the datalink frame of the tcp packet as ether
        tcp.datalink = ether;

        //set source and destination MAC addresses
        //MAC地址要转换成十进制，ipconfig /all 查看本机的MAC地址
        //源地址是自己机器的MAC地址，以下仅为用法示例

        try {
            ether.src_mac = convertMacFormat(srcMAC);
            ether.dst_mac = convertMacFormat(dstMAC);
            if(ether.src_mac == null || ether.dst_mac==null)
                throw new Exception("MAC地址输入错误");

            sender.sendPacket(tcp);
            System.out.println("发包成功！");
            sender.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            //重新抛出异常，调用者可以捕获处理
            throw new RuntimeException(e);
        }
    }

    public static int hexToDecimal(String hex) {
        int outcome = 0;
        for(int i = 0; i < hex.length(); i++){
            char hexChar = hex.charAt(i);
            outcome = outcome * 16 + charToDecimal(hexChar);
        }
        return outcome;
    }

    public static int charToDecimal(char c){
        if(c >= 'A' && c <= 'F')
            return 10 + c - 'A';
        else
            return c - '0';
    }

    public  static byte[] convertMacFormat(String mac){
        String[] hex_arr = mac.split("-");
        int[] dec_arr = new int[hex_arr.length];
        for (int i = 0; i < hex_arr.length; i++) {
            dec_arr[i] = hexToDecimal(hex_arr[i].toUpperCase());
            System.out.println(hex_arr[i]);
            System.out.println(dec_arr[i]);
        }
        byte[] bytes_arr = {(byte) dec_arr[0], (byte) (byte) dec_arr[1], (byte)(byte) dec_arr[2], (byte) (byte) dec_arr[3], (byte) (byte) dec_arr[4], (byte)(byte) dec_arr[5]};
        return bytes_arr;
    }

    public static void main(String[] args) throws IOException {
        PacketSender packetSender = new PacketSender();
        packetSender.sendTCPPacket("94-c6-91-24-77-03","00-11-5d-9c-94-00");
    }

//    public static void main(String[] args) {
//        byte[] bytes = convertMacFormat("94-c6-91-24-77-03");
//        for (int i = 0; i < bytes.length; i++) {
//            System.out.println(bytes[i]);
//        }
//    }

//    public static void main(String... args) {
//        Scanner input = new Scanner(System.in);
//        String content = input.nextLine();
//        if(!content.matches("[0-9a-fA-F]*")){
//            System.out.println("输入不匹配");
//            System.exit(-1);
//        }
//        //将全部的小写转化为大写
//        content = content.toUpperCase();
//        System.out.println(hexToDecimal(content));
//
//    }

}