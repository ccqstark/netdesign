package chapter12;

import chapter12.server.HelloServiceImpl;
import rmi.HelloService;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * 该服务器程序负责在RMI注册器中注册和启动远程服务对象
 */
public class RmiStudentServer {

    public static void main(String[] args) {
        try {
            //对于有多个网卡的机器，建议用下面的命令绑定固定的ip
            System.setProperty("java.rmi.server.hostname", "10.173.217.44");
            //(1)启动RMI注册器，并监听在1099端口（这是RMI的默认端口，正如HTTP的默认端口是80）
            Registry registry = LocateRegistry.createRegistry(1099);

            //(2)实例化远程服务对象，如果有多个远程接口，只实例化自己实现的接口（为什么可能有没有实例化的接口？）
            RmiKitServiceImpl rmiKitService = new RmiKitServiceImpl();

            //(3)用助记符来注册发布远程服务对象,助记符建议和远程服务接口命名相同，这样更好起到”助记"效果
            //助记符（别名）使用RmiKitService，供教师端检索
            registry.rebind("RmiKitService", rmiKitService);
            //也可以用另外一种方式进行注册发布，建议用上面的方式
            //Naming.rebind("HelloService",helloService);

            System.out.println("发布了一个RmiKitService RMI远程服务");

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
