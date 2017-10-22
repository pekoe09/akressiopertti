$(document).ready(function() {
   
    // fetches brewerys and sets up brewery typeahead
    var breweries = [];
    $.ajax({
       url: "/oluet/panimot",
       dataType: "json",
       contentType: "application/json; charset=utf-8",
       type: "get",
       async: false,
       success: function(result) {
           breweries = result;
       }
    });
    
    var breweryMatcher = function(breweries) {
        return function findMatches(q, cb) {
            var matches, substringRegex;
            matches = [];
            substringRegex = new RegExp(q, 'i');
            $.each(breweries, function(i, brewery) {
               if(substringRegex.test(brewery.name)) {
                   matches.push(brewery);
               }
            });
            cb(matches);
        };
    };
    
    var getBreweryName = function(brewery) {
        return brewery.name;
    }
    
    $('#brewery').typeahead({
       minLength: 3,
       highlight: true
    },
    {
        name: 'breweries',
        source: breweryMatcher(breweries),
        display: getBreweryName
    });
      
});
