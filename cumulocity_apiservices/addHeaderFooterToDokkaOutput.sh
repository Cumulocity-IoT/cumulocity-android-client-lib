#!/bin/sh

head=`cat cumulocity_apiservices/head.html`
header=`cat cumulocity_apiservices/header.html`
footer=`cat cumulocity_apiservices/footer.html`

cd cumulocity_apiservices/build/dokka

find . -name "*.html" -type f -exec sed -i "s|\<\/HEAD\>|$head\<\/HEAD\>|g" "{}" \; 
find . -name "*.html" -type f -exec sed -i "s|\<BODY\>|\<BODY\>$header|g" {} \; 
find . -name "*.html" -type f -exec sed -i "s|\<\/BODY\>|$footer\<\/BODY\>|g" {} \;