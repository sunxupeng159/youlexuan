//item_cat服务层
app.service('itemCatService', function($http){
	// 保存、修改
	this.save = function(entity, parentId) {
		var methodName = 'add'; 	// 方法名称
		if (entity.id != null) { 	// 如果有ID
			methodName = 'update'; 	// 则执行修改方法
		} else {
			entity.parentId = parentId;
		}

		return $http.post('../itemCat/' + methodName + '.do', entity);
	}

	// 查询单个实体
	this.findOne = function(id) {
		return $http.get('../itemCat/findOne.do?id=' + id);
	}
//查询全部用于显示分类名
	this.findAll = function() {
		return $http.get('../itemCat/findAll.do');
	}

	// 批量删除
	this.dele = function(ids) {
		// 获取选中的复选框
		return $http.get('../itemCat/delete.do?ids=' + ids);
	}

	// 查询
	this.search = function(page, size, searchEntity) {
		// post提交，page、size属性和之前相同，将searchEntity提交至后台@RequestBody对应的属性
		return $http.post('../itemCat/search.do?page=' + page + '&size=' + size,
				searchEntity);
	}
	//下拉列表数据
	this.selectOptionList=function(){
		return $http.get('../typeTemplate/selectOptionList.do');
	}
	this.findByParentId=function(parentId){
		return $http.get('../itemCat/findByParentId.do?parentId='+parentId);
	}

	this.findAll = function() {
		return $http.get('../itemCat/findAll.do');
	}
});