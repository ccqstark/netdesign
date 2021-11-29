package chapter12;

import rmi.RmiKitService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import static chapter10.PacketSender.hexToDecimal;

/**
 * 远程服务接口的实现类
 */
public class RmiKitServiceImpl extends UnicastRemoteObject implements RmiKitService {

    protected RmiKitServiceImpl() throws RemoteException {
        // 指定TCP端口
        super(9999);
    }

    //远程方法一 将ipv4格式字符串转为长整型
    @Override
    public long ipToLong(String strIp) throws RemoteException {
        long[] ip = new long[4];
        int position1 = strIp.indexOf(".");
        int position2 = strIp.indexOf(".", position1 + 1);
        int position3 = strIp.indexOf(".", position2 + 1);
        // 将每个.之间的字符串转换成整型
        ip[0] = Long.parseLong(strIp.substring(0, position1));
        ip[1] = Long.parseLong(strIp.substring(position1 + 1, position2));
        ip[2] = Long.parseLong(strIp.substring(position2 + 1, position3));
        ip[3] = Long.parseLong(strIp.substring(position3 + 1));
        //进行左移位处理
        return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
    }

    //远程方法二 将长整型转为ipv4字符串格式
    @Override
    public String longToIp(long ipLong) throws RemoteException {
        StringBuilder result = new StringBuilder(15);
        for (int i = 0; i < 4; i++) {
            result.insert(0, Long.toString(ipLong & 0xff));
            if (i < 3) {
                result.insert(0, '.');
            }
            ipLong = ipLong >> 8;
        }

        return result.toString();
    }

    //远程方法三 将“-”格式连接的MAC地址转为Jpcap可用的字节数组
    @Override
    public byte[] macStringToBytes(String macStr) throws RemoteException {
        String[] hex_arr = macStr.split("-");
        int[] dec_arr = new int[hex_arr.length];
        for (int i = 0; i < hex_arr.length; i++) {
            dec_arr[i] = hexToDecimal(hex_arr[i].toUpperCase());
            System.out.println(hex_arr[i]);
            System.out.println(dec_arr[i]);
        }
        byte[] bytes_arr = {(byte) dec_arr[0], (byte) (byte) dec_arr[1], (byte)(byte) dec_arr[2], (byte) (byte) dec_arr[3], (byte) (byte) dec_arr[4], (byte)(byte) dec_arr[5]};
        return bytes_arr;
    }

    //远程方法四 将Jpcap的byte[]格式的MAC地址转为"-"连接MAC字符串
    @Override
    public String bytesToMACString(byte[] macBytes) throws RemoteException {

        return null;
    }
}
