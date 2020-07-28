//goods控制层 
app.controller('goodsController' ,function($scope, $controller, goodsService,itemCatService){
	
	// 继承
	$controller("baseController", {
		$scope : $scope
	});
	
	// 保存
	$scope.save = function() {
		goodsService.save($scope.entity).success(function(response) {
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
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//批量删除 
	$scope.dele = function(){			
		//获取选中的复选框			
		goodsService.dele($scope.selectIds).success(
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
		goodsService.search(page,size,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;
			}			
		);
	}

	$scope.status=['未审核','已审核','审核未通过','关闭'];//商品状态
	$scope.itemCatList=[];//商品分类列表
	//查询商品分类
	$scope.findItemCatList=function(){
		itemCatService.findAll().success(
			function(response){
				for(var i=0;i<response.length;i++){
					$scope.itemCatList[response[i].id ]=response[i].name;
				}
			}
		);
	}
	$scope.updateStatus=function(status){
		goodsService.updateStatus($scope.selectIds, status).success(
			function(response){
				if(response.success){//成功
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];//清空ID集合
				}else{
					alert(response.message);
				}
			}
		);
	}
});	
