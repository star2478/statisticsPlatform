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
	var searchTitle = $('#searchTitle').val();
	var channel = $("#channelList").val();
	var beginTime = $("#beginTime").html();
	var endTime = addHMSTimeOnEndTime($("#endTime").html());
	var firstReturnData = ajaxMainData(1, eachPageNum, searchTitle, channel, beginTime, endTime);
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
            	$("#channelList").append($("#channelListTmpl").tmpl(data.body));
            }else{
               alert("get Channel Data Error,Msg:" + responseObj.message);
            }
        }
    });
  }
  
  // 获取分页统计数据，page页数，1开始
  function ajaxMainData(pageNo, limit, title, channel, beginTime, endTime){
	var result;
    $.ajax({
        type:'GET',
        url: getVideoInfoByPageUrl,
        data:{
          pageNo : pageNo,
          limit : limit,
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
  // 重置搜索条件
  function resetSearchHtml() {
	$('#searchTitle').val('');
	getChannelList();
  }
  // 重置视频列表页
  function resetVideoListHtml(jsonData){
    $('#videoListTmpl').html('');
    $('#statisticsMainTmpl').tmpl(jsonData,{makeArrayForEach: makeArrayForEach}).appendTo('#videoListTmpl');
  }
  
  function buildVideoListTmpl() {
	  var searchTitle = $('#searchTitle').val();
	  var channel = $("#channelList").val();
	  var beginTime = $("#beginTime").html();
	  var endTime = addHMSTimeOnEndTime($("#endTime").html());
	  var firstReturnData = ajaxMainData(1, eachPageNum, searchTitle, channel, beginTime, endTime);
	  if (firstReturnData != undefined) {
	      tabActiveChange($('#statisticsMain'))
	      $('#videoListTmpl').html('');
	      $('#statisticsMainTmpl').tmpl(firstReturnData,{makeArrayForEach: makeArrayForEach}).appendTo('#videoListTmpl');
	  }
  }
  
  // 点击统计tab页
  $(document).on('click','#statisticsMain',function(){
    var returnData = ajaxMainData(1, eachPageNum, null, null, null, null);
    if (returnData != undefined) {
      resetSearchHtml()
      resetVideoListHtml(returnData)
      tabActiveChange($(this))
    }
  });
  
  // 点击search按钮
  $(document).on('click','#searchStatistics',function(){
	  var searchTitle = $('#searchTitle').val();
	  var channel = $("#channelList").val();
	  var beginTime = $("#beginTime").html();
	  var endTime = addHMSTimeOnEndTime($("#endTime").html());
	  var firstReturnData = ajaxMainData(1, eachPageNum, searchTitle, channel, beginTime, endTime);
	  if (firstReturnData != undefined) {
	      tabActiveChange($('#statisticsMain'))
	      $('#videoListTmpl').html('');
	      $('#statisticsMainTmpl').tmpl(firstReturnData,{makeArrayForEach: makeArrayForEach}).appendTo('#videoListTmpl');
	  }
  });
  
  // 触发结束时间事件
  $(document).on('click','#endTime',function(){
	  buildVideoListTmpl();
  });
  // 触发起始时间事件
  $(document).on('click','#beginTime',function(){
	  buildVideoListTmpl();
  });
  // 触发渠道select事件
  $('#channelList').change(function(){
	  buildVideoListTmpl();
  });
  // 触发标题事件
  $(document).on('keypress','#searchTitle',function(event){
	  if(event.keyCode == "13") {
		  buildVideoListTmpl();
	  }
  });
  
  // 点击上一页
  $(document).on('click','#prePage',function(){
	  var disabled = $("#prePage").attr("class");
	  if(disabled == "disabled") {
		  return;
	  }
	  var searchTitle = $('#searchTitle').val();
	  var channel = $("#channelList").val();
	  var beginTime = $("#beginTime").html();
	  var endTime = addHMSTimeOnEndTime($("#endTime").html());
	  var pageNo = $("#prePage").attr("data-page");
	  var firstReturnData = ajaxMainData(pageNo-1, eachPageNum, searchTitle, channel, beginTime, endTime);
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
	  var beginTime = $("#beginTime").html();
	  var endTime = addHMSTimeOnEndTime($("#endTime").html());
	  var pageNo = $("#nextPage").attr("data-page");
	  var firstReturnData = ajaxMainData(pageNo, eachPageNum, searchTitle, channel, beginTime, endTime);
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
//    window.location.href = chartHtmlUrl;	// 当前页面打开
    var url = chartHtmlUrl + "?link=" + videoLink + "&&&channel=" + videoChannel;
    url = encodeURI(url);
    window.open(url);	// 新页面打开
  });
  
  // 触发导出
  $(document).on('click','#exportHomeStatistics',function(aLink){
	  var str = "col1,col2,col3\n三木三叉,value2,value3";  
      str =  encodeURIComponent(str);
      $(this).href = "data:text/csv;charset=utf-8,\ufeff"+str;
//      aLink.href = "data:text/csv;charset=utf-8,\ufeff"+str;
  });
  
  function hlxhlx() {
	  alert("hlxhlx");
  }
  
});
