package co.apache;

public class ProductBean {
	
	private String productId;
	private long quantity=0;
	private String customerFName;
	private String customerId;
	private String fromDate="";
	private String toDate="";
	private long accountCount=0;
	private long serviceMothCoun=0;
	private long serviceRecordCoun=0;
	private long serviceResponseCoun=0;
	
	
	public long getServiceResponseCoun() {
		return serviceResponseCoun;
	}
	public void setServiceResponseCoun(long serviceResponseCoun) {
		this.serviceResponseCoun = serviceResponseCoun;
	}
	public long getServiceRecordCoun() {
		return serviceRecordCoun;
	}
	public void setServiceRecordCoun(long serviceRecordCoun) {
		this.serviceRecordCoun = serviceRecordCoun;
	}
	public long getQuantity() {
		return quantity;
	}
	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}
	public long getAccountCount() {
		return accountCount;
	}
	public void setAccountCount(long accountCount) {
		this.accountCount = accountCount;
	}
	public long getServiceMothCoun() {
		return serviceMothCoun;
	}
	public void setServiceMothCoun(long serviceMothCoun) {
		this.serviceMothCoun = serviceMothCoun;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	
	public String getCustomerFName() {
		return customerFName;
	}
	public void setCustomerFName(String customerFName) {
		this.customerFName = customerFName;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
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
		
}
