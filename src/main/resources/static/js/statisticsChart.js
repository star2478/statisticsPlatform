$(document).ready(function () {

	$(document).on('click','#getStatBtn',function(){
		var strategyName = $('#knowledgeStrategyName').val();
        var appID = $('#appID').val();
        var os = $('#os').val();
        var appVersion = $('#appVersion').val();
        var triggerName = $('#triggerStrategyName').val();
        var data = {};
        data['key'] = strategyName;
        data['beginTime'] = $('#beginTime').val();
        data['endTime'] = $('#endTime').val();
        if(appID != "所有appID") {
        	data['appID'] = appID;
        }
        if(os != "所有os") {
        	data['os'] = os;
        }
        if(appVersion != "所有版本号") {
        	data['appVersion'] = appVersion;
        }
        if(triggerName != "所有trigger") {
        	data['triggerName'] = triggerName;
        }
        
        var chartDataList = [];
	    $.ajax({
	        type:'POST',
	        url:getStatByKeyAndTriggerAndBaseInfoUrl,
	        data: data,
	        dataType:'json',
	        success: function (data) {
                var responseObj = eval(data);
                if (responseObj.code == 0) {
                    var list= responseObj['data']['fieldList'];
                    for (var i = 0; i < list.length; i++) {
       
                        var keyAndField = list[i]['field'];
       
                        var countList = list[i]['countList'];
                        var dataList = new Array();
                        for (var  j= 0; j < countList.length; j++) {
                            var value = countList[j]['value'];
                            var time = countList[j]['time'];
                            dataList.push([splitTimeToSeconds(time), value]);
                        }
                        var jsonObj = {
                            name: keyAndField,
                            data: dataList
                        }
                        chartDataList.push(jsonObj);
                    }
                } else {
                    alert("get All appData Error,Msg:" + responseObj.msg)
                }
                drawCharts(chartDataList);
            }
	     });
	  })
	
    function requireChartData() {
        var strategysName = $('#knowledgeStrategyName').val();
        var triggerName = $('#triggerStrategyName').val();
        var beginTime = $('#beginTime').val();
        var endTime = $('#endTime').val();
        var chartDataList = [];
        if (triggerName == "所有trigger") {
             $.ajax({
                 type: 'POST',
                 url: getStatByKeyUrl,
                 async: false,
                 data: {
                     key: strategysName,
                     beginTime: beginTime,
                     endTime: endTime
                 },
                 dataType: 'json',
                 success: function (data) {
                     var responseObj = eval(data);
                     if (responseObj.code == 0) {
                         var list= responseObj['data']['fieldList'];
                         for (var i = 0; i < list.length; i++) {
            
                             var keyAndField = list[i]['field'];
            
                             var countList = list[i]['countList'];
                             var dataList = new Array();
                             for (var  j= 0; j < countList.length; j++) {
                                 var value = countList[j]['value'];
                                 var time = countList[j]['time'];
                                 dataList.push([splitTimeToSeconds(time), value]);
                             }
                             var jsonObj = {
                                 name: keyAndField,
                                 data: dataList
                             }
                             chartDataList.push(jsonObj);
                         }
                     } else {
                         alert("get All appData Error,Msg:" + responseObj.msg)
                     }
                 }
             });
        } else {
             $.ajax({
                 type: 'POST',
                 url: getStatByKeyAndFieldUrl,
                 async: false,
                 data: {
                     key: strategysName,
                     field: triggerName,
                     beginTime: beginTime,
                     endTime: endTime
                 },
                 dataType: 'json',
                 success: function (data) {
                     var responseObj = eval(data);
                     if (responseObj.code == 0) {
                    	 var list= responseObj['data']['fieldList'];
                         for (var i = 0; i < list.length; i++) {
            
                             var keyAndField = list[i]['field'];
            
                             var countList = list[i]['countList'];
                             var dataList = new Array();
                             for (var  j= 0; j < countList.length; j++) {
                                 var value = countList[j]['value'];
                                 var time = countList[j]['time'];
                                 dataList.push([splitTimeToSeconds(time), value]);
                             }
                             var jsonObj = {
                                 name: keyAndField,
                                 data: dataList
                             }
                             chartDataList.push(jsonObj);
                         }
                     } else {
                         alert("get All appData Error,Msg:" + responseObj.msg)
                     }
                 }
             });
        }
        return chartDataList;
    };

    function drawCharts(seriesData, timeList) {
//        var seriesData = requireChartData();
//         Highcharts.setOptions({
//             lang: {
//                 months: ['1', '2', '3', '4', '5', '6', '7',
//                     '8', '9', '10', '11', '12']
//             }
//         });
        $('#chartDiv').highcharts({
            chart: {
                type: 'spline'
            },
            title: {
                text: getUrlParam("videoTitle") + "-" + "来自" + getUrlParam("videoChannel")
            },
            xAxis: {
                // type: 'datetime',
                // dateTimeLabelFormats: {
                //     day: '%e. %b',
                // }
                categories : timeList
            },
            yAxis: {
                title: {
                    text: '数据'
                },
                min: 0
            },
            tooltip: {
                formatter: function () {
                    return '<b>' + '时间:' + this.x + '</b><br/>' +
                        '<b>' + '播放量:' + this.y + '</b><br/>';
                }
            },
            series: seriesData
        });
    };

    function init() {
        // var storage = window.localStorage;
        // var strategyName = storage.getItem("strategyName")
    	var videoTitle = getUrlParam("videoTitle");
        var videoChannel = getUrlParam("videoChannel");
        var uploadTime = getUrlParam("uploadTime");
        var beginTime = $("#beginTime").html();
        var endTime = $("#endTime").html();

        var chartDataList = [];
        // 获取beginTime到endTime之间的统计数据
         $.ajax({
             type: 'GET',
             url: getVideoDailyCountUrl,
             async: false,
             data: {
                 title: videoTitle,
                 channel: videoChannel,
                 uploadTime: uploadTime,
                 beginTime: beginTime,
                 endTime: endTime
             },
             dataType: 'json',
             success: function (data) {
                 var responseObj = eval(data);
                 if(responseObj.code == 200) {

                     var playCountList= responseObj.body;
                     var dataList = new Array();
                     var timeList = [];
                     for (var i = 0; i < playCountList.length; i++) {
                         var value = playCountList[i]['playCount'];
                         var time = playCountList[i]['date'];
                         dataList.push([time, value]);
                         timeList.push(time);
                     }
                     var jsonObj = {
                        name: "播放量",
                        data: dataList
                     }
                     chartDataList.push(jsonObj);
                 } else {
                     alert(responseObj.message);
                 }
                 drawCharts(chartDataList, timeList);
             }
         });
    }

    init();
});
