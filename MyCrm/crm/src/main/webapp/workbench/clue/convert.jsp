
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
String basePath = request.getScheme() + "://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<!DOCTYPE html>
<html>
<head>
	<base href="<%=basePath%>">
<meta charset="UTF-8">

<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>


<link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>

<script type="text/javascript">
	$(function(){

        //BootStrap中的日历控件
        $(".time").datetimepicker({
            minView: "month",
            language:  'zh-CN',
            format: 'yyyy-mm-dd',
            autoclose: true,
            todayBtn: true,
            pickerPosition: "top-left"
        });

		$("#isCreateTransaction").click(function(){
			if(this.checked){
				$("#create-transaction2").show(200);
			}else{
				$("#create-transaction2").hide(200);
			}
		});

		//为 放大镜 图标绑定事件，打开搜索市场活动的模态窗口
		$("#openSearchModel").click(function () {
            $("#searchActivityModal").modal("show");
        })
        
        //为搜索市场活动模态窗口中的搜索框绑定键盘事件
        $("#searchBox").keydown(function (event) {
            if(event.keyCode == 13){
                $.ajax({
                    url:"workbench/clue/getAListByName.do",
                    data:{
                        "name":$.trim($("#searchBox").val())
                    },
                    type:"get",
                    dataType:"json",
                    success:function (data) {
                        var html = "";
                        $.each(data,function (i,n) {
                            html += '<tr>';
                            html += '<td><input type="radio" name="activity" value="'+n.id+'"/></td> ';
                            html += '<td id="'+n.id+'">'+n.name+'</td>';
                            html += '<td>'+n.startDate+'</td>';
                            html += '<td>'+n.endDate+'</td>';
                            html += '<td>'+n.owner+'</td>';
                            html += '</tr>';
                        })
                        $("#activityBody").html(html);
                    }
                })
             // return  false是为了防止页面刷新，不写会导致页面直接刷新
                return false;
            }
        })
        //为搜索市场活动模态窗口中的提交按钮绑定单机事件
        //点击提交后将市场活动名，填充到市场活动源的文本框中，将市场活动id提交到隐藏域中
        $("#submitActivityBtn").click(function () {
            //获取选中的市场活动id
            var $ck = $("input[name=activity]:checked");
            var id = $ck.val();
            //获取市场活动的名称
            var name = $("#"+id).html();
            $("#activityName").val(name);

            //将市场活动id保存到隐藏域中
            $("#activityId").val(id);

            //清空输入框和单选按钮,清除tbody下的列表
            $("#searchBox").val("");
            $ck.prop("checked",false);
            $("#activityBody").html("");
            //将模态窗口关闭
            $("#searchActivityModal").modal("hide");
        })
        //为转换按钮绑定事件，执行线索的转换操作
        $("#convertBtn").click(function () {
            /*
              提交请求到后台，执行线索转换的操作，应该发出传统请求
              请求结束后，最终响应回线索的列表页中

              根据“为客户创建交易”的复选框有没有打钩，判断是否需要创建交易
             */
            if($("#isCreateTransaction").prop("checked")){
                //alert("需要创建交易")
                /*
                  如果需要创建交易，出了要为后台擦魂帝clueId之外，还得为后台传递交易表单中的信息，金额，预计成交日期，交易名称，阶段，市场活动源
                  如果使用：window.location.href = "workbench/clue/covert.do?xxx=xxx..后面需要跟上一大堆参数信息，很不方便，并且是get请求，请求的
                  的长度有限制。

                  我们可以选择使用提交表单的形式来发出本次的传统请求，提交表单的参数不用我们手动去挂载，并且能提交post请求，没有长度限制
                 */
                $("#tranForm").submit();

            }else{
                //不需要创建交易，直接走后台,传一个clueId就可以了
                window.location.href = "workbench/clue/covert.do?clueId=${param.id}"
            }
        })
	});
</script>

