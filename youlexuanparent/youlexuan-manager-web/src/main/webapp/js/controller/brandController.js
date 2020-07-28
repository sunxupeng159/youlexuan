app.controller("brandController", function ($scope, $controller, brandService) {

    // 继承baseController
    $controller("baseController", {
        $scope: $scope
    });

    // $http.get()
    // $http.post()
    /*
     $.ajax({
     type: "GET",
     url: "test.json",
     data: {username:$("#username").val(), content:$("#content").val()},
     dataType: "json",
     success: function(data){
     });
     */

    // 查询全部
    $scope.findAll = function () {
        brandService.findAll().success(function (resp) {
            $scope.list = resp;
        });
    }

    // 分页方法
    $scope.findPage = function (page, size) {
        brandService.findPage(page, size).success(function (resp) {
            $scope.paginationConf.totalItems = resp.total;
            $scope.list = resp.rows;
        });
    }

    // 保存、修改
    $scope.save = function () {
        brandService.save($scope.entity).success(function (resp) {
            // 新增成功
            if (resp.success) {
                // 基于分页刷新列表
                $scope.reloadList();
            } else {
                alert(resp.message);
            }
        });
    }

    // 根据id获取单个品牌
    $scope.findOne = function (id) {
        brandService.findOne(id).success(function (resp) {
            if (resp) {
                $scope.entity = resp;
            }
        });
    }

    // 删除
    $scope.delete = function () {
        if (confirm("确定要删除这些记录：" + $scope.selectIds)) {
            brandService.delete($scope.selectIds).success(function (resp) {
                if (resp.success) {
                    // 基于分页刷新列表
                    $scope.reloadList();
                    // 删除完成后清空记录的id
                    $scope.selectIds = [];

                    $("#selall").prop("checked", false);
                } else {
                    alert(resp.message);
                }
            });
        }
    }

    // 刚进入页面，为了防止searchEntity变量还不存在，导致请求出现错误
    // 所以，将该变量先声明出来
    $scope.searchEntity = {};
    // 条件查询
    $scope.search = function (page, size) {
        brandService.search(page, size, $scope.searchEntity).success(function (resp) {
            $scope.paginationConf.totalItems = resp.total;
            $scope.list = resp.rows;
        });
    }

});