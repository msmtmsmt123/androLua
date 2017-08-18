-- 文本输入
rootShell.simulateTypeText("17x21tf79")

-- 读取多个像素点上的颜色值
local colorArray = ScreenAssistant.getColors({{50,50.6},{100,100.3},{550,550.6},{200.5,200.9}})
local colors = luajava.astable(colorArray)
for key, value in pairs(colors) do
	Log.i("Luaprint", "come here...."..key.." "..value);
end

-- 找图
import "android.graphics.Point"
local points = ScreenAssistant.findImage({0, 0, 200, 1200}, "pgc_icon_item_pic_mask_play.png", nil, 0.9, true)

local pointTable = luajava.astable(points)
if next(pointTable) ~= nil then
	local point = pointTable[1]
	Log.i("Luaprint", "come here...."..point.x..", "..point.y);
end

-- 区域找色
import "android.graphics.Point"
local point = ScreenAssistant.findColor({196, 257, 346, 444}, {{"C0E16B"}}, 0)
Log.i("Luaprint", "come here...."..point.x..", "..point.y);

-- 多点找色
import "android.graphics.Point"
local color = "BEDB67"
local point = ScreenAssistant.findMultiColor({45, 225, 166, 1165}, "FF0000", "-111|18|FF0000,-73|22|FFFFFF,-31|30|FFFFFF,-8|26|FF0000,-66|53|FFFFFF,-56|51|FF0000", 0.9)
Log.i("Luaprint", "come here...."..point.x..", "..point.y);

-- 多点比色
local find = ScreenAssistant.cmpColor({{"227", "417", "FFFFFF", "101010"}, {"440", "200", "FFFFFF"}}, 0.9)
if find then
	Log.i("Luaprint", "come here....")
end

-- 找字
import "android.graphics.Point"
local textPoints = ScreenAssistant.findText({200, 400, 350, 450}, {"0FFE06007100034000280003000060000C000340004600307EF800F800", "0", "52", "19"},
{"FFFFFF", "101010"}, 0.9, true)
local pointTable = luajava.astable(textPoints)
if next(pointTable) ~= nil then
	for k, textPoint in pairs(pointTable) do
		Log.i("Luaprint", "come here...."..textPoint.getPoint().x..", "..textPoint.getPoint().y);
	end
end

-- 网络请求
import "http"

local url = "http://zhushou.dot7.cn/?mod=mv1&action=loginPhoneToken"
local body,_,code = http.get(url)

if code == 200 then
	Log.i("Luaprint", "result: "..body)
else
	Log.i("Luaprint", "error: "..code)	
end

sleep(10)

-- 账号操作
-- wifistate(0流量、1wifi)
-- orderId
-- planId
-- imei
-- realImei
-- deviceId
-- deviceCode
-- params
local result = get_account_info(1,0,0)
local result = getPayAccount(2)
local result = pay_detail(1,1,1,5)
local result = register_account(1,1)
if result ~= nil then
	Log.i("Luaprint", "result...>> "..result["account"])
end

-- 测试某个方法
if is_dialog_view() then
	Log.i("Luaprint", "come here....111")
else
	Log.i("Luaprint", "come here....222")
end
math.modf(15 / times)

ScreenAssistant.cmpColor({{"352", "585", "5EC925"}}, 0.9)


