public class Message {
    public boolean isRead;
    public String sender;
    public String receiver;
    public String body;
    public int id;
    public Message(int id, String sender, String receiver, String body) {
        this.id=id;
        this.sender=sender;
        this.receiver=receiver;
        this.body=body;
        this.isRead=false;
    }
}
