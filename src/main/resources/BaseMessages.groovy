package org.diorite.groovy

import org.apache.commons.lang3.StringUtils
import org.diorite.message.GroovyMessages

class BaseMessages implements GroovyMessages
{
    private Binding messages = new Binding();

    Object getProperty(String property)
    {
        if (messages.hasVariable(property))
        {
            return messages.getVariable(property)
        }
        String[] split = StringUtils.splitPreserveAllTokens(property, '.');
        if (split.length != 1)
        {
            Object node = this;
            for (String nodeKey : split)
            {
                node = node[nodeKey];
            }
            return node;
        }
        BaseMessages newNode = new BaseMessages();
        messages.setVariable(property, newNode)
        return newNode;
    }

    void setProperty(String property, Object newValue)
    {
        if ("metaClass" == property)
        {
            setMetaClass((MetaClass) newValue)
        }
        else
        {
            messages.setVariable(property, newValue)
        }
    }
}