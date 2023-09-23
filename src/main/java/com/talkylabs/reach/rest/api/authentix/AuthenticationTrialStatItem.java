/*
 * This code was generated by
 *  ___ ___   _   ___ _  _    _____ _   _    _  ___   ___      _   ___ ___      ___   _   ___     ___ ___ _  _ ___ ___    _ _____ ___  ___ 
 * | _ \ __| /_\ / __| || |__|_   _/_\ | |  | |/ | \ / / |    /_\ | _ ) __|___ / _ \ /_\ |_ _|__ / __| __| \| | __| _ \  /_\_   _/ _ \| _ \
 * |   / _| / _ \ (__| __ |___|| |/ _ \| |__| ' < \ V /| |__ / _ \| _ \__ \___| (_) / _ \ | |___| (_ | _|| .` | _||   / / _ \| || (_) |   /
 * |_|_\___/_/ \_\___|_||_|    |_/_/ \_\____|_|\_\ |_| |____/_/ \_\___/___/    \___/_/ \_\___|   \___|___|_|\_|___|_|_\/_/ \_\_| \___/|_|_\
 * 
 * Reach Authentix API
 *  Reach Authentix API helps you easily integrate user authentification in your application. The authentification allows to verify that a user is indeed at the origin of a request from your application.  At the moment, the Reach Authentix API supports the following channels:    * SMS      * Email   We are continuously working to add additionnal channels. ## Base URL All endpoints described in this documentation are relative to the following base URL: ``` https://api.reach.talkylabs.com/rest/authentix/v1/ ```  The API is provided over HTTPS protocol to ensure data privacy.  ## API Authentication Requests made to the API must be authenticated. You need to provide the `ApiUser` and `ApiKey` associated with your applet. This information could be found in the settings of the applet. ```curl curl -X GET [BASE_URL]/configurations -H \"ApiUser:[Your_Api_User]\" -H \"ApiKey:[Your_Api_Key]\" ``` ## Reach Authentix API Workflow Three steps are needed in order to authenticate a given user using the Reach Authentix API. ### Step 1: Create an Authentix configuration A configuration is a set of settings used to define and send an authentication code to a user. This includes, for example: ```   - the length of the authentication code,    - the message template,    - and so on... ``` A configuaration could be created via the web application or directly using the Reach Authentix API. This step does not need to be performed every time one wants to use the Reach Authentix API. Indeed, once created, a configuartion could be used to authenticate several users in the future.    ### Step 2: Send an authentication code A configuration is used to send an authentication code via a selected channel to a user. For now, the supported channels are `sms`, and `email`. We are working hard to support additional channels. Newly created authentications will have a status of `awaiting`. ### Step 3: Verify the authentication code This step allows to verify that the code submitted by the user matched the one sent previously. If, there is a match, then the status of the authentication changes from `awaiting` to `passed`. Otherwise, the status remains `awaiting` until either it is verified or it expires. In the latter case, the status becomes `expired`. 
 *
 * NOTE: This class is auto generated by OpenAPI Generator.
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

package com.talkylabs.reach.rest.api.authentix;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talkylabs.reach.base.Resource;
import com.talkylabs.reach.converter.Promoter;
import com.talkylabs.reach.exception.ApiConnectionException;

import com.talkylabs.reach.exception.ApiException;

import lombok.ToString;

import java.io.IOException;
import java.io.InputStream;

import java.util.Objects;


import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class AuthenticationTrialStatItem extends Resource {
    private static final long serialVersionUID = 261162849253585L;

    public static AuthenticationTrialStatItemFetcher fetcher(){
        return new AuthenticationTrialStatItemFetcher();
    }

    /**
    * Converts a JSON String into a AuthenticationTrialStatItem object using the provided ObjectMapper.
    *
    * @param json Raw JSON String
    * @param objectMapper Jackson ObjectMapper
    * @return AuthenticationTrialStatItem object represented by the provided JSON
    */
    public static AuthenticationTrialStatItem fromJson(final String json, final ObjectMapper objectMapper) {
        // Convert all checked exceptions to Runtime
        try {
            return objectMapper.readValue(json, AuthenticationTrialStatItem.class);
        } catch (final JsonMappingException | JsonParseException e) {
            throw new ApiException(e.getMessage(), e);
        } catch (final IOException e) {
            throw new ApiConnectionException(e.getMessage(), e);
        }
    }

    /**
    * Converts a JSON InputStream into a AuthenticationTrialStatItem object using the provided
    * ObjectMapper.
    *
    * @param json Raw JSON InputStream
    * @param objectMapper Jackson ObjectMapper
    * @return AuthenticationTrialStatItem object represented by the provided JSON
    */
    public static AuthenticationTrialStatItem fromJson(final InputStream json, final ObjectMapper objectMapper) {
        // Convert all checked exceptions to Runtime
        try {
            return objectMapper.readValue(json, AuthenticationTrialStatItem.class);
        } catch (final JsonMappingException | JsonParseException e) {
            throw new ApiException(e.getMessage(), e);
        } catch (final IOException e) {
            throw new ApiConnectionException(e.getMessage(), e);
        }
    }
    public enum Channel {
        SMS("sms"),
        EMAIL("email");

        private final String value;

        private Channel(final String value) {
            this.value = value;
        }

        public String toString() {
            return value;
        }

        @JsonCreator
        public static Channel forValue(final String value) {
            return Promoter.enumFromString(value, Channel.values());
        }
    }
    public enum TrialStatus {
        SUCCESSFUL("successful"),
        UNSUCCESSFUL("unsuccessful");

        private final String value;

        private TrialStatus(final String value) {
            this.value = value;
        }

        public String toString() {
            return value;
        }

        @JsonCreator
        public static TrialStatus forValue(final String value) {
            return Promoter.enumFromString(value, TrialStatus.values());
        }
    }

    private final String appletId;
    private final String apiVersion;
    private final Integer totalTrials;
    private final Integer numSuccessfulTrials;
    private final Integer numUnsuccessfulTrials;
    private final BigDecimal successRate;

    @JsonCreator
    private AuthenticationTrialStatItem(
        @JsonProperty("appletId")
        final String appletId,

        @JsonProperty("apiVersion")
        final String apiVersion,

        @JsonProperty("totalTrials")
        final Integer totalTrials,

        @JsonProperty("numSuccessfulTrials")
        final Integer numSuccessfulTrials,

        @JsonProperty("numUnsuccessfulTrials")
        final Integer numUnsuccessfulTrials,

        @JsonProperty("successRate")
        final BigDecimal successRate
    ) {
        this.appletId = appletId;
        this.apiVersion = apiVersion;
        this.totalTrials = totalTrials;
        this.numSuccessfulTrials = numSuccessfulTrials;
        this.numUnsuccessfulTrials = numUnsuccessfulTrials;
        this.successRate = successRate;
    }

        public final String getAppletId() {
            return this.appletId;
        }
        public final String getApiVersion() {
            return this.apiVersion;
        }
        public final Integer getTotalTrials() {
            return this.totalTrials;
        }
        public final Integer getNumSuccessfulTrials() {
            return this.numSuccessfulTrials;
        }
        public final Integer getNumUnsuccessfulTrials() {
            return this.numUnsuccessfulTrials;
        }
        public final BigDecimal getSuccessRate() {
            return this.successRate;
        }

    @Override
    public boolean equals(final Object o) {
        if (this==o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AuthenticationTrialStatItem other = (AuthenticationTrialStatItem) o;

        return Objects.equals(appletId, other.appletId) &&  Objects.equals(apiVersion, other.apiVersion) &&  Objects.equals(totalTrials, other.totalTrials) &&  Objects.equals(numSuccessfulTrials, other.numSuccessfulTrials) &&  Objects.equals(numUnsuccessfulTrials, other.numUnsuccessfulTrials) &&  Objects.equals(successRate, other.successRate)  ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(appletId, apiVersion, totalTrials, numSuccessfulTrials, numUnsuccessfulTrials, successRate);
    }

}
