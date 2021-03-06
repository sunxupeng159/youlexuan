app.controller('payController' ,function($scope ,payService,$location){
    //本地生成二维码
    $scope.createCode=function(){
        payService.createCode().success(function(response){
            if(response){
                $scope.money= response.total_fee;//金额
                $scope.out_trade_no= response.out_trade_no;//订单号
                //二维码
                var qr =new QRious({
                    element:document.getElementById('qrious'),
                    size:250,
                    level:'H',
                    value:response.qrcode//二维码需要的url
                });
                //生成二维码
                queryPayStatus(response.out_trade_no);//查询支付状态
            }
    } );
    }

    // 查询支付状态
    queryPayStatus = function(out_trade_no) {
        payService.queryPayStatus(out_trade_no).success(function(response) {
            if (response.success) {
                location.href="paysuccess.html#?money="+$scope.money;
            } else {
                if(response.message=='超过时间未支付,订单取消'){
                    location.href="orderfail.html";
                }else{
                    location.href="payfail.html";
                }
            }
        });
    }

    //获取金额
    $scope.getMoney=function(){
        return $location.search()['money'];
    }
});