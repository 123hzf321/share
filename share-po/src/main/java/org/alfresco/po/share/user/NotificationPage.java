/*
 * #%L
 * share-po
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
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
 * #L%
 */

package org.alfresco.po.share.user;

import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import org.alfresco.po.ElementState;
import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.SharePage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;

/**
 * User Notification page object has a checkbox to enable and disable notification feed emails.
 * 
 * @author Jamie Allison
 * @since 4.3
 */
public class NotificationPage extends SharePage
{
    private static final By NOTIFICATION_CHECKBOX = By.cssSelector("#user-notifications-email");
    private static final By OK_BUTTON = By.cssSelector("button[id$='default-button-ok-button']");
    private static final By CANCEL_BUTTON = By.cssSelector("button[id$='default-button-cancel-button']");

    private final Log logger = LogFactory.getLog(NotificationPage.class);

    @SuppressWarnings("unchecked")
    @Override
    public NotificationPage render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(NOTIFICATION_CHECKBOX), getVisibleRenderElement(OK_BUTTON), getVisibleRenderElement(CANCEL_BUTTON));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public NotificationPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Get the navigation bar.
     * 
     * @return ProfileNavigation
     */
    public ProfileNavigation getProfileNav()
    {
        return new ProfileNavigation(driver, factoryPage);
    }

    /**
     * Returns <code>true</code> if the OK button is displayed.
     * 
     * @return boolean
     */
    public boolean isOkButtonDisplayed()
    {
        try
        {
            return driver.findElement(OK_BUTTON).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("OK button not Present", e);
            }
        }
        return false;
    }

    /**
     * Returns <code>true</code> if the Cancel button is displayed.
     * 
     * @return boolean
     */
    public boolean isCancelButtonDisplayed()
    {
        try
        {
            return driver.findElement(CANCEL_BUTTON).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Cancel button not Present", e);
            }
        }
        return false;
    }

    /**
     * Returns <code>true</true> if the Notification Feed checkbox is checked.
     * 
     * @return boolean
     */
    public boolean isNotificationFeedChecked()
    {
        try
        {
            return driver.findElement(NOTIFICATION_CHECKBOX).isSelected();
        }
        catch (NoSuchElementException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Notification Feed checkbox not present", e);
            }
        }
        return false;
    }

    /**
     * Set the notification feed checkbox status.
     * 
     * @param enabled <code>true</code> to check the checkbox.
     */
    public void toggleNotificationFeed(boolean enabled)
    {
        try
        {
            if (enabled != isNotificationFeedChecked())
            {
                findAndWait(NOTIFICATION_CHECKBOX).click();
            }
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Unable to find Notification checkbox", e);
        }
    }

    /**
     * Click the OK button to confirm the notification selection.
     * 
     * @return {@link MyProfilePage}
     */
    public HtmlPage selectOk()
    {
          return submit(OK_BUTTON, ElementState.DELETE_FROM_DOM);
    }

    /**
     * Click the Cancel button to keep the existing notification setting.
     * 
     * @return {@link MyProfilePage}
     */
    public HtmlPage selectCancel()
    {
        return submit(CANCEL_BUTTON, ElementState.DELETE_FROM_DOM);
    }
}
