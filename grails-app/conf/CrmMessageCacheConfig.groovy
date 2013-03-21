import grails.plugins.crm.i18n.CrmMessage
import grails.plugins.crm.i18n.CrmMessageSource

config = {
    cache {
        name CrmMessageSource.CRM_MESSAGE_CACHE // 'crmMessageCache'
        eternal false
        overflowToDisk false
        maxElementsInMemory 10000
        maxElementsOnDisk 0
        timeToIdleSeconds 60 * 30
        timeToLiveSeconds 60 * 60
    }

    domain {
        name CrmMessage
        eternal false
        overflowToDisk false
        maxElementsInMemory 1000
        maxElementsOnDisk 0
        timeToLiveSeconds 600
        timeToIdleSeconds 300
    }
}
