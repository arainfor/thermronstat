#!/bin/bash

# Source the setup for thermronstat
. ./thermronstat_loader.sh

# We only whant whole numbers
it=${inside/.*}
#read outside < /var/thermronstat/temperature/1/f
# We only whant whole numbers
#ot=${outside/.*}

# Read outdoor temperature from WWWW
temperature=$(wget -q -O - http://www.rssweather.com/wx/us/il/mechanicsburg/wx.php | grep -o "Temperature:[[:space:]]*[0-9][0-9]*" | grep -E -o "[0-9]+")
ot=$temperature
running=$(./toOnOff.sh $relay)

#systemText=$(./toOnOff.sh $systemState)
systemText=$(./toAutoOff.sh $systemState)
if [ $systemText == "Off" ]
then
   offChecked="checked"
else
   autoChecked="checked"
fi
echo "content-type: text/html"
echo ""
echo "<?xml version=\"1.0\" encoding=\"UTF-8\"?>
<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"
 \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">


<html>
  <head>
    <title>The Shop @ 3258</title>
  </head>


<body bgcolor="#A4A4A4">
   <h2>
   <p align="center" style="font-size:60px">Thermronstat Control</p>
   </h2>
   
   <hr>
   <p align="center" style="font-size:40px">Status $running </p>
   <p align="center" style="font-size:40px">
   <input type="radio" name="systemState" value="Off" form="HVACInput" $offChecked>Off
   <br>
   <input type="radio" name="systemState" value="Auto" form="HVACInput" $autoChecked>Auto
   </p>

   <p align="center" style="font-size:40px"> 
   Target Temperature:

   <select name="target" form="HVACInput" style="font-size:40px">
        <option selected>$target
        <option value="40">40</option>
        <option value="41">41</option>
        <option value="42">42</option>
        <option value="43">43</option>
        <option value="44">44</option>
        <option value="45">45</option>
        <option value="46">46</option>
        <option value="47">47</option>
        <option value="48">48</option>
	<option value="49">49</option>
        <option value="50">50</option>
        <option value="51">51</option>
        <option value="52">52</option>
        <option value="53">53</option>
        <option value="54">54</option>
        <option value="55">55</option>
        <option value="56">56</option>
        <option value="57">57</option>
        <option value="58">58</option>
	<option value="59">59</option>
	<option value="60">60</option>
	<option value="61">61</option>
	<option value="62">62</option>
	<option value="63">63</option>
	<option value="64">64</option>
        <option value="65">65</option>
        <option value="66">66</option>
        <option value="67">67</option>
        <option value="68">68</option>
        <option value="69">69</option>
        <option value="70">70</option>
        <option value="71">71</option>
        <option value="72">72</option>
        <option value="73">73</option>
        <option value="74">74</option>
        <option value="75">75</option>
        <option value="76">76</option>
        <option value="77">77</option>
        <option value="78">78</option>
        <option value="79">79</option>
        <option value="80">80</option>
   </select>
   </p>
			    
   <form id="HVACInput" name="HVACInput" action="/cgi-bin/alan/therm_update.cgi" method="get">
   
     <p align="center">
   
     <div align="center">
       <br>
       <input type="Submit" value="Save" style="font-size:40px">
       <br> 
     </div>
   </form>

   </p>
   <hr>
   <ht>
   <h1>
   <p align="center">Indoor Temp $it </p>
   <p align="center">Outdoor Temp <a href="http://www.rssweather.com/wx/us/il/mechanicsburg/wx.php">$ot</a></p>
   </h1>
   
   <p>
   <a href="http://en.wikipedia.org/wiki/Beerware">
   <img src="http://upload.wikimedia.org/wikipedia/commons/thumb/d/d5/BeerWare_Logo.svg/170px-BeerWare_Logo.svg.png" width="80" height="80"> 
   </a>
   <p>
   <a href="/cgi-bin/alan/debug.cgi"/>
   Alan Rainford 2013
   </p>
   </body>
   </html>
"
