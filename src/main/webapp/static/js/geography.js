$(document).ready(function () {
    
    document.querySelector('#addParentButton').addEventListener('click', addParentToList, false);
    
    $('#parentList .btn-danger').click(function(){
        $(this).parents('.parentitem').remove();
    });
    
    $('#geographyForm').submit(function(event){
        packageParents();
    });    
});

function addParentToList() {
    var parentSelect = document.getElementById("parent");
    var parentName = parentSelect.options[parentSelect.selectedIndex].text;
    var parentId = parentSelect.options[parentSelect.selectedIndex].value;
    var newListItem = $("<div class='parentitem row' data-parentid='" + parentId + "'></div>");
    var nameElement = $("<span class='col-md-8'>" + parentName + "</span>");
    var removeBtn = $("<button class='btn btn-danger btn-sm' type='button' data-parentid='" + parentId + "'>Poista</button>");
    removeBtn.click(function(){
         $(this).parents(".parentitem").remove();
    });
    newListItem.append(nameElement, removeBtn);    
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