$(document).ready(function () {

    // 调用初始化方法
    mainChartData();
    
    // 初始化
    function mainChartData() {
        // var storage = window.localStorage;
        // var strategyName = storage.getItem("strategyName")
        // var title = getUrlParam("title");
        var link = getUrlParam("link");
        var channel = getUrlParam("channel");
        // var uploadTime = getUrlParam("uploadTime");
        var beginTime = $("#beginTime").html();
        var endTime = $("#endTime").html();

        var chartDataList4PlayCount = [];
        var chartDataList4GrowthRate = [];
        // 获取beginTime到endTime之间的统计数据
         $.ajax({
             type: 'GET',
             url: getVideoDailyCountUrl,
             async: false,
             data: {
                 // title: title,
                 link: link,
                 channel: channel,
                 // uploadTime: uploadTime,
                 beginTime: beginTime,
                 endTime: endTime
             },
             dataType: 'json',
             success: function (data) {
                 var responseObj = eval(data);
                 if(responseObj.code == 200) {

                     var dailyCount= responseObj.body.dailyCount;
                     var dateList = [];
                     var playCountList = new Array();
                     var growthRateList = new Array();
                     for (var i = 0; i < dailyCount.length; i++) {
                         var playCount = dailyCount[i]['playCount'];
                         var growthRate = dailyCount[i]['growthRate'];
                         var date = dailyCount[i]['date'];
                         playCountList.push([date, playCount]);
                         growthRateList.push([date, growthRate]);
                         dateList.push(date);
                     }
                     var jsonObj4PlayCount = {
                        name: "播放量",
                        data: playCountList
                     }
                     chartDataList4PlayCount.push(jsonObj4PlayCount);
                     drawCharts4PlayCount(chartDataList4PlayCount, dateList, responseObj.body.title);
                     var jsonObj4GrowthRate = {
                     	name: "播放增长率（如果前一天播放量为0，第二天播放量为正数，则第二天的增长率不显示）",
                        data: growthRateList
                     }
                     chartDataList4GrowthRate.push(jsonObj4GrowthRate);
                     drawCharts4GrowthRate(chartDataList4GrowthRate, dateList, responseObj.body.title);
                 } else {
                     alert(responseObj.message);
                 }
             }
         });
    };
    

    // 触发结束时间事件
    $(document).on('click','#endTime',function(){
    	mainChartData();
    });
    // 触发起始时间事件
    $(document).on('click','#beginTime',function(){
    	mainChartData();
    });
	
    // 点击search按钮
    $(document).on('click','#searchStatistics',function(){
    	mainChartData();
    });

    // 画播放量图
    function drawCharts4PlayCount(seriesData, timeList, title) {
        $('#chartDiv4PlayCount').highcharts({
            chart: {
                type: 'spline'
            },
            title: {
                text: "播放量：" + title + "-" + "来自" + getUrlParam("channel")
            },
            xAxis: {
                categories : timeList
            },
            yAxis: {
                title: {
                    text: '播放量'
                },
                min: 0
            },
            tooltip: {
                formatter: function () {
                    return '<b>' + '时间：' + this.x + '</b><br/>' +
                        '<b>' + '播放量：' + this.y + '</b><br/>';
                }
            },
            series: seriesData
        });
    };

    // 画播放量增长率图
    function drawCharts4GrowthRate(seriesData, timeList, title) {
        $('#chartDiv4GrowthRate').highcharts({
            chart: {
                type: 'spline'
            },
            title: {
            	text: "播放增长率：" + title + "-" + "来自" + getUrlParam("channel")
            },
            xAxis: {
                categories : timeList
            },
            yAxis: {
                title: {
                    text: '播放增长率(%)'
                },
//                min: 0
            },
            tooltip: {
                formatter: function () {
                    return '<b>' + '时间：' + this.x + '</b><br/>' +
                        '<b>' + '播放增长率：' + this.y + '%</b><br/>';
                }
            },
            series: seriesData
        });
    };

});
