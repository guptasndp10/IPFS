package com.fai.ipfs.services;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RestTemplateOverride {

    public RestTemplate restTemplate = new RestTemplate(getClientHttpReuqestFactory());

    private SimpleClientHttpRequestFactory getClientHttpReuqestFactory()
    {
        SimpleClientHttpRequestFactory clientHttpRequestFactory
                =new SimpleClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(5000);

        clientHttpRequestFactory.setReadTimeout(5000);
        return clientHttpRequestFactory;
    }
}
