//user控制层 
app.controller('userController' ,function($scope, $controller, userService){

	// 继承
	$controller("baseController", {
		$scope : $scope
	});
	// 保存
	$scope.save = function() {
		userService.save($scope.entity, $scope.code).success(function(response) {
			if (response.success) {
				location.href = "login.html";
			} else {
				alert(response.message);
			}
		});
	}

	
	//查询实体 
	$scope.findOne = function(id){				
		userService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//批量删除 
	$scope.dele = function(){			
		//获取选中的复选框			
		userService.dele($scope.selectIds).success(
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
		userService.search(page,size,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;
			}			
		);
	}
		//注册
		$scope.reg=function(){
			if($scope.entity.password!=$scope.password){
				alert("两次输入的密码不一致，请重新输入");
				return;
			}
			userService.add($scope.entity,$scope.code).success(
				function(response){
					alert(response.message);
					location.href = "login.html";
				}
			);
		}
//发送验证码
	$scope.sendCode=function(){
		if($scope.entity.phone==null){
			alert("请输入手机号！");
			return;
		}


		userService.sendCode($scope.entity.phone).success(
			function(response){
				alert(response.message);
				if(response.success) {

					var time = 180;

					$("#smsBtn").prop("disabled", "disabled");
					$("#smsBtn").prop("value", time + "秒后重新发送");

					var t = setInterval(function(){
						time--;
						if(time < 1) {
							$("#smsBtn").prop("disabled", "");
							$("#smsBtn").prop("value", "获取短信验证码");
							clearInterval(t);
							return;
						}

						$("#smsBtn").prop("value", time + "秒后重新发送");
					}, "1000");
				} else {
					alert(resp.message);
				}

			}
		);
	}
/*// 发送短信验证码
	$scope.sendCode = function () {
		userService.sendCode($scope.entity.phone).success(function (resp) {
			if(resp.success) {

				var time = 180;

				$("#smsBtn").prop("disabled", "disabled");
				$("#smsBtn").prop("value", time + "秒后重新发送");

				var t = setInterval(function(){
					time--;
					if(time < 1) {
						$("#smsBtn").prop("disabled", "");
						$("#smsBtn").prop("value", "获取短信验证码");
						clearInterval(t);
						return;
					}

					$("#smsBtn").prop("value", time + "秒后重新发送");
				}, "1000");
			} else {
				alert(resp.message);
			}
		});
	}*/

});	
