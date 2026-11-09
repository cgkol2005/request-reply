import java.io.*;
import java.net.*;
import java.util.*;
public class MessagingServer {
    private static Map<String, Account> accounts = new HashMap<>();
    private static Map<Integer, Account> tokens = new HashMap<>();
    private static int nextMessageId = 1;

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("ΠΡΕΠΕΙ ΝΑ ΔΩΣΕΙΣ PORT");
            return;
        }
        int port=Integer.parseInt(args[0]);
        System.out.println("Server start" + port);

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            while (true) {
                Socket client = serverSocket.accept();
                new ClientHandler(client).start();
            }

        } catch (IOException e) {
            System.err.println("ΣΦΑΛΜΑ");
        }
    }

    static class ClientHandler extends Thread{
        private Socket socket;
        private DataInputStream in;
        private DataOutputStream out;
        public ClientHandler(Socket socket) {
            this.socket = socket;
        }
        public void run() {
            try {
                in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                out = new DataOutputStream(socket.getOutputStream());

                String request=in.readUTF();
                String[] parts=request.split(" ");

                int fn=Integer.parseInt(parts[0]);

                switch (fn) {
                    case 1: createAccount(parts); break;
                    case 2: showacc(parts); break;
                    case 3: send(parts); break;
                    case 4: showInbox(parts); break;
                    case 5: readMessage(parts); break;
                    case 6: deleteMessage(parts); break;
                    default: out.writeUTF("ERROR");
                }

            } catch (Exception e) {
                System.err.println("ΣΦΑΛΜΑ CLIENT");
            } finally {
                try { socket.close(); } catch (IOException ignored) {}
            }
        }
        private Account auth(int token) throws IOException {
            if (!tokens.containsKey(token)) {
                out.writeUTF("Invalid Auth Token");
                return null;
            }
            return tokens.get(token);
        }


        private void createAccount(String[] p) throws IOException {
            String username = p[1];

            if (!username.matches("[A-Za-z0-9_]+")) {
                out.writeUTF("Invalid Username");
                return;
            }
            if (accounts.containsKey(username)) {
                out.writeUTF("Sorry, the user already exists");
                return;
            }
            int token = new Random().nextInt(900000) + 100000;
            Account acc = new Account(username, token);

            accounts.put(username, acc);
            tokens.put(token, acc);
            out.writeUTF(String.valueOf(token));
        }


        private void showacc(String[] p) throws IOException {
            int token = Integer.parseInt(p[1]);
            Account a = auth(token);
            if (a == null) return;

            StringBuilder sb = new StringBuilder();
            int i = 1;

            for (String user : accounts.keySet()) {
                sb.append(i++).append(". ").append(user).append("\n");
            }
            out.writeUTF(sb.toString().trim());
        }

        private void send(String[] p) throws IOException
        {
            int token=Integer.parseInt(p[1]);
            Account sender=auth(token);
            if (sender ==null) return;

            String recipient=p[2];
            String body=p[3];

            if (!accounts.containsKey(recipient)) {
                out.writeUTF("User does not exist");
                return;
            }
            Message m=new Message(nextMessageId++, sender.username, recipient, body);
            accounts.get(recipient).messageBox.add(m);
            out.writeUTF("OK");
        }

        private void showInbox(String[] p) throws IOException {
            int token = Integer.parseInt(p[1]);
            Account a = auth(token);
            if (a == null) return;

            StringBuilder sb = new StringBuilder();
            for (Message m : a.messageBox) {
                sb.append(m.id)
                        .append(". from: ")
                        .append(m.sender)
                        .append(m.isRead ? "" : "*")
                        .append("\n");
            }
            out.writeUTF(sb.toString().trim());
        }


        private void readMessage(String[] p) throws IOException {
            int token = Integer.parseInt(p[1]);
            Account a = auth(token);
            if (a == null) return;

            int id = Integer.parseInt(p[2]);
            for (Message m : a.messageBox) {
                if (m.id == id) {
                    m.isRead = true;
                    out.writeUTF("(" + m.sender + ") " + m.body);
                    return;
                }
            }

            out.writeUTF("Message ID does not exist");
        }


        private void deleteMessage(String[] p) throws IOException {
            int token = Integer.parseInt(p[1]);
            Account a = auth(token);
            if (a == null) return;

            int id = Integer.parseInt(p[2]);

            Iterator<Message> it = a.messageBox.iterator();
            while (it.hasNext()) {
                Message m = it.next();
                if (m.id == id) {
                    it.remove();
                    out.writeUTF("OK");
                    return;
                }
            }

            out.writeUTF("Message does not exist");
        }
    }

}
