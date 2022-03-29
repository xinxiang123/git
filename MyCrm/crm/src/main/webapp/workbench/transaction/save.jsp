<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
	String basePath = request.getScheme() + "://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";

   //获取application中的pMap
    Map<String,String> pMap = (Map<String,String>)application.getAttribute("pMap");
    //获取pMap中所有的key
	Set<String> set = pMap.keySet();
%>
<!DOCTYPE html>
<html>
<head>
	<base href="<%=basePath%>">
<meta charset="UTF-8">

<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet" />

<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>
	<script type="text/javascript" src="jquery/bs_typeahead/bootstrap3-typeahead.min.js"></script>
<script>
	/*
	    关于阶段和可能性
	      是一中一对一的关系
	      一个阶段对应一个可能性
	      我们可以将这中对应关系看成是一种键值对的对应关系
	      以阶段为key,通过选中的阶段，触发成交可能性value
	      stage(key)      possibility(value)

	      01资质审查       10%
	      02需求分析       25%
	      03键值建议       40%
	      。。。
	      07成交           100%
	      08...             0
	      09...             0
          对于以上的数据，通过观察得到结论
          （1） 数据量不大
          （2） 这是一种键值对的对应关系

          如果同时满足以上两种情况，那么我们可以不使用数据库表保存数据，因为数据没有什么变动
          可以选择使用properties属性配置文件来进行保存
          通常写成 stage2Possibility.properties
          01资质审查 = 10%
	      02需求分析 = 25%
	      03键值建议 = 40%
	      。。。
	      07成交 = 100%
	      08... = 0
	      09... = 0
	      但.properties属性配置文件中不支持中文，我们可以使用jdk中的bin下的javafxpackager.exe
	      将中文转为Unicode编码，写入配置文件，读取时在进行转码，转为中文
	 */
	$(function () {

		//使用BootStrap中自动补全的插件
		$("#addCustomerName").typeahead({
			source: function (query, process) {
				$.get(
						"workbench/transaction/getCustomerName.do",
						{ "name" : query },
						function (data) {
							//alert(data);
							//data是一个客户名的数组
							process(data);
						},
						"json"
				);
			},
			delay: 1500
		});
		//BootStrap中的日历控件
		$(".time").datetimepicker({
			minView: "month",
			language:  'zh-CN',
			format: 'yyyy-mm-dd',
			autoclose: true,
			todayBtn: true,
			pickerPosition: "button-left"
		});
		$(".time1").datetimepicker({
			minView: "month",
			language:  'zh-CN',
			format: 'yyyy-mm-dd',
			autoclose: true,
			todayBtn: true,
			pickerPosition: "top-right"
		});

		//为市场活动源按钮创建单击事件，进入搜索市场活动模态窗口
		$("#addActivityBtn").click(function () {
			$("#findMarketActivity").modal("show");
		})

		//为搜索市场活动框穿件键盘点击事件
		$("#searchActivityBox").keydown(function (event) {
			if(event.keyCode == 13){
				$.ajax({
					url:"workbench/transaction/getActivityListByName.do",
					data:{
						"name":$.trim($("#searchActivityBox").val())
					},
					type:"get",
					dataType:"json",
					success:function (data) {
						var html = "";
						$.each(data,function (i,n) {
							html += '<tr>'
						    html += '<td><input type="radio" name="activity" value="'+n.id+'"/></td>'
						    html += '<td id='+n.id+'>'+n.name+'</td>'
						    html += '<td>'+n.startDate+'</td>'
						    html += '<td>'+n.endDate+'</td>'
						    html += '<td>'+n.owner+'</td>'
						    html += '</tr>'
						})
						$("#activityBody").html(html);
					}
				})
				//展现完列表执行后，记得将模态窗口默认 的回车行为禁用掉
				//否则敲击回车展现完列表之后会再次刷新模态窗口
				return false;
			}
		})

		//为添加市场活动按钮绑定事件
		$("#submitActivityBtn").click(function () {
			var id = $("input[name = activity]:checked").val();
			var name = $("#"+id).html();
			if(confirm("确定提交?")){
				$("#addActivityName").val(name);
				$("#addActivityId").val(id);
				$("#searchActivityBox").val("");
				$("#activityBody").html("");
				$("#findMarketActivity").modal("hide");
			}
		})

		//为添加联系人按钮创建添加事件
		$("#addContactBtn").click(function () {
             $("#findContacts").modal("show");
		})

		//为搜索联系人框添加键盘点击事件
		$("#searchContactBox").keydown(function (event) {
			if(event.keyCode == 13){
				$.ajax({
					url:"workbench/transaction/getClueListByFullName.do",
					data:{
						"fullname":$.trim($("#searchContactBox").val())
					},
					type:"get",
					dataType:"json",
					success:function (data) {
						var html="";
						$.each(data,function (i,n) {
						    html += '<tr>';
							html += '<td><input type="radio" name="clue" value="'+n.id+'"/></td>';
							html += '<td id='+n.id+'>'+n.fullname+'</td>';
							html += '<td>'+n.website+'</td>';
							html += '<td>'+n.mphone+'</td>';
							html += '</tr>';
						})
						$("#contactBody").html(html);
					}
				})
				return false;
			}
		})
		//为添加客户信息按钮绑定事件
		$("#submitContactBtn").click(function () {
			var id = $("input[name = clue]:checked").val();

			var fullname = $("#"+id).html();
			if(confirm("确定提交?")){
				$("#addContactName").val(fullname);
				$("#addContactId").val(id);
				$("#searchContactBox").val("");
				$("#contactBody").html("");
				$("#findContacts").modal("hide");
			}
		})
     //为阶段的下拉框，绑定下拉的事件，根据选中的阶段填写可能性
		$("#addStage").change(function () {
			var stage = $(this).children('option:selected').val();//这就是selected的值

			/*
			   填写可能性

			   阶段有了stage
			   阶段和可能性之间的对应关系pMap,但pMap是java语言中的键值对关系（java中的map对象）
			   我们首先将pMap转换为js中的键值对的对应关系（js中的键值对关系就是json）

			   我们要做的是将pMap转换为
			   var json = {"01...":10,"02...":25,...}
			 */
			//犹豫我们声明这个就是一个json对象了，所以最后一个逗号会自动去掉
			var json = {
				<%
				    for(String key : set){
				    	String value = pMap.get(key);
				%>
                    "<%=key%>" : <%=value%>,
				<%
				}
				%>
			}
			/*
			    console.log(json);
			    结果：
			    01资质审查: 10
                02需求分析: 25
                03价值建议: 40
                04确定决策者: 60
                05提案/报价: 80
                06谈判/复审: 90
                07成交: 100
                08丢失的线索: 0
                09因竞争丢失关闭: 0
			 */
			/*
			var possibility = json.stage;
            alert(possibility);
			    我们现在以json.key的形式形式不能取得value，因为现在的stage是一个可变的变量
			    如果是这样的key，那么我们不能以传统的json.key的形式来取值
			    我们要使用的取值方式为json[key]
			 */
			var possibility = json[stage];
			//为可能性文本框赋值
            $("#addPossibility").val(possibility);
		})

		//为保存按钮绑定事件，点击保存按钮提交表单
		$("#saveTranBtn").click(function () {
			alert("保存交易");
			$("#tranForm").submit();
		})
	})

