package francis.wx.properties;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
        prefix = "wx.common"
)
@Data
@Accessors(chain = true)
public class WxProperties {

    private String AppID;

    private String appsecret;

    private String accessToken;

    private String transferUrl;

    private String tokenUrl;

    private String grant_type;

    private String accessTokenUrl;

    private String opendId;

    private String userInfoUrl;

    private String freshTokenUrl;

    private String menuUrl;

    private String redirectUrl;

}
