package francis.wx.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import francis.wx.entities.Menu;
import francis.wx.entities.WxPay;
import francis.wx.properties.WxProperties;
import francis.wx.utils.RandomStringGenerator;
import francis.wx.utils.WXPaySignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class WxController {

    private final static Logger logger = LoggerFactory.getLogger(WxController.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WxProperties wxProperties;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping(value = "/wxPay")
    public String wxPay() throws JsonProcessingException {
        XmlMapper xmlMapper = new XmlMapper();

        WxPay wxPay = WxPay.of().setDesc("a").setNonce_str(RandomStringGenerator.getRandomStringByLength(32))
                .setAmount(1).setMch_appid("wx2d1132eedf76e51d").setMchid("1488746752").setCheck_name("NO_CHECK")
                .setSpbill_create_ip("180.173.198.126").setPartner_trade_no("order").setOpenid("oxytysz3rJODh_DV4LLjx2jad9zs");

        String args = xmlMapper.writeValueAsString(wxPay.setSign(WXPaySignature.getSign(wxPay.toMap())));


        ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity(wxProperties.getTransferUrl(), args, String.class);
        return stringResponseEntity.toString();
    }


    @GetMapping(value = "/verify")
    public String verify(String echostr) {
        logger.info(echostr);
        return echostr;
    }


    @GetMapping(value = "/getAccessToken")
    public String gerAccessToken() {

        String returnValue = restTemplate.getForObject(UriComponentsBuilder.fromHttpUrl(wxProperties.getTokenUrl())
                .queryParam("grant_type", wxProperties.getGrant_type())
                .queryParam("appid", wxProperties.getAppID())
                .queryParam("secret", wxProperties.getAppsecret()).toUriString(), String.class);

        try {
            wxProperties.setAccessToken(objectMapper.readTree(returnValue).get("access_token").textValue());
        } catch (IOException e) {
            logger.error("read access_token fail", e);
        }

        return wxProperties.getAccessToken();
    }

    //静默授权
    @GetMapping(value = "/callback")
    public String callback(String code) {
        logger.info(code);

        String returnValue = restTemplate.getForObject(UriComponentsBuilder.fromHttpUrl(wxProperties.getAccessTokenUrl())
                .queryParam("appid", wxProperties.getAppID())
                .queryParam("secret", wxProperties.getAppsecret())
                .queryParam("code", code)
                .queryParam("grant_type", "authorization_code").toUriString(), String.class);

        try {
            wxProperties.setOpendId(objectMapper.readTree(returnValue).get("openid").textValue());
        } catch (IOException e) {
            logger.error("read openid fail", e);
        }

        logger.info(wxProperties.getOpendId());
        return wxProperties.getOpendId();
    }


    //主动授权
    @GetMapping(value = "/callback2")
    public String callback2(String code) {
        logger.info(code);


        String returnValue = restTemplate.getForObject(UriComponentsBuilder.fromHttpUrl(wxProperties.getAccessTokenUrl())
                .queryParam("appid", wxProperties.getAppID())
                .queryParam("secret", wxProperties.getAppsecret())
                .queryParam("code", code)
                .queryParam("grant_type", "authorization_code").toUriString(), String.class);

        try {
            wxProperties.setAccessToken(objectMapper.readTree(returnValue).get("access_token").textValue()).setOpendId(
                    objectMapper.readTree(returnValue).get("openid").textValue());
        } catch (IOException e) {
            logger.error("read openid fail", e);
        }


        String userInfo = restTemplate.getForObject(UriComponentsBuilder.fromHttpUrl(wxProperties.getUserInfoUrl())
                .queryParam("access_token", wxProperties.getAccessToken())
                .queryParam("openid", wxProperties.getOpendId())
                .queryParam("lang", "zh_CN ").toUriString(), String.class);


        logger.info(returnValue);
        logger.info(wxProperties.getOpendId());
        logger.info(userInfo + "  -userInfo");
        logger.info(wxProperties.getOpendId());
        return userInfo;
    }


    @GetMapping(value = "/menu")
    public String menu() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, List<Menu>> menuMap = new HashMap<>(1);
        List<Menu> arrayList = new ArrayList<>(1);
        arrayList.add(Menu.of().setName("获取用户信息").setType("view").setUrl(wxProperties.getRedirectUrl()));
        menuMap.put("button", arrayList);

        String requestJson = objectMapper.writeValueAsString(menuMap);

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

        return restTemplate.postForObject(UriComponentsBuilder.fromHttpUrl(wxProperties.getMenuUrl())
                .queryParam("access_token", wxProperties.getAccessToken()).toUriString(), entity, String.class);
    }

}
