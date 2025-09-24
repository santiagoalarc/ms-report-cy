package co.com.bancolombia.enums;

public enum DynamoTableNameEnum {

    TOTAL_APPROVED_LOANS("total_approved_loans"),
    TOTAL_AMOUNT_APPROVED("total_amount_approved");

    private final String id;

    DynamoTableNameEnum(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
