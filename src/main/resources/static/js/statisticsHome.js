$(document).ready(function() {
  var makeArrayForEach = function(word, size){
    var i, result = [];
    for(i = 0; i < size; i++){
      result.push(word);
    }
    return result;
  }
  
  refreshMainData();

  // 在endTime后面加上hour、minute、second
  function addHMSTimeOnEndTime(endTime) {
	  return endTime + " 23:59:59";
  }
  
  function refreshMainData(){
	getChannelList();
	buildPlayCountAndCrawlerNums();
	var searchTitle = $('#searchTitle').val();
	var channel = $("#channelList").val();
	var sortType = $("#sortType").val();
	var beginTime = $("#beginTime").html();
	var endTime = addHMSTimeOnEndTime($("#endTime").html());
	var firstReturnData = ajaxMainData(1, eachPageNum, searchTitle, channel, sortType, beginTime, endTime);
//    var firstReturnData = ajaxMainData(1, eachPageNum, null, null, null, null);
    if (firstReturnData != undefined) {
      tabActiveChange($('#statisticsMain'))
      $('#videoListTmpl').html('');
      $('#statisticsMainTmpl').tmpl(firstReturnData,{makeArrayForEach: makeArrayForEach}).appendTo('#videoListTmpl');
    }
  }
  
  // 获取所有渠道
  function getChannelList() {
	$.ajax({
        type:"GET",
        url: getChannelListUrl,
        data:{},
        dataType:"json",
        success: function(data){
            var responseObj = eval(data);
            if(responseObj.code == 200){
            	$("#channelList").html("");
            	$("#channelList").append($("#channelListTmpl").tmpl(data.body));
            }else{
               alert("get Channel Data Error,Msg:" + responseObj.message);
            }
        }
    });
  }
  
  // 获取分页统计数据，page页数，1开始
  function ajaxMainData(pageNo, limit, title, channel, sortType, beginTime, endTime){
	var result;
    $.ajax({
        type:'GET',
        url: getVideoInfoByPageUrl,
        data:{
          pageNo : pageNo,
          limit : limit,
          title : title,
          sortType : sortType,
          channel : channel,
          beginTime : beginTime,
          endTime : endTime,
        },
        async:false,
        dataType:'json',
        success:function  (data) {
           var responseObj = eval(data);
           if(responseObj.code == 200){
        	   result = responseObj;
        	   return ;
           }else{
              alert("get Main Data Error, Msg:" + responseObj.message);
           }
        }
     });
    return result;

  }
  function tabActiveChange(jqObj){
    $(".main-menu-li").removeClass("active");
    jqObj.parent().addClass('active')
  }
  // 重置视频列表页
  function resetVideoListHtml(jsonData){
    $('#videoListTmpl').html('');
    $('#statisticsMainTmpl').tmpl(jsonData,{makeArrayForEach: makeArrayForEach}).appendTo('#videoListTmpl');
  }
  
  // 构建视频列表
  function buildVideoListTmpl() {
	  var searchTitle = $('#searchTitle').val();
	  var channel = $("#channelList").val();
	  var sortType = $("#sortType").val();
	  var beginTime = $("#beginTime").html();
	  var endTime = addHMSTimeOnEndTime($("#endTime").html());
	  var firstReturnData = ajaxMainData(1, eachPageNum, searchTitle, channel, sortType, beginTime, endTime);
	  if (firstReturnData != undefined) {
	      tabActiveChange($('#statisticsMain'))
	      $('#videoListTmpl').html('');
	      $('#statisticsMainTmpl').tmpl(firstReturnData,{makeArrayForEach: makeArrayForEach}).appendTo('#videoListTmpl');
	  }
  }
  
  // 构建播放总量和爬取次数
  function buildPlayCountAndCrawlerNums() {
	  var title = $('#searchTitle').val();
	  var channel = $("#channelList").val();
	  var beginTime = $("#beginTime").html();
	  var endTime = addHMSTimeOnEndTime($("#endTime").html());
	  $.ajax({
	        type:'GET',
	        url: getVideoPlayCountTotalUrl,
	        data:{
	          title : title,
	          channel : channel,
	          beginTime : beginTime,
	          endTime : endTime,
	        },
	        async:false,
	        dataType:'json',
	        success:function  (data) {
	           var responseObj = eval(data);
	           if(responseObj.code == 200){
	        	   result = responseObj;
	        		  $('#crawlingTimes').html(responseObj.body.crawlingTimes);
	        		  $('#playCountTotal').html(responseObj.body.playCountTotal);
	           }else{
	              alert("get VideoPlayCountTotal Error, Msg:" + responseObj.message);
	           }
	        }
	  });
  }

  // 触发标题事件
  $(document).on('keypress','#searchTitle',function(event){
	  if(event.keyCode == "13") {
		  buildVideoListTmpl();
		  buildPlayCountAndCrawlerNums();
	  }
  });
  // 触发结束时间事件
  $(document).on('click','#endTime',function(){
	  buildVideoListTmpl();
	  buildPlayCountAndCrawlerNums();
  });
  // 触发起始时间事件
  $(document).on('click','#beginTime',function(){
	  buildVideoListTmpl();
	  buildPlayCountAndCrawlerNums();
  });
  // 触发渠道select事件
  $('#channelList').change(function(){
	  buildVideoListTmpl();
	  buildPlayCountAndCrawlerNums();
  });
  // 触发排序类型select事件(不需要重新计算播放总量)
  $('#sortType').change(function(){
	  buildVideoListTmpl();
  });
  
  // 点击上一页
  $(document).on('click','#prePage',function(){
	  var disabled = $("#prePage").attr("class");
	  if(disabled == "disabled") {
		  return;
	  }
	  var searchTitle = $('#searchTitle').val();
	  var channel = $("#channelList").val();
	  var sortType = $("#sortType").val();
	  var beginTime = $("#beginTime").html();
	  var endTime = addHMSTimeOnEndTime($("#endTime").html());
	  var pageNo = $("#prePage").attr("data-page");
	  var firstReturnData = ajaxMainData(pageNo-1, eachPageNum, searchTitle, channel, sortType, beginTime, endTime);
	  if (firstReturnData != undefined) {
	      tabActiveChange($('#statisticsMain'))
	      $('#videoListTmpl').html('');
	      $('#statisticsMainTmpl').tmpl(firstReturnData,{makeArrayForEach: makeArrayForEach}).appendTo('#videoListTmpl');
	  }
	  
  });
  
  // 点击下一页
  $(document).on('click','#nextPage',function(){
	  var disabled = $("#nextPage").attr("class");
	  if(disabled == "disabled") {
		  return;
	  }
	  var searchTitle = $('#searchTitle').val();
	  var channel = $("#channelList").val();
	  var sortType = $("#sortType").val();
	  var beginTime = $("#beginTime").html();
	  var endTime = addHMSTimeOnEndTime($("#endTime").html());
	  var pageNo = $("#nextPage").attr("data-page");
	  var firstReturnData = ajaxMainData(pageNo, eachPageNum, searchTitle, channel, sortType, beginTime, endTime);
	  if (firstReturnData != undefined) {
	      tabActiveChange($('#statisticsMain'))
	      $('#videoListTmpl').html('');
	      $('#statisticsMainTmpl').tmpl(firstReturnData,{makeArrayForEach: makeArrayForEach}).appendTo('#videoListTmpl');
	  }
  });
  
  // 点击历史图表
  $(document).on('click','.showChartBtn',function(){
    // var videoTitle = $(this).parent().parent().children().attr("videotitle");
    var videoLink = $(this).parent().parent().children().eq(1).attr("videolink");
    var videoChannel = $(this).parent().parent().children().eq(2).attr("videochannel");
    // var uploadTime = $(this).parent().parent().children().attr("uploadtime");
    // var storage = window.localStorage;
    // storage.setItem("videoTitle", videoTitle);
    // storage.setItem("videoChannel", videoChannel);
    // storage.setItem("uploadTime", uploadTime);
    var url = chartHtmlUrl + "?link=" + videoLink + "&&&channel=" + videoChannel;
    url = encodeURI(url);
//    window.open(url);	// 新页面打开
    window.location.href = url;	// 当前页面打开
  });
  
  // 触发导出
//  $(document).on('click','#exportHomeStatistics',function(aLink){
//	  var str = "col1,col2,col3\n三木三叉,value2,value3";  
//      str =  encodeURIComponent(str);
//      $(this).href = "data:text/csv;charset=utf-8,\ufeff"+str;
////      aLink.href = "data:text/csv;charset=utf-8,\ufeff"+str;
//  });
  
});
