<#import "/spring.ftl" as spring />

<#macro show path="form.*">
  <#assign pos = path?index_of(".") />
  
  <#if pos == -1>
    <#assign beanName = path />    
  <#else>
    <#assign beanName = path?substring(0, pos) />            
  </#if> 
  
  <#if rc.getErrors(beanName)?? && rc.getErrors(beanName).hasErrors()>     
    <@spring.bind path />   
      
    <div class="alert alert-danger" role="alert">
      <ul>
        <#list spring.status.errorMessages as error>
          <#if error != "">
            <li>${error}</li>
          </#if>
        </#list>
      </ul>
    </div>      

    <#assign hasErrors=true />
    <#assign hasAnywhereErrors=true />
  <#else>
    <#assign hasErrors=false />
  </#if>
</#macro>