</script>
</head>
<body>

	<!-- 查找市场活动 -->	
	<div class="modal fade" id="findMarketActivity" role="dialog">
		<div class="modal-dialog" role="document" style="width: 80%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title">查找市场活动</h4>
				</div>
				<div class="modal-body">
					<div class="btn-group" style="position: relative; top: 18%; left: 8px;">
						<form class="form-inline" role="form">
						  <div class="form-group has-feedback">
						    <input type="text" class="form-control" id="searchActivityBox" style="width: 300px;" placeholder="请输入市场活动名称，支持模糊查询">
						    <span class="glyphicon glyphicon-search form-control-feedback"></span>
						  </div>
						</form>
					</div>
					<table id="activityTable3" class="table table-hover" style="width: 900px; position: relative;top: 10px;">
						<thead>
							<tr style="color: #B3B3B3;">
								<td></td>
								<td>名称</td>
								<td>开始日期</td>
								<td>结束日期</td>
								<td>所有者</td>
							</tr>
						</thead>
						<tbody id="activityBody">

						</tbody>
					</table>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
					<button type="button" class="btn btn-primary" id="submitActivityBtn">提交</button>
				</div>
			</div>
		</div>
	</div>

	<!-- 查找联系人 -->	
	<div class="modal fade" id="findContacts" role="dialog">
		<div class="modal-dialog" role="document" style="width: 80%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title">查找联系人</h4>
				</div>
				<div class="modal-body">
					<div class="btn-group" style="position: relative; top: 18%; left: 8px;">
						<form class="form-inline" role="form">
						  <div class="form-group has-feedback">
						    <input type="text" class="form-control" style="width: 300px;"  id="searchContactBox" placeholder="请输入联系人名称，支持模糊查询">
						    <span class="glyphicon glyphicon-search form-control-feedback"></span>
						  </div>
						</form>
					</div>
					<table id="activityTable" class="table table-hover" style="width: 900px; position: relative;top: 10px;">
						<thead>
							<tr style="color: #B3B3B3;">
								<td></td>
								<td>名称</td>
								<td>邮箱</td>
								<td>手机</td>
							</tr>
						</thead>
						<tbody id="contactBody">

						</tbody>
					</table>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
					<button type="button" class="btn btn-primary" id="submitContactBtn">提交</button>
				</div>
			</div>
		</div>
	</div>
	
	
	<div style="position:  relative; left: 30px;">
		<h3>创建交易</h3>
	  	<div style="position: relative; top: -40px; left: 70%;">
			<button type="button" class="btn btn-primary" id="saveTranBtn">保存</button>
			<button type="button" class="btn btn-default">取消</button>
		</div>
		<hr style="position: relative; top: -40px;">
	</div>
	<form action="workbench/transaction/saveTran.do"  method="post" class="form-horizontal" role="form" style="position: relative; top: -30px;" id="tranForm">
		<div class="form-group">
			<label for="addOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
			<div class="col-sm-10" style="width: 300px;">
				<select class="form-control" id="addOwner" name="owner">
				  <option></option>
					<c:forEach items="${userList}" var="user">
						<option value="${user.id}">${user.name}</option>
					</c:forEach>
				</select>
			</div>
			<label for="addMoney" class="col-sm-2 control-label">金额</label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control" id="addMoney" name="money">
			</div>
		</div>
		
		<div class="form-group">
			<label for="addTransactionName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control" id="addTransactionName" name="name">
			</div>
			<label for="addExpectedDate" class="col-sm-2 control-label">预计成交日期<span style="font-size: 15px; color: red;">*</span></label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control time" id="addExpectedDate" name="expectedDate" readonly>
			</div>
		</div>
		
		<div class="form-group">
			<label for="addCustomerName" class="col-sm-2 control-label">客户名称<span style="font-size: 15px; color: red;">*</span></label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control" id="addCustomerName" name="customerName" placeholder="支持自动补全，输入客户不存在则新建">
			</div>
			<label for="addStage" class="col-sm-2 control-label">阶段<span style="font-size: 15px; color: red;">*</span></label>
			<div class="col-sm-10" style="width: 300px;">
			  <select class="form-control" id="addStage" name="stage">
			  	<option></option>
			  	<c:forEach items="${stageList}" var="stage">
					<option value="${stage.value}">${stage.text}</option>
				</c:forEach>
			  </select>
			</div>
		</div>
		
		<div class="form-group">
			<label for="addType" class="col-sm-2 control-label">类型</label>
			<div class="col-sm-10" style="width: 300px;">
				<select class="form-control" id="addType" name="type">
				  <option></option>
				  <c:forEach items="${transactionTypeList}" var="tranType">
					  <option value="${tranType.value}">${tranType.text}</option>
				  </c:forEach>
				</select>
			</div>
			<label for="addPossibility" class="col-sm-2 control-label">可能性</label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control" id="addPossibility" readonly>
			</div>
		</div>
		
		<div class="form-group">
			<label for="addSource" class="col-sm-2 control-label">来源</label>
			<div class="col-sm-10" style="width: 300px;">
				<select class="form-control" id="addSource" name="source">
					<option></option>
					<c:forEach items="${sourceList}" var="s">
						<option value="${s.value}">${s.text}</option>
					</c:forEach>
				</select>
			</div>
			<label for="addActivityName" class="col-sm-2 control-label">市场活动源&nbsp;&nbsp;<a  id="addActivityBtn"><span class="glyphicon glyphicon-search"></span></a></label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control" id="addActivityName">
				<input type="hidden" id="addActivityId" name="activityId">
			</div>
		</div>
		
		<div class="form-group">
			<label for="addContactName" class="col-sm-2 control-label">联系人名称&nbsp;&nbsp;<a id="addContactBtn"><span class="glyphicon glyphicon-search"></span></a></label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control" id="addContactName">
				<input type="hidden" id="addContactId" name="contactsId">
			</div>
		</div>
		
		<div class="form-group">
			<label for="addDescription" class="col-sm-2 control-label">描述</label>
			<div class="col-sm-10" style="width: 70%;">
				<textarea class="form-control" rows="3" id="addDescription" name="description"></textarea>
			</div>
		</div>
		
		<div class="form-group">
			<label for="addContactSummary" class="col-sm-2 control-label">联系纪要</label>
			<div class="col-sm-10" style="width: 70%;">
				<textarea class="form-control" rows="3" id="addContactSummary" name="contactSummary"></textarea>
			</div>
		</div>
		
		<div class="form-group">
			<label for="addNextContactTime" class="col-sm-2 control-label">下次联系时间</label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control time1" id="addNextContactTime" name="nextContactTime" readonly>
			</div>
		</div>
		
	</form>
</body>
</html>