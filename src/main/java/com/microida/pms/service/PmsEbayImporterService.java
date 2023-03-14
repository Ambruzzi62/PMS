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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.Arrays;


@Service
public class PmsEbayImporterService {

    private final PmsProductService pmsProductService;

    private final PmsParameterService pmsParameterService;

    private static String EBAY_AUTHNAUTH_KEY;

    public PmsEbayImporterService(final PmsProductService pmsProductService, final PmsParameterService pmsParameterService) {
        this.pmsProductService = pmsProductService;
        this.pmsParameterService = pmsParameterService;
        EBAY_AUTHNAUTH_KEY = pmsParameterService.get("EBAY_AUTHNAUTH_KEY").getValue();
    }

    public Long importProduct(String productUrl){
        int start = productUrl.indexOf("/itm/") + 5;
        int end = productUrl.indexOf("?");
        String itemId = productUrl.substring(start, end);
        Long productId = null;
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
            cred.seteBayToken(EBAY_AUTHNAUTH_KEY);

            GetItemCall apiCall = new GetItemCall(apiContext);
            apiCall.setItemID(itemId);
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
            productId = pmsProductService.create(product);

            saveItemPictures(productId, item.getPictureDetails().getPictureURL());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return productId;
    }

    private void saveItemPictures(Long productId, String[] pictureUrls) throws IOException {
        for (int i = 0; i < pictureUrls.length; i++) {
            String imgPath = "src/main/media/" + productId + "/"  + i + ".jpg";
            Path destinationPath = Paths.get(imgPath);

            InputStream in = null;
            FileOutputStream out = null;
            try {
                URL url = new URL(pictureUrls[i]);
                URLConnection conn = url.openConnection();
                in = conn.getInputStream();
                Files.createDirectories(destinationPath.getParent());
                out = new FileOutputStream(destinationPath.toFile());

                byte[] buffer = new byte[1024];
                int length;

                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                out.close();
                in.close();
            }
        }
    }
}
