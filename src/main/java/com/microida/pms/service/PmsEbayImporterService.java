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
import com.microida.pms.domain.PmsProduct;
import com.microida.pms.util.OpenAIGPT;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Date;


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

            apiContext.setApiCredential(cred);
            apiContext.setApiServerUrl("https://api.ebay.com/wsapi");
            cred.seteBayToken("v^1.1#i^1#I^3#r^1#f^0#p^3#t^Ul4xMF82Ojc3NzZERUQ4NEM1QkZFOTJGNTA5NkM1QTA4ODk5QzMzXzNfMSNFXjI2MA==");

            GetItemCall apiCall = new GetItemCall(apiContext);
            apiCall.setItemID("222506599297");
            ItemType item =  apiCall.getItem();

            PmsProduct product = new PmsProduct();
            item.getItemID().replaceAll("\"", "");
            product.setIdOriginal(Long.valueOf(item.getItemID()));
            product.setNameOwn(OpenAIGPT.translate(item.getTitle()));
            product.setNameOriginal(item.getTitle());
            if(item.getDescription() != null){
                String description = item.getDescription().replaceAll("\\<.*?\\>", "");
                description = description.substring(0,6000);
                product.setDescriptionOriginal(description);
            }
            if(item.getPrimaryCategory() != null){
                product.setCatgoryOriginal(item.getPrimaryCategory().getCategoryName());
            }
            if(item.getBuyerGuaranteePrice() != null){
                product.setPriceOriginal(item.getBuyerGuaranteePrice().getValue());
                product.setPriceOwn(item.getBuyerGuaranteePrice().getValue() * 300 * 1.3);
            }
            if(item.getListingDetails() != null){
                product.setOriginalUrl(item.getListingDetails().getViewItemURL());
            }

            product.setDateCreated(OffsetDateTime.now());
            pmsProductService.create(product);
        } catch (Exception e) {
            System.out.println("Failed to get the eBay official time.");
            e.printStackTrace();
        }
    }
}
