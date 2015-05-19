var EDUI = angular.module('EDUI',  [
	'mgcrea.ngStrap',
	'ui.bootstrap',
	'EDUIServices',
	"ngCookies",
	'ngResource',
	'dateParser'
]);


var EDUIRESTURL="";
var mode="";

EDUI.config(function($httpProvider,$datepickerProvider) {
	$httpProvider.defaults.headers.common = {
		"x-tenantId": "single",
		"Content-Type": "application/json",
		"Accept": "application/json"
	};

	angular.extend($datepickerProvider.defaults, {
		dateFormat: 'mediumDate',
		dateType:"iso"
	});
});


EDUI.filter('orderObjects', function($dateParser) {
	return function(array,attribute) {
		if (array != undefined) {
			array.sort(function(a, b){
				a = $dateParser(a.grantTime, '');
				b = $dateParser(b.grantTime, '');
				return a - b;
			});
		}
		return array;
	}
})


EDUI.run(function($rootScope,$http,$cookies) {
	EDUIRESTURL = $cookies.EDUIRESTURL || '';

	$(document).on("click",".revokePopover button.cancel",function(e) {
		e.preventDefault();
		$(".deleteGrantByID").popover('hide');
	});

	$(document).on("click",".revokePopover button.revoke",function(e) {
		e.preventDefault();
		var grantID= $(this).parents(".popover").prev().attr("data-grant-id");
		$rootScope.$broadcast('grantDelete', grantID);
	});

	$(document).popover({
		trigger: 'click',
		selector:".deleteGrantByID",
		html: true,
		placement: 'left',
		content: '<div  class="revokePopover"><div class="body">Do you really want to revoke this Grant</div> <div class="actions"><button class="btn btn-primary btn-sm pull-right cancel">No</button><button class="btn btn-link btn-sm revoke">Yes</button></div></div></div>'
	});
});



EDUI.controller('restURLCtrl', ['$scope', '$cookies', '$location', function ($scope,$cookies,$location) {
	if (EDUIRESTURL == '') {
		EDUIRESTURL = $location.protocol() + '://' + $location.host() + ':' + $location.port() + '/entitlements-web/rest/';
// Local settings for debug from within jetty
//		EDUIRESTURL = $location.protocol() + '://' + $location.host() + ':9876/rest/';
	}
	$scope.resturl = EDUIRESTURL;
	$scope.save = function() {
		$cookies.EDUIRESTURL=$scope.resturl;
		location.reload(); // re-init controllers with new URL
	};
}]);



EDUI.controller('searchCtrl', function ($rootScope,$scope) {
	$scope.submitSearch = function() {
		$rootScope.$broadcast('search', $scope.search);
	};

	$rootScope.$on('submitSearch', function(e,query) {
		$scope.submitSearch();
	});
});



EDUI.controller('subMenuCtrl', function ($rootScope,$scope) {
	$scope.hideSubMenu = true;
	$scope.hideBack = true;
	$scope.modeStack = ['list'];

	$rootScope.$on('subMenu.headline', function(e,str) {
		$scope.headline = str;
		$scope.hideSubMenu = false;
	});

	$rootScope.$on('subMenu.showExecutor', function(e,status) {
		$scope.showExecutor = status;
	});

	$rootScope.$on('search', function(e,val) {
		if (val != undefined) {
			$scope.userid = val;
		}
	});

	$scope.addNewGrant = function() {
		$rootScope.$broadcast('mode', 'createGrant', $scope.userId);
	};

	$scope.isUserDefined = function() {
		return $scope.userid != null && $scope.userid != undefined && $scope.userid.length > 0;
	};

	$scope.back = function() {
		var stack = angular.copy($scope.modeStack);
		stack.splice(0, 1);
		$rootScope.$broadcast('mode', stack[0]);
		$scope.hideBack = stack.length <= 1;
		$scope.modeStack = stack;
	};

	$rootScope.$on('mode', function(e, val) {
		var isList = (val == 'list');
		$scope.hideBack = false;
		if (isList || val == 'check') {
			$scope.hideBack = true;
			$scope.modeStack = [];
		}
		$scope.modeStack.splice(0, 0, val);
		$scope.mode = val;
	});

	$scope.openExecutor = function(e, userid) {
		$rootScope.$broadcast('mode', 'check', userid);
	};

	$scope.openList = function(e, userid) {
		$rootScope.$broadcast('mode', 'list', userid);
	};

	$scope.addNewGrant = function(e) {
		$rootScope.$broadcast('mode', 'createGrant', $scope.userid);
	};

	$rootScope.$on('saveUpdateButtonTitle', function(e,str) {
		$scope.saveUpdateButtonTitle = str;
	});

	$scope.save = function() {
		$rootScope.$broadcast('grant.save');
	};

	$scope.reload = function() {
		if ($scope.mode == 'list') {
			$rootScope.$broadcast('list.reload');
		} else {
			$rootScope.$broadcast('grant.reload');
		}
	};

});



