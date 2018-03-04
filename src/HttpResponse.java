import java.util.Date;

public class HttpResponse {

    public static String ok(int length, String contentType) {
        return "HTTP/1.1 200 OK" + "\r\n" +
                "Date: " + new Date().toString() + "\r\n" +
                "Server: Khotin \r\n"+
                "Content-Length: " + length + "\r\n" +
                "Content-type: " + contentType + "\r\n"+
                "Connection: " + "Closed" + "\r\n\r\n";
    }

    public static String serverOK() {
        return "HTTP/1.1 200 OK " + "\r\n" +
                "Date: " + new Date().toString() + "\r\n" +
                "Server: Khotin \r\n"+
                "Connection: " + "Closed" + "\r\n\r\n";
    }

    public static String forbidden() {

        return "HTTP/1.1 403 Forbidden" + "\r\n"+
                "Date: " + new Date().toString() + "\r\n" +
                "Server: Khotin \r\n"+
                "Connection: " + "Closed" + "\r\n\r\n";
    }

    public static String notFound() {
        return "HTTP/1.1 404 Not Found" + "\r\n"+
                "Date: " + new Date().toString() + "\r\n" +
                "Server: Khotin \r\n"+
                "Connection: " + "Closed" + "\r\n\r\n";
    }

    public static String notAllowed() {
        return "HTTP/1.1 405 Method Not Allowed" + "\r\n"+
                "Date: " + new Date().toString() + "\r\n" +
                "Server: Khotin \r\n"+
                "Connection: " + "Closed" + "\r\n\r\n";
    }
}
