<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"  prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>流程管理</title>

    <!-- Bootstrap -->
    <link href="bootstrap/css/bootstrap.css" rel="stylesheet">
    <link href="css/content.css" rel="stylesheet">

    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
    <script src="http://cdn.bootcss.com/html5shiv/3.7.2/html5shiv.min.js"></script>
    <script src="http://cdn.bootcss.com/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
    <script src="js/jquery.min.js"></script>
    <script src="bootstrap/js/bootstrap.min.js"></script>
</head>
<body>

<!--路径导航-->
<ol class="breadcrumb breadcrumb_nav">
    <li>首页</li>
    <li>报销管理</li>
    <li class="active">我的待办事务</li>
</ol>
<!--路径导航-->

<div class="page-content">
    <form class="form-inline">
        <div class="panel panel-default">
            <div class="panel-heading">报销单列表</div>
            
            <div class="table-responsive">
                <table class="table table-striped table-hover">
                    <thead>
                    <tr>
                        <th width="5%">ID</th>
                        <th width="15%">报销金额</th>
                        <th width="15%">标题</th>
                        <th width="15%">备注</th>
                        <th width="15%">时间</th>
                        <th width="15%">状态</th>
                        <th width="20%">操作</th>
                    </tr>
                    </thead>
                    <tbody>
						<c:forEach var="baoXiao" items="${BaoXiaoBill }">
							<tr>
		                        <td>${baoXiao.id }</td>
		                        <td>${baoXiao.money }</td>
		                        <td>${baoXiao.title }</td>
		                        <td>${baoXiao.remark }</td>
		                        
		                        <td>
									<fmt:formatDate value="${baoXiao.creatdate }" pattern="yyyy-MM-dd hh:mm:ss"/>				
								</td>
								<c:if test="${baoXiao.state==2 }">
			                        <td>审核完成</td>
			                        <td>
	                            		<a href="deleteBaoXiao?baoXiaoId=${baoXiao.id}" class="btn btn-danger btn-xs"><span class="glyphicon glyphicon-remove"></span> 删除</a>
		                        	    <a href="${pageContext.request.contextPath}/findBaoXiaoTaskForm?baoXiaoId=${baoXiao.id}" class="btn btn-success btn-xs"><span class="glyphicon gloyphicon-plus"></span>查看审核记录</a>
			                        	
			                        </td>
								</c:if>
								<c:if test="${baoXiao.state==1 }">
			                        <td>审核未完成</td>
			                        <td>
		                        	    <a href="${pageContext.request.contextPath}/findBaoXiaoTaskForm?baoXiaoId=${baoXiao.id}" class="btn btn-success btn-xs"><span class="glyphicon gloyphicon-plus"></span>查看审核记录</a>
		                        		<a href="${pageContext.request.contextPath}/viewCurrentImageByBill?baoXiaoId=${baoXiao.id}" target="_blank" class="btn btn-success btn-xs"><span class="glyphicon gloyphicon-eye-open"></span>查看当前流程图</a>
		                        	
			                        </td>
								</c:if>
                  		  </tr>
						
						
						</c:forEach>

                    </tbody>
                </table>
            </div>
                      <div class="page">
				      	【当前第${page.pageNum}页，总共${page.total}条记录,总共${page.pages }页】
				        <a href="${pageContext.request.contextPath }/queryBaoXiaoBill?pageNum=1" >首页</a>
				        <a href="${pageContext.request.contextPath }/queryBaoXiaoBill?pageNum=${page.prePage}" >上一页</a>
				        <a href="${pageContext.request.contextPath }/queryBaoXiaoBill?pageNum=${page.nextPage}" >下一页</a>
				        <a href="${pageContext.request.contextPath }/queryBaoXiaoBill?pageNum=${page.pages}" >尾页</a> 
     				 </div>
        </div>
    </form>
    
</div>
</body>
</html>