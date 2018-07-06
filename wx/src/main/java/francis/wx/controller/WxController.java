package francis.wx.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import francis.wx.entities.WxPay;
import francis.wx.utils.RandomStringGenerator;
import francis.wx.utils.WXPaySignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class WxController {

    @Autowired
    private RestTemplate restTemplate;



    @GetMapping(value = "/wxPay")
    public String wxPay () throws JsonProcessingException {
        XmlMapper xmlMapper = new XmlMapper();

        WxPay wxPay = WxPay.of().setDesc("a").setNonce_str(RandomStringGenerator.getRandomStringByLength(32))
                .setAmount(1).setMch_appid("wx2d1132eedf76e51d").setMchid("1488746752").setCheck_name("NO_CHECK")
                .setSpbill_create_ip("180.173.198.126").setPartner_trade_no("order").setOpenid("oxytysz3rJODh_DV4LLjx2jad9zs");

        String args = xmlMapper.writeValueAsString(wxPay.setSign(WXPaySignature.getSign(wxPay.toMap())));


        ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity("https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers", args, String.class);
        return stringResponseEntity.toString();
    }

}
