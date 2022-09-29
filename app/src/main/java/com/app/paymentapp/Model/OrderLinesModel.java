package com.app.paymentapp.Model;

import java.util.List;


//The individual line items of the order. This is mandatory for some providers, and recommended for a good customer experience.
public class OrderLinesModel {
    String description;//Description of an item.
    String itemId;//The item identification.
    String quantity;//The quantity of the item. The value must be greater than zero.
    String unitPrice;//The unit price, excluding sales tax. The value must be greater than zero, unless the optional goodsType parameter is set to handling, in which case the field can be used to provide a discount.
    String taxPercent;//This is the tax percentage of the unit price.
    String taxAmount;//This is the total tax on an order line, before any discounts are applied. It is recommended to use taxAmount if possible. If you provide both taxPercent and taxAmount, the amount takes precedence.
    String unitCode;//The relevant measurement unit for the order line. For example, kg.
    String discount;//The order line's discount in percent.
    String goodsType;//The goods type of the order line - shipment| handling| item
    String  imageUrl;//The full URL of the icon for the item
    String productUrl;//

    public OrderLinesModel() {
        this.description = "";
        this.itemId = "";
        this.quantity = "";
        this.unitPrice = "";
        this.taxPercent = "";
        this.taxAmount = "";
        this.unitCode = "";
        this.discount = "";
        this.goodsType = "";
        this.imageUrl = "";
        this.productUrl = "";
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getTaxPercent() {
        return taxPercent;
    }

    public void setTaxPercent(String taxPercent) {
        this.taxPercent = taxPercent;
    }

    public String getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(String taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(String goodsType) {
        this.goodsType = goodsType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }
}
