package server;

import thread.ServerThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Weirdo
 * Created on 2020-05-28 23:56
 */
public class MyServer {
    //public static ArrayList<Socket> socketLists=new ArrayList<>();
    public static Map<Integer,Socket> map=new ConcurrentHashMap<>();


    public static void main(String[] args) throws IOException {
        ServerSocket ss=new ServerSocket(3000);
        System.out.println("服务器启动啦，等待客户端连接");
        while (true){

            //此代码会阻塞，一直等待别人的连接
            Socket s=ss.accept();
            //每当有一个线程进来通知所有人，有一个端口号为多少的线程进来
            System.out.println("有一个客户端连接进来了....端口号为:"+s.getPort());
            //socketLists.add(s);
            map.put(s.getPort(),s);
            //每当客户端连接后启动一条serverThread为该客户端服务
            new Thread(new ServerThread(s)).start();
        }

    }
}
