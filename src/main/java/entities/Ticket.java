package entities;

public class Ticket {

    private int id;
    private String type;
    private double price;
    private int eventId;

    public Ticket(int id, String type, double price, int eventId) {
        this.id = id;
        this.type = type;
        this.price = price;
        this.eventId = eventId;
    }

    public Ticket(String type, double price, int eventId) {
        this.type = type;
        this.price = price;
        this.eventId = eventId;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }

    public int getEventId() {
        return eventId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", price=" + price +
                ", eventId=" + eventId +
                '}';
    }
}
