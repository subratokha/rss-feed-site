package com.subrato.aem.core.services.impl;

import com.subrato.aem.core.CommonUtils;
import com.subrato.aem.core.clients.FeedClient;
import com.subrato.aem.core.dtos.Rss;
import com.subrato.aem.core.services.FeedService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * FeedService Implementation class to get RSS Feed from endpoint and convert them to RSS Object
 */
@Component(service = FeedService.class, configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(
        ocd = FeedConfigurations.class
)
public class FeedServiceImpl implements FeedService {

    private static final Logger LOG = LoggerFactory.getLogger(FeedServiceImpl.class);
    public static final String BOM = "\uFEFF";

    private FeedClient feedClient;
    private String feedUrl;

    @Activate
    public void activate(FeedConfigurations feedConfigurations) {
        this.feedClient = new FeedClient();
        this.feedUrl = feedConfigurations.url();
    }

    /**
     * Get RSS Feed
     *
     * @return RSS Object
     * @throws IOException IOException
     */
    @Override
    public Rss getFeed() throws IOException {
        String response = this.feedClient.get(feedUrl);
        return CommonUtils.convertXmlToPojo(removeBOM(response));
    }

    private String removeBOM(String input) {
        if (input.startsWith(BOM)) {
            input = input.substring(1);
        }
        return input;
    }



}
