package com.microida.pms.service;

import com.google.gson.JsonParser;
import com.microida.pms.domain.PmsProduct;
import com.microida.pms.domain.PmsUser;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PmsJofogasService {

    private WebApplicationContext context;

    private final PmsProductService pmsProductService;

    private final PmsUserService pmsUserService;

    private final PmsDescriptionService pmsDescriptionService;

    public PmsJofogasService(final PmsProductService pmsProductService, final PmsUserService pmsUserService, final PmsDescriptionService pmsDescriptionService) {
        this.pmsProductService = pmsProductService;
        this.pmsUserService = pmsUserService;
        this.pmsDescriptionService = pmsDescriptionService;
    }

    @Autowired
    public void setServletContext(ServletContext servletContext) {
        this.context = WebApplicationContextUtils.getWebApplicationContext(servletContext);
    }

    public void uploadProducts(List<Long> productIds) {
        try {
            PmsUser user = pmsUserService.getActiveUser();

            CloseableHttpClient httpClient = login(user);

            for (Long productId : productIds) {
                PmsProduct product = pmsProductService.get(productId);

                String[] imagesIds = Files.list(new File("src/main/media/", product.getId() + "").toPath())
                        .map(path -> path.toFile().getPath())
                        .filter(path -> path.endsWith(".jpg"))
                        .limit(10)
                        .map(path -> {
                            try {
                                return uploadImage(httpClient, path);
                            } catch (IOException e) {
                                e.printStackTrace();
                                return "";
                            }
                        })
                        .toArray(String[]::new);

                String imageIdsStr = String.join(";", imagesIds);

                uploadProduct(httpClient, user, product, imageIdsStr);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public String uploadImage(CloseableHttpClient httpClient, String imagePath) throws IOException {
        HttpPost request = new HttpPost("https://apiv2.jofogas.hu/v2/media/upload");

        request.addHeader("accept", "application/json");

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody("media_type", "ad", ContentType.TEXT_PLAIN);

        File file = new File(imagePath);
        builder.addBinaryBody("file", Files.readAllBytes(file.toPath()),
                ContentType.create(Files.probeContentType(file.toPath())), file.getName());

        HttpEntity multipart = builder.build();
        request.setEntity(multipart);

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseString = EntityUtils.toString(response.getEntity());
            System.out.println(responseString);
            return JsonParser.parseString(responseString).getAsJsonObject().get("media").getAsJsonObject().get("id").getAsString();
        }
    }

    public void uploadProduct(CloseableHttpClient httpClient, PmsUser user, PmsProduct product, String imageIdsStr) throws IOException {
        HttpPost request = new HttpPost("https://www2.jofogas.hu/ai/verify/1");

        request.addHeader("accept", "application/json");

        CookieStore cookieStore = new BasicCookieStore();
        String cookie = cookieStore.getCookies().stream()
                .map(c -> c.getName() + "=" + c.getValue())
                .collect(Collectors.joining("; "));
        cookieStore.addCookie(new BasicClientCookie("session_http", JsonParser.parseString(cookie).getAsJsonObject().get("account_token").getAsString()));
        cookieStore.addCookie(new BasicClientCookie("uid", "8855364"));

        HttpUriRequest httpUriRequest = RequestBuilder.post()
                .setUri("https://www2.jofogas.hu/ai/verify/1")
                .addParameter("category", "5010")
                .addParameter("type", "s")
                .addParameter("image", imageIdsStr)
                .addParameter("subject", product.getNameOwn())
                .addParameter("body", product.getDescriptionOwn())
                .addParameter("price", Double.toString(product.getPriceOwn()))
                .addParameter("company_ad", "0")
                .addParameter("zipcode", "2100")
                .addParameter("city", "1939")
                .addParameter("email", user.getEmail())
                .addParameter("name", user.getName())
                .addParameter("phone", user.getPhone())
                .addParameter("chat", "true")
                .addParameter("accept_terms", "1")
                .setConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(5000)
                        .build())
                .build();

        try (CloseableHttpResponse response = httpClient.execute(httpUriRequest)) {
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity);
            System.out.println(responseString);
        }
    }

    public CloseableHttpClient login(PmsUser user) throws IOException {
        HttpPost request = new HttpPost("https://apiv2.jofogas.hu/v2/account/login?email=" + user.getEmail() + "&password=" + user.getPassword() + "&site_version=web");

        request.addHeader("accept", "application/json");
        request.addHeader("api_key", "jofogas-web-eFRv9myucHjnXFbj");

        try (CloseableHttpResponse response = HttpClients.createDefault().execute(request)) {
            String responseString = EntityUtils.toString(response.getEntity());
            System.out.println(responseString);
            CookieStore cookieStore = new BasicCookieStore();
            cookieStore.addCookie(new BasicClientCookie("session_id", JsonParser.parseString(responseString).getAsJsonObject().get("account_token").getAsString()));
            cookieStore.addCookie(new BasicClientCookie("session_http", JsonParser.parseString(responseString).getAsJsonObject().get("account_token").getAsString()));
            cookieStore.addCookie(new BasicClientCookie("uid", "8855364"));

            return HttpClients.custom()
                    .setDefaultRequestConfig(RequestConfig.custom()
                            .setConnectTimeout(5000)
                            .setSocketTimeout(5000)
                            .build())
                    .setDefaultCookieStore(cookieStore)
                    .build();
        }
    }
}
