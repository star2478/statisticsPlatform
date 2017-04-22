var hostUri = 'http://'+window.location.host;
var getVideoInfoByPageUrl = hostUri + "/getVideoInfoByPage";
var getChannelListUrl = hostUri + "/getChannelList";
var getVideoDailyCountUrl = hostUri + "/getVideoDailyCount";
var chartHtmlUrl = hostUri + "/chart.html";
var eachPageNum = 30;

var getStatByKeyAndTriggerAndBaseInfoUrl = hostUri + "/fsg/strategy/getStatByKeyAndTriggerAndBaseInfo";
var getStatByKeyUrl = hostUri + "/fsg/strategy/getStatByKey";
var getStatByKeyAndFieldUrl = hostUri + "/fsg/strategy/getStatByKeyAndField";
var getAllKnowledeNameUrl = hostUri + "/fsg/strategy/getAllPersistentedStrategy";
var getAllFieldByKeyUrl = hostUri + "/fsg/strategy/getAllFieldByKey";
var getAllTriggersByKeyUrl = hostUri + "/fsg/strategy/getAllTriggersByKey";
var getAllBaseInfoByKeyUrl = hostUri + "/fsg/strategy/getAllBaseInfoByKey";

function splitTimeToSeconds(time) {
    var year = time.split('-')[0];
    var month = time.split('-')[1] - 1;
    var day = time.split('-')[2].split(' ')[0];
    var hour = time.split(' ')[1].split(':')[0];
    var minute = time.split(' ')[1].split(':')[1];
    var targetTime = Date.UTC(year, month, day, hour, minute, 0);
    return targetTime;
}

function timeFormat(inputDate) {
    var year = inputDate.getUTCFullYear();
    var month = inputDate.getUTCMonth() + 1;
    var day = inputDate.getUTCDate();
    var hour = inputDate.getUTCHours();
    var minute = inputDate.getUTCMinutes();
    if (minute == 0){
    	minute = "00";
    }
    var formatDate = year + "/" + month + "/" + day + " " + hour + ":" + minute;
    return formatDate;
}

function htmlEntityEncode(str) {
	str = str.replace(/&lt;/g,"<");
	str = str.replace(/&gt;/g,">");
//	str = str.replace(/"/g,"\"");
//	str = str.replace(/&/g,"&");
//	str = str.replace(/ /g," ");
	return str;
}

// 获取url中的参数
function getUrlParam(name) {
    var reg = new RegExp("(^|&&&)" + name + "=([^&&&]*)(&&&|$)"); //构造一个含有目标参数的正则表达式对象
    var r = window.location.search.substr(1).match(reg);  //匹配目标参数
    if (r != null) {
    	return decodeURI(r[2]); 
    }
    return null; //返回参数值
}
function GetRequest() { 
	var url = location.search; //获取url中"?"符后的字串 
	var theRequest = new Object(); 
	if (url.indexOf("?") != -1) { 
		var str = url.substr(1); 
		var strs = str.split("&&&"); 
		for(var i = 0; i < strs.length; i ++) { 
			theRequest[strs[i].split("=")[0]]=decodeURI(strs[i].split("=").slice(1).join("=")); 
		} 
	} 
	return theRequest; 
} 