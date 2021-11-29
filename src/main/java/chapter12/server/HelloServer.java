package chapter12.server;

import chapter12.rmi.HelloService;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class HelloServer {
    public static void main(String[] args) {
        try {
            //对于有多个网卡的机器，建议用下面的命令绑定固定的ip
            System.setProperty("java.chapter12.rmi.server.hostname","127.0.0.1");
            //(1)启动RMI注册器，并监听在1099端口（这是RMI的默认端口，正如HTTP的默认端口是80）
            Registry registry = LocateRegistry.createRegistry(1099);

            //(2)实例化远程服务对象，如果有多个远程接口，只实例化自己实现的接口（为什么可能有没有实例化的接口？）
            HelloService helloService = new HelloServiceImpl("张三的远程服务");

            //(3)用助记符来注册发布远程服务对象,助记符建议和远程服务接口命名相同，这样更好起到”助记"效果
            registry.rebind("HelloService",helloService);
            //也可以用另外一种方式进行注册发布，建议用上面的方式
            //Naming.rebind("HelloService",helloService);

            System.out.println("发布了一个HelloService RMI远程服务");

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
