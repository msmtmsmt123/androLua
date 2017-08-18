local mainView = "cn.goapk.market/com.anzhi.market.ui.MainActivity" -- 主页
local appDetailView = "cn.goapk.market/com.anzhi.market.ui.AppDetailsActivity" -- 应用详情
local specialNewView = "cn.goapk.market/com.anzhi.market.ui.SpecialSubDetailNewActivity" -- 专题新闻
local rootDialogView = "cn.goapk.market/com.anzhi.market.ui.MarketRootTipDialog"
local autoInstallDialogView = "cn.goapk.market/com.anzhi.market.ui.AnzhiAutoInstallTipDialog"
local recommendView = "cn.goapk.market/com.anzhi.market.ui.RecommendInstallActivity"
local adView= "cn.goapk.market/com.anzhi.market.ui.PopAdsActivity"
local installView = "com.android.packageinstaller/com.android.packageinstaller.PackageInstallerActivity"
local openView = "com.android.packageinstaller/com.android.packageinstaller.permission.PackageInstallerPermsEditor"

function is_app_detail_view()
  return is_in_view(appDetailView)
end
function is_special_new_view()
  return is_in_view(specialNewView)
end
function is_dialog_view()
  return not ScreenAssistant.cmpColor({{"209", "1225", "17D4FA"}}, 0.9)
end

function is_root_tip_dialog()
  return is_in_view(rootDialogView)
end
function is_auto_install_tip_dialog()
  return is_in_view(autoInstallDialogView)
end

function is_recommend_view()
  return is_in_view(recommendView)
end

function is_install_view()
  return is_in_view(installView)
end
function is_ad_view()
  return is_in_view(adView)
end
-- 返回
function back()
  rootShell.simulateKey(4)
  random_sleep(4, 5)
end

-- 随机垂直滑动几次
function v_slide_random(min, max)
  local slids = random(min, max)
  if slids == 0 then
    return 0
  end
  for k = 1,slids do
    local y = random(300, 600)
    rootShell.simulateSwipe(100, 1100, 100, y, 0)
    random_sleep(3, 4)
  end
  return slids
end

-- 随机左滑动几次
function h_slide_random(min, max)
  local slids = random(min, max)
  if slids == 0 then
    return 0
  end
  for k = 1,slids do
    rootShell.simulateSwipe(555, 1111, 25, 1111, 0)
    random_sleep(3, 4)
  end
  return slids
end
-- 随机左滑动几次
function h_slide_right_random(min, max)
  local slids = random(min, max)
  if slids == 0 then
    return 0
  end
  for k = 1,slids do
    rootShell.simulateSwipe(25, 1111, 555, 1111, 0)
    random_sleep(3, 4)
  end
  return slids
end


-- 跳到主页
function goto_main_view()
  rootShell.startActivity("-n "..mainView)
  wait_view(mainView)
  random_sleep(22, 25)
  dialog_opt()
  
  --v_slide_random(1, 1)
end

function search()
  local words = {"QQ","京东","微信","WIFI万能钥匙","爱奇艺","王者荣耀","开心消消乐","美柚","今日头条","微博","携程","修图","美颜","相机","小说","游戏",
      "音乐","直播"}
  v_slide_random(1, 1)    
  rootShell.simulateTap(180,100)
  random_sleep(1, 2)
  rootShell.simulateTap(180,100)
  random_sleep(1, 2)
  rootShell.simulateTap(360,100)
  local index = random(1,18)
  local word = words[index]
  rootShell.simulateTypeText(word)
  rootShell.simulateTap(656,100)
  random_sleep(7, 8)
  if index <=11 then
    --if random(1,2)==1 then
     -- rootShell.simulateTap(650,226) --直接下载
    --else
      rootShell.simulateTap(360,226)  --下载详情界面
      go_download_detail_page(0)
   -- end
  else
    v_slide_random(1,2)
    rootShell.simulateTap(360,226)
     go_download_detail_page(0)
  end 
  install_opt()
  back()
  install_opt()
  back()
  install_opt()
  back()
  install_opt()
  back()
  install_opt()
  back()
  install_opt()
end
--下载详情页
function go_download_detail_page(index)
  random_sleep(10,15)
  rootShell.simulateTap(360, 1230)
  random_sleep(4, 5)
  dialog_opt()
  
  browser_app2()
  
