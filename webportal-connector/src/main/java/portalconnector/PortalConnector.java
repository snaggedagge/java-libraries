package portalconnector;


import dkarlsso.commons.repository.S3PersistenceRepository;
import org.springframework.web.client.RestTemplate;
import portalconnector.model.PortalConnectorException;
import portalconnector.model.WebsiteDTO;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class PortalConnector extends S3PersistenceRepository<WebsiteDTO, String> {

    private final RestTemplate restTemplate = new RestTemplate();

    public PortalConnector() {
        super("data/websites.json", "dkarlsso.com", WebsiteDTO::getWebsiteId, WebsiteDTO.class);
    }


    // TODO: Should probably have some kind of hashed clientSecret for this...
    public WebsiteDTO addWebsite(final WebsiteDTO websiteDTO, final boolean httpsActivated, final int port)
            throws PortalConnectorException {
        try {
            final String prefix = httpsActivated ? "https://" : "http://";

            final String portString = port == 80 ? "" : ":" + port;
            if (websiteDTO.getWebsiteLink() == null) {
                websiteDTO.setWebsiteLink(prefix + restTemplate.getForObject("https://api.ipify.org/", String.class) + portString);
            }

            if (websiteDTO.getLocalWebsiteLink() == null) {
                websiteDTO.setLocalWebsiteLink(prefix + getLocalAdress() + portString);
            }
            this.save(websiteDTO);
            return websiteDTO;
        }
        catch (final Exception e) {
            throw new PortalConnectorException("Could not add website", e);
        }
    }

    private String getLocalAdress() {
        String localAdress = null;
        try {
            final Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while(networkInterfaces.hasMoreElements()) {
                final Enumeration<InetAddress> addressEnumeration = networkInterfaces.nextElement().getInetAddresses();
                while(addressEnumeration.hasMoreElements()) {
                    final InetAddress inetAddress = addressEnumeration.nextElement();
                    if (inetAddress.getHostAddress().contains("192.")) {
                        localAdress = inetAddress.getHostAddress();
                    }
                }
            }
        } catch (final Exception e) {
            try {
                localAdress = InetAddress.getLocalHost().getHostAddress();
            } catch (final Exception e1) { }
        }
        return localAdress;
    }
}
