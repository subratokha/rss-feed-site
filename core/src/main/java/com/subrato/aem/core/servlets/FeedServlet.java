package com.subrato.aem.core.servlets;

import com.subrato.aem.core.CommonUtils;
import com.subrato.aem.core.dtos.Channel;
import com.subrato.aem.core.dtos.Item;
import com.subrato.aem.core.dtos.Rss;
import com.subrato.aem.core.services.FeedService;
import org.apache.commons.collections4.IterableUtils;
import org.apache.http.HttpStatus;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.subrato.aem.core.FeedConstants.*;

@Component(service = Servlet.class)
@SlingServletResourceTypes(resourceTypes = "feed/components/rss-feed", methods = HttpConstants.METHOD_GET, extensions = "json")

public class FeedServlet extends SlingSafeMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(FeedServlet.class);

    @Reference
    private FeedService feedService;

    @Override
    protected void doGet(final SlingHttpServletRequest slingRequest, final SlingHttpServletResponse slingResponse) throws IOException {
        Resource resource = slingRequest.getResource();
        List<Item> fallBackList = getFallBackSettings(resource);
        String listSize = resource.getValueMap().get("listSize", String.class);
        int size = listSize != null ? Integer.parseInt(listSize) : DEFAULT_FEED_SIZE;
        String response = buildResponse(size, fallBackList);

        slingResponse.setContentType("application/json");
        slingResponse.setCharacterEncoding("utf-8");
        slingResponse.getWriter().write(response);
        slingResponse.setStatus(HttpStatus.SC_OK);
    }

    /**
     * Get the Fallback Data in case of no response from endpoint
     *
     * @param resource Fallback resource
     * @return List of Items
     */
    private List<Item> getFallBackSettings(Resource resource) {
        Resource fallBackResource = resource.getChild(FALL_BACK_RESOURCE);
        Iterable<Resource> resourceIterable = IterableUtils.emptyIterable();
        if (fallBackResource != null && fallBackResource.hasChildren()) {
            resourceIterable = fallBackResource.getChildren();
        }
        List<Item> fallBackItemList = new ArrayList<>();
        for (Resource res : resourceIterable) {
            ValueMap valueMap = res.getValueMap();
            Item item = new Item();
            item.setTitle(valueMap.get(TITLE, String.class));
            item.setDescription(valueMap.get(DESCRIPTION, String.class));
            item.setPubDate(valueMap.get(PUBLISH_DATE, String.class));
            fallBackItemList.add(item);
        }
        return fallBackItemList;
    }

    private String buildResponse(int size, List<Item> fallBackList) {
        String response;
        try {
            Rss rss = feedService.getFeed();
            Channel channel = rss != null ? rss.getChannel() : null;
            List<Item> itemList = channel != null ? channel.getItems() : fallBackList;
            if (size > itemList.size()) {
                size = itemList.size();
            }
            response = CommonUtils.convertPojoToJson(itemList.subList(0, size));

        } catch (IOException e) {
            LOG.error("Error response from Feed service", e);
            response = CommonUtils.convertPojoToJson(fallBackList.subList(0, size));
        }
        return response;
    }


}