EDUI.controller('grantsListCtrl', function ($rootScope,$scope,grantsList,webservice) {
	$scope.visible = true;

	$rootScope.$on('search', function(e,query){
		if (query != undefined) {
			$scope.userid = query;
		}
		$rootScope.$broadcast('mode', 'list');
	});

	$rootScope.$on('mode', function(e,val) {
		if (val == 'list') {
			$scope.visible = true;
			$rootScope.$broadcast('subMenu.headline',"Grants for UserID " + $scope.userid);
			if ($scope.userid != null && $scope.userid != undefined && $scope.userid.length > 0) {
				$scope.reload($scope.userid);
			}
		} else {
			$scope.visible = false;
		}
	});

	//testing
	// $scope.grants = grantsList.query({userId: "8d859c120a719045af0b8105d9ff622dae"})
	// $rootScope.$broadcast('subMenu.headline',"Entitlements for UserID 8d859c120a719045af0b8105d9ff622dae");

	$rootScope.$on('list.reload', function() {
		$scope.reload();
	});

	$scope.isUserDefined = function() {
		return $scope.userid != null && $scope.userid != undefined && $scope.userid.length > 0;
	};

	$scope.reload = function() {
		$scope.grants = grantsList.query({userId: $scope.userid}, function(){}, errorHandler);
	};

	$scope.toggleStatus =function(id,status){
		var s = (status!="ACTIVE");
		webservice.setGrantStatus(id,s);
	};

	$scope.showDetail = function(id) {
		$rootScope.$broadcast('mode', 'editGrant', id);
	};

	$scope.addNewGrant = function() {
		$rootScope.$broadcast('mode', 'createGrant', $scope.userid);
	};
});



