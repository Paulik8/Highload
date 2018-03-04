import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    private static int pools = 5;
    private static int port = 1024;

    private static void configParser() throws IOException {
        FileInputStream fstream = new FileInputStream("/home/paul/Projects/AndroidProjects/Server/httpd.conf");
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
        String line;

        while((line = br.readLine()) != null) {
            final String[] buf;
            buf = line.split(":");
            if (buf[0].equals("Listen ")) {
                port = Integer.parseInt(buf[1]);
                System.out.println(port);
            }
            if (buf[0].equals("threads_max")) {
                pools = Integer.parseInt(buf[1]);
            }
            if (buf[0].equals("document_root")) {
                Worker.root = buf[1].substring(1);
            }
        }
        fstream.close();
        br.close();
    }
    public static void main(String[] args) throws IOException{
        configParser();
        ServerSocket ss = new ServerSocket(port);
        ThreadPool threadPool = new ThreadPool(pools);
        Worker.initTypeFiles();

        while(true) {
            //System.out.println("get");
            Socket socket = ss.accept();
            //System.out.println("2");
            Worker w = new Worker(socket);
            threadPool.execute(w);
        }
    }
}
