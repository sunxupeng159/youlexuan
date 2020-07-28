//type_template控制层 
app.controller('typeTemplateController' ,function($scope, $controller, typeTemplateService,brandService,specificationService){
	
	// 继承
	$controller("baseController", {
		$scope : $scope
	});

	$scope.specList={data:[]};//规格列表
	//读取规格列表
	$scope.findSpecList=function(){
		specificationService.selectOptionList().success(
			function(response){
				$scope.specList={data:response};
			}
		);
	}

	$scope.brandList={data:[{id:1,text:'联想'},{id:2,text:'华为'},{id:3,text:'小米'}]};//品牌列表
	//读取品牌列表
	$scope.findBrandList=function(){
		brandService.selectOptionList().success(
			function(response){
				$scope.brandList={data:response};
			}
		);
	}

	// 保存
	$scope.save = function() {
		typeTemplateService.save($scope.entity).success(function(response) {
			if (response.success) {
				// 重新加载
				$scope.reloadList();
			} else {
				alert(response.message);
			}
		});
	}

	$scope.findOne=function(id){
		typeTemplateService.findOne(id).success(
			function(response){
				$scope.entity= response;
				// JSON js内置对象
				$scope.entity.brandIds=  JSON.parse($scope.entity.brandIds);//转换品牌列表
				$scope.entity.specIds=  JSON.parse($scope.entity.specIds);//转换规格列表
				$scope.entity.customAttributeItems= JSON.parse($scope.entity.customAttributeItems);//转换扩展属性
			}
		);
	}
	//批量删除 
	$scope.dele = function(){			
		//获取选中的复选框			
		typeTemplateService.dele($scope.selectIds).success(
			function(response){
				if(response.success){
					$scope.reloadList();
					$scope.selectIds=[];
				}						
			}		
		);				
	}

	//品牌列表
	// 定义搜索对象 
	$scope.searchEntity = {};
	// 搜索
	$scope.search = function(page,size){			
		typeTemplateService.search(page,size,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;
			}			
		);
	}
//增加行
	$scope.addTableRow=function(){
		$scope.entity.customAttributeItems.push({});
	}

	//删除扩展属性行
	$scope.deleTableRow=function(index){
		$scope.entity.customAttributeItems.splice(index,1);//删除
	}

	//定义同时初始化品牌、规格列表数据
	$scope.initSelect=function(){
		$scope.findBrandList();
		$scope.findSpecList();
	}


});	
