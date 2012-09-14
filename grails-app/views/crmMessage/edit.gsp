<%@ page import="grails.plugins.crm.i18n.CrmMessage" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'crmMessage.label', default: 'Message')}"/>
    <title><g:message code="crmMessage.edit.title" args="[entityName, crmMessage]"/></title>
</head>

<body>

<g:hasErrors bean="${crmMessage}">
    <bootstrap:alert class="alert-error">
        <ul>
            <g:eachError bean="${crmMessage}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message
                        error="${error}"/></li>
            </g:eachError>
        </ul>
    </bootstrap:alert>
</g:hasErrors>

<crm:header title="crmMessage.edit.title" args="[entityName, crmMessage]"/>

<fieldset>
    <g:form class="form-horizontal" action="edit"
            id="${crmMessage?.id}">
        <g:hiddenField name="version" value="${crmMessage?.version}"/>
        <fieldset>
            <f:with bean="crmMessage">
                <f:field property="code"/>
                <f:field property="locale"><g:localeSelect name="locale" value="${crmMessage.locale}"
                                                             noSelection="['':'']"/></f:field>
                <f:field property="text"><g:textArea name="text" cols="70" rows="3" class="span6" autofocus=""
                                                     value="${crmMessage.text}"/></f:field>
            </f:with>
            <div class="form-actions">
                <crm:button visual="primary" icon="icon-ok icon-white" label="crmMessage.button.update.label"/>
                <crm:button action="delete" visual="danger" icon="icon-trash icon-white" label="crmMessage.button.delete.label"
                            confirm="crmMessage.button.delete.confirm.message" permission="crmMessage:delete"/>
            </div>
        </fieldset>
    </g:form>
</fieldset>

</body>
</html>
