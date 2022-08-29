package com.subrato.aem.core.workflow;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import java.util.*;
import java.util.stream.Collectors;

import static org.eclipse.jetty.util.URIUtil.SLASH;

@Component(service = WorkflowProcess.class, property = {"process.label = XF LiveCopy Sync Workflow"})
public class XFLiveCopySyncWorkflow implements WorkflowProcess {

    private static final Logger LOGGER = LoggerFactory.getLogger(XFLiveCopySyncWorkflow.class);

    final static String XF_ROOT_PATH = "/content/experience-fragments/feed";

    private ResourceResolver resourceResolver = null;

    /**
     * Overridden method which executes when the workflow is invoked
     */
    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {

        LOGGER.info("Executing the workflow");
        String payloadPath = workItem.getWorkflowData().getPayload().toString();
        resourceResolver = workflowSession.adaptTo(ResourceResolver.class);
        try {
            if (resourceResolver != null) {
                String languages = resourceResolver.getResource(payloadPath + "/jcr:content").getValueMap().get("languageValue", String.class);
                List<String> targetParentList = getTargetParents(payloadPath, languages);
                copyLiveCopies(payloadPath, targetParentList);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (resourceResolver != null && resourceResolver.isLive()) {
                resourceResolver.close();
            }
        }
    }

    /**
     * Get List of target Live Copies
     *
     * @param sourcePath    source XF
     * @param targetLocales locales
     * @return
     */
    private List<String> getTargetParents(String sourcePath, String targetLocales) {
        List<String> targetsParentsList = new ArrayList<>();
        List<String> localeList = Arrays.stream(targetLocales.split(",")).map(String::trim).collect(Collectors.toList());

        String[] locale = sourcePath.split(SLASH);
        String sourceLocale = locale[5];
        String sourceXFParentPath = resourceResolver.getResource(sourcePath).getParent().getPath();
        String targetRoot = sourceXFParentPath;
        if (!sourceLocale.equals("it_tl")) {
            targetRoot = sourceXFParentPath.startsWith(XF_ROOT_PATH + "/global/") ?
                    sourceXFParentPath.replace(XF_ROOT_PATH + "/global", XF_ROOT_PATH + "/sites") : sourceXFParentPath;
        }

        for (String targetLocale : localeList) {
            targetsParentsList.add(targetRoot.replace(sourceLocale, targetLocale));
        }
        return targetsParentsList;
    }

    private void setLiveCopyRelationShip(Resource resource) {
        try {
            if (resource != null) {
                Node postNode = resource.adaptTo(Node.class);
                postNode.addMixin("cq:LiveRelationship");
            }
        } catch (Exception e) {
            LOGGER.error("Exception while getting the node :: {}", e.getMessage(), e);
        }
    }

    private void copyLiveCopies(String sourcePath, List<String> targetParentsList) {
        Resource resource = resourceResolver.getResource(sourcePath);
        if (resource != null) {
            for (String targetParent : targetParentsList) {
                try {
                    Resource targetResource = resourceResolver.copy(sourcePath, targetParent);
                    Resource sourceResource = targetResource;
                    targetResource = resourceResolver.getResource(targetResource.getPath() + "/jcr:content");
                    setLiveCopyRelationShip(targetResource);
                    Objects.requireNonNull(targetResource).getResourceResolver().commit();

                    Map<String, Object> properties = new HashMap<>();
                    properties.put("jcr:primaryType", "cq:LiveCopy");
                    properties.put("cq:master", sourcePath);
                    properties.put("cq:isDeep", true);

                    resourceResolver.create(targetResource, "cq:LiveSyncConfig", properties);
                    Iterable<Resource> childResources = sourceResource.getChildren();

                    for (Resource childRes : childResources) {
                        if (!childRes.getPath().endsWith("jcr:content")) {
                            Objects.requireNonNull(childRes.adaptTo(Node.class)).remove();
                        }
                    }
                    resourceResolver.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
