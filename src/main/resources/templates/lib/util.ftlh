<#import "/spring.ftl" as spring/>

<#macro errorClass fieldName err dflt="">
  <@spring.bind fieldName />
              
  <#if spring.status.error>
    <#assign errClass=err />
  <#else>
    <#assign errClass=dflt />
  </#if>
</#macro>

<#function errClass fieldName className defaultClassName="">
  <@spring.bind fieldName />
  
  <#if spring.status.error>
  	<#return className />
  <#else>
  	<#return defaultClassName />
  </#if>
</#function>

<#macro ifIsCurrentUri uri what="" other="">
  <#assign checkedUrl = rc.getContextUrl(uri) />
  
  <#if rc.requestUri == checkedUrl>
    ${what}
  <#else>
    ${other}
  </#if>
</#macro>

<#macro r path>
  <@spring.url '/resources/${version}${path}' />
</#macro>

<#macro localized value escaped=true>
  <@compress single_line=true>    
    <#if escaped>
      ${value.inLocaleOrDefault(localeService.getLocale())}
    <#else>
      ${value.inLocaleOrDefault(localeService.getLocale())?no_esc}
    </#if>
  </@compress>
</#macro>

<#macro plural code text count>
  <@compress single_line=true>  
    ${services.plural(springMacroRequestContext.getMessage(code, text), count)}
  </@compress>  
</#macro>

<#function getBean name>
  <#return rc.webApplicationContext.getBean(name) />
</#function>
