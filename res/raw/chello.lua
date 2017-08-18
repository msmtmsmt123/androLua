local welcomeView = "com.chaozh.iReaderFree/com.chaozh.iReader.ui.activity.WelcomeActivity"
local selectView = "com.chaozh.iReaderFree/com.chaozh.iReader.ui.activity.SelectBook.SelectBookActivity"
local bookshelfView = "com.chaozh.iReaderFree/com.zhangyue.iReader.bookshelf.ui.ActivityBookShelf"
local bookdetailView = "com.chaozh.iReaderFree/com.zhangyue.iReader.read.ui.Activity_BookBrowser_TXT"
local cartoonView = "com.chaozh.iReaderFree/com.zhangyue.iReader.cartoon.ui.ActivityCartoon"
local cartoon_view = "com.chaozh.iReaderFree/com.zhangyue.iReader.cartoon.ui.ActivityCartoon"
-- 返回
function back()
  rootShell.simulateKey(4)
  random_sleep(4, 5)
end

function is_cartoonview()
  return is_in_view(cartoon_view)
end

function is_reading_view()
  return is_in_view(bookdetailView)
end

function is_book_self_view()
  return is_in_view(bookshelfView)
end

function is_select_view()
  return is_in_view(selectView)
end
--性别选择
function gender_select()
  if(is_select_view()) then
    local gender = random(1,2)
    if gender==1 then
      rootShell.simulateTap(171,500)
    else
      rootShell.simulateTap(524, 500)
    end
    random_sleep(1,2)
    --Log.i("LUA_TEST","gender:"+gender);
    rootShell.simulateTap(400, 1206)
    local hobby = random(0,3) 
    random_sleep(3,4)
    local y = (hobby*180)+350
    random_sleep(1,2)
    rootShell.simulateTap(400, y)
    random_sleep(1,2)
    rootShell.simulateTap(400, 1206)
    random_sleep(7,8)
  else
  rootShell.startActivity("-n "..welcomeView)
  end

end
--今日签到
function sign()
  local is_sign = random(1,10)
  if is_sign <=3 then
    rootShell.simulateTap(386,426)
    random_sleep(10,11)
    rootShell.simulateTap(345,361)--签到
    random_sleep(3,4)
    local x = (random(1,3)-1)*200+169
    local y = (random(1,2)-1)*210+570
    rootShell.simulateTap(x, y)
    random_sleep(3,4)
    rootShell.simulateTap(350,1112)
    back()
    random_sleep(3,4)
  end
end
--首页随机读书
function random_read_main()
  local x = (random(1,3)-1)*210+136
  local y = (random(1,2)-1)*320+654
  rootShell.simulateTap(x, y)
  random_sleep(9,10)
  local slids = random(50,60)
  if is_reading_view() then
    for k = 1,slids do
      read_book()
    end
  end
  back()
  random_sleep(1,2)
end
--跳转到书城，随机阅读
function random_read_book_market()
  rootShell.simulateTap(246,1248)
  random_sleep(5,6)
  h_slide_random(1,3)
  random_sleep(5,6)
  v_slide_random(1,3)
  local x = (random(1,3)-1)*210+136
  local y = (random(1,3)-1)*320+654
  rootShell.simulateTap(x, y)
  random_sleep(3,4)
  rootShell.simulateTap(147, 276)
  random_sleep(8,9)
  rootShell.simulateTap(147, 276)
  random_sleep(9,10)
  --Log.i("LUA_TEST","is_cartoon_view():"..is_cartoon_view())
  if is_cartoonview() then
    readCartoon()
  --elseif is_reading_view() then
  --  readBook()
  else
    back()
    random_read_book_market()
  end
end
function readBook()
  local slids = random(5,6)
  for k = 1,slids do
    if is_reading_view() then
      read_book()
    else
      break
    end
  end
  back()
  rootShell.simulateTap(374, 779)
  rootShell.startActivity("-n "..welcomeView)
  random_sleep(2,3)
  back()
end


--看漫画
function readCartoon()
  local slids = random(5,6)
  for k = 1,slids do
    if is_reading_view() then
      read_cartoon()
    else
      break
    end
  end
  back()
  rootShell.simulateTap(374, 779)
  back()
  rootShell.startActivity("-n "..welcomeView)
  random_sleep(2,3)
  back()
end
-- 随机垂直滑动几次
function v_slide_random(min, max)
  local slids = random(min, max)
  if slids == 0 then
    return 0
  end
  for k = 1,slids do
    local y = random(200, 600)
    rootShell.simulateSwipe(100, 1100, 100, y, 0)
    random_sleep(3, 4)
  end
  return slids
end
-- 随机滑动几次
function h_slide_random(min, max)
  local slids = random(min, max)
  if slids == 0 then
    return 0
  end
  for k = 1,slids do
    rootShell.simulateSwipe(700, 800, 100, 800, 0)
    random_sleep(7, 10)
  end
  return slids
end

--翻页读书
function read_book()
  rootShell.simulateSwipe(700, 558, 25, 558, 0)
  random_sleep(1,3)
end

function read_cartoon()
  rootShell.simulateSwipe(100, 1110, 100, 100, 0)
  random_sleep(1,3)
end

function go_main_view()
  rootShell.startActivity("-n "..welcomeView)
  wait_view(selectView)
  random_sleep(6,7)
  gender_select()
  --sign()
  --random_read_main()
  random_read_book_market()
  random_read_book_market()
  random_read_book_market()
end
go_main_view()