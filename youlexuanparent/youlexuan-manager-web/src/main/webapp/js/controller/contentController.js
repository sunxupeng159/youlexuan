//content控制层
app.controller('contentController' ,function($scope, $controller, contentService,uploadService,contentCategoryService){

	// 继承
	$controller("baseController", {
		$scope : $scope
	});

	// 保存
	$scope.save = function() {
		contentService.save($scope.entity).success(function(response) {
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
		contentService.findOne(id).success(
			function(response){
				$scope.entity= response;
			}
		);
	}

	//批量删除
	$scope.dele = function(){
		//获取选中的复选框
		contentService.dele($scope.selectIds).success(
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
		contentService.search(page,size,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;
				$scope.paginationConf.totalItems=response.total;
			}
		);
	}

	//上传广告图
	$scope.uploadFile=function(){
		uploadService.uploadFile().success(
			function(response){
				if(response.success){
					$scope.entity.pic=response.message;
				}else{
					alert("上传失败！");
				}
			}
		).error(
			function(){
				alert("上传出错！");
			}
		);
	}
//加载广告分类列表
	$scope.findContentCategoryList=function(){
		contentCategoryService.findAll().success(
			function(response){
				$scope.contentCategoryList=response;
			}
		);
	}
	$scope.status=["无效","有效"];
});
