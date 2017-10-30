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
     
     // sets up listeners for adding new ingredients and for linking recipes
     document.querySelector('#addIngredientButton').addEventListener('click', addIngredientToList, false);
     document.querySelector('#linkRecipeButton').addEventListener('click', addRecipeToLinkedList, false);
     document.querySelector('#openLinkRecipeModal').addEventListener('click', openLinkRecipeModal, false);
     document.querySelector('#closeLinkRecipeModal').addEventListener('click', closeLinkRecipeModal, false);
     
     // fetches ingredients and sets up ingredient typeahead
     var ingredients = [];     
     var ingredientIds = [];
     $.ajax({
        url: "/ainekset/lista",
        dataType: "json",
        contentType: "application/json; charset=utf-8",
        type: "get",
        async: false,
        success: function(result){
            ingredients = result;
            $.each(result, function(index, ingredient){
               ingredientIds[ingredient.partitive] = ingredient.id;
            }); 
        }
    });     
     
     var ingredientMatcher = function(ingredients){
         return function findMatches(q, cb){
             var matches, substringRegex;
             matches = [];
             substringRegex = new RegExp(q, 'i');
             $.each(ingredients, function(i, ingredient) {
                 if(substringRegex.test(ingredient.partitive)) {
                     matches.push(ingredient);
                 }
             });
             cb(matches);
         };
     };
     
     var getIngredientName = function(ingredient){
         return ingredient.partitive;
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
     
     $('#ingredientName').on('blur', function(evt){
        var name = $('#ingredientName').val();
        if(name.length !== 0){
            $('#ingredientId').val(ingredientIds[name]);
        }
     });
     
     // fetched recipes and sets up recipe typeahead
     var recipes = [];
     var recipeIds = [];
     $.ajax({
         url: "/reseptit/lista",
         dataType: "json",
         contentType: "application/json; charset=utf-8",
         type: "get",
         async: false,
         success: function(result){
             recipes = result;
             $.each(result, function(index, recipe){
                recipeIds[recipe.title] = recipe.id; 
             });
         }
     });
     
     var recipeMatcher = function(recipes){
         return function findMatches(q, cb){
             var matches, substringRegex;
             matches = [];
             substringRegex = new RegExp(q, 'i');
             $.each(recipes, function(i, recipe) {
                 if(substringRegex.test(recipe.title)){
                     matches.push(recipe);
                 }
             });
             cb(matches);
         };
     };
     
     var getRecipeTitle = function(recipe) {
         return recipe.title;
     };
     
     $('#linkRecipe').typeahead({
         minLength: 3,
         highlight: true
     },
     {
         name: 'recipes',
         source: recipeMatcher(recipes),
         display: getRecipeTitle
     });
     
     $('#linkRecipe').on('blur', function(evt){
         var title = $('#linkRecipe').val();
         if(title.length !== 0) {
             $('#linkRecipeId').val(recipeIds[title]);
         }
     });

     // adds listener to package ingredient list and related recipe list neatly on form submit
     $('#recipeForm').submit(function(event){
        packageIngredients(); 
        packageLinkedRecipes();
     });
 });
 
 function addIngredientToList(){
     var ingredientName = $("#ingredientName").val().toLowerCase();
     var ingredientId = $("#ingredientId").val();
     var measureName = $("#ingredientMeasure option:selected").text().toLowerCase();
     var measureId = $("#ingredientMeasure").val();
     var measureAmt = $("#ingredientAmount").val();     
     
     var amountInput = $("<input type='text' class='form-control' />").val(measureAmt);
     var amountDiv = $("<div class='col-md-1'></div>").append(amountInput);
     var measureNameField = $("<span></span>").text(measureName + " ");
     var ingredientNameField = $("<span></span>").text(ingredientName);
     var measureIdField = $("<input type='hidden' class='measureid'/>").val(measureId);
     var ingredientIdField = $("<input type='hidden' class='ingredientid' />").val(ingredientId);
     var removeButton = $("<button class='btn btn-danger btn-sm' type='button' data-ingredientid="
             + ingredientId
             + ">Poista</button>");   
     removeButton.click(function(){
         $(this).parents(".ingredient-row").remove();
     });
     var textContainer = $("<div class='col-md-3 form-control-static'></div>").append(
            measureNameField, 
            ingredientNameField,
            measureIdField,
            ingredientIdField);
     var newIngredient = $("<div class='row ingredient-row'></div>").append(
            amountDiv, 
            textContainer,
            removeButton);
     var newFormGroup = $(
             "<div class='form-group' data-ingredientid='" 
             + ingredientId
             + "'></div>").append(newIngredient);     
     $('#ingredientList').append(newFormGroup);
     
     $("#ingredientName").val('');
     $("#ingredientId").val('');
     $("#ingredientMeasure").prop('selected', 0);
     $("#ingredientMeasure").val('');
     $("#ingredientAmount").val(''); 
 }
 
 function addRecipeToLinkedList() {
     var recipeName = $("#linkRecipe").val();
     var recipeId = $("#linkRecipeId").val();
     var recipeNameField = $("<span></span>").text(recipeName);
     var recipeIdField = $("<input type='hidden' class='linked-recipe-id' />").val(recipeId);
     var removeButton = $("<button class='btn btn-danger btn-sm' type='button' data-recipeid="
             + recipeId
             + ">Poista</button>");   
     removeButton.click(function(){
         $(this).parents(".linked-recipe-row").remove();
     });
     var newRecipe = $("<div class='row linked-recipe-row'></div>").append(
             recipeNameField,
             recipeIdField,
             removeButton);
     var newFormGroup = $(
             "<div class='form-group'></div>").append(newRecipe);
     $('#relatedrecipelist').append(newFormGroup);
     
     $("#linkRecipe").val('');
     $("#linkRecipeId").val('');
 }
 
 function packageIngredients(){
     var ingredientData = [];
     $('.ingredient-row').each(function(){
         var recipeIngredient = $(this).find('.recipeingredientid');
         var recipeIngredientId;
         if(recipeIngredient){
             recipeIngredientId = recipeIngredient.val();
         }
         var ingredientId = $(this).find('.ingredientid').val();
         var measureId = $(this).find('.measureid').val();
         var amount = $(this).find('input[type=text]').val();
         var ingredientDatum = {
             recipeIngredientId: recipeIngredientId,
             ingredientId: ingredientId,
             measureId: measureId,
             amount: amount             
         };
         ingredientData.push(ingredientDatum);
     });
     
     var ingredientJSON = JSON.stringify(ingredientData);
     $("<input type='hidden' name='ingredientSet'  />").val(ingredientJSON).appendTo("#recipeForm");
 }
 
 function packageLinkedRecipes() {
     var linkedRecipeIds = [];
     $('.linked-recipe-row').each(function() {
         linkedRecipeIds.push({ recipeId: $(this).find('.linked-recipe-id').val() });
     });
     var linkedRecipeJSON = JSON.stringify(linkedRecipeIds);
     $("<input type='hidden' name='linkedRecipeIds' />").val(linkedRecipeJSON).appendTo("#recipeForm");
 }
 
 function openLinkRecipeModal() {
     $("#linkRecipeModal").style.display = "block";
 }
 
 function closeLinkRecipeModal() {
     $("#linkRecipeModal").style.display = "none";
 }