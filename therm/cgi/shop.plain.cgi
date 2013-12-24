#!/bin/bash
indoorSensor=/sys/bus/w1/devices/28-0000054d6b25/w1_slave
outdoorSensor=/sys/bus/w1/devices/28-0000057b7552/w1_slave

indoorOutput=/var/thermronstat/temperature/0/f
outdoorOutput=/var/thermronstat/temperature/1/f

./readTempSensor.sh $indoorSensor $indoorOutput > /dev/null
./readTempSensor.sh $outdoorSensor $outdoorOutput > /dev/null

read inside < /var/thermronstat/temperature/0/f
# We only whant whole numbers
it=${inside/.*}
read outside < /var/thermronstat/temperature/1/f
# We only whant whole numbers
ot=${outside/.*}

read target < /var/thermronstat/target/0

read systemState < /var/thermronstat/status/0
read relay < /var/thermronstat/relay/0
running=$(./toOnOff.sh $relay)

#systemText=$(./toOnOff.sh $systemState)
systemText=$(./toAutoOff.sh $systemState)

echo "content-type: text/html"
echo ""
echo "<?xml version=\"1.0\" encoding=\"UTF-8\"?>
<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"
 \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">


<html>
  <head>
    <title>The Shop Thermronstat</title>
  </head>


<body><h2><p align="center">Welcome to the Shop</h2></p>
<p align="center">Indoor Temp $it </p>
<p align="center">Outdoor Temp $ot </p>
<p align="center">Unit running? $running </p>

<p align="center">
<select name="systemState" form="HVACInput">
	<option systemState="Off">Off</option>
	<option systemState="Auto">Auto</option>
	<option selected>$systemText
</select>
</p>

<p align="center"> 
Select Temperature:

<select name="target" form="HVACInput">
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
HVAC

  <br>
  <div align="center">
  <br>
  <input type="Submit" value='Submit HVAC Values'>
  <br>
  </div>
</form>

</p>
</body></html>"
