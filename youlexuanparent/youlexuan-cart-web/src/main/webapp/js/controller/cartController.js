app.controller('cartController',function($scope,cartService){
    //查询购物车列表
    $scope.findCartList=function(){
        cartService.findCartList().success(
            function(response){
                $scope.cartList=response;
            }
        );
    }

    //添加商品到购物车
    $scope.addGoodsToCartList=function(itemId, num){
        cartService.addGoodsToCartList(itemId, num).success(
            function(response){
                if(response.success){
                    $scope.findCartList();//刷新列表
                }else{
                    alert(response.message);//弹出错误提示
                }
            }
        );
    }
// 改变购物车数量
    $scope.changNum = function(itemId, num) {
        cartService.changeNum(itemId, num).success(function (resp) {
            if(resp.success) {
                $scope.findCartList();
            } else {
                alert(resp.message);
            }
        });
    }

        $scope.findCartList = function () {
            cartService.findCartList().success(function (resp) {
                if(resp) {
                    $scope.cartList = resp;

                    $scope.total = {totalNum:0,totalPrice:0.00};

                    // 计算商品的总件数、总价格
                    for(var i = 0; i < $scope.cartList.length; i++) {

                        var cart = $scope.cartList[i];

                        for(var j = 0; j < cart.orderItemList.length; j++) {
                            $scope.total.totalNum += cart.orderItemList[j].num;
                            $scope.total.totalPrice += cart.orderItemList[j].totalFee;
                        }
                    }
                }
            });
        }

    //获取地址列表
    $scope.findAddressList=function(){
        cartService.findAddressList().success(
            function(response){
                $scope.addressList=response;
                //设置默认地址
                for(var i=0;i< $scope.addressList.length;i++){
                    if($scope.addressList[i].isDefault=='1'){
                        $scope.address=$scope.addressList[i];
                        break;
                    }
                }
            }
        );
    }

    //选择地址
    $scope.selectAddress=function(address){
        $scope.address=address;
    }
    //判断是否是当前选中的地址
    $scope.isSelectedAddress=function(address){
        if(address==$scope.address){
            return true;
        }else{
            return false;
        }
    }

    $scope.order={paymentType:'1'};
//选择支付方式
    $scope.selectPayType=function(type){
        $scope.order.paymentType= type;
    }

    //保存订单
    $scope.submitOrder=function(){
        $scope.order.receiverAreaName=$scope.address.address;//地址
        $scope.order.receiverMobile=$scope.address.mobile;//手机
        $scope.order.receiver=$scope.address.contact;//联系人
        cartService.submitOrder($scope.order).success(
            function(response){
                if(response.success){
                    //页面跳转
                    if($scope.order.paymentType=='1'){//如果是微信支付，跳转到支付页面
                        location.href="pay.html";
                    }else{//如果货到付款，跳转到提示页面
                        location.href="paysuccess.html";
                    }
                }else{
                    alert(response.message);	//也可以跳转到提示页面
                }
            }
        );
    }
});