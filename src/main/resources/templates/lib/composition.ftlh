<#macro composition templateName>
  <#nested />
  <#include templateName />
</#macro>

<#macro insert value>
  <#if value?markup_string?ends_with(".ftlh")>
    <#include value />
  <#else>
    ${value?no_esc}
  </#if>
</#macro>

<#macro ifndef libName>
  <#if !libs??>
    <#global libs = {} />    
  </#if>
  
  <#if !libs[libName]??>
    <#global libs = libs + {libName : true} />
    
    <#nested />   
  </#if>  
</#macro>

<#macro addScript>
  <#assign script>
    <#nested />
  </#assign>
  
  <#if !scripts??>
    <#global scripts = [] />
  </#if>
    
  <#global scripts = scripts + [script] />
</#macro>

<#macro insertScripts>
  <#if scripts??>
    <#list scripts as script>
      ${script?no_esc}
    </#list>    
  </#if>
</#macro>