end
--dialog 操作
function dialog_opt()
  if is_root_tip_dialog() or is_auto_install_tip_dialog() or is_recommend_view() or is_ad_view() then
    back()
  end
  sleep(1)
  if is_root_tip_dialog() or is_auto_install_tip_dialog() or is_recommend_view() or is_ad_view() then
    back()
  end
end
--如果是安装应用界面 操作安装
function install_opt()
  if is_install_view() then
    rootShell.simulateTap(542,1200)
    random_sleep(1, 2)
    dialog_opt()
    rootShell.simulateTap(542,1200)
    random_sleep(1, 2)
    dialog_opt()
    rootShell.simulateTap(542,1200)
    random_sleep(1, 2)
  end
end


-- 下载
function download()
  rootShell.simulateTap(360, 1230)
  random_sleep(4, 5)
  if is_dialog_view() and random() > 0.39 then
    rootShell.simulateTap(325, 808)
    random_sleep(4, 5)
  end
  rootShell.simulateTap(360, 1230)
end

-- 操作应用
function operate_app()
  if is_app_detail_view() then
    if h_slide_random(0, 3) ~= 0 then
      random_sleep(4, 8)
    end
    v_slide_random(0, 3)
    download()
  else
    v_slide_random(0, 5)
  end
end
--浏览应用详情
function browser_app2()
  if is_app_detail_view() then
    v_slide_random(0, 3)
    random_sleep(7,8)
    dialog_opt()
    if is_app_detail_view() then
      h_slide_random(1,1)
      random_sleep(8,10)
      v_slide_random(0, 3)
      random_sleep(1,2)
    end
    dialog_opt()
    if is_app_detail_view() then
      h_slide_random(1,1)
      random_sleep(8,10)
      v_slide_random(0, 3)
      random_sleep(1,2)
    end
  end

end
-- 浏览应用
function browse_app(max_times)
  Log.i("RootShell","browse_app")
  local times = random(1, max_times)
  for i=1,times do
    local x = 200
    if random() > 0.5 then
      x = 500
    end
    local y = random(260, 1125)
    rootShell.simulateTap(x, y)
    random_sleep(9, 13)
    operate_app()
    back()back()
    v_slide_random(0, 3)
  end
end

-- 分类
function browse_category()
  v_slide_random(0, 3)
  local y = random(260, 1130)
  rootShell.simulateTap(126, y)
  random_sleep(7, 11)
  browse_app(2)
  back()
end

-- 其它栏目
function other_channel(index, total_channel)
  local i = h_slide_random(1, total_channel)
  random_sleep(9, 13)
  if (index == 2 and i == 2) or (index == 3 and i == 3) then
    browse_category()
  else
    browse_app(2)
  end
end
-- 浏览应用 并可能下载
function browser_apps(times,total_channel)
  local time = random(1,times)
  for i =0,time do
    h_slide_random(0,total_channel)
    random_sleep(5,8)
    v_slide_random(0,3)
    local y = random(260, 1125)
    rootShell.simulateTap(360, y)
    random_sleep(9, 13)
    operate_app()
    back()back()
    h_slide_right_random(total_channel,total_channel)
  end
end

function browser_news(times,total_channel)
  local time = random(1,times)
  for i =0,time do
    h_slide_random(0,total_channel)
    random_sleep(5,8)
    v_slide_random(0,3)
    local y = random(260, 1125)
    v_slide_random(0,4)
    back()
    back()
    h_slide_right_random(total_channel,total_channel)
  end

end

-- 操作菜单
function operate_menu(index, total_channel)
  local x = 70 * (index * 2 - 1)
  rootShell.simulateTap(x, 1230)
  random_sleep(9, 15)
  if index <=3 then
    browser_apps(3,total_channel)
  else
    browser_news(3,total_channel)
  end
  
  other_channel(index, total_channel)
end

-- 主要
function main_run()
  goto_main_view()
  
 --[[ search()
  Log.i("RootShell","search:::")
  random_sleep(5, 8)
  rootShell.startActivity("-n "..mainView)
  wait_view(mainView)
  random_sleep(10,15)
  dialog_opt()]]--
  
  for i=1,4 do
    local tap = radom(1,4)
    if tap == 3 then
      total_channel = 4
    elseif tap==4 then
      total_channel = 5
    else
      total_channel = 3
    end
    operate_menu(tap, total_channel)
  end
end

main_run()
