package francis.wx.entities;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor(staticName = "of")
public class Menu {

    private String type;

    private String name;

    private String url;

}
