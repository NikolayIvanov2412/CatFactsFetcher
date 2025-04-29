package ru.netology;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class CatFactsFetcher {

    public static void main(String[] args) throws IOException {

        final String url = "https://raw.githubusercontent.com/netology-code/jd-homeworks/master/http/task1/cats";

        try (CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(30000)
                        .setRedirectsEnabled(false)
                        .build())
                .build()) {

            HttpGet request = new HttpGet(url);
            CloseableHttpResponse response = httpClient.execute(request);

            if (response.getStatusLine().getStatusCode() == 200) {

                HttpEntity entity = response.getEntity();
                String jsonData = EntityUtils.toString(entity);

                ObjectMapper mapper = new ObjectMapper();
                CollectionType listType = mapper.getTypeFactory().constructCollectionType(List.class, Fact.class);
                List<Fact> factsList = mapper.readValue(jsonData, listType);

                // Фильтруем факты с ненулевыми голосами
                List<Fact> filteredFacts = factsList.stream()
                        .filter(fact -> fact.getUpvotes() != null && fact.getUpvotes() > 0)
                        .collect(Collectors.toList());

                System.out.println(filteredFacts.size() + " фактов отобрано:");
                for (Fact fact : filteredFacts) {
                    System.out.println(fact.getText());
                }
            } else {
                System.err.println("Ошибка запроса: статус-код " + response.getStatusLine().getStatusCode());
            }
        }
    }
}