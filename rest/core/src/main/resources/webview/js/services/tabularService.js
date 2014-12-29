define(['./services'], function (appServices) {
  appServices.factory('TabularService', [
    function () {
      function TabularService() {
        this.fieldCols;
        this.fieldRows;
        this.orderByCol;
        this.isOrderReversed;
      }

      TabularService.prototype = {
        constructor: TabularService,
        init: function (col, order) {
          tabularService.orderByCol = col;
          tabularService.isOrderReversed = order;
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
              col.index = index;
              col.stringifiedPath = JSON.stringify(propPath);

              // Index the col by its colPropPath steps.
              tabularService.buildItemSchemaRecursive(valuesItemSchema, propPath, col);
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

          if (tabularService.orderByCol === col.stringifiedPath) {
            tabularService.isOrderReversed = !tabularService.isOrderReversed;
          } else {
            tabularService.orderByCol = col.stringifiedPath;
            tabularService.isOrderReversed = false;
          }
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
          return row[tabularService.orderByCol];
        }
      };
      var tabularService = new TabularService();
      return tabularService;
    }])
});