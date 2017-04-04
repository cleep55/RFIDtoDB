--Lua script to save time/ value pair to MySQL server
luasql = require "luasql.mysql"
local value=arg[1]
local current_time=os.date("%Y-%m-%d %H:%M:%S")
env = assert (luasql.mysql())
con = assert (env:connect('CamRFID', 'CamRfidTeacher', '12teacher34', '45.55.83.75'))
res = assert (con:execute('INSERT INTO Swipe(rfid) VALUES('..value..')'))