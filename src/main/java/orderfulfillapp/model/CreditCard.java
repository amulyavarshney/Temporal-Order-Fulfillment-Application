package orderfulfillapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents credit card information for payment processing.
 */
public class CreditCard {
    @JsonProperty("number")
    private String number;

    @JsonProperty("expiration")
    private String expiration;

    // Default constructor for Jackson
    public CreditCard() {
    }

    public CreditCard(String number, String expiration) {
        this.number = number;
        this.expiration = expiration;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    @Override
    public String toString() {
        return "CreditCard{" +
                "number='" + maskCardNumber(number) + '\'' +
                ", expiration='" + expiration + '\'' +
                '}';
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }
} 