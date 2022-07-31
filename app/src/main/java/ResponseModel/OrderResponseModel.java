package ResponseModel;

public class OrderResponseModel {
    String order_id,items_name,amount,txn_id,date,isReady,picked;

    public String getIsReady() {
        return isReady;
    }

    public void setIsReady(String isReady) {
        this.isReady = isReady;
    }

    public String getPicked() {
        return picked;
    }

    public void setPicked(String picked) {
        this.picked = picked;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getItems_name() {
        return items_name;
    }

    public void setItems_name(String items_name) {
        this.items_name = items_name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTxn_id() {
        return txn_id;
    }

    public void setTxn_id(String txn_id) {
        this.txn_id = txn_id;
    }

    public OrderResponseModel(String order_id, String items_name, String amount, String txn_id, String date, String isReady, String picked) {
        this.order_id = order_id;
        this.items_name = items_name;
        this.amount = amount;
        this.txn_id = txn_id;
        this.date = date;
        this.isReady = isReady;
        this.picked = picked;
    }

    public OrderResponseModel() {
    }
}
