package com.talkylabs.reach.type;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;


public class PaymentInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@JsonProperty("payee")
	private String payee;

	@JsonProperty("amount")
	private BigDecimal amount;

	@JsonProperty("currency")
	private String currency;

	public PaymentInfo payee(String payee) {
	  this.payee = payee;
	  return this;
	}


	public String getPayee() {
	  return payee;
	}

	public void setPayee(String payee) {
	  this.payee = payee;
	}

	public PaymentInfo amount(BigDecimal amount) {
	  this.amount = amount;
	  return this;
	}

	public BigDecimal getAmount() {
	  return amount;
	}

	public void setAmount(BigDecimal amount) {
	  this.amount = amount;
	}

	public PaymentInfo currency(String currency) {
	  this.currency = currency;
	  return this;
	}

	public String getCurrency() {
	  return currency;
	}

	public void setCurrency(String currency) {
	  this.currency = currency;
	}


	  @Override
	  public boolean equals(Object o) {
	    if (this == o) {
	      return true;
	    }
	    if (o == null || getClass() != o.getClass()) {
	      return false;
	    }
	    PaymentInfo paymentInfo = (PaymentInfo) o;
	    return Objects.equals(this.payee, paymentInfo.payee) &&
	        Objects.equals(this.amount, paymentInfo.amount) &&
	        Objects.equals(this.currency, paymentInfo.currency);
	  }

	  @Override
	  public int hashCode() {
	    return Objects.hash(payee, amount, currency);
	  }

	  @Override
	  public String toString() {
	    StringBuilder sb = new StringBuilder();
	    sb.append("class PaymentInfo {\n");
	    
	    sb.append("    payee: ").append(toIndentedString(payee)).append("\n");
	    sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
	    sb.append("    currency: ").append(toIndentedString(currency)).append("\n");
	    sb.append("}");
	    return sb.toString();
	  }
	  
	  public String asRequestParam() {
		    StringBuilder sb = new StringBuilder();
		    sb.append("{");		    
		    sb.append("\"payee\": \"").append(payee==null?"null":payee);
		    sb.append("\", \"amount\": \"").append(amount==null?"null":amount.toString());
		    sb.append("\", \"currency\": \"").append(currency==null?"null":currency);
		    sb.append("\"}");
		    return sb.toString();
		  }

	  /**
	   * Convert the given object to string with each line indented by 4 spaces
	   * (except the first line).
	   */
	  private String toIndentedString(Object o) {
	    if (o == null) {
	      return "null";
	    }
	    return o.toString().replace("\n", "\n    ");
	  }

}
