package com.redplutoanalytics.callpluto.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "trading_order_details")
public class TradingOrderDetails {

    @Id
    @Column(name = "trading_order_details_id")
    private String tradingOrderDetailsId;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private ClientDetails client;

    @ManyToOne
    @JoinColumn(name = "rm_id")
    private EmployeeDetails rm;

    @ManyToOne
    @JoinColumn(name = "instrument_id")
    private EquityDetails instrument;

    @ManyToOne
    @JoinColumn(name = "product_details_id")
    private ProductDetails product;

    @Column(name = "quantity")
    private String quantity;

    @Column(name = "price")
    private Double price;

    @Column(name = "transaction_type")
    private String transactionType;

    @Column(name = "stop_loss")
    private BigDecimal stopLoss;

    @Column(name = "target_price")
    private BigDecimal targetPrice;

    @Column(name = "order_status")
    private String orderStatus;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "order_execution_time")
    private LocalDateTime orderExecutionTime;

	public String getTradingOrderDetailsId() {
		return tradingOrderDetailsId;
	}

	public void setTradingOrderDetailsId(String tradingOrderDetailsId) {
		this.tradingOrderDetailsId = tradingOrderDetailsId;
	}

	public ClientDetails getClient() {
		return client;
	}

	public void setClient(ClientDetails client) {
		this.client = client;
	}

	public EmployeeDetails getRm() {
		return rm;
	}

	public void setRm(EmployeeDetails rm) {
		this.rm = rm;
	}

	public EquityDetails getInstrument() {
		return instrument;
	}

	public void setInstrument(EquityDetails instrument) {
		this.instrument = instrument;
	}

	public ProductDetails getProduct() {
		return product;
	}

	public void setProduct(ProductDetails product) {
		this.product = product;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public BigDecimal getStopLoss() {
		return stopLoss;
	}

	public void setStopLoss(BigDecimal stopLoss) {
		this.stopLoss = stopLoss;
	}

	public BigDecimal getTargetPrice() {
		return targetPrice;
	}

	public void setTargetPrice(BigDecimal targetPrice) {
		this.targetPrice = targetPrice;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public LocalDateTime getOrderExecutionTime() {
		return orderExecutionTime;
	}

	public void setOrderExecutionTime(LocalDateTime orderExecutionTime) {
		this.orderExecutionTime = orderExecutionTime;
	}

  
}
