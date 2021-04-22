package client.models;

public class Activity {
    public double saved;
    private String userId;

    public Activity(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getSaved() {
        return saved;
    }

    public void setSaved(double saved) {
        this.saved = saved;
    }


}

