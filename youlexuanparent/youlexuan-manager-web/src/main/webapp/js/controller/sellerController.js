//seller控制层 
app.controller('sellerController' ,function($scope, $controller, sellerService){	
	
	// 继承
	$controller("baseController", {
		$scope : $scope
	});
	
	// 保存
	$scope.save = function() {
		sellerService.save($scope.entity).success(function(response) {
			if (response.success) {
				// 重新加载
				$scope.reloadList();
			} else {
				alert(response.message);
			}
		});
	}
	
	//查询实体 
	$scope.findOne = function(id){				
		sellerService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//批量删除 
	$scope.dele = function(){			
		//获取选中的复选框			
		sellerService.dele($scope.selectIds).success(
			function(response){
				if(response.success){
					$scope.reloadList();
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	// 定义搜索对象 
	$scope.searchEntity = {};
	// 搜索
	$scope.search = function(page,size){			
		sellerService.search(page,size,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;
			}			
		);
	}
	//显示审核状态
$scope.statusName=["未审核",  "已审核", "审核未通过","关闭"];

//修改审核状态
$scope.updateStatus=function(status){
	$scope.entity.status=status;
	sellerService.save($scope.entity).success(function (resp){
	if(resp.success){
		$scope.reloadList();
	}else{
		alert(resp.message);
	}
		});
	}
	$scope.updateStatus = function (status) {
		// 将点击的审核状态 赋值  给页面回显的entity对象
		$scope.entity.status = status;
		sellerService.save($scope.entity).success(function (resp) {
			if(resp.success) {
				$scope.reloadList();
			} else {
				alert(resp.message);
			}
		});
	}

});	
