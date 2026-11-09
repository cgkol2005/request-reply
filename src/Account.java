import java.util.*;

public class Account {
    public String username;
    public int authToken;
    public List<Message> messageBox = new ArrayList<>();
    public Account(String username,int authToken) {
        this.username=username;
        this.authToken=authToken;
        this.messageBox=new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public int getAuthToken() {
        return authToken;
    }
    public void addMessage(Message message) {
        this.messageBox.add(message);
    }
}