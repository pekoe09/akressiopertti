 $(document).ready(function () {
     
     // sets up preparation time inputs as spinners
     $('#preparationMinutes').spinner({
         spin: function (event, ui) {
             if (ui.value >= 60) {
                 $(this).spinner('value', ui.value - 60);
                 $('#preparationHours').spinner('stepUp');
                 return false;
             } else if (ui.value < 0) {
                 $(this).spinner('value', ui.value + 60);
                 $('#preparationHours').spinner('stepDown');                 
                 return false;
             }            
         },
         min: 0
     });
     $('#preparationHours').spinner({
         min: 0
     });
     
     // sets up listener for adding new ingredients
     document.querySelector('#addIngredientButton').addEventListener('click', addIngredientToList, false);
     
     // fetches ingredients and sets up ingredient typeahead
     var ingredients = [];     
     $.ajax({
        url: "/ainekset/lista",
        dataType: "json",
        contentType: "application/json; charset=utf-8",
        type: "get",
        async: false,
        success: function(result){
            ingredients = result;
        }
    });     
     
     var ingredientMatcher = function(ingredients){
         return function findMatches(q, cb){
             var matches, substringRegex;
             matches = [];
             substringRegex = new RegExp(q, 'i');
             $.each(ingredients, function(i, ingredient) {
                 if(substringRegex.test(ingredient.name)) {
                     matches.push(ingredient);
                 }
             });
             cb(matches);
         };
     };
     
     var getIngredientName = function(ingredient){
         return ingredient.name;
     };
     
     $('#ingredientName').typeahead({
         minLength: 3,
         highlight: true
     },
     {
         name: 'ingredients',
         source: ingredientMatcher(ingredients),
         display: getIngredientName
     });

 });
 
 function addIngredientToList(){
     
     $('#ingredientList').append();
 }