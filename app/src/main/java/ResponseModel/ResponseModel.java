package ResponseModel;

public class ResponseModel {
    int user_id;
    String amountSpent;
    String newNumber;
    int lastOrderId;
    String order;
    String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public int getLastOrderId() {
        return lastOrderId;
    }

    public void setLastOrderId(Integer lastOrderId) {
        this.lastOrderId = lastOrderId;
    }


    public String getAmountSpent() {
        return amountSpent;
    }

    public void setAmountSpent(String amountSpent) {
        this.amountSpent = amountSpent;
    }

//    public ResponseModel(String user_id,String amountSpent) {
//        this.user_id = user_id;
//        this.amountSpent = amountSpent;
//    }

    public String getNewNumber() {
        return newNumber;
    }

    public void setNewNumber(String newNumber) {
        this.newNumber = newNumber;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public ResponseModel() {
    }
}
