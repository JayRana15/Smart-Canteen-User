package ResponseModel;

public class MenuResponseModel {
    String item_price,item_name,item_id;
    int item_quantity;

    public MenuResponseModel(String item_price, String item_name, String item_id, int item_quantity) {
        this.item_price = item_price;
        this.item_name = item_name;
        this.item_id = item_id;
        this.item_quantity = item_quantity;
    }

    public String getItem_price() {
        return item_price;
    }

    public void setItem_price(String item_price) {
        this.item_price = item_price;
    }

    public int getItem_quantity() {
        return item_quantity;
    }

    public void setItem_quantity(int item_quantity) {
        this.item_quantity = item_quantity;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public MenuResponseModel() {
    }
}