EDUI.controller('executionCtrl', function($rootScope,$scope,webservice) {
	$scope.visible = false;
	$scope.criteria = [];
	$scope.entitlementType = '';
	$scope.userId = '';
	$scope.affected = [];
	$scope.errorFields = [];

	var criterionTemplates = [
		{type: 'geo', properties: {entry: [{key: 'geoPath', value: '//'}]}},
		{type: 'string', properties: {entry: [{key: 'string', value: null}]}},
		{type: 'metered', properties: {entry: [{key: 'quantity', value: 1}]}},
		{type: 'timeframe', properties: {entry: [{key: 'time', value: new Date()}]}},
		{type: 'path', properties: {entry: [{key: 'file', value: null}]}}
	];

	$scope.openExecutor = function(userId) {
		$scope.userId = userId;
		$rootScope.$broadcast('mode', 'check');
	};

	$scope.knownConditionTypes = function() {
		return criterionTemplates;
	};

	$scope.getProperty = function(propertyAwareEntity, name, defValue) {
		return getProperty(propertyAwareEntity, name, defValue);
	};

	$scope.addCriterion = function(template) {
		for (var i = 0; i < criterionTemplates.length; i++) {
			if (criterionTemplates[i].type == template.type) {
				var instance = angular.copy(criterionTemplates[i]);
				// Do ordered insertion instead.
				// We cannot order criteria dynamically in view, because then we loose indexes.
				//$scope.criteria.push(instance);
				var at = $scope.criteria.length;
				for (var j = 0; j < at; j++) {
					if ($scope.criteria[j].type > instance.type) {
						at = j;
						break;
					}
				}
				$scope.criteria.splice(at, 0, instance);
				$scope.newCriterion = '';
				break;
			}
		}
	};

	$scope.removeCriterion = function(index) {
		$scope.criteria.splice(index, 1);
	};

	$rootScope.$on('mode', function(e,val, userId) {
		if (val == 'check') {
			if (userId != null && userId != undefined) {
				$scope.userId = userId;
				$scope.criteria = [];
				$scope.entitlementType = '';
				$scope.affected = { result: false, grantDataList: [] };
				$scope.errorFields = [];
			}
			$scope.visible = true;
			$rootScope.$broadcast('subMenu.headline', "Execute use/check for UserID " + $scope.userId);
			if ($scope.entitlementType.length > 0) {
				$scope.check(true);
			}
		} else {
			$scope.visible = false;
		}
	});

	$rootScope.$on('search', function(e, val) {
		if (val != undefined) {
			$scope.userId = val;
		}
	});

	$scope.getGeoParts = function(string) {
		return extractGeoParts(string);
	};

	$scope.setGeoParts = function(parts) {
		return constructGeoLocation(parts);
	};

	$scope.check = function(quiet) {
		$scope.execute('check', quiet);
	};

	$scope.use = function() {
		$scope.execute('use');
	};

	$scope.execute = function(action, quiet) {
		webservice.check($scope.userId, $scope.entitlementType, action, $scope.criteria, function(result) {
			$scope.errorFields = [];
			if (!quiet) {
				if (result.result) {
					addAlert('info', 'Access granted');
				} else {
					addAlert('warning', 'Denied. There are no grants fitting given criteria.');
				}
			}
			$scope.affected = result;
		})
	};

	$scope.getAffectedGrants = function() {
		return $scope.affected.grantDataList;
	};

	$scope.isEntitlementGranted = function() {
		return $scope.affected.result;
	}

	$scope.showDetail = function(grantId) {
		$rootScope.$broadcast('mode', 'editGrant', grantId);
	};

	$scope.getProperty = function(entity, name, defValue) {
		return getProperty(entity, name, defValue);
	};

	$scope.getCondition = function(grant, type) {
		for (var i = 0; i < grant.conditions.length; i++) {
			if (grant.conditions[i].type == 'metered') {
				return grant.conditions[i];
			}
		}
		return null;
	};

	$rootScope.$on('invalidField', function(e, name, message) {
		$scope.errorFields.push({field: name, message: message});
	});

	$scope.isValid = function(field) {
		for (var i = $scope.errorFields.length-1; i >= 0; i--) {
			if ($scope.errorFields[i].field == field) {
				return false;
			}
		}
		return true;
	};

	$scope.dateOptions = {
		formatYear: 'yy',
		startingDay: 1
	};

	$scope.initDate = new Date();

	$scope.formats = ['dd-MMMM-yyyy', 'yyyy/MM/dd', 'dd.MM.yyyy', 'shortDate', 'mediumDate'];
	$scope.format = $scope.formats[4];

});



