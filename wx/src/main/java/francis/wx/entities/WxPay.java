package francis.wx.entities;

import francis.wx.utils.WXPaySignature;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Map;
import java.util.stream.Collectors;

@Data
@Accessors(chain = true)
@NoArgsConstructor(staticName = "of")
public class WxPay {

    private String mch_appid;

    private String mchid;

    private String nonce_str;

    private String sign;

    private String partner_trade_no;

    private String openid;

    private String check_name;

    private int amount;

    private String desc;

    private String spbill_create_ip;

    public Map<String,Object> toMap(){
        return WXPaySignature.convertToMap(this).entrySet().stream().
                collect(Collectors.toMap(set -> set.getKey().toString(), set -> set.getValue()));

    }
}
