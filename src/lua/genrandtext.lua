-- 字符串随机模式枚举定义
RDModle = {
	RSM_Capital = 1, -- 纯大写字母
	RSM_Letter = 2,	-- 纯小写字母
	RSM_Cap_Let = 3, -- 大小写字母
	RSM_Number = 4, -- 纯数字
	RSM_Cap_Num = 5, -- 大写字母和数字
	RSM_Let_Num = 6, -- 小写字母和数字
	RSM_All = 7 -- 全部类型，包含数字小写字母和大写字母
}

-- 产生指定长度的随机整数
function genRandNum(len)
	local rt = ""
	for i = 1, len do
		if i == 1 then
			rt = rt..random(1, 9)
		else
			rt = rt..random(0, 9)
		end
	end
	return rt
end

-- 产生指定长度的随机小写字母字符串
function genRandLetter(len)
	local rt = ""
	for i = 1, len do
		rt = rt..string.char(random(97, 122))
	end
	return rt
end

-- 产生指定长度的随机大写字母字符串
function genRandCapital(len)
	local rt = ""
	for i = 1, len do
		rt = rt..string.char(random(65, 90))
	end
	return rt
end

-- 产生指定长度的随机文本
function genRandString(len, modle)
	local BC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
	local SC = "abcdefghijklmnopqrstuvwxyz"
	local NO = "0123456789"
	local maxLen = 0
	local templete = ""

	if modle == nil or modle == RDModle.RSM_Capital then -- 如果没有填写模式参数，默认生成纯大写字符串
		templete = BC
		maxLen = 26
	elseif modle == RDModle.RSM_Letter then
		templete = SC
		maxLen = 26
	elseif modle == RDModle.RSM_Cap_Let then
		templete = SC..BC
		maxLen = 52
	elseif modle == RDModle.RSM_Number then
		templete = NO
		maxLen = 10
	elseif modle == RDModle.RSM_Cap_Num then
		templete = NO..BC
		maxLen = 36
	elseif modle == RDModle.RSM_Let_Num then
		templete = NO..SC
		maxLen = 36
	elseif modle == RDModle.RSM_All then
		templete = NO..SC..BC
		maxLen = 62
	else -- 模式参数不正确，默认生成纯小写字符串
		templete = SC
		maxLen = 26
	end

	-- 产生字符串
	local srt = {}
	for i = 1, len do
		local index = random(1, maxLen)
		srt[i] = string.sub(templete, index, index)
	end
	return table.concat(srt, "")
end

---------------------------------------------------------------

local firstName = {"赵","钱","孙","李","周","吴","郑","王","冯","陈","褚","卫","蒋","沈","韩","杨","朱","秦","尤","许",  
                  "何","吕","施","张","孔","曹","严","华","金","魏","陶","姜","戚","谢","邹","喻","柏","水","窦","章","云","苏","潘","葛","奚","范","彭","郎",  
                  "鲁","韦","昌","马","苗","凤","花","方","俞","任","袁","柳","酆","鲍","史","唐","费","廉","岑","薛","雷","贺","倪","汤","滕","殷",  
                  "罗","毕","郝","邬","安","常","乐","于","时","傅","皮","卞","齐","康","伍","余","元","卜","顾","孟","平","黄","和",  
                  "穆","萧","尹","姚","邵","湛","汪","祁","毛","禹","狄","米","贝","明","臧","计","伏","成","戴","谈","宋","茅","庞","熊","纪","舒",  
                  "屈","项","祝","董","梁","杜","阮","蓝","闵","席","季","麻","强","贾","路","娄","危","江","童","颜","郭","梅","盛","林","刁","钟",  
                  "徐","邱","骆","高","夏","蔡","田","樊","胡","凌","霍","虞","万","支","柯","昝","管","卢","莫","经","房","裘","缪","干","解","应",  
                  "宗","丁","宣","贲","邓","郁","单","杭","洪","包","诸","左","石","崔","吉","钮","龚","程","嵇","邢","滑","裴","陆","荣","翁","荀",  
                  "羊","于","惠","甄","曲","家","封","芮","羿","储","靳","汲","邴","糜","松","井","段","富","巫","乌","焦","巴","弓","牧","隗","山",  
                  "谷","车","侯","宓","蓬","全","郗","班","仰","秋","仲","伊","宫","宁","仇","栾","暴","甘","钭","厉","戎","祖","武","符","刘","景",  
                  "詹","束","龙","叶","幸","司","韶","郜","黎","蓟","溥","印","宿","白","怀","蒲","邰","从","鄂","索","咸","籍","赖","卓","蔺","屠",  
                  "蒙","池","乔","阴","郁","胥","能","苍","双","闻","莘","党","翟","谭","贡","劳","逄","姬","申","扶","堵","冉","宰","郦","雍","却",  
                  "璩","桑","桂","濮","牛","寿","通","边","扈","燕","冀","浦","尚","农","温","别","庄","晏","柴","瞿","阎","充","慕","连","茹","习",  
                  "宦","艾","鱼","容","向","古","易","慎","戈","廖","庾","终","暨","居","衡","步","都","耿","满","弘","匡","国","文","寇","广","禄",  
                  "阙","东","欧","殳","沃","利","蔚","越","夔","隆","师","巩","厍","聂","晁","勾","敖","融","冷","訾","辛","阚","那","简","饶","空",  
                  "曾","毋","沙","乜","养","鞠","须","丰","巢","关","蒯","相","查","后","荆","红","游","郏","竺","权","逯","盖","益","桓","公","仉",  
                  "督","岳","帅","缑","亢","况","郈","有","琴","归","海","晋","楚","闫","法","汝","鄢","涂","钦","商","牟","佘","佴","伯","赏","墨",  
                  "哈","谯","篁","年","爱","阳","佟","言","福","南","火","铁","迟","漆","官","冼","真","展","繁","檀","祭","密","敬","揭","舜","楼",  
                  "疏","冒","浑","挚","胶","随","高","皋","原","种","练","弥","仓","眭","蹇","覃","阿","门","恽","来","綦","召","仪","风","介","巨",  
                  "木","京","狐","郇","虎","枚","抗","达","杞","苌","折","麦","庆","过","竹","端","鲜","皇","亓","老","是","秘","畅","邝","还","宾",  
                  "闾","辜","纵","侴","万俟","司马","上官","欧阳","夏侯","诸葛","闻人","东方","赫连","皇甫","羊舌","尉迟","公羊","澹台","公冶","宗正",  
                  "濮阳","淳于","单于","太叔","申屠","公孙","仲孙","轩辕","令狐","钟离","宇文","长孙","慕容","鲜于","闾丘","司徒","司空","兀官","司寇",  
                  "南门","呼延","子车","颛孙","端木","巫马","公西","漆雕","车正","壤驷","公良","拓跋","夹谷","宰父","谷梁","段干","百里","东郭","微生",  
                  "梁丘","左丘","东门","西门","南宫","第五","公仪","公乘","太史","仲长","叔孙","屈突","尔朱","东乡","相里","胡母","司城","张廖","雍门",  
                  "毋丘","贺兰","綦毋","屋庐","独孤","南郭","北宫","王孙"}