EDUI.controller('grantDetailCtrl', function ($rootScope,$scope,grantDetail,webservice) {
	var grantTemplate={
		id: null,
		userId: null,
		grantSource: null,
		grantSourceId: null,
		grantTime: null,
		entitlementType: null,
		status: 'ACTIVE',
		conditions: []
	};

	$scope.errorFields = [];

	$scope.visible = false;
	// $scope.newGrantBoolean=true
	// $scope.grant=grantTemplate

	// $scope.newGrantBoolean=false				
	// $scope.grant=grantDetail.get({grantId:"d00652dd-8888-4aa3-9e75-10f86a47dab0"})

	$rootScope.$on('mode', function(e, mode, id) {
		if (mode == 'editGrant') {
			$scope.visible = true;
			$scope.newGrantBoolean=false;
			$scope.conditions = angular.copy(conditions);
			$scope.conditionsModified = false;
			$scope.newConditions = "";
			$scope.grant= grantDetail.get({grantId:id},function(){
				$rootScope.$broadcast('subMenu.headline', $scope.grant.entitlementType+" for UserID "+$scope.grant.userId);
				$rootScope.$broadcast('saveUpdateButtonTitle', "Update");
				$scope.removeEmptyProperties($scope.grant);
			});
			$scope.showRefresh=true;
			$scope.showProperties=true;
			$scope.errorFields = [];
		} else if (mode == 'createGrant') {
			$scope.visible = true;
			var tpl = angular.copy(grantTemplate);
			$scope.newGrantBoolean=true;
			tpl.userId=id;
			$scope.grant=tpl;
			$scope.conditions = angular.copy(conditions);
			$scope.conditionsModified = false;
			$scope.newConditions = "";
			$rootScope.$broadcast('subMenu.headline', "Add new Grant for UserID "+id);
			$rootScope.$broadcast('saveUpdateButtonTitle', "Grant");
			$scope.showRefresh=false;
			$scope.showProperties=false;
			$scope.errorFields = [];
		} else {
			$scope.visible = false;
		}
	});

	$rootScope.$on('saveUpdateButtonTitle', function(e,str) {
		$scope.saveUpdateButtonTitle = str;
	});

	$rootScope.$on('invalidField', function(e, name, message) {
		$scope.errorFields.push({field: name, message: message});
	});

	$rootScope.$on('grant.save', function(e) {
		$scope.save();
	});

	$rootScope.$on('grant.reload', function(e) {
		$scope.reload();
	});

	// revoke grant
	$rootScope.$on('grantDelete', function(e, id) {
		grantDetail.revoke({grantId: id}, function() {
			$scope.grant= null;
			$scope.propModifications = [];
			$rootScope.$broadcast('submitSearch');
		})
	});

	$scope.grant = {};
	$scope.newProperty={};

	$scope.today = function() {
		$scope.dt = new Date();
	};
	$scope.today();

	$scope.clear = function () {
		$scope.dt = null;
	};

	// Disable weekend selection
	$scope.disabled = function(date, mode) {
		return ( mode === 'day' && ( date.getDay() === 0 || date.getDay() === 6 ) );
	};

	$scope.dateOptions = {
		formatYear: 'yy',
		startingDay: 1
	};

	$scope.initDate = new Date();

	$scope.formats = ['dd-MMMM-yyyy', 'yyyy/MM/dd', 'dd.MM.yyyy', 'shortDate', 'mediumDate'];
	$scope.format = $scope.formats[4];

	$scope.isValid = function(field) {
		for (var i = $scope.errorFields.length-1; i >= 0; i--) {
			if ($scope.errorFields[i].field == field) {
				return false;
			}
		}
		return true;
	};

	$scope.updateProperty = function(grant, index) {
		if (!grant || !grant.properties || index >= grant.properties.entry.length) {
			$scope.updateConditions($scope.grant.id, function() {
				$rootScope.$broadcast('submitSearch');
			});
			return;
		};

		webservice.updatePropertyForGrant(grant.id, grant.properties.entry[index].key, grant.properties.entry[index].value
			,
			function(g) {
				$scope.updateProperty(grant, index+1);
			}
		);
	};

	$scope.save = function() {
		$scope.errorFields = [];
		if($scope.grant.id) {
			$scope.updateProperty($scope.grant, 0);
		} else {
			webservice.addNewGrant($scope.grant, function(grant) {
				$scope.grant = angular.copy(grant);
				$rootScope.$broadcast('mode', 'list');
			});
		}
	};

	$scope.getProperty = function(propertyAwareEntity, name, defValue) {
		return getProperty(propertyAwareEntity, name, defValue);
	};

	$scope.setRemainingQuantity = function(grantId, value) {
		$scope.errorFields = [];
		webservice.updatePropertyForGrant(grantId, 'remainingQuantity', value, function(g) {
			getProperty($scope.grant, 'remainingQuantity').value = getProperty(g, 'remainingQuantity').value;
			addAlert('info', 'Remaining quantity was updated on server');
		});
	};

	$scope.addRemainingQuantity = function(grantId, offset) {
		$scope.errorFields = [];
		webservice.addPropertyForGrant(grantId, 'remainingQuantity', offset, function(g) {
			getProperty($scope.grant, 'remainingQuantity').value = getProperty(g, 'remainingQuantity').value;
			addAlert('info', 'Remaining quantity was updated on server');
		});
	};

	$scope.reload = function() {
		$scope.errorFields = [];
		$scope.grant= grantDetail.get({grantId:$scope.grant.id});
		$scope.conditions = angular.copy(conditions);
		$scope.conditionsModified = false;
		$scope.newConditions = "";
		$scope.removeEmptyProperties($scope.grant);
	};

	$scope.removeEmptyProperties = function(grant) {
		if (grant.properties != undefined && grant.properties.entry != undefined) {
			for (var i = grant.properties.entry.length - 1; i >= 0; i--) {
				if (grant.properties.entry[i].value == null) {
					grant.properties.entry.splice(i, 1);
				}
			}
		}
	};

	var conditions= [
		{properties:{entry:[{key:"path",value:null}]},type:"path"},
		{properties:{entry:[{key:"geoPath",value:null}]},type:"geo"},
		{properties:{entry:[{key:"string",value:null}]},type:"string"},
		{properties:{entry:[{key:"allowOverage",value:"false"},{key:"maxQuantity",value:null}]},type:"metered"},
		{properties:{entry:[{key:"startTime",value:null},{key:"endTime",value:null}]},type:"timeframe"}
	];

	$scope.conditions = angular.copy(conditions);
	$scope.conditionsModified = false;
	$scope.newConditions = "";
	$scope.propModifications = [];

	$scope.conditionModified = function() {
		$scope.conditionsModified = true;
	};

	$scope.updateConditions = function(id, successFunc) {
		webservice.saveConditionForGrant(id, {conditions: $scope.grant.conditions}, function(g) {
			$scope.grant.conditions = g.conditions;
			$scope.conditionsModified = false;
			$scope.newConditions = "";
			if (successFunc) {
				successFunc(g);
			}
		});
	};

	$scope.addConditions = function(data) {
		if (data) {
			$scope.grant.conditions.push(data);
			$scope.conditionsModified = true;
		}
	};

	$scope.removeCondition =function(id,type){
		for (var i = $scope.grant.conditions.length - 1; i >= 0; i--) {
			var item = $scope.grant.conditions[i];
			if (item.type == type) {
				$scope.grant.conditions.splice(i, 1);
				$scope.conditionsModified = true;
				$scope.newConditions = '';
				break;
			}
		}
	};

	$scope.filterConditions = function() {
		// filter buggy :(
		if(!$scope.grant){
			return null;
		}

		var conOJB = angular.copy($scope.grant.conditions);
		var arry= [];

		for (var i = $scope.conditions.length - 1; i >= 0; i--) {
			var found = false;
			if ($scope.grant.conditions) {
				var tp = $scope.conditions[i].type;
				for (var j = $scope.grant.conditions.length - 1; j >= 0; j--) {
					if (tp == $scope.grant.conditions[j].type) {
						found = true;
						break;
					}
				}
			}
			if (!found) {
				arry.push($scope.conditions[i]);
			}
		}
		return arry;
	};

	$scope.getGeoParts = function(string){
		var res = [];
		angular.forEach((string || '').split(','), function(value) {
			res.push(extractGeoParts(value));
		});
		return res;
	};

	$scope.parts=[];

	$scope.setGeoParts = function(OBJ) {
		var res = [];
		angular.forEach(OBJ,function(value,key) {
			res.push(constructGeoLocation(value));
		});
		return res.join(",");
	};

	$scope.removeGeoLine = function(index,parts){
		for (var i = $scope.grant.conditions.length - 1; i >= 0; i--) {
			if ($scope.grant.conditions[i].type == 'geo') {
				var value= $scope.grant.conditions[i].properties.entry[0].value;
				var v=value.split(",");
				v.splice(index,1);
				value=v.join(",");
				$scope.grant.conditions[i].properties.entry[0].value=value;
				parts.splice(index,1);
				$scope.conditionModified();
				break;
			}
		}
	};

	$scope.addGeoLine= function(parts){
		parts.push([
			{key: 'Country', value: ''},
			{key: 'County', value: ''},
			{key: 'City', value: ''}
		]);
		$scope.conditionModified();
	};
});


