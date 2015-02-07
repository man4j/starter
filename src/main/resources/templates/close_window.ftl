<#import "/spring.ftl" as spring/>

<!DOCTYPE html>

<html>
<head>
  <meta charset="utf-8">

  <meta http-equiv="Cache-Control" content="no-store"/>
  <meta http-equiv="Pragma" content="no-cache"/>
  <meta http-equiv="Expires" content="0"/>
  
  <script>
    window.opener.location = "<@spring.url '/' />";      
    window.close();
  </script>
</head>

<body>
</body>