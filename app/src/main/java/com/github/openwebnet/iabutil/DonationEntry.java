package com.github.openwebnet.iabutil;

public class DonationEntry {

    private final String sku;
    private final String name;
    private final String description;
    private final String price;
    private final String currencyCode;
    private boolean purchased;

    private DonationEntry(Builder builder) {
        this.sku = builder.sku;
        this.name = builder.name;
        this.description = builder.description;
        this.price = builder.price;
        this.currencyCode = builder.currencyCode;
        this.purchased = builder.purchased;
    }

    public static class Builder {

        private String sku;
        private String name;
        private String description;
        private String price;
        private String currencyCode;
        private boolean purchased;

        public Builder(String sku) {
            this.sku = sku;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder price(String price) {
            this.price = price;
            return this;
        }

        public Builder currencyCode(String currencyCode) {
            this.currencyCode = currencyCode;
            return this;
        }

        public Builder purchased(boolean purchased) {
            this.purchased = purchased;
            return this;
        }

        public DonationEntry build() {
            return new DonationEntry(this);
        }
    }

    public String getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public boolean isPurchased() {
        return purchased;
    }

    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

}
