$(document).ready(function () {
    
    // sets up listener for adding grapes
    document.querySelector('#addGrapeButton').addEventListener('click', addGrapeToList, false);
    
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
            substringRegex = new ReqExp(q, 'i');
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
    
    $('grape').on('blur', function(evt){
        var name = $('grape').val();
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
    var newListItem = $("<li class='grapeitem' data-grapeid='"
            + grapeId
            + " data-contentpc='"
            + contentPc
            + "'>"
            + grapeName + " " + contentPc + "%"
            + "</li>");
    $('#grapeContents').append(newListItem);
    
    $('#grape').val('');
    $('#grapeId').val('');
    $('#contentPc').val(0);
}

function packageGrapes() {
    var grapeData = [];
    $('.grapeitem').each(function(){
        var grapeDatum ={
            grapeId: $(this).dataset.grapeid,
            contentPc: $(this).dataset.contentPc
        };
        grapeData.push(grapeDatum);
    });
    var grapeJSON = JSON.stringify(grapeData);
    $("<input type='hidden' name='grapeData' />").val(grapeJSON).appendTo('#wineForm');
}