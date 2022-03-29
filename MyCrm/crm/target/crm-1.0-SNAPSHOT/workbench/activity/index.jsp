<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
	String basePath = request.getScheme() + "://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
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

<!--添加BootStrap中的分页插件-->
<link rel="stylesheet" type="text/css" href="jquery/bs_pagination/jquery.bs_pagination.min.css"/>
	<script type="text/javascript" src="jquery/bs_pagination/jquery.bs_pagination.min.js"></script>
	<script type="text/javascript" src="jquery/bs_pagination/en.js"	></script>

<script type="text/javascript">

	$(function(){

		//为创建按钮绑定事件，打开添加操作
		$("#addBtn").click(function () {

			$(".time").datetimepicker({
				minView: "month",
				language:  'zh-CN',
				format: 'yyyy-mm-dd',
				autoclose: true,
				todayBtn: true,
				pickerPosition: "bottom-left"
			});

			/*
			  操作模态窗口的方式：
			     需要操作的模态窗口的jQuery对象，调用modal方法，为该方法传递参数
			     参数show：表示打开模态窗口  参数hide：表示关闭模态窗口
			 */
			//先走后台，目的是为了取得用户信息列表，为模态窗口这种的所有者添加下拉列表中的数据
           $.ajax({
			   url:"workbench/activity/getUserList.do",
			   type:"get",
			   dataType:"json",
			   success:function (data) {
			   	//这里的data是一个User数组
				   var html="<option></option>"
				   $.each(data,function (i,n){
					   html += "<option value='"+n.id+"' >"+n.name+"</option>";

				   })
				   $("#create-owner").html(html);
				   //取得当前登录用户的id,在js中使用EL表达式，一定要套在双引号中,在html中使用不需要双引号
				   var userId = "${user.id}";
				   //将当前登录的用户，设置为下拉框默认的选项
				   $("#create-owner").val(userId);
				   //所有者信息处理完毕后展现模态窗口
				   $("#createActivityModal").modal("show");
			   }
		   })

		})
		//为添加操作中的保存按钮，执行添加操作
		$("#saveBtn").click(function () {
			$.ajax({
				url:"workbench/activity/saveActivity.do",
				data:{
					"owner":$.trim($("#create-owner").val()),
					"name":$.trim($("#create-name").val()),
					"startDate":$.trim($("#create-startDate").val()),
					"endDate":$.trim($("#create-endDate").val()),
					"cost":$.trim($("#create-cost").val()),
					"description":$.trim($("#create-description").val())
				},
				type:"post",//添加修改删除使用post请求
				dataType:"json",
				success:function (data) {
                     if(data.success){
                     	//添加成功后，刷新市场活动信息列表（局部刷新）
                        //pageList(1,2);
                         /*
                         pageList($("#activityPage").bs_pagination('getOption', 'currentPage')
                             ,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));

                            $("#activityPage").bs_pagination('getOption', 'currentPage')：表示操作后停留在当前页
                            $("#activityPage").bs_pagination('getOption', 'rowsPerPage')：表示操作后维持已经设置好的每页展示的记录数

                            上面这两个参数不需要我们进行任何修改操作
                              直接使用即可
                         */
                         //做完添加操作后，应该回到第一页，维持每页展现的记录数
                         pageList(1,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
						 //清空添加操作模态窗口中数据
						 /*
						    注意：我们拿到的form表单的jQuery对象，对于表单的jQuery对象提供了submit()方法让我们提交表单
						          但jQuery对象没有为我们提供reset()方法让我们重置表单（坑：但是idea会提示reset()方法，不过无法用）

						    如：form表单的jQuery对象.submit()：可以提交表单
						        form表单的jQuery对象.reset()：无法重置表单

						     重置表单需要使用原生的js中的dom对象，js中的form表单对象有reset()方法重置表单

						     将jQuery对象转为dom对象
						         jQuery对象[dom对象在jQuery对象数组中的下标]

						     dom对象转为jQuery对象：
                                  $(dom)
						  */
						 $("#addActivityForm")[0].reset();
						 //关闭添加操作的模态窗口
						 $("#createActivityModal").modal("hide");
					 }else{
                     	alert("添加市场活动失败");
					 }

				}
				
			})
		})
		//页面加载完毕后出发一个方法
		//默认展开列表的第一页，每页展现两条记录
		pageList(1,2);

		//为查询按钮绑定事件，触发pageList方法
		$("#search-Btn").click(function () {
			/*
           出现的问题：
              当输入查询条件之后，没有点击查询按钮，直接点击分页组件时，分页的条件就直接根据输入的
              查询条件进行分页了，正常应该点完查询按钮之后才按照查询条件分页

           解决方法
               点击查询按钮的时候，我们应该将搜索框中的信息保存起来，保存到一个隐藏域中
         */
			$("#hidden-name").val($.trim($("#search-name").val()));
			$("#hidden-owner").val($.trim($("#search-owner").val()));
			$("#hidden-startDate").val($.trim($("#search-startDate").val()));
			$("#hidden-endDate").val($.trim($("#search-endDate").val()));
			pageList(1,2);
		})

		//为全选的复选框绑定事件，触发全选操作
		$("#allChecked").click(function () {
			$("input[name=ck]").prop("checked",this.checked)
		})

		//通过查询列表数据中的复选框，判定查询列表表头中的复选框是否选择全选
		//以下这种方式是不行的,无法触发数据中的复选框的事件
		/*

		  $("input[name=ck]").click(function () {
			  alert(12324);
		   })
		*/
		/*
		   因为动态生成的元素，是不能以普通绑定事件的形式进行操作的

		   动态生成的元素，我们要以on方法的形式来触发事件

		    语法：
		       $(需要绑定元素的有效的外层元素).on(绑定事件的方式,需要绑定的元素的jQuery对象,回调函数)

		       需要绑定元素的有效的外层元素：不是动态生成的元素，选最靠近动态生成元素的那个有效元素
		 */
		$("#activityBody").on("click",$("input[name=ck]"),function () {
			/*
			    如果当前页中数据的条数，与当前页中数据列表中复选框被选中的数据条数相等，
			    说明本页中所有数据被选中，触发表头中复选框的checked事件，否则不触发
			    $("input[name=ck]").length：当前页中数据的条数
			    $("input[name=ck]:checked").length：当前页中数据列表中复选框被选中的数据条数
			    $("input[name=ck]").length==$("input[name=ck]:checked").length：结果是一个布尔值，如果为true触发前面的事件
			 */

			$("#allChecked").prop("checked",$("input[name=ck]").length==$("input[name=ck]:checked").length)
		})

		//为删除按钮绑定事件，执行市场活动的删除操作
		$("#deleteBtn").click(function () {

			//找出市场活动数据列表中所有被选中的jQuery对象
			var $ck = $("input[name=ck]:checked");

			if($ck.length==0){
				alert("请选择需要删除的记录");

				//肯定选了，而且可能是1条，有可能是多条
			}else{
				//需要传递的 url应该是以下的格式：
				//url:workbench/activity/deleteActivity.do?id=xxx&id=xxx&id=xxx

				//拼接参数
				var param = "";
				// alert("执行删除操作");
				//将$ck中的每一个动对象遍历出来，取其value值，就相当于取得需要删除的dom对象的id值
				for(var i=0;i<$ck.length;i++){

					//使用dom对象
					//param += "id=" + $ck[i].value
					//使用jQuery对象
					param += "id="+$($ck[i]).val();

					//判断如果不是最后一个元素，就加 & 符号
					if(i != $ck.length-1){
						param += "&";
					}
				}
				// alert(param);
				if(confirm("确定删除所选定的记录吗？")){
					$.ajax({
						url:"workbench/activity/deleteActivity.do",
						data:param,
						type:"get",
						dataType:"json",
						success:function (data) {
							/*
                               data
                                 {"success":true/fasle}
                             */
							if(data.success){
								alert("删除成功");
								//删除成功之后刷新列表
								//pageList(1,2);
                                //删除操作后回到第一页，维持每页展现的记录数
                                pageList(1,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
							}else{
								alert("删除失败");
							}
						}
					})
				}
			}
		})

		//为打开修改模态窗口按钮绑定事件，目的是打开修改操作的模态窗口
		$("#editBtn").click(function () {
			var $edit = $("input[name=ck]:checked");

			if($edit.length == 0){
				alert("请选择需要修改的记录");
			}else if($edit.length>1){
				alert("每次只能修改一条记录");
			}else{
				//能执行到这里说明只选了一条记录
				var id = $edit.val();
				$.ajax({
					url:"workbench/activity/getUserListAndActivity.do",
					data:{
						"id":id
					},
					type:"get",
					dataType:"json",
					success:function (data) {
						/*
						   data中有用户列表，市场活动对象
						   {
						      "userList":[{用户1},{用户2}]，
						      "activity":{市场活动}
						   }
						 */
						//处理所有者下拉框
						var html = "<option></option>";
						$.each(data.userList,function (i,n) {
							html += "<option value='"+n.id+"'>"+n.name+"</option>";
						})
						$("#edit-owner").html(html);

						//处理一条Activity
						$("#edit-id").val(data.activity.id);
						$("#edit-name").val(data.activity.name);
						$("#edit-owner").val(data.activity.owner);
						$("#edit-startDate").val(data.activity.startDate);
						$("#edit-endDate").val(data.activity.endDate);
						$("#edit-cost").val(data.activity.cost);
						$("#edit-description").val(data.activity.description);

						//所有的值都填好之后,就可以打开修改操作的模态窗口
						$("#editActivityModal").modal("show");
					}
				})
			}
		})

        //为更新按钮绑定事件，目的是跟新市场活动
        $("#updateBtn").click(function () {
            $.ajax({
                url:"workbench/activity/updateActivity.do",
                data:{
                    "id":$.trim($("#edit-id").val()),
                    "owner":$.trim($("#edit-owner").val()),
                    "name":$.trim($("#edit-name").val()),
                    "startDate":$.trim($("#edit-startDate").val()),
                    "endDate":$.trim($("#edit-endDate").val()),
                    "cost":$.trim($("#edit-cost").val()),
                    "description":$.trim($("#edit-description").val())
                },
                type:"post",
                dataType:"json",
                success:function (data) {
                    if(data.success){
                        alert("修改市场活动成功成功");
                        //修改市场活动信息列表
                        //pageList(1,2);
                        /*
                            修改操作后应该在当前页，维持每页展现的记录数
                         */
                        pageList($("#activityPage").bs_pagination('getOption', 'currentPage')
                            ,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));

                        //关闭修改操作的模态窗口
                        $("#editActivityModal").modal("hide");
                    }else{
                        alert("修改市场活动失败");
                    }
                }
            })

        })

	});
	/*
	   对于所有的关系型数据库，做前端分页相关操作的基础组件就是pageNo和pageSize
	   pageNo：页码
	   pageSize：每页展现的记录条数

	   pageList方法：就是发出ajax请求到后台，从后台取得最新的市场活动信息列表数据
	                 通过响应回来的数据，局部刷新市场活动信息列表

	   我们都有在哪些情况下，需要调用pageList方法（什么情况下需要刷新一下市场活动列表）
	   1）点击左侧菜单栏中的“市场活动”超链接，需要刷新市场活动列表，调用pageList方法
	   2）添加，修改，删除候，需要刷新市场活动列表，调用pageList方法
	   3）点击查询按钮的时候，需要刷新市场活动列表，调用pageList方法
	   4）点击分页组件的时候，调用pageList方法

	   以上为pageList方法执行了留个入口，也就是说，在以上的6个操作执行完毕后，我们必须调用pageList方法，刷新市场活动信息列表
	 */
	function pageList(pageNo,pageSize) {
        //每当刷新列表(调用这个分页的方法时)就将全选的 复选框中的对勾去掉
		$("#allChecked").prop("checked",false);

		//查询前，将隐藏域中保存的信息取出来，重新赋值到搜索框汇总
		$("#search-name").val($.trim($("#hidden-name").val()));
		$("#search-owner").val($.trim($("#hidden-owner").val()));
		$("#search-startDate").val($.trim($("#hidden-startDate").val()));
		$("#search-endDate").val($.trim($("#hidden-endDate").val()));
		$.ajax({
			url:"workbench/activity/pageList.do",
			data:{
				//这两个参数是做分页查询的
				"pageNo":pageNo,
				"pageSize":pageSize,

				//下面这四个参数是做分页查询的
                "name":$.trim($("#search-name").val()),
				"owner":$.trim($("#search-owner").val()),
		        "startDate":$.trim($("#search-startDate").val()),
		        "endDate":$.trim($("#search-endDate").val())
			},
			type:"get",
			dataType:"json",
			success:function (data) {
				/*
				    data:
				      我们需要市场活动列表
				      [{市场活动1},{..2},{..3}]   List<Activity> activityList
				      一会BootStrap分页插件需要的：查询出来的总记录数
				      {"total":100}   int total

                      最终data中数据的形式：
                        {"total":100,"activityList":[{市场活动1},{..2},{..3}]}
				 */
				var html="";
				console.log("activity"+typeof data);
				//每一个n就是一个市场活动对象
				$.each(data.dataList,function (i,n) {

                    html += '<tr class="active">';
                    html += '<td><input type="checkbox" name="ck" value="' + n.id + '"/></td>';
                    html += '<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href=\'workbench/activity/detail.do?id=' + n.id + '\';">' + n.name + '</a></td>';
                    html += '<td>' + n.owner + '</td>';
                    html += '<td>' + n.startDate + '</td>';
                    html += '<td>' + n.endDate + '</td>';
				})

				$("#activityBody").html(html);

				//计算总页数
				var totalPages = data.total%pageSize==0?data.total/pageSize:parseInt(data.total/pageSize)+1;

				//数据处理完毕后，结合分页插件，对前端展现分页相关的信息
				$("#activityPage").bs_pagination({
					currentPage: pageNo, // 页码
					rowsPerPage: pageSize, // 每页显示的记录条数
					maxRowsPerPage: 20, // 每页最多显示的记录条数
					totalPages: totalPages, // 总页数
					totalRows: data.total, // 总记录条数

					visiblePageLinks: 3, // 显示几个卡片

					showGoToPage: true,
					showRowsPerPage: true,
					showRowsInfo: true,
					showRowsDefaultInfo: true,
					//这个回调函数在点击分页组件是触发
					onChangePage : function(event, data){
						pageList(data.currentPage , data.rowsPerPage);
					}
				});
			}
		})
	}
</script>
</head>
<body>

    <!--创建隐藏域，用来保存模糊查询中条件框中的信息-->
	<input type="hidden" id="hidden-name"/>
	<input type="hidden" id="hidden-owner"/>
	<input type="hidden" id="hidden-startDate"/>
	<input type="hidden" id="hidden-endDate"/>

	<!-- 创建市场活动的模态窗口 -->
	<div class="modal fade" id="createActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel1">创建市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form id="addActivityForm" class="form-horizontal" role="form">
					
						<div class="form-group">
							<label for="create-owner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="create-owner">

								</select>
							</div>
                            <label for="create-name" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-name">
                            </div>
						</div>
						
						<div class="form-group">
							<label for="create-startDate" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="create-startDate" readonly>
							</div>
							<label for="create-endDate" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="create-endDate" readonly>
							</div>
						</div>
                        <div class="form-group">

                            <label for="create-cost" class="col-sm-2 control-label">成本</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-cost">
                            </div>
                        </div>
						<div class="form-group">
							<label for="create-description" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="create-description"></textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<!--
                       data-dismiss="modal"：表示关闭模态窗口

                    -->
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="saveBtn">保存</button>
				</div>
			</div>
		</div>
	</div>
	
	<!-- 修改市场活动的模态窗口 -->
	<div class="modal fade" id="editActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel2">修改市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form class="form-horizontal" role="form">
						<input type="hidden" id="edit-id">
						<div class="form-group">
							<label for="edit-owner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="edit-owner">

								</select>
							</div>
                            <label for="edit-name" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-name" value="发传单">
                            </div>
						</div>

						<div class="form-group">
							<label for="edit-startDate" class="col-sm-2 control-label ">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="edit-startDate"/>
							</div>
							<label for="edit-endDate" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="edit-endDate"/>
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-cost" class="col-sm-2 control-label">成本</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-cost" value="5,000">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-description" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<!--
                                     关于文本域textarea:
                                       1)文本域一定是以标签对的形式来呈现的，正常状态下标签对要紧挨着
                                       2）textarea虽然是以标签对的形式来呈现的，但是他是属于表单元素范畴
                                         我们所有对textarea的取值和赋值操作，应该通风一使用val()方法
                                         而不是html()方法
								-->
								<textarea class="form-control" rows="3" id="edit-description"></textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">

					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="updateBtn">更新</button>
				</div>
			</div>
		</div>
	</div>
	
	
	
	
	<div>
		<div style="position: relative; left: 10px; top: -10px;">
			<div class="page-header">
				<h3>市场活动列表</h3>
			</div>
		</div>
	</div>
	<div style="position: relative; top: -20px; left: 0px; width: 100%; height: 100%;">
		<div style="width: 100%; position: absolute;top: 5px; left: 10px;">
		
			<div class="btn-toolbar" role="toolbar" style="height: 80px;">
				<form class="form-inline" role="form" style="position: relative;top: 8%; left: 5px;">
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">名称</div>
				      <input class="form-control" type="text" id="search-name">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">所有者</div>
				      <input class="form-control" type="text" id="search-owner">
				    </div>
				  </div>


				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">开始日期</div>
					  <input class="form-control" type="text" id="search-startDate" />
				    </div>
				  </div>
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">结束日期</div>
					  <input class="form-control" type="text" id="search-endDate">
				    </div>
				  </div>
				  
				  <button type="button"  id="search-Btn" class="btn btn-default">查询</button>
				  
				</form>
			</div>
			<div class="btn-toolbar" role="toolbar" style="background-color: #F7F7F7; height: 50px; position: relative;top: 5px;">
				<div class="btn-group" style="position: relative; top: 18%;">
					<!--
                       点击创建按钮，观察两个属性和属性值

                       data-toggle="modal"：表示触发按钮，将要打开一个模态窗口

                       data-target="#createActivityModal"：表示要打开哪个模态窗口，通过#id的形式找到该窗口

                       现在我们以属性和属性值的方式写在了button元素中，用来打开模态窗口
                       但现在问题在于这样做是写死在里面的，没办法对按钮功能进行扩充

                       所以在未来的实际项目中，对于触发模态窗口的操作。一定不要写死在元素当中
                       应该由我们自己写的js代码来操作
                   -->
				  <button type="button" class="btn btn-primary" id="addBtn"><span class="glyphicon glyphicon-plus"></span> 创建</button>
				  <button type="button" class="btn btn-default" id="editBtn"><span class="glyphicon glyphicon-pencil"></span> 修改</button>
				  <button type="button" class="btn btn-danger" id="deleteBtn"><span class="glyphicon glyphicon-minus"></span> 删除</button>
				</div>

			</div>
			<div style="position: relative;top: 10px;">
				<table class="table table-hover">
					<thead>
						<tr style="color: #B3B3B3;">
							<td><input type="checkbox" id="allChecked"/></td>
							<td>名称</td>
                            <td>所有者</td>
							<td>开始日期</td>
							<td>结束日期</td>
						</tr>
					</thead>
					<tbody id="activityBody">
						<!--这里通过js动态生成查询的信息列表-->
					</tbody>
				</table>
			</div>
			
			<div style="height: 50px; position: relative;top: 30px;">
				<div id="activityPage"></div>
			</div>
			
		</div>
		
	</div>
</body>
</html>