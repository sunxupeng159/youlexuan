//购物车服务层
app.service('cartService',function($http){
    //购物车列表
    this.findCartList=function(){
        return $http.get('cart/findCartList.do');
    }

    this.changeNum = function(itemId, num) {
        return $http.get('/cart/addCart.do?skuId=' + itemId + '&num=' + num);
    }
    this.findAddressList=function(){
        return $http.get('address/findListByLoginUser.do');
    }

    //保存订单
    this.submitOrder=function(order){
        return $http.post('order/add.do',order);
    }
});