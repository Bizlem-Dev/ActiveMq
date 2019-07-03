package co.apache;

public class CheckOutCartServiceBean {
	
	private String strServicename = "";
	private String fromDate="";
	private String toDate="";
	private String description="";
	private String price="";
	private String contactname="";
	private String contactname1="";
	private String email="";
	private String organization="";
	private String lastname="";
	private String salesorder="";
       
	 public String getSalesorder() {
	        return salesorder;
	    }

	    public void setSalesorder(String salesorder) {
	        this.salesorder = salesorder;
	    }
	    public String getContactname() {
	        return contactname;
	    }

	    public void setContactname(String contactname) {
	        this.contactname = contactname;
	    }
	    public String getContactname1() {
	        return contactname1;
	    }

	    public void setContactname1(String contactname1) {
	        this.contactname1 = contactname1;
	    }
	    public String getLastname() {
	        return lastname;
	    }

	    public void setLastname(String lastname) {
	        this.contactname = lastname;
	    }

	    public String getEmail() {
	        return email;
	    }

	    public void setEmail(String email) {
	        this.email = email;
	    }

	    public String getOrganization() {
	        return organization;
	    }

	    public void setOrganization(String organization) {
	        this.organization = organization;
	    }
	
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
