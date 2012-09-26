# Grails CRM

CRM = [Customer Relationship Management](http://en.wikipedia.org/wiki/Customer_relationship_management)

Grails CRM is a set of [Grails Web Application Framework](http://www.grails.org/)
plugins that makes it easy to develop web application with CRM functionality.
With CRM we mean features like:

- Contact Management
- Task/Todo Lists
- Project Management

#Store i18n messages in database

This plugin provides storage of i18n messages in the database.
It is a clone of the *i18n-db* plugin customized for Grails CRM and tenant aware.

An admin UI is provided at <app-name>/crmMessage where you can add messages to the database that will override
messages defined in property files.

The plugin also has special fallback logic when it looks up messages.
Example: If the specified key is `crmContact.button.save` it tries to find messages using the following keys:

    <g:message code="crmContact.button.save"/>

1. crmContact.button.save
2. crmContact.button.save.label
3. default.button.save
4. default.button.save.label
5. calls standard Spring message source using original key (crmContact.button.save)

The plugin can use EhCache to cache messages retrieved from database (recommended).
To enable caching, configure a bean named "messageCache" in resources.groovy

Example:

    import org.springframework.cache.ehcache.EhCacheFactoryBean

    beans = {
        messageCache(EhCacheFactoryBean) {
            timeToLive = 3600
            timeToIdle = 1800
            maxElementsInMemory = 5000
            eternal = false
            overflowToDisk = false
        }
    }
