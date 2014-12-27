thermronstat
============

A RaspberryPi based thermostat project

This program is currently live on a single stage HEAT only appliction.  A planned H3/C2 implementation that will 
controll my GeoThermal is in the works.

Geo Notes:
DX18x20 Inputs - 
Indoor Temp
Plenum Temp
Intake Temp
Outdoor Temp

Relay Outputs -
G - Fan Control
Y1 - Stage 1
Y2 - Stage 2
W - Aux Heat
O - Reversing valve.

Signal Table -
G = Fan Only
G + Y1 = Stage 1 Heat
G + Y1 + Y2 = Stage 2 Heat
G + Y1 + Y2 + W = Stage 3 Heat
G + W = Emergency Heat
G + Y1 + O = Stage 1 Cool
G + Y1 + Y2 + O = Stage 2 Cool

User Inputs:
Target Temperature

Programatic Inputs:
Time in current state
Temperature rise / fall % of target while in current state
Efficency as defined by difference in return and plenum




