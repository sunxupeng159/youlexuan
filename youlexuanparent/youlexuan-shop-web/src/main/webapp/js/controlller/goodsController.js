//goods控制层 
app.controller('goodsController' ,function($scope, $controller,$location, goodsService,uploadService,itemCatService,typeTemplateService  ){
	
	// 继承
	$controller("baseController", {
		$scope : $scope
	});
	
	// 保存
	$scope.save = function() {
		$scope.entity.goodsDesc.introduction = editor.html();
		goodsService.save($scope.entity).success(function(response) {
			if (response.success) {
				alert('保存成功');
				$scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[]}};
				editor.html('');//清空富文本编辑器
			} else {
				alert(response.message);
			}
		});
	}
	
	//查询实体 
	$scope.findOne = function(){
		// search():接收页面所有的参数成一个数组
		var id= $location.search()['id'];//获取参数值
		if(id==null){
			return;
		}
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;
				//向富文本编辑器添加商品介绍
				editor.html($scope.entity.goodsDesc.introduction);

				//显示图片列表
				$scope.entity.goodsDesc.itemImages=
					JSON.parse($scope.entity.goodsDesc.itemImages);

				//显示扩展属性
				$scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.entity.goodsDesc.customAttributeItems);
				//规格
				$scope.entity.goodsDesc.specificationItems=JSON.parse($scope.entity.goodsDesc.specificationItems);
				for( var i=0;i<$scope.entity.itemList.length;i++){
					$scope.entity.itemList[i].spec = JSON.parse( $scope.entity.itemList[i].spec);
				}
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

	//保存基本属性
	//保存
	$scope.add=function(){
		goodsService.add( $scope.entity  ).success(
			function(response){
				if(response.success){
					alert('保存成功');
					$scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[]}};
				}else{
					alert(response.message);
				}
			}
		);
	}

	$scope.entity = {goods: {isEnableSpec: 0}, goodsDesc: {itemImages: [], specificationItems: []}};//定义页面实体结构

	$scope.updateSpecAttribute=function($event,name,value){
		var object= $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems ,'attributeName', name);
		if(object!=null){
			if($event.target.checked ){
				object.attributeValue.push(value);
			}else{
				//取消勾选
				object.attributeValue.splice( object.attributeValue.indexOf(value ) ,1);//移除选项
				//如果选项都取消了，将此条记录移除
				if(object.attributeValue.length==0){
					$scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(object),1);
				}
			}
		}else{
			$scope.entity.goodsDesc.specificationItems.push({"attributeName":name,"attributeValue":[value]});
		}
	}
	//添加图片列表
	$scope.add_image_entity=function(){
		$scope.entity.goodsDesc.itemImages.push($scope.image_entity);
	}

	//列表中移除图片
	$scope.remove_image_entity=function(index){
		$scope.entity.goodsDesc.itemImages.splice(index,1);
	}
	/**
	 * 上传图片
	 */
	$scope.uploadFile=function(){
		uploadService.uploadFile().success(function(response) {
			if(response.success){//如果上传成功，取出url
				$scope.image_entity.url=response.message;//设置文件地址
			}else{
				alert(response.message);
			}
		}).error(function() {
			alert("上传发生错误");
		});
	}

//读取1级分类

	$scope.selectItemCat1List=function () {
		itemCatService.findByParentId(0).success(
			function (response) {
				$scope.itemCat1List=response
			}
		);
	}
//读取二级分类
	$scope.$watch('entity.goods.category1Id', function(newValue, oldValue) {
		//判断一级分类有选择具体分类值，在去获取二级分类
		if(newValue){
			//根据选择的值，查询二级分类
			itemCatService.findByParentId(newValue).success(
				function(response){
					$scope.itemCat2List=response;
				}
			);
		}
	});

	//读取三级分类
	$scope.$watch('entity.goods.category2Id', function(newValue, oldValue) {
		//判断二级分类有选择具体分类值，在去获取三级分类
		if(newValue){
			//根据选择的值，查询二级分类
			itemCatService.findByParentId(newValue).success(
				function(response){
					$scope.itemCat3List=response;
				}
			);
		}
	});
	$scope.$watch('entity.goods.category3Id', function(newValue, oldValue) {
		//判断三级分类被选中，在去获取更新模板id
		if(newValue){
			itemCatService.findOne(newValue).success(
				function(response){
					$scope.entity.goods.typeTemplateId=response.typeId; //更新模板ID
				}
			);
		}
	});

	//模板ID选择后  更新模板对象
	$scope.$watch('entity.goods.typeTemplateId', function(newValue, oldValue) {
		if(newValue){
			typeTemplateService.findOne(newValue).success(
				function(response) {
					$scope.typeTemplate = response;//获取类型模板
					$scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds);//品牌列表
					//如果没有ID，则加载模板中的扩展数据
					if ($location.search()['id'] == null) {
						$scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);//扩展属性
					}
				}
			);
			//查询规格列表
			typeTemplateService.findSpecList(newValue).success(
				function(response){
					$scope.specList=response;
				}
			);
		}
	});

	//创建SKU列表
	$scope.createItemList=function(){
		//初始值：每次点击规格选项都重新生成，这样不管取消、选中规格选项都可以及时更改列表
		// itemList和后台goods组合属性对应
		$scope.entity.itemList=[{spec:{},price:0,num:99999,status:'1',isDefault:'0'}];
		// 避免直接使用太长，找个变量替换一下
		// 格式：[{"attributeName":"网络","attributeValue":["移动3G"]},{"attributeName":"机身内存","attributeValue":["32G","16G"]}]
		var items=  $scope.entity.goodsDesc.specificationItems;
		for(var i=0;i< items.length;i++){
			$scope.entity.itemList = addColumn($scope.entity.itemList,items[i].attributeName,items[i].attributeValue);
		}
	}
//添加列值：不使用$scope,表示是该controller中的私有方法，页面不能调用
	addColumn=function(list, columnName, conlumnValues){
		var newList=[];//新的集合
		for(var i=0;i<list.length;i++){
			var oldRow= list[i];
			for(var j=0;j<conlumnValues.length;j++){
				var newRow= JSON.parse( JSON.stringify( oldRow ) );//深克隆
				newRow.spec[columnName]=conlumnValues[j];
				newList.push(newRow);
			}
		}
		return newList;
	}
	$scope.status=['未审核','已审核','审核未通过','关闭'];//商品状态s

	$scope.itemCatList=[];//商品分类列表
//加载商品分类列表
	$scope.findItemCatList=function(){
		itemCatService.findAll().success(
			function(response){
				for(var i=0;i<response.length;i++){
					$scope.itemCatList[response[i].id]=response[i].name;
				}
			}
		);
	}
//根据规格名称和选项名称返回是否被勾选
	$scope.checkAttributeValue=function(specName, optionName){
		var items= $scope.entity.goodsDesc.specificationItems;
		var object= $scope.searchObjectByKey(items,'attributeName',specName);
		if(object==null){
			return false;
		}else{
			if(object.attributeValue.indexOf(optionName)>=0){
				return true;
			}else{
				return false;
			}
		}
	}

	$scope.updateStatus = function(status) {
		goodsService.updateStatus($scope.selectIds, status).success(
			function (response) {
				if (response.success) {
					$scope.reloadList();//刷新列表
					$scope.selectIds = [];//清空ID集合
				} else {
					alert(response.message);
				}
			}
		);
	}
//更改状态
	this.updateStatus=function(ids,status){
		return $http.get('../goods/updateStatus.do?ids='+ids+"&status="+status);
	}
});	
