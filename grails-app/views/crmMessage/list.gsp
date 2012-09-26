<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'crmMessage.label', default: 'Message')}"/>
    <title><g:message code="crmMessage.list.title" args="[entityName]"/></title>
</head>

<body>

<crm:header title="crmMessage.list.title" args="[entityName]"/>

<table class="table table-striped">
    <thead>
    <tr>
        <crm:sortableColumn property="code"
                            title="${message(code: 'crmMessage.code.label', default: 'Code')}"/>
        <crm:sortableColumn property="locale"
                            title="${message(code: 'crmMessage.locale.label', default: 'Locale')}"/>
        <crm:sortableColumn property="text"
                            title="${message(code: 'crmMessage.text.label', default: 'Text')}"/>
    </tr>
    </thead>
    <tbody>
    <g:each in="${crmMessageList}" var="crmMessage">
        <tr>
            <td>
                <g:link action="edit" id="${crmMessage.id}">
                    ${fieldValue(bean: crmMessage, field: "code")}
                </g:link>
            </td>
            <td>
                ${fieldValue(bean: crmMessage, field: "locale")}
            </td>
            <td>
                ${fieldValue(bean: crmMessage, field: "text")}
            </td>
        </tr>
    </g:each>
    </tbody>
</table>

<crm:paginate total="${crmMessageTotal}"/>

<div class="form-actions">
    <crm:button type="link" action="create" visual="success" icon="icon-file icon-white"
                label="crmMessage.create.label" permission="crmMessage:create"/>
    <crm:button type="link" action="index" icon="icon-font" label="crmMessage.index.label"/>
</div>

</body>
</html>
