#!/bin/bash
SYSTEMSTATE=$(echo "$QUERY_STRING" | sed -n 's/^.*systemState=\([^&]*\).*$/\1/p' | sed "s/%20/ /g")
TARGET=$(echo "$QUERY_STRING" | sed -n 's/^.*target=\([^&]*\).*$/\1/p' | sed "s/%20/ /g")

if [ $SYSTEMSTATE == "Off" ]
then
  ss="0"
else
  ss="1"
fi

echo $TARGET > /var/thermronstat/target/0
echo $ss > /var/thermronstat/status/0

# after setting the above values we should redirect back to origin.
echo content-type: text/html
echo 
cat << EOF
<html>
<meta http-equiv="Refresh" content="0; url=http://66.163.100.211:8888/cgi-bin/alan/shop.cgi">
</html>
