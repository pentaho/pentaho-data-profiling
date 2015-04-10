/*!
 * PENTAHO CORPORATION PROPRIETARY AND CONFIDENTIAL
 *
 * Copyright 2002 - 2015 Pentaho Corporation (Pentaho). All rights reserved.
 *
 * NOTICE: All information including source code contained herein is, and
 * remains the sole property of Pentaho and its licensors. The intellectual
 * and technical concepts contained herein are proprietary and confidential
 * to, and are trade secrets of Pentaho and may be covered by U.S. and foreign
 * patents, or patents in process, and are protected by trade secret and
 * copyright laws. The receipt or possession of this source code and/or related
 * information does not convey or imply any rights to reproduce, disclose or
 * distribute its contents, or to manufacture, use, or sell anything that it
 * may describe, in whole or in part. Any reproduction, modification, distribution,
 * or public display of this information without the express written authorization
 * from Pentaho is strictly prohibited and in violation of applicable laws and
 * international treaties. Access to the source code contained herein is strictly
 * prohibited to anyone except those individuals and entities who have executed
 * confidentiality and non-disclosure agreements or other agreements with Pentaho,
 * explicitly covering such access.
 */

define(['./services'], function (appServices) {
  appServices.factory('TabularViewService', [
    function () {
      function TabularViewService() {
        this.fieldCols;
        this.fieldRows;
        this.orderByCol;
        this.isOrderReversed;
        this.selectedTab;
        this.stateFormDisplay = true;
      }

      TabularViewService.prototype = {
        constructor: TabularViewService,
        init: function (col, order) {
          tabularViewService.fieldCols = [];
          tabularViewService.fieldRows = [];
          tabularViewService.orderByCol = col;
          tabularViewService.isOrderReversed = order;
          tabularViewService.selectedTab = 'aggregate';
        },
        buildItemSchemaRecursive: function (itemSchema, propPath, col) {
          var propName;

          for (var i = 0, P = propPath.length; i < P - 1; i++) {
            if (itemSchema.isColumn) throw new Error("Invalid property.");

            propName = propPath[i];

            var childItemSchema = itemSchema.propsByName[propName];
            if (!childItemSchema) {
              childItemSchema = {
                name: propName,
                props: [],
                propsByName: {}
              };
              itemSchema.props.push(childItemSchema);
              itemSchema.propsByName[propName] = childItemSchema;
            }

            itemSchema = childItemSchema;
          }

          propName = propPath[P - 1];

          if (itemSchema.isColumn || itemSchema.propsByName[propName]) throw new Error("Invalid property.");

          col.name = propName;
          itemSchema.props.push(col);
          itemSchema.propsByName[propName] = col;
        },
        buildItemSchema: function (cols) {

          var valuesItemSchema = {
                name: 'values',
                props: [],
                propsByName: {}
              },
              rootItemSchema = {
                // name: ''
                props: [
                  valuesItemSchema
                ],
                propsByName: {
                  'values': valuesItemSchema
                }
              };

          if (cols) {
            cols.forEach(function (col, index) {
              var propPath = col.pathToProperty;

              // Easily detect columns/leafs when traversing the item schema.
              col.isColumn = true;
              col.display = true;
              col.index = index;
              col.stringifiedPath = JSON.stringify(propPath);

              // Index the col by its colPropPath steps.
              tabularViewService.buildItemSchemaRecursive(valuesItemSchema, propPath, col);
            });
          }

          return rootItemSchema;
        },
        /**
         * Obtains the columns for which the given items have values.
         *
         * The returned columns are in the order given by their <i>index</i> property.
         *
         * @param {ItemSchema} rootItemSchema The item schema containing columns, indexed under their property paths.
         * @param {Object[]} rootItems The items.
         * @param {number} colCount The total number of columns in <i>rootItemSchema</i>.
         * This allows early exit, as soon as every column is found to be present.
         *
         * @return {Column[]} The present columns.
         */
        getCols: function (rootItemSchema, rootItems, colCount) {
          // Each column is only output once.
          var seenCols = {},
              usedCols = [];

          function recursive(itemSchema, item) {
            var i, L;
            if (Array.isArray(item)) {
              i = -1;
              L = item.length;
              while (++i < L) {
                if (recursive(itemSchema, item[i]) === false) return false;
              }
            } else {
              var props = itemSchema.props, propName, childSchema;
              i = -1;
              L = props.length
              while (++i < L) {
                childSchema = props[i];
                propName = childSchema.name;
                if (propName in item) {
                  if (childSchema.isColumn) {
                    if (!seenCols[childSchema.index]) {
                      seenCols[childSchema.index] = 1;
                      var oldFieldColArray = $.grep(tabularViewService.fieldCols, function(e){ return e.nameKey == childSchema.nameKey; });
                      if(oldFieldColArray.length === 1) {
                        if(oldFieldColArray[0].display !== undefined) {
                          childSchema.display = oldFieldColArray[0].display;
                        }
                      }
                      usedCols.push(childSchema);

                      // No use in traversing all rows if all possible columns
                      // have already been output.
                      if (usedCols.length >= colCount) return false;
                    }
                  } else {
                    if (recursive(childSchema, item[propName]) === false) return false;
                  }
                }
              }
            }
          }

          if (rootItems && colCount) recursive(rootItemSchema, rootItems);

          // Make sure columns are in the original order.
          return usedCols.sort(function (a, b) {
            return Pentaho.utilities.compare(a.index, b.index);
          });
        },
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
         * @param {ItemSchema} rootItemSchema The item schema containing columns, indexed under their property paths.
         * @param {Object[]} rootItems The items.
         * @return {Row[]} The rows.
         */
        getRows: function (rootItemSchema, rootItems) {

          function flatten(itemSchema, item, rows) {
            if (Array.isArray(item)) {
              var L = item.length;
              if (L === 1) {
                rows = flatten(itemSchema, item[0], rows);
              } else if (L > 1) {
                var newRows = [],
                    copyRows = function () {
                      return rows.map(function (row) {
                        return angular.copy(row);
                      });
                    };

                for (var i = 0; i < L - 1; i++) Pentaho.utilities.append(newRows, flatten(itemSchema, item[i], copyRows()));

                rows = Pentaho.utilities.append(newRows, flatten(itemSchema, item[L - 1], rows));
              }
            } else if (item) {
              if (itemSchema.isColumn) {
                var colStrPath = itemSchema.stringifiedPath;
                rows.forEach(function (row) {
                  row[colStrPath] = item;
                });
              } else {
                itemSchema.props.forEach(function (childItemSchema) {
                  var propName = childItemSchema.name;
                  if (propName in item)
                    rows = flatten(childItemSchema, item[propName], rows);
                });
              }
            }

            return rows;
          }

          return flatten(rootItemSchema, rootItems, [
            {}
          ]);
        },
        /**
         * Called to change the column by which field rows are sorted.
         *
         * This method is published in the scope object and can thus be called by the view.
         *
         * @param {Column} col The column by which to sort rows.
         */
        onOrderByCol: function (col) {
          // Cycles through: Ascending -> Descending

          if (tabularViewService.orderByCol === col.stringifiedPath) {
            tabularViewService.isOrderReversed = !tabularViewService.isOrderReversed;
          } else {
            tabularViewService.orderByCol = col.stringifiedPath;
            tabularViewService.isOrderReversed = false;
          }
        },
        /**
         * Called to show or hide the column display.
         *
         * This method is published in the scope object and can thus be called by the view.
         *
         * @param {Column} col The column by which to sort rows.
         */
        toggleColDisplay: function (col) {
          col.display = !col.display;
        },
        /**
         * Gets the order by value of a row.
         *
         * This method is published in the scope object and can thus be called by the view.
         *
         * @param {Row} row The row.
         * @return {Object} The order by value.
         */
        orderByKey: function (row) {
          return row[tabularViewService.orderByCol];
        }
      };
      var tabularViewService = new TabularViewService();
      return tabularViewService;
    }])
});