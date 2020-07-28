app.controller('searchController',function($scope,searchService,$location){
    //搜索
    $scope.search=function(){
        $scope.searchMap.pageNo = parseInt($scope.searchMap.pageNo);
        searchService.search( $scope.searchMap ).success(
            function(response){
                $scope.resultMap=response;//搜索返回的结果
                buildPageLabel();//调用
            }
        );
    }


//添加搜索项
    $scope.addSearchItem=function(key, value) {
        if (key == 'category' || key == 'brand'|| key=='price') {//如果点击的是分类或者是品牌
            $scope.searchMap[key] = value;
        } else {
            $scope.searchMap.spec[key] = value;
        }
        $scope.search();//执行搜索
    }

    //移除复合搜索条件
    $scope.removeSearchItem=function(key){
        if(key=="category" ||  key=="brand"|| key=='price'){//如果是分类或品牌
            $scope.searchMap[key]="";
        }else{//否则是规格
            delete $scope.searchMap.spec[key];//移除此属性
        }
        $scope.search();//执行搜索
    }
    $scope.searchMap={'keywords':'','category':'','brand':'','spec':{},'price':'','pageNo':1,'pageSize':20,'sortField':'','sort':''};
    //构建分页标签
    buildPageLabel = function() {
        $scope.pageLabel=[];
        var maxPageNo= $scope.resultMap.totalPages;
        var firstPage=1;
        var lastPage =maxPageNo;
        $scope.firstDot=true;//前面有点
        $scope.lastDot=true;//后边有点

        if (maxPageNo>5){
            if($scope.searchMap.pageNo<=3){
                lastPage=5;
                $scope.firstDot=false;//前面没点
            }else if($scope.searchMap.pageNo>=maxPageNo-2){
                firstPage=maxPageNo-4;
                $scope.lastDot=false;//后边没点
            }else{
                firstPage=$scope.searchMap.pageNo-2;
                lastPage=$scope.searchMap.pageNo+2;
            }
        }else{
            $scope.firstDot=false;//前面无点
            $scope.lastDot=false;//后边无点
        }
        for (var i=firstPage; i<=lastPage; i++){
            $scope.pageLabel.push(i)
        }
    }

    //根据页码查询
    $scope.queryByPage=function(pageNo){
        //页码验证
        if(pageNo<1 || pageNo > $scope.resultMap.totalPages){
            return;
        }
        $scope.searchMap.pageNo=pageNo;
        $scope.search();
    }
    //设置排序规则
    $scope.sortSearch=function (sortField,sort) {
        $scope.searchMap.sortField=sortField;
        $scope.searchMap.sort=sort;
        $scope.search();
    }

    $scope.loadkeywords=function(){
        $scope.searchMap.keywords=  $location.search()['keywords'];
        $scope.search();
    }
});