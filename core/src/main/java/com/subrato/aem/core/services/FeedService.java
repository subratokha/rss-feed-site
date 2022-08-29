package com.subrato.aem.core.services;

import com.subrato.aem.core.dtos.Rss;

import java.io.IOException;

/**
 * FeedService Interface
 */
public interface FeedService {

    /**
     * Get RSS Feed
     *
     * @return Rss Object
     * @throws IOException
     */
    Rss getFeed() throws IOException;

}
