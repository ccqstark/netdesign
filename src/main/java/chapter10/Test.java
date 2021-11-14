package chapter10;

import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.NetworkInterfaceAddress;


public class Test {

    public static void main(String[] args) {
        NetworkInterface[] devices = JpcapCaptor.getDeviceList();
        for (int i = 0; i < devices.length; i++) {
            //打印 GUID information and descriptio
            System.out.println(i + ": " + devices[i].name + " " + devices[i].description);
            //打印 MAC address，各段用":"隔开
            String mac = "";
            for (byte b : devices[i].mac_address) {
                //mac 地址 6 段，每段是 8 位，而 int 转换的十六进制是 4 个字节，所以和 0xff 相与，这样就只保留低 8 位
                mac = mac + Integer.toHexString(b & 0xff) + ":";
            }
            System.out.println("MAC address:" + mac.substring(0, mac.length() - 1));
            //print out its IP address, subnet mask and broadcast address
            for (NetworkInterfaceAddress addr : devices[i].addresses) {
                System.out.println(" address:" + addr.address + " " + addr.subnet
                        + " " + addr.broadcast);
            }
        }
    }
}
