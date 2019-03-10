$(document).ready(function () {
    
    document.querySelector('#addParentButton').addEventListener('click', addParentToList, false);
    
    $('#geographyForm').submit(function(event){
        packageParents();
    });    
});

function addParentToList() {
    var parentSelect = document.getElementById("parent");
    var parentName = parentSelect.options[parentSelect.selectedIndex].text;
    var parentId = parentSelect.options[parentSelect.selectedIndex].value;
    var newListItem = $("<li class='parentitem' data-parentid='"
            + parentId
            + "'>"
            + parentName + " "
            + "</li>");
    $('#parentList').append(newListItem);
}

function packageParents() {
    var parentData = [];
    $('.parentitem').each(function(){
        var parentDatum = {
            parentId: this.dataset.parentid
        };
        parentData.push(parentDatum);
    });
    var parentJSON = JSON.stringify(parentData);
    $("<input type='hidden' name='parentData' />").val(parentJSON).appendTo('#geographyForm');
}