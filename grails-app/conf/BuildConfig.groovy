grails.project.work.dir = "target"
grails.project.target.level = 1.6

grails.project.repos.default = "crm"

grails.project.dependency.resolution = {
    inherits("global") {}
    log "warn"
    legacyResolve false
    repositories {
        grailsCentral()
        mavenCentral()
        mavenRepo "http://labs.technipelago.se/repo/crm-releases-local/"
    }
    dependencies {
    }
    plugins {
        build(":tomcat:$grailsVersion",
                ":release:2.2.1",
                ":webxml:1.4.1",
                ":rest-client-builder:1.0.3") {
            export = false
        }
        runtime(":hibernate:$grailsVersion") {
            export = false
        }
        test ":cache:1.1.1"

        compile "grails.crm:crm-core:1.2.5"
    }
}
