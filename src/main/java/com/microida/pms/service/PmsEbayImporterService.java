package com.microida.pms.service;

import com.ebay.api.client.auth.oauth2.CredentialUtil;
import com.ebay.api.client.auth.oauth2.OAuth2Api;
import com.ebay.api.client.auth.oauth2.model.Environment;
import com.ebay.api.client.auth.oauth2.model.OAuthResponse;
import com.ebay.sdk.ApiContext;
import com.ebay.sdk.ApiCredential;
import com.ebay.sdk.Main;
import com.ebay.sdk.call.GetItemCall;
import com.ebay.soap.eBLBaseComponents.ItemType;
import org.springframework.stereotype.Service;

import java.util.Arrays;


@Service
public class PmsEbayImporterService {

    private final PmsProductService pmsProductService;

    public PmsEbayImporterService(final PmsProductService pmsProductService) {
        this.pmsProductService = pmsProductService;
    }

    public void importProduct(){
        try {
            ApiContext apiContext = new ApiContext();
            CredentialUtil.load(Main.class.getClassLoader().getResourceAsStream("ebay-config.yaml"));
            OAuth2Api oauth2Api = new OAuth2Api();
            OAuthResponse response = oauth2Api.getApplicationToken(Environment.PRODUCTION, Arrays.asList("https://api.ebay.com/oauth/api_scope"));

            ApiCredential cred = apiContext.getApiCredential();
            String token = response.getAccessToken().get().getToken();
            cred.setOAuthToken(token);

            System.out.println(token);
            apiContext.setApiCredential(cred);
            apiContext.setApiServerUrl("https://api.ebay.com/wsapi");
            cred.seteBayToken("v^1.1#i^1#I^3#r^1#f^0#p^3#t^Ul4xMF82Ojc3NzZERUQ4NEM1QkZFOTJGNTA5NkM1QTA4ODk5QzMzXzNfMSNFXjI2MA==");

            GetItemCall apiCall = new GetItemCall(apiContext);
            apiCall.setItemID("222506599297");
            ItemType item =  apiCall.getItem();
            System.out.println(item);
        } catch (Exception e) {
            System.out.println("Failed to get the eBay official time.");
            e.printStackTrace();
        }
    }



}
