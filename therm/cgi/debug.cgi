#!/bin/sh
. ./thermronstat_loader.sh

echo "content-type: text/html"
echo ""
echo "<?xml version=\"1.0\" encoding=\"UTF-8\"?>
<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"
 \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">


<html>
  <head>
    <title>Thermronstat Debug</title>
  </head>


<body>
   <h2>
   <p>Debug Values</p>
   </h2>
   
   <hr>
   <h1>
   <p>Target  Temp $target</p>
   <p>Indoor  Temp $inside</p>
   <p>Outdoor Temp $outside</p>
   <p>Status       $systemState</p>
   <p>Relay        $relay </p>
   </h1>
   <hr>
   </body>
   </html>
"
   

