$(document).ready(function(){
    
    $('#ingredientList .btn-danger').click(function(){
        $(this).parents('.ingredient-row').remove();
    });
    
});
