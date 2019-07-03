package co.apache;

public class CartServiceBean {
	
	private String strServicename = "";
	private String fromDate="";
	private String toDate="";
	private String description="";
	private String price="";
	
	public String getStrServicename() {
        return strServicename;
    }

    public void setStrServicename(String strServicename) {
        this.strServicename = strServicename;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