</head>
<body>
	
	<!-- 搜索市场活动的模态窗口 -->
	<div class="modal fade" id="searchActivityModal" role="dialog" >
		<div class="modal-dialog" role="document" style="width: 90%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title">搜索市场活动</h4>
				</div>
				<div class="modal-body">
					<div class="btn-group" style="position: relative; top: 18%; left: 8px;">
						<form class="form-inline" role="form">
						  <div class="form-group has-feedback">
						    <input type="text" class="form-control" id="searchBox" style="width: 300px;" placeholder="请输入市场活动名称，支持模糊查询">
						    <span class="glyphicon glyphicon-search form-control-feedback"></span>
						  </div>
						</form>
					</div>
					<table id="activityTable" class="table table-hover" style="width: 900px; position: relative;top: 10px;">
						<thead>
							<tr style="color: #B3B3B3;">
								<td></td>
								<td>名称</td>
								<td>开始日期</td>
								<td>结束日期</td>
								<td>所有者</td>
								<td></td>
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

	<!---
	     El表达式为我们提供了许多隐含对象
	     只有域对象（xxxScope）才可以省略对象名
	     其他的隐含对象的对象名一概不能省略（如果省略了，会变成从域对象中取值了）

	     param对象就是从请求头中获取参数

	     在jsp中有9个内置对象，EL表达中的内置对象和jsp中的内置对象并非一一对应

	     一个特殊的内置对象：pageContext,（页面域对象），可以取得另外8个内置对象
	     在jsp中可以直接使用的内置对象，在EL表达式中需要通过pageContext去取内置对象

	     如：jsp中获取路径：\<\%request.contextPath%>  注意;这里的\是为了转义，不然会直接取值
	     El表达式中获取路径：\${pagaContext.request.contextPath}
	-->
	<div id="title" class="page-header" style="position: relative; left: 20px;">
		<h4>转换线索 <small>${param.fullname}${param.appellation}-${param.company}</small></h4>
	</div>
	<div id="create-customer" style="position: relative; left: 40px; height: 35px;">
		新建客户：${param.company}
	</div>
	<div id="create-contact" style="position: relative; left: 40px; height: 35px;">
		新建联系人${param.fullname}${param.appellation}
	</div>
	<div id="create-transaction1" style="position: relative; left: 40px; height: 35px; top: 25px;">
		<input type="checkbox" id="isCreateTransaction"/>
		为客户创建交易
	</div>
	<div id="create-transaction2" style="position: relative; left: 40px; top: 20px; width: 80%; background-color: #F7F7F7; display: none;" >

        <!--
            最后提交表单的行为结果：
            请求头： workbench/clue/covert.do
            请求体：money=xxx name=xxx expectedDate=xx stage=xxx....
        --->
		<form id="tranForm" action="workbench/clue/covert.do" method="post">
            <input type="hidden" name="clueId" value="${param.id}">
		  <div class="form-group" style="width: 400px; position: relative; left: 20px;">
		    <label for="amountOfMoney">金额</label>
		    <input type="text" class="form-control" id="amountOfMoney" name="money">
		  </div>
		  <div class="form-group" style="width: 400px;position: relative; left: 20px;">
		    <label for="tradeName">交易名称</label>
		    <input type="text" class="form-control" id="tradeName" name="name">
		  </div>
		  <div class="form-group" style="width: 400px;position: relative; left: 20px;">
		    <label for="expectedClosingDate">预计成交日期</label>
		    <input type="text" class="form-control time" id="expectedClosingDate"  name="expectedDate" readonly>
		  </div>
		  <div class="form-group" style="width: 400px;position: relative; left: 20px;">
		    <label for="stage">阶段</label>
		    <select id="stage"  class="form-control" name="stage">
		    	<option></option>

                <%--
                     使用jstl表达式，从服务器缓存中遍历stateList数据字典(tbl_dic_type表和tbl_dic_value表执行多表查询)
                     在settings/service/impl/DicServiceImpl中实现数据字典并放入到缓存中
                --%>
		    	<c:forEach items="${stageList}" var="s">
                    <option value="${s.value}">${s.text}</option>
                </c:forEach>
		    </select>
		  </div>
		  <div class="form-group" style="width: 400px;position: relative; left: 20px;">
		    <label for="activityName">市场活动源&nbsp;&nbsp;<a href="javascript:void(0);" id="openSearchModel" style="text-decoration: none;"><span class="glyphicon glyphicon-search"></span></a></label>
		    <input type="text" class="form-control" id="activityName" placeholder="点击上面搜索" readonly>
              <%--上面文本框中显示的是市场活动名，但我们将来要的是市场活动的id，因此要加一个隐藏域保存市场活动的id--%>
              <input type="hidden" id="activityId" name="activityId">
		  </div>
		</form>
		
	</div>
	
	<div id="owner" style="position: relative; left: 40px; height: 35px; top: 50px;">
		记录的所有者：<br>
		<b>${param.owner}</b>
	</div>
	<div id="operation" style="position: relative; left: 40px; height: 35px; top: 100px;">
		<input class="btn btn-primary" type="button" id="convertBtn" value="转换">
		&nbsp;&nbsp;&nbsp;&nbsp;
		<input class="btn btn-default" type="button" value="取消">
	</div>
</body>
</html>