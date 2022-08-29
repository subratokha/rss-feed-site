package com.subrato.aem.core.servlets;

import com.subrato.aem.core.dtos.Rss;
import com.subrato.aem.core.services.FeedService;
import com.subrato.aem.core.testcontext.AppAemContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class FeedServletTest {

    public final AemContext aemContext = AppAemContext.newAemContext();

    @Mock
    private FeedService feedService;

    @InjectMocks
    FeedServlet feedServlet = new FeedServlet();

    @BeforeEach
    void setUp() throws IOException {
        aemContext.load().json("/com/subrato/aem/core/servlets/FeedServletTest.json", "/content");
        aemContext.requestPathInfo().setExtension("json");
        aemContext.currentResource("/content/rss-feed");
        Mockito.lenient().when(feedService.getFeed()).thenReturn(new Rss());
        feedService = aemContext.registerService(feedService);
    }

    @Test
    void doGetTest() throws IOException {
        feedServlet.doGet(aemContext.request(), aemContext.response());
        String expected = "[{\"title\":\"Sample 1\",\"description\":\"Sample 1 Description\",\"pubDate\":\"2021-03-21T10:20:14.000+01:00\"},{\"title\":\"Sample 2\",\"description\":\"Sample 2 Description\",\"pubDate\":\"2021-03-21T09:10:42.000+01:00\"}]";
        assertEquals(expected, aemContext.response().getOutputAsString());
    }
}
