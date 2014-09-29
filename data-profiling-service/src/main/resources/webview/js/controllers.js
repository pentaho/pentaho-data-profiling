'use-strict';

define([
    "require",
    "common-ui/angular",
    "common-ui/angular-route",
    "common-ui/angular-translate",
    "common-ui/angular-translate-loader-partial",
    "./services",
    "com.pentaho.profiling.notification.service"
  ], function(require, angular) {

  var appControllers = angular.module('appControllers', [
    'NotificationServiceModule',
    'ngRoute',
    'pascalprecht.translate']);

  appControllers.controller('AppController', [
    '$scope',
    '$routeParams',
    'Profile',
    'DataSourceService',
    'NotificationService',
    '$translate',
    '$translatePartialLoader',
    function($scope, $routeParams, profileService, dataSourceService, notificationService, $translate, $translatePartialLoader) {
      $scope.orderByCol = JSON.stringify(["name"]);
      $scope.isOrderReversed = false;

      // -----
      // Methods accessible by the view, through $scope.

      /**
       * Starts an operation on the scope object's profile, given its id.
       *
       * This method is published in the scope object and can thus be called by the view.
       *
       * @param {string} operationId The id of the operation to start.
       */
      $scope.startOperation = function(operationId) {
        profileService.start({profileId: $scope.profileId, operationId: operationId});
      };

      /**
       * Orders to stop the current operation on the scope object's profile.
       *
       * This method is published in the scope object and can thus be called by the view.
       */
      $scope.stopCurrentOperation = function() {
        profileService.stop({profileId: $scope.profileId});
      };

      /**
       * Called to change the column by which field rows are sorted.
       *
       * This method is published in the scope object and can thus be called by the view.
       *
       * @param {Column} col The column by which to sort rows.
       */
      $scope.onOrderByCol = function(col) {
        // Cycles through: Ascending -> Descending

        if($scope.orderByCol === col.stringifiedPath) {
          $scope.isOrderReversed = !$scope.isOrderReversed;
        } else {
          $scope.orderByCol = col.stringifiedPath;
          $scope.isOrderReversed = false;
        }
      };

      /**
       * Gets the order by value of a row.
       *
       * This method is published in the scope object and can thus be called by the view.
       *
       * @param {Row} row The row.
       * @return {Object} The order by value.
       */
      $scope.orderByKey = function(row) {
        return row[$scope.orderByCol];
      };

      // Register to receive profile status updates.
      notificationService.register(
        /* notifType */
        "com.pentaho.profiling.model.ProfileNotificationProvider",
        /* ids */
        [$routeParams.profileId],
        /* cb */
        function(profileStatus) { updateProfile(profileStatus); });

      // -----
      // Methods not-accessible to the view.

      /**
       * Updates the profile information with the given profile status object.
       *
       * This function is called each time a profile status update
       * is received by the profile notification service.
       *
       * It handles:
       * <ul>
       *   <li>obtaining available operations,</li>
       *   <li>determining the rows and columns structures corresponding to the fields in the profile,</li>
       *   <li>any errors reported by the profiling service.</li>
       * </ul>
       *
       * @param {ProfileStatus} profileStatus The profile status.
       */
      function updateProfile(profileStatus) {
        if(profileStatus) {
          $scope.profileId = profileStatus.id;

          // Load available operations asynchronously.
          profileService.getOperations({profileId: profileStatus.id}, function(operations) {
            $scope.operations = operations;
          });

          // Update datasource.
          updateDataSource(profileStatus.dataSourceReference);

          var itemSchema = {values: {}},
              cols = toArray(profileStatus.profileFieldProperties),
              colCount = cols && cols.length;

          if(colCount) {
            cols.forEach(function(col, index) {
              ensureTranslationPart(col.namePath);

              var colPropPath = col.pathToProperty;

              // Easily detect columns/leafs when traversing the index.
              col.isColumn = true;
              col.index = index;
              col.stringifiedPath = JSON.stringify(colPropPath);

              // Index the col by its colPropPath steps.
              setPath(itemSchema.values, colPropPath, col);
            });
          }

          var items = toArray(profileStatus.fields);

          $scope.fieldCols = getCols(itemSchema, items, colCount);
          $scope.fieldRows = getRows(itemSchema, items);

          var currentOper = $scope.currentOperation = profileStatus.currentOperation;
          if(currentOper) ensureTranslationPart(currentOper.messagePath);

          var operError = $scope.operationError = profileStatus.operationError;
          if(operError) {
            ensureTranslationPart(operError.message.messagePath);

            if(operError.recoveryOperations) {
              operError.recoveryOperations.forEach(function(recoveryOperation) {
                ensureTranslationPart(recoveryOperation.namePath);
              });
            }
          }
        } else {
          dataSourceService.getCreate({
              id:                 $scope.dataSourceReference.id,
              dataSourceProvider: $scope.dataSourceReference.dataSourceProvider
            }, function(createWrapper) {
              window.location.href = createWrapper.profileDataSourceInclude.url;
            });
        }
      }

      /**
       * Shows the "info" view of the given data source reference.
       *
       * When the given data source reference is different from the current data source reference,
       * the data source <i>getInclude</i> service is called to obtain
       * the new data source's requireJS module and view url.
       *
       * @param {DataSourceReference} dataSourceReference The data source reference.
       */
      function updateDataSource(dataSourceReference) {
        var oldDsr  = $scope.dataSourceReference,
          newDsId   = dataSourceReference.id,
          newDsProv = dataSourceReference.dataSourceProvider;

        if(!oldDsr || oldDsr.id != newDsId) {
          $scope.dataSourceReference = dataSourceReference;

          if(!oldDsr || oldDsr.dataSourceProvider != newDsProv) {
            dataSourceService.getInclude({id: newDsId, dataSourceProvider: newDsProv}, function(dsIncludeWrapper) {

              var dsInclude = dsIncludeWrapper.profileDataSourceInclude;
              if(dsInclude.require) {
                require([dsInclude.require], function() {
                  $scope.dataSourceUrl = dsInclude.url;
                  $scope.$apply();
                });
              } else {
                $scope.dataSourceUrl = dsInclude.url;
              }
            });
          }
        }
      }

      /**
       * Ensures that a given translation part, given its path, is loaded.
       *
       * @param {string} partPath The path of the translation part.
       */
      function ensureTranslationPart(partPath) {
        if(!$translatePartialLoader.isPartAvailable(partPath))
          $translatePartialLoader.addPart(partPath);
      }
    }
  ]);

  return appControllers;

  // -----

  /**
   * Obtains the columns for which the given items have values.
   *
   * The returned columns are in the order given by their <i>index</i> property.
   *
   * @param {Object} rootItemSchema The item schema containing columns, indexed under their property paths.
   * @param {Object[]} rootItems The items.
   * @param {number} colCount The total number of columns in <i>rootItemSchema</i>.
   * This allows early exit, as soon as every column is found to be present.
   *
   * @return {Column[]} The present columns.
   */
  function getCols(rootItemSchema, rootItems, colCount) {
    // Each column is only output once.
    var seenCols = {},
        usedCols = [];

    function recursive(itemSchema, item) {
      if(Array.isArray(item)) {
        for(var i = 0, L = item.length; i < L; i++) {
          if(recursive(itemSchema, item[i]) === false) return false;
        }
      } else {
        var childSchema;
        for(var propName in item) {
          if((childSchema = itemSchema[propName])) {
            if(childSchema.isColumn) {
              if(!seenCols[childSchema.index]) {
                seenCols[childSchema.index] = 1;

                usedCols.push(childSchema);

                // No use in traversing all rows if all possible columns
                // have already been output.
                if(usedCols.length >= colCount) return false;
              }
            } else {
              if(recursive(childSchema, item[propName]) === false) return false;
            }
          }
        }
      }
    }

    if(rootItems && colCount) recursive(rootItemSchema, rootItems);

    // Make sure columns are in the original order.
    return usedCols.sort(function(a, b) {
      return compare(a.index, b.index);
    });
  }

  /**
   * Obtains rows representing the given items in a tabular form.
   *
   * Rows have one property for each column present in the given item schema,
   * named with the column's <i>stringifiedPath</i>.
   *
   * For each given root item,
   * one row will be output for each combination of the different values
   * on the columns present in <i>rootItemSchema</i>.
   *
   * @param {Object} rootItemSchema The item schema containing columns, indexed under their property paths.
   * @param {Object[]} rootItems The items.
   * @return {Row[]} The rows.
   */
  function getRows(rootItemSchema, rootItems) {

    function flatten(itemSchema, item, rows) {
      if(Array.isArray(item)) {
        var L = item.length;
        if(L === 1) {
          rows = flatten(itemSchema, item[0], rows);
        } else if(L > 1) {
          var newRows = [],
            copyRows = function() {
              return rows.map(function(row) { return angular.copy(row); });
            };

          for(var i = 0; i < L - 1; i++)
            append(newRows, flatten(itemSchema, item[i], copyRows()));

          rows = append(newRows, flatten(itemSchema, item[L - 1], rows));
        }
      } else if(item) {
        for(var propName in itemSchema) {
          if(propName in item) {
            var childItemSchema = itemSchema[propName],
              childItem = item[propName];
            if(childItemSchema.isColumn) {
              var colStrPath = childItemSchema.stringifiedPath;
              rows.forEach(function(row) { row[colStrPath] = childItem; });
            } else {
              rows = flatten(childItemSchema, childItem, rows);
            }
          }
        }
      }

      return rows;
    }

    return flatten(rootItemSchema, rootItems, [{}]);
  }

  // -----
  // UTIL

  function toArray(t) {
    return (t == null || Array.isArray(t)) ? t : [t];
  }

  function getLazyMap(o, p) {
    return o[p] || (o[p] = {});
  }

  function setPath(o, path, value) {
    for(var i = 0, P = path.length; i < P - 1; i++)
      o = getLazyMap(o, path[i]);

    o[path[P - 1]] = value;
  }

  function append(a1, a2) {
    for(var i = 0, L = a2.length; i < L; i++) a1.push(a2[i]);
    return a1;
  }

  function compare(a, b) {
    return a > b ? 1 : a < b ? -1 : 0;
  }
});