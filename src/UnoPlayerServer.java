import java.io.*;
import java.net.*;

public class UnoPlayerServer {
    public static void main (String[] args) throws IOException {
        ServerSocket servsoc = new ServerSocket(4200);
        Socket soc = servsoc.accept();
        DataInputStream din = new DataInputStream(soc.getInputStream());
        DataOutputStream dout = new DataOutputStream(soc.getOutputStream());
    }
}
