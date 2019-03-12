$(document).ready(function () {
    
    // sets up listener for adding grapes
    document.querySelector('#addGrapeButton').addEventListener('click', addGrapeToList, false);
    
    // remove handler for pre-existing items on grape contents list
    $('#grapeContents .btn-danger').click(function(){
        $(this).parents('.grapeitem').remove();
    });
    
    // fetches geographies and sets up country and area typeaheads
    var geographies = [];
    var geographyIds = [];
    $.ajax({
        url: "/alueet/lista",
        dataType: "json",
        contentType: "applicaton/json; charset=utf-8",
        type: "get",
        async: false,
        success: function(result) {
            geographies = result;
            $.each(result, function(index, geography){
                geographyIds[geography.name] = geography.id;
            });
        }
    });
    
    var geographyMatcher = function(geographies) {
        return function findMatches(q, cb){
            var matches, substringRegex;
            matches = [];
            substringRegex = new RegExp(q, 'i');
            $.each(geographies, function(i, geography) {
                if(substringRegex.test(geography.name)) {
                    matches.push(geography);
                }
            });
            cb(matches);
        };
    };
    
    var getGeographyName = function(geography) {
        return geography.name;
    };
    
    $('#country').typeahead({
        minLength: 1,
        highlight: true
    },
    {
        name: 'geographies',
        source: geographyMatcher(geographies),
        display: getGeographyName
    });
    
    $('#country').on('blur', function(evt){
        var name = $('#country').val();
        if(name.length !== 0){
            $('#countryId').val(geographyIds[name]);
        }
    });
    
    $('#region').typeahead({
        minLength: 1,
        highlight: true
    },
    {
        name: 'geographies',
        source: geographyMatcher(geographies),
        display: getGeographyName
    });
    
    $('#region').on('blur', function(evt){
        var name = $('#region').val();
        if(name.length !== 0){
            $('#regionId').val(geographyIds[name]);
        }
    });
    
    // fetches grapes and sets up grape typeahead
    var grapes = [];
    var grapeIds = [];
    $.ajax({
        url: "/rypaleet/lista",
        dataType: "json",
        contentType: "application/json; charset=utf-8",
        type: "get",
        async: false,
        success: function(result) {
            grapes = result;
            $.each(result, function(index, grape){
                grapeIds[grape.name] = grape.id;
            });
        }
    });
    
    var grapeMatcher = function(grapes) {
        return function findMatches(q, cb){
            var matches, substringRegex;
            matches = [];
            substringRegex = new RegExp(q, 'i');
            $.each(grapes, function(i, grape) {
                if(substringRegex.test(grape.name)) {
                    matches.push(grape);
                }
            });
            cb(matches);
        };
    };
    
    var getGrapeName = function(grape) {
        return grape.name;
    };
    
    $('#grape').typeahead({
        minLength: 3,
        highlight: true
    },
    {
        name: 'grapes',
        source: grapeMatcher(grapes),
        display: getGrapeName
    });
    
    $('#grape').on('blur', function(evt){
        var name = $('#grape').val();
        if(name.length !== 0){
            $('#grapeId').val(grapeIds[name]);
        }
    });
    
    // adds listener to package grape list neatly on form submit
    $('#wineForm').submit(function(event){
        packageGrapes();
    });    
});

function addGrapeToList() {
    var grapeName = $('#grape').val();
    var grapeId = $('#grapeId').val();
    var contentPc = $('#contentPc').val();
    var newListItem = 
            $("<div class='grapeitem row' data-grapeid='" + grapeId + "' data-contentpc='" + contentPc + "'></div>");
    var nameElement = $("<span class=col-md-8>" + grapeName + " " + contentPc + "%</span>");
    var removeBtn = $("<button class='btn btn-danger btn-sm' type='button' data-grapeid='" + grapeId + "'>Poista</button>");
    removeBtn.click(function(){
         $(this).parents(".grapeitem").remove();
    });
    newListItem.append(nameElement, removeBtn);    
    $('#grapeContents').append(newListItem);
    
    $('#grape').val('');
    $('#grapeId').val('');
    $('#contentPc').val(0);
}

function packageGrapes() {
    var grapeData = [];
    $('.grapeitem').each(function(){
        var grapeDatum ={
            grapeId: this.dataset.grapeid,
            contentPc: this.dataset.contentpc
        };
        grapeData.push(grapeDatum);
    });
    var grapeJSON = JSON.stringify(grapeData);
    $("<input type='hidden' name='grapeData' />").val(grapeJSON).appendTo('#wineForm');
}