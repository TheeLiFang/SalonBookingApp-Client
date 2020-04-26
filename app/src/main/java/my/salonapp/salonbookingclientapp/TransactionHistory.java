package my.salonapp.salonbookingclientapp;

import java.util.ArrayList;

public class TransactionHistory {

    private int transactionId;
    private String refNo;
    private String companyName;
    private String staffName;
    private long bookingDate;
    private String servicesDesc;
    private Float subTotal;

    private ArrayList<TransactionHistoryService> transactionHistoryServices;

    public TransactionHistory() {
    }

    public TransactionHistory(int transactionId, String refNo,
                              String companyName, String staffName,
                              long bookingDate, String servicesDesc,
                              Float subTotal,
                              ArrayList<TransactionHistoryService> transactionHistoryServices) {
        this.transactionId = transactionId;
        this.refNo = refNo;
        this.companyName = companyName;
        this.staffName = staffName;
        this.bookingDate = bookingDate;
        this.servicesDesc = servicesDesc;
        this.subTotal = subTotal;
        this.transactionHistoryServices = transactionHistoryServices;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public long getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(long bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getServicesDesc() {
        return servicesDesc;
    }

    public void setServicesDesc(String servicesDesc) {
        this.servicesDesc = servicesDesc;
    }

    public Float getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(Float subTotal) {
        this.subTotal = subTotal;
    }

    public ArrayList<TransactionHistoryService> getTransactionHistoryServices() {
        return transactionHistoryServices;
    }

    public void setTransactionHistoryServices(ArrayList<TransactionHistoryService> transactionHistoryServices) {
        this.transactionHistoryServices = transactionHistoryServices;
    }

    public Float getServicesSubTotal(ArrayList<TransactionHistoryService> transactionHistoryServices) {
        Float subtotal = 0F;

        for (TransactionHistoryService transactionHistoryService : transactionHistoryServices) {
            subtotal += transactionHistoryService.getServicePrice();
        }

        return subtotal;
    }
}
