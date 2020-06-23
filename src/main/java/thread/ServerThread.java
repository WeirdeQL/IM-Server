package thread;

import com.alibaba.fastjson.JSON;
import entity.UserEntity;
import server.MyServer;
import util.TransportMsg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.util.Iterator;
import java.util.Map;

/**
 * Weirdo
 * Created on 2020-05-29 0:00
 */
public class ServerThread implements Runnable {
    //当前处理的socket
    private Socket socket=null;
    //该线程锁处理的socket所对应的输入流
    private BufferedReader br=null;
    Socket s;

    Iterator<Map.Entry<Integer, Socket>> iterator;

    public ServerThread(Socket socket) throws IOException {
        this.socket=socket;
        br=new BufferedReader(new InputStreamReader(socket.getInputStream(),"utf-8"));
    }

    @Override
    public void run() {
        try {
            String content=null;
            //先向客户端发送连接成功的信息
            //socket.getOutputStream().write(("连接已经建立\n").getBytes("utf-8"));

            //socket.getOutputStream().write((TransportMsg.getJsonString("SYSTEM", "", "", "系统已经建立连接")+"\n").getBytes("utf-8"));

            //System.out.println("发送连接已经建立的信息");

            //第一次连接，发送全部用户的信息过去
            iterator = MyServer.map.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry<Integer,Socket> entry=iterator.next();
                Socket s=entry.getValue();
                //如果有新人进来，要告知所有人，除了自己
                if(s.getPort()!=socket.getPort()){
                    String str=JSON.toJSONString(new UserEntity(String.valueOf(socket.getPort()),String.valueOf(socket.getPort())));
                    String system = TransportMsg.getJsonString("SYSTEM", "", "", str);
                    s.getOutputStream().write((system+"\n").getBytes("utf-8"));
                }
                String str=JSON.toJSONString(new UserEntity(String.valueOf(s.getPort()),String.valueOf(s.getPort())));
                String system = TransportMsg.getJsonString("SYSTEM", "", "", str);
                socket.getOutputStream().write((system+"\n").getBytes("utf-8"));

            }

            // 采用循环不断从Socket中读取客户端发送过来的数据
            while ((content=readFromClient())!=null){
                // 遍历socketList中的每个Socket，
                // 将读到的内容向每个Socket发送一次

                Map<String,String> mapType=JSON.parseObject(content, Map.class);
                mapType.put("from",String.valueOf(socket.getPort()));
                System.out.println("receive ----【"+mapType.get("type")+"】--------"+mapType.get("from")+">>>>"+mapType.get("to")+"   "+mapType.get("content"));

                //socket.getOutputStream().write((TransportMsg.getJsonString("SYSTEM", "", "", "系统已经建立连接")+"\n").getBytes("utf-8"));

                content=JSON.toJSONString(mapType);
                if(mapType.get("type").equals("QUIT")){
                    //MyServer.socketLists.remove(socket);
                    MyServer.map.remove(socket.getPort());
                    //通知个个客户端移除用户
                    iterator = MyServer.map.entrySet().iterator();
                    while (iterator.hasNext()){
                        Socket s=iterator.next().getValue();
                        s.getOutputStream().write((content+"\n").getBytes("utf-8"));
                    }
                }else{

                    //代表的是信息
                    String to = mapType.get("to");
                    //判断是发给个人还是全部人
                    if(to.equals("All")){
                        //发给全部人
                        iterator = MyServer.map.entrySet().iterator();
                        mapType.put("from","All");
                        content=JSON.toJSONString(mapType);
                        while (iterator.hasNext()){
                            s=iterator.next().getValue();
                            if(s.getPort()!=socket.getPort()){
                                System.out.println("send ----【"+mapType.get("type")+"】--------"+mapType.get("from")+">>>>"+mapType.get("to")+"   "+mapType.get("content"));
                                s.getOutputStream().write((content+"\n").getBytes("utf-8"));
                            }

                        }
                    }else{
                        //发给某给人
                        Integer port=Integer.parseInt(to);
                        s = MyServer.map.get(port);
                        //如果当前的端口不存在的话，系统给出提示，用户已经下线
                        if(s!=null){
                            System.out.println("send ----【"+mapType.get("type")+"】--------"+mapType.get("from")+">>>>"+mapType.get("to")+"   "+mapType.get("content"));
                            s.getOutputStream().write((content+"\n").getBytes("utf-8"));
                        }else{
                            content=TransportMsg.getJsonString("msg","SYSTEM",String.valueOf(socket.getPort()),"用户不在线！");
                            socket.getOutputStream().write((content+"\n").getBytes("utf-8"));
                        }

                    }



                }

                /*for(int i=0;i<MyServer.socketLists.size();i++){
                    Socket s=MyServer.socketLists.get(i);
                    if(s.isConnected()){
                        System.out.println("当前连接有效"+s.getPort());
                        s.getOutputStream().write((mapType.get("content")+"\n").getBytes("utf-8"));
                    }else{
                        //删除这个socket
                        System.out.println("当前连接失效了"+s.getPort());
                        //MyServer.socketLists.remove(s);
                    }
                }*/

            }
        }catch(SocketException e){
            e.printStackTrace();
            /*MyServer.map.remove(s);
            System.out.println("当前连接无效，断开");
            String content=TransportMsg.getJsonString("QUIT",String.valueOf(s.getPort()),"SYSTEM","退出了群聊");

            iterator = MyServer.map.entrySet().iterator();
            while (iterator.hasNext()){
                s=iterator.next().getValue();
                try {

                    s.getOutputStream().write((content+"\n").getBytes("utf-8"));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }*/


        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private String readFromClient(){
        try {
            return br.readLine();
        } catch (SocketException e) {
            e.printStackTrace();
            MyServer.map.remove(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }


}
