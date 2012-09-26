<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'crmMessage.label', default: 'Message')}"/>
    <title><g:message code="crmMessage.index.title" args="[entityName]"/></title>
    <r:script>
        function insertParam(key, value) {
            key = encodeURIComponent(key);
            value = encodeURIComponent(value);

            var kvp = window.location.search.substr(1).split('&');
            if (kvp == '') {
                return '?' + key + '=' + value;
            } else {
                var i = kvp.length;
                var x;
                while (i--) {
                    x = kvp[i].split('=');

                    if (x[0] == key) {
                        x[1] = value;
                        kvp[i] = x.join('=');
                        break;
                    }
                }
                if (i < 0) {
                    kvp[kvp.length] = [key, value].join('=');
                }
                return kvp.join('&');
            }
        }
        $(document).ready(function () {
            $("#locale").change(function (ev) {
                var locale = $("#locale option:selected").val();
                window.location.search = insertParam("lang", locale);
            });
        });
    </r:script>
</head>

<body>

<crm:header title="crmMessage.index.title" args="[entityName]"/>

<g:uploadForm>

    <div class="row-fluid">
        <div class="span7">
            <p class="lead"><g:message code="crmMessage.index.message" default="Change system messages."/>
            </p>
        </div>
    </div>

    <div class="tabbable">

        <ul class="nav nav-tabs">
            <li class="active"><a href="#main" data-toggle="tab"><g:message code="crmMessage.tab.main.label"/></a></li>
            <crm:pluginViews location="tabs" var="view">
                <li>
                    <a href="#${view.id}" data-toggle="tab">${view.label.encodeAsHTML()}</a>
                </li>
            </crm:pluginViews>
        </ul>

        <div class="tab-content">
            <div class="tab-pane active" id="main">

                <div class="row-fluid">
                    <div class="span6">
                        <div class="row-fluid">
                            <div class="control-group">
                                <label class="control-label">Application name</label>

                                <div class="controls">
                                    <g:textField name="properties.app.name" value="${properties['app.name']}"
                                                 placeholder="${message(code: 'app.name', default: '')}"
                                                 class="span10"/>
                                </div>
                            </div>
                        </div>

                    </div>

                    <div class="span6">
                        <div class="row-fluid">
                            <div class="control-group">
                                <label class="control-label">Error header</label>

                                <div class="controls">
                                    <g:textField name="properties.exception.title"
                                                 value="${properties['exception.title']}"
                                                 placeholder="${message(code: 'exception.title', default: '')}"
                                                 class="span10"/>
                                </div>
                            </div>

                            <div class="control-group">
                                <label class="control-label">Error instructions</label>

                                <div class="controls">
                                    <g:textField name="properties.exception.subtitle"
                                                 value="${properties['exception.subtitle']}"
                                                 placeholder="${message(code: 'exception.subtitle', default: '')}"
                                                 class="span10"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

            </div>

            <crm:pluginViews location="tabs" var="view">
                <div class="tab-pane" id="${view.id}">
                    <g:render template="${view.template}" model="${view.model}" plugin="${view.plugin}"/>
                </div>
            </crm:pluginViews>
        </div>

    </div>

    <div class="control-group">
        <div class="control-label"><g:message code="crmMessage.locale.label" default="Locale"/></div>

        <div class="controls">
            <g:localeSelect name="lang" id="locale" value="${locale}" noSelection="['': '']"/>
        </div>
    </div>

    <div class="form-actions">
        <crm:button action="index" visual="primary" icon="icon-ok icon-white" label="crmMessage.button.update.label"/>
        <crm:button type="link" action="create" visual="success" icon="icon-file icon-white"
                    label="crmMessage.create.label" permission="crmMessage:create"/>
        <crm:button type="link" action="list" icon="icon-list-alt"
                    label="crmMessage.list.label" permission="crmMessage:list"/>
        <crm:button type="link" action="export" visual="info" icon="icon-share-alt icon-white"
                    label="crmMessage.button.export.label"/>
        <crm:button action="upload" visual="primary" icon="icon-download-alt icon-white"
                    label="crmMessage.button.upload.label"/>
        <input type="file" name="file"/>
    </div>
</g:uploadForm>

</body>
</html>