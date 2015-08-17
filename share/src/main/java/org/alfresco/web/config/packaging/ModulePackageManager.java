/*
 * Copyright (C) 2005-2015 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.web.config.packaging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Examines Module Packages, on bootstrap lists the modules that are installed.
 * @author Gethin James
 */
public class ModulePackageManager implements InitializingBean
{
    public static final String MODULE_RESOURCES = "classpath*:alfresco/module/*/module.properties";
    private static Log logger = LogFactory.getLog(ModulePackageManager.class);

    List<ModulePackage> modules = new ArrayList<>();

    /**
     * Finds modules based on the resource path.
     * @param resourcePath path to resources
     * @return List<ModulePackage> the module packages
     */
    protected List<ModulePackage> resolveModules(String resourcePath)
    {
        Assert.notNull(resourcePath, "Resource path must not be null");
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        List<ModulePackage> modulesFound = new ArrayList<>();

        try
        {
            Resource[] resources = resolver.getResources(resourcePath);
            for(Resource resource : resources)
            {
                ModulePackage mp = asModulePackage(resource);
                if (mp != null) modulesFound.add(mp);
            }
        }
        catch (IOException ioe)
        {
            logger.error("Unable to resolve modules ", ioe);
        }
        return modulesFound;
    }

    /**
     * Takes a Resource and turns it into a ModulePackage
     * The current implementation only supports property files.
     * @param resource Spring resource
     * @return ModulePackage
     */
    protected ModulePackage asModulePackage(Resource resource)
    {
        Assert.notNull(resource, "Resource must not be null");

        try
        {
           return ModulePackageUsingProperties.loadFromResource(resource);
        }
        catch (IOException e)
        {
            logger.error("Failed to load resource "+resource.toString(), e);
            return null;
        }
    }


    /**
     * Writes a list of ModulePackages
     * @param foundModules  the module packages
     * @return String list the modules
     */
    protected String writeModuleList(List<ModulePackage> foundModules)
    {
        StringBuilder b = new StringBuilder(128);
        for (ModulePackage module : foundModules)
        {
            b.append(module.getTitle()).append(", " + module.getVersion()).append(", "+module.getDescription());
            b.append("\n");
        }
        return b.toString();
    }

    /**
     * Returns the available module packages in the application.
     * @return List<ModulePackage> the module packages
     */
    public List<ModulePackage> getModulePackages()
    {
        return modules;
    }

    @Override
    public void afterPropertiesSet()

    {
        logger.debug("Resolving module packages.");
        modules = resolveModules(MODULE_RESOURCES);
        String moduleList = writeModuleList(modules);
        if (!StringUtils.isEmpty(moduleList))
        {
            logger.info("Found "+ modules.size() +" module package(s)");
            logger.info(moduleList);
        }
    }

}