function errorHandler(e) {
	if (e.data.length == 0) {
		addAlert('danger', '<strong>Connection error</strong>');
	}
}

function addAlert(type,content) {
	var block = $('#alert');
	block.removeClass();
	block.addClass('alert alert-' + type + ' fade in');
	block.html(content);
	showAlert(block);
}

function showAlert(block) {
	block.attr('style', "display:block");
	setTimeout(function() {
		block.attr('style', "display:none");
	}, 2000)
}

function getProperty(entity, name, defValue) {
	if (!entity || !entity.properties || !entity.properties.entry) return null;
	for (var i = entity.properties.entry.length-1; i >= 0; i--) {
		if (entity.properties.entry[i].key == name) {
			return entity.properties.entry[i];
		}
	}
	if (defValue != undefined) {
		var res = { key: name, value: defValue };
		entity.properties.entry.push(res);
		return res;
	}
	return null;
}

String.prototype.trimRight = function(charlist) {
	if (charlist === undefined)
		charlist = "\s";
	return this.replace(new RegExp("[" + charlist + "]+$"), "");
};

function constructGeoLocation(object) {
	var res = (object[0].value || '') + '/' + (object[1].value || '') + '/' + (object[2].value || '');
	return res.trimRight('/');
}

function extractGeoParts(str) {
	var sValue = str.split('/');
	return [
		{key: 'Country', value: sValue[0] || ''},
		{key: 'County', value: sValue[1] || ''},
		{key: 'City', value: sValue[2] || ''}
	]
}

