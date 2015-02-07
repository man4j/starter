<#function isAuthorized>
  <#return securityService.currentPrincipal != "anonymousUser" />
</#function>

<#macro authorized>
  <#if isAuthorized()>
    <#nested />      
  </#if>
</#macro>

<#macro hasRole userRole>
  <#if securityService.roles?seq_contains(userRole)>
    <#nested />
  </#if>
</#macro>