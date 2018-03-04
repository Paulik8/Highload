import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Worker implements Runnable{

    public static  String root = "";
    private static final String indexFileName = "index.html";
    private static HashMap<String, String> typeFiles;

    Socket socket;
    BufferedReader in;
    Boolean changeFlag = false;
    BufferedOutputStream raw;
    OutputStreamWriter out;
    String[] array;

    private File getFile(int posQuestion, String fileName) {
        fileName=root+fileName;
        if (posQuestion == -1) {
            return new File(fileName);
        } else {
            return new File(fileName.substring(0, root.length() + posQuestion));
        }
    }

    private void sendFile(File theFile) {
        try {
            final InputStream ios = new FileInputStream(theFile);
            final byte[] buffer = new byte[1024];

            int read = 0;
            //System.out.println("q");
            while ((read = ios.read(buffer)) != -1) {
                raw.write(buffer, 0, read);
                raw.flush();
            }

        } catch (IOException e){
            e.printStackTrace();
            sendHeader(HttpResponse.notFound());
        }
    }

    private void commonResponse() throws IOException {
        final String partOf = array[0];
        String contentType = "";
        if (partOf.toUpperCase().equals("GET") || (partOf.toUpperCase().equals("HEAD"))) {
            String file = URLDecoder.decode(array[1], "UTF-8");
            //System.out.printf("decode %s \n", file);
            final int point = file.lastIndexOf('.');
            if (point != -1){
                try {
                    //System.out.println("q.");
                    contentType = typeFiles.get(file.substring(point + 1));
                }catch (Exception e){
                    sendHeader(HttpResponse.notAllowed());
                    return;
                }
            } else {
                if (file.endsWith("/")) {
                    changeFlag = true;
                    //System.out.println("q/");
                    file += indexFileName;
                    contentType = "text/html";
                } else {
                    //System.out.println("qf");
                    sendHeader(HttpResponse.forbidden());
                }
            }

            final int posQuestion = file.indexOf('?');
            final File theFile = getFile(posQuestion, file);
            sendResponse(theFile, partOf, contentType, file);
        } else  {
            sendHeader(HttpResponse.notAllowed());
        }
    }

    private void sendResponse(File theFile, String partOf, String contentType, String file) throws IOException {
        if ((theFile.canRead()) && (theFile.getCanonicalPath().startsWith(root))) {
            //System.out.println("q1");
            sendHeader(HttpResponse.ok((int) theFile.length(), contentType));
            if (partOf.toUpperCase().equals("GET")) {
                sendFile(theFile);
            }
        } else {
            if (changeFlag) {
                //System.out.println("for");
                sendHeader(HttpResponse.forbidden());
            }
            else {
                //System.out.println("qnotf");
                sendHeader(HttpResponse.notFound());
            }
        }
    }

    Worker(Socket socket) {
        this.socket = socket;
//        System.out.println("open");
//        System.out.println("socket");
    }

    @Override
    public void run() {
        try {
            this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.raw = new BufferedOutputStream(this.socket.getOutputStream());
            this.out = new OutputStreamWriter(raw);
            requestParser();
            commonResponse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            finish();
        }
    }

    private void sendHeader(String responseHeader) {
        try {
            out.write(responseHeader);
            out.flush();
        }catch (Exception e){
        }
    }

    private void requestParser() throws IOException {
        this.array = readRequest().split("\\s+");
    }

    private String readRequest() throws IOException {
        String firstable = "";
        String line;
        while (true) {
            line = in.readLine();
            if (line == null || line.isEmpty()) {
                break;
            }
            firstable += line + " ";
        }
        return firstable;
    }

    public static void initTypeFiles() {
        final HashMap<String, String> typeFiles = new HashMap<>();

        typeFiles.put("html", "text/html");
        typeFiles.put("css", "text/css");
        typeFiles.put("js", "text/javascript");
        typeFiles.put("jpg", "image/jpeg");
        typeFiles.put("jpeg", "image/jpeg");
        typeFiles.put("png", "image/png");
        typeFiles.put("gif", "image/gif");
        typeFiles.put("swf", "application/x-shockwave-flash");

        Worker.typeFiles = typeFiles;

    }

    private void finish(){
        try {
            System.out.println("close");
            out.close();
            raw.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}