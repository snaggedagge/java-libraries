package portalconnector.model;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WebsiteDTO {

    private String websiteId;

    private String websiteName;

    private String websiteDescription;

    private String imageBase64;

    private String websiteLink;

    private String localWebsiteLink;

    private String infoLink;

    private Permission permission;

    private Instant lastSeen;

    private boolean hasLogin;
}
