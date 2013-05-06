liferay61-webcontent-preview
============================

This is a Liferay plugin SDK for version 6.1 CE GA2.
It contains a hook for webcontent-preview through Esigate (http://www.esigate.org/).

Warning : the hook overrides the default ViewArticleContentAction.java and the /html/portlet/journal_content/view.jsp from Liferay.
it may cause conflicts with other hooks.

Usage : 
- configure in portal-ext.properties the esigate template base URL "journal.preview.url.base=http://myserver.com:myport"
The server must have access to this URL itself. If not set, teh default value is "http://localhost:8080"
- deploy the hook to your server


TODO : 
- tag fragments with the portlet instance id and inject the article content in the right fragement
- make a control panel portlet where administrator can configure for each structure the template layout and the instance id of the portlet to preview in
- integrate with asset publisher
