<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'crmMessage.label', default: 'Message')}"/>
    <title><g:message code="crmMessage.create.title" args="[entityName]"/></title>
</head>

<body>

<g:hasErrors bean="${crmMessage}">
    <crm:alert class="alert-error">
        <ul>
            <g:eachError bean="${crmMessage}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message
                        error="${error}"/></li>
            </g:eachError>
        </ul>
    </crm:alert>
</g:hasErrors>

<crm:header title="crmMessage.create.title" args="[entityName]"/>

<fieldset>
    <g:form class="form-horizontal" action="create">
        <fieldset>
            <f:with bean="crmMessage">
                <f:field property="code" input-autofocus=""/>
                <f:field property="locale"><g:localeSelect name="locale" value="${crmMessage.locale}"
                                                             noSelection="['':'']"/></f:field>
                <f:field property="text"><g:textArea name="text" cols="70" rows="3" class="span6"
                                                     value="${crmMessage.text}"/></f:field>
            </f:with>
            <div class="form-actions">
                <crm:button visual="success" icon="icon-ok icon-white" label="crmMessage.button.create.label"/>
            </div>
        </fieldset>
    </g:form>
</fieldset>
</body>
</html>
