package com.talkylabs.reach.type;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.talkylabs.reach.converter.Promoter;

public class TrialQuickInfo  implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@JsonProperty("dateCreated")
	private ZonedDateTime dateCreated;

	  @JsonProperty("trialId")
	  private String trialId;

	  /**
	   * The channel used.
	   */
	  public enum Channel {
	    SMS("sms"),
	    
	    EMAIL("email");

	    private String value;

	    private Channel(String value) {
	      this.value = value;
	    }

	    @JsonValue
	    public String getValue() {
	      return value;
	    }

	    @Override
	    public String toString() {
	      return String.valueOf(value);
	    }

	    @JsonCreator
	    public static Channel fromValue(String value) {
	    	return Promoter.enumFromString(value, Channel.values());	      
	    }
	  }

	  @JsonProperty("channel")
	  private Channel channel;

	  public TrialQuickInfo dateCreated(ZonedDateTime dateCreated) {
	    this.dateCreated = dateCreated;
	    return this;
	  }

	  /**
	   * The date and time in GMT that the trial was created. 
	   * @return dateCreated
	  */
	  public ZonedDateTime getDateCreated() {
	    return dateCreated;
	  }

	  public void setDateCreated(ZonedDateTime dateCreated) {
	    this.dateCreated = dateCreated;
	  }

	  public TrialQuickInfo trialId(String trialId) {
	    this.trialId = trialId;
	    return this;
	  }

	  /**
	   * the trial ID
	   * @return trialId
	  */
	  public String getTrialId() {
	    return trialId;
	  }

	  public void setTrialId(String trialId) {
	    this.trialId = trialId;
	  }

	  public TrialQuickInfo channel(Channel channel) {
	    this.channel = channel;
	    return this;
	  }

	  /**
	   * The channel used.
	   * @return channel
	  */
	  public Channel getChannel() {
	    return channel;
	  }

	  public void setChannel(Channel channel) {
	    this.channel = channel;
	  }


	  @Override
	  public boolean equals(Object o) {
	    if (this == o) {
	      return true;
	    }
	    if (o == null || getClass() != o.getClass()) {
	      return false;
	    }
	    TrialQuickInfo trialQuickInfo = (TrialQuickInfo) o;
	    return Objects.equals(this.dateCreated, trialQuickInfo.dateCreated) &&
	        Objects.equals(this.trialId, trialQuickInfo.trialId) &&
	        Objects.equals(this.channel, trialQuickInfo.channel);
	  }

	  @Override
	  public int hashCode() {
	    return Objects.hash(dateCreated, trialId, channel);
	  }

	  @Override
	  public String toString() {
	    StringBuilder sb = new StringBuilder();
	    sb.append("class TrialQuickInfo {\n");
	    
	    sb.append("    dateCreated: ").append(toIndentedString(dateCreated)).append("\n");
	    sb.append("    trialId: ").append(toIndentedString(trialId)).append("\n");
	    sb.append("    channel: ").append(toIndentedString(channel)).append("\n");
	    sb.append("}");
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
