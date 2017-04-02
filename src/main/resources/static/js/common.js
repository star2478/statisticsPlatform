var hostUri = 'http://'+window.location.host;
var getVideoInfoByPageUrl = hostUri + "/getVideoInfoByPage";
var eachPageNum = 2;

var getStatByKeyAndTriggerAndBaseInfoUrl = hostUri + "/fsg/strategy/getStatByKeyAndTriggerAndBaseInfo";
var getStatByKeyUrl = hostUri + "/fsg/strategy/getStatByKey";
var getStatByKeyAndFieldUrl = hostUri + "/fsg/strategy/getStatByKeyAndField";
var getAllKnowledeNameUrl = hostUri + "/fsg/strategy/getAllPersistentedStrategy";
var getAllFieldByKeyUrl = hostUri + "/fsg/strategy/getAllFieldByKey";
var getAllTriggersByKeyUrl = hostUri + "/fsg/strategy/getAllTriggersByKey";
var getAllBaseInfoByKeyUrl = hostUri + "/fsg/strategy/getAllBaseInfoByKey";
var chartHtmlUrl = hostUri + "/stat/strategy/chart.html";
var homeHtmlUrl = hostUri + "/stat/strategy/home.html";

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