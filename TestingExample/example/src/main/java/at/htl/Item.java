package at.htl;

public class Item {
    private Long id;
    private String message;

    public Item() {
    }

    public Item(Long id, String message) {
        this.id = id;
        this.message = message;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
