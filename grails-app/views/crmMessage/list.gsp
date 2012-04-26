<%@ page import="grails.plugins.crm.i18n.CrmMessage" %>
<!doctype html>
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

        <g:sortableColumn property="code"
                          title="${message(code: 'crmMessage.code.label', default: 'Code')}"/>

        <g:sortableColumn property="locale"
                          title="${message(code: 'crmMessage.locale.label', default: 'Locale')}"/>

        <g:sortableColumn property="text"
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

<div class="pagination">
    <bootstrap:paginate total="${crmMessageTotal}"/>
</div>
</body>
</html>