local girl = "秀娟英华慧巧美娜静淑惠珠翠雅芝玉萍红娥玲芬芳燕彩春菊兰凤洁梅琳素云莲真环雪荣爱妹霞香月莺媛艳瑞凡佳嘉琼勤珍贞莉桂娣叶璧璐娅琦晶妍茜秋珊莎锦黛青倩婷姣婉娴瑾颖露瑶怡婵雁蓓纨仪荷丹蓉眉君琴蕊薇菁梦岚苑婕馨瑗琰韵融园艺咏卿聪澜纯毓悦昭冰爽琬茗羽希宁欣飘育滢馥筠柔竹霭凝晓欢霄枫芸菲寒伊亚宜可姬舒影荔枝思丽"
local boy = "伟刚勇毅俊峰强军平保东文辉力明永健世广志义兴良海山仁波宁贵福生龙元全国胜学祥才发武新利清飞彬富顺信子杰涛昌成康星光天达安岩中茂进林有坚和彪博诚先敬震振壮会思群豪心邦承乐绍功松善厚庆磊民友裕河哲江超浩亮政谦亨奇固之轮翰朗伯宏言若鸣朋斌梁栋维启克伦翔旭鹏泽晨辰士以建家致树炎德行时泰盛雄琛钧冠策腾楠榕风航弘"
local emailSuffix = {"@gmail.com", "@yahoo.com", "@msn.com", "@hotmail.com", "@aol.com", "@ask.com", "@live.com", "@qq.com", "@0355.net", "@163.com", "@163.net", "@263.net", "@3721.net", "@yeah.net", "@googlemail.com", "@126.com", "@sina.com", "@sohu.com", "@yahoo.com.cn"}
local telSuffix = {"134", "135", "136", "137", "138", "139", "150", "151", "152", "157", "158", "159", "130", "131", "132", "155", "156", "133", "153"}

-- 随机生成邮箱
function genRandEmail(min, max)
	local len = random(min, max)
	local name = genRandString(len, RDModle.RSM_Let_Num)
	return name..emailSuffix[random(#emailSuffix)]
end

-- 随机生成电话
function genRandTel()
	local sec = ""
	for i = 1, 8 do
		sec = sec..random(0, 9)
	end
	return telSuffix[random(#telSuffix)]..sec
end

-- 随机生成中文名
function genRandChineseName()
	local sex = random(0, 1)
	local index = random(#firstName)
	local rt = firstName[index]
	local str = boy
	local _, len = string.gsub(boy, "[^\128-\193]", "")
	if sex == 0 then
		str = girl
		_, len = string.gsub(girl, "[^\128-\193]", "")
	end

	local double = random(1, 2)
	for i = 1, double do
		index = random(len)
		local start = (index - 1) * 3 + 1
		rt = rt..string.sub(str, start, start + 2)
	end
	return rt
end

-- 生成身份证号
function genRandId()
	-- 随机生成省、自治区、直辖市代码 1-2
	local provinces = {"11", "12", "13", "14", "15", "21", "22", "23",
		"31", "32", "33", "34", "35", "36", "37", "41", "42", "43",
		"44", "45", "46", "50", "51", "52", "53", "54", "61", "62",
		"63", "64", "65", "71", "81", "82"}
	local province = randomOne(provinces)
	-- 随机生成地级市、盟、自治州代码 3-4
	local city = randomCityCode(18)
	-- 随机生成县、县级市、区代码 5-6
	local county = randomCityCode(28)
	-- 随机生成出生年月 7-14
	local birth = genRandBirth(20, 50)
	-- 随机生成顺序号 15-17(随机性别)
	local no = random(899) + 100
	-- 随机生成校验码 18
	local checks = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
		"X"}
	local check = randomOne(checks)
	-- 拼接身份证号码
	return province..city..county..birth..no..check
end
function genRandBirth(minAge, maxAge)
	local randDay = 365 * minAge + random(365 * (maxAge - minAge))
	return os.date("%Y%m%d", os.time() - randDay * 24 * 60 * 60)
end
function randomOne(s)
	return s[random(#s)]
end
function randomCityCode(max)
	local i = random(max) + 1
	if i > 9 then
		return i
	end
	return "0"..i
end


