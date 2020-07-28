//商品详细页（控制层）
app.controller('itemController',function($scope, $http){
	$scope.num = 1;
	//数量操作
	$scope.addNum=function(x){
		$scope.num=$scope.num+x;
		if($scope.num<1){
			$scope.num=1;
		}
	}
	
	$scope.specificationItems={};//记录用户选择的规格
	//用户选择规格
	$scope.selectSpecification=function(name,value){
		$scope.specificationItems[name]=value;
		searchSku();//读取sku
	}	
	//判断某规格选项是否被用户选中
	$scope.isSelected=function(name,value){
		if($scope.specificationItems[name]==value){
			return true;
		}else{
			return false;
		}
	}
	
		//加载默认SKU
	$scope.loadSku=function(){
		$scope.sku=skuList[0];
		//默认用户选中
		$scope.specificationItems= JSON.parse(JSON.stringify($scope.sku.spec)) ;
	}
	
		//匹配两个对象
	matchObject=function(map1,map2){
		for(var k in map1){
			if(map1[k]!=map2[k]){
				return false;
			}
		}
		for(var k in map2){
			if(map2[k]!=map1[k]){
				return false;
			}
		}
		return true;
	}
		//查询SKU
	searchSku=function(){
		for(var i=0;i<skuList.length;i++ ){
			if( matchObject(skuList[i].spec ,$scope.specificationItems ) ){
				$scope.sku=skuList[i];
				return;
			}
		}
		//如果某些规格选项组合起来没有商品，给出一些提示字样
		$scope.sku={id:0,title:'--------',price:0};
	}
		//添加商品到购物车
	$scope.addToCart = function() {
		// alert("加入购物车的sku为：" + $scope.sku.id + "，数量为：" + $scope.num);
		// location.href = "http://localhost:9013/cart/addCart.do?skuId=" + $scope.sku.id + "&num=" + $scope.num;

		// 直接跳转，可以跳转成功，但是响应的结果直接在页面以json的格式输出了，不方便看购物车页面

		//  解决办法：可以使用 无刷新请求，根据请求响应的结果，再跳转到 购物车页面
		$http.get("http://localhost:9013/cart/addCart.do?skuId=" + $scope.sku.id + "&num=" + $scope.num, {'withCredentials':true}).success(function (resp) {
			if(resp.success) {
				location.href = "http://localhost:9013/cart.html";
			} else {
				alert(resp.message);
			}
		});

	}
});

