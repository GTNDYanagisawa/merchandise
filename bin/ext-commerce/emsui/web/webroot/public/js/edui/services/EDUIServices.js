var EDUIServices = angular.module('EDUIServices', ['ngResource']);

EDUIServices.factory('grantsList', ['$resource',
	function($resource) {
		return $resource(EDUIRESTURL+'grants/userId/:userId', {}, {
		query: {method:'GET'}
	});
}]);

EDUIServices.factory('grantDetail', ['$resource', function($resource) {
		return $resource(EDUIRESTURL+'grants/:grantId', {}, {
		get: {method:'GET'},
		revoke: {method:'DELETE'}
	});
}]);

EDUIServices.factory('executionCtrl', ['$resource', function($resource) {
	return $resource(EDUIRESTURL + 'grants/userId/:userId/execute', {}, {
		do: {method: 'PUT'}
	});
}]);


EDUIServices.service('webservice',  ['$rootScope', '$resource', function($rootScope,$resource) {
	var GrantStatusRes = $resource(EDUIRESTURL+'grants/:grantId/status',null,
		{
			set: {method:'PUT'}
		}
	);
	var GrantConditions = $resource(EDUIRESTURL+'grants/:grantId/conditions/:conditionType',null,{});
	var GrantProperties = $resource(EDUIRESTURL+'grants/:grantId/properties/:key',null,{'update': {method:'PUT'}});
	var grants = $resource(EDUIRESTURL+'grants',null,{});
	var GrantExecutor = $resource(
		EDUIRESTURL+'grants/userId/:userId/execute?action=:action&details=true&entitlementType=:entitlementType',
		null, {do:{method: 'PUT'}});

	this.setGrantStatus = function(id, active) {
		var OBJ;
		if (active) {
			SOBJ = { "status" : "ACTIVE" }
		} else {
			SOBJ = { "status" : "SUSPENDED" }
		}

		GrantStatusRes.set({grantId:id},SOBJ,function() {
			$rootScope.$broadcast('submitSearch');
		});		
	};

	this.check  = function(userId, entitlementType, action, criteria, callback) {
		GrantExecutor.do(
			{userId: userId, action: action, entitlementType: entitlementType},
			{criterions: criteria}, callback, this.errorFunction);
	};

	this.saveConditionForGrant = function(id,conditions,callback) {
		GrantConditions.save({grantId:id},conditions,callback,this.errorFunction);
	};

	this.setPropertyForGrant = function(id,property,value,callback) {
		GrantProperties.save({grantId:id,key:property},value,callback,this.errorFunction);
	};

	this.removePropertyForGrant = function(id,property,callback) {
		GrantProperties.remove({grantId:id,key:property},null,callback,this.errorFunction);
	};

	this.updatePropertyForGrant = function(id,property,value,callback) {
		GrantProperties.update({grantId:id,key:property},{relative:false,value:value},callback,this.errorFunction);
	};

	this.addPropertyForGrant = function(id,property,offset,callback) {
		GrantProperties.update({grantId:id,key:property},{relative:true,value:offset},callback,this.errorFunction);
	};

	this.addNewGrant = function(value,callback) {
		grants.save(null,value,callback,this.errorFunction);
	};

	this.broadcastErrors = function(message) {
		var field;
/*
		while (field = /\b([^= \t\n\r]+)(?:=)/g.exec(message)) {
			EDUI.$rootScope.$broadcast('invalidField', 'test', field);
		}
*/
		var test = message.match(/\b([^= \t\n\r]+)(?:=)/g);
		for (var i = 0; i < test.length; i++) {
			EDUI.$rootScope.$broadcast('invalidField', 'test', test[i]);
		}
	};

	this.errorFunction = function(event) {
		if (event.status == 412) {
			var test = event.data.match(/\b([^= \t\n\r\]]+)(?==)/g);
            if (test == null) {
                $rootScope.$broadcast('invalidField', event.data, '');
            } else {
                for (var i = 0; i < test.length; i++) {
                    $rootScope.$broadcast('invalidField', test[i], '');
                }
            }
			addAlert("danger","<strong>"+event.status+"</strong> "+event.data)
		} else if (event.status == 405) {
			addAlert("danger","<strong>"+event.status+"</strong> ")
		} else {
			addAlert('danger', '<strong>Connection error</strong>');
		}
	}
}]);

