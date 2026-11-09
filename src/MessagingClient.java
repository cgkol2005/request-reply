
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class MessagingClient {
    public static void main(String[]args)  {
        String ip=args[0];
        int port;
        try
        {
            port = Integer.parseInt(args[1]);
        }
        catch (NumberFormatException e) {
            System.err.println("ΠΡΕΠΕΙ ΑΚΕΡΑΙΟΣ");
            return;
        }
        String fnid=args[2];
        StringBuilder requestBuilder=new StringBuilder();
        requestBuilder.append(fnid).append(" ");

        for (int i = 3; i < args.length; i++) {
            requestBuilder.append(args[i]);
            if (i < args.length - 1) {
                requestBuilder.append(" ");
            }
        }
        String request = requestBuilder.toString();

        try {
            Socket socket=new Socket (ip,port);
            DataOutputStream out=new DataOutputStream(socket.getOutputStream());
            DataInputStream in=new DataInputStream(socket.getInputStream());
            out.writeUTF(request);
            String response=in.readUTF();
            System.out.println(response);
        }
        catch (UnknownHostException e)
        {
            System.err.println("ΑΓΝΩΣΤΗ IP");
        }
        catch (IOException e)
        {
            System.err.println("ΣΦΑΛΜΑ ΣΤΗ ΣΥΝΔΕΣΗ");
        }

    }
}

