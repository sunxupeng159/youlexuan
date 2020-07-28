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

	$scope.add = function(){
		sellerService.add($scope.entity).success(
			function(response){
				if(response.success){
					location.href='shoplogin.html';
				}else{
					alert(response.message);
				}
			}
		);

}
    
});	
