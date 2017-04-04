
var triggerModifyJqObj = null

$(document).ready(function() {
  var makeArrayForEach = function(word, size){
    var i, result = [];
    for(i = 0; i < size; i++){
      result.push(word);
    }
    return result;
  }

  refreshMainData();
  
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
//            	$('#getAppList').selectpicker('refresh');
//            	$('#getAppListMulti').data('mutishowData',data.data);
//            	//隐藏银行多选框
//            	$('.section').hide();
//        		$("#getAppListMulti").selectpicker('hide');
//        		$('#getAppVerList').selectpicker('hide');
//            	//加载appVersionList
//            	var appId = $('#getAppList').val();
//            	getAppVersionList(appId);
            }else{
               alert("get Channel Data Error,Msg:" + responseObj.message)
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
              alert("get Main Data Error, Msg:" + responseObj.message)
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
  function delConfirm(msg) {
    if (confirm(msg) == true){
      return true;
    }else{
      return false;
    }
  }
  function setOperationDatas(){
    $('#triggerOpe').html('');
    $('#triggerOpe').append("<option><</option>");
    $('#triggerOpe').append("<option>=</option>");
    $('#triggerOpe').append("<option>></option>");
    $('#triggerOpe').append("<option><=</option>");
    $('#triggerOpe').append("<option>>=</option>");
  }
  function refreshMainData(){
	getChannelList();
    var firstReturnData = ajaxMainData(1, eachPageNum, null, null, null, null);
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
  })
  
  // 点击search按钮
  $(document).on('click','#searchStatistics',function(){
	  var searchTitle = $('#searchTitle').val();
	  var channel = $("#channelList").val();
	  var beginTime = "";
	  var endTime = "";
	  var firstReturnData = ajaxMainData(1, eachPageNum, searchTitle, channel, beginTime, endTime);
	  if (firstReturnData != undefined) {
	      tabActiveChange($('#statisticsMain'))
	      $('#videoListTmpl').html('');
	      $('#statisticsMainTmpl').tmpl(firstReturnData,{makeArrayForEach: makeArrayForEach}).appendTo('#videoListTmpl');
	  }
  })
  
  $(document).on('click','input[name=persistentStatus]',function(){
   var perStat = $("input[name=persistentStatus]:checked").val();
   var expiTime = $('#knowledgeStrategyExpi').val();
   if(perStat ==1 && expiTime < 1800){
	   $("#persistentStatusDesc").html("数据过期时间必须大于或等于30分钟才能持久化！");
   }else{
	   $("#persistentStatusDesc").html("");
   }
  })
  $('#triggerModal').on('hidden.bs.modal', function () {
    $('#knowledgeModal').modal('show')
   })
  $('#triggerModal').on('show.bs.modal', function () {
   $('#knowledgeModal').modal('hide')
  })
  $(document).on('click','#addTriggerBtn',function(){
    $('#triggerForm')[0].reset();
    triggerModifyJqObj = null;
    setOperationDatas();
    setApplicationDatas();
    $('#triggerModal').modal();
  });
  $(document).on('click','.showChartBtn',function(){
    var knowledgeName = $(this).parent().parent().children('.strategyName').html();
    var storage = window.localStorage;
    storage.setItem("strategyName",knowledgeName)
    window.location.href = chartHtmlUrl;
  });
  $(document).on('click','.knowlegePaginationBtn',function(){
    pageNum = $(this).data('index');
	var searchTitle = $('#searchTitle').val();
    var returnData = ajaxMainData(pageNum, eachPageNum, searchTitle);
    if (returnData != undefined) {
      returnData.stype = 1
      resetVideoListHtml(returnData)
      tabActiveChange($('#statisticsMain'))
    }
  })
  $(document).on('click','#triggerSubmitBtn',function(){
    var triKey = $('#triggerKey').val();
    var sql = $('#sql').val();
    var triOpe = $('#triggerOpe').val();
    var triVal = $('#triggerVal').val();
    var triApp = $("#triggerApp").val();
    var persistent = $("input[name=persistent]:checked").val();
    if(triKey.trim().length == 0){
    	alert("触发应用名不能为空或者只含空格！");
    	return ;
    }
    if(triVal.trim().length == 0){
    	alert("触发应用阀值不能为空或者只含空格！");
    	return ;
    }
    if(triVal.length > 50){
    	alert("触发应用阀值长度超过限制！");
    	return ;
    }
    if (triggerModifyJqObj == null) {
      var triRow = {};
      triRow.name = triKey;
      triRow.sql = sql;
      triRow.op = triOpe;
      triRow.value = triVal;
      triRow.appStrategyName = triApp;
      triRow.persistent = persistent;
      var triDataJson = {appStrategyTriggers: []};
      triDataJson.appStrategyTriggers.push(triRow);
      $('#triggerListTmpl').tmpl(triDataJson).appendTo('#triggerListtbody');
      // alert(triKey + triOpe + triVal + triApp)
    }else{
      triggerModifyJqObj.parent().parent().children('.triggerKey').html(triKey);
      triggerModifyJqObj.parent().parent().children('.sql').html(sql);
      triggerModifyJqObj.parent().parent().children('.triggerOpe').html(triOpe);
      triggerModifyJqObj.parent().parent().children('.triggerOpe').data('op',triOpe);
      triggerModifyJqObj.parent().parent().children('.triggerVal').html(triVal);
      triggerModifyJqObj.parent().parent().children('.triggerApp').html(triApp);
      triggerModifyJqObj.parent().parent().children('.persistent').html(persistent);
    }
    $('#triggerModal').modal('hide');
  })
  $(document).on('click','.delTriggerBtn',function(){
    name = $(this).parent().parent().children('.triggerKey').html();
    var status = delConfirm("确认删除" + name + "?");
    if (status){
      $(this).parent().parent().remove();
    }
  })
  $(document).on('click','.modTriggerBtn',function(){
    var triKey = $(this).parent().parent().children('.triggerKey').html();
    var sql = $(this).parent().parent().children('.sql').text();
    var triOpe = $(this).parent().parent().children('.triggerOpe').data('op');
    var triVal = $(this).parent().parent().children('.triggerVal').html();
    var triApp = $(this).parent().parent().children('.triggerApp').html();
    var persistent = $(this).parent().parent().children('.persistent').html();
    var persistentObj = {};
    persistentObj.persistent = persistent;
    $('#triggerForm')[0].reset();
    triggerModifyJqObj = $(this);
    setOperationDatas();
    setApplicationDatas();
    $('#triggerKey').val(triKey);
    $('#sql').val(sql);
    $('#triggerOpe').val(triOpe);
    $('#triggerVal').val(triVal);
    $('#triggerApp').val(triApp);
    $('#persistentSelect').html('');
    $('#triggerModalPersistentSelectTmpl').tmpl(persistentObj).appendTo('#persistentSelect');
    $('#triggerModal').modal();
  })
  $(document).on('click','#knowledgeModalSubBtn',function(){
    var submitType = $('#knowledgeSubType').val();
    var knowledgeSubUrl = hostUri;
    var msg = "";
    var triggers = [];
    list = $('#triggerListtbody').children('tr');
    if (list.length >0) {
      var triggerKey;
      var triggerOpe;
      var triggerVal;
      var triggerApp;
      for(var i = 0 ; i < list.length ; i++){
        triggerKey = $(list[i]).children('.triggerKey').html()
        sql = $(list[i]).children('.sql').text()
        triggerOpe = $(list[i]).children('.triggerOpe').data('op')
        triggerVal = $(list[i]).children('.triggerVal').html()
        triggerApp = $(list[i]).children('.triggerApp').html()
        persistent = $(list[i]).children('.persistent').html()
        var triRow = {};
        triRow.name = triggerKey;
        triRow.sql = sql;//htmlEntityEncode(sql);
        triRow.op = triggerOpe;
        triRow.value = triggerVal;
        triRow.appStrategyName = triggerApp;
        triRow.persistent = persistent;
        triggers.push(triRow);
      }
    }
    if (submitType == 0) {
      knowledgeSubUrl = hostUri + "/fsg/strategy/addKnowledgeStrategy";
      msg = "添加";
    }else{
      knowledgeSubUrl = hostUri + "/fsg/strategy/modifyKnowledgeStrategyByName";
      msg = "修改";
    }
    var ksName = $('#knowledgeStrategyName').val();
    var ksDesc = $('#knowledgeStrategyDesc').val();
    var ksExpi = $('#knowledgeStrategyExpi').val();
    var ksStat = $("input[name=knowledgeStatus]:checked").val();
    var ptStat = $("input[name=persistentStatus]:checked").val();
	var knowledgeSubJson = {};
    knowledgeSubJson.key = ksName;
    knowledgeSubJson.description = ksDesc;
    knowledgeSubJson.status = ksStat;
    knowledgeSubJson.persistent = ptStat;
    knowledgeSubJson.expire = ksExpi;
    knowledgeSubJson.appStrategyTriggers = triggers;
    if(ptStat == 1 && ksExpi < 1800){
    	alert("数据过期时间必须大于或等于30分钟才能持久化！");
    	return;
    }
    if(ksName.match(/^[A-Za-z]+[A-Za-z0-9]*$/) == null){
      alert("知识策略名只能为数字和英文字母，且以英文开头");
      return ;
    }
    if(ksExpi.match(/^[0-9]+$/) == null){
      alert("只能为数字");
      return ;
    }
    $.ajax({
      type:'POST',
      url:knowledgeSubUrl,
      data:{
        subJson : JSON.stringify(knowledgeSubJson)
      },
      dataType:'json',
      success:function  (data) {
         var responseObj = eval(data);
         if(responseObj.code==0){
            alert(msg + "成功！");
            refreshMainData();
            $('#knowledgeModal').modal('hide');
         }else{
            alert(msg + "失败, Msg:" + responseObj.msg + ", 原因：" + responseObj.data)
         }
      }
   });
  })
  $(document).on('click','#applicationModalSubBtn',function(){
    var submitType = $('#applicationSubType').val();
    var appSubUrl = hostUri;
    var msg = "";
    if (submitType == 0) {
      appSubUrl = hostUri + "/fsg/strategy/addAppStrategy";
      msg = "添加";
    }else{
      appSubUrl = hostUri + "/fsg/strategy/modifyAppStategyByName";
      msg = "修改";
    }
    var asName = $('#applicationStrategyName').val();
    var asDesc = $('#applicationStrategyDesc').val();
    var asComm = $('#applicationStrategyComm').val();
    var applicationSubJson = {};
    applicationSubJson.name = asName;
    applicationSubJson.description = asDesc;
    applicationSubJson.command = asComm;
    if(asName.match(/^[A-Za-z0-9-_]+$/) == null){
      alert("只能为数字、英文字母、-和_");
      return ;
    }
    $.ajax({
      type:'POST',
      url:appSubUrl,
      data:{
    	  subJson : JSON.stringify(applicationSubJson)
      },
      dataType:'json',
      success:function  (data) {
         var responseObj = eval(data);
         if(responseObj.code==0){
            alert(msg + "成功！");
            refreshMainData();
            $('#applicationModal').modal('hide');
         }else{
            alert(msg + "失败,Msg:" + responseObj.msg)
         }
      }
   });
  })
  
  
});