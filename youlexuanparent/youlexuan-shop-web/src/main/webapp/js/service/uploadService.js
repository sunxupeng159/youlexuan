//文件上传服务
app.service("uploadService", function($http) {
    this.uploadFile = function() {
        // h5新增类，实现表单数据的序列化
        var formData = new FormData();
        // key-value
        formData.append("file", file.files[0]);

        // 上传文件，需要设置更多的属性
        return $http({
            method : 'POST',
            url : "../upload.do",
            data : formData,
            headers : {
                'Content-Type' : undefined
            },
            transformRequest : angular.identity
        });
    }
});